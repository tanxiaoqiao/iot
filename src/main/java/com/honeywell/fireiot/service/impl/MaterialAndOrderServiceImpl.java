package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.MaterialOperateType;
import com.honeywell.fireiot.constant.MaterialType;
import com.honeywell.fireiot.dto.MaterialDetailDto;
import com.honeywell.fireiot.dto.OrderMaterialDto;
import com.honeywell.fireiot.entity.MaterialAndOrder;
import com.honeywell.fireiot.entity.StockOrder;
import com.honeywell.fireiot.repository.MaterialAndOrderRepository;
import com.honeywell.fireiot.repository.StockOrderRepository;
import com.honeywell.fireiot.service.MaterialAndOrderService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialAndOrderServiceImpl implements MaterialAndOrderService {

    @Autowired
    MaterialAndOrderRepository materialAndOrderRep;

    @Autowired
    StockOrderRepository stockOrderRepository;

    @Override
    public void save(MaterialAndOrder entity) {
        materialAndOrderRep.save(entity);
    }

    @Override
    public void delete(MaterialAndOrder entity) {
        materialAndOrderRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        materialAndOrderRep.deleteById(id);
    }

    @Override
    public Optional<MaterialAndOrder> findById(Long id) {
        return materialAndOrderRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<MaterialAndOrder> findPage() {
        Page<MaterialAndOrder> entityPage = materialAndOrderRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<MaterialAndOrder> findPage(Specification<MaterialAndOrder> specification) {
        Page<MaterialAndOrder> entityPage = materialAndOrderRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }


   public void updateBackMaterial(OrderMaterialDto orderMaterialDto, MaterialDetailDto materialDetailDto, StockOrder stockOrder){

       if(stockOrder.getOperationType() != MaterialOperateType.MATERIAL_BACK) {
           return;
       }
       //获取关联单号
       List<StockOrder> stockOrderList = stockOrderRepository.findByAssociatedNumber(stockOrder.getAssociatedNumber());
       if (stockOrderList.isEmpty()) {
           return;
       }

       List<MaterialAndOrder> materialAndOrderList = new ArrayList<>();
       float amount = 0;
       if(orderMaterialDto != null){
           amount = orderMaterialDto.getAmount();
           materialAndOrderList = materialAndOrderRep.findMaterialAndOrder(stockOrder.getId(), MaterialType.STOCK_MATERIAL,orderMaterialDto.getStockMaterialId());
       }

       if(materialDetailDto != null){
           amount = materialDetailDto.getValidAmount();
           materialAndOrderList = materialAndOrderRep.findByStockOrderIdAndMaterialDetailId(stockOrder.getId(),materialDetailDto.getId());
       }

       if(materialAndOrderList.isEmpty()){
           return;
       }

       MaterialAndOrder entity = materialAndOrderList.get(0);
       float backAmount = entity.getBackAmount()+ amount;
       ;
       entity.setBackAmount(backAmount);
       update(entity);
   }

    @Override
    public void saveOrderMaterial(OrderMaterialDto orderMaterialDto, StockOrder stockOrder) {
        MaterialAndOrder materialAndOrder  = new MaterialAndOrder();
        BeanUtils.copyProperties(orderMaterialDto, materialAndOrder);
        materialAndOrder.setMaterialType(MaterialType.STOCK_MATERIAL);
        materialAndOrder.setStockOrderId(stockOrder.getId());
        materialAndOrderRep.save(materialAndOrder);

        //退库情况更新
        updateBackMaterial(orderMaterialDto, null,stockOrder);
    }


    @Override
    public void saveOrderMaterialDetail(MaterialDetailDto materialDetailDto, StockOrder stockOrder) {
        MaterialAndOrder materialAndOrder = new MaterialAndOrder();
        BeanUtils.copyProperties(materialDetailDto, materialAndOrder);
        materialAndOrder.setStockOrderId(stockOrder.getId());
        materialAndOrder.setAmount(materialDetailDto.getValidAmount());
        materialAndOrder.setMaterialType(MaterialType.MATERIAL_DETAIL);
        materialAndOrder.setMaterialDetailId(materialDetailDto.getId());
        materialAndOrderRep.save(materialAndOrder);

        //退库情况更新
        updateBackMaterial(null, materialDetailDto,stockOrder);
    }

    @Override
    public void update(MaterialAndOrder materialAndOrder) {
        Optional<MaterialAndOrder> optional = materialAndOrderRep.findById(materialAndOrder.getId());
        if (optional.isPresent()) {
            MaterialAndOrder entity = (MaterialAndOrder)optional.get();
            BeanUtils.copyProperties(materialAndOrder, entity);
            materialAndOrderRep.save(entity);
    }
    }

    @Override
    public Pagination<MaterialAndOrder> findMaterialPage(Specification<MaterialAndOrder> var1) {
        Page<MaterialAndOrder> entityPage = materialAndOrderRep.findAll(JpaUtils.getPageRequest());
        return (Pagination<MaterialAndOrder>) entityPage;
    }
}
