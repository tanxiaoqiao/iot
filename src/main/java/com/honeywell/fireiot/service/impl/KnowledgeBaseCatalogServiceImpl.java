package com.honeywell.fireiot.service.impl;

import com.honeywell.fireiot.constant.Permission;
import com.honeywell.fireiot.dto.KnowledgeBaseCatalogDto;
import com.honeywell.fireiot.entity.CatalogRoleRel;
import com.honeywell.fireiot.entity.KnowledgeBaseCatalog;
import com.honeywell.fireiot.repository.KnowledgeBaseCatalogRepository;
import com.honeywell.fireiot.service.KnowledgeBaseCatalogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeBaseCatalogServiceImpl implements KnowledgeBaseCatalogService {
    @Autowired
    KnowledgeBaseCatalogRepository knowledgeBaseCatalogRepository;
    @Override
    public void save(KnowledgeBaseCatalogDto knowledgeBaseCatalogDto) {
        KnowledgeBaseCatalog knowledgeBaseCatalog ;
        if(knowledgeBaseCatalogDto.getId()!=null){
            knowledgeBaseCatalog = knowledgeBaseCatalogRepository.getOne(knowledgeBaseCatalogDto.getId());
        }else{
            knowledgeBaseCatalog = new KnowledgeBaseCatalog();
        }
        dto2Entity(knowledgeBaseCatalogDto, knowledgeBaseCatalog);
        knowledgeBaseCatalogRepository.save(knowledgeBaseCatalog);
    }

    private void dto2Entity(KnowledgeBaseCatalogDto knowledgeBaseCatalogDto, KnowledgeBaseCatalog knowledgeBaseCatalog) {
        BeanUtils.copyProperties(knowledgeBaseCatalogDto,knowledgeBaseCatalog);
        knowledgeBaseCatalog.setFullName(knowledgeBaseCatalogDto.getName());
        if(knowledgeBaseCatalogDto.getParentId()!=null){
            Optional<KnowledgeBaseCatalog> parentOpt = knowledgeBaseCatalogRepository.findById(knowledgeBaseCatalogDto.getParentId());
            if (parentOpt.isPresent()) {
                KnowledgeBaseCatalog parent = parentOpt.get();
                knowledgeBaseCatalog.setParentCatalog(parent);
                knowledgeBaseCatalog.setFullName(parent.getFullName()+"-"+knowledgeBaseCatalog.getName());
            }
        }
        if (knowledgeBaseCatalog.getRoleRelList()==null){
            List<CatalogRoleRel> roleRelList = new ArrayList<>();
            knowledgeBaseCatalog.setRoleRelList(roleRelList);
        }
        knowledgeBaseCatalog.getRoleRelList().clear();
        if(knowledgeBaseCatalogDto.getAddRoleIds()!=null){
            for(Long roleId : knowledgeBaseCatalogDto.getAddRoleIds()){
                CatalogRoleRel catalogRoleRel = new CatalogRoleRel();
                catalogRoleRel.setRoleId(roleId);
                catalogRoleRel.setPermissionType(Permission.PERMISSION_ADD);
                catalogRoleRel.setKnowledgeBaseCatalog(knowledgeBaseCatalog);
                knowledgeBaseCatalog.getRoleRelList().add(catalogRoleRel);
            }
        }
        if(knowledgeBaseCatalogDto.getEditRoleIds()!=null){
            for(Long roleId : knowledgeBaseCatalogDto.getEditRoleIds()){
                CatalogRoleRel catalogRoleRel = new CatalogRoleRel();
                catalogRoleRel.setRoleId(roleId);
                catalogRoleRel.setPermissionType(Permission.PERMISSION_EDIT);
                catalogRoleRel.setKnowledgeBaseCatalog(knowledgeBaseCatalog);
                knowledgeBaseCatalog.getRoleRelList().add(catalogRoleRel);
            }
        }
        if(knowledgeBaseCatalogDto.getDeleteRoleIds()!=null){
            for(Long roleId : knowledgeBaseCatalogDto.getDeleteRoleIds()){
                CatalogRoleRel catalogRoleRel = new CatalogRoleRel();
                catalogRoleRel.setRoleId(roleId);
                catalogRoleRel.setPermissionType(Permission.PERMISSION_DELETE);
                catalogRoleRel.setKnowledgeBaseCatalog(knowledgeBaseCatalog);
                knowledgeBaseCatalog.getRoleRelList().add(catalogRoleRel);
            }
        }
    }

    @Override
    public void update(KnowledgeBaseCatalogDto knowledgeBaseCatalogDto) {
        Optional<KnowledgeBaseCatalog> entityOpt = knowledgeBaseCatalogRepository.findById(knowledgeBaseCatalogDto.getId());
       if(entityOpt.isPresent()){
           KnowledgeBaseCatalog knowledgeBaseCatalog = entityOpt.get();
           dto2Entity(knowledgeBaseCatalogDto, knowledgeBaseCatalog);
           knowledgeBaseCatalogRepository.save(knowledgeBaseCatalog);
       }
    }

    @Override
    public KnowledgeBaseCatalogDto findById(Long id) {
        Optional<KnowledgeBaseCatalog> entityOpt = knowledgeBaseCatalogRepository.findById(id);
        if (entityOpt.isPresent()) {
            KnowledgeBaseCatalogDto knowledgeBaseCatalogDto = new KnowledgeBaseCatalogDto();
            entity2Dto(knowledgeBaseCatalogDto,entityOpt.get());
            return knowledgeBaseCatalogDto;
        }
        return null;
    }

    @Override
    public List<KnowledgeBaseCatalogDto> findAll() {
        List<KnowledgeBaseCatalogDto> result = new ArrayList<>();
        List<KnowledgeBaseCatalog> allKnowledgeBaseCatalog =knowledgeBaseCatalogRepository.findByParentCatalog(null);
       for(KnowledgeBaseCatalog knowledgeBaseCatalog :allKnowledgeBaseCatalog){
           KnowledgeBaseCatalogDto knowledgeBaseCatalogDto = new KnowledgeBaseCatalogDto();
           entity2Dto(knowledgeBaseCatalogDto,knowledgeBaseCatalog);
           result.add(knowledgeBaseCatalogDto);
       }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        knowledgeBaseCatalogRepository.deleteById(id);
    }

    private void entity2Dto(KnowledgeBaseCatalogDto knowledgeBaseCatalogDto,KnowledgeBaseCatalog entity){
        BeanUtils.copyProperties(entity, knowledgeBaseCatalogDto);
        Long parentId = null;
        KnowledgeBaseCatalog parent = entity.getParentCatalog();
        if(parent!=null){
            parentId = parent.getId();
        }
        knowledgeBaseCatalogDto.setParentId(parentId);
        List<CatalogRoleRel> roleRelList = entity.getRoleRelList();
        List<Long> deleteRolesList= new ArrayList<>();
        List<Long> editRolesList= new ArrayList<>();
        List<Long> addRolesList= new ArrayList<>();
        if(roleRelList!=null){
            for(CatalogRoleRel catalogRoleRel :roleRelList){
                if(catalogRoleRel.getPermissionType().equals(Permission.PERMISSION_DELETE)){
                    deleteRolesList.add(catalogRoleRel.getRoleId());
                }
                if(catalogRoleRel.getPermissionType().equals(Permission.PERMISSION_ADD)){
                    addRolesList.add(catalogRoleRel.getRoleId());
                }
                if(catalogRoleRel.getPermissionType().equals(Permission.PERMISSION_EDIT)){
                    editRolesList.add(catalogRoleRel.getRoleId());
                }
            }
        }
        knowledgeBaseCatalogDto.setAddRoleIds(addRolesList);
        knowledgeBaseCatalogDto.setDeleteRoleIds(deleteRolesList);
        knowledgeBaseCatalogDto.setEditRoleIds(editRolesList);
        if(entity.getChildList()!=null){
            List<KnowledgeBaseCatalogDto> childList = new ArrayList<>();
            for(KnowledgeBaseCatalog child :entity.getChildList()){
                KnowledgeBaseCatalogDto childDto = new KnowledgeBaseCatalogDto();
                entity2Dto(childDto,child);
                childDto.setParentId(entity.getId());
                childList.add(childDto);
            }
            knowledgeBaseCatalogDto.setChildren(childList);
        }
    }
}
