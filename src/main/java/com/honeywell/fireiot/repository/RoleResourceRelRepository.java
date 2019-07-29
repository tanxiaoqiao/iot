package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.entity.RoleResourceRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleResourceRelRepository extends JpaRepository<RoleResourceRel, Long> {

    @Query("select entity.resource from RoleResourceRel entity where entity.role.id = ?1")
    List<Resource> findResourcesByRoleId(Long id);

    @Modifying
    @Query("delete from RoleResourceRel entity where entity.role.id = ?1")
    void deleteByRoleId(Long id);

    @Override
    RoleResourceRel save(RoleResourceRel rel);

}
