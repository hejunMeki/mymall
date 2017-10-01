package com.mymall.controller.backend;

import ch.qos.logback.core.joran.conditional.ElseAction;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mymall.common.Const;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.Product;
import com.mymall.pojo.User;
import com.mymall.service.IFileSercice;
import com.mymall.service.IProductService;
import com.mymall.service.IUserService;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/23 0023.
 * Description:商品
 */
@Controller("manageProductConroller")
@RequestMapping("/manage/product")
public class ProductController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    //注入文件上传server
    @Autowired
    private IFileSercice iFileSercice;
    /*
        /manage/product/list.do
        pageNum(default=1)
        pageSize(default=10)
        查找集合
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize)
    {
        //检查是否是登录
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createErrorByMessage("未登录");
        if(iUserService.checkAdminRole(user).isSuccess())
            return iProductService.selectProducts(pageNum,pageSize);
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }


    /*
        产品搜索  /manage/product/search.do
        productName    非必须条件
        productId       非必须条件
        pageNum(default=1)   有默认值
        pageSize(default=10)  吃有默认值
     */
    @RequestMapping("serach.do")
    @ResponseBody
    public ServerResponse<PageInfo> serach(HttpSession session,@RequestParam(value = "productName",required = false) String productName,@RequestParam(value = "productId",required = false)Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createErrorByMessage("未登录");
        if(iUserService.checkAdminRole(user).isSuccess())
            return iProductService.productSearch(productName,productId,pageNum,pageSize);
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");

    }
    /*
        图片上传
        /manage/product/upload.do
        file   非必须参数
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "file",required = false) MultipartFile file, HttpServletRequest request)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createErrorByMessage("未登录");
        if(iUserService.checkAdminRole(user).isSuccess())
        {
            //获取上传到服务器的地址 从项目根目录获取  todo  了解
            String path=request.getSession().getServletContext().getRealPath("upload");
            //上传
            String targetFilename=iFileSercice.pictureUpload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mymall.com/")+targetFilename;
            Map<String,String> resultMap=new HashMap<String,String>();
            resultMap.put("uri",targetFilename);
            resultMap.put("url",url);
            return ServerResponse.CreateSuccess(resultMap);
        }
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }


    /*
        产品详情
        /manage/product/detail.do
       request       productId
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session,Integer productId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createErrorByMessage("未登录");
        if(iUserService.checkAdminRole(user).isSuccess())
            return iProductService.manageProductDetail(productId);
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }
    /*
       产品下架 status 0标识在售 1标识下架 2标识已删除
        /manage/product/set_sale_status.do
       request productId status
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createErrorByMessage("未登录");
        if(iUserService.checkAdminRole(user).isSuccess())
            return iProductService.manageUpdateStatus(productId,status);
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }

    /*
        新增或修改商品
        /manage/product/save.do
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse save(HttpSession session, Product product)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
            return ServerResponse.createErrorByMessage("未登录");
        if(iUserService.checkAdminRole(user).isSuccess())
            return iProductService.saveOrUpdate(product);
        else
            return ServerResponse.createErrorByMessage("无权限，需要管理员登录");
    }
    /*
        富文本上传图片
        /manage/product/richtext_img_upload.do
     */

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileSercice.pictureUpload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getValue("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            //这个表头指定添加
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }

}
