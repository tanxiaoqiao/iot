package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.waterdevice.ValueDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceDetailInfoDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceSearch;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.Location;
import com.honeywell.fireiot.entity.WaterDevice;
import com.honeywell.fireiot.repository.BusinessDeviceRepository;
import com.honeywell.fireiot.repository.SystemTypeRepository;
import com.honeywell.fireiot.repository.WaterDeviceRepository;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.service.WaterDeviceService;
import com.honeywell.fireiot.utils.*;
import com.honeywell.fireiot.water.constant.WaterDeviceStatusEnum;
import com.honeywell.fireiot.water.service.WaterDailyDataService;
import com.honeywell.fireiot.water.service.WaterDataService;
import com.honeywell.fireiot.water.service.WaterDeviceStatusService;
import com.honeywell.fireiot.water.service.WaterFieldService;
import org.apache.commons.collections4.list.TreeList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 9:57 AM
 */
@Service
public class WaterDeviceServiceImpl implements WaterDeviceService {

    @Autowired
    BusinessDeviceService businessDeviceService;
    @Autowired
    LocationService locationService;
    @Autowired
    SystemTypeRepository systemTypeRepository;

    @Autowired
    WaterDeviceRepository waterDeviceRepo;
    @Autowired
    WaterDataService waterDataService;
    @Autowired
    WaterFieldService waterFieldService;
    @Autowired
    WaterDailyDataService waterDailyDataService;

    @Autowired
    WaterDeviceStatusService waterDeviceStatusService;

    @Autowired
    BusinessDeviceRepository businessDeviceRepo;

    @Autowired
    EntityManager entityManager;


    /**
     * find water device by EUI
     *
     * @param eui
     * @return
     * @author xiaoming.cao
     */
    @Override
    public Optional<WaterDevice> findByEui(String eui) {
        return waterDeviceRepo.findByDeviceEUI(eui);
    }

    @Override
    public void save(WaterDevice entity) {
        waterDeviceRepo.save(entity);
    }

    @Override
    public void delete(WaterDevice t) {
        waterDeviceRepo.delete(t);
    }

    @Override
    public void deleteById(Long id) {
        waterDeviceRepo.deleteById(id);
    }

    @Override
    public Optional<WaterDevice> findById(Long id) {
        Optional<WaterDevice> opt = waterDeviceRepo.findById(id);
        return opt;
    }

