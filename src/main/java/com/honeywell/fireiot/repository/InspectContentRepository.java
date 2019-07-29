package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.InspectionContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @InterfaceName InspectContentRepository
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-03-22 13:54
 */
@Repository
public interface InspectContentRepository extends JpaRepository<InspectionContent, Long>, JpaSpecificationExecutor<InspectionContent> {
    @Query(value="select ic from InspectionContent ic where  ic.formElementId =?1")
    InspectionContent isSame(long formElementId);
    @Query(value= "select ic from InspectionContent ic where ic.formElementId in ?1")
    List<InspectionContent> queryAllByFormElementId(List<Long> ids);


    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value="delete from  InspectionContent ic  where  ic.formElementId  =:id")
    void deleteByElementId(@Param("id") long id);

    @Query(value = "select ic from InspectionContent ic where ic.formElementId in :elements",countQuery = "select count(ic) from InspectionContent  ic where ic.formElementId in:elements" )
    Page<InspectionContent> findAllByElementIdIn(@Param("elements") List<Long> elements, @Param("pageable")Pageable pageable);
}
