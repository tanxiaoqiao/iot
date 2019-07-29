package com.honeywell.fireiot.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: Kris
 * @date: 4/18/2019
 * @time: 12:13 PM
 **/
@Data
public class SpotVo implements Serializable {
    private Long spotId;
    private List<Long> taskId;
}
