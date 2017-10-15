package com.mymall.util;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/10/14 0014.
 * Description:MD5加盐值对密码加密
 */
public class MD5Util {
    /*
        将字节数组转换为16进制的字母加数字的串
     */
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }
    /*
        对每个字节处理 转换为16进制的数字加字母组合
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 返回大写MD5   这个方法是通用方法
     * 但是下面我们指定utf-8常用编码MD5EncodeUtf8(String origin)对外开放
     * byte[] digest(byte[] input)执行最后一次更新在消化使用指定的字节数组,然后完成消化计算。这个方法首先调用 update(input),通过输入数组 update方法,然后调用 digest()。
     */
    private static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            //声明一个MessageDigest对象,指定MD5摘要算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString.toUpperCase();
    }
    //对字符串进行MD5加密算法转换
    public static String MD5EncodeUtf8(String origin) {
        //加密之前密码后面加salt处理
        origin = origin + PropertiesUtil.getProperty("password.salt", "");
        return MD5Encode(origin, "utf-8");
    }

    /*
        自定义数组 16进制
     */
    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

}
