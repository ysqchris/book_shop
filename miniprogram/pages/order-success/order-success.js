const api = require('../../utils/api')
const { resolveUrl } = require('../../utils/request')

Page({
  data: {
    loading: true,
    order: null,
    payAmount: '0.00',
    shopContact: null
  },

  onLoad(query) {
    this.orderId = query.id
    this.loadDetail()
  },

  async loadDetail() {
    this.setData({ loading: true })
    try {
      const res = await api.getOrderDetail(this.orderId)
      const order = res.data.order
      const shop = res.data.shopContact || {}
      this.setData({
        order,
        payAmount: Number(order.payAmount).toFixed(2),
        shopContact: {
          ...shop,
          wechatQrcodeUrl: resolveUrl(shop.wechatQrcode)
        }
      })
    } catch (e) {
      this.setData({ order: null })
    } finally {
      this.setData({ loading: false })
    }
  },

  previewQrcode(e) {
    const url = e.currentTarget.dataset.url
    if (!url) return
    wx.previewImage({ current: url, urls: [url] })
  },

  callPhone(e) {
    const phone = e.currentTarget.dataset.phone
    if (!phone) return
    wx.makePhoneCall({ phoneNumber: String(phone) })
  },

  goOrders() {
    wx.switchTab({ url: '/pages/orders/orders' })
  },

  goBooks() {
    wx.switchTab({ url: '/pages/books/books' })
  }
})
