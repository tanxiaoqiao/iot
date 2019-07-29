package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot,Long> ,  JpaSpecificationExecutor<Spot> {


    @Query(value = "select s from Spot s where s.id =:id")
    Spot findSpotById(@Param("id") long id);

    /**
     *
     * @param name
     * @param locationIds
     * @return
     */


    @Query(value = "select s from Spot s where s.name like concat('%',:name,'%') and  s.locationId in :locationIds")
    List<Spot> findAllByNameAndLocationId(@Param("name") String name,@Param("locationIds") List<Long> locationIds);

    /**
     * 根据点位名称查询
     * @param name
     * @return
     */

    @Query(value = "select s from Spot s where s.name like concat('%',:name,'%') ")
    List<Spot> findAllByNameLike(@Param("name") String name);

    /**
     * 根据安装位置查询
     * @param locationIds
     * @return
     */

    @Query(value = "select s from Spot s where s.locationId in :locationIds")

    List<Spot> findAllByLocationIdIn(@Param("locationIds") List<Long> locationIds);
}
