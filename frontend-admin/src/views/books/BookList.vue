<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <div class="filters">
          <el-input
            v-model="query.keyword"
            clearable
            placeholder="书名/作者/ISBN"
            class="filter-keyword"
            @keyup.enter="loadData"
          />
          <el-select v-model="query.categoryId" clearable placeholder="分类" class="filter-select">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-select v-model="query.status" clearable placeholder="状态" class="filter-status">
            <el-option label="上架" :value="0" />
            <el-option label="下架" :value="1" />
          </el-select>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </div>
        <el-button v-if="!isMobile" type="primary" class="desktop-add" @click="openDialog()">新增图书</el-button>
      </div>

      <!-- 桌面表格：窄屏不渲染，避免 fixed 列残留 -->
      <el-table v-if="!isMobile" :data="list" v-loading="loading" stripe class="desktop-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" class="table-cover" />
            <span v-else class="cover-empty">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="160" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="price" label="售价" width="90" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link @click="toggleStatus(row)">{{ row.status === 0 ? '下架' : '上架' }}</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 手机卡片列表 -->
      <div v-else class="mobile-list" v-loading="loading">
        <div v-if="!list.length && !loading" class="mobile-empty">暂无图书</div>
        <div v-for="row in list" :key="row.id" class="book-card">
          <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" class="card-cover" />
          <div v-else class="card-cover empty">无封面</div>
          <div class="card-body">
            <div class="card-title">{{ row.title }}</div>
            <div class="card-meta">
              <span class="price">¥{{ Number(row.price).toFixed(2) }}</span>
              <el-tag size="small" :type="row.status === 0 ? 'success' : 'info'">
                {{ row.status === 0 ? '上架' : '下架' }}
              </el-tag>
            </div>
            <div class="card-sub" v-if="row.author">{{ row.author }}</div>
            <div class="card-actions">
              <el-button size="small" type="primary" plain @click="openDialog(row)">编辑</el-button>
              <el-button size="small" plain @click="toggleStatus(row)">
                {{ row.status === 0 ? '下架' : '上架' }}
              </el-button>
              <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="pager">
        <el-pagination
          background
          :layout="isMobile ? 'total, prev, next' : 'total, prev, pager, next'"
          :total="total"
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-button v-if="isMobile" type="primary" class="mobile-fab" circle @click="openDialog()">
      <el-icon :size="22"><Plus /></el-icon>
    </el-button>

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑图书' : '新增图书'"
      :width="isMobile ? '100%' : '560px'"
      :fullscreen="isMobile"
      class="book-dialog"
      destroy-on-close
    >
      <el-form :model="form" :label-position="isMobile ? 'top' : 'right'" label-width="80px">
        <el-form-item label="书名" required>
          <el-input v-model="form.title" maxlength="200" placeholder="必填" size="large" />
        </el-form-item>
        <el-form-item label="售价" required>
          <el-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            size="large"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="封面">
          <div class="cover-field">
            <el-upload
              class="cover-uploader"
              :show-file-list="false"
              accept="image/*"
              :http-request="handleUpload"
              :disabled="uploading"
            >
              <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" alt="封面预览" />
              <div v-else class="cover-placeholder">
                <el-icon class="cover-uploader-icon"><Plus /></el-icon>
                <span>{{ uploading ? '上传中...' : '拍照 / 选图' }}</span>
              </div>
            </el-upload>
            <div class="cover-actions">
              <el-button v-if="form.coverImage" link type="danger" @click="form.coverImage = ''">移除</el-button>
              <p class="tip">手机可直接拍照上传，最大 5MB</p>
            </div>
          </div>
        </el-form-item>

        <button type="button" class="more-toggle" @click="showMore = !showMore">
          <span>{{ showMore ? '收起更多信息' : '展开更多信息' }}</span>
          <el-icon :class="{ open: showMore }"><ArrowDown /></el-icon>
        </button>

        <div v-show="showMore" class="more-fields">
          <el-form-item label="作者">
            <el-input v-model="form.author" placeholder="选填" />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.categoryId" clearable placeholder="选填" style="width: 100%">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="ISBN">
            <el-input v-model="form.isbn" />
          </el-form-item>
          <el-form-item label="出版社">
            <el-input v-model="form.publisher" />
          </el-form-item>
          <el-form-item label="出版日期">
            <el-input v-model="form.publishDate" placeholder="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="原价">
            <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="库存">
            <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio :label="0">上架</el-radio>
              <el-radio :label="1">下架</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="简介">
            <el-input v-model="form.description" type="textarea" :rows="3" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" size="large" class="save-btn" @click="handleSave">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createBookApi, deleteBookApi, getAdminBooksApi, updateBookApi } from '@/api/book'
import { getAdminCategoriesApi } from '@/api/category'
import { uploadImageApi } from '@/api/upload'
import { useMobile } from '@/composables/useMobile'

const { isMobile } = useMobile()
const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const dialogVisible = ref(false)
const showMore = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: undefined,
  status: undefined
})

