package com.honeywell.fireiot.fire;

import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.fire.entity.FireDailyData;
import com.honeywell.fireiot.fire.repository.FireDailyDataRepository;
import com.honeywell.fireiot.repository.RoleRepository;
import com.honeywell.fireiot.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 10:24 AM 3/6/2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InitTest {


    @Autowired
    RoleService roleService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FireDailyDataRepository fireDailyDataRepository;

    @Test
    public void insertData(){
        Role role = roleRepository.findById(1L).get();
        role.setMenuId("ACCOUNT");
        roleRepository.save(role);
    }

    @Test
    public void testDate(){
        FireDailyData fireDailyData = new FireDailyData();
        fireDailyData.setStatsType(0);
        fireDailyData.setCreateTime(LocalDateTime.now());
        fireDailyDataRepository.save(fireDailyData);
    }
}
