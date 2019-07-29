package com.honeywell.fireiot.water.service;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.RedisConstant;
import com.honeywell.fireiot.entity.WaterDevice;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.redis.RedisMap;
import com.honeywell.fireiot.redis.RedisMapFactory;
import com.honeywell.fireiot.service.WaterDeviceService;
import com.honeywell.fireiot.service.config.RedisResourceInitService;
import com.honeywell.fireiot.water.entity.WaterDailyData;
import com.honeywell.fireiot.water.entity.WaterData;
import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.repository.WaterDailyDataRepository;
import com.honeywell.fireiot.water.repository.WaterFieldRepository;
import org.apache.commons.collections4.list.TreeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WaterDailyDataService {

    @Autowired
    private WaterDataService waterDataService;
    @Autowired
    private WaterFieldRepository waterFieldRepository;
    @Autowired
    private WaterDailyDataRepository waterDailyDataRepository;
    @Autowired
    private WaterDeviceService waterDeviceService;

    private static RedisMap waterMap;
    private static RedisMapFactory redisFactory;

    @Autowired
    public void setRedisFactory(RedisMapFactory redisFactory) {
        WaterDailyDataService.redisFactory = redisFactory;
        WaterDailyDataService.waterMap = RedisMapFactory.getRedisMap(RedisConstant.HASH_KEY_WATER);
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FORMATTER2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.000");

    private static final Logger logger = LoggerFactory.getLogger(WaterDailyDataService.class);

    /**
     * 计算一个设备某天的平均值
     *
     * @param deviceNo
     * @param date
     * @return
     */
    public List<WaterDailyData> calculateDailyData(String deviceNo, LocalDate date) {

        if (date == null) {
            throw new BusinessException(ErrorEnum.DATE_NOT_NULL);
        }
        if (date.isAfter(LocalDate.now())) {
            // 日期超除当前时间时，抛出异常
            throw new BusinessException(ErrorEnum.DATE_OUT_RANGE);
        }

        List<WaterDailyData> result = new ArrayList<>();

        // 获取一天的开始和结束时间
        String start = LocalDateTime.of(date, LocalTime.MIN).format(FORMATTER);
        String end = LocalDateTime.of(date, LocalTime.MAX).format(FORMATTER);

        LocalDateTime currentDateTime = LocalDateTime.now();

        List<WaterField> waterFields = waterFieldRepository.findByDeviceNoAndCheckStatistics(deviceNo, 1);
        waterFields.forEach(waterField -> {

            // 获取一天的数据并计算平均值
            Map<String, Object> rangeMap = waterDataService.getByTimeRange(deviceNo, waterField.getName(), start, end);
            List<WaterData> datas = (List<WaterData>) rangeMap.get("datas");
            double avg = 0;
            if (datas.size() > 0) {
                for (WaterData data : datas) {
                    avg += Double.valueOf(data.getValue());
                }
                avg = avg / datas.size();
            }

            WaterDailyData waterDailyData = new WaterDailyData(
                    waterField.getEui(),
                    waterField.getName(),
                    // avg保留三位小数
                    DECIMAL_FORMAT.format(avg),
                    FORMATTER2.format(date),
                    currentDateTime
            );
            result.add(waterDailyData);
        });
        return result;
    }

    /**
     * 计算一个设备当天的平均值
     *
     * @param deviceNo
     * @return
     */
    public List<WaterDailyData> calculateDailyData(String deviceNo) {
        return calculateDailyData(deviceNo, LocalDate.now());
    }

    /**
     * 统计一天所有设备的平均值并保存到DB
     *
     * @param date
     */
    public void calculateDailyDataAndSave(LocalDate date) {
        List<WaterDevice> allDevice = waterDeviceService.findAll();
        // 删除date当天的旧数据，进行覆盖更新
        waterDailyDataRepository.deleteByTimestamp(FORMATTER2.format(date) + "%");
        allDevice.forEach(waterDevice -> {
            List<WaterDailyData> waterDailyDataList = calculateDailyData(waterDevice.getDeviceNo(), date);
            waterDailyDataList.forEach(data -> {
                waterDailyDataRepository.save(data);
//                logger.error("水系统设备数据统计异常！deviceEUI:{}，{}", waterDevice.getDeviceEUI(), e.getMessage());
            });
        });
    }

    /**
     * 保存
     *
     * @param dailyData
     */
    public void save(WaterDailyData dailyData) {
        waterDailyDataRepository.save(dailyData);
    }

    /**
     * 保存
     *
     * @param dailyDataList
     */
    public void save(List<WaterDailyData> dailyDataList) {
        dailyDataList.forEach(dailyData -> {
            waterDailyDataRepository.save(dailyData);
        });
    }


    /**
     * 查找一段日期内设备的平均值
     * @param startDate 开始时间
     * @param endDate  结束时间
     * @param euis  eui列表
     * @return
     */
    public Map<String, List<Map<String, Object>>> getDailyData(LocalDate startDate, LocalDate endDate, List<String> euis) {
        List<String> rangeDates = new ArrayList<>();

        LocalDate s = startDate;
        LocalDate e = endDate;
        while (s.isBefore(e) || s.isEqual(e)) {
            rangeDates.add(FORMATTER2.format(s));
            s = s.plusDays(1);
        }
        // 查询时间段内所有的Data，解析成特定格式：Map<eui+LocalDate, List<WaterDailyData>>
        List<WaterDailyData> dailyDatas;
        if (euis.size() == 0 || rangeDates.size() == 0) {
            dailyDatas = new ArrayList<>();
        } else {
            dailyDatas = waterDailyDataRepository.findByTimestampAndEui(rangeDates, euis);
        }

        Map<String, List<WaterDailyData>> groupedDailyData = new HashMap<>();

        dailyDatas.forEach(dailyData -> {
            String dataKey = dailyData.getEui() + dailyData.getTimestamp();

            List<WaterDailyData> timeLineDailyData;
            if (groupedDailyData.get(dataKey) == null) {
                timeLineDailyData = new TreeList<>();
                groupedDailyData.put(dataKey, timeLineDailyData);
            } else {
                timeLineDailyData = groupedDailyData.get(dataKey);
            }
            timeLineDailyData.add(dailyData);
        });

        // 取出所有waterFiled供之后使用，结构为：Map<deviceEui, List<WaterField>>
        Map<String, List<WaterField>> fieldMap = (Map<String, List<WaterField>>) waterMap.get(RedisConstant.HASH_KEY_WATER_FIELD);

        Map<String, List<Map<String, Object>>> rangeDateValuesWithEui = new HashMap<>();

        euis.forEach(eui -> {

            LocalDate start = startDate;
            LocalDate end = endDate;
            List<Map<String, Object>> rangeDateValues = new TreeList<>();

            // 循环获取近n天的数据
            while (start.isBefore(end) || start.isEqual(end)) {
                Map<String, Object> oneDayValues = new HashMap<>();

                List<WaterDailyData> timeLineDailyDataList = groupedDailyData.get(eui + FORMATTER2.format(start));
                if (eui == "6c95000010ffffff") {
                    System.out.println(timeLineDailyDataList);
                }
                if (timeLineDailyDataList == null) {
                    timeLineDailyDataList = Collections.emptyList();
                }

                Map<String, String> fieldValueMap = timeLineDailyDataList
                        .stream()
                        .collect(Collectors.toMap(
                                es -> es.getFieldName(),
                                es -> es.getValue())
                        );

                // 循环获取单个设备所有的值
                List<Map<String, Object>> valuesOfOneDevice = new ArrayList<>();
                for (WaterField field : fieldMap.get(eui)) {

                    Map<String, Object> valueMap = new HashMap<>();
                    valueMap.put("unit", field.getUnitSymbol());
                    valueMap.put("title", field.getTitle());
                    valueMap.put("value", fieldValueMap.get(field.getName()) != null ? fieldValueMap.get(field.getName()) : "0.000");
                    valueMap.put("max", field.getMax());
                    valueMap.put("min", field.getMin());
                    valuesOfOneDevice.add(valueMap);

                }
                oneDayValues.put("date", LocalDateTime.of(start, LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                oneDayValues.put("values", valuesOfOneDevice);
                rangeDateValues.add(oneDayValues);

                start = start.plusDays(1);
            }
            rangeDateValuesWithEui.put(eui, rangeDateValues);
        });

        return rangeDateValuesWithEui;
    }
}
