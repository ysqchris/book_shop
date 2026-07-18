package com.bookshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("shop_settings")
public class ShopSetting {

    @TableId(type = IdType.INPUT)
    private Long id;

    @TableField("shop_name")
    private String shopName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("address")
    private String address;

    @TableField("wechat_id")
    private String wechatId;

    @TableField("wechat_qrcode")
    private String wechatQrcode;

    @TableField("tip")
    private String tip;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
