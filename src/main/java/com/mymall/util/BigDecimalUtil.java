package com.mymall.util;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description：BigDecimal的加减乘除运算
 * 为了防止精度丢失  应该使用BigDecimal的String的构造方法
 */
public class BigDecimalUtil {
    //私有改造方法  不允许外部访问
    private BigDecimalUtil()
    {
    }
    //加法
    public static BigDecimal add(double p1,double p2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(p1));
        BigDecimal b2=new BigDecimal(Double.toString(p2));
        return b1.add(b2);
    }
    //减法
    public static BigDecimal sub(double p1,double p2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(p1));
        BigDecimal b2=new BigDecimal(Double.toString(p2));
        return b1.subtract(b2);
    }
    //乘法
    public static BigDecimal mult(double p1,double p2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(p1));
        BigDecimal b2=new BigDecimal(Double.toString(p2));
        return b1.multiply(b2);
    }
    //除法
    public static BigDecimal divide(double p1,double p2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(p1));
        BigDecimal b2=new BigDecimal(Double.toString(p2));
        //2 表示2位小数    ROUND_HALF_UP表示4舍五入
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }
}
