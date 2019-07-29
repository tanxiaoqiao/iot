package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @InterfaceName TemplateRepository
 * @Description TODO
 * @Aauthor Monica Z
 * @Date 2019/1/9 13:21
 */
@Repository
public interface TemplateRepository  extends JpaRepository<Template,Long>, JpaSpecificationExecutor<Template> {
    void  deleteById(long id);
    Template findAllByName(String name);
}
