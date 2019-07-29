package com.honeywell.fireiot.service.impl;


import com.honeywell.fireiot.entity.Occupation;
import com.honeywell.fireiot.repository.OccupationRepository;
import com.honeywell.fireiot.service.OccupationService;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @project: fire-user
 * @name: OccupationServiceImpl
 * @author: dexter
 * @create: 2018-12-18 16:41
 * @description:
 **/
@Service
public class OccupationServiceImpl implements OccupationService {

    @Autowired
    private OccupationRepository occupationRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void save(Occupation occupation) {
        occupationRepository.save(occupation);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void delete(String occupationId) {
        occupationRepository.softDelete(occupationId);
    }

    @Override
    public Occupation findById(String id) {

        Optional<Occupation> optionalOccupation = occupationRepository.findById(id);
        if (optionalOccupation.isPresent()) {
            return optionalOccupation.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Occupation> findAll() {
        return occupationRepository.findAll();
    }

    @Override
    public Pagination<Occupation> findAllPage(Integer pageIndex, Integer pageSize) {

        int size = pageIndex <= 0 ? 0 : (pageIndex - 1);
        PageRequest pageRequest = PageRequest.of(size, pageSize, Sort.Direction.DESC, "updateTime");
        Page<Occupation> all;
        all = occupationRepository.findAll(pageRequest);
        return new Pagination<>((int) all.getTotalElements(), all.getContent());
    }
}
