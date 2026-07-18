<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <div>
          <h3>分类列表</h3>
          <p class="hint">按住左侧图标拖动调整顺序，用户端分类将按此顺序展示</p>
        </div>
        <el-button type="primary" @click="openDialog()">新增分类</el-button>
      </div>

      <el-table
        ref="tableRef"
        :data="list"
        v-loading="loading"
        stripe
        row-key="id"
        class="category-table"
      >
        <el-table-column label="" width="52" align="center">
          <template #default>
            <span class="drag-handle" title="拖动排序">
              <el-icon :size="16"><Rank /></el-icon>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" min-width="140" />
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
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Sortable from 'sortablejs'
import {
  createCategoryApi,
  deleteCategoryApi,
  getAdminCategoriesApi,
  sortCategoriesApi,
  updateCategoryApi
} from '@/api/category'

const loading = ref(false)
const saving = ref(false)
const sorting = ref(false)
const dialogVisible = ref(false)
const list = ref([])
const tableRef = ref()
let sortable = null

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

const destroySortable = () => {
  if (sortable) {
    sortable.destroy()
    sortable = null
  }
}

const initSortable = async () => {
  await nextTick()
  destroySortable()
  const root = tableRef.value?.$el
  if (!root) return
  const tbody = root.querySelector('.el-table__body-wrapper tbody')
  if (!tbody) return

  sortable = Sortable.create(tbody, {
    handle: '.drag-handle',
    animation: 180,
    ghostClass: 'drag-ghost',
    chosenClass: 'drag-chosen',
    forceFallback: true,
    onEnd: async (evt) => {
      const { oldIndex, newIndex } = evt
      if (oldIndex == null || newIndex == null || oldIndex === newIndex) return

      const next = [...list.value]
      const [moved] = next.splice(oldIndex, 1)
      next.splice(newIndex, 0, moved)
      list.value = next.map((item, index) => ({
        ...item,
        sortOrder: index + 1
      }))

      sorting.value = true
      try {
        await sortCategoriesApi(list.value.map((item) => item.id))
        ElMessage.success('排序已更新，用户端将按此顺序展示')
      } catch {
        await loadData()
      } finally {
        sorting.value = false
      }
    }
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminCategoriesApi()
    list.value = res.data || []
    await initSortable()
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
  form.level = form.parentId === 0 ? 1 : 2
  if (!form.id) {
    const maxSort = list.value.reduce((max, item) => Math.max(max, item.sortOrder || 0), 0)
    form.sortOrder = maxSort + 1
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
    await loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteCategoryApi(row.id)
  ElMessage.success('删除成功')
  await loadData()
}

watch(list, () => {
  if (!sortable && list.value.length) {
    initSortable()
  }
})

onMounted(loadData)

onBeforeUnmount(destroySortable)
</script>

<style scoped>
.panel {
  border-radius: 12px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.toolbar h3 {
  margin: 0;
}

.hint {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--admin-muted);
}

.drag-handle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  color: #909399;
  cursor: grab;
  user-select: none;
  touch-action: none;
}

.drag-handle:active {
  cursor: grabbing;
  background: #f3f4f6;
  color: #409eff;
}

:deep(.drag-ghost) {
  opacity: 0.55;
  background: #ecf5ff !important;
}

:deep(.drag-chosen) {
  background: #f5faff !important;
}
</style>
