<template>
  <div class="page">
    <el-card shadow="never" class="panel" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>店铺信息</span>
          <span class="sub">将展示在确认订单、下单成功与我的订单页；上传微信二维码后，买家可直接扫码添加</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" style="max-width: 620px">
        <el-form-item label="店家名称" prop="shopName">
          <el-input v-model="form.shopName" maxlength="100" placeholder="例如：三定圆梦书店" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" maxlength="30" placeholder="买家联系用的手机号或座机" />
        </el-form-item>
        <el-form-item label="店铺地址" prop="address">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="店铺地址或见面取书地点"
          />
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="form.wechatId" maxlength="100" placeholder="选填，买家可搜索添加" />
        </el-form-item>
        <el-form-item label="微信二维码">
          <div class="qrcode-field">
            <el-upload
              class="qrcode-uploader"
              :show-file-list="false"
              accept="image/jpeg,image/png,image/gif,image/webp,image/bmp"
              :http-request="handleUpload"
              :disabled="uploading"
            >
              <img v-if="form.wechatQrcode" :src="form.wechatQrcode" class="qrcode-preview" alt="微信二维码" />
              <div v-else class="qrcode-placeholder">
                <el-icon class="qrcode-uploader-icon"><Plus /></el-icon>
                <span>{{ uploading ? '上传中...' : '上传二维码' }}</span>
              </div>
            </el-upload>
            <div class="qrcode-actions">
              <el-button v-if="form.wechatQrcode" link type="danger" @click="form.wechatQrcode = ''">移除</el-button>
              <p class="tip">建议上传清晰的微信「我的二维码」截图，方便买家扫码添加</p>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="下单提示">
          <el-input
            v-model="form.tip"
            type="textarea"
            :rows="2"
            maxlength="500"
            placeholder="选填，展示给买家的说明"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存</el-button>
          <el-button @click="loadData">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getShopSettingsApi, updateShopSettingsApi } from '@/api/shop'
import { uploadImageApi } from '@/api/upload'

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const formRef = ref()
const form = reactive({
  shopName: '',
  contactPhone: '',
  address: '',
  wechatId: '',
  wechatQrcode: '',
  tip: ''
})

const rules = {
  shopName: [{ required: true, message: '请填写店家名称', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请填写联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请填写店铺地址', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getShopSettingsApi()
    const data = res.data || {}
    form.shopName = data.shopName || ''
    form.contactPhone = data.contactPhone || ''
    form.address = data.address || ''
    form.wechatId = data.wechatId || ''
    form.wechatQrcode = data.wechatQrcode || ''
    form.tip = data.tip || ''
  } finally {
    loading.value = false
  }
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
    form.wechatQrcode = res.data
    ElMessage.success('二维码上传成功')
  } finally {
    uploading.value = false
  }
}

const save = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await updateShopSettingsApi({ ...form })
    ElMessage.success('店铺信息已保存')
    await loadData()
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.panel {
  border-radius: 12px;
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sub {
  font-size: 13px;
  color: var(--admin-muted);
  font-weight: 400;
}

.qrcode-field {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.qrcode-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.2s;
}

.qrcode-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.qrcode-preview,
.qrcode-placeholder {
  width: 140px;
  height: 140px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  object-fit: contain;
  background: #f5f7fa;
  color: #909399;
  font-size: 13px;
}

.qrcode-uploader-icon {
  font-size: 24px;
}

.qrcode-actions {
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
  max-width: 280px;
}
</style>
