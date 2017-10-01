package com.mymall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Administrator on 2017/9/23 0023.
 * Description:mmall properties配置文件的工具类
 * 感觉应该扩展为读取任意properties的配置文件
 *
 *
 */
public class PropertiesUtil {
    private static final Logger logger= LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties;
    //加载配置文件
    //类的加载顺序    静态代码块   构造方法
    static {
        String fileName = "mmall.properties";
        properties = new Properties();
        try {
            //在使用ClassLoader.getResourceAsStream时，路径直接使用相对于classpath的绝对路径,并且不能已 / 开头。
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));

        } catch (IOException e) {
            logger.error("加载失败", e);
        }
    }


        /*
            根据key值取到value
         */
        public static  String getValue(String key)
        {
            String value=properties.getProperty(key.trim());
            //不是只有空格的不为空的串
            if(StringUtils.isBlank(key))
                return null;
            return value;
         }
         //重载   key赋予初始值
    public static String getProperty(String key,String defaultValue){

        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }

//        public static void main(String args[])
//        {
//           System.out.println( PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
//        }

    }








