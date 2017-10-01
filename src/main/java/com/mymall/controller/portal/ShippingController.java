package com.mymall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mymall.common.Const;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.Shipping;
import com.mymall.pojo.User;
import com.mymall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:收货地址控制层
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /*
        添加地址
        /shipping/add.do
        response:"shippingId"
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session,Shipping shipping)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //添加
        shipping.setUserId(user.getId());
        return iShippingService.addShipping(shipping);
    }

    /*
        删除地址
        /shipping/del.do
        request   shippingId
     */
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession session,Integer shippingId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //删除  但是要防止横向越权
        return iShippingService.delShippingById(user.getId(),shippingId);
    }

    /*
        登录状态更新地址
        /shipping/update.do
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session,Shipping shipping)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //更新
       shipping.setUserId(user.getId());
        return iShippingService.updateShipping(shipping);
    }

    /*
        选中查看具体的地址
        /shipping/select.do
        shippingId
     */
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session,Integer shippingId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //更新  绑定id查询
        return iShippingService.selectShipping(user.getId(),shippingId);
    }

    /*
        地址列表
        /shipping/list.do
        pageNum(默认1),pageSize(默认10)
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //分页查询
        return iShippingService.getShippingList(user.getId(),pageNum,pageSize);
    }

}
