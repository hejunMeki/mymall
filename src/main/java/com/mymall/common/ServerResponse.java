package com.mymall.common;

import com.sun.org.apache.regexp.internal.RE;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:消息返回的一个封装类
 * 服务端响应对象
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)   //空的key不打包成json返回
public class ServerResponse<T> implements Serializable {
    private int status;      //状态标记
    private String msg;    //提示消息
    private T data;     //数据

    /*
        构造方法私有
     */
    private ServerResponse(int status){
        this.status=status;
    }
    private ServerResponse(int status ,String msg)
    {
        this.status=status;
        this.msg=msg;
    }
    private ServerResponse(int status,T data)
    {
        this.status=status;
        this.data=data;
    }
    private ServerResponse(int status,T data,String msg)
    {
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    /*
    开放方法
     */
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    //创建只带状态status的serverResponse
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    //创建带状态status和提示信息的serverResponse
    public static <T> ServerResponse<T> createBySuccessMessage(String message)
    {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),message);
    }
    //创建带状态status和返回数据的serverResponse
    public static <T> ServerResponse<T> CreateSuccess(T data)
    {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    //创建带状态status、提示消息和返回数据的serverResponse
    public static <T> ServerResponse<T> createSuccess(String message,T data)
    {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data,message);
    }
    //以上是成功状态   以下是失败的创建
    //只返回失败的状态码status    错误不用返回数据
    public static <T> ServerResponse<T> createError()
    {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }
    //返回错误消息
    public static <T> ServerResponse<T> createErrorByMessage(String errorMessage)
    {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    //暴露一个指定状态码的方法
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String message)
    {
        return new ServerResponse<T>(errorCode,message);
    }

}
