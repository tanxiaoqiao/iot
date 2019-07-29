package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:54 AM
 */
@Repository
public interface BusinessDeviceRepository extends JpaRepository<BusinessDevice, Long>, JpaSpecificationExecutor<BusinessDevice> {



    @Query(value = "select new map(entity.deviceType.name as name, count(entity.id) as value) from BusinessDevice entity " +
            "where entity.systemType.name like ?1 and entity.locationId = ?2 group by entity.deviceType.name")
    List<Map<String, Object>> getDeviceCount(String systemType, Long locationId);

    @Query(value = "select new map(entity.deviceType.name as name, count(entity.id) as value) from BusinessDevice entity " +
            "where entity.systemType.name like ?1 and entity.locationId = ?2 and entity.deviceType.name = ?3 group by entity.deviceType.name")
    List<Map<String, Object>> getDeviceCount(String systemType, Long locationId, String deviceType);

    @Query(value = "select new map(entity.deviceType.name as name, count(entity.id) as value) from BusinessDevice entity " +
            "where entity.systemType.name like ?1 and entity.deviceType.name = ?2 group by entity.deviceType.name")
    List<Map<String, Object>> getDeviceCount(String systemType, String deviceType);

    @Query(value = "select entity from BusinessDevice entity " +
            "where entity.systemType.name like ?1")
    List<BusinessDevice>  getDevice(String systemType);
    @Query(value = "select entity from BusinessDevice entity " +
            "where entity.systemType.name like ?1 and entity.locationId = ?2")
    List<BusinessDevice> getDevice(String systemType, Long locationId);
    @Query(value = "select entity from BusinessDevice entity " +
            "where entity.systemType.name like ?1 and entity.locationId = ?2 and entity.deviceType.name = ?3")
    List<BusinessDevice> getDevice(String systemType, Long locationId, String deviceType);
    @Query(value = "select entity from BusinessDevice entity " +
            "where entity.systemType.name like ?1 and entity.deviceType.name = ?2")
    List<BusinessDevice> getDevice(String systemType, String deviceType);



    List<BusinessDevice> findByDeviceNo(String DeviceNO);

    List<BusinessDevice> findByLocationIdAndSystemType_Name(Long locationId, String systemTypeName);
    List<BusinessDevice> findAllByDeviceType(DeviceType deviceType);
    /**
     * 根据LocationId查找设备
     * @param locationId
     * @return
     */
    List<BusinessDevice> findByLocationId(Long locationId);

    @Transactional
    @Modifying
    @Query(value = " UPDATE BusinessDevice SET mapLocation = ?1 WHERE deviceNo = ?2 ")
    void updateMapLocation(String mapLocation,String deviceId);

    @Query(value = "select new map(entity.deviceType.name as name, count(entity.id) as value) from BusinessDevice entity " +
            "where entity.systemType.name like ?1 group by entity.deviceType.name")
    List<Map<String, Object>> getDeviceCount(String systemType);

    @Transactional
    @Modifying
    void deleteByDeviceNo(String deviceNo);

    List<BusinessDevice> findByDeviceTypeIn(List<DeviceType> types);
}

