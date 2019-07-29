package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.WaterDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:54 AM
 */
@Repository
public interface WaterDeviceRepository extends JpaRepository<WaterDevice, Long>, JpaSpecificationExecutor<WaterDevice> {

    Optional<WaterDevice> findByDeviceEUI(String eui);

    List<WaterDevice> findByDeviceNo(String deviceNo);

    List<WaterDevice> findAllByDeviceNoIn(List<String> deviceNoList);

    @Query("select entity.deviceEUI from WaterDevice entity where entity.deviceNo = ?1")
    String findEuiByDeviceNo(String deviceNo);
    @Query("select entity.deviceNo from WaterDevice entity where entity.deviceEUI = ?1")
    String findNoByDeviceEui(String deviceEUI);

    @Query("select entity.deviceEUI from WaterDevice entity")
    List<String> findAllEui();

    @Transactional
    @Modifying
    void deleteByDeviceNo(String deviceNo);

}
