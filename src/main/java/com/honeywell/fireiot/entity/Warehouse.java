package com.honeywell.fireiot.entity;

import com.honeywell.fireiot.constant.NotifyWayEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @Author: Kayla, Ye
 * @Description: 仓库管理
 * @Date:Created in 1:57 PM 3/27/2019
 */
@Data
@Entity
@Table(name = "wms_warehouse")
public class Warehouse extends BaseEntity<Warehouse>{
    @NotBlank(message = "name_not_null")
    @Column(unique = true)
    private String name; //仓库名字唯一

    @NotEmpty(message = "admin_id_list_not_null")
    private ArrayList<Long> adminIdList; //管理员id

    private Long  locationId;//空间位置

    private String description;//备注信息

    @Enumerated(EnumType.STRING)
    private NotifyWayEnum notifyWay; //通知类型

    @Column(name = "create_time")
    @CreatedDate
    private Timestamp createTime;

    @Column(name = "update_time")
    @LastModifiedDate
    private Timestamp updateTime;



}
