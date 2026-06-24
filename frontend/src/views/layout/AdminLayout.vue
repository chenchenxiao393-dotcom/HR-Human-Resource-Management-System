<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-sidebar">
      <div class="admin-logo">
        <div class="logo-icon">
          <el-icon :size="22"><Briefcase /></el-icon>
        </div>
        <div class="logo-text">
          <div class="logo-title">HR管理</div>
          <div class="logo-sub">企业人事系统</div>
        </div>
      </div>

      <el-scrollbar class="sidebar-scroll">
        <el-menu
          :default-active="activeMenu"
          router
          class="admin-menu"
          background-color="transparent"
          text-color="#7d8ba0"
          active-text-color="#1f6feb"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>工作台</span>
          </el-menu-item>

          <el-sub-menu index="personnel">
            <template #title>
              <el-icon><User /></el-icon>
              <span>人事管理</span>
            </template>
            <el-menu-item index="/admin/personnel">人员管理</el-menu-item>
            <el-menu-item index="/admin/department">部门管理</el-menu-item>
            <el-menu-item index="/admin/contract">合同管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="attendance">
            <template #title>
              <el-icon><Clock /></el-icon>
              <span>考勤管理</span>
            </template>
            <el-menu-item index="/admin/attendance">考勤记录</el-menu-item>
            <el-menu-item index="/admin/overtime">加班管理</el-menu-item>
            <el-menu-item index="/admin/business-trip">出差管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="change">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>人事变动</span>
            </template>
            <el-menu-item index="/admin/personnel-change">变动记录</el-menu-item>
            <el-menu-item index="/admin/resignation">离职管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="performance">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>考核奖惩</span>
            </template>
            <el-menu-item index="/admin/appraisal">考核记录</el-menu-item>
            <el-menu-item index="/admin/reward-punishment">奖惩记录</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/training">
            <el-icon><Medal /></el-icon>
            <span>培训管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/task">
            <el-icon><List /></el-icon>
            <span>任务管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/notification">
            <el-icon><Bell /></el-icon>
            <span>通知管理</span>
          </el-menu-item>

          <el-sub-menu index="system" v-if="userStore.isAdmin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统维护</span>
            </template>
            <el-menu-item index="/admin/system">系统设置</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>

      <div class="sidebar-footer">
        <div class="footer-line"></div>
        <div class="footer-version">v1.0.0 · 企业版</div>
      </div>
    </el-aside>

    <el-container class="admin-main-wrap">
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/" class="admin-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <div class="header-role">
            <span class="role-badge">
              <el-icon><UserFilled /></el-icon>
              <span>{{ roleLabel }}</span>
            </span>
          </div>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="header-user">
              <div class="user-avatar">
                {{ avatarText }}
              </div>
              <div class="user-info-meta">
                <div class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username || '管理员' }}</div>
                <div class="user-status">
                  <span class="status-dot"></span>
                  在线
                </div>
              </div>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人信息</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <el-dialog v-model="profileDialogVisible" title="个人信息" width="400px" class="profile-dialog">
      <div class="profile-simple">
        <!-- 头像区 -->
        <div class="simple-avatar-section">
          <div class="simple-avatar">{{ avatarText }}</div>
          <div class="simple-name">{{ profileForm.realName || '管理员' }}</div>
          <div class="simple-role">{{ profileForm.roleName }}</div>
        </div>

        <el-divider />

        <!-- 信息区 -->
        <div class="simple-info">
          <div class="simple-row">
            <span class="simple-label">出生日期</span>
            <template v-if="isEditMode">
              <el-date-picker
                v-model="employeeForm.birthDate"
                type="date"
                size="small"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 160px"
              />
            </template>
            <span v-else class="simple-value">{{ employeeForm.birthDate || '未设置' }}</span>
          </div>

          <div class="simple-row">
            <span class="simple-label">联系电话</span>
            <template v-if="isEditMode">
              <el-input v-model="employeeForm.phone" size="small" style="width: 160px" placeholder="请输入电话" />
            </template>
            <span v-else class="simple-value">{{ employeeForm.phone || '未设置' }}</span>
          </div>

          <div class="simple-row">
            <span class="simple-label">电子邮箱</span>
            <template v-if="isEditMode">
              <el-input v-model="employeeForm.email" size="small" style="width: 160px" placeholder="请输入邮箱" />
            </template>
            <span v-else class="simple-value">{{ employeeForm.email || '未设置' }}</span>
          </div>

          <div class="simple-row" v-if="!isPasswordMode">
            <span class="simple-label">登录密码</span>
            <el-link type="primary" :underline="false" @click="isPasswordMode = true">修改密码</el-link>
          </div>

          <!-- 修改密码 -->
          <div v-if="isPasswordMode" class="simple-password">
            <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" size="small">
              <el-form-item prop="oldPassword" label="原密码" label-width="70px">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="原密码" />
              </el-form-item>
              <el-form-item prop="newPassword" label="新密码" label-width="70px">
                <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="新密码（至少6位）" />
              </el-form-item>
              <el-form-item prop="confirmPassword" label="确认密码" label-width="70px">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="确认新密码" />
              </el-form-item>
            </el-form>
            <div class="simple-pwd-btns">
              <el-button size="small" @click="cancelPasswordChange">取消</el-button>
              <el-button type="primary" size="small" :loading="passwordLoading" @click="handlePasswordChange">确认修改</el-button>
            </div>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="simple-footer">
          <template v-if="!isEditMode && !isPasswordMode">
            <el-button @click="profileDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="isEditMode = true">编辑资料</el-button>
          </template>
          <template v-else-if="isEditMode">
            <el-button @click="handleCancelEdit">取消</el-button>
            <el-button type="primary" @click="handleSaveEmployeeProfile">保存</el-button>
          </template>
        </div>
      </div>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Briefcase, HomeFilled, User, Clock, TrendCharts, DataAnalysis, Medal, Setting, House, UserFilled, ArrowDown, SwitchButton, Edit, Check, List, Lock, Bell } from '@element-plus/icons-vue'
