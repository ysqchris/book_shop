import request from '@/utils/request'

export const getAdminCategoriesApi = () => request.get('/api/admin/categories')

export const createCategoryApi = (data) => request.post('/api/admin/category', data)

export const updateCategoryApi = (data) => request.put('/api/admin/category', data)

export const deleteCategoryApi = (id) => request.delete(`/api/admin/category/${id}`)

/** @param {number[]} ids 按展示顺序排列的分类 ID */
export const sortCategoriesApi = (ids) => request.put('/api/admin/categories/sort', ids)
