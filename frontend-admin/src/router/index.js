import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guestOnly: true }
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', requiresAuth: true }
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('@/views/books/BookList.vue'),
        meta: { title: '图书管理', requiresAuth: true }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('@/views/categories/CategoryList.vue'),
        meta: { title: '分类管理', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/users/UserList.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/orders/OrderList.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: 'consultations',
        name: 'Consultations',
        component: () => import('@/views/consultations/ConsultationList.vue'),
        meta: { title: '咨询管理', requiresAuth: true }
      },
      {
        path: 'settings',
        name: 'ShopSettings',
        component: () => import('@/views/settings/ShopSettings.vue'),
        meta: { title: '店铺设置', requiresAuth: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.title) {
    document.title = `${to.meta.title} - 三定圆梦书店管理后台`
  }

  if (to.meta.requiresAuth && (!userStore.isLoggedIn || !userStore.isAdmin)) {
    next('/login')
    return
  }
  if (to.meta.guestOnly && userStore.isLoggedIn && userStore.isAdmin) {
    next('/dashboard')
    return
  }
  next()
})

export default router
