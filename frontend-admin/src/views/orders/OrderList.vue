<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <div class="filters">
          <el-input v-model="query.keyword" clearable placeholder="订单号/收货人/手机" style="width: 220px" @keyup.enter="loadData" />
          <el-select v-model="query.status" clearable placeholder="状态" style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </div>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column prop="userId" label="用户 ID" width="90" />
        <el-table-column prop="payAmount" label="实付" width="90" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag>{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="电话" width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button link :disabled="row.status !== 0" @click="handleConfirmPay(row)">确认收款</el-button>
            <el-button link :disabled="row.status !== 1" @click="handleShip(row)">发货</el-button>
            <el-button link :disabled="row.status !== 2" @click="handleComplete(row)">完成</el-button>
            <el-button link type="danger" :disabled="row.status >= 3" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailVisible" title="订单详情" size="420px">
      <template v-if="detail">
        <p><strong>订单号：</strong>{{ detail.order.orderNo }}</p>
        <p><strong>状态：</strong>{{ statusText(detail.order.status) }}</p>
        <p><strong>实付：</strong>¥{{ detail.order.payAmount }}</p>
        <p><strong>收货人：</strong>{{ detail.order.receiverName }} {{ detail.order.receiverPhone }}</p>
        <p><strong>地址：</strong>{{ detail.order.receiverAddress }}</p>
        <p><strong>备注：</strong>{{ detail.order.remark || '无' }}</p>
        <el-divider />
        <h4>商品明细</h4>
        <div v-if="!detail.items?.length" class="empty">暂无明细</div>
        <div v-for="item in detail.items" :key="item.id" class="item">
          <div>
            <strong>{{ item.bookTitle }}</strong>
            <p>¥{{ item.bookPrice }} × {{ item.quantity }}</p>
          </div>
          <span>¥{{ item.subtotal }}</span>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelOrderApi,
  completeOrderApi,
  confirmPayApi,
  getAdminOrdersApi,
  getOrderDetailApi,
  shipOrderApi
} from '@/api/order'

const loading = ref(false)
const detailVisible = ref(false)
const list = ref([])
const total = ref(0)
const detail = ref(null)

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined
})

const statusOptions = [
  { label: '待线下付款', value: 0 },
  { label: '已收款', value: 1 },
  { label: '已发货', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

const statusText = (status) => statusOptions.find((item) => item.value === status)?.label || '未知'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminOrdersApi({ ...query })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const openDetail = async (row) => {
  const res = await getOrderDetailApi(row.id)
  detail.value = res.data
  detailVisible.value = true
}

const handleConfirmPay = async (row) => {
  await ElMessageBox.confirm('确认已线下收到该订单款项？', '提示')
  await confirmPayApi(row.id)
  ElMessage.success('已确认收款')
  loadData()
}

const handleShip = async (row) => {
  await ElMessageBox.confirm('确认将该订单标记为已发货？', '提示')
  await shipOrderApi(row.id)
  ElMessage.success('已发货')
  loadData()
}

const handleComplete = async (row) => {
  await ElMessageBox.confirm('确认完成该订单？', '提示')
  await completeOrderApi(row.id)
  ElMessage.success('订单已完成')
  loadData()
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确认取消该订单？', '提示', { type: 'warning' })
  await cancelOrderApi(row.id)
  ElMessage.success('订单已取消')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.panel {
  border-radius: 12px;
}

.toolbar {
  margin-bottom: 16px;
}

.filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid var(--admin-border);
}

.item p,
.empty {
  margin: 4px 0 0;
  color: var(--admin-muted);
}
</style>
