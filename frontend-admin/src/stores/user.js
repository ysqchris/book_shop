import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { loginApi, logoutApi } from '@/api/user'

const TOKEN_KEY = 'admin_token'
const USER_KEY = 'admin_user'

export const useUserStore = defineStore('admin-user', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref(JSON.parse(localStorage.getItem(USER_KEY) || 'null'))
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  const login = async (username, password) => {
    const response = await loginApi(username, password)
    const user = response.user || response.data
    if (!user || user.role !== 1) {
      throw new Error('该账号没有管理后台权限')
    }
    token.value = response.token || ''
    userInfo.value = user
    localStorage.setItem(TOKEN_KEY, token.value)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
    return user
  }

  const logout = async () => {
    try {
      await logoutApi()
    } catch {
      // ignore
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    logout
  }
})
