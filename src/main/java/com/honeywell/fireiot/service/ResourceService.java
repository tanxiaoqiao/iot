package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.ResourceDto;
import com.honeywell.fireiot.entity.Resource;

import java.util.List;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/14/2018 9:48 AM
 */
public interface ResourceService {


    List<ResourceDto> getAll();

    Resource findOne(Long id);
}
