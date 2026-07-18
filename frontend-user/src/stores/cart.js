import { defineStore } from 'pinia'

const CART_KEY = 'guest_cart'
const CHECKOUT_KEY = 'checkout_items'
const ORDERS_KEY = 'guest_orders'

function loadCart() {
  try {
    return JSON.parse(localStorage.getItem(CART_KEY) || '[]')
  } catch {
    return []
  }
}

function saveCart(items) {
  localStorage.setItem(CART_KEY, JSON.stringify(items))
}

function loadGuestOrders() {
  try {
    return JSON.parse(localStorage.getItem(ORDERS_KEY) || '[]')
  } catch {
    return []
  }
}

function saveGuestOrders(orders) {
  localStorage.setItem(ORDERS_KEY, JSON.stringify(orders))
}

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: loadCart()
  }),

  getters: {
    itemCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    hasItems: (state) => state.items.length > 0,
    totalAmount: (state) =>
      state.items.reduce((sum, item) => sum + Number(item.bookPrice) * item.quantity, 0)
  },

  actions: {
    persist() {
      saveCart(this.items)
    },

    addItem(book, quantity = 1) {
      const qty = Math.max(1, Number(quantity) || 1)
      const exist = this.items.find((item) => item.bookId === book.id)
      if (exist) {
        const next = exist.quantity + qty
        exist.quantity = book.stock != null ? Math.min(next, book.stock) : next
      } else {
        this.items.push({
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
      this.persist()
    },

    updateQuantity(itemId, quantity) {
      const item = this.items.find((i) => i.id === itemId)
      if (!item) return
      item.quantity = Math.max(1, Math.min(Number(quantity) || 1, item.maxStock || 99))
      this.persist()
    },

    removeItem(itemId) {
      this.items = this.items.filter((item) => item.id !== itemId)
      this.persist()
    },

    clear() {
      this.items = []
      this.persist()
    },

    setCheckoutItems(items) {
      sessionStorage.setItem(CHECKOUT_KEY, JSON.stringify(items))
    },

    getCheckoutItems() {
      try {
        return JSON.parse(sessionStorage.getItem(CHECKOUT_KEY) || '[]')
      } catch {
        return []
      }
    },

    clearCheckoutItems() {
      sessionStorage.removeItem(CHECKOUT_KEY)
    },

    removePurchased(bookIds) {
      const idSet = new Set(bookIds)
      this.items = this.items.filter((item) => !idSet.has(item.bookId))
      this.persist()
    },

    saveGuestOrder(summary) {
      const orders = loadGuestOrders()
      orders.unshift(summary)
      saveGuestOrders(orders.slice(0, 50))
    },

    listGuestOrders() {
      return loadGuestOrders()
    }
  }
})
