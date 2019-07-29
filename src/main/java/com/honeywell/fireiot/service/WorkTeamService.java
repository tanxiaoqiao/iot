package com.honeywell.fireiot.service;


import com.honeywell.fireiot.dto.TeamDto;
import com.honeywell.fireiot.dto.WorkTeamDto;
import com.honeywell.fireiot.entity.WorkTeam;
import com.honeywell.fireiot.utils.Pagination;

import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
public interface WorkTeamService {

    void addUserTeam(WorkTeam workTeam);

    void deleteUserTeamById(String id);

    void updateUserTeam(WorkTeam workTeam);

    List<WorkTeam> findAllUserTeam();

    Pagination<WorkTeamDto> findUserPage(Integer pi, Integer ps);

    WorkTeamDto findOne(String id);

    TeamDto findTeamDtoOne(String id);

    /**
     * 提供给员工添加工作组接口 0员工 1主管
     *
     * @param empId
     * @param type
     */
    void updateWorkTeamByEmp(String workteamId, String empId, Integer type);

}
