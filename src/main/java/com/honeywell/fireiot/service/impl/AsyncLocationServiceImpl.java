package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.entity.Location;
import com.honeywell.fireiot.repository.LocationRepository;
import com.honeywell.fireiot.service.AsyncLocationService;
import com.honeywell.fireiot.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AsyncLocationServiceImpl implements AsyncLocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncLocationServiceImpl.class);

    @Autowired
    private LocationService locationService;
    @Autowired
    private LocationRepository locationRepository;

    /**
     * 更新所有相关节点的fullName
     * @param id
     */
    @Async
    @Override
    public void updateRelatedNodes(Long id) {
        List<Long> childrenNodeIds = new ArrayList<>();
        synchronized (childrenNodeIds) {
            try{

                childrenNodeIds = locationService.getAllChildren(id,childrenNodeIds);
                for (int i = 0; i < childrenNodeIds.size(); i++) {
                    Location node = locationRepository.findAllById(childrenNodeIds.get(i));
                    String fullName = locationService.getFullName(childrenNodeIds.get(i), node.getName());
                    node.setFullName(fullName);
                    locationRepository.save(node);
                }
                LOGGER.info(Thread.currentThread().getName()+"异步更新fullname");
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        LOGGER.info(Thread.currentThread().getName()+"执行完毕");

    }
}
