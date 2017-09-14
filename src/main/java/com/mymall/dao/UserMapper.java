package com.mymall.dao;

import com.mymall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    //以下方法为自己添加
    //检查用户名是否存在
    int checkUsername(String username);
    //检查登录  用户是否存在
    User login(@Param("username") String username,@Param("password") String password);
}
