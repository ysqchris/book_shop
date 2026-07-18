<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <div class="filters">
          <el-input v-model="query.keyword" clearable placeholder="书名/作者/ISBN" style="width: 220px" @keyup.enter="loadData" />
          <el-select v-model="query.categoryId" clearable placeholder="分类" style="width: 160px">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-select v-model="query.status" clearable placeholder="状态" style="width: 120px">
            <el-option label="上架" :value="0" />
            <el-option label="下架" :value="1" />
          </el-select>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </div>
        <el-button type="primary" @click="openDialog()">新增图书</el-button>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              fit="cover"
              class="table-cover"
            />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑图书' : '新增图书'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="书名" required>
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="作者" required>
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.categoryId" style="width: 100%">
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
        <el-form-item label="售价" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="封面">
          <div class="cover-field">
            <el-upload
              class="cover-uploader"
              :show-file-list="false"
              accept="image/jpeg,image/png,image/gif,image/webp,image/bmp"
              :http-request="handleUpload"
              :disabled="uploading"
            >
              <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" alt="封面预览" />
              <div v-else class="cover-placeholder">
                <el-icon class="cover-uploader-icon"><Plus /></el-icon>
                <span>{{ uploading ? '上传中...' : '上传封面' }}</span>
              </div>
            </el-upload>
            <div class="cover-actions">
              <el-button v-if="form.coverImage" link type="danger" @click="form.coverImage = ''">移除</el-button>
              <p class="tip">支持 jpg / png / gif / webp，最大 5MB</p>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">上架</el-radio>
            <el-radio :label="1">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
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

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const dialogVisible = ref(false)
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
  price: 0,
  originalPrice: 0,
  stock: 0,
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
    price: 0,
    originalPrice: 0,
    stock: 0,
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
  if (row) {
    Object.assign(form, row)
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
  if (!form.title || !form.author || !form.categoryId) {
    ElMessage.warning('请填写书名、作者和分类')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await updateBookApi(form)
      ElMessage.success('更新成功')
    } else {
      await createBookApi(form)
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
</style>
