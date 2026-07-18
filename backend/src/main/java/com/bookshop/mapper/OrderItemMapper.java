package com.bookshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookshop.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId} AND deleted = 0")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    @Select("SELECT * FROM order_items WHERE book_id = #{bookId} AND deleted = 0")
    List<OrderItem> selectByBookId(@Param("bookId") Long bookId);

    @Select("SELECT COALESCE(SUM(quantity), 0) FROM order_items WHERE deleted = 0")
    Long sumSoldQuantity();
}