package com.mymall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/10/3 0003.
 * Description:这个类是订单生成前的查看订单信息
 */
public class OrderProductVo {
    //订单中每个商品信息
    private List<OrderItemVo> orderItemVoList;
    //总价格
    private BigDecimal productTotalPrice;
    //图片服务器地址
    private String imageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
