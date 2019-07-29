package com.honeywell.fireiot.dto;

import com.honeywell.fireiot.entity.MaterialDetail;
import lombok.Data;

import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:MaterialDto = BaseMaterial + stockMaterial
 * @Date:Created in 3:35 PM 3/28/2019
 */
@Data
public class MaterialDto {

    private Long id;
    private Long baseMaterialId;
    private Long warehouseId;
    private String warehouseName;
    private String name;
    private String code;
    private String unit;
    private String brand;
    private String model;
    private float approvedPrice;
    private List pictureList;
    private List fileList;
    private String description;
    private String qrCode;
    private String qrImage;
    private String shelf;
    private float validAmount;
    private float lockAmount;
    private float minAmount;
    private Integer status;
    private List<MaterialDetail> materialDetailList;
}
