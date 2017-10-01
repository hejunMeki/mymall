package com.mymall.service;

import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:user用户的相关接口
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);
    //用户注册
    ServerResponse<String> register(User user);
    //根据类型检查信息是否存在
    ServerResponse<String> check_valid(String str,String type);
    //根据用户名查询密保问题
    ServerResponse<String> searchQuestionByUsername(String username);
    //验证密保答案
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    //未登录状态修改密码
    ServerResponse<String> resetPassword(String username,String newPassword,String forgetToken);
    //登录状态修改密码
    ServerResponse<String> ChangePassword(User user,String passwordOld,String passwordNew);
    //更新个人信息
    ServerResponse<User> changeUserMessage(User user);
    //获取登录用户信息
    ServerResponse<User> getUserMessage(Integer id);
    //判断是否是管理员
    ServerResponse checkAdminRole(User user);
}
