package com.mymall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Administrator on 2017/9/24 0024.
 * Description:描述商品信息的枚举
 */
public enum ProductEnum {
    ;
    public interface Status{
        //在线状态
        public static final Integer ON_SALE=1;
        //下架状态
        public static final Integer OUT_OF_STOCK=2;
        //已删除
        public static final Integer DELETED=3;
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public interface LimitCountOfProduct{
        public static final String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
        public static final String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
    }
    public interface CheckOrNotCheck
    {
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态
    }
}
