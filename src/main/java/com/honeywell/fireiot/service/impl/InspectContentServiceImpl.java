package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.repository.InspectContentRepository;
import com.honeywell.fireiot.service.InspectContentService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName InspectContentServiceImpl
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-03-22 13:47
 */
@Service
public class InspectContentServiceImpl  implements InspectContentService {
    @Autowired
    private InspectContentRepository inspectContentRepository;
    @Override
    public void save(InspectionContent ic) {
        if(ic.getId() == 0){
          ic.setCreateTime(new Date());
        }
        inspectContentRepository.save(ic);
    }
    public void insert(InspectionContent ic){
        ic.setCreateTime(new Date());
        inspectContentRepository.save(ic);
    }


    @Override
    public void deleteByElementId(long formElementId) {
        inspectContentRepository.deleteByElementId(formElementId);
    }


    /**
     * 判断是否存在formData记录
     * @param formElementId
     * @return
     */
    @Override
    public boolean isSame(long formElementId) {
       InspectionContent ic =  inspectContentRepository.isSame(formElementId);
       if (ic == null){
           return false;
       }else {
           return true;
       }
    }

    /**
     *
     * @param ids
     * @return
     */
    @Override
    public List<InspectionContent> queryByFormElementIds(List<Long> ids) {
       List<InspectionContent>  data = inspectContentRepository.queryAllByFormElementId(ids);
        return data;
    }

    /**
     * 分页条件查询巡检内容
     * @param specification
     * @return
     */

    @Override
    public Page<InspectionContent> findByPage(Specification<InspectionContent> specification) {
        return inspectContentRepository.findAll(specification, JpaUtils.getPageRequest());
    }

    /**
     * 分页条件（element in） 查询
     * @param elements
     * @param pageable
     * @return
     */
    @Override
    public Page<InspectionContent> findAllByElementIdIn(List<Long> elements, Pageable pageable) {
        return inspectContentRepository.findAllByElementIdIn(elements,pageable);
    }
}
