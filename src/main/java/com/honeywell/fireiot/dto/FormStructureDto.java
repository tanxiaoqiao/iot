package com.honeywell.fireiot.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FormStructureDto implements Serializable {

    private Long id;
    private String uuid;
    private String name;
    private String description;
    private boolean root;
    private int layoutMode;
    private List<FormStructureDto> subForms;
    private List<FormElementDto> elements;
}
