package com.mymall.common;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:常量类
 */
public class Const {
    //当前用户常量
    public static final String CURRENT_USER="currentUser";
    //用户名字段
    public static final String USERNAME="username";
    //email字段
    public static final String EMAIL="email";
    //定义角色
    public interface Role{
        //用户权限
        public static final Integer ROLE_CUSTOMER=0;
        //管理员权限
        public static final Integer ROLE_ADMIN=1;

    }
}
