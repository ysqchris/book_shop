const cart = require('./utils/cart')

App({
  globalData: {
    cartCount: 0
  },

  onLaunch() {
    this.refreshCartCount()
  },

  refreshCartCount() {
    const count = cart.getCartCount()
    this.globalData.cartCount = count
    if (typeof this.cartCountListeners === 'function') {
      this.cartCountListeners(count)
    }
    return count
  }
})
