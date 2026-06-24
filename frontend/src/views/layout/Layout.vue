<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h2>人事管理</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-sub-menu index="personnel">
          <template #title>
            <el-icon><User /></el-icon>
            <span>人事管理</span>
          </template>
          <el-menu-item index="/employee">员工管理</el-menu-item>
          <el-menu-item index="/department">部门管理</el-menu-item>
          <el-menu-item index="/contract">合同管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="attendance">
          <template #title>
            <el-icon><Clock /></el-icon>
            <span>考勤管理</span>
          </template>
          <el-menu-item index="/attendance">考勤记录</el-menu-item>
          <el-menu-item index="/overtime">加班管理</el-menu-item>
          <el-menu-item index="/business-trip">出差管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="change">
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>人事变动</span>
          </template>
          <el-menu-item index="/personnel-change">变动记录</el-menu-item>
          <el-menu-item index="/resignation">离职管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="performance">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>考核奖惩</span>
          </template>
          <el-menu-item index="/appraisal">考核记录</el-menu-item>
          <el-menu-item index="/reward-punishment">奖惩记录</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/training">
          <el-icon><Medal /></el-icon>
          <span>培训管理</span>
        </el-menu-item>

        <el-sub-menu index="system" v-if="userStore.isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统维护</span>
          </template>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/system">系统设置</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>

    <el-dialog v-model="profileDialogVisible" title="个人信息" width="500px">
      <el-form :model="profileForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="profileForm.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="profileForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="状态">
          <el-input v-model="profileForm.status" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile">保存</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { updateProfile } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const profileDialogVisible = ref(false)
const profileForm = reactive({
  username: '',
  realName: '',
  role: '',
  roleName: '',
  status: ''
})

const handleCommand = (command) => {
  if (command === 'profile') {
    profileForm.username = userStore.userInfo?.username || ''
    profileForm.realName = userStore.userInfo?.realName || ''
    profileForm.role = userStore.userInfo?.role || ''
    profileForm.roleName = userStore.userInfo?.role === 'ADMIN' ? '管理员' : userStore.userInfo?.role === 'HR' ? '人事专员' : '普通员工'
    profileForm.status = userStore.userInfo?.status || '正常'
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
    ElMessage.success('保存成功')
    profileDialogVisible.value = false
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #263445;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #303133;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
}
</style>