import { updateProfile, getEmployeeProfile, updateEmployeeProfile, changePassword } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const roleLabel = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 'ADMIN') return '系统管理员'
  if (role === 'HR') return '人事专员'
  return '普通员工'
})

const avatarText = computed(() => {
  const name = userStore.userInfo?.realName || userStore.userInfo?.username || '管'
  return name.charAt(0)
})

const profileDialogVisible = ref(false)
const isEditMode = ref(false)
const isPasswordMode = ref(false)
const passwordLoading = ref(false)
const passwordFormRef = ref(null)
const profileForm = reactive({
  username: '',
  realName: '',
  role: '',
  roleName: '',
  status: ''
})
const employeeForm = reactive({
  id: '',
  employeeCode: '',
  name: '',
  gender: '',
  birthDate: '',
  phone: '',
  email: '',
  address: '',
  householdType: '',
  politicalAffiliation: '',
  healthStatus: '',
  hireDate: '',
  departmentName: '',
  position: '',
  employeeStatus: '',
  salary: ''
})
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }]
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    profileForm.username = userStore.userInfo?.username || ''
    profileForm.realName = userStore.userInfo?.realName || ''
    profileForm.role = userStore.userInfo?.role || ''
    profileForm.roleName = roleLabel.value
    profileForm.status = '正常'
    isEditMode.value = false
    
    try {
      const response = await getEmployeeProfile()
      if (response.data) {
        const emp = response.data
        employeeForm.id = emp.id || ''
        employeeForm.employeeCode = emp.employeeCode || ''
        employeeForm.name = emp.name || ''
        employeeForm.gender = emp.gender || ''
        employeeForm.birthDate = emp.birthDate || ''
        employeeForm.phone = emp.phone || ''
        employeeForm.email = emp.email || ''
        employeeForm.address = emp.address || ''
        employeeForm.householdType = emp.householdType || ''
        employeeForm.politicalAffiliation = emp.politicalAffiliation || ''
        employeeForm.healthStatus = emp.healthStatus || ''
        employeeForm.hireDate = emp.hireDate || ''
        employeeForm.departmentName = emp.departmentName || '未分配'
        employeeForm.position = emp.position || ''
        employeeForm.employeeStatus = emp.employeeStatus || ''
        employeeForm.salary = emp.salary || ''
      }
    } catch (error) {
      console.error('获取员工信息失败:', error)
    }
    
    profileDialogVisible.value = true
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  }
}

const handleUpdateProfile = async () => {
  try {
    await updateProfile({
      username: profileForm.username,
      realName: profileForm.realName
    })
    userStore.setUserInfo({
      ...userStore.userInfo,
      realName: profileForm.realName
    })
    // 同步更新 employeeForm 中的 name
    employeeForm.name = profileForm.realName
    ElMessage.success('保存成功')
    profileDialogVisible.value = false
  } catch (error) {
    console.error(error)
  }
}

