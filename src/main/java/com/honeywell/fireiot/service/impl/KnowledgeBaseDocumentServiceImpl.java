package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.OperatingType;
import com.honeywell.fireiot.dto.DocumentOperatingRecordDto;
import com.honeywell.fireiot.dto.KnowledgeBaseDocumentDto;
import com.honeywell.fireiot.entity.KnowledgeBaseCatalog;
import com.honeywell.fireiot.entity.KnowledgeBaseDocument;
import com.honeywell.fireiot.entity.KnowledgeBaseDocumentOperatingRecord;
import com.honeywell.fireiot.repository.KnowledgeBaseCatalogRepository;
import com.honeywell.fireiot.repository.KnowledgeBaseDocumentOperatingRecordRepository;
import com.honeywell.fireiot.repository.KnowledgeBaseDocumentRepository;
import com.honeywell.fireiot.service.KnowledgeBaseDocumentService;
import com.honeywell.fireiot.utils.JpaUtils;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeBaseDocumentServiceImpl implements KnowledgeBaseDocumentService {
    @Autowired
    KnowledgeBaseDocumentRepository knowledgeBaseDocumentRepository;
    @Autowired
    KnowledgeBaseCatalogRepository knowledgeBaseCatalogRepository;
    @Autowired
    KnowledgeBaseDocumentOperatingRecordRepository knowledgeBaseDocumentOperatingRecordRepository;
    @Override
    public void save(KnowledgeBaseDocumentDto knowledgeBaseDocumentDto) {
        KnowledgeBaseDocument knowledgeBaseDocument = new KnowledgeBaseDocument();
        dto2Entity(knowledgeBaseDocumentDto,knowledgeBaseDocument, OperatingType.OP_ADD);
        knowledgeBaseDocumentRepository.save(knowledgeBaseDocument);


    }

    @Override
    public KnowledgeBaseDocumentDto findById(Long id) {
        Optional<KnowledgeBaseDocument> documentOpt =  knowledgeBaseDocumentRepository.findById(id);
        if(documentOpt.isPresent()){
            KnowledgeBaseDocumentDto knowledgeBaseDocumentDto = new KnowledgeBaseDocumentDto();
            KnowledgeBaseDocument entity = documentOpt.get();
            entity2Dto(entity,knowledgeBaseDocumentDto);
            return knowledgeBaseDocumentDto;
        }
        return null;
    }

    @Override
    public boolean update(KnowledgeBaseDocumentDto knowledgeBaseDocumentDto) {
        Optional<KnowledgeBaseDocument> documentOpt =  knowledgeBaseDocumentRepository.findById(knowledgeBaseDocumentDto.getId());
        if(documentOpt.isPresent()){
            KnowledgeBaseDocument entity = documentOpt.get();
            dto2Entity(knowledgeBaseDocumentDto,entity,OperatingType.OP_EDIT);
            knowledgeBaseDocumentRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(Long id) {
        knowledgeBaseDocumentRepository.deleteById(id);
    }

    @Override
    public void deleteById(Long[] ids) {
        for(Long id :ids){
            knowledgeBaseDocumentRepository.deleteById(id);
        }
    }

    @Override
    public List<KnowledgeBaseDocumentDto> findByCatalogId(Long id) {
        Optional<KnowledgeBaseCatalog> catalogOpt = knowledgeBaseCatalogRepository.findById(id);
        if(catalogOpt.isPresent()){
          List<KnowledgeBaseDocument>  entityList = knowledgeBaseDocumentRepository.findByKnowledgeBaseCatalog(catalogOpt.get());
            List<KnowledgeBaseDocumentDto> dtoList = getDocumentDtoList(entityList);
            return dtoList;
        }
        return null;
    }

    @Override
    public Pagination<KnowledgeBaseDocumentDto> findPage(Specification<KnowledgeBaseDocument> specification) {
        Page<KnowledgeBaseDocument> page = knowledgeBaseDocumentRepository.findAll(specification, JpaUtils.getPageRequest());
        List<KnowledgeBaseDocument> entityList =  page.getContent();
        List<KnowledgeBaseDocumentDto> dtoList = getDocumentDtoList(entityList);
        return new Pagination<>((int) page.getTotalElements(), dtoList);
    }

    private List<KnowledgeBaseDocumentDto> getDocumentDtoList(List<KnowledgeBaseDocument> entityList) {
        List<KnowledgeBaseDocumentDto> dtoList = new ArrayList<>();
        for (KnowledgeBaseDocument entity : entityList) {
            KnowledgeBaseDocumentDto dto = new KnowledgeBaseDocumentDto();
            entity2Dto(entity, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    private void dto2Entity(KnowledgeBaseDocumentDto dto,KnowledgeBaseDocument entity,String operatingType){
       BeanUtils.copyProperties(dto,entity);
       if(dto.getCatalogId()!=null){
           Optional<KnowledgeBaseCatalog> catalogOpt = knowledgeBaseCatalogRepository.findById(dto.getCatalogId());
           if(catalogOpt.isPresent()){
               entity.setKnowledgeBaseCatalog(catalogOpt.get());
           }
       }
        KnowledgeBaseDocumentOperatingRecord knowledgeBaseDocumentOperatingRecord = new  KnowledgeBaseDocumentOperatingRecord();
        knowledgeBaseDocumentOperatingRecord.setUserId(dto.getUserId());
        knowledgeBaseDocumentOperatingRecord.setUserName(dto.getUserName());
        knowledgeBaseDocumentOperatingRecord.setOperatingType(operatingType);
        knowledgeBaseDocumentOperatingRecord.setOperatingTime(new Date());
        knowledgeBaseDocumentOperatingRecord.setKnowledgeBaseDocument(entity);
        if(entity.getOperatingRecordList()==null){
            List<KnowledgeBaseDocumentOperatingRecord> operatingRecordList = new ArrayList<>();
            operatingRecordList.add(knowledgeBaseDocumentOperatingRecord);
            entity.setOperatingRecordList(operatingRecordList);
        }else{
            entity.getOperatingRecordList().add(knowledgeBaseDocumentOperatingRecord);
        }
   }

   private void entity2Dto(KnowledgeBaseDocument entity,KnowledgeBaseDocumentDto dto){
       BeanUtils.copyProperties(entity,dto);
       dto.setCatalogId(entity.getKnowledgeBaseCatalog().getId());
       if(entity.getOperatingRecordList()!=null){
           List<DocumentOperatingRecordDto> operatingRecordDtoList = new ArrayList<>();
           for(KnowledgeBaseDocumentOperatingRecord knowledgeBaseDocumentOperatingRecord:entity.getOperatingRecordList()){
               DocumentOperatingRecordDto documentOperatingRecordDto = new DocumentOperatingRecordDto();
               BeanUtils.copyProperties(knowledgeBaseDocumentOperatingRecord,documentOperatingRecordDto);
               operatingRecordDtoList.add(documentOperatingRecordDto);
           }
           dto.setOperatingRecordDtoList(operatingRecordDtoList);
       }
   }
}
