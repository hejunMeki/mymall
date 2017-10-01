package com.mymall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Administrator on 2017/9/24 0024.
 * Description:dataTime与String之间的相互转换
 */
public class DateTimeUtil {

    //定义format格式
    public static final String STANDARD_FORMAT= "yyyy-MM-dd HH:mm:ss";

    //要完成两个
    //datetime-->>string
    //string-->>datetime
    public static Date parseStringToDate(String time,String format)
    {
        //格式化
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(format);
        //转化为时间
        DateTime dateTime=dateTimeFormatter.parseDateTime(time);
        return dateTime.toDate();
    }
    //date-->>string
    public static String parseDateToStr(Date date,String format)
    {
        if(date==null)
            return StringUtils.EMPTY;
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(format);
    }

    //以上两种是可以指定format的转换 接下来重载他们的默认format转换
    public static Date parseStringToDate(String time)
    {
        //格式化
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(STANDARD_FORMAT);
        //转化为时间
        DateTime dateTime=dateTimeFormatter.parseDateTime(time);
        return dateTime.toDate();
    }

    public static String parseDateToStr(Date date)
    {
        if(date==null)
            return StringUtils.EMPTY;
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

//    public static void main(String args[])
//    {
//            String time="1996-10-20 00:23:24";
//            Date date=DateTimeUtil.parseStringToDate(time);
//            System.out.println(date);
//            System.out.println(parseDateToStr(date));
//    }

}
