package com.honeywell.fireiot.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class BaseMaterialDto implements Serializable {

    private Long id;
    private String name;
    private String code;
    private String unit;
    private String brand;
    private String model;
    private float approvedPrice;
    private ArrayList<String> pictureList;
    private ArrayList<String> fileList;
    private String description;
    private String qrCode;
    private String qrImage;

}
