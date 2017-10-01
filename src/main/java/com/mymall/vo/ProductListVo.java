package com.mymall.vo;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/9/23 0023.
 * Description:商品列表的vo
 */
public class ProductListVo {
    private Integer id;   //商品id
    private Integer categoryId;    //分类

    private String name;            //名字
    private String subtitle;     //主标题
    private String mainImage;    //主图
    private BigDecimal price;     //价格

    private Integer status;     //状态

    private String imageHost;     // 图片服务器主机


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
