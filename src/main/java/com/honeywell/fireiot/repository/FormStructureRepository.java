package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.FormStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FormStructureRepository extends JpaRepository<FormStructure, Long>, JpaSpecificationExecutor<FormStructure> {

    @Query(value = "select entity from FormStructure entity where entity.uuid = ?1")
    FormStructure findByUUID(String uuid);

}
