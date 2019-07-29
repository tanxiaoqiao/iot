package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.LocationDto;
import com.honeywell.fireiot.dto.LocationMapTree;
import com.honeywell.fireiot.dto.LocationTree;
import com.honeywell.fireiot.entity.Location;
import com.honeywell.fireiot.entity.LocationMap;
import com.honeywell.fireiot.service.LocationMapService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @ClassName LocationController
 * @Description TODO
 * @Author monica Z
 * @Date 12/4/2018 1:19 PM
 **/
@RestController
@RequestMapping("/api/location")
@Api(value = "空间位置controller", tags = {"空间管理"})
public class LocationController {
    @Autowired
    LocationService locationService;

    @Autowired
    LocationMapService locationMapService;

    @Value("${location.map.upload}")
    private String mapLocationUpload;

    @Value("${location.map.down}")
    private String mapLocationDown;

    @PostMapping(value = "/create")
    @ApiOperation(value = "新增空间")
    public ResponseObject create(@RequestBody @ApiParam(name = "location 对象", value = "传入json") LocationDto locationDto) {
        try{
             locationService.create(locationDto);
             return ResponseObject.success(null);
        }catch(Exception e) {
            return ResponseObject.fail(null);
//            return ResponseObject.error(ErrorEnum.FAIL.getCode(),e.getMessage());
        }
    }
    @PostMapping(value = "/update")
    @ApiOperation(value = "更新空间")
    public ResponseObject update(@RequestBody @ApiParam(name = "location 对象", value = "传入json") LocationDto locationDto){

        try{
            locationService.update(locationDto);

            return ResponseObject.success(null);
        }catch(Exception e){
            return ResponseObject.fail(null);
//            return ResponseObject.error(ErrorEnum.FAIL.getCode(),e.getMessage());
        }
    }
    @GetMapping(value = "/delete/{id}")
    @ApiOperation(value = "删除空间")
    public ResponseObject delete(@ApiParam(name = "id",value = "空间id", required = true) @PathVariable("id") long id){

        try{
            locationService.delete(id);
            return ResponseObject.success(null);
//            return ResponseObject.success(ErrorEnum.SUCCESS);
        }catch(Exception e){
            return ResponseObject.fail(null);
//            return ResponseObject.error(ErrorEnum.FAIL.getCode(),e.getMessage());
        }
    }
    @GetMapping(value = "/detail/{id}")
    @ApiOperation(value = "空间位置详情")
    public ResponseObject getDetail(@ApiParam(name = "id",value = "空间id", required = true) @PathVariable("id") long id){
        try{
             Location existNode = locationService.getDetail(id);
             return ResponseObject.success(existNode);
        }catch (Exception e){
            return ResponseObject.fail(null);
//            return ResponseObject.error(ErrorEnum.FAIL.getCode(),e.getMessage());
        }
    }
    @GetMapping(value = "/children/{parentId}")
    @ApiOperation(value = "空间位置下属包含的子节点")
    public ResponseObject getChildren(@ApiParam(name = "parentId",value = "空间父id", required = true) @PathVariable("parentId") long parentId){
        try {
            List<Location> childrenList  = locationService.getChildren(parentId);
            return ResponseObject.success(childrenList);
        }catch(Exception e){
            return ResponseObject.fail(null);
//            return ResponseObject.error(ErrorEnum.FAIL.getCode(),e.getMessage());
        }
    }
    @GetMapping(value="/allChildren/{parentId}")
    @ApiOperation(value = "以该节点为根节点遍历所得节点")
    public ResponseObject getAllChildren(@ApiParam(name = "parentId",value = "空间父id", required = true) @PathVariable("parentId") long parentId){
       try{
           List<Long> childrenIds = new ArrayList<>();
           childrenIds = locationService.getAllChildren(parentId,childrenIds);
           return ResponseObject.success(childrenIds);
       }catch (Exception e){
           return ResponseObject.fail(null);
       }

    }
    @PostMapping(value="/upload")
    @ApiOperation(value = "上传excel 批量导入location数据")
    public ResponseObject uploadExcel(@RequestParam(value = "file") @ApiParam(name = "file", value = "需要导入的excel文件") MultipartFile file){
        if (file.isEmpty()){
            return ResponseObject.fail(ErrorEnum.FILE_IS_EMPTY);
        }
        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return ResponseObject.fail(ErrorEnum.FILE_FORMAT_ERROR);
        }
        List<String> msg =  locationService.batchImport(file);
        return ResponseObject.success(msg);
    }

    @GetMapping(value = "/tree")
    @ApiOperation(value = "获取前端格式的location树")
    public ResponseObject getTree() {
        List<LocationTree> treeNodes = locationService.getTree();
        return ResponseObject.success(treeNodes);
    }


    /**
     * 初始节点是-1，大屏需要拿到4层结构
     * @param parentId
     * @return
     */
    @GetMapping("/platform/{id}")
    @ApiOperation(value = "大屏根据Id查询子节点(4层结构)")
    public ResponseObject getChildrens(@PathVariable("id") long parentId){
        try{
            List<Map<String,Object>> list = new ArrayList<>();
            List<Location> childrenList  = locationService.getChildren(parentId);
            childrenList.forEach(location -> {
                Map<String,Object> map = new HashMap<>(2);
                map.put("id",location.getId());
                map.put("name",location.getName());
                list.add(map);
            });
            return ResponseObject.success(list);
        }catch (Exception e){
            return ResponseObject.fail(null);
        }
    }

    @PostMapping(value="ht/map/save")
    @ApiOperation(value = "地图编辑器上传底图文件")
    public ResponseObject uploadMap(@RequestBody Map<String, Object> data){
        String  mapFile =(String) data.get("mapFile");
        Long id = Long.valueOf( data.get("locationId").toString());
        Location location = locationService.getInfo(id);
        LocationMap locationMap = null;
        if(Objects.isNull(location)){
            return ResponseObject.fail(ErrorEnum.LOCATION_IS_NOT_EXIST);
        }else{
            locationMap = location.getLocationMap();
            if(Objects.isNull(locationMap)){
                locationMap = new LocationMap();
            }
        }
        locationMap.setMapFile(mapFile);
        Long locationMapId =locationMapService.save(locationMap);
        // 保存到location中
        locationService.updateLocationMapById(locationMapId,id);
        return ResponseObject.success("ok");
    }

    /**
     * HT所需底图树
     * @return
     */
    @GetMapping(value = "/ht/tree")
    @ApiOperation(value = "地图编辑器获取树结构")
    public ResponseObject getTreeByMap() {
        List<LocationMapTree> treeNodes = locationService.getTreeAndMap();
        return ResponseObject.success(treeNodes);
    }

    @GetMapping(value ="/findByPage")
    @ApiOperation(value = "分页查询")
    public ResponseObject findByPage(@RequestParam("pi") int pi, @RequestParam("ps") int ps) {
        Pageable pageable = PageRequest.of(pi,ps);
        Page<Location> pageList = locationService.findByPage(pageable);
        Pagination<Location> page = new Pagination<Location>((int) pageList.getTotalElements(), pageList.getContent());
        return ResponseObject.success(page);
    }

}
