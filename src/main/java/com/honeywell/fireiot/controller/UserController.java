package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.EnableTraceLog;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.TraceLogType;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.dto.UserSearch;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.sso.SSOUser;
import com.honeywell.fireiot.sso.SSORestApi;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import com.honeywell.fireiot.utils.SessionUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/6/2018 4:05 PM
 */
@RestController
@RequestMapping("/api/user")
@Api(tags = {"用户接口"})
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 查询单个用户信息（不包含密码）
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询单个用户信息", httpMethod = "GET")
    public ResponseObject<UserDto> findUserById(@PathVariable("id") Long id) {
        UserDto userDto = userService.findUserDetailById(id);
        if (userDto != null) {
            // 密码置空
            userDto.setPassword(null);
            return ResponseObject.success(userDto);
        } else {
            return ResponseObject.fail(ErrorEnum.ACCOUNT_NOT_EXIST);
        }
    }

    /**
     * 根据EID查询单个用户信息（不包含密码）
     *
     * @param eid
     * @return
     */
    @GetMapping("/eid/{eid}")
    @ApiOperation(value = "查询单个用户信息", httpMethod = "GET")
    public ResponseObject<UserDto> findUserByEid(@PathVariable("eid") String eid) {
        UserDto userDto = userService.findUserDetailByEid(eid);
        if (userDto != null) {
            // 密码置空
            userDto.setPassword(null);
            return ResponseObject.success(userDto);
        } else {
            return ResponseObject.fail(ErrorEnum.ACCOUNT_NOT_EXIST);
        }
    }

    /**
     * 查询单个用户（包含密码）
     *
     * @param key Username，Email查询
     * @return
     */
    @GetMapping("/username")
    @ApiOperation(value = "查询单个用户信息", httpMethod = "GET")
    public ResponseObject findUserByUsernameOrEmail(@RequestParam("key") String key) {
        User user = userService.findUserByUsernameOrEmail(key);
        user.setPassword(null);
        if (user != null) {
            return ResponseObject.success(new UserDto(user));
        } else {
            return ResponseObject.fail(ErrorEnum.ACCOUNT_NOT_EXIST);
        }
    }


    @GetMapping("/users")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "username"),
            @ApiImplicitParam(name = "name", value = "name"),
            @ApiImplicitParam(name = "pi", value = "pi"),
            @ApiImplicitParam(name = "ps", value = "ps")
    })
    @ApiOperation(value = "查找用户", httpMethod = "GET")
    public ResponseObject findAll(@RequestParam(value = "keyword", required = false) String username,
                                  @RequestParam(value = "pi", defaultValue = "1") Integer pi,
                                  @RequestParam(value = "ps", defaultValue = "5") Integer ps) {
        UserSearch userSearch = new UserSearch();
        userSearch.setUsername(username);
        userSearch.setPi(pi);
        userSearch.setPs(ps);
        Pagination<User> page = userService.findUserByCondition(userSearch);
        return ResponseObject.success(page);

    }

    @GetMapping("/list")
    @ApiOperation(value = "查找所有用户", httpMethod = "GET")
    public ResponseObject<List<User>> findAllList() {
        List<User> list = userService.findAllList();
        return ResponseObject.success(list);
    }

    @PostMapping
    @ApiOperation(value = "保存用户", httpMethod = "POST")
    @EnableTraceLog(type = TraceLogType.USER, content = "用户新增：${log_username}")
    public ResponseObject save(@RequestBody @Validated @ApiParam UserDto userDto, HttpServletRequest request) {
//        if (SSORestApi.ssoEnable) {
//            if (userService.checkUserExist(userDto.getUsername())) {
//                throw new BusinessException(ErrorEnum.USERNAME_EXIST);
//            }
//        }
        // 用户名唯一
        User user = userService.findUserByUsernameOrEmail((userDto.getUsername()));
        if (user != null && 2 != user.getStatus()) {
            throw new BusinessException(ErrorEnum.USERNAME_EXIST);
        }
        UserDto dto = userService.findUserByEid(userDto.getEid());
        if (dto != null && 2 != dto.getStatus()) {
            throw new BusinessException(ErrorEnum.EID_EXIST);
        }

        userService.addUser(userDto);

        // 注入log变量
        request.setAttribute("log_username", userDto.getUsername());
        return ResponseObject.success(null);
    }

    @DeleteMapping("/{id}")
    @ApiImplicitParam(name = "userId", value = "userId")
    @ApiOperation(value = "删除用户", httpMethod = "DELETE")
    @EnableTraceLog(type = TraceLogType.USER, content = "用户删除：${log_username}")
    public ResponseObject change(@PathVariable("id") Long id, HttpServletRequest request) {
        User user = userService.findUserById(id);
        request.setAttribute("log_username", user.getUsername());

        boolean b = userService.deleteUserById(id);
        // 注入log变量
        if (b) {
            return ResponseObject.success(null);
        } else {
            return ResponseObject.fail(ErrorEnum.USER_DELETE_FAIL);
        }
    }

    @PutMapping
    @ApiOperation(value = "更新用户", httpMethod = "PUT")
    public ResponseObject update(@RequestBody @Validated @ApiParam UserDto userDto) {
        userService.updateUser(userDto);
        return ResponseObject.success(null);
    }


    @PatchMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId"),
            @ApiImplicitParam(name = "oldPwd", value = "oldPwd"),
            @ApiImplicitParam(name = "newPwd", value = "newPwd")
    })
    @ApiOperation(value = "修改密码", httpMethod = "PATCH")
    public ResponseObject<Boolean> changePassword(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        String oldPwd = (String) data.get("oldPwd");
        String newPwd = (String) data.get("newPwd");
        boolean added = userService.updatePasswordById(oldPwd, newPwd, userId);
        if (added) {
            return ResponseObject.success(added);
        } else {
            return ResponseObject.fail(ErrorEnum.PASSWORD_UPDATE_ERROR);
        }
    }

    @PatchMapping("/status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId"),
            @ApiImplicitParam(name = "status", value = "status")
    })
    @ApiOperation(value = "禁用用户", httpMethod = "PATCH")
    public ResponseObject<Boolean> changeStatus(@RequestBody Map<String, Object> data) {
        Long userId = (Long) data.get("userId");
        Integer status = (Integer) data.get("status");
        boolean added = userService.updateStatus(userId, status);
        return ResponseObject.success(added);
    }

    @GetMapping("/menu")
    @ApiOperation(value = "获取菜单", httpMethod = "GET")
    public ResponseObject<Set> getMenu(HttpServletRequest request) {
        //TODO 获取role属性 多对多
        Set<String> menu = userService.getMenu(SessionUtils.getCurrentUser().getId());
        return ResponseObject.success(menu);
    }

    @PostMapping("/users/syn")
    @ApiOperation(value = "同步本地和SSO的用户信息", httpMethod = "POST")
    public ResponseObject synUserFromSSO(HttpServletRequest request) {
        //TODO 获取role属性 多对多
        List<User> allUser = userService.findAllList();
        allUser.forEach(user -> {
            if (user.getUsername() != "admin") {
                SSOUser ssoUser = SSORestApi.getUserByEid(user.getEid());
                if (ssoUser != null) {
                    List<Integer> apps = ssoUser.getApplications();
                    if (apps.contains(1)) {
                        user.setResource(1);
                    } else if (apps.contains(5)) {
                        user.setResource(5);
                    } else if (apps.contains(8)) {
                        user.setResource(8);
                    } else if (apps.contains(9)) {
                        user.setResource(9);
                    }
                    userService.save(user);
                }

            }
        });
        return ResponseObject.success("OK");
    }

    /**
     * 获取未绑定员工的账户信息
     * @return
     */
    @GetMapping("/unbind")
    @ApiOperation(value = "查找所有用户", httpMethod = "GET")
    public ResponseObject getUnbindUser() {
       List<User> unbind = userService.queryUnbindUser();
        return ResponseObject.success(unbind);
    }

}

