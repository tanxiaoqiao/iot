package com.honeywell.fireiot.service;



import com.honeywell.fireiot.dto.*;
import com.honeywell.fireiot.entity.Spot;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface SpotService {

     long save(SpotDto spotDto);
     void delete(long id);
     List<SpotDto> queryByPage(SpotDto spotDto);
     Spot getSpotById(long id);
     long insert(Spot spot);
     long update(Spot spot);
     List<SpotDto> findAll();

     Page<Spot> findPage();

     Pagination<Spot> findPage(Specification<Spot> specification);
//     Page<Spot> findPage(Specification<Spot> specification);

     List<SpotTaskDto> query(SpotTaskSearch spotTaskSearch);
     Boolean isExist(long id);
     List<SpotTaskShow>  findSpotContents(SpotTaskSearch spotTaskSearch);
     List<SpotTaskShow> findSpotContentsByPage(SpotTaskSearch spotTaskSearch, Pageable pageable);

     List<TaskDto> querySpotTask(SpotTaskSearch spotTaskSearch,Pageable pageable);



}
