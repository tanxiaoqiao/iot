package com.honeywell.fireiot.water.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author: xiaomingCao
 * @date: 2019/1/2
 */
public class WaterPageUtil {

    public static Pageable buildPageRequest(final int page,final int pageSize,final String sortField,final String sortType) {

        Sort sort = null;



        if ("DESC".equalsIgnoreCase(sortType)) {
            sort = new Sort(Sort.Direction.DESC, sortField);
        }

        if ("ASC".equalsIgnoreCase(sortField)) {
            sort = new Sort(Sort.Direction.ASC, sortField);
        }

        if (null == sort) {
            throw new RuntimeException("不支持的排序方式");
        }

        int pageNumber = page - 1;

        if (0 > pageNumber) {
            throw new RuntimeException("分页参数有误");
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }

}
