package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.SpotDto;
import com.honeywell.fireiot.dto.SpotTaskSearch;
import com.honeywell.fireiot.dto.SpotTaskShow;
import com.honeywell.fireiot.entity.Spot;
import com.honeywell.fireiot.service.SpotService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api/spot")
@Api(tags = "点位")
public class SpotController {
    @Autowired
    private SpotService spotService;

    @PostMapping("/save")
    @ApiOperation(value = "保存点位", httpMethod = "POST")
    public ResponseObject save(@RequestBody @ApiParam SpotDto spotDto) {
        long id =  spotService.save(spotDto);
        return ResponseObject.success(id);
    }
    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除点位")
    public ResponseObject delete(@PathVariable("id") long id){
        if (null == spotService.getSpotById(id)) {
            return ResponseObject.fail(ErrorEnum.DATA_NOT_EXIST);
        }
        spotService.delete(id);
        return ResponseObject.success(null);
    }
    @GetMapping("/queryByPage")
    @JpaPage
    @ApiOperation(value ="分页查询点位")
    public ResponseObject queryByPage(){
        Pagination<Spot> pagination  = spotService.findPage(JpaUtils.getSpecification());
        return ResponseObject.success(pagination);
    }
    @GetMapping("/query/{id}")
    @ApiOperation(value = "查询所有点位或根据id查询")
    public ResponseObject getAll(@PathVariable("id") long id){
        List<SpotDto> list = new ArrayList<>();
        if (id == 0){
            list = spotService.findAll();
        }else {
            Spot spot = spotService.getSpotById(id);
            SpotDto spotDto = new SpotDto();
            BeanUtils.copyProperties(spot,spotDto);
            list.add(spotDto);
        }
        return ResponseObject.success(list);
    }
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "点位详情")
    public ResponseObject getDetail(@PathVariable("id") long id) {
        Spot spot = spotService.getSpotById(id);
        return ResponseObject.success(spot);
    }
    @GetMapping("/exportQrImage")
    @ApiOperation(value = "导出二维码")
    public ResponseObject exportQrImage(){

        return ResponseObject.success(null);

    }

    /**
     * 点位与工作任务关联，分页查询
     * @param spotTaskSearch
     * @return
     */
    @PostMapping("/associatedQuery")
    @ApiOperation(value = "点位工作任务,供巡检计划页面添加点位任务")
    @JpaPage
    public ResponseObject query(@RequestBody SpotTaskSearch spotTaskSearch){
        List<SpotTaskShow> list =  spotService.findSpotContents(spotTaskSearch);
        return ResponseObject.success(list);
    }
    /**
     *
     */
    @PostMapping("/PageQuery")
    @ApiOperation(value = "点位工作任务分页查询,供巡检计划页面添加点位任务")
    @JpaPage
    public ResponseObject queryByPage(@RequestBody SpotTaskSearch spotTaskSearch,@RequestParam("pi") int pi,@RequestParam("ps") int ps){
        Pageable pageable = PageRequest.of(pi,ps);
        List<SpotTaskShow> list =  spotService.findSpotContentsByPage(spotTaskSearch,pageable);
        return ResponseObject.success(list);
    }

    @PostMapping("/spotTask")
    @ApiOperation(value ="点位工作任务分页查询,供巡检计划页面添加点位任务")
    public ResponseObject querySpotTask(@RequestBody SpotTaskSearch spotTaskSearch) {
        Pageable pageable = PageRequest.of(spotTaskSearch.getPi(),spotTaskSearch.getPs());

        return null;
    }

}

