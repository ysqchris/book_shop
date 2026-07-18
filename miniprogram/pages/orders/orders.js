const api = require('../../utils/api')
const cart = require('../../utils/cart')
const { resolveUrl } = require('../../utils/request')

const STATUS_MAP = {
  0: '待线下付款',
  1: '已收款',
  2: '已发货',
  3: '已完成',
  4: '已取消'
}

Page({
  data: {
    loading: false,
    shopContact: null,
    details: []
  },

  onShow() {
    this.loadPage()
  },

  async loadPage() {
    this.setData({ loading: true })
    try {
      await this.loadShop()
      await this.loadOrders()
    } finally {
      this.setData({ loading: false })
    }
  },

  async loadShop() {
    try {
      const res = await api.getShopContact()
      const shop = res.data || {}
      this.setData({
        shopContact: {
          ...shop,
          wechatQrcodeUrl: resolveUrl(shop.wechatQrcode)
        }
      })
    } catch (e) {
      this.setData({ shopContact: null })
    }
  },

  async loadOrders() {
    const summaries = cart.listGuestOrders()
    const details = []
    for (const summary of summaries) {
      try {
        const res = await api.getOrderDetail(summary.id)
        const order = res.data.order
        const items = res.data.items || []
        details.push({
          orderId: order.id,
          order,
          items,
          statusText: STATUS_MAP[order.status] || '处理中',
          payAmount: Number(order.payAmount).toFixed(2),
          createTime: String(order.createTime || '').replace('T', ' ').slice(0, 19)
        })
      } catch (e) {
        details.push({
          orderId: summary.id,
          order: summary,
          items: [],
          statusText: STATUS_MAP[summary.status] || '处理中',
          payAmount: Number(summary.payAmount || 0).toFixed(2),
          createTime: String(summary.createTime || '').replace('T', ' ').slice(0, 19)
        })
      }
    }
    this.setData({ details })
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

  goSuccess(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/order-success/order-success?id=${id}` })
  },

  goBooks() {
    wx.switchTab({ url: '/pages/books/books' })
  }
})
