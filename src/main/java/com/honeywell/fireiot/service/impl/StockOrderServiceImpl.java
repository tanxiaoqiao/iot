package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.MaterialOperateType;
import com.honeywell.fireiot.constant.MaterialOrderPrefix;
import com.honeywell.fireiot.dto.MaterialDetailDto;
import com.honeywell.fireiot.dto.OrderMaterialDto;
import com.honeywell.fireiot.dto.StockOrderDto;
import com.honeywell.fireiot.dto.StockOrderMaterialDto;
import com.honeywell.fireiot.entity.MaterialDetail;
import com.honeywell.fireiot.entity.StockMaterial;
import com.honeywell.fireiot.entity.StockOrder;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.StockMaterialRepository;
import com.honeywell.fireiot.repository.StockOrderRepository;
import com.honeywell.fireiot.service.MaterialAndOrderService;
import com.honeywell.fireiot.service.MaterialDetailService;
import com.honeywell.fireiot.service.StockMaterialService;
import com.honeywell.fireiot.service.StockOrderService;
import com.honeywell.fireiot.utils.JpaUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class StockOrderServiceImpl implements StockOrderService {

    @Autowired
    StockOrderRepository stockOrderRep;

    @Autowired
    StockMaterialRepository stockMaterialRepository;

    @Autowired
    MaterialDetailService materialDetailService;
    @Autowired
    StockMaterialService stockMaterialService;

   @Autowired
   MaterialAndOrderService materialAndOrderService;


    @Override
    public void save(StockOrder entity) {
        stockOrderRep.save(entity);
    }

    @Override
    public void delete(StockOrder entity) {
        stockOrderRep.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        stockOrderRep.deleteById(id);
    }

    @Override
    public Optional<StockOrder> findById(Long id) {
        return stockOrderRep.findById(id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public Page<StockOrder> findPage() {
        Page<StockOrder> entityPage = stockOrderRep.findAll(JpaUtils.getPageRequest());
        return entityPage;
    }

    /**
     * 分页查询/
     *
     * @return
     */
    @Override
    public Page<StockOrder> findPage(Specification<StockOrder> specification) {
        Page<StockOrder> entityPage = stockOrderRep.findAll(specification, JpaUtils.getPageRequest());
        return entityPage;
    }

    @Override
    public StockOrder toEntity(StockOrderDto dto) {
        StockOrder entity = new StockOrder();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public StockOrderDto toDto(StockOrder entity) {
        StockOrderDto dto = new StockOrderDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public String stockOperate(StockOrderMaterialDto stockOrderMaterialDto) {

        List<OrderMaterialDto> operateMaterialList = stockOrderMaterialDto.getOperateMaterial(); //提取操作对象
        if (null == operateMaterialList || operateMaterialList.isEmpty()) {

            return  null;
        }

        StockOrderDto stockOrderDto = stockOrderMaterialDto.getStockOrderDto();

        MaterialOperateType materialOperateType = stockOrderDto.getOperationType();

        // 1、 生成订单号
        String orderNumber = genenerateOrderNumber(materialOperateType);
        if(null == orderNumber){ //操作不存在
            return  null;
        }
        stockOrderDto.setOrderNumber(orderNumber);
        //移库时需要同时保存移入库以及移出库订单
        if( MaterialOperateType.MOVE_STOCK == materialOperateType){

            //移入单号
            String instockOrderNumber = orderNumber.replace(MaterialOrderPrefix.OUT_STOCK_PRE, MaterialOrderPrefix.IN_STOCK_PRE);

            // 保存移出库信息
            stockOrderDto.setAssociatedNumber(instockOrderNumber);
            stockOrderDto.setOperationType(MaterialOperateType.OUT_STOCK);
            stockMaterialOperate(stockOrderDto, operateMaterialList);

            Long  targetWarehouseId =  stockOrderMaterialDto.getTargetWarehouseId();
            Long  targetAdminId = stockOrderMaterialDto.getTargetAdminId();
            stockOrderDto.setAssociatedNumber(orderNumber);
            instockMaterial(stockOrderDto, instockOrderNumber,targetWarehouseId, targetAdminId,operateMaterialList);
            stockMaterialOperate(stockOrderDto, operateMaterialList);
        }else{
            stockMaterialOperate(stockOrderDto, operateMaterialList);
        }
        return  orderNumber;
    }

    public void instockMaterial(StockOrderDto stockOrderDto,String instockOrderNumber,  Long  targetWarehouseId, Long  targetAdminId, List<OrderMaterialDto> operateDMaterialList){

        //移入库单操作
        stockOrderDto.setOrderNumber(instockOrderNumber);
        stockOrderDto.setWarehouseId(targetWarehouseId);
        stockOrderDto.setAdminId(targetAdminId);
        stockOrderDto.setOperationType(MaterialOperateType.IN_STOCK);

        List<OrderMaterialDto> orderMaterialDtoList = new ArrayList<>();

        //对于移入物资的更改操作
        for(OrderMaterialDto orderMaterialDto: operateDMaterialList){
           // 原仓库里信息
            Long stockMaterilId = orderMaterialDto.getStockMaterialId();
            StockMaterial stockMaterial = stockMaterialService.findById(stockMaterilId).orElse(null);
            if(null == stockMaterial){
                continue;
            }

            //根据基础物资baseMaterial 判断目标仓库中是否有这种物质，无则新建但数据量为0，有则获取对应Id
            List<StockMaterial> stockMaterials = stockMaterialRepository.findByBaseMaterialIdAndWarehouseId(stockMaterial.getBaseMaterialId(),targetWarehouseId);

            if(!stockMaterials.isEmpty()){
                stockMaterilId = stockMaterials.get(0).getId();
            }else{
                StockMaterial targetStockMaterial = new StockMaterial();
                BeanUtils.copyProperties(stockMaterial, targetStockMaterial);
                targetStockMaterial.setLockAmount(0);
                targetStockMaterial.setValidAmount(0);
                targetStockMaterial.setWarehouseId(targetWarehouseId);
                targetStockMaterial.setId(null);
                stockMaterilId = stockMaterialRepository.saveAndFlush(targetStockMaterial).getId();

            }
            orderMaterialDto.setStockMaterialId(stockMaterilId);

            List<MaterialDetailDto>   materialDetailDtoList = orderMaterialDto.getMaterialDetailList();//物资详情
            for(MaterialDetailDto materialDetailDto: materialDetailDtoList){
                materialDetailDto.setStockMaterialId(stockMaterilId);
            }
        }

    }



    public void stockMaterialOperate(StockOrderDto stockOrderDto, List<OrderMaterialDto> operatedMaterialList){

        MaterialOperateType materialOperateType = stockOrderDto.getOperationType();
        // 1、保存订单
        StockOrder stockOrder = ((StockOrder)stockOrderRep.save(this.toEntity(stockOrderDto)));

        for(OrderMaterialDto orderMaterialDto: operatedMaterialList){
            Long stockMaterialId = orderMaterialDto.getStockMaterialId();
            StockMaterial stockMaterial = stockMaterialService.findById(stockMaterialId).orElse(null);

            //2、更新库存物资
            if (null == stockMaterial){
                continue;
            }

            float[] amount = changeAmount(stockMaterial.getValidAmount(),stockMaterial.getLockAmount(),orderMaterialDto.getAmount(),materialOperateType);
            stockMaterial.setValidAmount(amount[0]);
            stockMaterial.setLockAmount(amount[1]);
            stockMaterialService.update(stockMaterial);

            // 3、保存物资与订单关系
            materialAndOrderService.saveOrderMaterial(orderMaterialDto, stockOrder);
            if(MaterialOperateType.MATERIAL_RESERVE == materialOperateType){ //预定操作只针对库存物资进行操作，不对物资详情进行操作
                continue;
            }

            List<MaterialDetailDto>   materialDetailDtoList = orderMaterialDto.getMaterialDetailList();//物资详情
            if(materialDetailDtoList != null){

                for(MaterialDetailDto materialDetailDto: materialDetailDtoList){
                    // 4、保存或者更新物资详情
                    Long id = materialDetailDto.getId();
                    if((MaterialOperateType.MATERIAL_IN ==  materialOperateType) || (MaterialOperateType.IN_STOCK == materialOperateType) ) {//入库
                        id = materialDetailService.saveDto(materialDetailDto);
                    }
                    else{
                        MaterialDetail materialDetail = materialDetailService.findById(id).orElse(null);
                        if(null == materialDetail){
                            continue;
                        }
                        float[] detailAmount = changeAmount(materialDetail.getValidAmount(),0, materialDetailDto.getValidAmount(), materialOperateType);
                        materialDetail.setValidAmount(detailAmount[0]);
                        materialDetailService.update(materialDetail);
                    }

                    materialDetailDto.setId(id);
                    // 5、物资详情与记录
                    materialAndOrderService.saveOrderMaterialDetail(materialDetailDto, stockOrder);

                }
            }
        }

    }


    private float[] changeAmount(float validAmount, float lockAmount, float amount, MaterialOperateType materialOperateType) {
        float[] result = {validAmount, lockAmount};
        if ((MaterialOperateType.MATERIAL_OUT == materialOperateType)
                || (MaterialOperateType.OUT_STOCK == materialOperateType)) {
            result[0] -= amount;
        } else if (MaterialOperateType.MATERIAL_RESERVE == materialOperateType) {
            result[1] += amount;
        } else if (MaterialOperateType.MATERIAL_RESERVE_OUT == materialOperateType) {
            result[0] -= amount;
            result[1] -= amount;
        } else if (MaterialOperateType.MATERIAL_CHECK == materialOperateType) {
            result[0] = amount;
        } else {
            result[0] += amount;
        }
        if((Float.compare(result[0], 0) < 0) ){
            throw  new BusinessException(ErrorEnum.LOW_STOCK);
        }

        return result;
    }


    //构造订单编号
    private String genenerateOrderNumber(MaterialOperateType materialOperateType) {
        String orderNumber = MaterialOrderPrefix.OUT_STOCK_PRE;

        switch (materialOperateType) {
            case MATERIAL_CHECK:
                orderNumber = MaterialOrderPrefix.MATERIAL_CHECK_PRE;
                break;
            case MATERIAL_RESERVE_OUT:
                orderNumber = MaterialOrderPrefix.MATERIAL_RESERVE_OUT_PRE;
                break;
            case MATERIAL_RESERVE:
                orderNumber = MaterialOrderPrefix.MATERIAL_RESERVE_PRE;
                break;
            case MATERIAL_BACK:
                orderNumber = MaterialOrderPrefix.MATERIAL_BACK_PRE;
                break;
            case OUT_STOCK:
                orderNumber = MaterialOrderPrefix.OUT_STOCK_PRE;
                break;
            case MATERIAL_IN:
                orderNumber = MaterialOrderPrefix.MATERIAL_IN_PRE;
                break;
            case MATERIAL_OUT:
                orderNumber = MaterialOrderPrefix.MATERIAL_OUT_PRE;
                break;
            case MOVE_STOCK:
                break;
            case IN_STOCK:
                orderNumber = MaterialOrderPrefix.IN_STOCK_PRE;
                break;
            default:
                return null;
        }


        orderNumber += Calendar.getInstance().getTimeInMillis();
        return orderNumber;
    }

}
