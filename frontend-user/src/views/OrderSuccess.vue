<template>
  <div class="success-page page-enter" v-loading="loading">
    <el-result v-if="order" icon="success" title="下单成功" sub-title="请尽快联系店家，确认品相后完成线下付款">
      <template #extra>
        <div class="order-summary">
          <p>订单号：{{ order.orderNo }}</p>
          <p>应付金额：<strong>¥{{ Number(order.payAmount).toFixed(2) }}</strong></p>
          <p>状态：待线下确认付款</p>
        </div>

        <ShopContactCard
          v-if="shopContact"
          class="contact-block"
          variant="success"
          :contact="shopContact"
          title="店家联系方式"
        />

        <div class="actions">
          <el-button type="primary" @click="$router.push('/orders')">查看我的订单</el-button>
          <el-button @click="$router.push('/books')">继续逛逛</el-button>
        </div>
      </template>
    </el-result>

    <el-result v-else-if="!loading" icon="warning" title="订单不存在" sub-title="请返回图书列表重新下单">
      <template #extra>
        <el-button type="primary" @click="$router.push('/books')">返回图书列表</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetailApi } from '@/api/book'
import ShopContactCard from '@/components/ShopContactCard.vue'

const route = useRoute()
const loading = ref(false)
const order = ref(null)
const shopContact = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getOrderDetailApi(route.params.id)
    order.value = res.data.order
    shopContact.value = res.data.shopContact
  } catch {
    order.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.success-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 8px 0 48px;
}

.order-summary {
  text-align: left;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 16px;
  color: var(--ink-soft);
}

.order-summary p {
  margin: 6px 0;
}

.order-summary strong {
  color: var(--price);
  font-size: 18px;
}

.contact-block {
  margin-bottom: 20px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
