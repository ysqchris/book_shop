<template>
  <el-container class="admin-layout">
    <el-aside v-if="!isMobile" class="sidebar desktop-sidebar" width="220px">
      <div class="brand">
        <img class="brand-logo" src="/shop-sign.png" alt="三定圆梦书店" />
        <div>
          <strong>三定圆梦书店</strong>
          <p>管理后台</p>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1f2a37"
        text-color="#d1d5db"
        active-text-color="#ffffff"
      >
        <el-menu-item v-for="item in menus" :key="item.index" :index="item.index">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-drawer
      v-model="drawerOpen"
      direction="ltr"
      size="78%"
      :with-header="false"
      class="mobile-drawer"
      append-to-body
    >
      <div class="sidebar drawer-sidebar">
        <div class="brand">
          <img class="brand-logo" src="/shop-sign.png" alt="三定圆梦书店" />
          <div>
            <strong>三定圆梦书店</strong>
            <p>管理后台</p>
          </div>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#1f2a37"
          text-color="#d1d5db"
          active-text-color="#ffffff"
          @select="drawerOpen = false"
        >
          <el-menu-item v-for="item in menus" :key="item.index" :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-drawer>

    <el-container class="main-wrap">
      <el-header class="header">
        <div class="header-left">
          <button
            type="button"
            class="menu-btn"
            :class="{ 'is-mobile-show': isMobile }"
            aria-label="打开菜单"
            @click="drawerOpen = true"
          >
            <el-icon :size="22"><Expand /></el-icon>
          </button>
          <div class="header-title">{{ currentTitle }}</div>
        </div>
        <div class="header-right">
          <span class="username">{{ userStore.userInfo?.username }}</span>
          <el-button text type="primary" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Expand,
  List,
  Menu,
  Odometer,
  Reading,
  Setting,
  User
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useMobile } from '@/composables/useMobile'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { isMobile } = useMobile()

const drawerOpen = ref(false)

const menus = [
  { index: '/dashboard', label: '仪表盘', icon: Odometer },
  { index: '/books', label: '图书管理', icon: Reading },
  { index: '/categories', label: '分类管理', icon: Menu },
  { index: '/users', label: '用户管理', icon: User },
  { index: '/orders', label: '订单管理', icon: List },
  { index: '/settings', label: '店铺设置', icon: Setting }
]

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '管理后台')

watch(isMobile, (mobile) => {
  if (!mobile) drawerOpen.value = false
})

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定退出管理后台吗？', '提示', { type: 'warning' })
    await userStore.logout()
    ElMessage.success('已退出')
    router.push('/login')
  } catch {
    // cancelled
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  width: 100%;
  max-width: 100vw;
  overflow: hidden;
}

.sidebar {
  background: var(--admin-sidebar);
  color: #fff;
  min-height: 100vh;
}

.drawer-sidebar {
  min-height: 100%;
}

.brand {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-logo {
  width: 48px;
  height: 48px;
  object-fit: cover;
  object-position: center 28%;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: #f5d84a;
  flex-shrink: 0;
}

.brand strong {
  display: block;
  font-size: 16px;
}

.brand p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #9ca3af;
}

.main-wrap {
  min-width: 0;
  flex: 1;
  width: 100%;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid var(--admin-border);
  padding: 0 12px 0 8px;
  height: 52px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}

.menu-btn {
  display: none;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: #111827;
  cursor: pointer;
  flex-shrink: 0;
}

.menu-btn:active {
  background: #f3f4f6;
}

.menu-btn.is-mobile-show {
  display: inline-flex !important;
}

.header-title {
  font-size: 17px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.username {
  color: var(--admin-muted);
  max-width: 96px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main {
  background: var(--admin-bg);
  padding: 16px;
  overflow-x: hidden;
}

/* 手机 / 窄屏：隐藏常驻侧栏，显示汉堡菜单 */
@media (max-width: 992px), ((hover: none) and (pointer: coarse) and (max-width: 1280px)) {
  .desktop-sidebar {
    display: none !important;
    width: 0 !important;
  }

  .menu-btn {
    display: inline-flex !important;
  }

  .main {
    padding: 10px;
  }

  .header-title {
    font-size: 16px;
  }

  .username {
    display: none;
  }
}
</style>

<style>
.mobile-drawer.el-drawer,
.mobile-drawer .el-drawer {
  background: #1f2a37;
}

.mobile-drawer .el-drawer__body {
  padding: 0;
  background: #1f2a37;
}
</style>
