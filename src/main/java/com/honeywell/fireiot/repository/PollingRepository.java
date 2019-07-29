package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.Polling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PollingRepository extends JpaRepository<Polling, Long>, JpaSpecificationExecutor<Polling> {

    @Query("update Polling pl set pl.activated =:activated where pl.id=:id")
    @Transactional
    @Modifying
    int updateActivated(@Param("activated") Boolean activated, @Param("id") Long id);


    Polling findByName(@Param("name") String Name);
}
