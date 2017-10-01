package com.mymall.dao;

import com.mymall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    //根据用户id查询购物车中的所有Cart
    List<Cart> getAllCartByUserId(Integer userId);
    //根据id查看购物车中商品是否全选
    int getAllCheckesStatus(Integer userId);
    //查看用户购物车中是否存在某商品
    Cart selectProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);
    //删除购物车中商品
    int deleteProduct(@Param("userId") Integer userId, @Param("productList") List<String> productList);
    //购物车商品选中，全选，反选
    int checkedOrUncheckedProduct(@Param("userId") Integer userId, @Param("productId") Integer productId,@Param("check") Integer check);
    //查看购物车中产品数量 包括未选中的
    int selectProductCount(Integer userId);
}