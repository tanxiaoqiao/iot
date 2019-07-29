package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.Spot;
import com.honeywell.fireiot.repository.SpotRepository;
import com.honeywell.fireiot.service.SpotAsynService;
import com.honeywell.fireiot.service.WorkTaskService;
import com.honeywell.fireiot.utils.QRcodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Date;

@Service
public class SpotAsynServiceImpl implements SpotAsynService {
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private WorkTaskService wtService;

    /**
     * 异步任务生成二维码 ，进行存储
     * @param id  二维码所对应点位id
     * @param name 二维码下面文字描述
     * @param fullName 二维码内容
     * @throws Exception
     */
    @Async
    @Override
    public void createQrImage(long id ,String name, String fullName) throws Exception {
        BufferedImage qrImage =  QRcodeUtil.getQRcodeWithNote(fullName, name);
        String img = QRcodeUtil.toBase64String(qrImage);
        Spot spot = spotRepository.getOne(id);
        spot.setQrImage(img);
        spot.setUpdateTime(new Date());
        spotRepository.save(spot);

    }

    /**
     * 异步任务  根据点位id，删除关联工作任务
     * @param spotId
     */
    @Async
    @Override
    public void deleteRelatedTask(long spotId) {
        wtService.deleteBySpotId(spotId);
//        List<Task> list =  wtService.queryBySpotId(spotIds);
//        if(list.size()> 0){
//            for (int i = 0; i< list.size(); i++){
//               SpotAndTask st = new SpotAndTask();
//               st.setSpot_id(spotIds);
//               st.setTask_id(list.get(i).getId());
//                wtService.deleteBySpotId(list.get(i));
//            }
//        }

    }
}
