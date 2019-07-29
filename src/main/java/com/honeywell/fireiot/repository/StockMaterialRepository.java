package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.StockMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMaterialRepository extends JpaRepository<StockMaterial, Long>, JpaSpecificationExecutor<StockMaterial> {
    List<StockMaterial>  findByBaseMaterialId(Long materialId);

    List<StockMaterial> findByBaseMaterialIdAndWarehouseId(Long materialId, Long warehoouseId);

    @Query("from StockMaterial s where s.validAmount < s.minAmount")
    List<StockMaterial> findLowAmountMaterial();
}
