package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.Location;
import com.honeywell.fireiot.entity.Spot;
import com.honeywell.fireiot.entity.Task;
import com.honeywell.fireiot.repository.SpotRepository;
import com.honeywell.fireiot.service.*;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class SpotServiceImpl implements SpotService {
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private SpotAsynService spotAsynService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private WorkTaskService workTaskService;
    @Autowired
    private TaskDeviceService taskDeviceService;
    @Autowired
    private SpotAndTaskService spotAndTaskService;

    /**
     * 保存点位信息
     * @param spotDto
     * @return
     */
    @Override
    public long save(SpotDto spotDto) {
        Spot spot = new Spot();
       long ids =  spotDto.getId();
        long id = 0;
        if (spotDto.getId() == 0) {
            BeanUtils.copyProperties(spotDto,spot);
             id = this.insert(spot);
        }else {
            BeanUtils.copyProperties(spotDto,spot);
            id = this.update(spot);
        }
        return id;
    }

    /**
     * 删除点位信息
     * @param id
     */
    @Override
    public void delete(long id) {
        spotRepository.deleteById(id);
        // 查询关联工作任务进行删除
        spotAsynService.deleteRelatedTask(id);
    }

    @Override
    public List<SpotDto> queryByPage(SpotDto spotDto) {
        return null;
    }

    /**
     * 获取点位详情
     * @param id
     * @return
     */
    @Override
    public Spot getSpotById(long id) {
//        Spot spot = this.isExist(id);

        Spot originSpot = spotRepository.findSpotById(id);
        return originSpot;
    }

    /**
     * 新增spot，返回id
     * @param spot
     * @return
     */
    @Override
    public long insert(Spot spot) {
        // 方法一 qrCode 直接存储信息  方法二 qrcode 存储图片
        String qrCode = null;
        String nfcTag = null;

        if(spot.getFullName() != null){
             qrCode = spot.getLocationId()+"|"+ spot.getName()+ "|" + spot.getFullName();
             nfcTag = spot.getLocationId()+"|"+ spot.getName()+ "|" + spot.getFullName();
        }else {
            Location location = locationService.findById(spot.getId());
            qrCode = spot.getLocationId()+"|"+ spot.getName()+"|"+location.getFullName();
            nfcTag = spot.getLocationId()+"|"+ spot.getName()+"|"+location.getFullName();
        }
        spot.setQrCode(qrCode);
        spot.setNfcTag(nfcTag);
        spot.setCreateTime(new Date());
        Spot newSpot = spotRepository.save(spot);
        // 异步生成qrImage
        try {
           spotAsynService.createQrImage(newSpot.getId(),spot.getName(),qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newSpot.getId();
    }

    /**
     * 更新spot
     * @param spot
     * @return
     */
    @Override
    public long update(Spot spot) {
        Spot originRecord = spotRepository.getOne(spot.getId());
        //  点位名称或是位置发生变化 ， 点位二维码随之发生变化
        if ((! originRecord.getName().equals(spot.getName())) || (! originRecord.getFullName().equals(spot.getFullName()))){
            try {
                spotAsynService.createQrImage(originRecord.getId(),spot.getName(),spot.getFullName());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        spot.setUpdateTime(new Date());
        Spot updateSpot = spotRepository.saveAndFlush(spot);
        return updateSpot.getId();
    }

    /**
     * 查询所有点位信息，映射至spotDto返回
     * @return
     */
    @Override
    public List<SpotDto> findAll() {
        List<Spot> list = spotRepository.findAll();
        List<SpotDto> spotDtoList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            SpotDto spotDto = new SpotDto();
            BeanUtils.copyProperties(list.get(i),spotDto);
            spotDtoList.add(spotDto);
        }
        return spotDtoList;
    }

    /**
     * 分页查询点位信息
     * @return
     */
    @Override
    public Page<Spot> findPage() {
        Page<Spot> page = spotRepository.findAll(JpaUtils.getPageRequest());
        return page;
    }
    @Override
    public Pagination<Spot> findPage(Specification<Spot> specification) {
        Page<Spot> page = spotRepository.findAll(specification, JpaUtils.getPageRequest());
        return new Pagination<>((int) page.getTotalElements(), page.getContent());
    }
    @Override
    public List<SpotTaskDto> query(SpotTaskSearch spotTaskSearch) {
        if (spotTaskSearch.getSpotName() != null){

        }
        return null;
    }
    /**
     * 判断是否存在该id的对象
     * @param id
     * @return
     */
    @Override
    public Boolean isExist(long id) {
        Spot spot = spotRepository.findSpotById(id);
        if (spot == null){
            return false;
        }else {
            return true;
        }
    }
    /**
     * 获取点位工作任务内容,应用于巡检查询页面
     * @param spotTaskSearch
     * @return
     */
    @Override
    public List<SpotTaskShow> findSpotContents(SpotTaskSearch spotTaskSearch) {
        // 点位名称
        String name = spotTaskSearch.getSpotName();
        // locationId
        long locationId = spotTaskSearch.getLocationId();
        // 工作任务名称
        String taskName = spotTaskSearch.getTaskName();
        //         定义返回列表
        List<SpotTaskShow> returnList = new ArrayList<>();
        SpotTaskShow spotTaskShow = new SpotTaskShow();
        List<Spot> spotList = new ArrayList<>();
        if(name == null && locationId == 0 && taskName == null){
            // 无条件查询
            spotList = spotRepository.findAll();
        }else{
            spotList = getSpotIds(name,locationId);
        }
        for(int i =0; i< spotList.size(); i++){
            List<SpotTaskShow> data = toSpotTaskShow(spotList.get(i),taskName);

            returnList.addAll(data);
        }
        return returnList;
    }

    @Override
    public List<SpotTaskShow> findSpotContentsByPage(SpotTaskSearch spotTaskSearch, Pageable pageable) {
        // 任务名称
      String taskName = spotTaskSearch.getTaskName();
      List<SpotTaskShow> returnList = new ArrayList<>();
      Page<Task> taskPage = null;
      List<Spot> spotList = this.getSpotIds(spotTaskSearch.getSpotName(),spotTaskSearch.getLocationId());
      if(spotList.size() > 0){
          for(int i =0;i< spotList.size();i++){
              // 任务相关id集合
              List<Long> taskIds = spotAndTaskService.queryBySpotId(spotList.get(i).getId());
              if(taskName == null){
                  taskPage= workTaskService.findAllByTaskIdIn(taskIds,pageable);
              }else {
                  taskPage = workTaskService.findAllByTaskIdInAndNameLike(taskIds,taskName,pageable);
              }
              for(int n =0; n< taskPage.getContent().size();n++){

                  SpotTaskShow spotTaskShow = new SpotTaskShow();
                  spotTaskShow.setSpotId(spotList.get(i).getId());
                  spotTaskShow.setName(spotList.get(i).getName());
                  spotTaskShow.setFullName(spotList.get(i).getFullName());
                  spotTaskShow.setTaskName(taskPage.getContent().get(n).getName());
                  spotTaskShow.setTaskId(taskPage.getContent().get(n).getId());
                  int m =  taskDeviceService.relatedDeviceNum(taskPage.getContent().get(n).getId());
                  spotTaskShow.setDeviceNum(m);
                  returnList.add(spotTaskShow);
              }
          }
      }
        return returnList;
    }

    @Override
    public List<TaskDto> querySpotTask(SpotTaskSearch spotTaskSearch, Pageable pageable) {
        return null;
    }

    private List<Spot> getSpotIds(String name,long locationId){
        // 获取 当前location 所包含的所有下属节点
        List<Long> childrenIds = new ArrayList<>();
        List<Spot> spotList = new ArrayList<>();

        // 存在locationId
        if(locationId != 0){
            childrenIds = locationService.getAllChildren(locationId,childrenIds);
        }
        childrenIds.add(locationId);
        // 查询相关点位
        if(name != null && locationId == 0){
            spotList = spotRepository.findAllByNameLike(name);
        }else if(name == null && locationId != 0){
            spotList = spotRepository.findAllByLocationIdIn(childrenIds);
        }else if (name != null && locationId != 0){
            spotList = spotRepository.findAllByNameAndLocationId(name, childrenIds);
        }
        return spotList;

    }

    /**
     *
     * @param spot
     * @param taskName
     * @return
     */
    private List<SpotTaskShow> toSpotTaskShow(Spot spot,String taskName){
        List<SpotTaskShow> data = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();

        if(taskName == null){
            taskList = workTaskService.queryBySpotId(spot.getId());
        }else {
            taskList = workTaskService.queryBySpotIdAndTaskName(spot.getId(),taskName);
        }
        if(taskList.size() > 0){
            for(int i= 0; i<taskList.size(); i++){
                SpotTaskShow spotTaskShow = new SpotTaskShow();
                spotTaskShow.setSpotId(spot.getId());
                spotTaskShow.setFullName(spot.getFullName());
                spotTaskShow.setName(spot.getName());
                spotTaskShow.setTaskId(taskList.get(i).getId());
                spotTaskShow.setTaskName(taskList.get(i).getName());
                spotTaskShow.setDeviceNum( taskDeviceService.relatedDeviceNum(taskList.get(i).getId()));
                data.add(spotTaskShow);
            }
        }else {
            SpotTaskShow spotTaskShow = new SpotTaskShow();
            spotTaskShow.setSpotId(spot.getId());
            spotTaskShow.setFullName(spot.getFullName());
            spotTaskShow.setName(spot.getName());
            data.add(spotTaskShow);
        }
//        spotTaskShow.setSpotId(spot.getId());
//        spotTaskShow.setFullName(spot.getFullName());
//        spotTaskShow.setName(spot.getName());
//        for(int i= 0; i<taskList.size(); i++){
//            spotTaskShow.setTaskId(taskList.get(i).getId());
//            spotTaskShow.setTaskName(taskList.get(i).getName());
//            spotTaskShow.setDeviceNum( taskDeviceService.relatedDeviceNum(taskList.get(i).getId()));
//            data.add(spotTaskShow);
//         }
        return data;
    }

}
