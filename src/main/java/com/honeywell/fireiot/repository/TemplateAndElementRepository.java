package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.TemplateAndElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @InterfaceName TemplateAndFormRepository
 * @Description TODO
 * @Author Monica Z
 */
@Repository
public interface TemplateAndElementRepository extends JpaRepository<TemplateAndElement,Long>, JpaSpecificationExecutor<TemplateAndElement> {

    @Query(value="select te.elementId from TemplateAndElement te where te.templateId =?1 ")
    List<Long> queryElementIdsById(long templateId);
    @Query( value = "delete from TemplateAndElement te  where te.templateId =:templateId and te.elementId =:elementId ")
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    void deleteByTemplateIdAndAndElementId(@Param("templateId") long templateId, @Param("elementId") Long elementId);
}
