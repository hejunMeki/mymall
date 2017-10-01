package com.mymall.dao;

import com.mymall.pojo.Order;
import org.apache.ibatis.annotations.Param;

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
}