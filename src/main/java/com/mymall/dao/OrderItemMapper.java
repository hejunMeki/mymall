package com.mymall.dao;

import com.mymall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    //根据用户id和订单id查询所有子订单
    List<OrderItem> selectOrderItemsById(@Param("userId")Integer userId, @Param("orderNo")Long orderNo);
}