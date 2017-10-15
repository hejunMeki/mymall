package com.mymall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mymall.common.Const;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;
import com.mymall.service.IOrderService;
import com.mymall.service.IUserService;
import com.mymall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/10/3 0003.
 * Description:后台订单
 */
@Controller
@RequestMapping("/manage/order/")
public class OrderManagerController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    /*
        订单分页查询
        /manage/order/list.do
        pageSize(default=10)
        pageNum(default=1)
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            //查询
            return iOrderService.getAllOrderVoByPage(pageNum,pageSize);
        }
        return ServerResponse.createErrorByMessage("没有权限");
    }

    /*
    按照订单号绝对查询  以后该查询会扩展到模糊查询  因此这里做分页处理先 便于以后扩展
    /manage/order/search.do
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> serach(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize,Long orderNo)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            //查询
            return iOrderService.orderSerach(orderNo,pageNum,pageSize);
        }
        return ServerResponse.createErrorByMessage("没有权限");
    }

    /*
        订单详情
        /manage/order/detail.do
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpSession session, Long orderNo)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            //查询
            return iOrderService.orderDetail(orderNo);
        }
        return ServerResponse.createErrorByMessage("没有权限");
    }


    /*
    /manage/order/send_goods.do
    订单发货
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(HttpSession session, Long orderNo)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            //查询
            return iOrderService.orderSendFoods(orderNo);
        }
        return ServerResponse.createErrorByMessage("没有权限");
    }

}
