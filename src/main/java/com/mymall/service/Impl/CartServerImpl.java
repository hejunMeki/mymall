package com.mymall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mymall.common.ProductEnum;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.dao.CartMapper;
import com.mymall.dao.ProductMapper;
import com.mymall.pojo.Cart;
import com.mymall.pojo.Product;
import com.mymall.service.ICartService;
import com.mymall.util.BigDecimalUtil;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.CartProductVo;
import com.mymall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:购物车业务层
 */
@Service("iCartService")
public class CartServerImpl implements ICartService {


    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    /*
     获取购物车列表
     购物车列表需要一个VO类来表示属性
     */
    public ServerResponse<CartVo> getList(Integer userId)
    {
        CartVo cartVo=getCartVoLimit(userId);
        return ServerResponse.CreateSuccess(cartVo);
    }

    /*
        添加商品
     */
    public ServerResponse<CartVo> addProductToCart(Integer userId,Integer productId,Integer count)
    {
        //判断空
        if(productId==null||count==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //先判断该商品在购物车中是否已经存在
        Cart cart=cartMapper.selectProduct(userId,productId);
        if(cart!=null)
        {
            //如果购物车中该商品已存在 直接修改
            cart.setQuantity(cart.getQuantity()+count);
            //修改
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        else {
            //如果该商品在购物车中不存在 则新增商品
            Cart addCart=new Cart();
            addCart.setUserId(userId);
            addCart.setProductId(productId);
            addCart.setQuantity(count);
            //添加购物车默认选中状态
            addCart.setChecked(ProductEnum.CheckOrNotCheck.CHECKED);
            //添加
            cartMapper.insert(addCart);
        }
        return getList(userId);
    }

    /*
    修改购物车数量
     */
    public ServerResponse<CartVo> updateCartProduct(Integer userId,Integer productId,Integer count)
    {
        //判断空
        if(productId==null||count==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //先判断该商品在购物车中是否已经存在
        Cart cart=cartMapper.selectProduct(userId,productId);
        if(cart==null)
        {
            return ServerResponse.createErrorByMessage("该商品未加入购物车");
        }
        //如果购物车中该商品已存在 直接修改
        cart.setQuantity(cart.getQuantity()+count);
        //修改
        cartMapper.updateByPrimaryKeySelective(cart);
        return getList(userId);
    }

    /*
        从购物车中移除商品
        todo  第一个if判断是否为“”带有空白的情况
     */
    public ServerResponse<CartVo> deleteProducts(Integer userId,String productIds)
    {
        if(StringUtils.isBlank(productIds))
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //将字符串分割并存放在集合中
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList))
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //删除该集合购物车中的商品
        cartMapper.deleteProduct(userId,productList);
        return this.getList(userId);
    }


    public ServerResponse<CartVo> deleteProductAll(Integer userId)
    {
        //删除该集合购物车中的商品
        cartMapper.deleteProduct(userId,null);
        return this.getList(userId);
    }

    /*
        设置购物车中商品选中
        ******代码的复用性很强
     */
    public ServerResponse<CartVo> checkOrUnCheckPro(Integer userId,Integer productId,Integer check)
    {
        //修改
        cartMapper.checkedOrUncheckedProduct(userId,productId,check);
        return this.getList(userId);

    }

    /*
        查看购物车中产品数量
        包括没有选中的
     */
    public ServerResponse<Integer> selectProducuCount(Integer userId)
    {
        if(userId==null)
            return ServerResponse.CreateSuccess(0);
        int resultCount=cartMapper.selectProductCount(userId);
        return ServerResponse.CreateSuccess(resultCount);
    }


    /*
        定义一个核心方法  用于购物车的一些列操作
     */
    private CartVo getCartVoLimit(Integer userId)
    {
            //查询该用户下购物车中所有商品
        List<Cart> cartList=cartMapper.getAllCartByUserId(userId);
        //存放CartProductVo集合
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //初始化总价
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList))
        {
            // 遍历该集合
            for(Cart cartItem:cartList)
            {
                //创建一个cart商品vo对象
                CartProductVo cartProductVo=new CartProductVo();
                //设置购物车中商品id
                cartProductVo.setId(cartItem.getId());
                //设置商品的id
                cartProductVo.setProductId(cartItem.getProductId());
                //设置用户id
                cartProductVo.setUserId(cartItem.getUserId());
                //接下来从商品表mmall_product中取找其他的商品信息
                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                //todo 购物车中产品不存在时的空指针处理
                if(product!=null)
                {
                    //如果该商品存在  那么该商品的某些信息存入cartProductVo中用于前台显示
                    //设置商品的名称
                    cartProductVo.setProductName(product.getName());
                    //商品的单价
                    cartProductVo.setProductPrice(product.getPrice());
                    //商品的主图
                    cartProductVo.setProductMainImage(product.getMainImage());
                    //商品的副标题
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    //该商品的库存
                    cartProductVo.setProductStock(product.getStock());
                    //还商品的状态
                    cartProductVo.setProductStatus(product.getStatus());
                    //设置是否选中
                    cartProductVo.setProductChecked(cartItem.getChecked());
                    //以上基本信息设置完成之后  进行价格计算  还有商品数量上限判断及设置
                    //初始化该商品的实际能购数量限制
                    int buyLimitCount=0;
                    if(product.getStock()>cartItem.getQuantity())
                    {
                        //当库存量大于所购买量  限制成功  返回给前端
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(ProductEnum.LimitCountOfProduct.LIMIT_NUM_SUCCESS);
                    }
                    else
                    {
                        //当库存量小于所购买量  限制失败 那么久将库存量设置为所购买最大量 并修改购物车该商品购买信息 返回给前端
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(ProductEnum.LimitCountOfProduct.LIMIT_NUM_FAIL);
                        //更新购物车中该商品的信息
                        Cart cart=new Cart();
                        //设置购物车中该商品购物车id
                        cart.setId(cartItem.getId());
                        //重新设置数量为最大限制数
                        cart.setQuantity(buyLimitCount);
                        //更新数据库  todo 是否应该判断下更新成功or失败
                        cartMapper.updateByPrimaryKeySelective(cart);
                    }
                    //设置cartProductVo中该商品的实际最大购买数量
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算该商品的总价格  通过一个工具类来计算
                    BigDecimal producePrice=BigDecimalUtil.mult(product.getPrice().doubleValue(),cartProductVo.getQuantity());
                    //设置该商品的总价格
                    cartProductVo.setProductTotalPrice(producePrice);
                }
                //如果该商品在购物车中被勾选  则将价格加入总价格中
                if(cartItem.getChecked()==ProductEnum.CheckOrNotCheck.CHECKED)
                {
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        CartVo cartVo=new CartVo();
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        //cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getValue("ftp.server.http.prefix"));
        return cartVo;
    }


    private boolean getAllCheckedStatus(Integer userId)
    {
        int resultCount=cartMapper.getAllCheckesStatus(userId);
        if(resultCount>0)
            return false;
        return true;
    }

}
