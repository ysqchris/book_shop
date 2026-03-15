package com.bookshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookshop.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类数据访问接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 查询所有顶级分类
     * @return 顶级分类列表
     */
    List<Category> selectTopLevelCategories();
    
    /**
     * 根据父分类ID查询子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order")
    List<Category> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查询所有分类（树形结构）
     * @return 分类列表
     */
    List<Category> selectAllCategories();
    
    @Select("SELECT * FROM categories WHERE level = #{level} AND deleted = 0 ORDER BY sort_order")
    List<Category> selectByLevel(@Param("level") Integer level);
}