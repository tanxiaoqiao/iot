package com.honeywell.fireiot.fire.dto;

import com.honeywell.fireiot.dto.BaseSearch;
import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:08 PM 12/29/2018
 */
@Data
public class FireEventSearch extends BaseSearch {

    private String deviceId;

    private Long startTime;

    private Long endTime;

}
