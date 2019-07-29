package com.honeywell.fireiot.service;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.FileImportAction;
import com.honeywell.fireiot.entity.WaterDevice;
import com.honeywell.fireiot.repository.BusinessDeviceRepository;
import com.honeywell.fireiot.repository.WaterDeviceRepository;
import com.honeywell.fireiot.utils.ExcelUtil;
import com.honeywell.fireiot.utils.ReadFileConditon;
import com.honeywell.fireiot.water.repository.WaterFieldRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author ： YingZhang
 * @Description: 解析各种文件
 * @Date : Create in 4:16 PM 5/22/2019
 */
@Slf4j
@Service
public class FileParseService {

    @Autowired
    WaterDeviceRepository waterDeviceRepository;

    @Autowired
    BusinessDeviceRepository businessDeviceRepository;

    @Autowired
    WaterFieldRepository waterFieldRepository;

    public ErrorEnum deleteWaterDevice(MultipartFile file,ReadFileConditon rfc){
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        try {
            if (fileName.endsWith(FileImportAction.SUFFIX_XLS)) {
                // xls
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith(FileImportAction.SUFFIX_XLSX)) {
                // xlsx
                workbook = new XSSFWorkbook(file.getInputStream());
            } else{
                return ErrorEnum.FILE_PATTERN_ERROR;
            }

            // 取第一页
            Sheet sheet = workbook.getSheetAt(rfc.getSheetIndex());
            // 获取设备ID(DeviceEUI)
            List<String> deviceNoList = IntStream.rangeClosed(rfc.getIgonreRowNum(), sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .map(cells -> ExcelUtil.readRowString(cells, rfc.getColumnNum()))
                    .filter(s -> StringUtils.isNotBlank(s))
                    .collect(Collectors.toList());
            // 删除
            deviceNoList.forEach(s -> {
                Optional<WaterDevice> optional = waterDeviceRepository.findByDeviceEUI(s);
                if(optional.isPresent()){
                    String deviceNo = optional.get().getDeviceNo();
                    waterDeviceRepository.deleteByDeviceNo(deviceNo);
                    businessDeviceRepository.deleteByDeviceNo(deviceNo);
                    waterFieldRepository.deleteAllByDeviceNo(deviceNo);
                }else{
                    log.error("水系统设备找不着 deviceEUI = " + s);
                }
            });
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ErrorEnum.FILE_PARSE_ERROR;
        }

        return null;
    }

}
