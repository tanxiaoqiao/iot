package com.honeywell.fireiot.water.controller;

import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.ExcelTitle;
import com.honeywell.fireiot.dto.waterdevice.WaterDataDto;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.service.FileParseService;
import com.honeywell.fireiot.utils.*;
import com.honeywell.fireiot.water.entity.WaterData;
import com.honeywell.fireiot.water.entity.WaterEvent;
import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.sensor.constant.WaterSensorEnum;
import com.honeywell.fireiot.water.service.WaterDataService;
import com.honeywell.fireiot.water.service.WaterEventService;
import com.honeywell.fireiot.water.service.WaterFieldService;
import com.honeywell.fireiot.water.service.WaterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.honeywell.fireiot.utils.ResponseObject.success;
import static java.util.Objects.isNull;

/**
 * 水系统接口
 *
 * @author: xiaomingCao
 * @date: 2018/12/27
 */
@Api(value = "水系统", tags = {"水系统"})
@RequestMapping("water/api/v1")
@RestController
@Slf4j
public class WaterRestController {

    private WaterFieldService waterFieldService;

    private WaterDataService waterDataService;

    private WaterService waterService;

    private WaterEventService waterEventService;

    @Autowired
    FileParseService fileParseService;

    @Autowired
    public WaterRestController(WaterFieldService waterFieldService,
                               WaterDataService waterDataService,
                               WaterService waterService,
                               WaterEventService waterEventService) {
        this.waterFieldService = waterFieldService;
        this.waterDataService = waterDataService;
        this.waterService = waterService;
        this.waterEventService = waterEventService;
    }

