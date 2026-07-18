const { get, post } = require('./request')

function getBooks(params) {
  return get('/api/books', params)
}

function getBookDetail(id) {
  return get(`/api/book/${id}`)
}

function getCategories() {
  return get('/api/categories')
}

function getShopContact() {
  return get('/api/shop/contact')
}

function createOrder(payload) {
  return post('/api/order/create', payload)
}

function getOrderDetail(id) {
  return get(`/api/order/${id}`)
}

module.exports = {
  getBooks,
  getBookDetail,
  getCategories,
  getShopContact,
  createOrder,
  getOrderDetail
}
