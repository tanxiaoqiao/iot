package com.honeywell.fireiot.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日志追踪类
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/3/14 9:34 AM
 */
@Data
public class TraceLogDto {
  public Long id;
  public String type;                   // 操作类型
  public String content;                // 操作内容
  public String operator;               // 操作人
  public LocalDateTime operateTime;     // 操作时间

}
