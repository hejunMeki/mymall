package com.mymall.dao;

import com.mymall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    //根据地址id和用户id删除地址
    int delShippingById(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);
    //绑定用户id修改地址
    int updateByPrimaryId(Shipping shipping);
    //绑定用户id查询地址
    Shipping selectShippingById(@Param("userId") Integer userId,@Param("shippingId")Integer shippingId);
    //根据用户id查询所有地址
    List<Shipping> selectListByUserId(Integer userId);
}