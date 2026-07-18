package com.bookshop.controller;

import com.bookshop.common.Result;
import com.bookshop.entity.ShopSetting;
import com.bookshop.service.ShopSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class ShopController {

    @Autowired
    private ShopSettingService shopSettingService;

    @GetMapping({"/shop/contact", "/api/shop/contact"})
    public Result<Map<String, Object>> contact() {
        return Result.success(shopSettingService.toContactMap());
    }

    @GetMapping({"/admin/shop/settings", "/api/admin/shop/settings"})
    public Result<ShopSetting> getSettings() {
        return Result.success(shopSettingService.getSettings());
    }

    @PutMapping({"/admin/shop/settings", "/api/admin/shop/settings"})
    public Result<ShopSetting> updateSettings(@RequestBody ShopSetting request) {
        try {
            return Result.success("店铺信息已保存", shopSettingService.updateSettings(request));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
