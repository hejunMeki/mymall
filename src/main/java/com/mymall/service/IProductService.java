package com.mymall.service;

import com.github.pagehelper.PageInfo;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.Product;
import com.mymall.vo.ProductDetailVo;

/**
 * Created by Administrator on 2017/9/23 0023.
 * Description:Product Service接口方法
 */
public interface IProductService {
    //后台无条件查询商品列表
    ServerResponse<PageInfo> selectProducts(int pageNum, int pageSize);
    //后台根据id或者商品名搜索  分页
    ServerResponse<PageInfo> productSearch(String productName,Integer productId,int pageNum,int pageSize);
    //后台商品详情
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    //后台修改商品的状态
    ServerResponse<String> manageUpdateStatus(Integer productId,Integer status);
    //修改或新增商品
    ServerResponse<String> saveOrUpdate(Product product);
    //用户前台商品动态排序 根据条件查找排序
    ServerResponse<PageInfo> serachProductByCategoryName(Integer categoryId, String keyword,int pageNum ,int pageSize,String orderBy);
    //前台获取商品详情
    ServerResponse<ProductDetailVo> getDetail(Integer productId);
}
