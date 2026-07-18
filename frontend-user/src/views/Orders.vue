<template>
  <div class="orders-page page-enter" v-loading="loading">
    <h1>我的订单</h1>
    <p class="hint">下单后请联系店家完成线下付款。本机可查看近期订单，方便家长与同学核对。</p>

    <ShopContactCard
      v-if="shopContact"
      class="contact-block"
      variant="banner"
      :contact="shopContact"
      title="店家联系方式"
    />

    <el-empty v-if="!details.length && !loading" description="暂无订单，去选几本喜欢的书吧">
      <el-button type="primary" @click="$router.push('/books')">去逛逛</el-button>
    </el-empty>

    <div v-else class="order-list">
      <div class="order-card" v-for="row in details" :key="row.order.id">
        <div class="row">
          <span class="no">订单号 {{ row.order.orderNo }}</span>
          <span class="status">{{ statusText(row.order.status) }}</span>
        </div>
        <div class="row">
          <span>应付 ¥{{ Number(row.order.payAmount).toFixed(2) }}</span>
          <span class="time">{{ formatTime(row.order.createTime) }}</span>
        </div>

        <div class="items" v-if="row.items?.length">
          <div class="item" v-for="item in row.items" :key="item.id">
            <span class="item-title">{{ item.bookTitle }}</span>
            <span>¥{{ Number(item.bookPrice).toFixed(2) }} × {{ item.quantity }}</span>
            <span class="item-sub">¥{{ Number(item.subtotal).toFixed(2) }}</span>
          </div>
        </div>

        <div class="receiver" v-if="row.order.receiverName">
          联系人 {{ row.order.receiverName }}
          <template v-if="row.order.receiverPhone"> · {{ row.order.receiverPhone }}</template>
          <template v-if="row.order.receiverAddress"> · {{ row.order.receiverAddress }}</template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getOrderDetailApi } from '@/api/book'
import { getShopContactApi } from '@/api/shop'
import { useCartStore } from '@/stores/cart'
import ShopContactCard from '@/components/ShopContactCard.vue'

const cartStore = useCartStore()
const loading = ref(false)
const details = ref([])
const shopContact = ref(null)

const statusText = (status) => {
  const map = {
    0: '待线下付款',
    1: '已收款',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '处理中'
}

const formatTime = (value) => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 19)
}

onMounted(async () => {
  loading.value = true
  try {
    const [contactRes] = await Promise.all([
      getShopContactApi().catch(() => null)
    ])
    shopContact.value = contactRes?.data || null

    const summaries = cartStore.listGuestOrders()
    const rows = []
    for (const summary of summaries) {
      try {
        const res = await getOrderDetailApi(summary.id)
        rows.push(res.data)
      } catch {
        rows.push({
          order: summary,
          items: []
        })
      }
    }
    details.value = rows
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.orders-page {
  max-width: 880px;
  margin: 0 auto;
  padding-bottom: 40px;
}

h1 {
  margin: 0 0 8px;
  font-size: 26px;
  color: var(--ink);
}

.hint {
  color: var(--muted);
  margin: 0 0 16px;
  font-size: 14px;
  line-height: 1.6;
}

.contact-block {
  margin-bottom: 16px;
}

.order-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 12px;
}

.row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  color: #334155;
}

.no {
  font-weight: 600;
}

.status {
  color: var(--accent-deep);
}

.time {
  color: #94a3b8;
  font-size: 13px;
}

.items {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}

.item {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 6px 0;
  font-size: 14px;
  color: #475569;
}

.item-title {
  flex: 1;
  min-width: 0;
  color: #1f2937;
}

.item-sub {
  color: var(--price);
  font-weight: 600;
  min-width: 72px;
  text-align: right;
}

.receiver {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #e2e8f0;
  font-size: 13px;
  color: #64748b;
}

/* ===== 移动端优化（参考小程序订单页布局） ===== */
@media (max-width: 768px) {
  h1 {
    font-size: 20px;
  }

  .order-card {
    padding: 12px 14px;
    border-radius: 10px;
  }

  /* 订单号 + 状态行：订单号可截断，状态保持显示 */
  .row {
    gap: 8px;
  }

  .no {
    font-size: 13px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    min-width: 0;
  }

  .status {
    font-size: 12px;
    flex-shrink: 0;
    padding: 2px 8px;
    background: var(--accent-soft);
    border-radius: 10px;
    line-height: 1.6;
  }

  .time {
    font-size: 12px;
  }

  /* 商品行：移动端改为书名独占一行，价格×数量和小计在第二行 */
  .item {
    flex-wrap: wrap;
    gap: 4px 8px;
    padding: 8px 0;
  }

  .item-title {
    width: 100%;
    font-size: 13px;
  }

  .item-sub {
    min-width: auto;
    margin-left: auto;
    font-size: 13px;
  }

  /* 收件人信息：每项单独一行 */
  .receiver {
    display: flex;
    flex-direction: column;
    gap: 3px;
    font-size: 12px;
    line-height: 1.6;
  }
}
</style>