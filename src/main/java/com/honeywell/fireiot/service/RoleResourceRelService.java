package com.honeywell.fireiot.service;


import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.entity.RoleResourceRel;

import java.util.List;

public interface RoleResourceRelService {
    List<Resource> getResourcesByRole(Long id);

    void deleteByRoleId(Long id);

    void save(List<RoleResourceRel> rels);
}
