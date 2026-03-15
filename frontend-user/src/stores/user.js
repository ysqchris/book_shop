import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, getUserInfoApi, logoutApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref(null)
  
  // token
  const token = ref(localStorage.getItem('token') || '')
  
  // 是否已登录
  const isLoggedIn = ref(!!token.value)

  // 登录
  const login = async (username, password) => {
    try {
      const response = await loginApi(username, password)
      
      if (response.code === 200) {
        token.value = response.token
        userInfo.value = response.user
        isLoggedIn.value = true
        
        // 保存token到本地存储
        localStorage.setItem('token', response.token)
        
        return { success: true, message: '登录成功' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: '登录失败，请检查网络连接' }
    }
  }

  // 退出登录
  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      // 忽略退出错误
    } finally {
      // 清除本地状态
      token.value = ''
      userInfo.value = null
      isLoggedIn.value = false
      
      // 清除本地存储
      localStorage.removeItem('token')
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    if (!token.value) {
      return null
    }
    
    try {
      const response = await getUserInfoApi()
      if (response.code === 200) {
        userInfo.value = response.user
        return userInfo.value
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
    
    return null
  }

  // 检查登录状态
  const checkLoginStatus = async () => {
    if (token.value) {
      await getUserInfo()
    }
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    login,
    logout,
    getUserInfo,
    checkLoginStatus
  }
})