package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName WorkTaskRepository
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/10 13:39
 */
@Repository
public interface WorkTaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    void deleteAllById(long id);

    /**
     *
     * @param taskIds
     * @param pageable
     * @return
     */
    @Query(value = "select t from Task t where t.id in (:taskIds)",countQuery = "select count(t) from Task t where t.id in (:taskIds)")
    Page<Task> findAllByTaskIdIn(@Param("taskIds") List<Long> taskIds, @Param("pageable") Pageable pageable);

    /**
     *
     * @param taskIds
     * @param name
     * @param pageable
     * @return
     */
    @Query(value ="select  t from Task t where t.id in (:taskIds) and t.name like concat('%',:name,'%') ",countQuery = "select  t from Task t where t.id in (:taskIds) and t.name like concat('%',:name,'%') ")
    Page<Task> findAllByTaskIdInAndNameLike(@Param("taskIds") List<Long> taskIds,@Param("name") String name, @Param("pageable") Pageable pageable);

    /**
     *
     * @param taskIds
     * @param name
     * @return
     */
    @Query(value ="select  t from Task t where t.id in(:taskIds) and t.name like concat('%',:name,'%') ")
    List<Task> findByTaskIdInAndNameLike(@Param("taskIds") List<Long> taskIds,@Param("name") String name);

    /**
     *
     * @param taskIds
     * @return
     */
    @Query(value ="select  t from Task t where t.id in(:taskIds)")
    List<Task> findByTaskIdIn(@Param("taskIds")  List<Long> taskIds);
}
