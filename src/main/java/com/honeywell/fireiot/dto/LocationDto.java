package com.honeywell.fireiot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName LocationDto
 * @Description TODO
 * @Author monica Z
 * @Date 12/4/2018 3:02 PM
 **/
@Data
@ApiModel(value = "location请求对象", description = "location 请求对象描述")
public class LocationDto {
    @ApiModelProperty(value = "id", name = "id")
    private long id;
    @ApiModelProperty(value = "父id", name = "parentId")
    private long parentId;
    @ApiModelProperty(value = "层次", name = "level")
    private Integer level;
    @ApiModelProperty(value = "名称", name = "name")
    private String name;
    @ApiModelProperty(value = "全称", name = "fullName",hidden = true)
    private String fullName;
    @ApiModelProperty(value = "类型", name = "type")
    private Integer type;
    @ApiModelProperty(value = "纬度", name = "lat")
    private Float lat;
    @ApiModelProperty(value = "经度", name = "lng")
    private Float lng;
    @ApiModelProperty(value = "紧急联系电话", name = "safetyContact")
    private String safetyContact;
    @ApiModelProperty(value = "紧急联系人", name = "safetyOfficer")
    private String safetyOfficer;
    @ApiModelProperty(value = "位置地址", name = "address")
    private String address;
    @ApiModelProperty(value = "预留", name = "polyline")
    private byte[] polyline;

}
