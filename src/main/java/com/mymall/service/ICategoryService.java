package com.mymall.service;

import com.mymall.common.ServerResponse;
import com.mymall.pojo.Category;

import java.util.List;

/**
 * Created by Administrator on 2017/9/22 0022.
 * Description:category
 */
public interface ICategoryService {
    //根据id修改品类名
    ServerResponse<String> updateCategoryNameById(Integer categoryId, String categoryName);
    //获取品类子节点（平级）
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    //获取某节点的所有子孙节点的id集合
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
    //添加指定名称的节点名
    ServerResponse<String> addCategory(Integer parentId, String categoryName);
}
