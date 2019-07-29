package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.ExcelTitle;
import com.honeywell.fireiot.dto.waterdevice.*;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.fire.repository.FireDailyDataRepository;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.DeviceTypeService;
import com.honeywell.fireiot.service.SystemTypeService;
import com.honeywell.fireiot.service.WaterDeviceService;
import com.honeywell.fireiot.utils.*;
import com.honeywell.fireiot.water.WaterEventStatsBo;
import com.honeywell.fireiot.water.entity.WaterDailyEvent;
import com.honeywell.fireiot.water.repository.WaterDailyEventRepository;
import com.honeywell.fireiot.water.service.WaterDailyDataService;
import com.honeywell.fireiot.water.service.WaterDailyEventService;
import com.honeywell.fireiot.water.service.WaterEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.honeywell.fireiot.utils.ResponseObject.success;

/**
 * 水系统设备接口
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/21 1:54 PM
 */
@RestController
@Api(tags = "水系统设备")
@Slf4j
public class WaterDeviceController {
    @Autowired
    WaterDeviceService waterDeviceService;
    @Autowired
    SystemTypeService systemTypeService;
    @Autowired
    DeviceTypeService deviceTypeService;
    @Autowired
    BusinessDeviceService businessDeviceService;
    @Autowired
    WaterDailyDataService wddService;
    @Autowired
    WaterDailyEventService waterDailyEventService;

    @Autowired
    WaterEventService waterEventService;

    @GetMapping("/api/waterDevice")
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject findPage2Dto() {
        return success(waterDeviceService.findPage2Dto());
    }

    @GetMapping("/api/waterDevice/excel")
    @JpaPage
    @ApiOperation(value = "导出Excel")
    public ResponseObject exportExcel(HttpServletResponse response) throws Exception {
        List<WaterDeviceDto> results = waterDeviceService.find(JpaUtils.getSpecification());
        String fileName = UUID.randomUUID() + ".xlsx";
        if (results.size() == 0) {
            return ResponseObject.fail(ErrorEnum.DEVICE_NOT_EXIST);
        }
        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(ExcelTitle.WATER_TITLE_LIST);
        List<List<Object>> rowData = new ArrayList<>();
        for (WaterDeviceDto waterDeviceDto : results) {
            List<Object> row = new ArrayList<>();
            row.add(waterDeviceDto.getDeviceEUI());
            row.add(waterDeviceDto.getDeviceLabel());
            row.add(waterDeviceDto.getDeviceType());
            row.add(waterDeviceDto.getStatus());
            row.add(waterDeviceDto.getLocationName());
            rowData.add(row);
        }
        data.setRows(rowData);
        ExcelUtil.exportExcel(response, data);
        return success(null);
    }


//    @ApiOperation(value = "通过ID查找")
//    @GetMapping("/api/waterDevice/{id}")
//    public ResponseObject findOne(@PathVariable("id") Long id) {
//        Optional<WaterDevice> entity = waterDeviceService.findById(id);
//        if (entity.isPresent()) {
//            return ResponseObject.success(waterDeviceService.toDto(entity.get()));
//        }
//        return ResponseObject.fail(ErrorEnum.NOT_FOUND);
//    }

    @ApiOperation(value = "查询水系统设备数量")
    @PostMapping("/api/waterDevice/count")
    public ResponseObject findDeviceCount(@RequestBody WaterDeviceSearch[] searchList) {
        List<WaterDeviceInfoDto> infoDtoList = new ArrayList<>();
        // 循环查询所有的数量
        for (WaterDeviceSearch search : searchList) {
            WaterDeviceInfoDto infoDto = new WaterDeviceInfoDto();
            List<ValueDto> values = waterDeviceService.findWaterDeviceCount(search);
            infoDto.setValues(values);
            infoDto.setSearch(search);
            infoDtoList.add(infoDto);
        }
        return success(infoDtoList);
    }

