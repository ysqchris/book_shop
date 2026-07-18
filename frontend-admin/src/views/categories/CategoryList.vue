<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <h3>分类列表</h3>
        <el-button type="primary" @click="openDialog()">新增分类</el-button>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" min-width="140" />
        <el-table-column prop="parentId" label="父分类 ID" width="110" />
        <el-table-column prop="level" label="级别" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" style="width: 100%" @change="onParentChange">
            <el-option label="顶级分类" :value="0" />
            <el-option
              v-for="item in parentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-input-number v-model="form.level" :min="1" :max="2" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createCategoryApi,
  deleteCategoryApi,
  getAdminCategoriesApi,
  updateCategoryApi
} from '@/api/category'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const list = ref([])

const form = reactive({
  id: null,
  name: '',
  parentId: 0,
  level: 1,
  sortOrder: 0,
  status: 0,
  description: '',
  icon: ''
})

const parentOptions = computed(() =>
  list.value.filter((item) => item.level === 1 && item.id !== form.id)
)

const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    parentId: 0,
    level: 1,
    sortOrder: 0,
    status: 0,
    description: '',
    icon: ''
  })
}

const onParentChange = (value) => {
  form.level = value === 0 ? 1 : 2
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminCategoriesApi()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  resetForm()
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.name) {
    ElMessage.warning('请填写分类名称')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await updateCategoryApi(form)
      ElMessage.success('更新成功')
    } else {
      await createCategoryApi(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteCategoryApi(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.panel {
  border-radius: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.toolbar h3 {
  margin: 0;
}
</style>
