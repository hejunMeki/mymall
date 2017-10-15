package com.mymall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/10/1 0001.
 * Description:返回生成的订单信息给前端
 */
public class OrderVo {
    //order的基本信息
    //订单号
    private Long orderNo;
    //实际付款金额,单位是元,保留两位小数
    private BigDecimal payment;
    //支付类型,1-在线支付
    private Integer paymentType;
    //支付类型的描述  由后台处理 不然前端还要根据integer的值来判断
    private String paymentTypeDesc;
    //邮费
    private Integer postage;
    //订单状态:0-已取消-10-未付款，20-已付款，40-已发货
    private Integer status;
    //订单状态描述
    private String statusDesc;
    //交易创建时间
    private String createTime;
    //付款时间
    private String paymentTime;
    //发货时间
    private String sendTime;
    //交易完成时间
    private String endTime;
    //交易关闭时间
    private String closeTime;

    //订单的明细
    private List<OrderItemVo> orderItemVoList;
    //图片服务器地址
    private String imageHost;
    //地址id
    private Integer shippingId;
    //收货人姓名
    private String receiverName;
    //地址Vo
    private ShippingVo shippingVo;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentTypeDesc() {
        return paymentTypeDesc;
    }

    public void setPaymentTypeDesc(String paymentTypeDesc) {
        this.paymentTypeDesc = paymentTypeDesc;
    }

    public Integer getPostage() {
        return postage;
    }

    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public ShippingVo getShippingVo() {
        return shippingVo;
    }

    public void setShippingVo(ShippingVo shippingVo) {
        this.shippingVo = shippingVo;
    }
}
