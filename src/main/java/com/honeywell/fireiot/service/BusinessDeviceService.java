package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.BusinessDeviceDto;
import com.honeywell.fireiot.dto.BusinessFireDeviceDto;
import com.honeywell.fireiot.dto.BusinessLocationDto;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/*
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:56 AM
 */
public interface BusinessDeviceService {

    void save(BusinessDevice entity);
//
//    void delete(BusinessDevice t);
//
//    void deleteById(Long id);
//
//    Optional<BusinessDevice> findById(Long id);
//
//    Page<BusinessDevice> findPage();
//
//    Page<BusinessDevice> findPage(Specification<BusinessDevice> specification);

    void save(BusinessDeviceDto dto);


    void delete(BusinessDevice t);

    void deleteById(Long id);

    void deleteById(Long[] ids);

    boolean update(BusinessDeviceDto dto);

    BusinessDeviceDto findById(Long id);

    Pagination<BusinessDeviceDto> findPage();

    Pagination<BusinessDeviceDto> findPage(Specification<BusinessDevice> specification);

    List<BusinessDeviceDto> find(Specification<BusinessDevice> specification);

    BusinessDeviceDto toDto(BusinessDevice entity);

    Map<String, Object> getDeviceCount(String systemType, String building,String floor, String deviceType);

    List<BusinessDevice> getDevice(String systemType, String building,String floor, String deviceType);

    BusinessDevice findByNo(String deviceNo);

    List<BusinessDevice> findByDeviceType(DeviceType deviceType);

    /**
     * 根据LocationId查找设备
     * @param locationId
     * @return
     */
    List<BusinessDevice> findByLocationId(Long locationId);

    BusinessLocationDto toBLDto(BusinessDevice entity);

    Pagination<BusinessLocationDto> getBusinessLocation(Specification<BusinessDevice> specification);

    void setMapLocationInfo(MultipartFile file);

    /**
     * 根据deviceNo更新mapLocation
     * @param mapLocation
     * @param deviceId
     */
    void updateMapLocation(String mapLocation,String deviceId);

    BusinessFireDeviceDto toBFDDto(BusinessDevice entity);

    /**
     * 初始化系统
     */
    void initSystem();

   void createQRCode();

    void updateLocationTest();

   int[] uploadDeviceForPolling(MultipartFile file)throws Exception ;


    int[] readFileAndSaveData(MultipartFile file)throws Exception ;
}
