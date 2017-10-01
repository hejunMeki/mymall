package com.mymall.service.Impl;

import com.alipay.api.AlipayResponse;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mymall.common.OrderEnum;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.dao.OrderItemMapper;
import com.mymall.dao.OrderMapper;
import com.mymall.dao.PayInfoMapper;
import com.mymall.pojo.Order;
import com.mymall.pojo.OrderItem;
import com.mymall.pojo.PayInfo;
import com.mymall.service.IOrderService;
import com.mymall.util.BigDecimalUtil;
import com.mymall.util.DateTimeUtil;
import com.mymall.util.FtpUtil;
import com.mymall.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
