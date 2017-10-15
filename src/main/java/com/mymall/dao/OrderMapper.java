package com.mymall.dao;

import com.mymall.pojo.Cart;
import com.mymall.pojo.Order;
import com.mymall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    //根据用户和订单id查看订单是否生成
    Order selectOrderById(@Param("userId")Integer userId,@Param("orderNo")Long orderNo);
    //根据订单号查看订单是否存在
    Order selectOrderByOrderId(long orderNo);
    //根据用户id查看所有订单的集合
    List<Order> selectOrdersByUserId(Integer userId);


    //backup
    //查询所有order列表 按照时间降序排列
    List<Order> selectAll();

}