    @Override
    public Page<WaterDevice> findPage() {
        Page<WaterDevice> page = waterDeviceRepo.findAll(JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public WaterDeviceDto toDto(WaterDevice entity) {
        WaterDeviceDto dto = new WaterDeviceDto();
        BeanUtils.copyProperties(entity, dto);
        dto.setDeviceLabel(entity.getLabel());
        BusinessDevice businessDevice = businessDeviceService.findByNo(entity.getDeviceNo());
        if (businessDevice != null) {
            if (businessDevice.getDeviceType() != null) {
                dto.setDeviceType(businessDevice.getDeviceType().getName());
            }
            Location location = locationService.getInfo(businessDevice.getLocationId());
            dto.setLocationName(location != null ? location.getFullName() : businessDevice.getLocationDetail());
            dto.setLocationDetail(businessDevice.getLocationDetail());
        }
        dto.setStatus(WaterDeviceStatusEnum.get(waterDeviceStatusService.getStatus(entity.getDeviceEUI()).getStatus()).getDescription());
        return dto;
    }

    @Override
    public List<WaterDeviceDto> toDto(List<WaterDevice> list) {
        if (list == null) {
            return null;
        }
        List<WaterDeviceDto> dtoList = new ArrayList<>();
        list.forEach(entity -> {
            dtoList.add(toDto(entity));
        });
        return dtoList;
    }

    /**
     * 查询水系统设备数量
     *
     * @param search
     * @return
     */
    @Override
    public List<ValueDto> findWaterDeviceCount(WaterDeviceSearch search) {

        // 获取水系统设备数量
        Map<String, Object> deviceCount = businessDeviceService.getDeviceCount("水", search.getBuilding(), search.getFloor(), search.getDeviceType());
        List<ValueDto> deviceInfoDtos = new ArrayList<>();
        if (deviceCount != null) {
            deviceCount.forEach((key, value) -> {
                ValueDto info = new ValueDto();
                info.setName(key);
                info.setValue(value);
                deviceInfoDtos.add(info);
            });
        }
        return deviceInfoDtos;
    }

    /**
     * 获取水系统设备信息（不包含设备value）
     *
     * @param search 楼栋、楼层、设备类型
     * @return
     */
    @Override
    public List<WaterDeviceDto> findWaterDevice(WaterDeviceSearch search) {
        // 获取水系统设备
        List<BusinessDevice> businessDeviceList = businessDeviceService.getDevice("水", search.getBuilding(), search.getFloor(), search.getDeviceType());

        List<WaterDeviceDto> waterDeviceDtoList = transferToWaterDeviceDto(businessDeviceList);
        return waterDeviceDtoList;
    }

    /**
     * businessDevice --> waterDeviceDto
     *
     * @param businessDevices
     * @return
     */
    @Override
    public List<WaterDeviceDto> transferToWaterDeviceDto(List<BusinessDevice> businessDevices) {
        List<Long> locationIds = new ArrayList<>();
        List<String> deviceNos = new ArrayList<>();

        for (BusinessDevice bd : businessDevices) {
            locationIds.add(bd.getLocationId());
            deviceNos.add(bd.getDeviceNo());
        }

        List<WaterDevice> waterDevices = waterDeviceRepo.findAllByDeviceNoIn(deviceNos);
        List<Location> locations = locationService.findAll(locationIds);


        // 组装map，key: deviceNo, value: waterDevice
        Map<String, WaterDevice> wdMaps = waterDevices
                .stream()
                .collect(Collectors.toMap(d -> d.getDeviceNo(), d -> d));
        // 组装map，key: locationId, value: fullName
        Map<Long, String> locationMaps = locations
                .stream()
                .collect(Collectors.toMap(l -> l.getId(), l -> l.getFullName()));

        return businessDevices
                .stream()
                .map(be -> {
                    WaterDevice waterDevice = wdMaps.get(be.getDeviceNo());

                    WaterDeviceDto dto = new WaterDeviceDto();
                    BeanUtils.copyProperties(waterDevice, dto);
                    if (be.getDeviceType() != null) {
                        dto.setDeviceType(be.getDeviceType().getName());

                    }
                    dto.setDeviceId(be.getDeviceNo());
                    dto.setLocationName(locationMaps.get(be.getLocationId()));
                    dto.setMapLocation(be.getMapLocation());
                    dto.setDeviceLabel(waterDevice.getLabel());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据search条件，获取水系统设备信息（包含设备当前value）
     *
     * @param search
     * @return
     */
    @Override
    public List<WaterDeviceDto> findWaterDeviceWithValue(WaterDeviceSearch search) {
        List<WaterDeviceDto> waterDeviceDtoList = findWaterDevice(search);
        // 获取设备值
        waterDeviceDtoList.forEach(waterDeviceDto -> {
            waterDeviceDto.setValues(getWaterValues(waterDeviceDto.getDeviceNo()));
        });
        return waterDeviceDtoList;
    }

    /**
     * 根据search条件，获取水系统设备信息（包含设备一周的value）
     *
     * @param search
     * @return
     */
    @Override
    public List<WaterDeviceDto> findWaterDeviceWithWeekValue(WaterDeviceSearch search) {
        List<WaterDeviceDto> waterDeviceDtoList = findWaterDevice(search);
        waterDeviceDtoList = getRangeDateWaterValues(waterDeviceDtoList, 7);
        return waterDeviceDtoList;
    }

    /**
     * 根据search条件，获取水系统设备信息（包含设备一月的value）
     *
     * @param search
     * @return
     */
    @Override
    public List<WaterDeviceDto> findWaterDeviceWithMonthValue(WaterDeviceSearch search) {
        List<WaterDeviceDto> waterDeviceDtoList = findWaterDevice(search);
        waterDeviceDtoList = getRangeDateWaterValues(waterDeviceDtoList, 30);
        return waterDeviceDtoList;
    }

    /**
     * 根据search条件，获取水系统设备信息（包含设备当天的部分value）
     *
     * @param search
     * @return
     */
    @Override
    public List<WaterDeviceDto> findWaterDeviceWithDayValue(WaterDeviceSearch search) {
        List<WaterDeviceDto> waterDeviceDtoList = findWaterDevice(search);
        // 获取设备值
        waterDeviceDtoList.forEach(waterDeviceDto -> {
            waterDeviceDto.setValues(getDayWaterValues(waterDeviceDto.getDeviceNo()));
        });
        return waterDeviceDtoList;
    }

    /**
     * 查找某个设备一段时间内的值
     *
     * @param deviceNo
     * @param startTimestamp
     * @param endTimestamp
     * @return
     */
    @Override
    public List<Map<String, Object>> findWaterDeviceWithDayValue(String deviceNo, String startTimestamp, String endTimestamp) {
        return waterFieldService.findByDeviceNo(deviceNo)
                .stream()
                .map(field -> waterDataService.getByTimeRange(deviceNo, field.getName(), startTimestamp, endTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public WaterDeviceDetailInfoDto findDeviceDetailByDeviceNo(String deviceNo) {
        WaterDeviceDetailInfoDto waterDeviceDto = new WaterDeviceDetailInfoDto();
        //查找水系统设备
        List<WaterDevice> deviceList = waterDeviceRepo.findByDeviceNo(deviceNo);
        if (null == deviceList || deviceList.isEmpty()) {
            return null;
        }
        WaterDevice waterDevice = deviceList.get(0);
        BeanUtils.copyProperties(waterDevice, waterDeviceDto);
        waterDeviceDto.setDeviceId(deviceNo);
        waterDeviceDto.setDeviceLabel(waterDevice.getLabel());

        //查找台帐设备
        BusinessDevice device = businessDeviceService.findByNo(deviceNo);
        if (device != null) {
            BeanUtils.copyProperties(device, waterDeviceDto);

            waterDeviceDto.setDeviceType(device.getDeviceType().getName());
            Location location = locationService.getInfo(device.getLocationId());
            if (location != null) {
                waterDeviceDto.setLocationName(location.getFullName());
            }
        }

        // 获取并设置水系统设备值
        waterDeviceDto.setValues(getWaterValues(deviceNo));
        return waterDeviceDto;
    }

    @Override
    public List<WaterDevice> findByDeviceNoList(List<String> deviceNo) {
        return waterDeviceRepo.findAllByDeviceNoIn(deviceNo);
    }

    @Override
    public WaterDevice findByDeviceNo(String deviceNo) {
        List<WaterDevice> waterDeviceList = waterDeviceRepo.findByDeviceNo(deviceNo);
        return waterDeviceList == null ? null : waterDeviceList.get(0);
    }

    @Override
    public List<WaterDevice> findAll() {
        return waterDeviceRepo.findAll();
    }

    /**
     * 获取水系统设备的当前值
     *
     * @param deviceNo
     * @return
     */
    @Override
    public List<Map<String, Object>> getWaterValues(String deviceNo) {
        List<Map<String, Object>> waterValues = waterDataService.getValuesByDeviceNo(deviceNo);
        return waterValues;
    }

    /**
     * 获取水系统设备一段日期内的值
     *
     * @param dtos
     * @return
     */
    @Override
    public List<WaterDeviceDto> getRangeDateWaterValues(List<WaterDeviceDto> dtos, int day) {
        List<String> euis = dtos.stream()
                .map(dto -> dto.getDeviceEUI())
                .collect(Collectors.toList());

        Map<String, List<Map<String, Object>>> dailyData = waterDailyDataService.getDailyData(
                LocalDate.now().minusDays(day-1),
                LocalDate.now(), euis
        );
        for (WaterDeviceDto dto : dtos) {
            dto.setValues(dailyData.get(dto.getDeviceEUI()));
        }

        return dtos;
    }


    /**
     * 获取水系统设备一天内的值
     *
     * @param deviceNo
     * @return
     */
    @Override
    public List getDayWaterValues(String deviceNo) {
        // 获取当天的起始时间戳
        long startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List dataWaterValues = new ArrayList<>();

        waterFieldService.findByDeviceNo(deviceNo).forEach(field -> {
            // 创建集合，用于存放最近的n条数据
            List<Object> list = new ArrayList<>();

            // 获取某个设备最近的n条数据并封装到集合中
            waterDataService.getRecentValue(field.getEui(), field.getName(), startTime).forEach(data -> {
                Map<String, Object> valueMap = new HashMap<>();
                valueMap.put("date", data.getTimestamp());
                valueMap.put("unit", field.getUnitSymbol());
//                valueMap.put("title", field.getTitle());
                valueMap.put("value", data.getValue());
                valueMap.put("max", field.getMax());
                valueMap.put("min", field.getMin());
                list.add(valueMap);
            });

            // format集合格式后返回
            Map<String, Object> recentValueMap = new LinkedHashMap<>();
            recentValueMap.put("title", field.getTitle());
            recentValueMap.put("values", list);
            dataWaterValues.add(recentValueMap);

        });
        return dataWaterValues;
    }


//    -----------------------------第二版--------------------------------------------


    @Override
    public Pagination findPage(Specification<WaterDevice> specification) {
        Page<WaterDevice> pageList = waterDeviceRepo.findAll(specification, JpaUtils.getPageRequest());
        Pagination page = new Pagination((int) pageList.getTotalElements(), pageList.getContent());
        return page;
    }

    @Override
    public Pagination findPage2Dto() {
        StringBuilder querySql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        querySql.append("select entity ");
        countSql.append("select entity.id ");


        StringBuilder whereString = new StringBuilder();
        whereString.append(" from WaterDevice entity " +
                "left join BusinessDevice entity2 on entity.deviceNo=entity2.deviceNo where 1=1 ");

        PageSearch pageSearch = PageHolder.getHolder();

        for (Map.Entry<String, Object> entry : pageSearch.getPageParameter().entrySet()) {
            if ("locationDetail".equals(entry.getKey())) {
                whereString.append(" and ").append(" entity2.locationDetail like '%").append(entry.getValue()).append("%'");
            } else if ("deviceLabel".equals(entry.getKey())) {
                whereString.append(" and ").append(" entity2.deviceLabel like '%").append(entry.getValue()).append("%'");
            } else if ("status".equals(entry.getKey())) {
                whereString.append(" and ").append(" entity2.deviceStatus like '%").append(entry.getValue()).append("%'");
            } else {
                whereString.append(" and ").append(" entity." + entry.getKey() + " like '%").append(entry.getValue()).append("%'");
            }
        }

        if ("locationDetail".equals(pageSearch.getOrderColumn())) {
            whereString.append(" order by entity2.locationDetail ").append(pageSearch.getOrderType().toString());
        } else if ("deviceLabel".equals(pageSearch.getOrderColumn())) {
            whereString.append(" order by entity2.deviceLabel ").append(pageSearch.getOrderType().toString());
        } else if ("status".equals(pageSearch.getOrderColumn())) {
            whereString.append(" order by entity2.deviceStatus ").append(pageSearch.getOrderType().toString());
        } else {
            whereString.append(" order by entity.").append(pageSearch.getOrderColumn()).append(" ").append(pageSearch.getOrderType().toString());
        }


        Query query = entityManager.createQuery(querySql.append(whereString).toString());
        Query countQuery = entityManager.createQuery(countSql.append(whereString).toString());


        // 总数
        int count = (int) countQuery.getResultList().size();
        // 分页
        query.setMaxResults(pageSearch.getPs());
        query.setFirstResult(pageSearch.getPi() * pageSearch.getPs());

//        Page<WaterDevice> pageList = waterDeviceRepo.findAll(specification, JpaUtils.getPageRequest());
        Pagination page = new Pagination(count, toDto(query.getResultList()));
        return page;
    }
    @Override
    public List<WaterDeviceDto>  find(Specification<WaterDevice> specification){
        List<WaterDevice>  deviceList = waterDeviceRepo.findAll(specification);
        List<WaterDeviceDto> dtoList = toDto(deviceList);
        return dtoList;
    }
    @Override
    public List<Map<String, Object>> countDeviceByStatus() {

        Map<Object, Object> map = new HashMap<>(3);
        List<WaterDevice> waterDeviceList = waterDeviceRepo.findAll();
        Map<Integer, Integer> mapStatus = new HashMap<>(3);
        waterDeviceList.forEach(waterDevice -> {
            int status = waterDeviceStatusService.getStatus(waterDevice.getDeviceEUI()).getStatus();
            if (mapStatus.containsKey(status)) {
                mapStatus.put(status, mapStatus.get(status) + 1);
            } else {
                mapStatus.put(status, 1);
            }
        });
        mapStatus.forEach((int1, int2) -> map.put(WaterDeviceStatusEnum.get(int1), int2));

        return MapUtil.map2Listmap(map);
    }

    @Override
    public List countDeviceByStatusAndLocation(WaterDeviceSearch search) {

        List<WaterDeviceDto> deviceDtos = findWaterDevice(search);

        Map<String, Map<Object, Object>> deviceMapStatus = new HashMap<>();

        deviceDtos.forEach(waterDevice -> {
            int status = waterDeviceStatusService.getStatus(waterDevice.getDeviceEUI()).getStatus();
            String deviceType = waterDevice.getDeviceType();

            Map<Object, Object> mapStatus;
            if (deviceMapStatus.containsKey(deviceType)) {
                mapStatus = deviceMapStatus.get(deviceType);
            } else {
                mapStatus = new HashMap<>();
                deviceMapStatus.put(deviceType, mapStatus);
            }

            String deviceStatus = WaterDeviceStatusEnum.get(status).toString();
            if (mapStatus.containsKey(deviceStatus)) {
                mapStatus.put(deviceStatus, Integer.valueOf(mapStatus.get(deviceStatus).toString()) + 1);
            } else {
                mapStatus.put(deviceStatus, 1);
            }
        });

        List resultList = new TreeList();
        deviceMapStatus.forEach((var1, var2) -> {
            Map resultMap = new HashMap<>();
            resultMap.put("deviceType", var1);
            resultMap.put("values", MapUtil.map2Listmap(var2));
            resultList.add(resultMap);
        });
//        mapStatus.forEach((int1, int2) -> map.put(WaterDeviceStatusEnum.get(int1),int2) );

        return resultList;
    }

    @Override
    public List<Map<String, Object>> countDeviceByDeviceType() {
        return businessDeviceRepo.getDeviceCount("%" + SystemTypeRepository.WATERSYSTEM + "%");
    }
}
