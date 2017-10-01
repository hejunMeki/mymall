package com.mymall.vo;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:购物车每个product显示vo
 */
public class CartProductVo {
    //结合了产品和购物车的一个抽象对象

    private Integer id;                //购物车id
    private Integer userId;             //用户id
    private Integer productId;         //产品id
    private Integer quantity;           //购物车中此商品的数量
    private String productName;          //商品名
    private String productSubtitle;        //商品副标题
    private String productMainImage;          //商品主图
    private BigDecimal productPrice;      //商品单价
    private Integer productStatus;       //该商品的状态  在售   下架   删除
    private BigDecimal productTotalPrice;   //该商品的总价格
    private Integer productStock;                 //该商品的库存
    private Integer productChecked;             //此商品是否勾选

    private String limitQuantity;//限制数量的一个返回结果    和前台约定怎样的返回结果

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
    }

    public String getProductMainImage() {
        return productMainImage;
    }

    public void setProductMainImage(String productMainImage) {
        this.productMainImage = productMainImage;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getProductChecked() {
        return productChecked;
    }

    public void setProductChecked(Integer productChecked) {
        this.productChecked = productChecked;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }
}
