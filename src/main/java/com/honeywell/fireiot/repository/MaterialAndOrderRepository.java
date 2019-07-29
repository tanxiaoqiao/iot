package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.MaterialAndOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialAndOrderRepository extends JpaRepository<MaterialAndOrder, Long>, JpaSpecificationExecutor<MaterialAndOrder> {

    @Query("from MaterialAndOrder ps where ps.stockOrderId = ?1 and ps.materialType =?2  and ps.stockMaterialId =?3")
    List<MaterialAndOrder> findMaterialAndOrder(Long stockerOrderId, int materialType, Long stockMaterialid);

    List<MaterialAndOrder> findByStockOrderIdAndMaterialDetailId(Long stockerOrderId, Long MaterialDetailId);
}
