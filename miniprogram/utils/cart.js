const CART_KEY = 'guest_cart'
const CHECKOUT_KEY = 'checkout_items'
const ORDERS_KEY = 'guest_orders'

function loadCart() {
  try {
    return wx.getStorageSync(CART_KEY) || []
  } catch (e) {
    return []
  }
}

function saveCart(items) {
  wx.setStorageSync(CART_KEY, items || [])
}

function getCartCount() {
  return loadCart().reduce((sum, item) => sum + (Number(item.quantity) || 0), 0)
}

function addToCart(book, quantity = 1) {
  const items = loadCart()
  const qty = Math.max(1, Number(quantity) || 1)
  const exist = items.find((item) => item.bookId === book.id)
  if (exist) {
    const next = exist.quantity + qty
    exist.quantity = book.stock != null ? Math.min(next, book.stock) : next
  } else {
    items.push({
      id: `${book.id}-${Date.now()}`,
      bookId: book.id,
      bookTitle: book.title,
      bookAuthor: book.author,
      bookPublisher: book.publisher,
      bookCover: book.coverImage,
      bookPrice: Number(book.price),
      quantity: book.stock != null ? Math.min(qty, book.stock) : qty,
      maxStock: book.stock ?? 99,
      selected: true
    })
  }
  saveCart(items)
  return items
}

function updateQuantity(itemId, quantity) {
  const items = loadCart()
  const item = items.find((i) => i.id === itemId)
  if (!item) return items
  item.quantity = Math.max(1, Math.min(Number(quantity) || 1, item.maxStock || 99))
  saveCart(items)
  return items
}

function removeItem(itemId) {
  const items = loadCart().filter((item) => item.id !== itemId)
  saveCart(items)
  return items
}

function clearCart() {
  saveCart([])
}

function setSelected(itemId, selected) {
  const items = loadCart()
  const item = items.find((i) => i.id === itemId)
  if (item) {
    item.selected = !!selected
    saveCart(items)
  }
  return items
}

function setSelectAll(selected) {
  const items = loadCart().map((item) => ({ ...item, selected: !!selected }))
  saveCart(items)
  return items
}

function setCheckoutItems(items) {
  wx.setStorageSync(CHECKOUT_KEY, items || [])
}

function getCheckoutItems() {
  try {
    return wx.getStorageSync(CHECKOUT_KEY) || []
  } catch (e) {
    return []
  }
}

function clearCheckoutItems() {
  wx.removeStorageSync(CHECKOUT_KEY)
}

function removePurchased(bookIds) {
  const idSet = new Set(bookIds)
  const items = loadCart().filter((item) => !idSet.has(item.bookId))
  saveCart(items)
  return items
}

function saveGuestOrder(summary) {
  let orders = []
  try {
    orders = wx.getStorageSync(ORDERS_KEY) || []
  } catch (e) {
    orders = []
  }
  orders.unshift(summary)
  wx.setStorageSync(ORDERS_KEY, orders.slice(0, 50))
}

function listGuestOrders() {
  try {
    return wx.getStorageSync(ORDERS_KEY) || []
  } catch (e) {
    return []
  }
}

module.exports = {
  loadCart,
  saveCart,
  getCartCount,
  addToCart,
  updateQuantity,
  removeItem,
  clearCart,
  setSelected,
  setSelectAll,
  setCheckoutItems,
  getCheckoutItems,
  clearCheckoutItems,
  removePurchased,
  saveGuestOrder,
  listGuestOrders
}
