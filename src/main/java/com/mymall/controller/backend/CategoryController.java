package com.mymall.controller.backend;

import com.mymall.common.Const;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.Category;
import com.mymall.pojo.User;
import com.mymall.service.ICategoryService;
import com.mymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Administrator on 2017/9/22 0022.
 * Description:商品分类控制器   用于后台调用
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryController {

    /*
        注入UserSerivce
     */
    @Autowired
    private IUserService iUserService;
    //注入categoryService
    @Autowired
    private ICategoryService iCategoryService;
    /*
        set_category_name.do
        根据品类id修改名字
     */

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session,Integer categoryId, String categoryName)
    {
        //检查用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要管理员登录");
        //检查用户角色
        ServerResponse response=iUserService.checkAdminRole(user);
        if(!response.isSuccess())
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
        else
            return iCategoryService.updateCategoryNameById(categoryId,categoryName);
    }

    /*
            /manage/category/get_category.do
            获取品类子节点（平级）
            根据categoryId获取
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId)
    {
        //检查用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要管理员登录");
        if(iUserService.checkAdminRole(user).isSuccess())
            return iCategoryService.getChildrenParallelCategory(categoryId);
        else
        return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }

    /*
        /manage/category/get_deep_category.do
        获取当前分类id及递归子节点categoryId
        requsest  categoryId
        return List<Integer>  categoryId集合
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId)
    {
        //检查用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要管理员登录");
        if(iUserService.checkAdminRole(user).isSuccess())
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }

    /*
        增加节点
        /manage/category/add_category.do
        request    parentId(default=0) categoryName
        return string
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, @RequestParam( value = "parentId",defaultValue = "0") Integer parentId, String categoryName)
    {
        //检查用户是否登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要管理员登录");

        if(iUserService.checkAdminRole(user).isSuccess())
            //todo
            return iCategoryService.addCategory(parentId,categoryName);
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }

}
