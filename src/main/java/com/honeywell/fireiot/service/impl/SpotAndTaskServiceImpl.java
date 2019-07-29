package com.honeywell.fireiot.service.impl;



import com.honeywell.fireiot.entity.SpotAndTask;
import com.honeywell.fireiot.repository.SpotAndTaskRepository;
import com.honeywell.fireiot.service.SpotAndTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SpotAndTaskServiceimpl
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/10 13:25
 */
@Service
public class SpotAndTaskServiceImpl implements SpotAndTaskService {
    @Autowired
    private SpotAndTaskRepository stRepository;

    /**
     * 根据点位ID 查询相关任务id
     * @param id
     * @return
     */
    @Override
    public List<Long> queryBySpotId(long id) {
       List<SpotAndTask> list =  stRepository.findAllBySpotId(id);
       List<Long> idsList = new ArrayList<>();
       for (int i = 0;i< list.size(); i++){
           idsList.add(list.get(i).getTaskId());
       }
        return idsList;
    }

    /**
     * 根据点位id,删除该点位所有的关联的工作任务
     * @param id
     */
    @Override
    public void deleteBySpotId(long id) {
        stRepository.deleteAllBySpotId(id);
    }

    /**
     * 删除工作任务与点位的关联
     * @param spotAndTask
     */
    @Override
    public void deleteByTaskId(SpotAndTask spotAndTask) {
        stRepository.delete(spotAndTask);
    }

    /**
     * 新增点位与工作任务关联
     * @param st
     */
    @Override
    public void save(SpotAndTask st) {

        stRepository.save(st);
    }
}
