package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.LocationWorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface LocationWorkorderRepository extends JpaRepository<LocationWorkOrder, Long>, JpaSpecificationExecutor<LocationWorkOrder> {

    @Query(value = "select count(distinct lw.id) from LocationWorkOrder lw where lw.status=?1 and lw.deviceId=?2 ")
    Long countFinished(@Param("status") Integer status, @Param("deviceId") Long deviceId);

    @Query(value = "select count(distinct lw.id) from LocationWorkOrder lw where lw.deviceId=?1")
    Long countAll( @Param("deviceId") Long deviceId);


}
