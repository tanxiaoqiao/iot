package com.honeywell.fireiot.entity;

import com.honeywell.fireiot.constant.TraceLogType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 日志追踪类
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/3/14 9:34 AM
 */
@Data
@Entity
@Table(name = "trace_log")
public class TraceLog extends BaseEntity<TraceLog> {

  @Enumerated(EnumType.STRING)
  private TraceLogType type;      // 操作类型
  private String content;         // 操作内容
  private String operator;        // 操作人
  private LocalDateTime operateTime;     // 操作时间
  private Integer resource;

  public TraceLog(TraceLogType type, String content, String operator, LocalDateTime operateTime) {
    this.type = type;
    this.content = content;
    this.operator = operator;
    this.operateTime = operateTime;
  }

  public TraceLog() {}
}