    @PostMapping("device")
    @ApiOperation(value = "excel导入")
    public ResponseObject batchCreate(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "temperature_humidity_sensor_cname", defaultValue = "温湿度") String eName1,
            @RequestParam(value = "water_immersion_sensor_cname", defaultValue = "水浸检测") String eName2,
            @RequestParam(value = "pressure_sensor_cname", defaultValue = "喷淋水压力,喷淋压力,喷淋压力,室内栓压力,室外栓压力") String eName3,
            @RequestParam(value = "liquid_level_sensor_cname", defaultValue = "水池水位,天面水池水位,细水雾水箱水位") String eName4,
            @RequestParam(value = "flow_meter", defaultValue = "1#喷淋泵流量,2#喷淋泵流量,1#室内栓流量,2#室内栓流量,1#室外栓流量,2#室外栓流量")String eName5,
            @RequestParam(value = "battery_check", defaultValue = "柴油泵电池监测,发电机电池监测") String eName6,
            @RequestParam(value = "colsize", defaultValue = "10") int colsize,
            @RequestParam(value = "startrow", defaultValue = "3") int startRow
            ) throws IOException {
        Map<String, String> mapper = new HashMap<>(32);

        Arrays.stream(eName1.split(","))
                .forEach(ename -> mapper.put(ename, WaterSensorEnum.WATER_SYS_TEMPERATURE_HUMIDITY.name()));

        Arrays.stream(eName2.split(","))
                .forEach(ename -> mapper.put(ename, WaterSensorEnum.WATER_SYS_WATER_IMMERSION.name()));

        Arrays.stream(eName3.split(","))
                .forEach(ename -> mapper.put(ename, WaterSensorEnum.WATER_SYS_PRESSURE_COLLECT.name()));

        Arrays.stream(eName4.split(","))
                .forEach(ename -> mapper.put(ename, WaterSensorEnum.WATER_SYS_LIQUID_LEVEL.name()));

        Arrays.stream(eName5.split(","))
                .forEach(ename -> mapper.put(ename, WaterSensorEnum.WATER_SYS_FLOWMETER.name()));

        Arrays.stream(eName6.split(","))
                .forEach(ename -> mapper.put(ename, WaterSensorEnum.WATER_SYS_BATTERY.name()));

        waterService.batchCreate(file, mapper, colsize, startRow);
        return success(null);
    }


    /**
     * 创建字段
     *
     * @param field
     * @param deviceNo
     */
    @ApiOperation(value = "创建Field")
    @PostMapping("device/{deviceNo}/field")
    public ResponseObject save(@RequestBody WaterField field, @PathVariable String deviceNo){
        field.setDeviceNo(deviceNo);
        WaterField wf = waterFieldService.save(field);
        return success(wf);
    }

    /**
     * 修改字段信息
     *
     * @param field
     * @param deviceNo
     * @param fieldId
     * @return
     */
    @ApiOperation(value = "修改Field", notes = "可修改高限低限")
    @PutMapping("device/{deviceNo}/field/{fieldId}")
    public ResponseObject updateMaxMin(@RequestBody WaterField field,
                                       @PathVariable("deviceNo") String deviceNo,
                                       @PathVariable("fieldId") Long fieldId){
        field.setDeviceNo(deviceNo);
        field.setId(fieldId);
        if(isNull(fieldId) || isNull(deviceNo)){
            throw new BusinessException(ErrorEnum.PARAMETER_ERROR);
        }
        return success(waterFieldService.save(field));
    }


    /**
     * 删除字段
     *
     * @param deviceNo
     * @param id
     */
    @ApiOperation("删除Field")
    @DeleteMapping("device/{deviceNo}/field/{id}")
    public ResponseObject delete(@PathVariable("deviceNo") String deviceNo,
                       @PathVariable("id") Long id){
        waterFieldService.delete(deviceNo, id);
        return success(null);
    }


    /**
     * 获取设备下所有字段
     *
     * @param deviceNo
     * @return
     */
    @ApiOperation("Field列表")
    @GetMapping("device/{deviceNo}/field/all")
    public ResponseObject getFields(@PathVariable String deviceNo){
        List<WaterField> datas = waterFieldService.findByDeviceNo(deviceNo);
        return success(datas);
    }


    /**
     * 查询历史数据
     *
     * @param deviceNo
     * @param fieldName
     * @param start
     * @param end
     * @return
     */
    @ApiOperation(value = "查询历史数据", notes = "时间格式为 yyyy-MM-dd HH:mm")
    @GetMapping("device/{deviceNo}/field/{fieldName}/data/history")
    public ResponseObject getDataHistory(
            @PathVariable("deviceNo") String deviceNo,
            @PathVariable("fieldName") String fieldName,
            @RequestParam(value = "startTime", required = true) String start,
            @RequestParam(value = "endTime", required = true) String end ){
        Map<String, Object> datas = waterDataService.getByTimeRange(deviceNo, fieldName, start, end);
        return success(datas);
    }


    /**
     * 查询事件历史数据
     *
     * @param deviceNo
     * @param fieldName
     * @param start
     * @param end
     * @return
     */
    @ApiOperation(value = "查询事件历史数据", notes = "时间格式为 yyyy-MM-dd HH:mm")
    @GetMapping("device/{deviceNo}/field/{fieldName}/event/history")
    public ResponseObject getEventHistory(
            @PathVariable("deviceNo") String deviceNo,
            @PathVariable("fieldName") String fieldName,
            @RequestParam(value = "startTime", required = true) String start,
            @RequestParam(value = "endTime", required = true) String end
    ){
        Map<String, Object> data = waterEventService.getByTimeRange(deviceNo, fieldName, start, end);
        return success(data);
    }


    /**
     * 查询最新历史数据
     *
     * @param deviceNo
     * @param fieldName
     * @return
     */
    @ApiOperation(value = "查询最新历史数据")
    @GetMapping("device/{deviceNo}/field/{fieldName}/data/top")
    public ResponseObject getTopData( @PathVariable("deviceNo") String deviceNo,
                                  @PathVariable("fieldName") String fieldName){
        WaterDataDto top = waterDataService.getTop(deviceNo, fieldName);
        return success(top);
    }

    /**
     * 查询最新时间数据
     *
     * @param deviceNo
     * @param fieldName
     * @return
     */
    @ApiOperation(value = "查询最新时间数据")
    @GetMapping("device/{deviceNo}/field/{fieldName}/event/top")
    public ResponseObject getTopAlarm( @PathVariable("deviceNo") String deviceNo,
                                        @PathVariable("fieldName") String fieldName
    ){
        WaterEvent top = waterEventService.getTop(deviceNo, fieldName);
        return success(top);
    }

    /**
     * 分页查询
     *
     * @param start
     * @param end
     * @param pageSize
     * @param page
     * @param sortField
     * @param sortType
     * @param deviceNo
     * @param fieldName
     * @return
     */
    @ApiOperation(value = "历史数据分页列表")
    @GetMapping("device/data/page")
    public ResponseObject getDataPage(
            @RequestParam(value = "startTime", required = false) String start,
            @RequestParam(value = "endTime", required = false) String end,
            @RequestParam(value = "ps", defaultValue = "20") int pageSize,
            @RequestParam(value = "pi", defaultValue = "1") int page,
            @RequestParam(value = "sortField", defaultValue = "startTime") String sortField,
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
            @RequestParam(value = "deviceNo", required = false) String deviceNo,
            @RequestParam(value = "fieldName", required = false) String fieldName
    ){
        Page<WaterData> p = waterDataService
                .getPage(page, pageSize, sortField, sortType, deviceNo, fieldName, start, end);
        Pagination<WaterData> pagination = new Pagination<>((int) p.getTotalElements(), p.getContent());
        return success(pagination);
    }

    @GetMapping("/device/ht/json")
    public  Map<String, List<Map<String, String>>> getHtJson(){
        return waterService.getHtJson();
    }













