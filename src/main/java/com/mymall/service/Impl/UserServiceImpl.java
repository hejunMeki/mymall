package com.mymall.service.Impl;

import com.mymall.common.ServerResponse;
import com.mymall.dao.UserMapper;
import com.mymall.pojo.User;
import com.mymall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:...
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

     @Autowired
     private UserMapper userMapper;

    public ServerResponse<User> login(String username, String password) {
        //检查用户名是否存在
    int count=userMapper.checkUsername(username);
    if(count==0)
    {
        return ServerResponse.createErrorByMessage("用户名不存在");
    }
     //todo MD5加密
     //检查用户名和密码是否正确
    User user=userMapper.login(username,password);
        if (user==null)
        {
            return ServerResponse.createErrorByMessage("密码错误");
        }
        //用户登录成功  处理返回值的密码
        user.setPassword(StringUtils.EMPTY);
        //登录成功  返回user
        return ServerResponse.createSuccess("登录成功",user);
    }


}
