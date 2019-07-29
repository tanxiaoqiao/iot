package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @InterfaceName TaskRepository
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-02-19 09:42
 */
@Repository
public interface TaskRepository  extends JpaRepository<Task,Long>, JpaSpecificationExecutor<Task> {
}
