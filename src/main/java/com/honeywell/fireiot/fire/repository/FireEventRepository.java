package com.honeywell.fireiot.fire.repository;

import com.honeywell.fireiot.fire.entity.FireEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:51 PM 12/17/2018
 */
@Repository
public interface FireEventRepository extends JpaRepository<FireEvent, Long>, JpaSpecificationExecutor<FireEvent> {
    String EVENT_TYPE_FAULT = "故障";
    String EVENT_TYPE_FIRE = "报警";
    String EVENT_TYPE_SHIELD = "隔离";
    String EVENT_TYPE_OTHER = "其它";

    String EVENT_STATUS_ADD = "Add";
    String EVENT_STATUS_DEL = "Del";

    Long countByDeviceIdAndEventTypeAndEventStatusAndCreateDatetimeIsBetween(String deviceId,String eventType,String eventStatus,Long start,Long end);
}
