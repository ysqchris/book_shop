package com.bookshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookshop.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 图书数据访问接口
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
    
    /**
     * 根据分类ID查询图书列表
     * @param categoryId 分类ID
     * @return 图书列表
     */
    List<Book> selectByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据关键词搜索图书
     * @param keyword 关键词
     * @return 图书列表
     */
    List<Book> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 根据卖家ID查询图书列表
     * @param sellerId 卖家ID
     * @return 图书列表
     */
    List<Book> selectBySellerId(@Param("sellerId") Long sellerId);
    
    @Select("SELECT * FROM books WHERE category_id = #{categoryId} LIMIT #{offset}, #{size}")
    List<Book> selectBooks(@Param("offset") int offset, 
                          @Param("size") int size, 
                          @Param("categoryId") Long categoryId,
                          @Param("keyword") String keyword);
    
    @Select("SELECT * FROM books ORDER BY create_time DESC LIMIT #{limit}")
    List<Book> selectHotBooks(@Param("limit") int limit);
    
    @Select("SELECT * FROM books WHERE category_id IN (SELECT category_id FROM user_interests WHERE user_id = #{userId}) LIMIT #{limit}")
    List<Book> selectRecommendBooks(@Param("userId") Long userId, @Param("limit") int limit);
}