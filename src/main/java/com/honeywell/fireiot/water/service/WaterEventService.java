package com.honeywell.fireiot.water.service;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.waterdevice.WaterEventDto;
import com.honeywell.fireiot.entity.WaterDevice;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.WaterDeviceRepository;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.water.WaterEventStatsBo;
import com.honeywell.fireiot.water.constant.WaterDeviceStatusEnum;
import com.honeywell.fireiot.water.entity.WaterEvent;
import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.repository.WaterEventRepository;
import com.honeywell.fireiot.water.repository.WaterFieldRepository;
import com.honeywell.fireiot.water.sensor.bean.WaterKeyValue;
import com.honeywell.fireiot.water.util.WaterPageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
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
 * @date: 2019/1/2
 */
@Service
public class WaterEventService {

    private WaterEventRepository waterEventRepository;

    private WaterFieldService waterFieldService;

    private WaterFieldRepository waterFieldRepository;

    private static final String NEW_EVENT_PREFIX = "Add";

    private static final String END_EVENT_PREFIX = "Del";

    private WaterDeviceStatusService waterDeviceStatusService;

    @Autowired
    private WaterDeviceRepository waterDeviceRepository;


    @Autowired
    public WaterEventService(WaterEventRepository waterEventRepository,
                             WaterFieldService waterFieldService,
                             WaterFieldRepository waterFieldRepository,
                             WaterDeviceStatusService waterDeviceStatusService) {
        this.waterEventRepository = waterEventRepository;
        this.waterFieldService = waterFieldService;
        this.waterFieldRepository = waterFieldRepository;
        this.waterDeviceStatusService = waterDeviceStatusService;
    }

    public WaterEvent save(WaterEvent waterEvent){
        Optional<WaterDevice> deviceOpt =  waterDeviceRepository.findByDeviceEUI(waterEvent.getEui());
        if(deviceOpt.isPresent()){
            waterEvent.setDeviceLabel(deviceOpt.get().getLabel());
        }
        return waterEventRepository.save(waterEvent);
    }


