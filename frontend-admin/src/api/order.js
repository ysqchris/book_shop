import request from '@/utils/request'

export const getAdminOrdersApi = (params) => request.get('/api/admin/orders', { params })

export const getOrderDetailApi = (id) => request.get(`/api/admin/order/${id}`)

export const shipOrderApi = (id) => request.put(`/api/admin/order/${id}/ship`)

export const completeOrderApi = (id) => request.put(`/api/admin/order/${id}/complete`)

export const cancelOrderApi = (id) => request.put(`/api/admin/order/${id}/cancel`)
