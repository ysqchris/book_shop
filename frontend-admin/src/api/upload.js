import request from '@/utils/request'

export const uploadImageApi = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/upload', formData)
}
