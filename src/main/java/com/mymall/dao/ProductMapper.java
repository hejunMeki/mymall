package com.mymall.dao;

import com.mymall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    //无条件查询列表
    List<Product> selectProducts();
    //根据商品名或者id查询商品
    List<Product> selectProductsByNameAndId(@Param("productName") String productName,@Param("productId") Integer productId);
    //根据关键字和id集合查询商品
    List<Product> selectProductsByKeyAndId(@Param("keyWord") String keyWord,@Param("listId") List<Integer> listId);
}