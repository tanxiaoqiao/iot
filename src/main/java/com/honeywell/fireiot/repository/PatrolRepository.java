package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.Patrol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PatrolRepository extends JpaRepository<Patrol, Long>, JpaSpecificationExecutor<PatrolRepository> {


    @Query("update Patrol pl set pl.status =:status where pl.id=:id")
    @Transactional
    @Modifying
    int updateStatus(@Param("status") Integer status, @Param("id") Long id);


    @Query("update Patrol pl set pl.pollingId =:newId where pl.pollingId=:origin")
    @Transactional
    @Modifying
    int updatePollingId(@Param("origin") Long origin, @Param("newId") Long newId);


    @Query("select to_char(pl.createTime,'YYYY-MM'),count(*) from Patrol pl where to_char(pl.createTime,'YYYY-MM') BETWEEN ?1 and ?2 group by to_char(pl.createTime,'YYYY-MM')")
    List<Object[]> findByMonthBetween(String start, String end);

    @Query("select count(*) from Patrol pl where to_char(pl.createTime,'YYYY-MM')=?1 and pl.status=?2")
    int findByMonthAndStatus(String month, Integer status);

    @Query("select count(*) from Patrol pl where to_char(pl.createTime,'YYYY')=?1 and pl.status=?2")
    int findByYearAndStatus(String month, Integer status);




    @Query("select count(*),SUM(pl.spotCount) ,SUM(pl.normalNums), SUM(pl.repairNums) ,SUM(pl.exceptionNums),  SUM(pl.supplementNums) ,SUM(pl.missNums) from Patrol pl where to_char(pl.createTime,'YYYY')=?1")
    List<Object[]> findByYear(String year);



    @Query("select SUM(pl.spotCount) ,SUM(pl.normalNums), SUM(pl.repairNums) ,SUM(pl.exceptionNums),  SUM(pl.supplementNums),SUM(pl.missNums) from Patrol pl where to_char(pl.createTime,'YYYY-MM')=?1")
    List<Object[]> findByMonth(String month);





}
