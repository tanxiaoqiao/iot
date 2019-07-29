package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.StatusType;
import com.honeywell.fireiot.dao.UserDao;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.dto.UserSearch;
import com.honeywell.fireiot.entity.EmployeeRelations;
import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.EmployeeRelationsRepository;
import com.honeywell.fireiot.repository.RoleRepository;
import com.honeywell.fireiot.repository.UserRepository;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.sso.SSOUser;
import com.honeywell.fireiot.sso.SSORestApi;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.PasswordUtils;
import com.honeywell.fireiot.utils.SessionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private EmployeeRelationsRepository employeeRelationsRepository;

    @Autowired
    UserDao userDao;

    @Value("${sso.enable}")
    private boolean ssoEnable;

    @Override
    public boolean checkPassword(Long id, String rawPassword) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new BusinessException(ErrorEnum.ACCOUNT_NOT_EXIST);
        }
        boolean result = PasswordUtils.sha256PasswordVerify(rawPassword, user.getPassword());
        return result;
    }

    @Override
    public void updateUser(UserDto userDto) {
        if (ssoEnable) {
            SSORestApi.updateUser(userDto);
        }
        userDao.update(userDto);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }


    @Override
    public boolean deleteUserById(Long id) {
        if (ssoEnable) {
            User user = findUserById(id);
            boolean r = SSORestApi.deleteUser(user.getUserId());
            // sso删除成功
            if (r) {
                userRepository.updateStatusById(StatusType.DELETE.getValue(), id);
                return true;
            } else {
                return false;
            }
        } else {
            int i = userRepository.updateStatusById(StatusType.DELETE.getValue(), id);
            return i > 0 ? true : false;
        }
    }

    @Override
    public boolean updatePasswordById(String oldPwd, String newPwd, Long userId) {
        if (userId == null || StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
            throw new BusinessException(ErrorEnum.MISS_REQUEST_PARAMTER);
        }
        if (oldPwd.equals(newPwd)) {
            throw new BusinessException(ErrorEnum.OLDPASSWORDANDNEW_ERROR);
        }
        if (!checkPassword(userId, oldPwd)) {
            throw new BusinessException(ErrorEnum.OLDPASSWORD_ERROR);
        }


        if (ssoEnable) {
            User user = findUserById(userId);
            boolean r = SSORestApi.changePassword(
                    SSORestApi.getSSOTokenFromCookie(SessionUtils.getRequest()),
                    user.getUserId(),
                    oldPwd,
                    newPwd
            );
            // sso更新失败，返回false
            if (!r) {
                return false;
            }
        }

        int i = userRepository.updatePasswordById(PasswordUtils.sha256Encode(newPwd), userId);
        return i > 0 ? true : false;
    }


    @Override
    public void addUser(UserDto userDto) {
        List<Role> roles = new ArrayList<>(8);
        if (userDto.getRoleIds() != null) {

            for (long roleId : userDto.getRoleIds()) {
                Optional<Role> byId = roleRepository.findById(roleId);
                roles.add(byId.get());
            }
        }
        User oldUser = userRepository.findByUsernameOrEmail(userDto.getUsername(), EnvHolder.getHolder().getResource());
        // 逻辑恢复已删除的账户
        if (oldUser != null && oldUser.getStatus() == 2) {
            userDto.setId(oldUser.getId());
            userDto.setUserId(oldUser.getUserId());
        }
        User user = new User(
                userDto.getId(),
                userDto.getUsername(),
                userDto.getEmail(),
                PasswordUtils.sha256Encode(userDto.getPassword()),
                userDto.getStatus(),
                userDto.getName(),
                userDto.getPhone(),
                userDto.getCreator(),
                userDto.getLanguage(),
                roles,
                userDto.getEid(),
                userDto.getUserId(),
                userDto.getDsc()
        );
        user.setCreator(SessionUtils.getCurrentUser().getUsername());
        user.setMobileAccess(userDto.getMobileAccess());
        user.setResource(EnvHolder.getHolder().getResource());

        // SSO创建用户
        if (ssoEnable) {
            // userId不为空，说明sso中已存在该用户，走sso更新操作
            if (userDto.getUserId() != null) {
                SSORestApi.updateUser(new UserDto(user));
            } else {
                String userID = SSORestApi.createUser(user, userDto.getPassword());
                user.setUserId(userID);
            }
        }

        userRepository.save(user);
    }

    @Override
    public User findUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    @Override
    public UserDto findUserDetailById(Long id) {
        User user = findUserById(id);
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto(user);
        if (ssoEnable) {
            SSOUser ssoUser = SSORestApi.getUserByEid(user.getEid());
            if (ssoUser != null) {
                userDto.setMobileAccess(ssoUser.getMobileAccess());
            }
        }
        return userDto;
    }

    @Override
    public UserDto findUserDetailByEid(String eid) {
        User user = userRepository.findByEidAndResource(eid, EnvHolder.getHolder().getResource());
        if (user == null) {
            user = new User();
        }
        if (ssoEnable) {
            SSOUser ssoUser = SSORestApi.getUserByEid(eid);
            UserDto dto = new UserDto();
            if (ssoUser != null) {
                ssoUser.reflectSSOField(user);
                dto = new UserDto(user);
                dto.setMobileAccess(ssoUser.getMobileAccess());
            }
            return dto;
        }
        return new UserDto(user);
    }

    @Override
    public UserDto findUserByEid(String eid) {
        User user = userRepository.findByEidAndResource(eid, EnvHolder.getHolder().getResource());
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto(user);
        return userDto;
    }

    @Override
    public List<User> findUserByEidIgnoreResource(String eid) {
        return userRepository.findByEid(eid);
    }

    @Override
    public Set<String> getMenu(Long id) {
        User user = findUserById(id);
        List<Role> roles = user.getRoles();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        // 整合所有的MenuId
        roles.forEach(r -> {
            if (r.getMenuId() != null) {
                set.addAll(Arrays.asList(r.getMenuId().split(",")));
            }
        });
        return set;
    }


    @Override
    public Pagination<User> findUserByCondition(UserSearch userSearch) {

        // sso开启时，从sso获取用户
        if (ssoEnable) {
            Pagination<SSOUser> ssoUserPagin = SSORestApi.searchPageUser(userSearch.getPs(), userSearch.getPi(), userSearch.getUsername());

            List<SSOUser> ssoUsers = ssoUserPagin.getDataList();

            List<User> userTreeList = new TreeList<>();
            // 遍历ssoUsers，解析成系统支持的User列表
            ssoUsers.forEach(ssoUser -> {
                User user = findUserByUsernameOrEmail(ssoUser.getUserName());
                if (user == null) {
                    // sso中的用户在当前系统中不存在，则新建用户。
                    user = addBusinessUser(ssoUser.getUserName(), null, ssoUser.getId());
                }
                ssoUser.reflectSSOField(user);
                userTreeList.add(user);
            });

            return new Pagination<>(ssoUserPagin.getTotalCount(), excludeAdminRole(userTreeList));
        } else {
            Pagination<User> page = userDao.findUser(userSearch);
            // 过滤ADMIN角色
            page.setDataList(excludeAdminRole(page.getDataList()));
            return page;
        }
    }

    public List<User> excludeAdminRole(List<User> users) {
        TreeList<User> userTreeList = new TreeList<>();
        users.forEach(user -> {
            if (user.getRoles() != null) {
                List<Role> roleList = new ArrayList<>();
                user.getRoles().forEach(role -> {
                    if (!"ADMIN".equals(role.getName())) {
                        roleList.add(role);
                    }
                });
                user.setRoles(roleList);
            }
            userTreeList.add(user);
        });
        return userTreeList;
    }

    @Override
    public List<User> findAllList() {
        //排除已经删除的
        List<User> userList = userRepository.findAllList(StatusType.DELETE.getValue());
        return userList;
    }

    @Override
    public User findUserByUsernameOrEmail(String key) {
        int resource = 1;
        // 由于401过滤器中会调用此方法，因此不能使用EnvHolder获取resource
        if (SessionUtils.getRequest().getHeader("resource") !=null) {
            resource = Integer.parseInt(SessionUtils.getRequest().getHeader("resource"));
        }
        User user = userRepository.findByUsernameOrEmail(key, resource);
        return user;
    }

    @Override
    public Boolean checkUserExist(String username) {
        List<User> user = userRepository.findByUsername(username);
        return CollectionUtils.isEmpty(user) ? false : true;
    }

