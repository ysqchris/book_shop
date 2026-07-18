const api = require('../../utils/api')
const cart = require('../../utils/cart')
const { resolveUrl } = require('../../utils/request')

Page({
  data: {
    loading: false,
    submitting: false,
    items: [],
    totalAmount: '0.00',
    shopContact: null,
    region: ['湖南省', '株洲市', '攸县'],
    regionText: '湖南省 株洲市 攸县',
    form: {
      receiverName: '',
      receiverPhone: '',
      addressDetail: '',
      remark: ''
    }
  },

  onLoad() {
    this.loadPage()
  },

  async loadPage() {
    this.setData({ loading: true })
    try {
      await Promise.all([this.loadShop(), this.loadItems()])
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

  async loadItems() {
    const checkout = cart.getCheckoutItems()
    if (!checkout.length) {
      this.setData({ items: [], totalAmount: '0.00' })
      return
    }
    const items = []
    for (const row of checkout) {
      try {
        const res = await api.getBookDetail(row.bookId)
        const book = res.data
        if (!book) continue
        items.push({
          bookId: book.id,
          bookTitle: book.title,
          bookAuthor: book.author,
          bookCover: book.coverImage,
          coverUrl: resolveUrl(book.coverImage),
          bookPrice: Number(book.price).toFixed(2),
          quantity: Math.min(Number(row.quantity) || 1, book.stock || 1)
        })
      } catch (e) {
        // skip invalid book
      }
    }
    const total = items.reduce((sum, i) => sum + Number(i.bookPrice) * i.quantity, 0)
    this.setData({
      items,
      totalAmount: total.toFixed(2)
    })
  },

  previewQrcode(e) {
    const url = e.currentTarget.dataset.url
    if (!url) return
    wx.previewImage({ current: url, urls: [url] })
  },

  onInput(e) {
    const key = e.currentTarget.dataset.key
    this.setData({
      [`form.${key}`]: e.detail.value
    })
  },

  onRegionChange(e) {
    const region = e.detail.value || []
    this.setData({
      region,
      regionText: region.join(' ')
    })
  },

  buildAddress() {
    const regionPart = (this.data.region || []).join('')
    const detail = (this.data.form.addressDetail || '').trim()
    if (regionPart && detail) return `${regionPart}${detail}`
    return regionPart || detail
  },

  async submitOrder() {
    const { form, items, submitting, region } = this.data
    if (submitting) return
    if (!items.length) {
      wx.showToast({ title: '没有可下单的商品', icon: 'none' })
      return
    }
    if (!form.receiverName || !form.receiverName.trim()) {
      wx.showToast({ title: '请填写联系人', icon: 'none' })
      return
    }
    if (!/^1\d{10}$/.test((form.receiverPhone || '').trim())) {
      wx.showToast({ title: '请填写有效手机号', icon: 'none' })
      return
    }
    if (!region || region.length < 3) {
      wx.showToast({ title: '请选择所在地区', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      const res = await api.createOrder({
        userId: null,
        receiverName: form.receiverName.trim(),
        receiverPhone: form.receiverPhone.trim(),
        receiverAddress: this.buildAddress(),
        remark: (form.remark || '').trim(),
        items: items.map((item) => ({
          bookId: item.bookId,
          quantity: item.quantity
        }))
      })
      const order = res.data.order
      cart.removePurchased(items.map((i) => i.bookId))
      cart.clearCheckoutItems()
      cart.saveGuestOrder({
        id: order.id,
        orderNo: order.orderNo,
        payAmount: order.payAmount,
        status: order.status,
        createTime: order.createTime
      })
      getApp().refreshCartCount()
      wx.redirectTo({
        url: `/pages/order-success/order-success?id=${order.id}`
      })
    } catch (e) {
      // toast shown
    } finally {
      this.setData({ submitting: false })
    }
  },

  goBack() {
    wx.navigateBack({ fail: () => wx.switchTab({ url: '/pages/cart/cart' }) })
  },

  goBooks() {
    wx.switchTab({ url: '/pages/books/books' })
  }
})
