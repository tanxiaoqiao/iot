package com.honeywell.fireiot.dto.waterdevice;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author: zhenzhong.wang@honeywell.com
 * @date: 2019/7/19 4:46 PM
 */
@Data
public class WaterValueRangeDateSearch {
    @NotEmpty(message = "设备编号不能为空")
    private String deviceNo;
    @NotNull(message = "时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDatetime;
//    @NotNull(message = "结束时间不能为空")
//    private String endDatetime;
}