//    @Override
//    public Pagination<User> findAllPage(Integer pi, Integer ps) {
//        int size = pi <= 0 ? 0 : (pi - 1) * ps;
//        PageRequest pr = PageRequest.of(size, ps, Direction.DESC, "id");
//        Page<User> all = userRepository.findAll(pr);
//        Pagination<User> page = new Pagination<>((int) all.getTotalElements(), all.getContent());
//        return page;
//    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        int i = userRepository.updateStatusById(status, id);
        return i > 0 ? true : false;
    }

    @Override
    public User addBusinessUser(String username, String password, String userId) {

//        List<Role> roles = roleRepository.findByName("BUSINESS");
        // 如果password为空，则设定默认密码
        if (StringUtils.isEmpty(password)) {
            password = "11111111";
        }
        List<Role> roles = new ArrayList<>();
        User user = new User(null,
                username,
                null,
                PasswordUtils.sha256Encode(password),
                1,
                "业务用户",
                null,
                null,
                "zh_CN",
                roles,
                null,
                userId,
                null);
        user.setResource(EnvHolder.getHolder().getResource());
        User entity = userRepository.save(user);

        return entity;
    }

    /**
     * 获取未被绑定员工的账号
     * @return
     */
    @Override
    public List<User> queryUnbindUser() {
       List<EmployeeRelations> bindList = employeeRelationsRepository.getBindUser();
       List<Long> bindUserIds = new ArrayList<>();
       for(int i = 0; i< bindList.size(); i++){
           String userId = bindList.get(i).getUserId();
           if(userId != null &&!userId.equals("")& !userId.equals("null")){
               bindUserIds.add(Long.parseLong(bindList.get(i).getUserId()));
           }
       }
       List<User> unbindUser = userRepository.getUnbindUser(bindUserIds);
       return unbindUser;
    }

}

