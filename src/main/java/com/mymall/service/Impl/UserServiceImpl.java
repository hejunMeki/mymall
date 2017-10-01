package com.mymall.service.Impl;

import com.mymall.common.Const;
import com.mymall.common.ServerResponse;
import com.mymall.common.TokenCache;
import com.mymall.dao.UserMapper;
import com.mymall.pojo.User;
import com.mymall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.jmx.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;
import sun.security.util.Password;

import java.util.UUID;

/**
 * Created by Administrator on 2017/9/14 0014.
 * Description:...
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    public ServerResponse<User> login(String username, String password) {
        //检查用户名是否存在
        int count = userMapper.checkUsername(username);
        if (count == 0) {
            return ServerResponse.createErrorByMessage("用户名不存在");
        }
        //todo MD5加密
        //检查用户名和密码是否正确
        User user = userMapper.login(username, password);
        if (user == null) {
            return ServerResponse.createErrorByMessage("密码错误");
        }
        //用户登录成功  处理返回值的密码
        user.setPassword(StringUtils.EMPTY);
        //登录成功  返回user
        return ServerResponse.createSuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //检查用户名是否存在
        ServerResponse validResponse=this.check_valid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess())
            return validResponse;
        validResponse=this.check_valid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess())
            return validResponse;
        //用户名和邮箱都不存在
        //设置用户的权限为customer
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //开始插入  todo md5加密
        int result=userMapper.insert(user);
        if(result==0)
            return ServerResponse.createErrorByMessage("用户注册失败！");
        return ServerResponse.createBySuccessMessage("用户注册成功！");
    }

    /*
        根据类型判断是否存在
     */
    public ServerResponse<String> check_valid(String str, String type) {
        //类型不为空
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int count = userMapper.check_username(str);
                if (count > 0)
                    return ServerResponse.createErrorByMessage("用户名已存在");
            }
            if (Const.EMAIL.equals(type)) {
                int count = userMapper.check_email(str);
                if (count > 0)
                    return ServerResponse.createErrorByMessage("邮箱已存在");
            }
        }
        else
            return ServerResponse.createErrorByMessage("参数错误");
        return ServerResponse.createBySuccessMessage("校验成功");
    }
    //根据用户名查找密保问题
    public ServerResponse<String> searchQuestionByUsername(String username)
    {
        //检查用户是否存在
        ServerResponse response=this.check_valid(username,"username");
        if(response.isSuccess())
            ServerResponse.createBySuccessMessage("用户名不存在");
        //用户名存在之后  查询问题
        String question=userMapper.SltQuesByUsername(username);
        if(StringUtils.isNotBlank(question))
            return ServerResponse.CreateSuccess(question);
        return ServerResponse.createErrorByMessage("用户没有设置密保问题");
    }
    //检查密保的答案是否正确
    public ServerResponse<String> checkAnswer(String username,String question,String answer)
    {
        //直接在数据库中查询
        int count=userMapper.checkQuestionAnswer(username,question,answer);
        //密保问题回答正确   返回一个验证的token  就是一个字符串  标记字符串
        if(count>0)
        {
           String forgetToken=UUID.randomUUID().toString();
           //放入缓存  key前面token_相当于一个标识
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            //返回toekn
            return ServerResponse.CreateSuccess(forgetToken);
        }
        return ServerResponse.createErrorByMessage("密码错误");
    }
    /*
        未登录修改密码
     */
    public ServerResponse<String> resetPassword(String username,String newPassword,String forgetToken)
    {
        //检查forgetToken是否为空
        if(StringUtils.isBlank(forgetToken))
        {
            return ServerResponse.createErrorByMessage("参数token缺失");
        }
        //检查用户名是否为空
        ServerResponse response=this.check_valid(username,"username");
        if(response.isSuccess())
            ServerResponse.createBySuccessMessage("用户名不存在");
        //校验token
        String tokenVlaue=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        //判断缓存中的token
        if(StringUtils.isBlank(tokenVlaue))
        {
            return ServerResponse.createErrorByMessage("token无效或过期");
        }
        //比较两个token是否一致
        if(StringUtils.equals(forgetToken,tokenVlaue))
        {
            //todo MD5加密
            int count=userMapper.updatePassword(username,newPassword);
            if(count>0)
                return ServerResponse.createBySuccessMessage("密码修改成功");
        }
        else{
            return ServerResponse.createErrorByMessage("token错误，请重新获取");
        }
        return ServerResponse.createErrorByMessage("密码修改失败");
    }
    /*
        登录状态修改密码
     */
    public ServerResponse<String> ChangePassword(User user,String passwordOld,String passwordNew)
    {
            //防止横向越权  将passwordOld与用户的id绑定查询
        int count=userMapper.checkPassword(user.getId(),passwordOld);
        if(count==0)
            return ServerResponse.createErrorByMessage("旧密码错误");
        //todo MD5加密
        user.setPassword(passwordNew);
        //选择性更新
       int resultCount=userMapper.updateByPrimaryKeySelective(user);
       if(resultCount>0)
           return ServerResponse.createBySuccessMessage("密码修改成功");
       return ServerResponse.createErrorByMessage("密码修改失败");
    }

    public ServerResponse<User> changeUserMessage(User user)
    {
        //用户名不能被修改
        //邮箱不能喝别人的重复
       int resultCount= userMapper.checkEmail(user.getId(),user.getEmail());
       if(resultCount>0)
           return ServerResponse.createErrorByMessage("邮箱已被注册");
       //开始更新
        User currentUser=new User();
        currentUser.setId(user.getId());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());
        currentUser.setQuestion(user.getQuestion());
        currentUser.setAnswer(user.getAnswer());
        //
        int count=userMapper.updateByPrimaryKeySelective(currentUser);
        if(count>0)
            return ServerResponse.createSuccess("更新个人信息成功",currentUser);
        return ServerResponse.createErrorByMessage("更新个人信息失败");
    }

    /*
        获取当前登录用户的详细信息，并强制登录
     */
    public ServerResponse<User> getUserMessage(Integer id)
    {
        //根据id查询
        User currentUser=userMapper.selectByPrimaryKey(id);
        //todo 常量10可以用枚举声明
        if(currentUser==null)
            ServerResponse.createByErrorCodeMessage(10,"用户未登录");
        //将密码设置空
        currentUser.setPhone(StringUtils.EMPTY);
        return ServerResponse.createSuccess("查询成功",currentUser);
    }

    /*
        校验是否是管理员
     */
    public ServerResponse checkAdminRole(User user)
    {
        if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN.intValue())
            return ServerResponse.createBySuccess();
        return ServerResponse.createError();
    }


}
