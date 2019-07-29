package com.honeywell.fireiot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
/**
 * @ClassName InspectionResult
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/21 10:11
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wo_inspect_result")
public class InspectionResult implements Serializable {

    /**
     * unique serial version ID
     */
    private static final long serialVersionUID = -4111111000000000L;

    /**
     * 巡检内容id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * 巡检内容名称
     */
    @Column
    private String name;
    /**
     * 巡检结果
     */
    @Column
    private String value;
    /**
     * 巡检状态
     */
    @Column
    private Integer status;
    /**
     * 异常处理结果
     * 已处理
     * 未处理
     */
    @Column
    private Integer abnormalResult;
    /**
     * 异常处理方式
     * 人工排查 1
     * 故障保修 2
     */
    @Column
    private Integer abnormalMethod;
    /**
     * 故障描述
     */
    @Column
    private String fault;
    /**
     * 操作人员
     */
    @Column
    private String operator;
    /**
     * 相关图片
     */
    @Column
    private ArrayList<String> images;
    /**
     * 创建时间
     */
    @Column
    private Date createTime;
    /**
     * 最后更新时间
     */
    @Column
    private Date updateTime;

    /**
     * 点位id
     */
    private Long spotId;

    /**
     * 巡检计划id
     *
     */
    private Long patrolId;

    /**
     * 0 - 综合类型
     * 1 - 设备类型
     */
    private Integer type;

    /**
     * 设备id
     *
     */
    private Long deviceId;

}
