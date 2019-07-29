package com.honeywell.fireiot.service;





import com.honeywell.fireiot.entity.SpotAndTask;

import java.util.List;

/**
 * @InterfaceName SpotAndTaskService
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/10 13:24
 */
public interface SpotAndTaskService {
    List<Long>  queryBySpotId(long id);
    void deleteBySpotId(long id);
    void deleteByTaskId(SpotAndTask spotAndTask);
    void save(SpotAndTask spotAndTask);
}
