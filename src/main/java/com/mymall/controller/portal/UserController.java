package com.mymall.controller.portal;

import com.mymall.common.Const;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;
import com.mymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    /*
        register注册操作
     */
    @RequestMapping(value = "register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user)
    {
        return iUserService.register(user);
    }
    /*
        根据类型查看信息是否存在
     */
    @RequestMapping(value = "check_Valid",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type)
    {
        return iUserService.check_valid(str,type);
    }

    /*
        获取用户登录信息
        如果登录直接从session中取
     */
    @RequestMapping(value = "get_user_info",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session)
    {
        //检查是否登录
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser!=null)
            return ServerResponse.CreateSuccess(currentUser);
        return ServerResponse.createErrorByMessage("用户未登录");
    }
    /*
        忘记密码的问题
     */
    @RequestMapping(value = "forget_get_question",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username)
    {
         //查询该用户名对应的问题
        return iUserService.searchQuestionByUsername(username);
    }


    /*
        检查密保的答案是否正确
     */
    @RequestMapping(value = "forget_check_answer",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer)
    {
        return iUserService.checkAnswer(username,question,answer);
    }
    /*
        未登录修改密码
     */
    @RequestMapping(value = "forget_reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken)
    {
       return iUserService.resetPassword(username,newPassword,forgetToken);
    }

    /*
        登录状态修改密码
     */
    @RequestMapping(value = "reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew)
    {
        //判断用户是否登录
       User user=(User)session.getAttribute(Const.CURRENT_USER);
       if(user==null)
           return ServerResponse.createErrorByMessage("用户未登录");
        return iUserService.ChangePassword(user,passwordOld,passwordNew);
    }
    //todo 时间等信息不对
    @RequestMapping(value = "update_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user)
    {
        //检查登录
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
            return ServerResponse.createErrorByMessage("用户未登录");
        //将登录用户的id传递  根据id更新
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
       ServerResponse<User> response=iUserService.changeUserMessage(user);
       if(response.isSuccess())
       {
           //用户名传回去
           response.getData().setUsername(currentUser.getUsername());
           //todo 传这个数据不准确
           session.setAttribute(Const.CURRENT_USER,response.getData());
       }
       return response;
    }



    @RequestMapping(value = "get_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session)
    {
        //检查登录
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
            return ServerResponse.createErrorByMessage("用户未登录");
        return iUserService.getUserMessage(currentUser.getId());
    }

}
