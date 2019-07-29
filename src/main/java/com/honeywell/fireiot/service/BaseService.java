package com.honeywell.fireiot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

/**
 * service基类接口
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/10/22 10:51 AM
 */
public interface BaseService<T> {

    void save(T t);

    void delete(T t);

    void deleteById(Long id);

    Optional<T> findById(Long id);

    Page<T> findPage();

    Page<T> findPage(Specification<T> specification);

}
