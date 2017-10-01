package com.mymall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:购物车列表显示vo
 */
public class CartVo {

    private List<CartProductVo> cartProductVoList;      //购物车中所有商品的列表
    private BigDecimal cartTotalPrice;       //购物车中已勾选的商品的总价格
    private Boolean allChecked;          //是否已经都勾选 为什么要这个呢   前端需要判断进行显示
    private String imageHost;            //图片的服务器地址


    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
