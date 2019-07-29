package com.honeywell.fireiot.repository;



import com.honeywell.fireiot.entity.WorkTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface WorkTeamRepository extends JpaRepository<WorkTeam, String> {


    @Modifying
    @Query("update WorkTeam wt set wt.deleted = 1 where wt.id = :id")
    @Transactional
    void softDelete(@Param("id") String id);

}
