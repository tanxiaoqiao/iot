package com.honeywell.fireiot.dto;

import com.honeywell.fireiot.utils.PageSearch;
import lombok.Data;


/**
 * @ClassName SpotTaskSearch
 * @Description
 *     点位工作任务关联查询 ，
 * 其中locationId 可为父节点 也可为子节点
 * @Author Monica Z
 * @Date 2019/1/14 10:54
 */
@Data
public class SpotTaskSearch  extends PageSearch {

    // 点位名称
    private String spotName;
    // 点位安装位置id
    private long locationId;
    // 工作任务名称
    private String taskName;


}
