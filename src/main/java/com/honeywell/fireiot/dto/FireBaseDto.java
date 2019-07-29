package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:18 PM 1/3/2019
 */
@Data
public class FireBaseDto {
    // 返回数据中加入查询条件
    private Object search;
    private List<Object> values = new ArrayList<>();

    public void addObj(Object object){
        values.add(object);
    }

    public void setValues(List list){
        list.forEach(o -> {
            values.add(o);
        });
    }

}
