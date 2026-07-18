package com.bookshop.service;

import com.bookshop.entity.ShopSetting;

import java.util.Map;

public interface ShopSettingService {
    ShopSetting getSettings();

    ShopSetting updateSettings(ShopSetting request);

    Map<String, Object> toContactMap();
}
