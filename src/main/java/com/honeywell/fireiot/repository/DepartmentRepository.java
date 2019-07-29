package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @project: fire-user
 * @name: DepartmentRepository
 * @author: dexter
 * @create: 2018-12-10 19:33
 * @description:
 **/
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {

    /**
     * 根据parentId查找相应Department
     *
     * @param parentId
     * @return
     */
    List<Department> findByParentId(String parentId);

    /**
     * soft delete
     *
     * @param id
     */
    @Modifying
    @Query("update Department d set d.deleted = 1 where d.id = ?1")
    void softDelete(String id);
}
