package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/14/2018 9:52 AM
 */
@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    List<Role> findByName(String name);
    Role findByNameAndSystemType(String name, String systemType);

    @Query(value = "select entity from Role entity where entity.name = 'ADMIN'")
    List<Role> findAdmin();
}
