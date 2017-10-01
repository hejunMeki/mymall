package com.mymall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mymall.common.ProductEnum;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.dao.CategoryMapper;
import com.mymall.dao.ProductMapper;
import com.mymall.pojo.Category;
import com.mymall.pojo.Product;
import com.mymall.service.ICategoryService;
import com.mymall.service.IProductService;
import com.mymall.util.DateTimeUtil;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.ProductDetailVo;
import com.mymall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/23 0023.
 * Description:Product Service层
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    /*
        无条件查询商品 列表
        return list  集合中的商品信息是重新封装的vo类
    */
    public ServerResponse<PageInfo> selectProducts(int pageNum, int pageSize)
    {
        //设置分页参数
        /*
        通过该参数  会生成一个Page对象
         Page<E> page = new Page(pageNum, pageSize, count);
         */
        PageHelper.startPage(pageNum,pageSize);
        //直接查询
        List<Product> productList=productMapper.selectProducts();
        List<ProductListVo> productListVoList= Lists.newArrayList();
        //将查询出来的product的信息封装到ProductVo中
        for(Product product:productList)
        {
            productListVoList.add(assembleProductListVo(product));
        }
        //有必要吗？？？？？？？   有必要
        //初始化会计算Page的相关信息  页数什么的
        PageInfo pageResult = new PageInfo(productList);
        //计算完了之后   再将要显示List替换掉
        pageResult.setList(productListVoList);
        return ServerResponse.CreateSuccess(pageResult);
    }
    /*
        根据id或者商品名搜索  分页
        缺点：一个都查不到是是否应该设置提醒？？
        todo productName为“”de 时候参与查询
     */
    public ServerResponse<PageInfo> productSearch(String productName,Integer productId,int pageNum,int pageSize)
    {
        //page初始化
        PageHelper.startPage(pageNum,pageSize);
        //判断条件
        if(StringUtils.isNotBlank(productName))
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
//        else
//            productName=StringUtils.EMPTY;   //防止非空productName参与查询
        //开始查询
        List<Product> list=productMapper.selectProductsByNameAndId(productName,productId);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for(Product product:list)
        {
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo=new PageInfo(list);
        pageInfo.setList(productListVoList);
        return ServerResponse.createSuccess("查找成功",pageInfo);
    }

    /*
     产品详情
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId)
    {
        //判断Id是否为空
        if(productId==null)
            return ServerResponse.createErrorByMessage("id不能为空");
        //查询   需要指定商品详情的vo类
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null)
            return ServerResponse.createErrorByMessage("商品已下架或被删除");
        return ServerResponse.CreateSuccess(assembleProductDetailVo(product));
    }
    /*
        遍历Product details到ProductDetailVo中
     */
    private ProductDetailVo assembleProductDetailVo(Product product)
    {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.parseDateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.parseDateToStr(product.getUpdateTime()));
        return productDetailVo;
    }


    /*
        遍历Product的信息到ProductVo中
     */
    private ProductListVo assembleProductListVo(Product product)
    {
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        //封装图片服务器host
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }

    /*
        后台修改商品下架
     */
    public ServerResponse<String> manageUpdateStatus(Integer productId,Integer status)
    {
        //判断id
        if(productId==null&&status==null)
            return ServerResponse.createErrorByMessage("id为空或status为空");
        //判断要修改的status   只能为0,1,2
        if(status.equals(ProductEnum.Status.ON_SALE)||status.equals(ProductEnum.Status.DELETED)||status.equals(ProductEnum.Status.OUT_OF_STOCK))
        {
            Product product=new Product();
            product.setId(productId);
            product.setStatus(status);
            //调用方法
           int resultCount= productMapper.updateByPrimaryKeySelective(product);
            if(resultCount>0)
                return ServerResponse.CreateSuccess("修改状态成功");
            return ServerResponse.createErrorByMessage("修改状态失败");
        }
        return ServerResponse.createErrorByMessage("status参数错误");

    }

    /*
        新增或修改商品
     */
    public ServerResponse<String> saveOrUpdate(Product product)
    {
        if(product==null)
            return ServerResponse.createErrorByMessage("商品信息为空，新增失败");
        //给主图赋值
        if(product.getSubImages()!=null)
        {
            //约定以,分割图片地址
         String urls[]= product.getSubImages().split(",");
         //第一个设置为主图
         product.setMainImage(urls[0]);
        }
        //判断商品是应该新增还是修改
        if(product.getId()!=null)
        {
            //如果是id存在  则修改商品
            int resultCount=productMapper.updateByPrimaryKey(product);
            if(resultCount>0)
                return ServerResponse.CreateSuccess("修改商品成功");
            return ServerResponse.createErrorByMessage("修改商品失败");
        }
        else{
            //新增商品
            int resultCount=productMapper.insert(product);
            if(resultCount>0)
                return ServerResponse.CreateSuccess("新增商品成功");
            return ServerResponse.createErrorByMessage("新增商品失败");
        }
    }

    /*
        用户前台商品动态排序
        根据条件查找排序
     */
    public ServerResponse<PageInfo> serachProductByCategoryName(Integer categoryId, String keyword,int pageNum ,int pageSize,String orderBy)
    {
        if(StringUtils.isBlank(keyword)&&categoryId==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数缺失");
        List<Integer> categoryIdList=new ArrayList<Integer>();
        //检查该种类是否存在
        if(categoryId!=null)
        {
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            //种类不存在且关键字为空 查找结果success 但是数据为空
            if(category==null&&StringUtils.isBlank(keyword))
            {
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> targetList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(targetList);
                return ServerResponse.createSuccess("查询成功",pageInfo);
            }
            //category不为空  查询所有该分类下子孙节点
            categoryIdList=iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        //开始设置排序什么的
        PageHelper.startPage(pageNum,pageSize);
        //orderBy的规则是排序字段民_排序方法
        if(StringUtils.isNotBlank(orderBy)){
            if(ProductEnum.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                //设置排序规则
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        //开始查询   blank置为空
        List<Product> resultList=productMapper.selectProductsByKeyAndId(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        //转换为vo
        List<ProductListVo> voList=Lists.newArrayList();
        for(Product product:resultList)
        {
            voList.add(assembleProductListVo(product));
        }
        //
        PageInfo pageInfo=new PageInfo(resultList);
        pageInfo.setList(voList);
        return ServerResponse.CreateSuccess(pageInfo);
    }

   /*
    前台商品详情获取
     */
   public ServerResponse<ProductDetailVo> getDetail(Integer productId)
   {
       if(productId==null)
           return ServerResponse.createErrorByMessage("id为空");
       //查询
      Product product= productMapper.selectByPrimaryKey(productId);
      if(product==null)
          return ServerResponse.createErrorByMessage("该商品不存在或已被删除");

      if(product.getStatus().intValue()!=ProductEnum.Status.ON_SALE.intValue())
          return ServerResponse.createErrorByMessage("该商品已下架或被删除");
      ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return  ServerResponse.CreateSuccess(productDetailVo);
   }




}
