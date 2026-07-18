import request from '@/utils/request'

export const getAdminConsultationsApi = (params) =>
  request.get('/api/admin/consultations', { params })

export const updateConsultationApi = (data) =>
  request.put('/api/admin/consultation', data)

export const deleteConsultationApi = (id) =>
  request.delete(`/api/admin/consultation/${id}`)
