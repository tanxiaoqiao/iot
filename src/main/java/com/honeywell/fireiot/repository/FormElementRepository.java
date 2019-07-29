package com.honeywell.fireiot.repository;



import com.honeywell.fireiot.entity.FormElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FormElementRepository extends JpaRepository<FormElement, Long>, JpaSpecificationExecutor<FormElement> {

}
