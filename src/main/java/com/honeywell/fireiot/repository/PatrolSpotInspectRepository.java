package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.PatrolSpotInspect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author: create by kris
 * @description:
 * @date:1/24/2019
 */
@Repository
public interface PatrolSpotInspectRepository extends JpaRepository<PatrolSpotInspect, Long> {

    @Query("select count(ps) from PatrolSpotInspect ps where ps.patrolId =:patrolId")
    Integer getCountByPatrolId(@Param("patrolId") long patrolId);

    @Query("select ps from PatrolSpotInspect  ps where ps.patrolId=:patrolId")
    List<PatrolSpotInspect> findByPatrolId(@Param("patrolId") long patrolId);

    /**
     * 根据patrolId和spotId查询关联关系
     *
     * @param patrolId
     * @param spotId
     * @return
     */
    @Query("select ps from PatrolSpotInspect ps where ps.patrolId =:patrolId and ps.spotId =:spotId")
    Optional<PatrolSpotInspect> findByPatrolIdAndAndSpotId(@Param("patrolId") long patrolId,@Param("spotId") long spotId);
}
