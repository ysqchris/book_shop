<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-copy">
        <p class="eyebrow">三定圆梦 · 管理后台</p>
        <h1>三定圆梦书店<br />管理后台</h1>
        <p>管理图书、分类、用户与订单</p>
      </div>
      <el-card class="login-card" shadow="never">
        <h2>管理员登录</h2>
        <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" size="large" placeholder="用户名" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-button type="primary" size="large" class="submit" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form>
        <p class="hint">测试账号：admin / admin123</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at top left, rgba(47, 128, 237, 0.18), transparent 35%),
    linear-gradient(135deg, #eef2f6 0%, #dbe4ee 100%);
  padding: 24px;
}

.login-panel {
  width: min(920px, 100%);
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 24px;
  align-items: center;
}

.login-copy .eyebrow {
  margin: 0 0 12px;
  color: #2f80ed;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  font-size: 13px;
}

.login-copy h1 {
  margin: 0 0 12px;
  font-size: 40px;
  line-height: 1.2;
}

.login-copy p:last-child {
  margin: 0;
  color: var(--admin-muted);
}

.login-card {
  border-radius: 16px;
  border: 1px solid rgba(31, 41, 55, 0.08);
  padding: 12px;
}

.login-card h2 {
  margin: 0 0 20px;
}

.submit {
  width: 100%;
}

.hint {
  margin: 16px 0 0;
  color: var(--admin-muted);
  font-size: 13px;
}

@media (max-width: 768px) {
  .login-page {
    padding: 16px;
    align-items: stretch;
  }

  .login-panel {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .login-copy h1 {
    font-size: 28px;
  }

  .login-copy {
    text-align: center;
  }
}
</style>
