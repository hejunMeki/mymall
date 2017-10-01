package com.mymall.controller.portal;

import com.mymall.common.ServerResponse;
import com.mymall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/9/24 0024.
 * Description:前台商品
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /*
        产品动态排序
        /product/list.do
        categoryId
        keyword
        pageNum(default=1)
        pageSize(default=10)
        orderBy(default="")：排序参数：例如price_desc，price_asc
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(Integer categoryId, String keyword,
                               @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                               @RequestParam(value = "orderBy",defaultValue = "") String orderBy)
    {
            return iProductService.serachProductByCategoryName(categoryId,keyword,pageNum,pageSize,orderBy);
    }


    /*
        前台商品详情
         /product/detail.do
         productId
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(Integer productId)
    {
        return iProductService.getDetail(productId);
    }
}
