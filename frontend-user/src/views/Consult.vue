<template>
  <div class="consult-page page-enter" v-loading="loading">
    <h1>找书咨询</h1>
    <p class="hint">
      书架上还没来得及上架的还很多。如果您没有找到需要的书籍，可以加店家微信联系，
      也可以在这里给店家留言，我们会尽快回复您。
    </p>

    <ShopContactCard
      v-if="shopContact"
      class="contact-block"
      variant="banner"
      :contact="shopContact"
      title="店家联系方式"
      lead="没有找到需要的书？扫码加微信，或填写下方留言"
    />

    <div class="form-card">
      <h3>给店家留言</h3>
      <p class="form-tip">请留下您的姓名和联系方式（电话或微信），方便店家与您联系。</p>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @submit.prevent="onSubmit"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" maxlength="20" placeholder="您的称呼，如：张同学" clearable />
        </el-form-item>
        <el-form-item label="电话 / 微信" prop="phone">
          <el-input v-model="form.phone" maxlength="50" placeholder="手机号或微信号，方便店家回复" clearable />
        </el-form-item>
        <el-form-item label="想找的书（选填）" prop="bookTitle">
          <el-input v-model="form.bookTitle" maxlength="100" placeholder="书名或作者，如：《活着》/ 余华" clearable />
        </el-form-item>
        <el-form-item label="留言内容（选填）" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="可以描述想要的版本、品相、数量等，也可以问问店家有没有这本书"
          />
        </el-form-item>
        <el-button
          type="primary"
          :loading="submitting"
          class="submit-btn"
          @click="onSubmit"
        >提交咨询</el-button>
      </el-form>
    </div>

    <el-dialog
      v-model="successVisible"
      title="咨询已提交"
      width="420px"
      align-center
    >
      <div class="success-box">
        <div class="success-icon">✅</div>
        <p class="success-title">留言已提交</p>
        <p class="success-desc">
          店家收到后会通过您留下的电话或微信与您联系。<br />
          想更快得到回复，也可以直接扫码加店家微信。
        </p>
        <ShopContactCard
          v-if="shopContact"
          :contact="shopContact"
          title="店家微信"
          :lead="null"
        />
      </div>
      <template #footer>
        <el-button @click="successVisible = false">关闭</el-button>
        <el-button type="primary" @click="goBooks">继续逛书架</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShopContactApi } from '@/api/shop'
import { createConsultationApi } from '@/api/consultation'
import ShopContactCard from '@/components/ShopContactCard.vue'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const successVisible = ref(false)
const formRef = ref(null)
const shopContact = ref(null)

const form = reactive({
  name: '',
  phone: '',
  bookTitle: '',
  content: ''
})

const rules = {
  name: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请填写电话或微信', trigger: 'blur' }]
}

const loadShopContact = async () => {
  try {
    const res = await getShopContactApi()
    shopContact.value = res?.data || null
  } catch {
    shopContact.value = null
  }
}

const onSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    await createConsultationApi({
      name: form.name,
      phone: form.phone,
      bookTitle: form.bookTitle,
      content: form.content
    })
    successVisible.value = true
    form.name = ''
    form.phone = ''
    form.bookTitle = ''
    form.content = ''
  } catch (e) {
    ElMessage.error(e?.message || '提交失败，请稍后再试')
  } finally {
    submitting.value = false
  }
}

const goBooks = () => {
  successVisible.value = false
  router.push('/books')
}

onMounted(async () => {
  loading.value = true
  try {
    await loadShopContact()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.consult-page {
  max-width: 720px;
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
  line-height: 1.7;
}

.contact-block {
  margin-bottom: 18px;
}

.form-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 20px 22px;
}

.form-card h3 {
  margin: 0 0 4px;
  font-size: 17px;
  color: var(--ink);
}

.form-tip {
  margin: 0 0 16px;
  font-size: 13px;
  color: var(--muted);
  line-height: 1.5;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

/* 弹窗 */
.success-box {
  text-align: center;
  padding: 4px 0 8px;
}

.success-icon {
  font-size: 42px;
  line-height: 1;
}

.success-title {
  margin: 10px 0 6px;
  font-size: 17px;
  color: var(--ink);
  font-weight: 600;
}

.success-desc {
  margin: 0 0 14px;
  font-size: 13px;
  color: var(--ink-soft);
  line-height: 1.7;
}

@media (max-width: 768px) {
  h1 {
    font-size: 20px;
  }

  .form-card {
    padding: 16px 14px;
    border-radius: 10px;
  }
}
</style>
