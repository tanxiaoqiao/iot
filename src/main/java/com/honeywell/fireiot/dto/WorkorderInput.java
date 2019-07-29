package com.honeywell.fireiot.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: Kris
 * @Date: 2019-07-15  12:29
 */
@Data
public class WorkorderInput {
    List<Long> ids;
    List<String> acceptors;
    String workorderTeam;
}
