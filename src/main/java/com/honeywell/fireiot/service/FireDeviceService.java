package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.BusinessFireDeviceDto;
import com.honeywell.fireiot.dto.firedevice.DevicePointDto;
import com.honeywell.fireiot.dto.firedevice.FireDeviceDto;
import com.honeywell.fireiot.dto.firedevice.FireDevicePointSearch;
import com.honeywell.fireiot.dto.waterdevice.ValueDto;
import com.honeywell.fireiot.dto.waterdevice.WaterDeviceSearch;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.FireDevice;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ReadFileConditon;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 3:01 PM 12/17/2018
 */
public interface FireDeviceService {

    void save(FireDevice entity);

    List<FireDevice> getAll();

    void add(FireDeviceDto fireDeviceDto);

    void update(FireDevice fireDevice);

    FireDevice findById(Long Id);

    void deleteById(Long Id);

    boolean checkUniqueDevice(FireDeviceDto device);

    boolean checkUpdateDevice(FireDeviceDto fireDeviceDto);

    DevicePointDto findDevicePointDtoByNo(String deviceNo);

    //用于更新或者添加设备
    void checkAndSaveDevice(FireDeviceDto fireDeviceDto);


    void toEntity(FireDeviceDto fireDeviceDto, FireDevice fireDevice);

    //文件操作
 //   int[] readCsvAnOperateDB(MultipartFile file, ReadFileConditon readFileConditon) throws Exception;

    int[] readExcelAndOperateDB(Workbook workbook,ReadFileConditon readFileConditon) throws Exception;

    //对于导入设备作用
    int[] readFileAndSaveData(MultipartFile file, ReadFileConditon readFileConditon )throws Exception;

    //对于导入设备作用
    int[] readFileAndDeleteData(MultipartFile file, ReadFileConditon readFileConditon )throws Exception;
    //查找设备点位信息
    List<DevicePointDto> findDevicePoint(FireDevicePointSearch fireDevicePointSearch);

    /**
     * 根据设备类型统计 消防设备数量
     * @param search
     * @return
     */
    List<ValueDto> findFireDeviceCount(WaterDeviceSearch search);

    /**
     * 根据businessDeviceNo查找设备
     * @param businessDeviceNo
     * @return
     */
    List<FireDevice> findByBusinessDeviceNo(String businessDeviceNo);

    FireDeviceDto toDto(FireDevice fireDevice);

    FireDevice findFirstByDeviceId(String businessDeviceNo);

    /**
     * 查询包括 Business FireDevice Location 的全部信息的分页
     * @return 分页返回设备的全部信息
     */
    Pagination findPage2BFL();
    //返回所有查询结果，不分页
     List<BusinessFireDeviceDto> find(Specification<BusinessDevice> specification);
    /**
     * 获取异常和正常的火系统设备数量
     * @return
     */
    List<Map<String,Object>> countDeviceByStatus();

    List<Map<String,Object>> countDeviceByDeviceType();

    public Integer patchChangeMTPointCName();
}
