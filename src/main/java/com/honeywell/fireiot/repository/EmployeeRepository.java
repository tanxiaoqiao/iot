package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @project: fire-user
 * @name: EmployeeRepository
 * @author: dexter
 * @create: 2018-12-18 18:27
 * @description:
 **/
@Repository
public interface EmployeeRepository extends
        JpaRepository<Employee, String>,
        JpaSpecificationExecutor<Employee> {

    /**
     * soft delete
     *
     * @param id
     */
    @Modifying
    @Query("update Employee e set e.deleted = 1 where e.id = ?1")
    void softDelete(String id);

    /**
     * 根据用户名模糊查询
     *
     * @param name
     * @return
     */
    List<Employee> findByNameLike(String name);
}
