package com.mymall.service;

import com.github.pagehelper.PageInfo;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.Shipping;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:地址接口
 */
public interface IShippingService {
    //添加地址
    ServerResponse<Map<String,Integer>> addShipping(Shipping shipping);
    //删除地址
    ServerResponse delShippingById(Integer userId,Integer shippingId);
    //更新地址
    ServerResponse updateShipping(Shipping shipping);
    //查询指定地址
    ServerResponse selectShipping(Integer userId,Integer shippingId);
    //分页查询用户所有地址
    ServerResponse<PageInfo> getShippingList(Integer userId, int pageNum, int pageSize);
}
