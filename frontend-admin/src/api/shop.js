import request from '@/utils/request'

export const getShopSettingsApi = () => request.get('/api/admin/shop/settings')

export const updateShopSettingsApi = (data) => request.put('/api/admin/shop/settings', data)
