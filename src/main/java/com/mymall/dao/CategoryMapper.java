package com.mymall.dao;

import com.mymall.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
    //根据parentID 查找统计子节点
    List<Category> selectCategoryChildrenByParentId(Integer categoryId);
    //添加category

}