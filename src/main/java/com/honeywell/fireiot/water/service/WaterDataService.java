package com.honeywell.fireiot.water.service;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.waterdevice.WaterDataDto;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.water.constant.WaterDeviceStatusEnum;
import com.honeywell.fireiot.water.entity.WaterData;
import com.honeywell.fireiot.water.entity.WaterDeviceStatus;
import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.repository.WaterDataRepository;
import com.honeywell.fireiot.water.repository.WaterEventRepository;
import com.honeywell.fireiot.water.repository.WaterFieldRepository;
import com.honeywell.fireiot.water.sensor.bean.WaterKeyValue;
import com.honeywell.fireiot.water.sensor.constant.WaterSensorItems;
import com.honeywell.fireiot.water.util.WaterPageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Service
public class WaterDataService {

    private static String[] EXCLUDE_FIELD = {
            WaterSensorItems.ELECTRIC_PRESSURE_ALARM,
            WaterSensorItems.BATTERY_TEMPERATURE_ALARM,
            WaterSensorItems.INTERNAL_RESISTANCE_ALARM,
            WaterSensorItems.WATER_OVERFLOW_ALARM,
            WaterSensorItems.WATER_EXIST_ALARM,
    };


    private WaterDataRepository waterDataRepository;

    private WaterFieldRepository waterFieldRepository;

    private WaterEventRepository waterEventRepository;

    private WaterDeviceStatusService waterDeviceStatusService;


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    @Autowired
    public WaterDataService(WaterDataRepository waterDataRepository,
                            WaterFieldRepository waterFieldRepository,
                            WaterEventRepository waterEventRepository,
                            WaterDeviceStatusService waterDeviceStatusService) {
        this.waterDataRepository = waterDataRepository;
        this.waterFieldRepository = waterFieldRepository;
        this.waterEventRepository = waterEventRepository;
        this.waterDeviceStatusService = waterDeviceStatusService;
    }

