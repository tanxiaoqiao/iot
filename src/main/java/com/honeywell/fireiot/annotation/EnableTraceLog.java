package com.honeywell.fireiot.annotation;

import com.honeywell.fireiot.constant.TraceLogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用TraceLog
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/3/14 9:56 AM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableTraceLog {

  /**
   * TraceLog类型
   * @return
   */
  TraceLogType type();

  /**
   * 操作记录内容，支持插入变量，格式为 ${log_变量名称} ，例：content="删除用户${log_userName}"
   *
   * 在controller方法参数中加入：HttpServletRequest request，然后在方法体内执行request.setAttribute()方法设置log变量：request.setAttribute("log_userName", "ADMIN");
   * 最终content内容为：“删除用户ADMIN”
   *
   * @return
   */
  String content();
}
