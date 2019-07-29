package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.KnowledgeBaseCatalogDto;
import com.honeywell.fireiot.service.KnowledgeBaseCatalogService;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledgeBaseCatalog")
@Api(tags = {"知识库分类"})
public class KnowledgeBaseCatalogController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseCatalogController.class);
    @Autowired
    KnowledgeBaseCatalogService knowledgeBaseCatalogService;

    @PostMapping
    @ApiOperation(value = "增加知识库分类") //方法描述
    public ResponseObject addCatalog(@ApiParam @Validated @RequestBody KnowledgeBaseCatalogDto knowledgeBaseCatalogDto) {
        LOGGER.info("addCatalog|KnowledgeBaseCatalogDto:{} ", knowledgeBaseCatalogDto);
        knowledgeBaseCatalogService.save(knowledgeBaseCatalogDto);
        return ResponseObject.success(null);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除知识库分类") //方法描述
    public ResponseObject deleteCatalog(@PathVariable("id") Long id) {
        LOGGER.info("deleteCatalog|id:{} ", id);
        //id号是否存在
        if (null == knowledgeBaseCatalogService.findById(id)) {
            return ResponseObject.fail(ErrorEnum.PARAMS_ERROR);
        }
        knowledgeBaseCatalogService.deleteById(id);

        return ResponseObject.success(null);
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "通过Id查找知识库分类") //方法描述
    public ResponseObject findCatalog(@PathVariable("id") Long id) {
        LOGGER.info("find|id:{} ", id);
        //id号是否存在
        KnowledgeBaseCatalogDto knowledgeBaseCatalogDto = knowledgeBaseCatalogService.findById(id);
        if (null == knowledgeBaseCatalogDto) {
            return ResponseObject.fail(ErrorEnum.CATALOG_NOT_EXIST);
        }
        return ResponseObject.success(knowledgeBaseCatalogDto);
    }

    @GetMapping
    @ApiOperation(value = "获取所有知识库分类") //方法描述
    public ResponseObject getAllDeviceSystems() {
        List<KnowledgeBaseCatalogDto> knowledgeBaseCatalogDtoList =knowledgeBaseCatalogService.findAll();
        return ResponseObject.success(knowledgeBaseCatalogDtoList);
    }


}
