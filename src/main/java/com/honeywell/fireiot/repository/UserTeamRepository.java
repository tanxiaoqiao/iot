package com.honeywell.fireiot.repository;


import com.honeywell.fireiot.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Integer> {

    @Query("from UserTeam u where u.status =0")
    List<UserTeam> findAllByStatus();

}
