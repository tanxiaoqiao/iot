package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.entity.RoleResourceRel;
import com.honeywell.fireiot.repository.RoleResourceRelRepository;
import com.honeywell.fireiot.service.RoleResourceRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhenzhong.wang
 * @Date: 2019-03-26
 */
@Service
public class RoleResourceRelServiceImpl implements RoleResourceRelService {

    @Autowired
    RoleResourceRelRepository roleResourceRelRep;

    @Override
    public List<Resource> getResourcesByRole(Long id) {
        List<Resource> resources = roleResourceRelRep.findResourcesByRoleId(id);
        return resources;
    }

    @Override
    public void deleteByRoleId(Long id) {
        roleResourceRelRep.deleteByRoleId(id);
    }

    @Override
    public void save(List<RoleResourceRel> rels) {
        roleResourceRelRep.saveAll(rels);
    }
}
