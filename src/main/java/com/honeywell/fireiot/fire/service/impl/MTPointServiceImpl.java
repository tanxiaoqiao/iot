package com.honeywell.fireiot.fire.service.impl;

import com.honeywell.fireiot.fire.entity.MTPoint;
import com.honeywell.fireiot.fire.repository.MTPointRepository;
import com.honeywell.fireiot.fire.service.MTPointService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class MTPointServiceImpl implements MTPointService {

    @Autowired
    MTPointRepository MTPointRepo;

    @Override
    public void save(MTPoint entity) {
        MTPointRepo.save(entity);
    }

    @Override
    public void delete(MTPoint t) {
        MTPointRepo.delete(t);
    }

    @Override
    public void deleteById(Long id) {
        MTPointRepo.deleteById(id);
    }

    @Override
    public Optional<MTPoint> findById(Long id) {
        Optional<MTPoint> opt = MTPointRepo.findById(id);
        return opt;
    }

    @Override
    public Page<MTPoint> findPage() {
        Page<MTPoint> page = MTPointRepo.findAll(JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public Page<MTPoint> findPage(Specification<MTPoint> specification) {
        Page<MTPoint> page = MTPointRepo.findAll(specification, JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public List<MTPoint> findAll() {
        return MTPointRepo.findAll();
    }

    @Override
    public List<MTPoint> findByGatewayId(Long id){
        return MTPointRepo.findByFireDevice_mtGateway_Id(id);
    }
}
