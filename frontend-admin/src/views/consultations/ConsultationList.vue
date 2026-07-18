<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <div class="filters">
          <el-input
            v-model="query.keyword"
            clearable
            placeholder="姓名/电话微信/书名/内容"
            style="width: 240px"
            @keyup.enter="loadData"
          />
          <el-select v-model="query.status" clearable placeholder="状态" style="width: 140px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </div>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话/微信" min-width="140" />
        <el-table-column prop="bookTitle" label="想找的书" min-width="160">
          <template #default="{ row }">
            <span v-if="row.bookTitle">{{ row.bookTitle }}</span>
            <span v-else class="muted">未填写</span>
          </template>
        </el-table-column>
        <el-table-column label="留言内容" min-width="220">
          <template #default="{ row }">
            <span v-if="row.content" class="content-text">{{ row.content }}</span>
            <span v-else class="muted">无</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复" min-width="180">
          <template #default="{ row }">
            <span v-if="row.reply" class="content-text">{{ row.reply }}</span>
            <span v-else class="muted">未回复</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" min-width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openReply(row)">回复</el-button>
            <el-button
              link
              :disabled="row.status === 1"
              @click="handleStatus(row, 1)"
            >标记已联系</el-button>
            <el-button
              link
              :disabled="row.status === 2"
              @click="handleStatus(row, 2)"
            >关闭</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="replyVisible" title="回复咨询" width="480px">
      <template v-if="current">
        <div class="reply-meta">
          <p><strong>姓名：</strong>{{ current.name }}</p>
          <p><strong>联系方式：</strong>{{ current.phone }}</p>
          <p v-if="current.bookTitle"><strong>想找的书：</strong>{{ current.bookTitle }}</p>
          <p v-if="current.content"><strong>留言：</strong>{{ current.content }}</p>
        </div>
        <el-divider />
        <el-input
          v-model="replyText"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          placeholder="输入给客户的回复，例如：这本书有货，已加您微信沟通"
        />
      </template>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="submitReply">保存回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminConsultationsApi,
  updateConsultationApi,
  deleteConsultationApi
} from '@/api/consultation'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined
})

const statusOptions = [
  { label: '待处理', value: 0 },
  { label: '已联系', value: 1 },
  { label: '已关闭', value: 2 }
]

const statusText = (status) =>
  statusOptions.find((item) => item.value === status)?.label || '未知'

const statusTagType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  return 'info'
}

const replyVisible = ref(false)
const replying = ref(false)
const current = ref(null)
const replyText = ref('')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminConsultationsApi({ ...query })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const openReply = (row) => {
  current.value = row
  replyText.value = row.reply || ''
  replyVisible.value = true
}

const submitReply = async () => {
  if (!current.value) return
  replying.value = true
  try {
    await updateConsultationApi({
      id: current.value.id,
      reply: replyText.value,
      status: current.value.status === 0 ? 1 : current.value.status
    })
    ElMessage.success('回复已保存')
    replyVisible.value = false
    loadData()
  } finally {
    replying.value = false
  }
}

const handleStatus = async (row, status) => {
  const label = statusText(status)
  await ElMessageBox.confirm(`确认将该咨询标记为「${label}」？`, '提示')
  await updateConsultationApi({ id: row.id, status })
  ElMessage.success(`已标记为${label}`)
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该咨询记录？删除后无法恢复。', '提示', {
    type: 'warning'
  })
  await deleteConsultationApi(row.id)
  ElMessage.success('已删除')
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

.muted {
  color: var(--admin-muted);
}

.content-text {
  display: inline-block;
  max-width: 100%;
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.5;
}

.reply-meta p {
  margin: 6px 0;
  font-size: 14px;
  line-height: 1.6;
}

.reply-meta strong {
  color: var(--admin-muted);
  margin-right: 4px;
}
</style>
