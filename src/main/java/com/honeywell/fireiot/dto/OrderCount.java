package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/13/2018 12:04 PM
 */
@Data
public class OrderCount {

    Integer doneCount;
    Integer undoCount;
    Integer arrangeCount;
}
