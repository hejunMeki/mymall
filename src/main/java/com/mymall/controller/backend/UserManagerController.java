package com.mymall.controller.backend;

import com.mymall.common.Const;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;
import com.mymall.service.IUserService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/9/22 0022.
 * Description:后台管理员控制层
 */
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {
    @Autowired
    private IUserService iUserService;
    /*
            login.do
     */

    @RequestMapping("login.do")
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session)
    {
        ServerResponse<User> response=iUserService.login(username,password);
        //判断是否登录成功
        if(response.isSuccess())
        {
            //获取登录用户信息
            User user=response.getData();
            //判断是否是管理员登录
            if(user.getRole()==Const.Role.ROLE_ADMIN)
            {
                //放入session
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }
        }
        return response;
    }

}
