package com.bookshop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_items")
public class OrderItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("book_id")
    private Long bookId;
    
    @TableField("book_title")
    private String bookTitle;
    
    @TableField("book_cover")
    private String bookCover;
    
    @TableField("book_price")
    private BigDecimal bookPrice;
    
    @TableField("quantity")
    private Integer quantity;
    
    @TableField("subtotal")
    private BigDecimal subtotal;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}