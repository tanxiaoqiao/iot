package com.honeywell.fireiot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Data
@Entity
@Table(name = "wo_history")
@ToString(exclude = "workorder")
@EntityListeners(AuditingEntityListener.class)
public class HistoryOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //操作步骤
    private Integer status;
    //操作人
    private String operator;

    private String info;

    @Column(name = "create_time")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "wo_workorder_history", joinColumns = {
            @JoinColumn(name = "workorder_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "history_id", referencedColumnName = "id")
    })
    @JsonIgnore
    private Workorder workorder;


    public HistoryOperation(Integer status, String operator, Workorder workorder, String info) {
        this.status = status;
        this.operator = operator;
        this.workorder = workorder;
        this.info = info;
    }

    public HistoryOperation() {
    }
}
