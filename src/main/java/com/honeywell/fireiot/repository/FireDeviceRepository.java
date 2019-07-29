package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.FireDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:57 PM 12/17/2018
 */
@Repository
public interface FireDeviceRepository extends JpaRepository<FireDevice, Long>, JpaSpecificationExecutor<FireDevice> {
    List<FireDevice> findByBusinessDeviceNo(String BusinessDeviceNo);
    FireDevice findFirstByBusinessDeviceNo(String businessDeviceNo);

    @Query(value = "select new map(entity.status as name, count(entity.id) as value) from FireDevice entity " +
            " group by entity.status")
    List<Map<String, Object>> getDeviceStatusCount();

}
