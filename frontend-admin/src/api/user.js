import request from '@/utils/request'

export const loginApi = (username, password) =>
  request.post('/api/user/login', { username, password })

export const logoutApi = () => request.post('/api/user/logout')

export const getUsersApi = (params) => request.get('/api/admin/users', { params })

export const updateUserApi = (userData) => request.put('/api/user/update', userData)

export const deleteUserApi = (id) => request.delete(`/api/user/${id}`)
