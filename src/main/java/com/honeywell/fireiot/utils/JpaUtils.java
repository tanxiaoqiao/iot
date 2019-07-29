package com.honeywell.fireiot.utils;

import com.honeywell.fireiot.constant.DBOperator;
import com.honeywell.fireiot.constant.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JPA工具方法
 *
 * @Author: zhenzhong.wang
 * @Date: 8/24/2018 9:55 AM
 */
@Component
public class JpaUtils {

    private static EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        JpaUtils.entityManager = entityManager;
    }

    /**
     * 执行动态更新，可自动忽略实体中值为NULL的字段
     *
     * @param entity          更新的实体
     * @param whereParameters 条件参数，格式为：key,value,key,value... 长度必须为复数
     *                        注：多条件之间关系默认为and，不支持and和or混用
     * @return
     */
    public static boolean dynamicUpdate(Object entity, DBOperator operator, Object... whereParameters) {
        Assert.notNull(entity, "动态更新异常：实体不可为空！");
        Assert.notEmpty(whereParameters, "动态更新异常：条件参数不可为空！");
        Assert.isTrue(whereParameters.length % 2 == 0, "where条件参数不匹配！");
        if (operator == null) {
            operator = DBOperator.AND;
        }
        // 获取对象参数
        String entityName = entity.getClass().getName().substring(entity.getClass().getName().lastIndexOf(".") + 1);
        Map<String, Object> fieldsMap = reflectFieldsExcludeNull(entity);

        // 构建queryString
        String hql;
        String setStr;
        String whereStr = "";
        int paramNumber = 1;

        // 处理set字符串
        StringBuffer setBuf = new StringBuffer();
        for (Map.Entry<String, Object> field : fieldsMap.entrySet()) {
            setBuf.append(" entity.").append(field.getKey());
            setBuf.append(" = ?").append(paramNumber++).append(",");
        }
        setStr = setBuf.substring(0, setBuf.length() - 1);
        ArrayList<Object> values = new ArrayList<>(fieldsMap.values());
        // 处理条件字符串
        for (int i = 0; i < whereParameters.length; i = i + 2) {
            whereStr += "entity." + whereParameters[i] + " = ?" + paramNumber++
                    + " " + operator + " ";
            values.add(whereParameters[i + 1]);
        }

        hql = "update " + entityName + " entity set " + setStr + " where "
                + whereStr.substring(0, whereStr.lastIndexOf("and"));

        return executeUpdate(hql, values.toArray());
    }

    /**
     * 反射获取实体的成员，过滤值为Null的字段
     *
     * @param entity
     * @return
     */
    public static Map<String, Object> reflectFieldsExcludeNull(Object entity) {
        // 解析对象参数，获取key,value集合
        Map<String, Object> fieldsMap = new LinkedHashMap<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                // 忽略值为null的属性
                if (field.get(entity) != null) {
                    fieldsMap.put(field.getName(), field.get(entity));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fieldsMap;
    }

    /**
     * 执行hql查询
     *
     * @param hql
     * @param parameters
     * @return
     */
    private static boolean executeUpdate(String hql, Object[] parameters) {
        Query query = entityManager.createQuery(hql);
        int position = 1;
        for (Object param : parameters) {
            query.setParameter(position++, param);
        }
        return query.executeUpdate() == 1 ? true : false;
    }

    /**
     * 获取PageRequest请求
     *
     * @return
     */
    public static Pageable getPageRequest() {
        PageSearch pageSearch = PageHolder.getHolder();
        PageRequest pageRequest = PageRequest.of(pageSearch.getPi(), pageSearch.getPs(),
                Sort.Direction.valueOf(pageSearch.getOrderType().toString()), pageSearch.getOrderColumn());
        return pageRequest;
    }

    /**
     * 获取where条件语句
     *
     * @return
     */
    public static String getWhereString() {
        PageSearch pageSearch = PageHolder.getHolder();
        Map<String, Object> pageParameters = pageSearch.getPageParameter();
        String where = "";
        for (Map.Entry<String, Object> entry : pageParameters.entrySet()) {
            where += entry.getKey() + " like %" + entry.getValue() + "% and ";
        }
        if (where.indexOf("and") > 0) {
            where = where.substring(0, where.lastIndexOf("and"));
        }
        return where;
    }

    /**
     * 封装查询条件
     *
     * @return
     */
    public static Specification getSpecification() {
        Specification specification = (Specification) (root, query, criteriaBuilder) -> {
            PageSearch pageSearch = PageHolder.getHolder();

            // 条件集合
            List<Predicate> predicates = new ArrayList<>();

            // 查
            if (pageSearch.getPageParameter() != null) {
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                for (Map.Entry<String, Object> entry : pageSearch.getPageParameter().entrySet()) {
                    Path p = getPath(root, entry.getKey());
                    if (p.getJavaType() == String.class) {
                        predicates.add(criteriaBuilder.like(p, "%" + entry.getValue() + "%"));
                    } else {
                        predicates.add(criteriaBuilder.equal(p, entry.getValue()));
                    }
                }
            }

            // 筛选条件
            if (pageSearch.getFilterParameter() != null) {
                for (Map.Entry<String, Object[]> entry : pageSearch.getFilterParameter().entrySet()) {
                    Path p = getPath(root, entry.getKey());
                    if (p.getJavaType() == String.class) {
                        for (Object filterValue : entry.getValue()) {
                            predicates.add(criteriaBuilder.like(p, "%" + filterValue + "%"));
                        }
                    } else {
                        for (Object filterValue : entry.getValue()) {
                            predicates.add(criteriaBuilder.equal(p, filterValue));
                        }
                    }
                }
            }


            if (pageSearch.getRangeMaxParameter() != null) {
                for (Map.Entry<String, Object> entry : pageSearch.getRangeMaxParameter().entrySet()) {
                    Path p = getPath(root, entry.getKey());

                    if (entry.getValue().getClass() == String.class) {
                        String value = (String) entry.getValue();
                        if (p.getJavaType() == long.class || p.getJavaType() == Long.class) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(p, Long.parseLong(value)));
                        } else if (p.getJavaType() == int.class || p.getJavaType() == Integer.class) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(p, Integer.parseInt(value)));
                        } else if (p.getJavaType() == float.class || p.getJavaType() == Float.class) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(p, Float.parseFloat(value)));
                        } else if (p.getJavaType() == double.class || p.getJavaType() == Double.class) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(p, Double.parseDouble(value)));
                        } else if (p.getJavaType() == Timestamp.class) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(p, Timestamp.valueOf(value)));
                        }
                    }
                }
            }

            if (pageSearch.getRangeMinParameter() != null) {
                for (Map.Entry<String, Object> entry : pageSearch.getRangeMinParameter().entrySet()) {
                    Path p = getPath(root, entry.getKey());
                    if (entry.getValue().getClass() == String.class) {
                        String value = (String) entry.getValue();
                        if (p.getJavaType() == long.class || p.getJavaType() == Long.class) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(p, Long.parseLong(value)));
                        } else if (p.getJavaType() == int.class || p.getJavaType() == Integer.class) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(p, Integer.parseInt(value)));
                        } else if (p.getJavaType() == float.class || p.getJavaType() == Float.class) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(p, Float.parseFloat(value)));
                        } else if (p.getJavaType() == double.class || p.getJavaType() == Double.class) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(p, Double.parseDouble(value)));
                        } else if (p.getJavaType() == Timestamp.class) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(p, Timestamp.valueOf(value)));
                        }
                    }
                }
            }

            // 排序
            if (pageSearch.getOrderColumn() != null) {
                if (OrderType.ASC.equals(pageSearch.getOrderType().toString().toUpperCase())) {
                    query = query.orderBy(criteriaBuilder.asc(root.get(pageSearch.getOrderColumn())));
                } else {
                    query = query.orderBy(criteriaBuilder.desc(root.get(pageSearch.getOrderColumn())));
                }
            }

            query.where(predicates.toArray(new Predicate[predicates.size()]));

            return query.getRestriction();
        };
        return specification;
    }

    private static Path getPath (Root root, String entryKey){
        String[] key = entryKey.split("\\.");
        Path p = null;
        for (String k : key) {
            if (p == null) {
                p = root.get(k);
            } else {
                p = p.get(k);
            }
        }
        return p;
    }
}