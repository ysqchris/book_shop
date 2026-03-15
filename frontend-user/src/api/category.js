import request from '@/utils/request'

// 获取所有分类
export const getCategoriesApi = () => {
  return request.get('/api/categories')
}

// 获取分类详情
export const getCategoryDetailApi = (categoryId) => {
  return request.get(`/api/category/${categoryId}`)
}

// 获取子分类
export const getSubCategoriesApi = (parentId) => {
  return request.get(`/api/category/${parentId}/subcategories`)
}

// 获取热门分类
export const getHotCategoriesApi = (limit = 6) => {
  return request.get('/api/categories/hot', {
    params: { limit }
  })
}

// 获取分类树
export const getCategoryTreeApi = () => {
  return request.get('/api/categories/tree')
}