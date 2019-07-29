package com.honeywell.fireiot.repository;

import com.honeywell.fireiot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    //status =0 表示已经删除了的用户
    @Query(value = "select u from User u where u.status != ?1")
    @Modifying
    @Transactional
    List<User> findAllList(@Param("status") Integer status);

    @Query(value = "update User u set u.password=:password where u.id=:id")
    @Modifying
    @Transactional
    int updatePasswordById(@Param("password") String password, @Param("id") Long id);

    @Query(value = "update User u set u.status=:status where u.id=:id")
    @Modifying
    @Transactional
    int updateStatusById(@Param("status") Integer status, @Param("id") Long id);

    @Override
    <S extends User> S save(S entity);

    @Query(value = "from User entity where (entity.username = ?1 or entity.email = ?1) and entity.resource = ?2")
    User findByUsernameOrEmail(String username, Integer resource);

    User findByUserIdAndResource(String userId, Integer resource);

    User findByEidAndResource(String eid, Integer resource);

    List<User> findByEid(String eid);

    /**
     * 查询未被绑定员工的用户
     * @param bindUserList
     * @return
     */
    @Query(value = "select u from User u where u.id not in :bindUserList ")
    List<User> getUnbindUser(@Param("bindUserList") List<Long> bindUserList );

    List<User> findByUsername(String username);

}
