package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Workorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Repository
public interface WorkorderRepository extends JpaRepository<Workorder, Long>, JpaSpecificationExecutor<Workorder> {


    @Query("update Workorder w set w.workTeamId=?1 where w.id in ?2")
    @Transactional
    @Modifying
    int updateWorkorder(String workTeamId, List<Long> ids);

    @Query("from Workorder w where w.processId =:processId ")
    Workorder findByProcessId(@Param("processId") String processId);

    @Query("SELECT COUNT(DISTINCT wo.id) FROM Workorder wo WHERE to_char(wo.createTime,'YYYY') = ?1 ")
    int findYearWorkorder(String year);

    @Query("SELECT to_char(wo.createTime,'YYYY-MM'), COUNT(DISTINCT wo.id) FROM Workorder wo WHERE to_char(wo.createTime,'YYYY-MM') BETWEEN ?1 and ?2 group by to_char(wo.createTime,'YYYY-MM') order by to_char(wo.createTime,'YYYY-MM')")
    List<Object[]> findMonthWorkorder(String firstTime, String secondTime);

//    @Query(value = "SELECT COUNT(*) FROM Workorder wo WHERE to_char(wo.createTime, 'YYYY-MM-DD')=?1")
//    List<Integer> findDayWorkorder(String day);


    @Query(value = "SELECT COUNT(DISTINCT wo.id) FROM Workorder wo WHERE to_char(wo.createTime, 'YYYY')=?1 and wo.status=?2 ")
    int findYearAndStatusWorkorder(String year, Integer status);


//    @Query(value = "SELECT COUNT(*) FROM Workorder wo WHERE to_char(wo.createTime, 'YYYY')=?1 and wo.status=?2 ")
//    Integer findMonthAndStatusWorkorder(String month, Integer status);

    @Query(value = "SELECT COUNT(DISTINCT wo.id) FROM Workorder wo WHERE to_char(wo.createTime, 'YYYY-MM')=?1 and wo.status=?2 ")
    int findMonthlyAndStatusWorkorder(String month, Integer status);
}
