package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.BaseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseMaterialRepository extends JpaRepository<BaseMaterial, Long>, JpaSpecificationExecutor<BaseMaterial> {
    List<BaseMaterial> findByCode(String code);

}
