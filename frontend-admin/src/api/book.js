import request from '@/utils/request'

export const getAdminBooksApi = (params) => request.get('/api/admin/books', { params })

export const getBookDetailApi = (id) => request.get(`/api/book/${id}`)

export const createBookApi = (data) => request.post('/api/book', data)

export const updateBookApi = (data) => request.put('/api/book', data)

export const deleteBookApi = (id) => request.delete(`/api/book/${id}`)
