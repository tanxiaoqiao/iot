package com.honeywell.fireiot.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.honeywell.fireiot.dto.FormElementDto;
import lombok.Data;

import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppGeneralTaskVO
 * @author: dexter
 * @create: 2019-04-26 15:48
 * @description:
 **/
@Data
public class AppGeneralTaskVO {

    @JsonProperty("formElementList")
    List<FormElementDto> formElements;
}
