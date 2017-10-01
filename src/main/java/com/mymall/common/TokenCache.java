package com.mymall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/9/19 0019.
 * Description:将token放在本地缓存中
 */
public class TokenCache {
    //打印日志
    private static Logger logger= LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX="token_";
    /*
        创建一个本地的cache对象
        expireAfterAccess(12, TimeUnit.HOURS)    最长缓存12小时
     */
    private static LoadingCache<String,String> localCache= CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                /*
                当key值对应的value为空时，默认调用load默认方法赋值
                为了避免null.equals(obj)报错
                这里指定字符串null
                 */
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    //将key对应的value放入缓存
    public static void setKey(String key,String value)
    {
        localCache.put(key,value);
    }
    //根据key值从缓存中取值
    public static String getKey(String key)
    {
        String value=null;
        try {
            value=(String)localCache.get(key);
            if(value.equals("null"))
                return null;
        }
        catch (Exception e)
        {
            logger.error("localCache error");
        }
        return value;
    }

}
