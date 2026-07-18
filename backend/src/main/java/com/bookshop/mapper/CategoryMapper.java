package com.bookshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookshop.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