//---------- 水系统设备事件-----------------


    /**
     * 分页查询
     * @return
     */
    @JpaPage
    @ApiOperation(value = "水系统设备事件列表")
    @GetMapping("device/event/page")
    public ResponseObject getEventPage(){
        return success(waterEventService.findPage(JpaUtils.getSpecification()));
    }

    /**
     * EXCEL导出
     * @return
     */
    @JpaPage
    @ApiOperation(value = "水系统设备事件导出")
    @GetMapping("device/event/excel")
    public ResponseObject excel(HttpServletResponse response)throws Exception{
        List<WaterEvent> results = waterEventService.find(JpaUtils.getSpecification());
        String fileName = UUID.randomUUID() + ".xlsx";
        if(results.size()==0){
            return ResponseObject.fail(ErrorEnum.EVENT_NOTEXIST);
        }
        //构造excelData
        ExcelData data = new ExcelData();
        data.setFileName(fileName);
        data.setTitle(ExcelTitle.WATER_EVENT_TITLE_LIST);
        List<List<Object>> rowData = new ArrayList<>();
        for(WaterEvent waterEvent :results){
            List<Object> row = new ArrayList<>();
            row.add(waterEvent.getEui());
            row.add(waterEvent.getDeviceLabel());
            row.add(waterEvent.getFieldName());
            row.add(waterEvent.getMax());
            row.add(waterEvent.getMin());
            row.add(waterEvent.getValue());
            row.add(waterEvent.getUnit());
            row.add(waterEvent.getType());
            row.add(waterEvent.getStartTime());
            rowData.add(row);
        }
        data.setRows(rowData);
        ExcelUtil.exportExcel(response,data);
        return success(null);
    }

    @PostMapping("device/delete")
    @ApiOperation(value = "excel删除设备")
    public ResponseObject batchCreate( @RequestParam("file") MultipartFile file){
        ReadFileConditon readFileConditon = new ReadFileConditon(1,1);
        ErrorEnum errorEnum = fileParseService.deleteWaterDevice(file,readFileConditon);
        if(errorEnum != null){
            return ResponseObject.fail(errorEnum);
        }else{
            return ResponseObject.success(null);
        }
    }



}
