package com.honeywell.fireiot.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "wo_task")
@Entity
@Data
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Task  implements Serializable {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * 工作任务名称
     */
    @Column
    private String name;
    /**
     * 任务内容
     */
    @Column
    private String content;
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
