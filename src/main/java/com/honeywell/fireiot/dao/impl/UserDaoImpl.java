package com.honeywell.fireiot.dao.impl;


import com.honeywell.fireiot.dao.UserDao;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.dto.UserSearch;
import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.repository.RoleRepository;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/13/2018 3:27 PM
 */
@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    RoleRepository roleRepository;


    @Override
    public Pagination<User> findUser(UserSearch userSearch) {
        //构建Query，注意用的是HQL语法
        int resource = EnvHolder.getHolder().getResource();
        String sql = " from User t where status!=2 and resource=" + resource;
        if (!StringUtils.isEmpty(userSearch.getUsername())) {
            sql += " and t.username like '%" + userSearch.getUsername() + "%'";
        }
        if (!StringUtils.isEmpty(userSearch.getName())) {
            sql += " and t.name like '%" + userSearch.getName() + "%'";
        }
        sql += "order by id desc";
        //第一次查询获得count
        Query q = entityManager.createQuery(sql);
        int size = 0;
        if (!CollectionUtils.isEmpty(q.getResultList())) {
            size = q.getResultList().size();
        }
        //第二次查询获得data
        Query q2 = entityManager.createQuery(sql);
        q2.setFirstResult(userSearch.getPi() <= 0 ? 0 : (userSearch.getPi() - 1) * userSearch.getPs());
        q2.setMaxResults(userSearch.getPs());
        List list = q2.getResultList();
        Pagination<User> page = new Pagination<>();
        page.setTotalCount(size);
        page.setDataList(list);
        return page;
    }

    @Override
    public void update(UserDto userDto) {
        User u = entityManager.find(User.class, userDto.getId());
        if (!StringUtils.isEmpty(userDto.getName())) {
            u.setName(userDto.getName());
        }
        if (!StringUtils.isEmpty(userDto.getEmail())) {
            u.setEmail(userDto.getEmail());
        }
        if (!StringUtils.isEmpty(userDto.getStatus())) {
            u.setStatus(userDto.getStatus());
        }
        if (!StringUtils.isEmpty(userDto.getPhone())) {
            u.setPhone(userDto.getPhone());
        }

        if (!StringUtils.isEmpty(userDto.getLanguage())) {
            u.setLanguage(userDto.getLanguage());
        }

        if (userDto.getRoleIds() != null && userDto.getRoleIds().length > 0) {
            List<Role> list = Arrays.asList(userDto.getRoleIds())
                    .stream()
                    .map(i -> {
                        return roleRepository.findById(i).get();
                    })
                    .collect(Collectors.toList());
            u.setRoles(list);
        } else {
            u.setRoles(null);
        }
        entityManager.merge(u);

    }
}
