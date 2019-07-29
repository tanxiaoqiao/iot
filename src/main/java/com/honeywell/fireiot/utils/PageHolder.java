package com.honeywell.fireiot.utils;

/**
 * 构建线程安全类存储Page信息
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/10/22 6:08 PM
 */
public class PageHolder {

    private static final ThreadLocal<PageSearch> HOLDER = new ThreadLocal<>();

    public static PageSearch getHolder() {
        return HOLDER.get();
    }

    public static void setHolder(PageSearch ps) {
        HOLDER.set(ps);
    }

    public static void remove() {
        HOLDER.remove();
    }
}
