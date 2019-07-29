package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.SpotAndTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @ClassName SpotAndTaskRepository
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/10 13:27
 */
@Repository
public interface SpotAndTaskRepository  extends JpaRepository<SpotAndTask, Long> {

    @Query(value = "select st from SpotAndTask st where st.spotId = ?1")
    List<SpotAndTask> findAllBySpotId(long spotId);


    @Query(value = "select st.taskId from SpotAndTask st where st.spotId = ?1")

    List<Long> findIdBySpotId(List<Long> spotId);

    @Transactional(rollbackOn = Exception.class)
    @Modifying
    @Query(value = "delete from SpotAndTask st where st.spotId = ?1 ")
    void deleteAllBySpotId(long spotId);



    @Query(value = "select count(*) from SpotAndTask st where to_char(st.createTime,'YYYY')=?1")
    int findAllByYear(String year);

}


