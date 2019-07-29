package com.honeywell.fireiot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:55 PM 3/20/2019
 */
public class MapUtil {

    public static List<Map<String,Object>> map2Listmap(Map<Object,Object> map){
        List<Map<String,Object>> list  = new ArrayList<>(7);
        map.forEach((s, o) -> {
            Map<String,Object> maps = new HashMap<>(2);
            maps.put("name",s);
            maps.put("value",o);
            list.add(maps);
        });
        return list;
    }
}
