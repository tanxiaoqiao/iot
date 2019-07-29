package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOrderRepository extends JpaRepository<StockOrder, Long>, JpaSpecificationExecutor<StockOrder> {

    List<StockOrder> findByAssociatedNumber(String AsociatedNumber);

}
