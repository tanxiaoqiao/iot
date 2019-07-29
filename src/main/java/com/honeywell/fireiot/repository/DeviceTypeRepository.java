package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.entity.EventType;
import com.honeywell.fireiot.entity.SystemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:51 PM 12/17/2018
 */
@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long>, JpaSpecificationExecutor<DeviceType> {
    String XIAO_HUO_SHUANG = "消火栓";
    String SHOU_BAO= "手报";
    String PAI_YAN_FA="排烟阀";
    String WEI_ZHI="未知";
    String YAN_GAN="烟感";
    String OTHER="其它";

    List<DeviceType> findByName(String name);

    Optional<DeviceType> findByDescriptionAndSystemTypeId(String name, Long id);

    List<DeviceType> findAllBySystemType(SystemType systemType);

    @Query(value = "select entity from DeviceType entity " +
            "JOIN entity.eventTypes type where type.name LIKE ?1 ")
    List<DeviceType> findByEventTypeName(String typeName);


}
