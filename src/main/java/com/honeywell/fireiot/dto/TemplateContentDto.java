package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @ClassName TemplateContentDto
 * @Description  巡检模版添加巡检neirong
 * @Author Monica Z
 * @Date 2019-03-20 10:48
 */
@Data
public class TemplateContentDto {
    // 模版id
    private long id;

    // 巡检表单 -表单项目
    private FormElementDto formElementDto;
}
