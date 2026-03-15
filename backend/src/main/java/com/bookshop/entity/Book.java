package com.bookshop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书实体类
 */
@Data
@TableName("books")
public class Book {
    
    /**
     * 图书ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 图书标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 图书作者
     */
    @TableField("author")
    private String author;
    
    /**
     * 图书ISBN
     */
    @TableField("isbn")
    private String isbn;
    
    /**
     * 出版社
     */
    @TableField("publisher")
    private String publisher;
    
    /**
     * 出版日期
     */
    @TableField("publish_date")
    private String publishDate;
    
    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;
    
    /**
     * 图书价格
     */
    @TableField("price")
    private BigDecimal price;
    
    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    /**
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;
    
    /**
     * 图书封面图片
     */
    @TableField("cover_image")
    private String coverImage;
    
    /**
     * 图书描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 图书状态（0-上架，1-下架）
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 卖家用户ID
     */
    @TableField("seller_id")
    private Long sellerId;
    
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