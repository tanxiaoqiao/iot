package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.dto.ResourceDto;
import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.repository.ResourceRepository;
import com.honeywell.fireiot.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/14/2018 9:52 AM
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    ResourceRepository resourceRepository;

    @Override
    public List<ResourceDto> getAll() {
        List<Resource> all = resourceRepository.findAll();
        List<ResourceDto> rd = new ArrayList<>();
        all.forEach(a -> {
            ResourceDto r = new ResourceDto();
            r.setDescription(a.getDescription());
            r.setId(a.getId());
            rd.add(r);
        });
        return rd;
    }

    @Override
    public Resource findOne(Long id) {
        return resourceRepository.getOne(id);
    }
}
