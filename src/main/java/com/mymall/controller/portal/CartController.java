package com.mymall.controller.portal;

import com.mymall.common.Const;
import com.mymall.common.ProductEnum;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.User;
import com.mymall.service.ICartService;
import com.mymall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:购物车
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    /*
        查询购物车列表
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session)
    {
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            if(user==null)
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
           //开始调用
        return iCartService.getList(user.getId());
    }


    /*
    /cart/add.do
    productId,count
    向购物车中增加商品
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session,Integer productId,Integer count)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.addProductToCart(user.getId(),productId,count);
    }
    /*
    更新购物车某个产品数量
    /cart/update.do
    productId,count
    这里的修改时新增或减少
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session,Integer productId,Integer count)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.updateCartProduct(user.getId(),productId,count);
    }

    /*
        移除购物车某个产品
        productIds  和前台约定  以逗号分隔要删除的商品
        /cart/delete_product.do
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.deleteProducts(user.getId(),productIds);
    }


    /*
        清空购物车
       delete_product_all.do
     */
    @RequestMapping("delete_product_all.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProductAll(HttpSession session,String productIds)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.deleteProductAll(user.getId());
    }

    /*
        购物车选中某个商品
        /cart/select.do
        productId
     */
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.checkOrUnCheckPro(user.getId(),productId, ProductEnum.CheckOrNotCheck.CHECKED);
    }

    /*
    购物车取消选中某个商品
    /cart/un_select.do
     */
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.checkOrUnCheckPro(user.getId(),productId, ProductEnum.CheckOrNotCheck.UN_CHECKED);
    }

    /*
       购物车全选
       /cart/select_all.do
     */
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session,Integer productId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.checkOrUnCheckPro(user.getId(),null, ProductEnum.CheckOrNotCheck.CHECKED);
    }

    /*
        购物车取消全选
        /cart/un_select_all.do
     */
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session,Integer productId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        //开始调用
        return iCartService.checkOrUnCheckPro(user.getId(),null, ProductEnum.CheckOrNotCheck.UN_CHECKED);
    }

    /*
        查询在购物车里产品的数量
        所谓的数量：不是商品种类  而是包括数量在一起的总量
        /cart/get_cart_product_count.do
     */
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.CreateSuccess(0);
        //开始调用
        return iCartService.selectProducuCount(user.getId());
    }

}
