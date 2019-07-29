package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;


/**
 * @ClassName LocationRepository
 * @Description TODO
 * @Author monica Z
 * @Date 12/4/2018 3:21 PM
 **/
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * 判断相同层次内是否存在重名
     * @param name
     * @return
     */
    @Query(value="select * from location l where l.name = ?1 and l.parent_id = ?2 and l.delete_time is  null ", nativeQuery = true)
    List<Location> isSame (String name,long parentId);

    @Query(value = "select * from location l where l.parent_id = ?1 and l.delete_time is null", nativeQuery = true)
    List<Location> findAllByParentId (long parentId);

    @Query(value = "select * from location l where l.id = ?1 and l.delete_time is null",nativeQuery = true)
    Location findAllById(long id);

    @Query(value = "select * from location l where l.id = ?1 and l.delete_time is null",nativeQuery = true)
    Location findNameById(long id);

    @Transactional(rollbackOn = Exception.class)
    @Modifying(clearAutomatically = true)
    @Query(value = "update location  set delete_time = current_timestamp  where id = ?1 ",nativeQuery = true)
    void  updateDeleteById(long id);

    @Query(value = "select * from location l where l.name = ?1 and l.level =?2  and  l.delete_time is null",nativeQuery = true)
    Location findAllByNameAndLevel(String name,Integer level);

    @Query(value = "select * from location l where l.name = ?1 and l.level =?2  and l.parent_id =?3 and  l.delete_time is null",nativeQuery = true)
    Location findListByNameAndLevelAndParentId(String name,Integer level,long parentId);

    List<Location> findByFullName(String fullName);

    @Query(value = "select entity.id from Location entity where entity.fullName = ?1")
    Long findIdByFullName(String locationName);

    @Query(value = "select entity.fullName from Location entity where entity.id = ?1")
    String findFullNameById(Long id);

    @Query(value = "select entity.id from Location entity where entity.fullName = ?1 and entity.parentId = ?2")
    Long findIdByNameAndParentId(String locationName, Long parentId);


    // 4/10 加入模糊查询
    @Query(value = "select entity.id from Location entity where entity.fullName like ?1 ")
    Long findIdByBuildingAndFloor(String name);

    //
    @Query(value = "select entity.id from Location entity where entity.name = ?1 ")
    Long findIdByBuilding(String name);

    // 根据ID更新locationMap
    @Query(value = " update location set location_map_id = ?1 where id = ?2 " ,nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackOn = Exception.class)
    void updateLocationMapById(Long locationMapId,Long id);

    List<Location> findAllByIdIn(List<Long> ids);

}