    /**
     * 保存
     *
     * @param waterData
     */
    public void save(WaterData waterData){
        waterDataRepository.save(waterData);
    }


    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortType
     * @param deviceNo
     * @param fieldName
     * @param start
     * @param end
     * @return
     */
    @SuppressWarnings("Duplicates")
    public Page<WaterData> getPage(int page,
                                    int pageSize,
                                    String sortField,
                                    String sortType,
                                    String deviceNo,
                                    String fieldName,
                                    String start,
                                    String end
    ){

        Pageable pageable = WaterPageUtil.buildPageRequest(page, pageSize, sortField, sortType);
        Specification<WaterData> spec = (root, cb, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.hasText(deviceNo)){
                String eui = getEUI(deviceNo);
                predicates.add(builder.and(builder.equal(root.get("eui"), eui)));
            }
            if(StringUtils.hasText(fieldName)){
                predicates.add(builder.and(builder.equal(root.get("fieldName"), fieldName)));
            }
            if(StringUtils.hasText(start) && StringUtils.hasText(end)){
                long startTime = timeString2long(start);
                long endTime = timeString2long(end);
                predicates.add(builder.and(builder.greaterThanOrEqualTo(root.get("startTime"), startTime)));
                predicates.add(builder.and(builder.lessThanOrEqualTo(root.get("startTime"), endTime)));
            }
            if(CollectionUtils.isEmpty(predicates)){
                return null;
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return  waterDataRepository.findAll(spec, pageable);
    }


    /**
     * 时间范围查询历史数据
     *
     * @param deviceNo
     * @param fieldName
     * @param start
     * @param end
     * @return
     */
    public Map<String, Object> getByTimeRange(String deviceNo,
                                              String fieldName,
                                              String start,
                                              String end){
        WaterField waterField = waterFieldRepository.findByDeviceNoAndName(deviceNo, fieldName)
                .orElseThrow(() -> new RuntimeException(String
                        .format("device field not found deviceNO: %s EUI: %s", deviceNo, fieldName)));
        String eui = waterField.getEui();
        long startTime = timeString2long(start);
        long endTime = timeString2long(end);

        Specification<WaterData> spec =
                (root, query, builder) ->
                        builder.and(
                                builder.greaterThanOrEqualTo(root.get("timestamp"), startTime),
                                builder.lessThanOrEqualTo(root.get("timestamp"), endTime),
                                builder.equal(root.get("eui"), eui),
                                builder.equal(root.get("fieldName"), fieldName));
        List<WaterData> datas = waterDataRepository.findAll(spec);
        Map<String, Object> res = new HashMap<>();
        res.put("field", waterField);
        res.put("datas", datas);
        return res;
    }


    /**
     * 获取最新历史记录
     *
     * @param deviceNo
     * @param fieldName
     * @return
     */
    public WaterDataDto getTop(String deviceNo, String fieldName){
        WaterField waterField = waterFieldRepository.findByDeviceNoAndName(deviceNo, fieldName)
                .orElseThrow(() -> new RuntimeException(String
                        .format("device field not found deviceNO: %s EUI: %s", deviceNo, fieldName)));
        String eui = waterField.getEui();
        WaterData waterData = waterDataRepository.findTopByEuiAndFieldNameOrderByTimestampDesc(eui, fieldName)
                .orElseThrow(() -> new BusinessException(ErrorEnum.NOT_FOUND));
        WaterDataDto waterDataDto = new WaterDataDto();
        BeanUtils.copyProperties(waterData, waterDataDto);
        waterDataDto.setUnitSymbol(waterField.getUnitSymbol());
        return waterDataDto;
    }

    /**
     * 获取最新的几个数据
     *
     * @param eui
     * @param fieldName
     * @param startTime 起始时间
     * @return
     */
    public List<WaterData> getRecentValue(String eui, String fieldName, Long startTime){
        List<WaterData> recentWaterData = waterDataRepository.findTop10ByEuiAndFieldNameAndTimestampGreaterThanEqualOrderByTimestampDesc(eui, fieldName, startTime);
        return recentWaterData;
    }

    /**
     * 获取最新历史记录
     *
     * @param deviceNo
     * @return
     */
    public List<Map<String, Object>> getValuesByDeviceNo(String deviceNo){
        // 存放Value
        List<Map<String, Object>> dataList = new ArrayList<>();

        List<WaterField> waterFieldList = waterFieldRepository.findByDeviceNo(deviceNo);
        for (WaterField waterField : waterFieldList) {
            // 判断是否为报警Field，若为1，则为报警字段，忽略值
            if (waterField.getCheckTrueFalse() == 1) {
                continue;
            }

            String eui = waterField.getEui();
            Optional<WaterData> waterData = waterDataRepository.findTopByEuiAndFieldNameOrderByTimestampDesc(eui, waterField.getName());

            waterData.ifPresent(data -> {

                // 获取状态
                WaterDeviceStatus status = waterDeviceStatusService.getStatus(data.getEui());


                String statusTitle = WaterDeviceStatusEnum.get(status.getStatus()).getDescription();
                Map<String, Object> valueMap = new HashMap<>();
                valueMap.put("max", waterField.getMax());
                valueMap.put("min", waterField.getMin());
                valueMap.put("unit", waterField.getUnitSymbol());
                valueMap.put("status", status.getStatus());
                valueMap.put("statusTitle", statusTitle);
                valueMap.put("value", data.getValue());
                valueMap.put("title", waterField.getTitle());
                dataList.add(valueMap);
            });
        }

        return dataList;
    }


    /**
     * 保存
     *
     * @param kv
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(WaterKeyValue kv){
        Map<String, String> data = kv.getData();
        List<WaterData> list = data.keySet()
                .stream()
                .filter(k -> !Arrays.stream(EXCLUDE_FIELD).anyMatch(s-> s.equals(k)))
                .map(k -> {
                    WaterData waterData = new WaterData();
                    waterData.setFieldName(k);
                    waterData.setValue(data.get(k));
                    waterData.setEui(kv.getEui());
                    waterData.setTimestamp(kv.getTimestamp());
                    return waterData;
                })
                .collect(toList());
        this.waterDataRepository.saveAll(list);
    }


    /**
     * 时间转换
     *
     * @param localDateTime
     * @return
     */
    private long timeString2long(String localDateTime) {
        return  LocalDateTime.parse(localDateTime, FORMATTER)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }


    /**
     * get EUI by deviceNo
     *
     * @param deviceNo
     * @return
     */
    private String getEUI(String deviceNo){
        return waterFieldRepository.findByDeviceNo(deviceNo)
                .stream()
                .findAny()
                .map(WaterField::getEui)
                .orElseThrow(() -> new BusinessException(ErrorEnum.NOT_FOUND));
    }



}
