import request from '@/utils/request'

// 用户注册
export const registerApi = (userData) => {
  return request.post('/api/user/register', userData)
}

// 用户登录
export const loginApi = (username, password) => {
  return request.post('/api/user/login', {
    username,
    password
  })
}

// 获取用户信息
export const getUserInfoApi = (userId) => {
  return request.get(`/api/user/info/${userId}`)
}

// 更新用户信息
export const updateUserApi = (userData) => {
  return request.put('/api/user/update', userData)
}

// 修改密码
export const changePasswordApi = (passwordData) => {
  return request.post('/api/user/change-password', passwordData)
}

// 退出登录
export const logoutApi = () => {
  return request.post('/api/user/logout')
}