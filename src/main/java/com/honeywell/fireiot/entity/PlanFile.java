package com.honeywell.fireiot.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: Kayla,Ye
 * @Description: 预案文件
 * @Date:Created in 2:52 PM 7/19/2018
 */
@Data
@Document(collection = "planFile")
public class PlanFile {
    @Id
    private String fileId;

    private String filePath;//文件路径

    private String fileTitle;// 文件标题

    private String fileDescription; //文件描述

    private String picturePath;//图片路径

    private String categoryId; //所属的类别id
}