const handleSaveEmployeeProfile = async () => {
  try {
    await updateEmployeeProfile({
      id: employeeForm.id,
      birthDate: employeeForm.birthDate,
      phone: employeeForm.phone,
      email: employeeForm.email
    })
    ElMessage.success('保存成功')
    isEditMode.value = false
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const handleCancelEdit = () => {
  isEditMode.value = false
}

const cancelPasswordChange = () => {
  isPasswordMode.value = false
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const handlePasswordChange = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    passwordLoading.value = true
    try {
      await changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword,
        confirmPassword: passwordForm.confirmPassword
      })
      ElMessage.success('密码修改成功，请重新登录')
      setTimeout(() => {
        userStore.logout()
        router.push('/login')
      }, 1500)
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.response?.data?.message || '修改密码失败')
    } finally {
      passwordLoading.value = false
    }
  })
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background: #f5f7fb;
}

.admin-sidebar {
  background: #0f1d30;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid #1f2a3d;
}

.admin-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #1f6feb 0%, #4d9fff 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(31, 111, 235, 0.3);
}

.logo-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.2;
}

.logo-sub {
  color: #5d6e85;
  font-size: 11px;
  margin-top: 3px;
}

.sidebar-scroll {
  flex: 1;
  padding: 12px 10px;
}

.admin-menu {
  border-right: none;
}

.admin-menu :deep(.el-menu-item),
.admin-menu :deep(.el-sub-menu__title) {
  height: 42px;
  line-height: 42px;
  margin: 2px 0;
  border-radius: 8px;
  transition: all 0.2s;
}

.admin-menu :deep(.el-menu-item:hover),
.admin-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.04);
  color: #c9d4e5;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(31, 111, 235, 0.15) 0%, rgba(31, 111, 235, 0.05) 100%);
  color: #66a7ff;
  position: relative;
}

.admin-menu :deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: -10px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  background: #1f6feb;
  border-radius: 0 2px 2px 0;
}

.admin-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 46px !important;
  min-width: auto;
  font-size: 13px;
}

.sidebar-footer {
  padding: 16px 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.footer-line {
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.1) 50%, transparent 100%);
  margin-bottom: 10px;
}

.footer-version {
  font-size: 11px;
  color: #4a5768;
  text-align: center;
}

.admin-main-wrap {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  border-bottom: 1px solid #eef0f4;
  height: 64px;
}

.admin-breadcrumb :deep(.el-breadcrumb__inner) {
  color: #8a94a6;
  font-size: 14px;
}

.admin-breadcrumb :deep(.el-breadcrumb__inner.is-link) {
  color: #1f6feb;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: #f0f5ff;
  color: #1f6feb;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px 6px 6px;
  border-radius: 30px;
  cursor: pointer;
  transition: background 0.2s;
}

.header-user:hover {
  background: #f5f7fb;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1f6feb 0%, #4d9fff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.user-info-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.user-name {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.user-status {
  font-size: 12px;
  color: #8a94a6;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 2px;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #52c41a;
  border-radius: 50%;
  display: inline-block;
}

.arrow-icon {
  color: #8a94a6;
  font-size: 12px;
}

.admin-content {
  background: #f5f7fb;
  padding: 24px 28px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.profile-form {
  padding-top: 10px;
}

/* 简洁商务风个人信息弹窗 */
.profile-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.profile-simple {
  padding: 0 0 16px;
}

.simple-avatar-section {
  text-align: center;
  padding: 28px 0 16px;
}

.simple-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: #1f6feb;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
  margin: 0 auto 12px;
}

.simple-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.simple-role {
  font-size: 13px;
  color: #8a94a6;
  margin-top: 4px;
}

.simple-info {
  padding: 0 24px;
}

.simple-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #f0f2f5;
}

.simple-row:last-child {
  border-bottom: none;
}

.simple-label {
  font-size: 14px;
  color: #666;
}

.simple-value {
  font-size: 14px;
  color: #1a1a1a;
}

.simple-password {
  padding: 12px 0;
}

.simple-pwd-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

.simple-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 16px 24px 0;
  border-top: 1px solid #f0f2f5;
  margin-top: 16px;
}
</style>
