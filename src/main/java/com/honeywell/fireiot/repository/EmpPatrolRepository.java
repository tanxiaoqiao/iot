package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.EmpPatrol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpPatrolRepository extends JpaRepository<EmpPatrol, Long> {

    List<EmpPatrol> findByEmployeeId(String employeeId);


}
