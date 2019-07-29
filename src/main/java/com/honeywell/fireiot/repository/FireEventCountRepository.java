package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.fire.entity.FireEventCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FireEventCountRepository  extends JpaRepository<FireEventCount, Long>, JpaSpecificationExecutor<FireEventCount> {
    List<FireEventCount> findByDeviceNoAndCountDateIsBetween(String deviceNo,String start,String end);

    @Query(value = "select new FireEventCount( entity.deviceNo,entity.eventType, substring(entity.countDate, 1, 4),SUM (entity.countNumber)) " +
            "  from FireEventCount entity  where entity.deviceNo=?1 and  substring(entity.countDate, 1, 4)=?2 group by entity.deviceNo, substring(entity.countDate, 1, 4),entity.eventType")
    List<FireEventCount> findByDeviceNoaAndCountDate(String deviceNo,String countDate);


    FireEventCount findByDeviceNoAndCountDateAndEventType(String deviceNo,String countDate,String eventType);




}
