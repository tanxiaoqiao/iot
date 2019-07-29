package com.honeywell.fireiot.service;

import com.honeywell.fireiot.dto.MaterialDto;
import com.honeywell.fireiot.dto.StockMaterialDto;
import com.honeywell.fireiot.entity.StockMaterial;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface StockMaterialService extends BaseService<StockMaterial> {

    StockMaterial toEntity(StockMaterialDto var1);

    StockMaterialDto toDto(StockMaterial var1);

    MaterialDto toMaterialDto(StockMaterial var1);

    Long saveMaterial(MaterialDto var1) throws IOException;

    Long saveDto(StockMaterialDto var1);

    boolean updateDto(StockMaterialDto var1);

    boolean update(StockMaterial var1);

    Pagination<MaterialDto> findMaterialPage(Specification<StockMaterial> var1);


    void exportQrPicture(List<Long> ids, HttpServletResponse response) throws IOException;

    List<StockMaterial> findLowAmountMaterial();
}
