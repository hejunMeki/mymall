package com.mymall.service.Impl;

import com.google.common.collect.Lists;
import com.mymall.common.ServerResponse;
import com.mymall.dao.CategoryMapper;
import com.mymall.dao.UserMapper;
import com.mymall.pojo.Category;
import com.mymall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Administrator on 2017/9/22 0022.
 * Description:商品分类服务层
 *
 */
@Service
public class CategoryServiceImpl implements ICategoryService {


    //引入usermapper
    @Autowired
    private CategoryMapper categoryMapper;


    /*
        获取品类子节点（平级）
     */

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId)
    {
        if(categoryId==null)
            return ServerResponse.createErrorByMessage("参数异常");
        //根据categoryId查询平级节点
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isNotEmpty(categoryList))
            return ServerResponse.createSuccess("查询成功",categoryList);
       return ServerResponse.createErrorByMessage("未查找到该分类");
    }

    /*
        递归获取所有子孙节点的id集合
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId)
    {
        if(categoryId==null)
            return ServerResponse.createErrorByMessage("参数异常");
        //set防重
        Set<Category> categorySet=new HashSet<Category>();
        //开始遍历
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList=Lists.newArrayList();
        if(categorySet!=null)
        {
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createSuccess("查询成功",categoryIdList);
    }

    /*
    子孙节点递归方法   集合set存放元素可以防重
    每次搞定一个
     */
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId)
    {
        //查询该节点是否存在
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null)
            categorySet.add(category);
        //对该节点的子节点进行遍历  结束条件selectCategoryChildrenByParentId查询解控为空自动return
        List<Category> list=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category cate:list)
            findChildCategory(categorySet,cate.getId());
        return categorySet;
    }

    /*
        根据商品的id修改商品名称
     */
    public ServerResponse<String> updateCategoryNameById(Integer categoryId,String categoryName)
    {
        //判空
        if(categoryId==null|| StringUtils.isBlank(categoryName))
            return ServerResponse.createErrorByMessage("更新品类参数错误");
        //包装要修改的category   选择性修改
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        //选择性更新
        int resultCount=categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount>0)
            return ServerResponse.createBySuccessMessage("商品种类名字修改成功");
        return ServerResponse.createErrorByMessage("商品种类名字修改失败");
    }

    //添加category节点
    public ServerResponse<String> addCategory(Integer parentId, String categoryName)
    {
        //categoryName是否为空   为什么不判断parentId  因为controller中设置了默认值0
        if(StringUtils.isBlank(categoryName))
            return ServerResponse.createErrorByMessage("品类名为空或不符合规则");
        //开始添加
        Category category=new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);    //设置上架
        //时间在sql语句中插入
       int resultCount=categoryMapper.insert(category);
        if(resultCount>0)
            return ServerResponse.createBySuccessMessage("添加品类成功");
        return ServerResponse.createErrorByMessage("添加品类失败");
    }

}