    @ApiOperation(value = "查询水系统设备信息")
    @PostMapping("/api/waterDevice/info")
    public ResponseObject findDeviceInfo(@RequestBody String[] deviceIdList) {
        List responseList = new ArrayList<>();
        for (String deviceId : deviceIdList) {
            WaterDeviceInfoDto infoDto = new WaterDeviceInfoDto();

            Map resultMap = new HashMap();
            WaterDeviceDetailInfoDto waterDeviceDetailInfoDto = waterDeviceService.findDeviceDetailByDeviceNo(deviceId);
            // 封装search条件
            Map searchMap = new HashMap();
            searchMap.put("deviceId", deviceId);
            infoDto.setSearch(searchMap);

            // 设置values
            List valueList = new ArrayList<>();
            valueList.add(waterDeviceDetailInfoDto);
            infoDto.setValues(valueList);

            resultMap.put("search", searchMap);
            resultMap.put("values", valueList);
            responseList.add(resultMap);
        }
        return success(responseList);
    }

    @ApiOperation(value = "查询一栋楼下的水系统设备信息")
    @PostMapping("/api/waterDevice")
    public ResponseObject findDevice(@RequestBody WaterDeviceSearch[] searchList) {
        List<WaterDeviceInfoDto> infoDtoList = new ArrayList<>();
        // 循环查询所有的数量
        for (WaterDeviceSearch search : searchList) {
            WaterDeviceInfoDto infoDto = new WaterDeviceInfoDto();
            List<WaterDeviceDto> values = waterDeviceService.findWaterDeviceWithValue(search);
            infoDto.setValues(values);
            infoDto.setSearch(search);
            infoDtoList.add(infoDto);
        }
        return success(infoDtoList);
    }

    @ApiOperation(value = "查询最近一周的数据")
    @PostMapping("/api/waterDevice/week")
    public ResponseObject findWeekData(@RequestBody WaterDeviceSearch[] searchList) {
        List<WaterDeviceInfoDto> infoDtoList = new ArrayList<>();
        // 循环查询所有的数量
        for (WaterDeviceSearch search : searchList) {
            WaterDeviceInfoDto infoDto = new WaterDeviceInfoDto();
            List<WaterDeviceDto> values = waterDeviceService.findWaterDeviceWithWeekValue(search);
            infoDto.setValues(values);
            infoDto.setSearch(search);
            infoDtoList.add(infoDto);
        }
        return success(infoDtoList);
    }

    @ApiOperation(value = "查询最近一月的数据")
    @PostMapping("/api/waterDevice/month")
    public ResponseObject findMonthData(@RequestBody WaterDeviceSearch[] searchList) {
        List<WaterDeviceInfoDto> infoDtoList = new ArrayList<>();
        // 循环查询所有的数量
        for (WaterDeviceSearch search : searchList) {
            WaterDeviceInfoDto infoDto = new WaterDeviceInfoDto();
            List<WaterDeviceDto> values = waterDeviceService.findWaterDeviceWithMonthValue(search);
            infoDto.setValues(values);
            infoDto.setSearch(search);
            infoDtoList.add(infoDto);
        }
        return success(infoDtoList);
    }

    @ApiOperation(value = "计算某一天的设备数据")
    @PostMapping("/api/waterDevice/dailyData")
    public ResponseObject generateDailyData(@RequestParam(name = "date", required = true)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            throw new BusinessException(ErrorEnum.DATE_NOT_NULL);
        }
        if (LocalDate.now().isBefore(date)) {
            // 日期超除当前时间时，抛出异常
            throw new BusinessException(ErrorEnum.DATE_OUT_RANGE);
        }

