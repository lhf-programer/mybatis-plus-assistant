package com.lvhaifeng.mybatis.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.lvhaifeng.mybatis.constant.OrderTypeConstant;
import com.lvhaifeng.mybatis.utils.ConvertUtils;
import com.lvhaifeng.mybatis.utils.SqlInjectionUtil;
import com.lvhaifeng.mybatis.utils.StringUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 字段查询规则生成器
 * @Author haifeng.lv
 * @Date 2020/1/10 16:07
 */
public class QueryHelper {
    /**
     * @Description 获取查询条件构造器QueryWrapper实例 通用查询条件已被封装完成
     * @Author haifeng.lv
     * @param: searchObj
     * @param: sortProp
     * @param: sortType
     * @Date 2020/1/13 17:04
     * @return: com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>
     */
    public static <T> QueryWrapper<T> initQueryWrapper(T searchObj, String sortProp, String sortType) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        installField(queryWrapper, searchObj, sortProp, sortType);
        return queryWrapper;
    }

    /**
     * @Description 装载查询规则字段
     * @Author haifeng.lv
     * @param: queryWrapper
     * @param: searchObj
     * @param: sortProp
     * @param: sortType
     * @Date 2020/5/25 16:08
     */
    public static void installField(QueryWrapper<?> queryWrapper, Object searchObj, String sortProp, String sortType) {
        PropertyDescriptor[] originDescriptors = PropertyUtils.getPropertyDescriptors(searchObj);

        String name;
        for (int i = 0; i < originDescriptors.length; i++) {
            name = originDescriptors[i].getName();
            try {
                if (judgedIsUselessField(name) || !PropertyUtils.isReadable(searchObj, name)) {
                    continue;
                }

                Object value = PropertyUtils.getSimpleProperty(searchObj, name);
                if (null != value) {
                    final String field = ConvertUtils.camelToUnderline(name);
                    queryWrapper.like(field, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 排序逻辑 处理
        resolverSort(queryWrapper, sortProp, sortType);
    }

    /**
     * @Description 解析排序 (queryWrapper 包装字段查询规则使用的排序工具方法)
     * @Author haifeng.lv
     * @param: queryWrapper
     * @param: sortProp
     * @param: sortType
     * @Date 2020/5/25 16:10
     */
    public static void resolverSort(QueryWrapper<?> queryWrapper, String sortProp, String sortType) {
        if (StringUtils.isNotEmpty(sortProp) && StringUtils.isNotEmpty(sortType)) {
            String[] props = sortProp.split(",");
            String[] types = sortType.split(",");
            if (props.length == types.length) {
                for (int i = 0; i < props.length; i++) {
                    //SQL注入check
                    SqlInjectionUtil.filterContent(props[i]);

                    if (OrderTypeConstant.ORDER_TYPE_ASC.indexOf(types[i]) >= 0) {
                        queryWrapper.orderByAsc(ConvertUtils.camelToUnderline(props[i]));
                    } else {
                        queryWrapper.orderByDesc(ConvertUtils.camelToUnderline(props[i]));
                    }
                }
            }
        }
    }

    /**
     * @Description 解析排序 (page 分页使用的排序工具方法)
     * @Author haifeng.lv
     * @param: sortProp 字段
     * @param: sortType 类型
     * @Date 2020/1/14 11:33
     * @return: java.util.List<com.baomidou.mybatisplus.core.metadata.OrderItem>
     */
    public static List<OrderItem> resolverSort(String sortProp, String sortType) {
        List<OrderItem> orderItems = new ArrayList<>();

        if (StringUtils.isNotEmpty(sortProp) && StringUtils.isNotEmpty(sortType)) {
            String[] props = sortProp.split(",");
            String[] types = sortType.split(",");
            if (props.length == types.length) {
                for (int i = 0; i < props.length; i++) {
                    //SQL注入check
                    SqlInjectionUtil.filterContent(props[i]);

                    OrderItem orderItem = new OrderItem();
                    orderItem.setColumn(props[i]);
                    orderItem.setAsc(types[i].indexOf(OrderTypeConstant.ORDER_TYPE_ASC) >= 0?true:false);
                    orderItems.add(orderItem);
                }
            }
        }

        return orderItems;
    }

    /**
     * @Description 基本字段不添加查询规则
     * @Author haifeng.lv
     * @param: name
     * @Date 2020/5/25 16:05
     * @return: boolean
     */
    private static boolean judgedIsUselessField(String name) {
        return "class".equals(name) || "pageNo".equals(name) || "pageSize".equals(name);
    }

}
