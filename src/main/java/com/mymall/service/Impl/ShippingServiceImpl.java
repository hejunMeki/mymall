package com.mymall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.dao.ShippingMapper;
import com.mymall.pojo.Shipping;
import com.mymall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/26 0026.
 * Description:收货地产业务层
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
    @Autowired
    private ShippingMapper shippingMapper;
    /*
        添加地址
        返回添加的地址id
        需要在mapper.xml中设置 useGeneratedKeys="true" keyProperty="id"
     */
    public ServerResponse<Map<String,Integer>> addShipping(Shipping shipping)
    {
        if(shipping==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        int resultCount=shippingMapper.insert(shipping);
        if(resultCount>0)
        {
            Map<String,Integer> resultMap=new HashMap<String,Integer>();
            resultMap.put("shippingId",shipping.getId());
            return ServerResponse.createSuccess("新建地址成功",resultMap);
        }
        return ServerResponse.createErrorByMessage("创建地址失败");
    }

    /*
        根据地址地址id删除地址
        要绑定用户id删除
     */
    public ServerResponse delShippingById(Integer userId,Integer shippingId)
    {
        if(shippingId==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //正常删除
        int resultCount=shippingMapper.delShippingById(userId,shippingId);
        if(resultCount>0)
        {
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createErrorByMessage("删除地址失败");
    }

    /*
        登录状态修改地址
        要防止横向越权
        地址的id不能被修改
        不用再次返回地址的id
        前台有id
     */
    public ServerResponse updateShipping(Shipping shipping)
    {
        if(shipping==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //开始修改操作
        int resultCount=shippingMapper.updateByPrimaryId(shipping);
        if(resultCount>0)
        {
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createErrorByMessage("更新地址失败");
    }


    /*
        根据地址id和用户id绑定查找地址信息
     */
    public ServerResponse selectShipping(Integer userId,Integer shippingId)
    {
        if(shippingId==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        //开始查询操作
        Shipping shipping=shippingMapper.selectShippingById(userId,shippingId);
        if(shipping!=null)
        {
            return ServerResponse.createSuccess("查询地址成功",shipping);
        }
        return ServerResponse.createErrorByMessage("地址不存在");
    }

    /*
        分页查询地址
    */
    public ServerResponse<PageInfo> getShippingList(Integer userId,int pageNum,int pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        //查询
        List<Shipping> shippingList=shippingMapper.selectListByUserId(userId);
        if(shippingList!=null)
        {
            PageInfo pageInfo=new PageInfo(shippingList);
            return ServerResponse.createSuccess("查询成功",pageInfo);
        }
        return ServerResponse.createErrorByMessage("地址不存在");
    }
}