        log.info("====== 水系统日数据统计开始：{}", date);
        Thread thread = new Thread(() -> {
            wddService.calculateDailyDataAndSave(date);
            log.info("====== 水系统日数据统计完成，生成 {} 数据。", date);
        });
        thread.start();
        return success("数据统计已开始");
    }

    @ApiOperation(value = "计算最近一个月的数据")
    @PostMapping("/api/waterDevice/monthData")
    public ResponseObject generateDailyData() {

        LocalDate date = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            LocalDate d = date.minusDays(i);
            log.info("====== 水系统日数据统计开始：{}", d);
            Thread thread = new Thread(() -> {
                wddService.calculateDailyDataAndSave(d);
                log.info("====== 水系统日数据统计完成，生成 {} 数据。", d);
            });
            thread.start();
        }
        return success("数据统计已开始");
    }

    @ApiOperation(value = "查询当天的数据")
    @PostMapping("/api/waterDevice/day")
    public ResponseObject findDayData(@RequestBody WaterDeviceSearch[] searchList) {
        List<WaterDeviceInfoDto> infoDtoList = new ArrayList<>();
        // 循环查询所有的数量
        for (WaterDeviceSearch search : searchList) {
            WaterDeviceInfoDto infoDto = new WaterDeviceInfoDto();
            List<WaterDeviceDto> values = waterDeviceService.findWaterDeviceWithDayValue(search);
            infoDto.setValues(values);
            infoDto.setSearch(search);
            infoDtoList.add(infoDto);
        }
        return success(infoDtoList);
    }

    @ApiOperation(value = "查询某个设备某段时间内的数据")
    @PostMapping("/api/waterDevice/rangeDate")
    public ResponseObject findDayData(@RequestBody @Validated WaterValueRangeDateSearch dateSearch) {
        LocalDateTime startDateTime = LocalDateTime.of(dateSearch.getStartDatetime(), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(dateSearch.getStartDatetime(), LocalTime.MAX);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Map<String, Object>> values = waterDeviceService.findWaterDeviceWithDayValue(dateSearch.getDeviceNo(), startDateTime.format(formatter), endDateTime.format(formatter));
        return success(values);
    }


//------------------ 第二版---------------------

    @GetMapping("/api/waterDevice/count/status")
    @ApiOperation(value = "设备状态统计")
    public ResponseObject countDeviceStatusForHome() {
        return ResponseObject.success(waterDeviceService.countDeviceByStatus());
    }

    @GetMapping("/api/waterDevice/count/status/location")
    @ApiOperation(value = "设备状态统计,根据location分类统计")
    public ResponseObject countDeviceStatusGroupByLocation(@RequestBody WaterDeviceSearch search) {
        return ResponseObject.success(waterDeviceService.countDeviceByStatusAndLocation(search));
    }

    @GetMapping("/api/waterDevice/count/deviceType")
    @ApiOperation(value = "设备类型统计")
    public ResponseObject countDeviceForHome() {
        return ResponseObject.success(waterDeviceService.countDeviceByDeviceType());
    }

    /**
     * 获取周数据
     */
    @GetMapping("/api/waterEvent/weekend")
    @ApiOperation(value = "获取前七天数据", httpMethod = "GET")
    public ResponseObject getWeekend() {
        Map<Object, Object> map = new LinkedHashMap<>(7);
        List<WaterDailyEvent> list = waterDailyEventService.findByStatsTypeAndStatsDateBetween(WaterDailyEventRepository.STATS_TYPE_DAY,
                LocalDate.now().minusDays(7).toString(), LocalDate.now().minusDays(1).toString());
        list.forEach(waterDailyEvent -> {
                    long exceptionTotalCount = 0;
                    if (map.get(waterDailyEvent.getStatsDate()) != null) {
                        exceptionTotalCount = (long) map.get(waterDailyEvent.getStatsDate());
                    }
                    map.put(waterDailyEvent.getStatsDate(),
                            exceptionTotalCount + waterDailyEvent.getHighException() + waterDailyEvent.getLowException());
                }
        );
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }

    /**
     * 获取单个设备周数据
     */
    @GetMapping("/api/waterEvent/weekend/eui/{eui}")
    @ApiOperation(value = "获取前七天数据", httpMethod = "GET")
    public ResponseObject getWeekendByEui(@PathVariable("eui") String eui) {
        Map<Object, Object> map = new LinkedHashMap<>(7);
        List<WaterDailyEvent> list = waterDailyEventService.findByStatsTypeAndEuiAndStatsDateBetween(
                WaterDailyEventRepository.STATS_TYPE_DAY,
                eui,
                LocalDate.now().minusDays(7).toString(),
                LocalDate.now().minusDays(1).toString()
        );
        list.forEach(waterDailyEvent -> {
            long exceptionTotalCount = 0;
            if (map.get(waterDailyEvent.getStatsDate()) != null) {
                exceptionTotalCount = (long) map.get(waterDailyEvent.getStatsDate());
            }
            map.put(waterDailyEvent.getStatsDate(),
                    exceptionTotalCount + waterDailyEvent.getHighException() + waterDailyEvent.getLowException());
        });
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }

    /**
     * 获取月数据
     */
    @GetMapping("/api/waterEvent/month")
    @ApiOperation(value = "获取前30天数据", httpMethod = "GET")
    public ResponseObject getMonth() {
        Map<Object, Object> map = new LinkedHashMap<>(31);
        List<WaterDailyEvent> list = waterDailyEventService.findByStatsTypeAndStatsDateBetween(FireDailyDataRepository.STATS_TYPE_DAY,
                LocalDate.now().minusDays(31).toString(), LocalDate.now().minusDays(1).toString());
        list.forEach(waterDailyEvent -> {
                    long exceptionTotalCount = 0;
                    if (map.get(waterDailyEvent.getStatsDate()) != null) {
                        exceptionTotalCount = (long) map.get(waterDailyEvent.getStatsDate());
                    }
                    map.put(waterDailyEvent.getStatsDate(),
                            exceptionTotalCount + waterDailyEvent.getHighException() + waterDailyEvent.getLowException());
                }
        );
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }

    /**
     * 获取单个设备月数据
     */
    @GetMapping("/api/waterEvent/month/eui/{eui}")
    @ApiOperation(value = "获取前30天数据", httpMethod = "GET")
    public ResponseObject getMonthByEid(@PathVariable("eui") String eui) {
        Map<Object, Object> map = new LinkedHashMap<>(31);
        List<WaterDailyEvent> list = waterDailyEventService.findByStatsTypeAndEuiAndStatsDateBetween(
                FireDailyDataRepository.STATS_TYPE_DAY,
                eui,
                LocalDate.now().minusDays(31).toString(),
                LocalDate.now().minusDays(1).toString()
        );
        list.forEach(waterDailyEvent -> {
                    long exceptionTotalCount = 0;
                    if (map.get(waterDailyEvent.getStatsDate()) != null) {
                        exceptionTotalCount = (long) map.get(waterDailyEvent.getStatsDate());
                    }
                    map.put(waterDailyEvent.getStatsDate(),
                            exceptionTotalCount + waterDailyEvent.getHighException() + waterDailyEvent.getLowException());
                }
        );
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }

    /**
     * 获取某月数据
     */
    @GetMapping("/api/waterEvent/month/{month}")
    @ApiOperation(value = "获取某个月的数据", httpMethod = "GET")
    public ResponseObject getSomeMonth(@PathVariable("month") String month) {
        LocalDate localDate = LocalDate.parse(month + "-01");
        Map<Object, Object> map = new LinkedHashMap<>(31);
        List<WaterDailyEvent> list = waterDailyEventService.findByStatsTypeAndStatsDateBetween(FireDailyDataRepository.STATS_TYPE_DAY,
                localDate.with(TemporalAdjusters.firstDayOfMonth()).toString(), localDate.with(TemporalAdjusters.lastDayOfMonth()).toString());
        list.forEach(waterDailyEvent -> {
                    long exceptionTotalCount = 0;
                    if (map.get(waterDailyEvent.getStatsDate()) != null) {
                        exceptionTotalCount = (long) map.get(waterDailyEvent.getStatsDate());
                    }
                    map.put(waterDailyEvent.getStatsDate(),
                            exceptionTotalCount + waterDailyEvent.getHighException() + waterDailyEvent.getLowException());
                }

        );
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }

    /**
     * 获取今天的数据
     */
    @GetMapping("/api/waterEvent/day")
    @ApiOperation(value = "获取今天数据", httpMethod = "GET")
    public ResponseObject getDay() {
        long start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Map<Object, Object> map = new HashMap<>(2);
        map.put(WaterDailyEventRepository.EVENT_PRESSURE, waterEventService.countByWaterEventStatsBo(new WaterEventStatsBo(start, end, null, WaterDailyEventRepository.EVENT_PRESSURE, null)));
        map.put(WaterDailyEventRepository.EVENT_LIQUID_LEVEL, waterEventService.countByWaterEventStatsBo(new WaterEventStatsBo(start, end, null, WaterDailyEventRepository.EVENT_LIQUID_LEVEL, null)));
        map.put(WaterDailyEventRepository.EVENT_ELECTRIC_PRESSURE_ALARM, waterEventService.countByWaterEventStatsBo(new WaterEventStatsBo(start, end, null, WaterDailyEventRepository.EVENT_ELECTRIC_PRESSURE_ALARM, null)));
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }
}
