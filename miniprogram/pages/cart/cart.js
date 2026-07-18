const cart = require('../../utils/cart')
const { resolveUrl } = require('../../utils/request')

Page({
  data: {
    items: [],
    itemCount: 0,
    selectedCount: 0,
    selectedAmount: '0.00',
    selectAll: false
  },

  onShow() {
    this.refresh()
  },

  refresh() {
    const items = cart.loadCart().map((item) => ({
      ...item,
      coverUrl: resolveUrl(item.bookCover)
    }))
    const selected = items.filter((i) => i.selected)
    const selectedAmount = selected
      .reduce((sum, i) => sum + Number(i.bookPrice) * i.quantity, 0)
      .toFixed(2)
    this.setData({
      items,
      itemCount: items.reduce((s, i) => s + i.quantity, 0),
      selectedCount: selected.length,
      selectedAmount,
      selectAll: items.length > 0 && selected.length === items.length
    })
    getApp().refreshCartCount()
  },

  toggleSelect(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.items.find((i) => i.id === id)
    if (!item) return
    cart.setSelected(id, !item.selected)
    this.refresh()
  },

  toggleSelectAll() {
    cart.setSelectAll(!this.data.selectAll)
    this.refresh()
  },

  changeQty(e) {
    const { id, delta } = e.currentTarget.dataset
    const item = this.data.items.find((i) => i.id === id)
    if (!item) return
    cart.updateQuantity(id, item.quantity + Number(delta))
    this.refresh()
  },

  removeItem(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该商品吗？',
      success: (res) => {
        if (res.confirm) {
          cart.removeItem(id)
          this.refresh()
        }
      }
    })
  },

  checkout() {
    const selected = this.data.items.filter((i) => i.selected)
    if (!selected.length) {
      wx.showToast({ title: '请选择要结算的商品', icon: 'none' })
      return
    }
    cart.setCheckoutItems(
      selected.map((item) => ({
        bookId: item.bookId,
        quantity: item.quantity
      }))
    )
    wx.navigateTo({ url: '/pages/checkout/checkout' })
  },

  goBooks() {
    wx.switchTab({ url: '/pages/books/books' })
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/book-detail/book-detail?id=${id}` })
  }
})
