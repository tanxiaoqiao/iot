package com.honeywell.fireiot.vo;

import com.honeywell.fireiot.entity.InspectionResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: Kris
 * @date: 4/18/2019
 * @time: 12:13 PM
 **/
@Data
public class SpotDetailVo implements Serializable {

    private String name;
    /**
     * 安装位置全程
     */
    private String fullName;
    /**
     * 二维码
     */
    private List<InspectionResult> inspectionResults;

    /**
     * 状态
     */
    private Integer status;
}
