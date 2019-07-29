package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.AppInitInfoVersion;
import com.honeywell.fireiot.vo.AppInitInfoVersionVO;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppInitInfoVersionService
 * @author: dexter
 * @create: 2019-04-01 17:04
 * @description:
 **/
public interface AppInitInfoVersionService {

    /**
     * 保存
     *
     * @param appInitInfoVersion
     * @return
     */
    AppInitInfoVersion save(AppInitInfoVersion appInitInfoVersion);

    /**
     * 查询最新版本初始化数据
     *
     * @return
     */
    AppInitInfoVersionVO check();
}
