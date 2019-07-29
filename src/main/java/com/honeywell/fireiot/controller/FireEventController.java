package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.ExcelTitle;
import com.honeywell.fireiot.fire.bo.FireEventStatsBo;
import com.honeywell.fireiot.fire.entity.FireDailyData;
import com.honeywell.fireiot.fire.entity.FireEvent;
import com.honeywell.fireiot.fire.entity.FireEventCount;
import com.honeywell.fireiot.fire.repository.FireDailyDataRepository;
import com.honeywell.fireiot.fire.repository.FireEventRepository;
import com.honeywell.fireiot.fire.service.FireDailyDataService;
import com.honeywell.fireiot.fire.service.FireEventService;
import com.honeywell.fireiot.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * 消防系统接口
 *
 * @author:
 * @date:
 */
@Api(tags = "消防事件中心")
@RequestMapping("api/fireEvent")
@RestController
public class FireEventController {

    @Autowired
    FireEventService fireEventService;

    @Autowired
    FireDailyDataService fireDailyDataService;

    /**
     * 实时事件
     */
    @GetMapping("real")
    @JpaPage
    @ApiOperation(value = "获取实时事件列表", httpMethod = "GET")
    public ResponseObject getReal(){
        return ResponseObject.success(fireEventService.findPage(JpaUtils.getSpecification()));
    }


    /**
     * 事件导出
     */
    @GetMapping("excel")
    @JpaPage
    @ApiOperation(value = "事件列表导出", httpMethod = "GET")
    public ResponseObject getExcel(HttpServletResponse response) throws Exception {
        List<FireEvent> results = fireEventService.find(JpaUtils.getSpecification());
        if(results.size()==0){
            return ResponseObject.fail(ErrorEnum.EVENT_NOTEXIST);
        }
        String fileName = UUID.randomUUID() + ".xlsx";
        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(ExcelTitle.FIRE_EVENT_TITLE_LIST);
        List<List<Object>> rowData = new ArrayList<>();
        for(FireEvent fireEvent :results){
            List<Object> row = new ArrayList<>();
            row.add(fireEvent.getDeviceId());
            row.add(fireEvent.getDeviceLabel());
            row.add(fireEvent.getDeviceType());
            row.add(fireEvent.getBuilding());
            row.add(fireEvent.getFloor());
            row.add(fireEvent.getEventType());
            row.add(fireEvent.getEventStatus());
            row.add(fireEvent.getCreateDatetime());
            rowData.add(row);
        }
        data.setRows(rowData);
        ExcelUtil.exportExcel(response,data);
        return ResponseObject.success(null);
    }






    @GetMapping("count-m")
    @JpaPage
    @ApiOperation(value = "单个设备事件频次（次/月）统计", httpMethod = "GET")
    public ResponseObject countM(String deviceNo,String start,String end){
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        Long startTime=   c.getTime().getTime();
        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        Long endTime = ca.getTime().getTime();
        fireEventService.updateEventCount(deviceNo,startTime,endTime);

        List<FireEventCount> results = fireEventService.findCountByDeviceNo(deviceNo,start,end);
        return ResponseObject.success(results);
    }


    @GetMapping("count-y")
    @JpaPage
    @ApiOperation(value = "单个设备事件频次（次/年）统计", httpMethod = "GET")
    public ResponseObject countY(String deviceNo,String year){
        List<FireEventCount> results = fireEventService.findCountByDeviceNo(deviceNo,year);
        return ResponseObject.success(results);
    }


    /**
     * 获取周数据
     */
    @GetMapping("/weekend")
    @ApiOperation(value = "获取前七天数据", httpMethod = "GET")
    public ResponseObject getWeekend(){
        Map<Object,Object> map = new LinkedHashMap<>(7);
        List<FireDailyData> list = fireDailyDataService.findByStatsTypeAndStatsDateBetween(FireDailyDataRepository.STATS_TYPE_DAY,
                LocalDate.now().minusDays(7).toString(),LocalDate.now().minusDays(1).toString());
        list.forEach(fireDailyData ->
            map.put(fireDailyData.getStatsDate(),fireDailyData.getCountAll())
        );
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }

    /**
     * 获取月数据
     */
    @GetMapping("/month")
    @ApiOperation(value = "获取前30天数据", httpMethod = "GET")
    public ResponseObject getMonth(){
        Map<Object,Object> map = new LinkedHashMap<>(30);
        List<FireDailyData> list = fireDailyDataService.findByStatsTypeAndStatsDateBetween(FireDailyDataRepository.STATS_TYPE_DAY,
                LocalDate.now().minusDays(31).toString(),LocalDate.now().minusDays(1).toString());
        list.forEach(fireDailyData ->
                map.put(fireDailyData.getStatsDate(),fireDailyData.getCountAll())
        );
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }

    /**
     * 获取今天的数据
     */
    @GetMapping("/day")
    @ApiOperation(value = "获取今天数据", httpMethod = "GET")
    public ResponseObject getDay(){
        long start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Map<Object,Object> map = new HashMap<>(2);
        map.put(FireEventRepository.EVENT_TYPE_FIRE,fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,FireEventRepository.EVENT_TYPE_FIRE,FireEventRepository.EVENT_STATUS_ADD)));
        map.put(FireEventRepository.EVENT_TYPE_FAULT,fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,FireEventRepository.EVENT_TYPE_FAULT,FireEventRepository.EVENT_STATUS_ADD)));
        map.put(FireEventRepository.EVENT_TYPE_SHIELD,fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,FireEventRepository.EVENT_TYPE_SHIELD,FireEventRepository.EVENT_STATUS_ADD)));
        map.put(FireEventRepository.EVENT_TYPE_OTHER,fireEventService.countByFireEventStatsBo(new FireEventStatsBo(start,end,FireEventRepository.EVENT_TYPE_OTHER,FireEventRepository.EVENT_STATUS_ADD)));
        return ResponseObject.success(MapUtil.map2Listmap(map));
    }
}
