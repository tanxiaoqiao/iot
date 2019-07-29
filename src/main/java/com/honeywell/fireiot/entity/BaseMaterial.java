package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

/**
 * @Author: Kayla, Ye
 * @Description:物资基本信息
 * @Date:Created in 1:59 PM 3/27/2019
 */
@Data
@Entity
@Table(name = "wms_base_material")
public class BaseMaterial extends BaseEntity<BaseMaterial> {

    @NotBlank(message = "name_not_null")
    private String name; //名字

    @NotBlank(message = "code_not_null")
    @Column(unique = true)
    private String code;//物资编码全局唯一

    @NotBlank(message = "name_not_null")
    private String unit;//单位


    private String brand;//品牌

    private String model;//型号

    @Column(name = "approved_price",scale = 2)
    private float approvedPrice;//核定价格

    private ArrayList<String> pictureList;//图片附件

    private ArrayList<String> fileList;//文件附件

    private String description;//备注

    private String qrCode;//二维码信息：物资名称+物资型号+物资编码

    @Column(columnDefinition = "text")
    private String qrImage;//二维码图片信息




}
