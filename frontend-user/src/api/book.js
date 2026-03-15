import request from '@/utils/request'

// 获取图书列表
export const getBooksApi = (params) => {
  return request.get('/api/books', { params })
}

// 搜索图书
export const searchBooksApi = (keyword, params = {}) => {
  return request.get('/api/books/search', {
    params: {
      keyword,
      ...params
    }
  })
}

// 获取图书详情
export const getBookDetailApi = (bookId) => {
  return request.get(`/api/book/${bookId}`)
}

// 获取热门图书
export const getHotBooksApi = (limit = 8) => {
  return request.get('/api/books/hot', {
    params: { limit }
  })
}

// 获取推荐图书
export const getRecommendBooksApi = (userId, limit = 6) => {
  return request.get('/api/books/recommend', {
    params: { userId, limit }
  })
}

// 获取分类图书
export const getBooksByCategoryApi = (categoryId, params = {}) => {
  return request.get(`/api/category/${categoryId}/books`, { params })
}

// 添加到购物车
export const addToCartApi = (bookId, quantity = 1) => {
  return request.post('/api/cart/add', {
    bookId,
    quantity
  })
}

// 获取购物车列表
export const getCartListApi = () => {
  return request.get('/api/cart/list')
}

// 更新购物车商品数量
export const updateCartItemApi = (cartItemId, quantity) => {
  return request.put('/api/cart/update', {
    cartItemId,
    quantity
  })
}

// 删除购物车商品
export const removeCartItemApi = (cartItemId) => {
  return request.delete(`/api/cart/remove/${cartItemId}`)
}

// 清空购物车
export const clearCartApi = () => {
  return request.delete('/api/cart/clear')
}

// 创建订单
export const createOrderApi = (orderData) => {
  return request.post('/api/order/create', orderData)
}

// 获取订单列表
export const getOrdersApi = (params = {}) => {
  return request.get('/api/orders', { params })
}

// 获取订单详情
export const getOrderDetailApi = (orderId) => {
  return request.get(`/api/order/${orderId}`)
}

// 取消订单
export const cancelOrderApi = (orderId) => {
  return request.put(`/api/order/${orderId}/cancel`)
}

// 确认收货
export const confirmOrderApi = (orderId) => {
  return request.put(`/api/order/${orderId}/confirm`)
}