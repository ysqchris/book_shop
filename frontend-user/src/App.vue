<template>
  <div id="app">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h1>📚 二手书平台</h1>
        </div>
        <div class="nav-menu">
          <el-menu
            :default-active="$route.path"
            mode="horizontal"
            router
            class="nav-menu-list"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/books">图书浏览</el-menu-item>
            <el-menu-item index="/cart">购物车</el-menu-item>
            <el-menu-item index="/orders">我的订单</el-menu-item>
          </el-menu>
        </div>
        <div class="user-info">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown>
              <span class="user-name">
                {{ userStore.userInfo?.username }}
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <router-view />
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 二手书买卖平台. All rights reserved.</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/')
  } catch {
    // 用户取消操作
  }
}
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 60px;
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
}

.logo h1 {
  margin: 0;
  color: #409eff;
  font-size: 24px;
}

.nav-menu-list {
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  cursor: pointer;
  color: #606266;
}

.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.footer {
  background-color: #f5f7fa;
  text-align: center;
  padding: 20px;
}

.footer-content p {
  margin: 0;
  color: #909399;
}
</style>