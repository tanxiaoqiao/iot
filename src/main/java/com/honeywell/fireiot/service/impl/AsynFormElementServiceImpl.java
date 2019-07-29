package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.FormElement;
import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.service.AsynFormElementService;
import com.honeywell.fireiot.service.InspectContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName AsynFormElementServiceImpl
 * @Description  formData 数据解析
 * @Author Monica Z
 * @Date 2019-03-22 10:19
 */
@Service
public class AsynFormElementServiceImpl implements AsynFormElementService {
    @Autowired
    private InspectContentService inspectContentService;

    /**
     * 将element的内容处理 入库
     * @param element
     */

    @Async
    @Override
    public void toInspectContent(FormElement element) {
        double maxNumber = 0;
        double minNumber = 0;
        if( element.getValidator().getMaxNumber() != 0){
            maxNumber =  element.getValidator().getMaxNumber();
        }
        if( element.getValidator().getMinNumber() != 0){
            minNumber =   element.getValidator().getMinNumber();
        }
        // 单位
        String unit = null;
        if( element.getValidator().getUnit() != null){
            unit = element.getValidator().getUnit();
        }
        InspectionContent ic = new InspectionContent();
        // 巡检内容项名称
        ic.setName(element.getKey());
        // 默认值
        ic.setDefaultValue(element.getValue());

        Map<String,String> options = element.getOptions();
        StringBuilder option = new StringBuilder();
        // 结果
        String result = null;
        if(options != null){
            for(String value:options.values() ){
                if(option.length() != 0){
                    option.append("/").append(value);
                }else{
                    option.append(value);
                }
            }
            result = option.toString();

        }else{
            result = minNumber+"~"+maxNumber;

        }

        ic.setResult(result);
        ic.setUnit(unit);
        ic.setFormElementId(element.getId());

        inspectContentService.save(ic);



    }
}
