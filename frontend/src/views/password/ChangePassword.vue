<template>
  <div class="change-password-container">
    <div class="page-heading">
      <div class="heading-left">
        <div class="breadcrumb-mini" @click="navigateTo(homePath)">
          <el-icon><House /></el-icon>
        </div>
        <h2 class="page-title">修改密码</h2>
        <span class="page-divider">/</span>
        <span class="page-subtitle">账号安全</span>
      </div>
      <div class="heading-right">
        <span class="heading-tip">
          <el-icon :size="12"><Sunny /></el-icon>
          {{ currentDate }}
        </span>
      </div>
    </div>

    <div class="password-card">
      <div class="card-header">
        <div class="header-icon">
          <el-icon :size="28"><Lock /></el-icon>
        </div>
        <div class="header-text">
          <h3>修改登录密码</h3>
          <p>请妥善保管您的新密码</p>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="password-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
            size="large"
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
            size="large"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
          <div class="password-strength" v-if="form.newPassword">
            <span class="strength-label">密码强度：</span>
            <div class="strength-bar">
              <div class="strength-item" :class="{ active: passwordStrength >= 1 }">
                <span>弱</span>
              </div>
              <div class="strength-item" :class="{ active: passwordStrength >= 2 }">
                <span>中</span>
              </div>
              <div class="strength-item" :class="{ active: passwordStrength >= 3 }">
                <span>强</span>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            size="large"
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="form-tips">
          <el-icon><InfoFilled /></el-icon>
          <span>密码长度不能少于6位，建议包含字母、数字和特殊字符</span>
        </div>

        <el-form-item class="form-actions">
          <el-button size="large" @click="handleCancel">
            取消
          </el-button>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
            <el-icon v-if="!loading"><Check /></el-icon>
            确认修改
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Lock, Key, Check, House, Sunny, InfoFilled } from '@element-plus/icons-vue'
import { changePassword } from '@/api/password'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const homePath = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 'EMPLOYEE') return '/user/dashboard'
  return '/admin/dashboard'
})

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 计算密码强度
const passwordStrength = computed(() => {
  const password = form.newPassword
  if (!password) return 0
  
  let strength = 0
  if (password.length >= 6) strength++
  if (password.length >= 10) strength++
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++
  
  return Math.min(strength, 3)
})

const currentDate = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const navigateTo = (path) => {
  router.push(path || homePath.value)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      await changePassword({
        oldPassword: form.oldPassword,
        newPassword: form.newPassword,
        confirmPassword: form.confirmPassword
      })
      
      ElMessage.success('密码修改成功，请重新登录')
      
      // 延迟执行退出登录，让用户看到成功提示
      setTimeout(() => {
        userStore.logout()
        router.push('/login')
      }, 1500)
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.response?.data?.message || '修改密码失败')
    } finally {
      loading.value = false
    }
  })
}

const handleCancel = () => {
  ElMessageBox.confirm('确定要取消修改吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push(homePath.value)
  }).catch(() => {})
}
</script>

<style scoped>
.change-password-container {
  max-width: 600px;
  margin: 0 auto;
}

.page-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 18px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.heading-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.breadcrumb-mini {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f3f5fc;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.breadcrumb-mini:hover {
  background: #667eea;
  color: #fff;
  transform: translateY(-1px);
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a2235;
  margin: 0;
  letter-spacing: -0.3px;
}

.page-divider {
  color: #c9d2e0;
  font-size: 14px;
}

.page-subtitle {
  font-size: 13px;
  color: #8a94a6;
  font-weight: 500;
}

.heading-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.heading-tip {
  font-size: 13px;
  color: #8a94a6;
  padding: 6px 14px;
  background: #f5f7fb;
  border-radius: 20px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.password-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 32px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 28px;
  border-bottom: 1px solid #f0f2f5;
  margin-bottom: 28px;
}

.header-icon {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
}

.header-text h3 {
  margin: 0 0 6px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1a2235;
}

.header-text p {
  margin: 0;
  font-size: 14px;
  color: #8a94a6;
}

.password-form {
  max-width: 420px;
  margin: 0 auto;
}

.password-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #2c3e50;
  padding-bottom: 8px;
}

.password-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 0 0 1px #e4e7ed;
  transition: all 0.2s;
}

.password-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

.password-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2), 0 0 0 1px #667eea;
}

.password-form :deep(.el-input__prefix .el-icon) {
  color: #8a94a6;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
}

.strength-label {
  font-size: 12px;
  color: #8a94a6;
}

.strength-bar {
  display: flex;
  gap: 6px;
}

.strength-item {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
  background: #f0f2f5;
  color: #8a94a6;
  transition: all 0.3s;
}

.strength-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.form-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  background: #f8f9fc;
  border-radius: 12px;
  margin-bottom: 28px;
  font-size: 13px;
  color: #8a94a6;
}

.form-tips .el-icon {
  color: #667eea;
  flex-shrink: 0;
}

.form-actions {
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.form-actions :deep(.el-button) {
  border-radius: 12px;
  padding: 14px 36px;
  font-weight: 600;
}

.form-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
}

.form-actions :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.45);
}
</style>
