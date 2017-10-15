package com.mymall.common;

/**
 * Created by Administrator on 2017/9/29 0029.
 * Description:订单的一些列常量和枚举
 */
public enum OrderEnum {
    ;
    //这个订单状态对应本地设计数据库表
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");
        int code;
        String value;

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        //根据order的code获取values
        public static String getValue(int code)
        {
            for (OrderStatusEnum orderStatusEnum:
                values() ) {
                if(orderStatusEnum.getCode()==code)
                    return orderStatusEnum.getValue();
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
    //对应支付宝回调的订单状态
    public interface AlipayCallback{
        //等待付款
        String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        //付款成功
        String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
    //支付方式
    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");
        int code;
        String value;

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
