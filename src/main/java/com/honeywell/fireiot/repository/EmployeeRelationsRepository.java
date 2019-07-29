package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.EmployeeRelations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @project: fire-user
 * @name: EmployeeRelationsRepository
 * @author: dexter
 * @create: 2018-12-19 09:46
 * @description:
 **/
@Repository
public interface EmployeeRelationsRepository
        extends JpaRepository<EmployeeRelations, String>,
        JpaSpecificationExecutor<EmployeeRelations> {

    /**
     * soft delete by employeeId
     *
     * @param employeeId
     */
    @Modifying
    @Query("update EmployeeRelations er set er.deleted = 1 where er.employeeId = ?1 and er.deleted = 0")
    void softDelete(String employeeId);

    /**
     * query EmployeeRelations by employee id
     *
     * @param employeeId
     * @return
     */
    @Query(value = "from EmployeeRelations er where er.deleted = 0  and er.employeeId = ?1")
    Optional<EmployeeRelations> findByEmployeeId(String employeeId);

    /**
     * query EmployeeRelations by user id
     *
     * @param userId
     * @return
     */
    @Query(value = "from EmployeeRelations er where er.deleted = 0 and er.userId = ?1")
    Optional<EmployeeRelations> findByUserId(String userId);

    /**
     * 查询在职已绑定用户的employee关联信息
     * @return
     */
    @Query(value = "select er from EmployeeRelations  er where er.deleted = 0 and er.userId is not null or er.userId <> ''")
    List<EmployeeRelations> getBindUser();
}
