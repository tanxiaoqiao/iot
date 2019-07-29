package com.honeywell.fireiot.fire.service.impl;

import com.honeywell.fireiot.fire.entity.MTGateway;
import com.honeywell.fireiot.fire.repository.MTGatewayRepository;
import com.honeywell.fireiot.fire.service.MTGatewayService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class MTGatewayServiceImpl implements MTGatewayService {

    @Autowired
    MTGatewayRepository mTGatewayRepo;

    @Override
    public void save(MTGateway entity) {
        mTGatewayRepo.save(entity);
    }

    @Override
    public void delete(MTGateway t) {
        mTGatewayRepo.delete(t);
    }

    @Override
    public void deleteById(Long id) {
        mTGatewayRepo.deleteById(id);
    }

    @Override
    public Optional<MTGateway> findById(Long id) {
        Optional<MTGateway> opt = mTGatewayRepo.findById(id);
        return opt;
    }

    @Override
    public Page<MTGateway> findPage() {
        Page<MTGateway> page = mTGatewayRepo.findAll(JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public Page<MTGateway> findPage(Specification<MTGateway> specification) {
        Page<MTGateway> page = mTGatewayRepo.findAll(specification, JpaUtils.getPageRequest());
        return page;
    }

    @Override
    public List<MTGateway> findAll() {
        return mTGatewayRepo.findAll();
    }

}
