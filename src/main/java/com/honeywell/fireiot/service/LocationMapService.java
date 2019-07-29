package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.LocationMap;
import com.honeywell.fireiot.repository.LocationMapRepository;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:45 PM 4/22/2019
 */
@Service
public class LocationMapService {
    @Autowired
    LocationMapRepository locationMapRepo;

    public Long save(LocationMap entity) {
        return locationMapRepo.save(entity).getId();
    }

    public void delete(LocationMap t) {
        locationMapRepo.delete(t);
    }

    public void deleteById(Long id) {
        locationMapRepo.deleteById(id);
    }

    public Optional<LocationMap> findById(Long id) {
        Optional<LocationMap> opt = locationMapRepo.findById(id);
        return opt;
    }

    public Page<LocationMap> findPage() {
        Page<LocationMap> page = locationMapRepo.findAll(JpaUtils.getPageRequest());
        return page;
    }

    public Page<LocationMap> findPage(Specification<LocationMap> specification) {
        Page<LocationMap> page = locationMapRepo.findAll(specification, JpaUtils.getPageRequest());
        return page;
    }

    public List<LocationMap> findAll() {
        return locationMapRepo.findAll();
    }
}