    /**
     * 查询最新
     *
     * @param eui
     * @param fieldName
     * @return
     */
    public Optional<WaterEvent> findTop(String eui, String fieldName){
        return waterEventRepository.findTopByEuiAndFieldNameOrderByStartTimeDesc(eui, fieldName);
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
    public Page<WaterEvent> getPage(int page,
                                    int pageSize,
                                    String sortField,
                                    String sortType,
                                    String deviceNo,
                                    String fieldName,
                                    String start,
                                    String end
    ){

        Pageable pageable = WaterPageUtil.buildPageRequest(page, pageSize, sortField, sortType);
        Specification<WaterEvent> spec = (root, cb, builder) -> {
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

        return  waterEventRepository.findAll(spec, pageable);
    }


    /**
     * 获取最新报警
     *
     * @param deviceNo
     * @param fieldName
     * @return
     */
    public WaterEvent getTop(String deviceNo, String fieldName){
        WaterField waterField = waterFieldRepository.findByDeviceNoAndName(deviceNo, fieldName)
                .orElseThrow(() -> new RuntimeException(String
                        .format("device field not found deviceNO: %s EUI: %s", deviceNo, fieldName)));
        String eui = waterField.getEui();
        return waterEventRepository.findTopByEuiAndFieldNameOrderByStartTimeDesc(eui, fieldName)
                .orElseThrow(() -> new BusinessException(ErrorEnum.NOT_FOUND));
    }


    /**
     * 时间范围查询历史报警
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
        Specification<WaterEvent> spec = (root, cb, builder) ->
                builder.and(
                        builder.greaterThanOrEqualTo(root.get("startTime"), startTime),
                        builder.lessThanOrEqualTo(root.get("startTime"), endTime),
                        builder.equal(root.get("eui"), eui),
                        builder.equal(root.get("fieldName"), fieldName));
        List<WaterEvent> datas = waterEventRepository.findAll(spec);
        Map<String, Object> res = new HashMap<>();
        res.put("field", waterField);
        res.put("datas", datas);
        return res;
    }


    /**
     * 检查上下限
     *
     * @param keyValue
     * @return
     */
    public List<WaterEvent> checkAlarm(WaterKeyValue keyValue){
        Map<String, String> data = keyValue.getData();
        String eui = keyValue.getEui();
        return waterFieldService.findByEui(eui)
                .stream()
                .filter(f -> Objects.nonNull(data.get(f.getName())))
                .filter(f -> f.getCheckRange() == 1 || f.getCheckTrueFalse() == 1)
                .map(f -> {
                    if(f.getCheckRange() == 1){
                        return getRangeEvent(keyValue, f);
                    }else if(f.getCheckTrueFalse() == 1){
                        return getTrueFalseEvent(keyValue, f);
                    }else{
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @SuppressWarnings("Duplicates")
    private WaterEvent getTrueFalseEvent(WaterKeyValue kv, WaterField f){
        Map<String, String> data = kv.getData();
        int flag = Integer.parseInt(data.get(f.getName()));
        // 判断上一条事件记录
        Optional<WaterEvent> latest = findTop(kv.getEui(), f.getName());

        if(latest.isPresent()){
            // 如果存在报警记录
            WaterEvent latestEvent = latest.get();
            boolean isAdd = NEW_EVENT_PREFIX.equals(latestEvent.getType());

            if(isAdd){
                if(flag == 0){
                    // 上次报警未结束 当前不报警
                    setStatusNormal(kv);
                    latestEvent.setType(END_EVENT_PREFIX);
                    latestEvent.setEndTime(kv.getTimestamp());
                    return save(latestEvent);
                }
                // 上次报警未结束 当前报警
                setStatusException(kv);
                return null;
            }else{
                if(flag == 1){
                    // 上次事件已结束 当前报警
                    setStatusException(kv);
                    WaterEvent event = new WaterEvent();
                    event.setStartTime(kv.getTimestamp());
                    event.setType(NEW_EVENT_PREFIX);
                    event.setFieldName(f.getName());
                    event.setEui(kv.getEui());
                    event.setDescription(latestEvent.getDescription());
                    event.setValue((double)flag);
                    event.setUnit(f.getUnitSymbol());
                    return save(event);
                }
                // 上次事件已结束 当前未报警
                setStatusNormal(kv);
                return null;
            }

        }else{
            // 没有事件历史
            if(flag == 1){
                setStatusException(kv);
                WaterEvent event = new WaterEvent();
                event.setEui(kv.getEui());
                event.setFieldName(f.getName());
                event.setValue((double)flag);
                event.setType(NEW_EVENT_PREFIX);
                event.setDescription(f.getTitle());
                event.setStartTime(kv.getTimestamp());
                event.setUnit(f.getUnitSymbol());
                return save(event);
            }
            setStatusNormal(kv);
            return null;
        }
    }


    private WaterEvent getRangeEvent(WaterKeyValue kv, WaterField f){

        Map<String, String> data = kv.getData();

        double d =  Double.parseDouble(data.get(f.getName()));

        // 判断上一条事件记录
        Optional<WaterEvent> latest = findTop(kv.getEui(), f.getName());

        if(latest.isPresent()){
            WaterEvent latestEvent = latest.get();
            boolean isAdd = NEW_EVENT_PREFIX.equals(latestEvent.getType());
            if(isAdd){
                if(d >= f.getMin() && d <= f.getMax()){
                    // 上次报警未结束 当前正常
                    setStatusNormal(kv);
                    latestEvent.setEndTime(kv.getTimestamp());
                    latestEvent.setType(END_EVENT_PREFIX);
                    return save(latestEvent);
                }
                // 上次未结束 当前报警
                setStatusException(kv);
                return null;
            }else{
                if(d < f.getMin() || d > f.getMax()){
                    // 上次报警已结束 当前报警
                    setStatusException(kv);
                    return getNewRangeEvent(d, f, kv);
                }

                // 上次报警已结束 当前正常
                setStatusNormal(kv);
                return null;
            }
        }else{
            if(d < f.getMin() || d > f.getMax()){
                // 无历史报警 当前报警
                setStatusException(kv);
                return getNewRangeEvent(d, f, kv);
            }
            setStatusNormal(kv);
            return null;
        }
    }

    private WaterEvent getNewRangeEvent(double d, WaterField f, WaterKeyValue kv){
        // 超上限
        if(d > f.getMax()){
            WaterEvent event = new WaterEvent();
            event.setEui(kv.getEui());
            event.setFieldName(f.getName());
            event.setMax(f.getMax());
            event.setMin(f.getMin());
            event.setValue(d);
            event.setType(NEW_EVENT_PREFIX);
            event.setDescription(f.getTitle() + "高位异常");
            event.setStartTime(kv.getTimestamp());
            event.setUnit(f.getUnitSymbol());
            return save(event);

        }

        // 超下限
        if(d < f.getMin()){
            WaterEvent event = new WaterEvent();
            event.setEui(kv.getEui());
            event.setFieldName(f.getName());
            event.setMax(f.getMax());
            event.setMin(f.getMin());
            event.setValue(d);
            event.setType(NEW_EVENT_PREFIX);
            event.setDescription(f.getTitle() + "低位异常");
            event.setStartTime(kv.getTimestamp());
            event.setUnit(f.getUnitSymbol());
            return save(event);
        }

        return null;
    }



    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 时间转换
     *
     * @param localDateTime
     * @return
     */
    private long timeString2long(String localDateTime){
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


    /**
     * 更新设备状态为正常状态
     *
     * @param kv
     */
    private void setStatusNormal(WaterKeyValue kv){
        waterDeviceStatusService.updateStatus(
                kv.getEui(),
                WaterDeviceStatusEnum.ONLINE,
                kv.getTimestamp()
        );
    }


    /**
     * 更新设备状态为异常
     *
     * @param kv
     */
    private void setStatusException(WaterKeyValue kv){
        waterDeviceStatusService.updateStatus(
                kv.getEui(),
                WaterDeviceStatusEnum.EXCEPTION,
                kv.getTimestamp());
    }


//---------------------------------------------第二版----------------------------------

    public Pagination findPage(Specification<WaterEvent> specification) {
        Page<WaterEvent> pageList = waterEventRepository.findAll(specification, JpaUtils.getPageRequest());

        Pagination page = new Pagination((int)pageList.getTotalElements(),pageList.getContent().stream().map(waterEvent -> toDto(waterEvent)).collect(toList()));
        return page;
    }

    public List<WaterEvent> find(Specification<WaterEvent> specification) {
       List<WaterEvent>  eventList = waterEventRepository.findAll(specification);
        return eventList;
    }


    public long countByWaterEventStatsBo(WaterEventStatsBo waterEventStatsBo) {
        Specification<WaterEvent> spec = (root, cb, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.hasText(waterEventStatsBo.getDescription())){
                predicates.add(builder.like(root.get("description"), "%"+waterEventStatsBo.getDescription()+"%"));
            }
            if(StringUtils.hasText(waterEventStatsBo.getFieldName())){
                predicates.add(builder.equal(root.get("fieldName"), waterEventStatsBo.getFieldName()));
            }
            if(Objects.nonNull(waterEventStatsBo.getStartDateTime())){
                predicates.add(builder.greaterThanOrEqualTo(root.get("startTime"), waterEventStatsBo.getStartDateTime()));
            }
            if(Objects.nonNull(waterEventStatsBo.getEndDateTime())){
                predicates.add(builder.lessThanOrEqualTo(root.get("startTime"), waterEventStatsBo.getEndDateTime()));
            }
            if (StringUtils.hasText(waterEventStatsBo.getEui())) {
                predicates.add(builder.equal(root.get("eui"), waterEventStatsBo.getEui()));
            }
            if(CollectionUtils.isEmpty(predicates)) {
                return null;
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return waterEventRepository.count(spec);
    }

    public WaterEventDto toDto(WaterEvent entity ){
        WaterEventDto dto = new WaterEventDto();
        BeanUtils.copyProperties(entity, dto);
        WaterDevice waterDevice = waterDeviceRepository.findByDeviceEUI(entity.getEui()).orElse(null);
        if(Objects.nonNull(waterDevice)){
            dto.setLabel(waterDevice.getLabel());
            dto.setDeviceNo(waterDevice.getDeviceNo());
        }
        return dto;
    }

}
