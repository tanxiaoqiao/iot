package com.honeywell.fireiot.entity;


import lombok.Data;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 设备台账
 */
@Data
@Entity
@Table(
    name = "dev_business_device",
    indexes = {
        @Index(name = "BD_INDEX_NO", columnList = "deviceNo"),
        @Index(name = "BD_INDEX_LOCATION_ID", columnList = "locationId"),
    })
public class BusinessDevice extends BaseEntity<BusinessDevice> {

  private String deviceNo;            // 设备编号(UUID)
  private String deviceLabel;         // 设备标签

  private Long locationId;            // 位置ID
  private String locationDetail;      // 详细位置

  @ManyToOne
  private DeviceType deviceType;      // 设备类型
  @ManyToOne
  private SystemType systemType;      // 系统类型

  private String deviceStatus; //设备状态

  private String brandName; //品牌

  private String typeSpecification; //型号

  private Date dateOfPoduction; // 出厂日期

  private Date dateOfInstallation; //安装日期

  private Date dateOfCommissioning; // 启用日期

  private String weight;//重量

  private Integer lifeTime;//设计寿命

  private String importance; //重要性

  private String description;//描述

  private String mapLocation;//地图位置


  // 第二版迁移
  private Long manufacturerId;//生产商Id

  private Long supplierId;//供应商Id

  private Long installerId;//安装单位Id

  private Long gatewayIndex;//网关索引（可空）

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "businessDevice", orphanRemoval = true)
  private List<BusinessDeviceKnowledgeBaseRel> BusinessDeviceKnowledgeBaseRel;//维修标准，维保标准

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "businessDevice", orphanRemoval = true)
  private List<DeviceParameter> deviceParameters;//设备对应的参数

  private String QRCode;//二维码


}
