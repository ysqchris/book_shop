package com.bookshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookshop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<Order> selectByUserId(@Param("userId") Long userId, 
                              @Param("offset") int offset, 
                              @Param("size") int size);
    
    /**
     * 根据订单状态查询订单列表
     * @param status 订单状态
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<Order> selectByStatus(@Param("status") Integer status, @Param("limit") int limit);
    
    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);
}