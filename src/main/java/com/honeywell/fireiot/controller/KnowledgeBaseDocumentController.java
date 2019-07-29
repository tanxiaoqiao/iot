package com.honeywell.fireiot.controller;
import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.KnowledgeBaseDocumentDto;
import com.honeywell.fireiot.service.KnowledgeBaseDocumentService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 知识库文档管理
 */
@RestController
@RequestMapping("/api/knowledgeBaseDocument")
@Api(tags = "知识库文档")
public class KnowledgeBaseDocumentController {
    @Autowired
    KnowledgeBaseDocumentService knowledgeBaseDocumentService;


    @GetMapping
    @JpaPage
    @ApiOperation(value = "分页查询")
    public ResponseObject<Pagination<KnowledgeBaseDocumentDto>> findPage() {
        Pagination<KnowledgeBaseDocumentDto> pagination  = knowledgeBaseDocumentService.findPage(JpaUtils.getSpecification());
        return ResponseObject.success(pagination);
    }

    @ApiOperation(value = "通过ID查找")
    @GetMapping("/{id}")
    public ResponseObject findOne(@PathVariable("id") Long id) {
        KnowledgeBaseDocumentDto result = knowledgeBaseDocumentService.findById(id);
        if (result!=null) {
            return ResponseObject.success(result);
        }else {
            return ResponseObject.fail(ErrorEnum.NOT_FOUND);
        }
    }

    @ApiOperation(value = "通过分类ID查找")
    @GetMapping("/byCatalog/{id}")
    public ResponseObject findByCatalog(@PathVariable("id") Long id) {
        List<KnowledgeBaseDocumentDto> result = knowledgeBaseDocumentService.findByCatalogId(id);
        if (result!=null) {
            return ResponseObject.success(result);
        }else {
            return ResponseObject.fail(ErrorEnum.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation(value = "保存实体")
    public ResponseObject save(@ApiParam @Validated @RequestBody KnowledgeBaseDocumentDto dto) {
        knowledgeBaseDocumentService.save(dto);
        return ResponseObject.success("OK");
    }

    @PatchMapping
    @ApiOperation(value = "部分更新")
    public ResponseObject patchUpdate(@ApiParam @RequestBody KnowledgeBaseDocumentDto dto) {
        boolean isUpdated = knowledgeBaseDocumentService.update(dto);
        if (isUpdated) {
            return ResponseObject.success("OK");
        }
        return ResponseObject.fail(ErrorEnum.UPDATE_ERROR);
    }

    @ApiOperation(value = "删除单个实体")
    @DeleteMapping("/{id}")
    public ResponseObject delete(@PathVariable("id") Long id) {
        knowledgeBaseDocumentService.deleteById(id);
        return ResponseObject.success("OK");
    }

    @ApiOperation(value = "删除多个实体")
    @DeleteMapping("patchDelete/{id}")
    public ResponseObject delete(@PathVariable("id") Long[] ids) {
        knowledgeBaseDocumentService.deleteById(ids);
        return ResponseObject.success("OK");
    }
}
