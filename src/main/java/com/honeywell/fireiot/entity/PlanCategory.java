package com.honeywell.fireiot.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Kayla,Ye
 * @Description: 预案管理类别
 * @Date:Created in 2:52 PM 7/19/2018
 */
@Data
@Document(collection = "planCategory")
public class PlanCategory {
    @Id
    private String categoryId; //类别id

    @NotBlank(message = "category_param_empty")
    private String category; //类别名字

}
