package com.honeywell.fireiot.repository;



import com.honeywell.fireiot.entity.FormValidator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FormValidatorRepository extends JpaRepository<FormValidator, Long>, JpaSpecificationExecutor<FormValidator> {

}
