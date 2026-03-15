package com.bookshop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order {
    
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    /**
     * 实际支付金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;
    
    /**
     * 订单状态（0-待支付，1-已支付，2-已发货，3-已完成，4-已取消）
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 支付方式（0-支付宝，1-微信，2-银行卡）
     */
    @TableField("pay_type")
    private Integer payType;
    
    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;
    
    /**
     * 发货时间
     */
    @TableField("ship_time")
    private LocalDateTime shipTime;
    
    /**
     * 收货人姓名
     */
    @TableField("receiver_name")
    private String receiverName;
    
    /**
     * 收货人电话
     */
    @TableField("receiver_phone")
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    @TableField("receiver_address")
    private String receiverAddress;
    
    /**
     * 订单备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标记
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}