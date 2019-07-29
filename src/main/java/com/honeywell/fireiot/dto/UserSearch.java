package com.honeywell.fireiot.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/7/2018 9:17 AM
 */
@Data
public class UserSearch {


    @Range(min = 1)
    Integer pi = 1;
    @Range(min = 1, max = 200)
    Integer ps = 10;
    String username;
    String name;
}
