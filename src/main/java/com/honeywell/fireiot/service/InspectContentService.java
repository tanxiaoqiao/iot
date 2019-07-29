package com.honeywell.fireiot.service;

import com.honeywell.fireiot.entity.InspectionContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @InterfaceName InspectContentService
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-03-22 13:46
 */
public interface InspectContentService {
    void save(InspectionContent ic);
    void deleteByElementId(long formElementId);
    boolean isSame(long formElementId);
    List<InspectionContent> queryByFormElementIds(List<Long> ids);
    Page<InspectionContent> findByPage(Specification<InspectionContent> specification);
    Page<InspectionContent> findAllByElementIdIn(List<Long> elements,Pageable pageable);


}
