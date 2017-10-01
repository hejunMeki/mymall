package com.mymall.common;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:存放常量  反馈消息用到
 */
public enum ResponseCode {
    //成功
    SUCCESS(0,"SUCCESS"),
    //有错误
    ERROR(1,"ERROR"),
    //非法的行为
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT"),
    //需要登录
    NEED_LOGIN(10,"NEED_LOGIN");
    //状态码  只有小于等于0才是正常
    private final int code;
    //消息码对应的反馈消息
    private final String desc;
    ResponseCode(int code,String desc)
    {
        this.code=code;
        this.desc=desc;
    }
    /*
    将code和desc开放出去
     */
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
