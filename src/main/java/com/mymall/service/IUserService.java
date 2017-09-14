package com.mymall.service;

import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:user用户的相关接口
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);
}
