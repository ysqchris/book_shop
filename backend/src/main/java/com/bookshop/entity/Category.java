package com.bookshop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类实体类
 */
@Data
@TableName("categories")
public class Category {
    
    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 父分类ID（0表示顶级分类）
     */
    @TableField("parent_id")
    private Long parentId;
    
    /**
     * 分类级别（1-一级分类，2-二级分类）
     */
    @TableField("level")
    private Integer level;
    
    /**
     * 排序权重
     */
    @TableField("sort_order")
    private Integer sortOrder;
    
    /**
     * 分类图标
     */
    @TableField("icon")
    private String icon;
    
    /**
     * 分类描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 分类状态（0-正常，1-禁用）
     */
    @TableField("status")
    private Integer status;
    
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