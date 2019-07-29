package com.honeywell.fireiot.repository.impl;

import com.honeywell.fireiot.repository.enhance.DeviceTypeRepositoryEnhance;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:00 PM 6/7/2018
 */
public class DeviceTypeRepositoryImpl implements DeviceTypeRepositoryEnhance {

    @Autowired
    EntityManager entityManager;
}
