package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.waterdevice.ValueDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceDetailInfoDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceSearch;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.WaterDevice;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:56 AM
 */
public interface WaterDeviceService {

    Optional<WaterDevice> findByEui(String eui);

    void save(WaterDevice entity);

    void delete(WaterDevice t);

    void deleteById(Long id);

    Optional<WaterDevice> findById(Long id);
    Page<WaterDevice> findPage();


    WaterDeviceDto toDto(WaterDevice entity);
    List<WaterDeviceDto> toDto(List<WaterDevice> entity);

    List<ValueDto> findWaterDeviceCount(WaterDeviceSearch search);
    List<WaterDeviceDto> findWaterDevice(WaterDeviceSearch search);

    List<WaterDeviceDto> transferToWaterDeviceDto(List<BusinessDevice> businessDevices);

    List<WaterDeviceDto> findWaterDeviceWithValue(WaterDeviceSearch search);

    List<WaterDeviceDto> findWaterDeviceWithWeekValue(WaterDeviceSearch search);

    List<WaterDeviceDto> findWaterDeviceWithMonthValue(WaterDeviceSearch search);

    List<WaterDeviceDto> findWaterDeviceWithDayValue(WaterDeviceSearch search);

    List<Map<String, Object>> findWaterDeviceWithDayValue(String deviceNo, String startTimestamp, String endTimestamp);

    WaterDeviceDetailInfoDto findDeviceDetailByDeviceNo(String deviceNo);

    List<WaterDevice> findByDeviceNoList(List<String> deviceNo);

    WaterDevice findByDeviceNo(String deviceNo);

    List<WaterDevice> findAll();

    List<Map<String, Object>> getWaterValues(String deviceNo);

    List<WaterDeviceDto> getRangeDateWaterValues(List<WaterDeviceDto> dtos, int day);

    List<Map<String, Object>> getDayWaterValues(String deviceNo);

    Pagination findPage(Specification<WaterDevice> specification);
    Pagination findPage2Dto();

    List<WaterDeviceDto>  find(Specification<WaterDevice> specification);

    List<Map<String,Object>> countDeviceByStatus();

    List countDeviceByStatusAndLocation(WaterDeviceSearch search);

    List<Map<String,Object>> countDeviceByDeviceType();
}
