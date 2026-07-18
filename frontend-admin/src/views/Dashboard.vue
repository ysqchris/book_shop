<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :xs="12" :sm="8" :md="4" v-for="item in cards" :key="item.label">
        <div class="stat-card">
          <p>{{ item.label }}</p>
          <strong>{{ item.value }}</strong>
        </div>
      </el-col>
    </el-row>

    <el-card class="panel" shadow="never">
      <template #header>快捷入口</template>
      <div class="shortcuts">
        <el-button type="primary" @click="$router.push('/books')">图书管理</el-button>
        <el-button @click="$router.push('/categories')">分类管理</el-button>
        <el-button @click="$router.push('/users')">用户管理</el-button>
        <el-button @click="$router.push('/orders')">订单管理</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getStatsApi } from '@/api/dashboard'

const stats = ref({
  userCount: 0,
  bookCount: 0,
  categoryCount: 0,
  orderCount: 0,
  onSaleBookCount: 0,
  pendingOrderCount: 0
})

const cards = computed(() => [
  { label: '用户数', value: stats.value.userCount },
  { label: '图书数', value: stats.value.bookCount },
  { label: '在售图书', value: stats.value.onSaleBookCount },
  { label: '分类数', value: stats.value.categoryCount },
  { label: '订单数', value: stats.value.orderCount },
  { label: '待发货', value: stats.value.pendingOrderCount }
])

onMounted(async () => {
  const res = await getStatsApi()
  stats.value = { ...stats.value, ...res.data }
})
</script>

<style scoped>
.stat-card {
  background: #fff;
  border: 1px solid var(--admin-border);
  border-radius: 12px;
  padding: 18px;
  margin-bottom: 16px;
}

.stat-card p {
  margin: 0 0 8px;
  color: var(--admin-muted);
  font-size: 13px;
}

.stat-card strong {
  font-size: 28px;
}

.panel {
  border-radius: 12px;
}

.shortcuts {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
