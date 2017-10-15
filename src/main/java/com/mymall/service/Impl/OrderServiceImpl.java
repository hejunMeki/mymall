package com.mymall.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.PreOrderResult;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mymall.common.OrderEnum;
import com.mymall.common.ProductEnum;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.dao.*;
import com.mymall.pojo.*;
import com.mymall.service.IOrderService;
import com.mymall.util.BigDecimalUtil;
import com.mymall.util.DateTimeUtil;
import com.mymall.util.FtpUtil;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.OrderItemVo;
import com.mymall.vo.OrderProductVo;
import com.mymall.vo.OrderVo;
import com.mymall.vo.ShippingVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017/9/29 0029.
 * Description:订单业务层
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService{
    //日志声明
    private static final Logger logger= LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    /*
        创建订单 并返回订单视图
         todo 这里存在订单横向越权的问题  即可能地址填写其他的地址
     */
    public ServerResponse createOrder(Integer userId,Integer shippingId)
    {
        //todo验证地址
        if(shippingId==null)
            return ServerResponse.createErrorByMessage("地址未选择");
        //1、根据用户id查询购物车中勾选的商品列表
        List<Cart> cartList=cartMapper.getCartListByUserId(userId);
        //2、这个订单的总价
        //详细步骤  分析购物车中的每一个商品 检验商品状态和库存等信息
        ServerResponse<List<OrderItem>> serverResponse=this.createCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess())
            return serverResponse;
        //3、计算购物车已选商品的总价格
        List<OrderItem> orderItemList=serverResponse.getData();
        if (CollectionUtils.isEmpty(orderItemList))
            return ServerResponse.createErrorByMessage("购物车为空");
        BigDecimal payTotal=getOrderTotalPrice(orderItemList);
        //4、生成订单信息
        Order order=this.assembleOrder(userId,payTotal,shippingId);
        if(order==null)
            return ServerResponse.createErrorByMessage("订单生成失败");
        //order表的订单生成之后  为之前orderItemList表中的每一个orderItem赋订单号
        for (OrderItem orderItem:
                orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        //5、将orderItem批量插入orderItem中
        int resultCount=orderItemMapper.batchInsertOrderItem(orderItemList);
        //todo   这个判断需要有吗
        if(resultCount<1)
            return ServerResponse.createErrorByMessage("订单生成失败");
        //6、订单生成插入之后  就可以减少库存了
        Boolean isReduce=this.reduceProducrStock(orderItemList);
        if(!isReduce)
            return ServerResponse.createErrorByMessage("库存修改失败");
        //7、清空购物车
        this.cleanCart(cartList);
        //返回vo数据给前端  orderVo
        //详细描述：需要返回order数据、orderItem数据集合(orderItemVo)，shipping地址(shippingVo)
        OrderVo orderVo=assembleOrderVo(order,orderItemList);
        return ServerResponse.CreateSuccess(orderVo);
    }
    /*
        分析购物车中的每一个商品 检验商品状态和库存等信息
        生成购物车已选商品的详细订单集合
     */
    private ServerResponse<List<OrderItem>> createCartOrderItem(Integer userId,List<Cart> cartList)
    {
        //检查是否为空
        if (CollectionUtils.isEmpty(cartList))
            return ServerResponse.createErrorByMessage("购物车中没有选中商品");
        //orderitem的集合
        List<OrderItem> orderItemList=Lists.newArrayList();
        //遍历该集合
        for(Cart cartItem:cartList){
            OrderItem orderItem=new OrderItem();
            //根据product查询当前的产品
            Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
            //检查当前商品的状态
            if(product.getStatus()!= ProductEnum.Status.ON_SALE)
                return ServerResponse.createErrorByMessage("该商品已下架或删除");
            //检查库存
            if(cartItem.getQuantity()>product.getStock())
                return ServerResponse.createErrorByMessage("库存不足");
            //然后正常的装配orderItem   此时还并没有生成订单号
            orderItem.setUserId(cartItem.getUserId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mult(product.getPrice().doubleValue(),cartItem.getQuantity()));
            //添加
            orderItemList.add(orderItem);
        }
        return ServerResponse.CreateSuccess(orderItemList);
    }

    /*
        计算购物车已选商品的总价格
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList)
    {
        BigDecimal payTotal=new BigDecimal("0");
        for (OrderItem orderItem:
                orderItemList ) {
            payTotal=BigDecimalUtil.add(payTotal.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payTotal;
    }
    //生成未付款订单   并返回该订单
    private Order assembleOrder(Integer userId,BigDecimal payTotal,Integer shippingId)
    {
        Order order=new Order();
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payTotal);
        //设置支付的方式  目前仅支持在线支付
        order.setPaymentType(ProductEnum.PaymentType.PAY_ONLINE.getCode());
        //设置支付状态为未付款  10
        order.setStatus(OrderEnum.OrderStatusEnum.NO_PAY.getCode());
        //设置邮费 目前未计算邮费  初始为0
        order.setPostage(0);

        //生成订单号   订单号生成有自己的规则  业务初期可以简单设置如下
        Long orderNo=this.generateOrderNo();
        order.setOrderNo(orderNo);
        //order的基本信息封装完毕 写入order数据库
        int resultCount=orderMapper.insert(order);
        if(resultCount>0)
            return order;
        return null;


    }
    //生成订单号
    private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }
    //减少商品库存
    private boolean reduceProducrStock(List<OrderItem> orderItemList)
    {
        boolean isReduce=true;
        for (OrderItem orderItem:
                orderItemList) {
            //查询商品
            Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            //修改商品的库存
            product.setStock(product.getStock()-orderItem.getQuantity());
            //修改
            int resultCount=productMapper.updateByPrimaryKeySelective(product);
            if(resultCount<1)
                isReduce=false;
        }
         return isReduce;
    }
    //清空购物车
    private void cleanCart(List<Cart> cartList){
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    //返回vo数据给前端  orderVo
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList)
    {
        //1、先将order中的值提取出来
        OrderVo orderVo=this.assembleOrderVoByOrder(order);
        //2、开始提取orderItem中的值
        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        for (OrderItem orderItem:orderItemList) {
            OrderItemVo orderItemVo=this.assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        //地址id赋值
        orderVo.setShippingId(order.getShippingId());
        //地址查询
        Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());
        //图片服务器
        orderVo.setImageHost(PropertiesUtil.getValue("ftp.server.http.prefix"));
        if(shipping!=null)
        {
            //收货人
            orderVo.setReceiverName(shipping.getReceiverName());
            //将shipping的值赋值给shippingVo
            ShippingVo shippingVo=this.assembleShippingVo(shipping);
            orderVo.setShippingVo(shippingVo);
        }
        return orderVo;

    }

    //将order的值赋给orderVo
    private OrderVo assembleOrderVoByOrder(Order order)
    {
        if(order==null)
            return null;
        OrderVo orderVo=new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setStatus(order.getStatus());
        //获取Status的描述信息
        orderVo.setStatusDesc(OrderEnum.OrderStatusEnum.getValue(order.getStatus()));
        orderVo.setPayment(order.getPayment());
        orderVo.setPostage(order.getPostage());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(ProductEnum.PaymentType.getValue(order.getPaymentType()));
        orderVo.setCreateTime(DateTimeUtil.parseDateToStr(order.getCreateTime()));
        orderVo.setPaymentTime(DateTimeUtil.parseDateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.parseDateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.parseDateToStr(order.getEndTime()));
        orderVo.setCloseTime(DateTimeUtil.parseDateToStr(order.getCloseTime()));
        return orderVo;
    }
    //将orderItem中要返回给前端的值赋值给orderItemVo
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem)
    {
        if(orderItem==null)
            return null;
        OrderItemVo orderItemVo=new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.parseDateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
    //将Shipping中要返回给前端的值赋值给ShippingVo
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    /*
        订单 生成前查看订单中的商品信息
    */
    public ServerResponse getOrderProducts(Integer userId)
    {
        //查询购物车中选中的商品
        List<Cart> cartList=cartMapper.getCartListByUserId(userId);
        //根据cartList查看商品
       ServerResponse response=this.createCartOrderItem(userId,cartList);
       if(!response.isSuccess())
           return response;
       //将orderIｔｅｍ取出来并转换为ｏｒｄｅｒＩｔｅｍＶｏ
        List<OrderItem> orderItemList=(List<OrderItem>)response.getData();
        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        //计算总价格
        BigDecimal totalPrice=new BigDecimal("0");
        for (OrderItem orderItem:orderItemList
             ) {
            totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
            OrderItemVo orderItemVo=assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        //封装
        OrderProductVo orderProductVo=new OrderProductVo();
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(totalPrice);
        orderProductVo.setImageHost(PropertiesUtil.getValue("ftp.server.http.prefix"));
        return ServerResponse.CreateSuccess(orderProductVo);
    }

    /*
        分页获取所有订单信息
     */
    public ServerResponse<PageInfo> getOrderListByPage(Integer userId,int pageSize,int pageNum)
    {
        PageHelper.startPage(pageNum,pageSize);
        //根据用户id在order表中查询所有的订单
        List<Order> orderList=orderMapper.selectOrdersByUserId(userId);
        //查询orderItem的集合 并封装为orderVo的集合
        List<OrderVo> orderVoList=assembleOrderVo(userId,orderList);
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.CreateSuccess(pageInfo);
    }

    private List<OrderVo> assembleOrderVo(Integer userId,List<Order> orderList)
    {
        List<OrderVo> orderVoList=Lists.newArrayList();
        List<OrderItem> orderItemList=Lists.newArrayList();
        for (Order order:orderList) {
            if(userId==null)
                orderItemList=orderItemMapper.selectOrderItemsByOrderId(order.getOrderNo());
            else{
                orderItemList=orderItemMapper.selectOrderItemsById(userId,order.getOrderNo());
            }
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    /*
     获取订单的详细信息
     */
    public ServerResponse getOrderDetails(Integer userId,Long orderNo){
        //查询订单是否存在
        Order order=orderMapper.selectOrderById(userId,orderNo);
        if(order!=null)
        {
            List<OrderItem> orderItemList=orderItemMapper.selectOrderItemsById(userId,orderNo);
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            return ServerResponse.CreateSuccess(orderVo);
        }
        return ServerResponse.createErrorByMessage("订单不存在");
    }

    /*
        取消订单
        todo  取消订单时  库存是否要修改？？
     */
    public ServerResponse<String> orderCancel(Integer userId,Long orderNo)
    {
        //查看该订单是否存在
        Order order=orderMapper.selectOrderById(userId,orderNo);
        if(order==null)
            return ServerResponse.createErrorByMessage("该订单不存在");
        //查看该订单是否已经付款  付款订单无法取消
        if(order.getStatus()!=OrderEnum.OrderStatusEnum.NO_PAY.getCode())
        {
            return ServerResponse.createErrorByMessage("无法退款");
        }
        Order updateOrder=new Order();
        //updateOrder.setOrderNo(order.getOrderNo());
        updateOrder.setId(order.getId());
        updateOrder.setStatus(OrderEnum.OrderStatusEnum.CANCELED.getCode());
        //修改
        int resultCount=orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(resultCount>0)
            return ServerResponse.CreateSuccess("取消订单成功");
        return ServerResponse.createErrorByMessage("取消订单失败");
    }


    //-------------------------backup
    /*
     分页查询所有订单的orderVo
     */
    public ServerResponse<PageInfo> getAllOrderVoByPage(int pageNum,int pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        //查询列表
        List<Order> orderList=orderMapper.selectAll();
        List<OrderVo> orderVoList=assembleOrderVo(null,orderList);
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.CreateSuccess(pageInfo);
    }

    /*
      第一期：订单号绝对查询  但是做分页处理
     */
    public ServerResponse<PageInfo> orderSerach(Long orderNo,int pageNum,int pageSize)
    {
        //查询该订单是否存在
        Order order=orderMapper.selectOrderByOrderId(orderNo);
        if(order==null)
            return ServerResponse.createErrorByMessage("该订单不存在");
        PageHelper.startPage(pageNum,pageSize);
        List<OrderItem> orderItemList=orderItemMapper.selectOrderItemsByOrderId(orderNo);
        OrderVo orderVo=assembleOrderVo(order,orderItemList);
        PageInfo pageInfo=new PageInfo(Lists.newArrayList(order));

        pageInfo.setList(Lists.newArrayList(orderVo));
        return ServerResponse.CreateSuccess(pageInfo);
    }

    /*
      指定订单的详情
     */
    public ServerResponse<OrderVo> orderDetail(Long orderNo)
    {
        //查询该订单是否存在
        Order order=orderMapper.selectOrderByOrderId(orderNo);
        if(order==null)
            return ServerResponse.createErrorByMessage("该订单不存在");
        List<OrderItem> orderItemList=orderItemMapper.selectOrderItemsByOrderId(orderNo);
        OrderVo orderVo=assembleOrderVo(order,orderItemList);
        return ServerResponse.CreateSuccess(orderVo);
    }

    /*
    订单发货
     */
    public ServerResponse<String> orderSendFoods(Long orderNo)
    {
        //查询该订单是否存在
        Order order=orderMapper.selectOrderByOrderId(orderNo);
        if(order==null)
            return ServerResponse.createErrorByMessage("该订单不存在");
       //查询该订单的状态是否为已付款
        if(order.getStatus()== OrderEnum.OrderStatusEnum.PAID.getCode())
        {
            //修改状为已发货
            Order updateOrder=new Order();
            //order.setOrderNo(orderNo);
            updateOrder.setId(order.getId());
            //发货时间
           updateOrder.setSendTime(new Date());
            //已发货
            updateOrder.setStatus(OrderEnum.OrderStatusEnum.SHIPPED.getCode());
            int resultCount=orderMapper.updateByPrimaryKeySelective(updateOrder);
            if (resultCount>0)
                return ServerResponse.CreateSuccess("发货成功");
        }
        return ServerResponse.createErrorByMessage("发货失败");

    }













    public ServerResponse<Map<String,String>> pay(Long orderNo,Integer userId,String path) {
        //判断订单号
        if (orderNo == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //1、查询该用户 该订单是否存在 订单存在 组装参数
        Order order = orderMapper.selectOrderById(userId, orderNo);
        if (order == null)
            return ServerResponse.createErrorByMessage("订单不存在");
        //如果用户存在 组装参数  将参数存在map中
        Map<String, String> resultMap = Maps.newHashMap();
        //将订单号存入
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject =  new StringBuilder().append("mymall扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body =  new StringBuilder().append("购买商品").append(outTradeNo).append("共花费").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsById(userId, orderNo);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail good = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), BigDecimalUtil.mult(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(), orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(good);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getValue("alipay.callback"))   //回调地址
                .setGoodsDetailList(goodsDetailList);

        //加载配置文件
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 将生成的支付二维码持久化  并且存放在图片服务器上面
               File file=new File(path);
               if(!file.exists())
               {
                   file.setWritable(true);
                   file.mkdirs();
               }
               //声明二维码路径
                String filePath = String.format(path+"/qr-%s.png",
                        response.getOutTradeNo());
                //生成支付二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                //文件名  提取url时需要
                String fileName=String.format("qr-%s.png",
                        response.getOutTradeNo());
                File targetFile=new File(path,fileName);
                //从upload中上传支付二维码
                try {
                    FtpUtil.uploadFile(Lists.newArrayList(targetFile));
                    //todo 要不要对上传成功的判断  true  or   false
                } catch (IOException e) {
                    logger.error("上传失败",e);
                }
                logger.info("filePath:" + filePath);
                //ftp中支付二维码的路径
                String qrUrl=PropertiesUtil.getValue("ftp.server.http.prefix")+targetFile.getName();
                //放入map中
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.CreateSuccess(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createErrorByMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createErrorByMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createErrorByMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }


    /*
        验证回调的参数
        ？？？？如果一直没有付款 然后一直回调？？？然后多次记录payINfo信息？
        事实表明  支付宝在用户付款成功后才会回调 只插入一次
     */
    public ServerResponse checkAlipayCallBack(Map<String,String> params)
    {
        //获取
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        //根据订单号查询订单是否存在
        Order order=orderMapper.selectOrderByOrderId(Long.valueOf(orderNo));
        if(order==null)
            return ServerResponse.createErrorByMessage("订单不存在，回调忽略");
        //支付交易号
        String tradeNo = params.get("trade_no");
        //交易状态
        String tradeStatus = params.get("trade_status");
        //判断交易的状态
        if(order.getStatus()>=OrderEnum.OrderStatusEnum.PAID.getCode())
            return ServerResponse.CreateSuccess("已付款，不需要再回调");
        //如果没有成功支付  等待回调 回调支付成功 修改支付状态
        if(OrderEnum.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus))
        {
            //回调支付成功  修改订单表的支付状态
            //修改订单交易时间
            order.setPaymentTime(DateTimeUtil.parseStringToDate(params.get("gmt_payment")));
            //修改订单状态为已支付
            order.setStatus(OrderEnum.OrderStatusEnum.PAID.getCode());
            //将修改写入数据库
            orderMapper.updateByPrimaryKeySelective(order);
        }
        //
        PayInfo payInfo = new PayInfo();
        //用户id
        payInfo.setUserId(order.getUserId());
        //订单号
        payInfo.setOrderNo(order.getOrderNo());
        //支付方式
        payInfo.setPayPlatform(OrderEnum.PayPlatformEnum.ALIPAY.getCode());
        //交易号
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        //插入数据库
       // System.out.println("已插入");
        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();

    }

    /*
        查询订单师傅状态
     */
    public ServerResponse checkOrderPayStatus(Integer userId,Long orderNo)
    {
        //查询订单是否存在
        Order order=orderMapper.selectOrderById(userId,orderNo);
        if(order==null)
            return ServerResponse.createErrorByMessage("订单不存在");
        //订单存在  查询订单的支付状态
        if(order.getStatus()>=OrderEnum.OrderStatusEnum.PAID.getCode())
            return ServerResponse.createBySuccess();
        return ServerResponse.createError();
    }









}
