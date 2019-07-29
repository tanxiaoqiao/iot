package com.honeywell.fireiot.controller;


import com.honeywell.fireiot.annotation.JpaPage;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.TemplateContentDto;
import com.honeywell.fireiot.dto.TemplateDto;
import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.entity.Template;
import com.honeywell.fireiot.service.InspectContentService;
import com.honeywell.fireiot.service.TemplateService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName TemplateController
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/9 13:12
 */

@RestController
@RequestMapping("/api/template")
@Api(tags = "巡查模版")
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private InspectContentService inspectContentService;

    @GetMapping("/queryByPage")
    @JpaPage
    @ApiOperation(value ="分页查询模板数据")
    public ResponseObject queryByPage() {
       Pagination<Template> pagination =  templateService.findPage(JpaUtils.getSpecification());
        return ResponseObject.success(pagination);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存模板")
    public ResponseObject saveTemplate(@RequestBody TemplateDto templateDto) {
       long id =  templateService.save(templateDto);
       if(id == 0){
           return ResponseObject.fail(ErrorEnum.REPEAT_MODEL);
       } else {
           return ResponseObject.success(id);
       }
    }
    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除模板信息")
    public ResponseObject deleteTemplate(@PathVariable("id") long id) {
        templateService.delete(id);
        return ResponseObject.success(null);
    }
    @PostMapping("/saveContents")
    @ApiOperation(value="新增巡检内容（针对某一特定模版）")
    public ResponseObject saveContents(@RequestBody TemplateContentDto templateContentDto){
        templateService.saveContents(templateContentDto);
        return ResponseObject.success(null);
    }
    @GetMapping("/contents")
    @ApiOperation(value="查询模版内的巡检内容列表,分页条件查询")
    @JpaPage
    public ResponseObject getContents(@RequestParam("id") long id,@RequestParam("pi") int pi,@RequestParam("ps") int ps){
        List<Long> ids = templateService.getContentsById(id);
        if(ids.size() == 0){
            return ResponseObject.fail(ErrorEnum.INSPECT_CONTENT_NULL);
        }else {
            Pageable pageable = PageRequest.of(pi-1,ps);
            Page<InspectionContent> pageList = inspectContentService.findAllByElementIdIn(ids,pageable);
            Pagination<InspectionContent> page = new Pagination<InspectionContent>((int) pageList.getTotalElements(), pageList.getContent());
            return ResponseObject.success(page);
        }
    }
    @DeleteMapping("/contents")
    @ApiOperation(value ="模版内单个巡检内容删减")
    public ResponseObject deleteContent(@RequestParam("templateId") long templateId, @RequestParam("formId") long formId){
        templateService.deleteContents(templateId,formId);
        return ResponseObject.success(null);
    }

}
