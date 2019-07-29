package com.honeywell.fireiot.dto;

import com.honeywell.fireiot.entity.InspectionContent;
import com.honeywell.fireiot.entity.TaskAndDevice;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @ClassName TaskDetailDto
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-04-22 09:52
 */
@Data
public class TaskDetailDto {
    private List<InspectionContent> contents;
    private List<BusinessDeviceDto> deviceDtos;

    private Page<InspectionContent> inspectionContentPage;

    private Page<TaskAndDevice> taskAndDevicePage;
}
