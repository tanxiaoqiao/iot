package com.honeywell.fireiot.utils;

import com.honeywell.fireiot.entity.Env;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/4/11 12:05 PM
 */
public class EnvHolder {
    private static final ThreadLocal<Env> HOLDER = new ThreadLocal<>();

    public static Env getHolder() {
        return HOLDER.get() == null ? new Env() : HOLDER.get();
    }

    public static void setHolder(Env env) {
        HOLDER.set(env);
    }

    public static void remove() {
        HOLDER.remove();
    }
}
