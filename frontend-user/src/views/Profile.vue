<template>
  <div class="profile-page">
    <h2>个人中心</h2>
    <el-card v-if="userStore.userInfo">
      <p><strong>用户名：</strong>{{ userStore.userInfo.username }}</p>
      <p><strong>邮箱：</strong>{{ userStore.userInfo.email || '未设置' }}</p>
      <p><strong>角色：</strong>{{ userStore.userInfo.role === 1 ? '管理员' : '普通用户' }}</p>
    </el-card>
    <el-empty v-else description="请先登录" />
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.profile-page {
  padding: 20px 0;
}

.profile-page p {
  margin: 12px 0;
}
</style>
