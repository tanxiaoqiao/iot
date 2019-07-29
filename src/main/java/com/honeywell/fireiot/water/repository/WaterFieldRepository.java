package com.honeywell.fireiot.water.repository;

import com.honeywell.fireiot.water.entity.WaterField;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author xiaomingCao
 */
@Repository
public interface WaterFieldRepository extends CrudRepository<WaterField, Long> {

    List<WaterField> findByEui(String eui);

    Optional<WaterField> findByDeviceNoAndName(String deviceNo, String name);

    List<WaterField> findByDeviceNo(String deviceNo);

    List<WaterField> findByDeviceNoAndCheckStatistics(String deviceNo, int statistics);

    @Transactional
    @Modifying
    void deleteAllByDeviceNo(String deviceNo);

}
