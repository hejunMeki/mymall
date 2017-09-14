package com.mymall.controller;

import com.mymall.common.Const;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;
import com.mymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:用户控制层
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody                //自动转为json
    public ServerResponse<User> login(String username, String password, HttpSession session)
    {
        //查询
       ServerResponse<User> response=iUserService.login(username,password);
       //如果成功
       if(response.isSuccess())
       {
           System.out.println(response.getData());
            session.setAttribute(Const.CURRENT_USER,response.getData());
       }
        return response;
    }

}
