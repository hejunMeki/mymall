package com.mymall.service;

import com.mymall.common.ServerResponse;
import com.mymall.vo.CartVo;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:业务层接口
 */
public interface ICartService {
    //查詢購物車列表
    ServerResponse<CartVo> getList(Integer userId);
    //向购物车中添加商品
    ServerResponse<CartVo> addProductToCart(Integer userId,Integer productId,Integer count);
    //修改购物车中指定商品的数量
    ServerResponse<CartVo> updateCartProduct(Integer userId,Integer productId,Integer count);
    //购物车中移除商品
    ServerResponse<CartVo> deleteProducts(Integer userId,String productIds);
    //移除购物车中所有商品
    ServerResponse<CartVo> deleteProductAll(Integer userId);
    //购物车选中反选商品
    ServerResponse<CartVo> checkOrUnCheckPro(Integer userId,Integer productId,Integer check);
    //查看购物车中产品数量 包括未选中的
    ServerResponse<Integer> selectProducuCount(Integer userId);
}
