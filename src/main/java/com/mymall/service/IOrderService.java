package com.mymall.service;

import com.mymall.common.ServerResponse;

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
}
