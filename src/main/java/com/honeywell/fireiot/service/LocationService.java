package com.honeywell.fireiot.service;



import com.honeywell.fireiot.dto.LocationDto;
import com.honeywell.fireiot.dto.LocationMapTree;
import com.honeywell.fireiot.dto.LocationTree;
import com.honeywell.fireiot.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @InterfaceName LocationService
 * @Description TODO
 * @Author monica Z
 * @Date 12/4/2018 3:04 PM
 **/
public interface LocationService {
     void create(LocationDto locationDto);
     void insert(LocationDto locationDto);
     void delete(long id);
     void update(LocationDto locationDto);
     Location getDetail(long id);

     List<Location> findAll(List<Long> ids);

     Location getInfo(long id);
     List<Location> getChildren(Long parentId);
     List<Long> getAllChildren(Long parentId,List<Long> childrenNodes);
     String getFullName(Long parentId, String name);

     String findFullNameById(long id);

     Long findIdByLocationName(String fullName);
     Location findById(long id);

     String getFullName(String buildingName, String floorName);

     List<String> batchImport(MultipartFile file);
     List<Location> findByFullName(String buildingName, String floorName);
     List<LocationTree> getTree();
     List<Location> getOriginTree();

     Long findIdByBuildingAndFloor(String building,String floor);
     void updateLocationMapById(Long locationMapId,Long id);
     List<LocationMapTree> getTreeAndMap();

     Page<Location> findByPage(Pageable pageable);

}
