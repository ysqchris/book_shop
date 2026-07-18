import request from '@/utils/request'

export const getShopContactApi = () => {
  return request.get('/api/shop/contact')
}
