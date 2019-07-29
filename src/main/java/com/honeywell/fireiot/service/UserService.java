package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.dto.UserSearch;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.utils.Pagination;

import java.util.List;
import java.util.Set;

public interface UserService {

    boolean checkPassword(Long id, String password);

    void addUser(UserDto userDto);

    void save(User user);

    boolean deleteUserById(Long id);

    void updateUser(UserDto userDto);

    boolean updatePasswordById(String oldPwd, String newPwd, Long userId);

    Boolean checkUserExist(String username);

    boolean updateStatus(Long userId, Integer status);

    User findUserById(Long id);

    UserDto findUserDetailById(Long id);

    UserDto findUserDetailByEid(String eid);

    UserDto findUserByEid(String eid);

    List<User> findUserByEidIgnoreResource(String eid);

    Set<String> getMenu(Long id);

    Pagination<User> findUserByCondition(UserSearch userSearch);

    List<User> findAllList();

    User findUserByUsernameOrEmail(String key);

//    Pagination<User> findAllPage(Integer pi, Integer ps);

    User addBusinessUser(String username, String password, String userId);

    List<User> queryUnbindUser();

}
