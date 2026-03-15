import { defineStore } from 'pinia'
import { getBooksApi, getBookDetailApi, addToCartApi, getCartListApi, updateCartItemApi, removeCartItemApi, clearCartApi, createOrderApi, getOrdersApi, getOrderDetailApi } from '@/api/book'

export const useBookStore = defineStore('book', {
  state: () => ({
    // 图书列表
    books: [],
    totalBooks: 0,
    currentPage: 1,
    pageSize: 12,
    
    // 当前图书详情
    currentBook: null,
    
    // 购物车
    cartItems: [],
    cartTotal: 0,
    cartItemCount: 0,
    
    // 订单
    orders: [],
    totalOrders: 0,
    currentOrder: null,
    
    // 搜索条件
    searchKeyword: '',
    selectedCategory: '',
    sortBy: 'newest'
  }),

  getters: {
    // 购物车总金额
    cartTotalAmount: (state) => {
      return state.cartItems.reduce((total, item) => {
        return total + (item.bookPrice * item.quantity)
      }, 0)
    },
    
    // 购物车商品数量
    cartItemCount: (state) => {
      return state.cartItems.reduce((count, item) => {
        return count + item.quantity
      }, 0)
    },
    
    // 是否有购物车商品
    hasCartItems: (state) => {
      return state.cartItems.length > 0
    },
    
    // 图书列表分页信息
    paginationInfo: (state) => {
      return {
        currentPage: state.currentPage,
        pageSize: state.pageSize,
        total: state.totalBooks,
        totalPages: Math.ceil(state.totalBooks / state.pageSize)
      }
    }
  },

  actions: {
    // 获取图书列表
    async fetchBooks(params = {}) {
      try {
        const response = await getBooksApi({
          page: this.currentPage,
          size: this.pageSize,
          categoryId: this.selectedCategory,
          sort: this.sortBy,
          keyword: this.searchKeyword,
          ...params
        })
        
        if (response.code === 200) {
          this.books = response.data.books
          this.totalBooks = response.data.total
        }
        return response
      } catch (error) {
        console.error('获取图书列表失败:', error)
        throw error
      }
    },

    // 获取图书详情
    async fetchBookDetail(bookId) {
      try {
        const response = await getBookDetailApi(bookId)
        if (response.code === 200) {
          this.currentBook = response.data
        }
        return response
      } catch (error) {
        console.error('获取图书详情失败:', error)
        throw error
      }
    },

    // 添加到购物车
    async addToCart(bookId, quantity = 1) {
      try {
        const response = await addToCartApi(bookId, quantity)
        if (response.code === 200) {
          // 添加成功后重新获取购物车
          await this.fetchCartList()
        }
        return response
      } catch (error) {
        console.error('添加到购物车失败:', error)
        throw error
      }
    },

    // 获取购物车列表
    async fetchCartList() {
      try {
        const response = await getCartListApi()
        if (response.code === 200) {
          this.cartItems = response.data.items
          this.cartTotal = response.data.total
          this.cartItemCount = response.data.itemCount
        }
        return response
      } catch (error) {
        console.error('获取购物车列表失败:', error)
        throw error
      }
    },

    // 更新购物车商品数量
    async updateCartItem(cartItemId, quantity) {
      try {
        const response = await updateCartItemApi(cartItemId, quantity)
        if (response.code === 200) {
          await this.fetchCartList()
        }
        return response
      } catch (error) {
        console.error('更新购物车商品失败:', error)
        throw error
      }
    },

    // 删除购物车商品
    async removeCartItem(cartItemId) {
      try {
        const response = await removeCartItemApi(cartItemId)
        if (response.code === 200) {
          await this.fetchCartList()
        }
        return response
      } catch (error) {
        console.error('删除购物车商品失败:', error)
        throw error
      }
    },

    // 清空购物车
    async clearCart() {
      try {
        const response = await clearCartApi()
        if (response.code === 200) {
          this.cartItems = []
          this.cartTotal = 0
          this.cartItemCount = 0
        }
        return response
      } catch (error) {
        console.error('清空购物车失败:', error)
        throw error
      }
    },

    // 创建订单
    async createOrder(orderData) {
      try {
        const response = await createOrderApi(orderData)
        if (response.code === 200) {
          // 创建成功后清空购物车
          await this.clearCart()
        }
        return response
      } catch (error) {
        console.error('创建订单失败:', error)
        throw error
      }
    },

    // 获取订单列表
    async fetchOrders(params = {}) {
      try {
        const response = await getOrdersApi(params)
        if (response.code === 200) {
          this.orders = response.data.orders
          this.totalOrders = response.data.total
        }
        return response
      } catch (error) {
        console.error('获取订单列表失败:', error)
        throw error
      }
    },

    // 获取订单详情
    async fetchOrderDetail(orderId) {
      try {
        const response = await getOrderDetailApi(orderId)
        if (response.code === 200) {
          this.currentOrder = response.data
        }
        return response
      } catch (error) {
        console.error('获取订单详情失败:', error)
        throw error
      }
    },

    // 更新搜索条件
    updateSearchConditions(keyword, category, sort) {
      this.searchKeyword = keyword || ''
      this.selectedCategory = category || ''
      this.sortBy = sort || 'newest'
      this.currentPage = 1
    },

    // 重置搜索条件
    resetSearchConditions() {
      this.searchKeyword = ''
      this.selectedCategory = ''
      this.sortBy = 'newest'
      this.currentPage = 1
    }
  }
})