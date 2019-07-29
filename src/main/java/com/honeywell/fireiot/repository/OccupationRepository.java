package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @project: fire-user
 * @name: OccupationRepository
 * @author: dexter
 * @create: 2018-12-11 10:50
 * @description:
 **/
@Repository
public interface OccupationRepository extends
        JpaRepository<Occupation, String>,
        JpaSpecificationExecutor<Occupation> {

    /**
     * soft delete
     *
     * @param id
     */
    @Modifying
    @Query("update Occupation o set o.deleted = 1 where o.id = ?1")
    void softDelete(String id);

}
