import request from '@/utils/request'

export const createConsultationApi = (data) => {
  return request.post('/api/consultation/create', data)
}
