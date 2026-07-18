<template>
  <el-container class="admin-layout">
    <el-aside v-if="!isMobile" width="220px" class="sidebar">
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
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/books">
          <el-icon><Reading /></el-icon>
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>店铺设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-drawer
      v-model="drawerOpen"
      direction="ltr"
      size="260px"
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
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/books">
            <el-icon><Reading /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/categories">
            <el-icon><Menu /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/settings">
            <el-icon><Setting /></el-icon>
            <span>店铺设置</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-drawer>

    <el-container class="main-wrap">
      <el-header class="header">
        <div class="header-left">
          <el-button v-if="isMobile" class="menu-btn" text @click="drawerOpen = true">
            <el-icon :size="22"><Fold /></el-icon>
          </el-button>
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
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const drawerOpen = ref(false)
const isMobile = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '管理后台')

const updateViewport = () => {
  isMobile.value = window.innerWidth <= 768
  if (!isMobile.value) drawerOpen.value = false
}

onMounted(() => {
  updateViewport()
  window.addEventListener('resize', updateViewport)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateViewport)
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
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid var(--admin-border);
  padding: 0 16px;
  height: 56px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}

.menu-btn {
  padding: 8px;
  margin-left: -8px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
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
}

@media (max-width: 768px) {
  .main {
    padding: 12px;
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
.mobile-drawer .el-drawer__body {
  padding: 0;
  background: #1f2a37;
}
</style>
