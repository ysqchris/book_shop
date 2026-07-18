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

// 创建订单（线下付款，无需登录）
export const createOrderApi = (orderData) => {
  return request.post('/api/order/create', orderData)
}

// 获取订单详情
export const getOrderDetailApi = (orderId) => {
  return request.get(`/api/order/${orderId}`)
}