const form = reactive({
  id: null,
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  publishDate: '',
  categoryId: undefined,
  price: undefined,
  originalPrice: undefined,
  stock: 1,
  coverImage: '',
  description: '',
  status: 0,
  sellerId: 1
})

const resetForm = () => {
  Object.assign(form, {
    id: null,
    title: '',
    author: '',
    isbn: '',
    publisher: '',
    publishDate: '',
    categoryId: undefined,
    price: undefined,
    originalPrice: undefined,
    stock: 1,
    coverImage: '',
    description: '',
    status: 0,
    sellerId: 1
  })
}

const loadCategories = async () => {
  const res = await getAdminCategoriesApi()
  categories.value = res.data || []
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminBooksApi({ ...query })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  resetForm()
  showMore.value = false
  if (row) {
    Object.assign(form, {
      ...row,
      price: row.price != null ? Number(row.price) : undefined,
      originalPrice: row.originalPrice != null ? Number(row.originalPrice) : undefined
    })
    const hasExtra =
      !!row.author ||
      !!row.categoryId ||
      !!row.isbn ||
      !!row.publisher ||
      !!row.publishDate ||
      !!row.description ||
      (row.stock != null && row.stock !== 1) ||
      row.status === 1
    showMore.value = hasExtra
  }
  dialogVisible.value = true
}

const handleUpload = async ({ file }) => {
  if (!file.type?.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }
  uploading.value = true
  try {
    const res = await uploadImageApi(file)
    form.coverImage = res.data
    ElMessage.success('封面上传成功')
  } finally {
    uploading.value = false
  }
}

const handleSave = async () => {
  if (!form.title?.trim()) {
    ElMessage.warning('请填写书名')
    return
  }
  if (form.price == null || form.price === '' || Number(form.price) < 0) {
    ElMessage.warning('请填写售价')
    return
  }
  saving.value = true
  try {
    const payload = {
      ...form,
      title: form.title.trim(),
      author: form.author?.trim() || '',
      stock: form.stock == null ? 1 : form.stock,
      categoryId: form.categoryId || null
    }
    if (form.id) {
      await updateBookApi(payload)
      ElMessage.success('更新成功')
    } else {
      await createBookApi(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (row) => {
  await updateBookApi({ id: row.id, status: row.status === 0 ? 1 : 0 })
  ElMessage.success('状态已更新')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除《${row.title}》吗？`, '提示', { type: 'warning' })
  await deleteBookApi(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  await loadCategories()
  await loadData()
})
</script>

<style scoped>
.panel {
  border-radius: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  flex: 1;
  min-width: 0;
}

.filter-keyword {
  width: 220px;
}

.filter-select {
  width: 160px;
}

.filter-status {
  width: 120px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.table-cover {
  width: 40px;
  height: 52px;
  border-radius: 4px;
}

.cover-empty {
  color: #909399;
  font-size: 12px;
}

.cover-field {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.cover-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.2s;
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.cover-preview,
.cover-placeholder {
  width: 120px;
  height: 160px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  object-fit: cover;
  background: #f5f7fa;
  color: #909399;
  font-size: 13px;
}

.cover-uploader-icon {
  font-size: 24px;
}

.cover-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 4px;
}

.tip {
  margin: 0;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
}

.more-toggle {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin: 4px 0 12px;
  padding: 10px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  background: #fafafa;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
}

.more-toggle .el-icon {
  transition: transform 0.2s;
}

.more-toggle .el-icon.open {
  transform: rotate(180deg);
}

.more-fields {
  padding-top: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.save-btn {
  min-width: 120px;
}

.mobile-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.mobile-fab {
  position: fixed;
  right: 18px;
  bottom: calc(20px + env(safe-area-inset-bottom));
  width: 56px;
  height: 56px;
  z-index: 20;
  box-shadow: 0 8px 20px rgba(47, 128, 237, 0.35);
}

.mobile-empty {
  text-align: center;
  color: #909399;
  padding: 32px 0;
}

.book-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--admin-border);
  border-radius: 10px;
  background: #fff;
}

.card-cover {
  width: 72px;
  height: 96px;
  border-radius: 6px;
  flex-shrink: 0;
  background: #f3f4f6;
}

.card-cover.empty {
  display: grid;
  place-items: center;
  color: #9ca3af;
  font-size: 12px;
}

.card-body {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.35;
  margin-bottom: 6px;
  word-break: break-word;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.price {
  color: #e11d48;
  font-weight: 700;
}

.card-sub {
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 8px;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

@media (max-width: 992px), ((hover: none) and (pointer: coarse) and (max-width: 1280px)) {
  .filter-keyword,
  .filter-select,
  .filter-status {
    width: 100%;
  }

  .filters {
    width: 100%;
  }

  .filters .el-button {
    width: 100%;
  }

  .cover-field {
    flex-direction: column;
  }

  .cover-preview,
  .cover-placeholder {
    width: 140px;
    height: 180px;
  }

  .dialog-footer {
    width: 100%;
  }

  .dialog-footer .el-button {
    flex: 1;
  }

  .pager {
    justify-content: center;
  }

  .page {
    padding-bottom: 80px;
  }
}
</style>
