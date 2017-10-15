package com.mymall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mymall.common.Const;
import com.mymall.common.OrderEnum;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;
import com.mymall.service.IOrderService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/29 0029.
 * Description:订单处理
 */
@Controller
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /*
     创建订单
     /order/create.do
     创建订单时指定发货地址shippingId
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    /*
        获取订单的商品信息
        未下订单前的确认
        /order/get_order_cart_product.do
     */
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        return iOrderService.getOrderProducts(user.getId());
    }
    /*
        订单list
        /order/list.do
        pageSize(default=10)
        pageNum(default=1)
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize, @RequestParam(value = "pageNum",defaultValue = "1")  int pageNum)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        return iOrderService.getOrderListByPage(user.getId(),pageSize,pageNum);
    }

    /*
        订单详情
        /order/detail.do
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session,Long orderNo){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        return iOrderService.getOrderDetails(user.getId(),orderNo);
    }

    /*
    取消订单
    /order/cancel.do
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse<String> cancel(HttpSession session,Long orderNo)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        return iOrderService.orderCancel(user.getId(),orderNo);
    }


    /*
            提交订单后  生成支付二维码
          /order/pay.do
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo,HttpServletRequest request)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //定位到根目录下upload下面
        String path=request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(),path);
    }


    /*
           pay方法执行完毕之后 当用户扫描二维码支付时 支付宝会回调request
           /order/alipay_callback.do
           注意：用户扫描二维码支付时   可以不处于登录状态 但是服务器会一直接受支付宝的回调请求
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request)
    {
        //支付宝将回调的信息都封装在request里面
        Map<String,String> reaultMap= Maps.newHashMap();
        //获取支付宝回调的map
        Map backMap=request.getParameterMap();
        //遍历
        for (Iterator iterator=backMap.keySet().iterator();iterator.hasNext();) {
            //取到当前key
            String key= (String) iterator.next();
            //根据key取到value值   注意value的值是数组
            String[] value= (String[]) backMap.get(key);
            //用于字符串拼接
            String valueStr="";
            for (int i = 0; i <value.length ; i++) {
                //三元运算符来解决拼接的时候的分隔符问题
                valueStr=(i==value.length-1)?valueStr+value[i]:valueStr+value[i]+",";
            }
            //放入reaultMap中   key的值一定要是支付宝回调的key
            reaultMap.put(key,valueStr);
        }
        //打印日志
        logger.info("支付宝回调：sign{},trade_status:{},参数:{}",reaultMap.get("sign"),reaultMap.get("trade_status"),reaultMap.toString());
        //以上参数取到之后  就可以验证了 验证的时候sign_type和sign不要
        reaultMap.remove("sign_type");
        try {
            //1、验证回调是否是支付宝发出的回调
//            boolean aAlipaySignature=AlipaySignature.rsaCheckV2(reaultMap, Configs.getPublicKey(),"utf-8",Configs.getSignType());
            boolean aAlipaySignature=AlipaySignature.rsaCheckV2(reaultMap, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
           // System.out.println("是否为支付宝回调"+aAlipaySignature);
            if(!aAlipaySignature)
                return ServerResponse.createErrorByMessage("非支付宝回调，禁止恶意请求");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //2、验证回调中的参数是否与订单中的参数一直  如有任意一处不一致  那么此支付为异常请求支付 应该忽略
        ServerResponse response=iOrderService.checkAlipayCallBack(reaultMap);
        if(response.isSuccess())
            return OrderEnum.AlipayCallback.RESPONSE_SUCCESS;
        return OrderEnum.AlipayCallback.RESPONSE_FAILED;
    }


    /*
        查询订单支付状态
        /order/query_order_pay_status.do
        和前端约定  支付了就返回true  没有支付就返回false
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //用户不为空
        ServerResponse response=iOrderService.checkOrderPayStatus(user.getId(),orderNo);
        if(response.isSuccess())
            return ServerResponse.CreateSuccess(true);
        return ServerResponse.CreateSuccess(false);
    }

}
