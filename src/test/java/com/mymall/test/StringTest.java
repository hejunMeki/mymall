package com.mymall.test;

import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:...
 */
public class StringTest {
    //对空格的研究
    @Test
    public void Test1()
    {
            String s="  ";
            String[] ss=s.split(",");
            System.out.println(ss[0].length());
    }
    @Test
    public void Test2()
    {
        String s="  ";
        List<String> productList = Splitter.on(",").splitToList(s);
        System.out.println(CollectionUtils.isEmpty(productList));
    }

}
