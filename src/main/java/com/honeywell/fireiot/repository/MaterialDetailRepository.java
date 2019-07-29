package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.MaterialDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialDetailRepository extends JpaRepository<MaterialDetail, Long>, JpaSpecificationExecutor<MaterialDetail> {
    List<MaterialDetail> findByStockMaterialId(Long var1);
}
