const api = require('../../utils/api')
const cart = require('../../utils/cart')
const { resolveUrl } = require('../../utils/request')

Page({
  data: {
    book: null,
    coverUrl: '',
    quantity: 1,
    loading: true
  },

  onLoad(query) {
    this.bookId = query.id
    this.loadDetail()
  },

  async loadDetail() {
    this.setData({ loading: true })
    try {
      const res = await api.getBookDetail(this.bookId)
      const book = res.data
      this.setData({
        book,
        coverUrl: resolveUrl(book && book.coverImage),
        quantity: 1
      })
    } catch (e) {
      this.setData({ book: null })
    } finally {
      this.setData({ loading: false })
    }
  },

  decQty() {
    if (this.data.quantity <= 1) return
    this.setData({ quantity: this.data.quantity - 1 })
  },

  incQty() {
    const stock = (this.data.book && this.data.book.stock) || 1
    if (this.data.quantity >= stock) return
    this.setData({ quantity: this.data.quantity + 1 })
  },

  addCart() {
    const { book, quantity } = this.data
    if (!book || book.stock === 0) return
    cart.addToCart(book, quantity)
    getApp().refreshCartCount()
    wx.showToast({ title: '已加入购物车', icon: 'success' })
  },

  buyNow() {
    const { book, quantity } = this.data
    if (!book || book.stock === 0) return
    cart.setCheckoutItems([{ bookId: book.id, quantity }])
    wx.navigateTo({ url: '/pages/checkout/checkout' })
  }
})
