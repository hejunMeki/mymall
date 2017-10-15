package com.mymall.service;

import com.github.pagehelper.PageInfo;
import com.mymall.common.ServerResponse;
import com.mymall.vo.OrderVo;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/29 0029.
 * Description:...
 */
public interface IOrderService {
    //提交订单  生成二维码
    ServerResponse<Map<String,String>> pay(Long orderNo, Integer userId, String path);
    //处理支付宝回调验证
    ServerResponse checkAlipayCallBack(Map<String,String> params);
    //订单支付状态查询
    ServerResponse checkOrderPayStatus(Integer userId,Long orderNo);
    //生成订单
    ServerResponse createOrder(Integer userId,Integer shippingId);
    //订单生成前查看订单中的商品信息
    ServerResponse getOrderProducts(Integer userId);
    //分页获取用户所有的订单vo
    ServerResponse<PageInfo> getOrderListByPage(Integer userId, int pageSize, int pageNum);
    //获取指定订单的详细信息orderVo
    ServerResponse getOrderDetails(Integer userId,Long orderNo);
    //取消订单
    ServerResponse<String> orderCancel(Integer userId,Long orderNo);

    //backup
    //分页查询所有订单
    ServerResponse<PageInfo> getAllOrderVoByPage(int pageNum,int pageSize);
    //分页查询指定订单的orderVo
    ServerResponse<PageInfo> orderSerach(Long orderNo,int pageNum,int pageSize);
    //订单详情
    ServerResponse<OrderVo> orderDetail(Long orderNo);
    //订单发货
    ServerResponse<String> orderSendFoods(Long orderNo);
}
