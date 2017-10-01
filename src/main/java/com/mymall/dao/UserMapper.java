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

    //检查用户名是否存在
    int check_username(String username);
    //检查邮箱是否存在
    int check_email(String email);
    //根据用户名查找密保问题
    String SltQuesByUsername(String username);
    //检查密保问题
    int checkQuestionAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
    //未登录修改密码
    int updatePassword(@Param("username")String username,@Param("passwordNew")String passwordNew);
    //根据用户id和密码查询
    int checkPassword(@Param("id")Integer id,@Param("password")String password);
    //修改信息时邮箱检重
    int checkEmail(@Param("id") Integer id,@Param("email")String email);


}
