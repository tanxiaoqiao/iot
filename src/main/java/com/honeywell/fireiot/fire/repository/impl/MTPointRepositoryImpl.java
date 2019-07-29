package com.honeywell.fireiot.fire.repository.impl;

import com.honeywell.fireiot.fire.repository.enhance.MTPointRepositoryEnhance;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:00 PM 6/7/2018
 */
public class MTPointRepositoryImpl implements MTPointRepositoryEnhance {

    @Autowired
    EntityManager entityManager;
}
