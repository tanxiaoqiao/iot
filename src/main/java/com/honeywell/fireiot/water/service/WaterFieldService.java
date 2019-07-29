package com.honeywell.fireiot.water.service;

import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.repository.WaterFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: xiaomingCao
 * @date: 2018/12/27
 */
@Service
public class WaterFieldService {

    private WaterFieldRepository waterFieldRepository;


    @Autowired
    public WaterFieldService(WaterFieldRepository waterFieldRepository) {
        this.waterFieldRepository = waterFieldRepository;
    }



    public void saveAll(List<WaterField> waterFields){
        this.waterFieldRepository.saveAll(waterFields);
    }


    /**
     * 保存
     *
     * @param field
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WaterField save(WaterField field){
        return this.waterFieldRepository.save(field);
    }


    /**
     * 删除
     *
     * @param deviceNo
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String deviceNo, Long id){
        waterFieldRepository.findById(id)
                .ifPresent(f -> {
                    if(deviceNo.equals(f.getDeviceNo())){
                        waterFieldRepository.deleteById(id);
                    }
                });
    }


    /**
     * 通过device NO查询
     *
     * @param deviceNo
     * @return
     */
    public List<WaterField> findByDeviceNo(String deviceNo){
        return waterFieldRepository.findByDeviceNo(deviceNo);
    }


    /**
     * 通过eui查询
     *
     * @param eui
     * @return
     */
    public List<WaterField> findByEui(String eui){
        return waterFieldRepository.findByEui(eui);
    }





}
