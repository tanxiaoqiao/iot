package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.entity.AppInitInfoVersion;
import com.honeywell.fireiot.repository.AppInitInfoVersionRepository;
import com.honeywell.fireiot.service.AppInitInfoVersionService;
import com.honeywell.fireiot.vo.AppInitInfoVersionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppInitInfoVersionServiceImpl
 * @author: dexter
 * @create: 2019-04-01 17:05
 * @description:
 **/
@Service
public class AppInitInfoVersionServiceImpl implements AppInitInfoVersionService {

    @Autowired
    private AppInitInfoVersionRepository appInitInfoVersionRepository;

    @Override
    public AppInitInfoVersion save(AppInitInfoVersion appInitInfoVersion) {
        return appInitInfoVersionRepository.save(appInitInfoVersion);
    }

    @Override
    public AppInitInfoVersionVO check() {
        List<AppInitInfoVersion> appInitInfoVersionList =
                appInitInfoVersionRepository
                        .findAll(new Sort(Sort.Direction.DESC, "createDate"));
        if (!CollectionUtils.isEmpty(appInitInfoVersionList)) {
            AppInitInfoVersionVO appInitInfoVersionVO = new AppInitInfoVersionVO();
            BeanUtils.copyProperties(appInitInfoVersionList.get(0), appInitInfoVersionVO);
            return appInitInfoVersionVO;
        }
        return null;
    }
}
