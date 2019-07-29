package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.dto.LocationDto;
import com.honeywell.fireiot.dto.LocationMapTree;
import com.honeywell.fireiot.dto.LocationTree;
import com.honeywell.fireiot.entity.Location;
import com.honeywell.fireiot.repository.LocationRepository;
import com.honeywell.fireiot.service.AsyncLocationService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.utils.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName LocationServiceImpl
 * @Description TODO
 * @Author monica Z
 * @Date 12/4/2018 3:05 PM
 **/
@Service
public class LocationServiceImpl implements LocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger (LocationServiceImpl.class);
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AsyncLocationService asyncLocationService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void create(LocationDto locationDto) {
        long n = locationRepository.count();
        List<Location> existList = new ArrayList<>();
        if (n > 0) {
            existList = locationRepository.isSame(locationDto.getName(), locationDto.getParentId());
            if (existList.size() == 0) {
                if (locationDto.getLevel() == 1) {
                    locationDto.setFullName(locationDto.getName());
                } else if (locationDto.getLevel() == 2) {
                    long parentId = locationDto.getParentId();
                    Location parLocation = locationRepository.findNameById(parentId);
                    locationDto.setFullName(parLocation.getName() + "/" + locationDto.getName());
                } else if (locationDto.getLevel() == 3) {
                    long parentId = locationDto.getParentId();
                    Location location = locationRepository.findNameById(parentId);
                    Location pLocation = locationRepository.findNameById(location.getParentId());
                    locationDto.setFullName(pLocation.getName() + "/" + location.getName() + "/" + locationDto.getName());
                }
                insert(locationDto);
            } else {
//                throw new BusinessException(ErrorEnum.PARAM_EXIST);
            }
        } else { // 根节点
            locationDto.setParentId((long) -1);
            this.insert(locationDto);
        }

    }

    @Override
    public void insert(LocationDto locationDto) {
        Location location = new Location();
        BeanUtils.copyProperties(locationDto, location);
        location.setCreateTime(new Date());
        locationRepository.saveAndFlush(location);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(long id) {
        //  查询其他服务关联
        locationRepository.updateDeleteById(id);
        // 发布消息
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(LocationDto locationDto) {
        Location originRecord = locationRepository.findAllById(locationDto.getId());
        Location location = new Location();
        BeanUtils.copyProperties(locationDto, location);
        location.setUpdateTime(new Date());
        // name 修改
        if (!locationDto.getName().equals(originRecord.getName())) {
            // 查询节点下所有子节点 更新fullName
            asyncLocationService.updateRelatedNodes(originRecord.getId());
        }
        locationRepository.save(location);
    }

    @Override
    public Location getDetail(long id) {
        Location existNode = locationRepository.findAllById(id);
        if (existNode != null) {
            return existNode;
        } else {
            return null;
//            throw new BusinessException(ErrorEnum.PARAM_NOT_EXIST);
        }
    }

    @Override
    public List<Location> findAll(List<Long> ids) {
        return locationRepository.findAllByIdIn(ids);
    }

    @Override
    public Location getInfo(long id) {
        Optional<Location> opt = locationRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public String findFullNameById(long id) {
        return locationRepository.findFullNameById(id);
    }

    @Override
    public Long findIdByLocationName(String fullName) {
        if (StringUtils.isEmpty(fullName)) {
            return null;
        }
        Long locationId = locationRepository.findIdByFullName(fullName);
        return locationId;
    }

    @Override
    public Location findById(long id) {
        return locationRepository.findAllById(id);
    }

    @Override
    public String getFullName(String buildingName, String floorName) {
        if (StringUtils.isEmpty(buildingName)) {
            return null;
        }
        String locationName = buildingName;
        if (StringUtils.isNotEmpty(floorName)) {
            locationName += "/" + floorName;
        }
        return locationName;
    }

    /**
     * 上传excel
     * @param file
     * @return
     */
    @Override
    public List<String> batchImport(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        List<String> msg = new ArrayList<>();
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }

        InputStream is = null;
        Workbook wb = null;
        try {
            is = file.getInputStream();
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {
                wb = new XSSFWorkbook(is);
            }
           this.readExcel(wb);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
    /**
     * 解析excel文件内容
     * @return
     */
    public List<String> readExcelValue(Workbook wb) {
        // excel数据已在数据库存在记录
        List<String> existDate = new ArrayList<>();

        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        //总列数
        int totalCells = 0;
        //得到Excel的列数(前提是有行数)，从第二行算起
        if (totalRows >= 2 && sheet.getRow(1) != null) {
            totalCells = sheet.getRow(1).getPhysicalNumberOfCells();
        }

        String br = "<br/>";
        String rowMessage = "";
        String errorMsg = "";


        // 从第二行开始， 首行为标题
        for (int i = 1; i < totalRows; i++) {
            String parentName = null;
            String nodeName = null;
            Row row = sheet.getRow(i);
            if (row == null) {
                existDate.add(errorMsg += br + "第" + (i + 1) + "行数据有问题，请仔细检查！");
                continue;
            }
            // 循环列
            for (int j = 0; j < totalCells; j++) {
                Cell cell = row.getCell(j);
                if (null != cell) {
                    if (j == 0) {
                        parentName = cell.getStringCellValue();
                        if (org.springframework.util.StringUtils.isEmpty(parentName)) {
                            existDate.add(rowMessage += "楼宇不能为空");
                        }

                    } else if (j == 1) {
                        nodeName = cell.getStringCellValue();
                    }
                }
            }
            if (null != parentName && null != nodeName) {
                Location childNode = null;
                Location parentNode = locationRepository.findAllByNameAndLevel(parentName, 1);

                if (null != parentNode) {
                    childNode = locationRepository.findListByNameAndLevelAndParentId(nodeName, 2, parentNode.getId());
                }
                Location location = new Location();
                Location location2 = new Location();
                if (null == parentNode && null == childNode) {
                    location.setParentId((long) -1);
                    location.setName(parentName);
                    location.setLevel(1);
                    location.setFullName(parentName);
                    location.setCreateTime(new Date());
                    Location originNode = locationRepository.save(location);
                    location2.setLevel(2);
                    location2.setFullName(parentName + "/" + nodeName);
                    location2.setParentId(originNode.getId());
                    location2.setName(nodeName);
                    location2.setCreateTime(new Date());
                    locationRepository.save(location2);

                } else if (null != parentNode && null == childNode) {
                    location.setParentId(parentNode.getId());
                    location.setName(nodeName);
                    location.setFullName(parentNode.getName() + "/" + nodeName);
                    location.setLevel(2);
                    location.setCreateTime(new Date());

                    locationRepository.save(location);
                } else if (null != parentNode && null != childNode) {
                    existDate.add("第" + (i + 1) + "行数据，数据库已经存在");
                }
                // 仅仅只有楼栋信息
            } else if (null == nodeName && null != parentName) {
                Location originNode = locationRepository.findAllByNameAndLevel(parentName, 1);
                if (originNode == null) {
                    Location newNode = new Location();
                    newNode.setParentId((long) -1);
                    newNode.setCreateTime(new Date());
                    newNode.setName(parentName);
                    newNode.setFullName(parentName);
                    newNode.setLevel(1);
                    locationRepository.save(newNode);
                } else {
                    existDate.add("第" + (i + 1) + "行数据，数据库已经存在");
                }
            }
        }
        return existDate;
    }


    private  void readExcel(Workbook wb){
        Sheet sheet = wb.getSheetAt(0);
        int rowNum = sheet.getLastRowNum() + 1;
        LOGGER.info("sheetName"+sheet.getSheetName());
        for(int j = 1 ; j < rowNum; j++ ) {
            Row row = sheet.getRow(j);
            //读取每一行
            String line = ExcelUtil.readRowString(row, 10);
            String[] pills = line.split(",");
            String level1=pills[0].trim();
            String level2=pills[1].trim();
            String level3=pills[2].trim();
            String level4=pills[3].trim();
            Location location  = new Location();
            if((!level1.isEmpty())&level2.isEmpty()&level3.isEmpty()&level4.isEmpty()){
                location.setLevel(1);
                location.setName(level1);
                location.setFullName(level1);
                location.setAddress(level1);
                location.setParentId(-1L);
            }else if((!level1.isEmpty())&(!level2.isEmpty())&level3.isEmpty()&level4.isEmpty()){
                location.setLevel(2);
                location.setName(level2);
                location.setFullName(level1+"/"+level2);
                location.setAddress(level2);
                Long parentId = locationRepository.findIdByFullName(level1);
                location.setParentId(parentId);
            }else if((!level1.isEmpty())&(!level2.isEmpty())&(!level3.isEmpty())&level4.isEmpty()){
                location.setLevel(3);
                location.setName(level3);
                location.setFullName(level1+"/"+level2+"/"+level3);
                location.setAddress(level3);
                Long parentId = locationRepository.findIdByFullName(level1+"/"+level2);
                location.setParentId(parentId);
            }else if((!level1.isEmpty())&(!level2.isEmpty())&(!level3.isEmpty())&(!level4.isEmpty())){
                location.setLevel(4);
                location.setName(level4);
                location.setFullName(level1+"/"+level2+"/"+level3+"/"+level4);
                location.setAddress(level4);
                Long parentId = locationRepository.findIdByFullName(level1+"/"+level2+"/"+level3);
                location.setParentId(parentId);
            }
            locationRepository.save(location);
        }
    }

    /**
     * 获取该节点下的子节点
     *
     * @param parentId
     * @return
     */
    @Override
    public List<Location> getChildren(Long parentId) {
        List<Location> childList = locationRepository.findAllByParentId(parentId);
        if (childList.size() > 0) {
            return childList;
        } else {
            return null;
//            throw new BusinessException(ErrorEnum.CHILDREM_NOT_EXIST);
        }
    }

    /**
     * 获取该节点下所有子孙节点（以该节点为根节点进行遍历）
     *
     * @param id
     * @return
     */
    @Override
    public List<Long> getAllChildren(Long id, List<Long> childrenNodes) {
        List<Location> childrenList = new ArrayList<>();
        childrenList = locationRepository.findAllByParentId(id);
        if (childrenList.size() > 0) {
            for (int i = 0; i < childrenList.size(); i++) {
                childrenNodes.add(childrenList.get(i).getId());
                getAllChildren(childrenList.get(i).getId(), childrenNodes);
            }
        }
        return childrenNodes;

    }

    /**
     * 获取fullName
     *
     * @param id   当前节点id
     * @param name
     * @return
     */
    @Override
    public String getFullName(Long id, String name) {
        String fullName = null;
        Location parentNode = new Location();
        Location currentNode = new Location();
        currentNode = locationRepository.findAllById(id);
        parentNode = locationRepository.findAllById(currentNode.getParentId());
        if (currentNode.getParentId() != -1) {
            fullName = parentNode.getName() + "/" + name;
            name = getFullName(parentNode.getId(), fullName);
        }
        return name;
    }

    @Override
    public List<Location> findByFullName(String buildingName, String floorName) {
        if (buildingName == null) {
            return null;
        } else if (floorName == null) {
            return locationRepository.findByFullName(buildingName);
        } else {
            return locationRepository.findByFullName(buildingName + "/" + floorName);
        }
    }

    /**
     * 获取设备树
     *
     * @return
     */
    @Override
    public List<LocationTree> getTree() {
        List<Location> root = locationRepository.findAllByParentId((long) -1);
        List<LocationTree> rootTree = new ArrayList<>();
        rootTree = transformTree(root, rootTree);
        List<LocationTree> tree = rebuildTree(rootTree);
        return tree;
    }

    public List<LocationTree> rebuildTree(List<LocationTree> treeNodes) {

        for (int i = 0; i < treeNodes.size(); i++) {
            List<Location> childNodes = locationRepository.findAllByParentId(treeNodes.get(i).getSort());
            List<LocationTree> childTree = new ArrayList<>();
            childTree = transformTree(childNodes, childTree);
            treeNodes.get(i).setChildren(childTree);
            rebuildTree(childTree);
        }
        return treeNodes;
    }

    public List<LocationTree> transformTree(List<Location> originNodes, List<LocationTree> treeNodes) {
        for (Location location : originNodes) {
            LocationTree tree = new LocationTree();
            tree.setLevel(location.getLevel());
            tree.setName(location.getName());
            tree.setSort(location.getId());
            tree.setFullName(location.getFullName());
            treeNodes.add(tree);
        }
        return treeNodes;
    }

    @Override
    public List<Location> getOriginTree() {
        List<Location> root = locationRepository.findAllByParentId((long) -1);
        List<Location> originTree = rebuildOriginTree(root);
        return originTree;
    }

    public List<Location> rebuildOriginTree(List<Location> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            List<Location> child = locationRepository.findAllByParentId(nodes.get(i).getId());
            nodes.get(i).setChildren(child);
            rebuildOriginTree(child);
        }
        return nodes;
    }

    @Override
    public Long findIdByBuildingAndFloor(String building, String floor) {
        if (StringUtils.isBlank(floor) && StringUtils.isNotBlank(building)) {
            return locationRepository.findIdByBuilding(building);
        } else if (StringUtils.isNotBlank(floor) && StringUtils.isNotBlank(building)) {
            String fullname = building + "/" + floor;
            return locationRepository.findIdByBuildingAndFloor("%" + fullname);
        }
        return null;
    }

    @Override
    public void updateLocationMapById(Long locationMapId, Long id) {
        locationRepository.updateLocationMapById(locationMapId, id);
    }


    /**
     * 底图编辑器获取空间树和对应的资源文件路径
     *
     * @return
     */
    @Override
    public List<LocationMapTree> getTreeAndMap() {
        List<Location> root = locationRepository.findAllByParentId((long) -1);
        List<LocationMapTree> rootTree = new ArrayList<>();
        rootTree = transformMapTree(root, rootTree);
        List<LocationMapTree> tree = rebuildMapTree(rootTree);
        return tree;
    }

    @Override
    public Page<Location> findByPage(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }


    public List<LocationMapTree> transformMapTree(List<Location> originNodes, List<LocationMapTree> treeNodes){
        for(Location location: originNodes){
            LocationMapTree tree = new LocationMapTree();
            tree.setLevel(location.getLevel());
            tree.setName(location.getName());
            tree.setId(location.getId());
            tree.setParentId(location.getParentId());

            if(location.getLocationMap() != null) {
                tree.setMapFile(location.getLocationMap().getMapFile());
            }
            treeNodes.add(tree);
        }
        return treeNodes;
    }

    public List<LocationMapTree> rebuildMapTree(List<LocationMapTree> treeNodes) {

        for (int i = 0; i < treeNodes.size(); i++) {
            List<Location> childNodes = locationRepository.findAllByParentId(treeNodes.get(i).getId());
            List<LocationMapTree> childTree = new ArrayList<>();
            childTree = transformMapTree(childNodes, childTree);
            treeNodes.get(i).setChildren(childTree);
            rebuildMapTree(childTree);
        }
        return treeNodes;
    }

}