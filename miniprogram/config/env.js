/**
 * 小程序接口配置
 * - 开发：填本机局域网 IP（微信开发者工具勾选「不校验合法域名」）
 * - 上线：改为 HTTPS 域名，并在公众平台配置 request 合法域名
 */
const ENV = {
  // 示例：'http://192.168.1.8:8080'
  baseUrl: 'http://127.0.0.1:8080',
  shopName: '三定圆梦书店'
}

module.exports = ENV
