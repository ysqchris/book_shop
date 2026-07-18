package com.bookshop.service.impl;

import com.bookshop.entity.ShopSetting;
import com.bookshop.mapper.ShopSettingMapper;
import com.bookshop.service.ShopSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShopSettingServiceImpl implements ShopSettingService {

    private static final long SETTINGS_ID = 1L;

    @Autowired
    private ShopSettingMapper shopSettingMapper;

    @Override
    public ShopSetting getSettings() {
        ShopSetting setting = shopSettingMapper.selectById(SETTINGS_ID);
        if (setting == null) {
            setting = defaultSettings();
            shopSettingMapper.insert(setting);
        }
        return setting;
    }

    @Override
    public ShopSetting updateSettings(ShopSetting request) {
        if (request == null || !StringUtils.hasText(request.getShopName())) {
            throw new RuntimeException("请填写店家名称");
        }
        if (!StringUtils.hasText(request.getContactPhone())) {
            throw new RuntimeException("请填写联系电话");
        }
        if (!StringUtils.hasText(request.getAddress())) {
            throw new RuntimeException("请填写店铺地址");
        }

        ShopSetting setting = getSettings();
        setting.setShopName(request.getShopName().trim());
        setting.setContactPhone(request.getContactPhone().trim());
        setting.setAddress(request.getAddress().trim());
        setting.setWechatId(StringUtils.hasText(request.getWechatId()) ? request.getWechatId().trim() : "");
        setting.setWechatQrcode(StringUtils.hasText(request.getWechatQrcode()) ? request.getWechatQrcode().trim() : "");
        if (request.getTip() != null) {
            setting.setTip(request.getTip().trim());
        }
        setting.setUpdateTime(LocalDateTime.now());
        shopSettingMapper.updateById(setting);
        return setting;
    }

    @Override
    public Map<String, Object> toContactMap() {
        ShopSetting setting = getSettings();
        Map<String, Object> data = new HashMap<>();
        data.put("name", setting.getShopName());
        data.put("phone", setting.getContactPhone());
        data.put("address", setting.getAddress());
        data.put("wechat", setting.getWechatId());
        data.put("wechatQrcode", setting.getWechatQrcode());
        data.put("tip", setting.getTip());
        return data;
    }

    private ShopSetting defaultSettings() {
        ShopSetting setting = new ShopSetting();
        setting.setId(SETTINGS_ID);
        setting.setShopName("三定圆梦书店");
        setting.setContactPhone("");
        setting.setAddress("");
        setting.setWechatId("");
        setting.setWechatQrcode("");
        setting.setTip("下单后请电话或微信联系店家完成付款与发货，暂不支持线上支付。");
        setting.setUpdateTime(LocalDateTime.now());
        return setting;
    }
}
