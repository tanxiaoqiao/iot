package com.honeywell.fireiot.interceptor;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.OrderType;
import com.honeywell.fireiot.constant.PageOperator;
import com.honeywell.fireiot.utils.PageHolder;
import com.honeywell.fireiot.utils.PageSearch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Page信息拦截封装
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/10/22 3:11 PM
 */
public class PageInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截GET请求
        if (!"GET".equals(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;

        if (method.getMethodAnnotation(JpaPage.class) == null) {
            return true;
        }

        // 创建PageSearch对象存储Page信息
        PageSearch ps = new PageSearch();
        ps.setOpsType(request.getParameter("opsType") == null ?
                PageOperator.LIST : PageOperator.valueOf(request.getParameter("opsType")));

        // 查询参数
        HashMap<String, Object> pageParameter = new HashMap<>();
        // 筛选参数
        HashMap<String, Object[]> filterParameter = new HashMap<>();
        // 范围参数
        HashMap<String, Object> rangeMaxParameter = new HashMap<>();
        HashMap<String, Object> rangeMinParameter = new HashMap<>();
        // 导出参数
        HashMap<String, Object> exportParameter = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();

            if ("ps".equals(parameterName)) {
                ps.setPs(Integer.valueOf(request.getParameter(parameterName)));
                continue;
            }
            if ("pi".equals(parameterName)) {
                int pi = Integer.valueOf(request.getParameter(parameterName)) - 1;
                ps.setPi(pi < 0 ? 0 : pi);
                continue;
            }
            // 封装排序字段，排序参数为：key=DESC | key=ASC
            if (StringUtils.isNotEmpty(parameterName) && parameterName.startsWith("sort_")) {
                ps.setOrderType(OrderType.valueOf(request.getParameter(parameterName).toUpperCase()));
                ps.setOrderColumn(parameterName.substring(5));
                continue;
            }

            // 封装查询参数，以page_开头
            if (StringUtils.isNotEmpty(parameterName) && parameterName.startsWith("page_")) {
                if (StringUtils.isNotEmpty(request.getParameter(parameterName))) {
                    pageParameter.put(parameterName.substring(5), request.getParameter(parameterName).trim());
                }
            }

            // 封装筛选参数，筛选参数以filter_开头，多条件用“,”隔开
            if (StringUtils.isNotEmpty(parameterName) && parameterName.startsWith("filter_")) {

                filterParameter.put(parameterName.substring(7), request.getParameter(parameterName).split(","));
            }

            // 封装导出参数
            if (StringUtils.isNotEmpty(parameterName) && parameterName.startsWith("expo_")) {
                exportParameter.put(parameterName.substring(5), request.getParameter(parameterName));
            }

            // 封装范围
            if (StringUtils.isNotEmpty(parameterName) && parameterName.startsWith("max_")) {
                rangeMaxParameter.put(parameterName.substring(4), request.getParameter(parameterName));
            }
            if (StringUtils.isNotEmpty(parameterName) && parameterName.startsWith("min_")) {
                rangeMinParameter.put(parameterName.substring(4), request.getParameter(parameterName));
            }

        }
        ps.setPageParameter(pageParameter);
        ps.setExportParameter(exportParameter);
        ps.setFilterParameter(filterParameter);
        ps.setRangeMaxParameter(rangeMaxParameter);
        ps.setRangeMinParameter(rangeMinParameter);
        PageHolder.setHolder(ps);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        PageHolder.remove();
    }
}
