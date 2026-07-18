# 三定圆梦书店 · 用户端小程序

与 Web 用户端（`frontend-user`）并行的微信小程序方案，共用同一套后端 API。

## 功能

- 浏览图书（分类 / 搜索 / 排序 / 分页）
- 图书详情、加入购物车、立即购买
- 本地购物车（无需登录）
- 线下付款下单，展示店家电话 / 地址 / 微信二维码
- 本机订单列表与订单详情

## 目录

```
miniprogram/
├── app.js / app.json / app.wxss
├── config/env.js          # 后端地址
├── utils/                 # request、api、cart
├── pages/                 # 选书、详情、购物车、下单、订单
└── assets/                # logo 与 tab 图标
```

## 本地运行

1. 启动后端（默认 `http://127.0.0.1:8080`）
2. 用[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)导入本目录 `miniprogram`
3. AppID 可先用测试号 / 游客模式
4. 详情 → 本地设置 → 勾选「不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书」
5. 真机调试时，把 `config/env.js` 里的 `baseUrl` 改成电脑的局域网 IP，例如：

```js
baseUrl: 'http://192.168.1.8:8080'
```

## 上线前

- 将 `baseUrl` 改为 HTTPS 域名
- 在微信公众平台配置 request 合法域名
- 将 `project.config.json` 中的 `appid` 换成正式小程序 AppID
