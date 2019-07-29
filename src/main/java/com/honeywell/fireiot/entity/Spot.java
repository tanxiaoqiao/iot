package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "wo_spot")
@Entity
@Data
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Spot implements Serializable {

    /**
     * id 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * 点位名称
     */
    @Column
    private String name;
    /**
     * 安装位置全程
     */
    @Column
    private String fullName;
    /**
     * 二维码
     */
    @Column
    private String qrCode;

    @Column
    private String qrImage;
    /**
     * nfc标签
     */
    @Column
    private String nfcTag;
    /**
     * 类型
     */
    @Column
    private Integer type;
    /**
     * 状态
     */
    @Column
    private Integer status;
    /**
     * 安装位置id
     */
    @Column
    private long locationId;
    /**
     * 创建时间
     */
    @Column
    private Date createTime;
    /**
     * 更新时间
     */
    @Column
    private Date updateTime;


}
