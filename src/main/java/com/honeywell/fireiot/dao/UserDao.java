package com.honeywell.fireiot.dao;


import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.dto.UserSearch;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.utils.Pagination;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/13/2018 3:26 PM
 */
public interface UserDao {

    Pagination<User> findUser(UserSearch userSearch);

    void update(UserDto userDto);

}
