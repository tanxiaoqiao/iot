package com.honeywell.fireiot.interceptor;

import com.honeywell.fireiot.annotation.EnableTraceLog;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.entity.TraceLog;
import com.honeywell.fireiot.repository.TraceLogRepository;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * 操作日志记录
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/10/22 3:11 PM
 */
public class TraceLogInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(TraceLogInterceptor.class);

    @Autowired
    TraceLogRepository logRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }

        HandlerMethod method = (HandlerMethod) handler;
        EnableTraceLog annotation = method.getMethodAnnotation(EnableTraceLog.class);
        if (annotation == null) {
            return;
        }

        TraceLog traceLog = new TraceLog();
        traceLog.setType(annotation.type());
        traceLog.setOperateTime(LocalDateTime.now());
        traceLog.setResource(EnvHolder.getHolder().getResource());

        UserDto currentUser = SessionUtils.getCurrentUser();
        if (currentUser == null) {
            traceLog.setOperator("匿名用户");
        } else {
            traceLog.setOperator(currentUser.getUsername());
        }

        // 解析content
        String content = annotation.content();
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attribute = attributeNames.nextElement();
            if (attribute.startsWith("log_")) {
                try {
                    content = content.replaceAll("\\$\\{" + attribute + "\\}", request.getAttribute(attribute).toString());
                } catch (Exception e) {
                    logger.error("操作日志写入异常，变量 {} 替换失败", attribute);
                    return;
                }
            }
        }
        traceLog.setContent(content);
        traceLog.setResource(EnvHolder.getHolder().getResource());
        logRepository.save(traceLog);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {

    }
}
