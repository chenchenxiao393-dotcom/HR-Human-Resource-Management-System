<template>
  <div class="user-layout">
    <header class="user-header">
      <div class="header-inner">
        <div class="brand" @click="navigateTo('/user/dashboard')">
          <div class="brand-logo">
            <el-icon :size="22"><Briefcase /></el-icon>
          </div>
          <div class="brand-text">
            <div class="brand-title">HR管理系统</div>
            <div class="brand-sub">员工服务平台</div>
          </div>
        </div>

        <el-menu
          class="user-nav"
          mode="horizontal"
          :default-active="activePath"
          :ellipsis="false"
          background-color="transparent"
          text-color="#5a6478"
          active-text-color="#667eea"
          menu-trigger="click"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/user/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-sub-menu index="hr">
            <template #title>
              <el-icon><User /></el-icon>
              <span>人事管理</span>
            </template>
            <el-menu-item index="/user/employee">员工管理</el-menu-item>
            <el-menu-item index="/user/department">部门管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="att">
            <template #title>
              <el-icon><Clock /></el-icon>
              <span>考勤管理</span>
            </template>
            <el-menu-item index="/user/attendance">考勤记录</el-menu-item>
            <el-menu-item index="/user/overtime">加班申请</el-menu-item>
            <el-menu-item index="/user/business-trip">出差申请</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="chg">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>人事变动</span>
            </template>
            <el-menu-item index="/user/personnel-change">变动记录</el-menu-item>
            <el-menu-item index="/user/resignation">离职管理</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/user/reward-punishment">
            <el-icon><DataAnalysis /></el-icon>
            <span>奖惩记录</span>
          </el-menu-item>

          <el-menu-item index="/user/training">
            <el-icon><Medal /></el-icon>
            <span>培训管理</span>
          </el-menu-item>

          <el-menu-item index="/user/salary">
            <el-icon><Money /></el-icon>
            <span>工资查询</span>
          </el-menu-item>

          <el-menu-item index="/user/task">
            <el-icon><List /></el-icon>
            <span>我的任务</span>
          </el-menu-item>
        </el-menu>

        <div class="header-actions">
          <el-tooltip content="刷新页面" placement="bottom" effect="light">
            <button class="action-btn" @click="refreshPage">
              <el-icon :size="18"><Refresh /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip content="通知消息" placement="bottom" effect="light">
            <el-popover
              v-model:visible="notificationVisible"
              placement="bottom-end"
              width="360"
              :trigger="'click'"
              popper-class="notification-popover"
              @show="loadNotifications"
            >
              <template #reference>
                <button class="action-btn badge-btn">
                  <el-icon :size="18"><Bell /></el-icon>
                  <span class="action-badge" v-if="notificationCount > 0">{{ notificationCount }}</span>
                </button>
              </template>
              <div class="notification-header">
                <h3 class="notification-title">通知消息</h3>
                <span class="notification-count">共 {{ notifications.length }} 条</span>
              </div>
              <el-divider class="notification-divider" />
              <div class="notification-list">
                <div
                  v-for="notification in notifications"
                  :key="notification.id"
                  class="notification-item"
                  :class="{ 'is-unread': notification.status === '未读' }"
                  @click="handleNotificationClick(notification)"
                >
                  <div class="notification-icon">
                    <el-icon :size="18"><Bell /></el-icon>
                  </div>
                  <div class="notification-content">
                    <div class="notification-title">{{ notification.title }}</div>
                    <div class="notification-desc">{{ notification.content }}</div>
                    <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
                  </div>
                  <span v-if="notification.status === '未读'" class="notification-dot"></span>
                </div>
                <div v-if="notifications.length === 0" class="notification-empty">
                  <el-icon :size="32" class="empty-icon"><Bell /></el-icon>
                  <p>暂无通知</p>
                </div>
              </div>
            </el-popover>
          </el-tooltip>

          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-profile">
              <div class="user-avatar-ring">
                <div class="user-avatar-inner">{{ avatarText }}</div>
              </div>
              <div class="user-detail">
                <div class="user-title">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</div>
                <div class="user-role">
                  <el-icon :size="11"><Sunny /></el-icon>
                  <span>{{ greetingShort }}</span>
                </div>
              </div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown">
                <div class="dropdown-header">
                  <div class="dropdown-avatar">{{ avatarText }}</div>
                  <div class="dropdown-info">
                    <div class="dropdown-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</div>
                    <div class="dropdown-role">{{ currentDate }} · {{ currentWeekday }}</div>
                  </div>
                </div>
                <el-dropdown-item command="profile" class="dropdown-item-custom">
                  <el-icon><User /></el-icon>
                  <span>个人信息</span>
                </el-dropdown-item>
                <el-dropdown-item command="changePassword" class="dropdown-item-custom">
                  <el-icon><Lock /></el-icon>
                  <span>修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item command="help" class="dropdown-item-custom">
                  <el-icon><QuestionFilled /></el-icon>
                  <span>帮助中心</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" class="dropdown-item-logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="user-main">
      <!-- 首页：欢迎卡片 -->
      <div class="page-hero" v-if="isUserDashboard">
        <div class="hero-bg"></div>
        <div class="hero-overlay"></div>
        <div class="hero-content">
          <div class="hero-left">
            <div class="hero-badge">
              <el-icon><Sunny /></el-icon>
              <span>{{ greetingText }}</span>
            </div>
            <h1 class="hero-title">
              {{ userStore.userInfo?.realName || userStore.userInfo?.username }}，<span>欢迎回来</span>
            </h1>
            <div class="hero-quote">
              <el-icon><Reading /></el-icon>
              <p>{{ currentQuote }}</p>
            </div>
            <div class="hero-actions">
              <button class="hero-btn-primary" @click="navigateTo('/user/attendance')">
                <el-icon><Calendar /></el-icon>
                <span>考勤打卡</span>
              </button>
              <button class="hero-btn-outline" @click="navigateTo('/user/overtime')">
                <el-icon><Edit /></el-icon>
                <span>加班申请</span>
              </button>
              <button class="hero-btn-light" @click="changeQuote">
                <el-icon><MagicStick /></el-icon>
                <span>换一句</span>
              </button>
            </div>
          </div>
          <div class="hero-right">
            <div class="date-card">
              <div class="date-day">{{ new Date().getDate() }}</div>
              <div class="date-month">{{ new Date().getMonth() + 1 }} 月</div>
              <div class="date-week">{{ currentWeekday }}</div>
              <div class="date-full">{{ currentDate }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-block">
                <div class="stat-num">{{ applyStats.monthlyTotal }}</div>
                <div class="stat-label">本月申请</div>
              </div>
              <div class="stat-split"></div>
              <div class="stat-block">
                <div class="stat-num">{{ applyStats.pending }}</div>
                <div class="stat-label">待审批</div>
              </div>
              <div class="stat-split"></div>
              <div class="stat-block">
                <div class="stat-num">{{ applyStats.approved }}</div>
                <div class="stat-label">已通过</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 其他页面：标题 -->
      <div class="page-heading" v-else>
        <div class="heading-left">
          <div class="breadcrumb-mini" @click="navigateTo('/user/dashboard')">
            <el-icon><House /></el-icon>
          </div>
          <h2 class="page-title">{{ $route.meta.title || '工作台' }}</h2>
          <span class="page-divider">/</span>
          <span class="page-subtitle">员工服务</span>
        </div>
        <div class="heading-right">
          <span class="heading-tip">
            <el-icon :size="12"><Sunny /></el-icon>
            {{ currentDate }} · {{ currentWeekday }}
          </span>
        </div>
      </div>

      <div class="page-body">
        <router-view v-slot="{ Component }">
          <transition name="fade-up" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>

    <el-dialog v-model="profileDialogVisible" title="个人档案" width="580px" class="user-dialog">
      <div class="profile-container">
        <div class="profile-header">
          <div class="avatar-large">{{ avatarText }}</div>
          <div class="header-info">
            <h3>{{ employeeForm.name || profileForm.realName }}</h3>
            <p class="role-text">{{ profileForm.roleName }}</p>
          </div>
        </div>

        <el-divider />

        <div class="section">
          <h4 class="section-title">基本信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">员工编号</span>
              <span class="value readonly">{{ employeeForm.employeeCode || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">姓名</span>
              <span class="value readonly">{{ employeeForm.name || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别</span>
              <span class="value readonly">{{ employeeForm.gender || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">出生日期</span>
              <span class="value readonly">{{ employeeForm.birthDate || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">入职日期</span>
              <span class="value readonly">{{ employeeForm.hireDate || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">政治面貌</span>
              <span class="value readonly">{{ employeeForm.politicalAffiliation || '-' }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="section">
          <h4 class="section-title">工作信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">所属部门</span>
              <span class="value readonly">{{ employeeForm.departmentName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">职位</span>
              <span class="value readonly">{{ employeeForm.position || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">员工状态</span>
              <el-tag :type="employeeForm.employeeStatus === '在职' ? 'success' : 'warning'">
                {{ employeeForm.employeeStatus || '-' }}
              </el-tag>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="section">
          <h4 class="section-title">联系方式</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">联系电话</span>
              <template v-if="isEditMode">
                <el-input v-model="employeeForm.phone" class="edit-input" />
              </template>
              <span v-else class="value">{{ employeeForm.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱</span>
              <template v-if="isEditMode">
                <el-input v-model="employeeForm.email" class="edit-input" />
              </template>
              <span v-else class="value">{{ employeeForm.email || '-' }}</span>
            </div>
            <div class="info-item full-width">
              <span class="label">家庭住址</span>
              <template v-if="isEditMode">
                <el-input v-model="employeeForm.address" class="edit-input" />
              </template>
              <span v-else class="value">{{ employeeForm.address || '-' }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="section">
          <h4 class="section-title">其他信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">户口类型</span>
              <span class="value readonly">{{ employeeForm.householdType || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">健康状况</span>
              <span class="value readonly">{{ employeeForm.healthStatus || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">薪资</span>
              <span class="value readonly">{{ employeeForm.salary ? '¥' + employeeForm.salary : '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="profileDialogVisible = false" size="large">关闭</el-button>
        <template v-if="!isEditMode">
          <el-button type="primary" size="large" @click="isEditMode = true">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </template>
        <template v-else>
          <el-button size="large" @click="handleCancelEdit">取消</el-button>
          <el-button type="primary" size="large" @click="handleSaveEmployeeProfile">
            <el-icon><Check /></el-icon>
            保存
          </el-button>
        </template>
      </template>
    </el-dialog>

    <!-- 登录后通知弹窗 -->
    <el-dialog
      v-model="loginNotificationVisible"
      title="您有新消息"
      width="450px"
      class="notification-dialog"
      :close-on-click-modal="true"
    >
      <div class="login-notification-content" v-if="unreadNotifications.length > 0">
        <div class="notification-list-in-dialog">
          <div
            v-for="notification in unreadNotifications"
            :key="notification.id"
            class="notification-item-dialog"
          >
            <div class="notification-icon-dialog">
              <el-icon :size="20"><Bell /></el-icon>
            </div>
            <div class="notification-text">
              <div class="notification-title-text">{{ notification.title }}</div>
              <div class="notification-content-text">{{ notification.content }}</div>
              <div class="notification-time-text">{{ formatTime(notification.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-notification">
        <el-icon :size="40"><Bell /></el-icon>
        <p>暂无新通知</p>
      </div>
      <template #footer>
        <el-button @click="handleCloseLoginNotification" size="large">我知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Briefcase, HomeFilled, User, Clock, Calendar, TrendCharts, DataAnalysis, Medal,
  ArrowDown, Sunny, Bell, Refresh, House, QuestionFilled, Edit, Reading, MagicStick,
  SwitchButton, Check, Money, List, Lock
} from '@element-plus/icons-vue'
import { updateProfile, getEmployeeProfile, updateEmployeeProfile } from '@/api/auth'
import { getNotifications, getUnreadNotifications, markNotificationAsRead } from '@/api/notification'
import { getMyOvertimeList } from '@/api/overtime'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 鸡汤语录
const quotes = [
  '今天的努力，是幸运的伏笔；当下的付出，是明日的花开。',
  '不怕慢，就怕站。每天进步一点点，平凡也会变得不平凡。',
  '工作不是为了生存，而是为了把个人的生活赋予意义。',
  '每一次的坚持，都是离成功更近了一步。',
  '生活不会辜负每一个努力的人。',
  '你要悄悄拔尖，然后惊艳所有人。',
  '山再高，往上攀，总能登顶；路再长，走下去，定能到达。',
  '没有一种生活是可惜的，也没有一种生活是不值得的。',
  '把每一天当成礼物，认真去生活。',
  '不是每一次努力都会有收获，但每一次收获都必须努力。',
  '成长就是把哭声调成静音的过程。',
  '请相信，所有的努力都会开花结果。',
  '你比昨天的自己更好，就是最大的进步。',
  '人生没有白走的路，每一步都算数。',
  '先努力让自己发光，对的人才能迎着光而来。',
  '做一个温柔的人，浅浅笑，轻轻爱，稳稳走。',
  '无论做什么，记得为自己而做，那就毫无怨言。',
  '所谓奇迹，不过是努力的另一个名字。',
  '做一个积极向上的人，读温柔的句子，见阳光的人。',
  '不要因为走得太远，而忘记为什么出发。'
]

const quoteIndex = ref(0)
const currentQuote = computed(() => quotes[quoteIndex.value])

// 申请统计数据
const applyStats = reactive({
  monthlyTotal: 0,
  pending: 0,
  approved: 0
})

// 加载我的申请统计
const loadApplyStats = async () => {
  try {
    const currentMonth = dayjs().format('YYYY-MM')
    // 获取所有加班申请
    const res = await getMyOvertimeList({ pageNum: 1, pageSize: 100 })
    const allRecords = res.data?.records || []

    // 本月申请
    const monthlyRecords = allRecords.filter(item => {
      return item.overtimeDate && String(item.overtimeDate).startsWith(currentMonth)
    })

    // 待审批
    const pendingRecords = allRecords.filter(item => item.status === '待审批')

    // 已通过
    const approvedRecords = allRecords.filter(item => item.status === '通过')

    applyStats.monthlyTotal = monthlyRecords.length
    applyStats.pending = pendingRecords.length
    applyStats.approved = approvedRecords.length
  } catch (error) {
    console.error('加载申请统计失败:', error)
  }
}

const changeQuote = () => {
  let next = quoteIndex.value
  while (next === quoteIndex.value && quotes.length > 1) {
    next = Math.floor(Math.random() * quotes.length)
  }
  quoteIndex.value = next
}

onMounted(() => {
  quoteIndex.value = Math.floor(Math.random() * quotes.length)
  // 加载登录后的未读通知
  loadUnreadNotificationsOnLogin()
  // 加载申请统计
  loadApplyStats()
})

// 导航
const activePath = computed(() => route.path)

const handleMenuSelect = (index) => {
  router.push(index)
}

const navigateTo = (path) => {
  if (route.path !== path) {
    router.push(path)
  }
}

const refreshPage = () => {
  router.go(0)
}

const avatarText = computed(() => {
  const name = userStore.userInfo?.realName || userStore.userInfo?.username || '员'
  return name.charAt(0)
})

const currentDate = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const currentWeekday = computed(() => {
  const days = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return days[new Date().getDay()]
})

const greetingText = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了，注意休息'
  if (h < 9) return '早上好，开启美好的一天'
  if (h < 12) return '上午好，专注工作'
  if (h < 14) return '中午好，记得休息'
  if (h < 18) return '下午好，继续加油'
  if (h < 22) return '晚上好，辛苦了'
  return '夜深了，注意休息'
})

const greetingShort = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '早上好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const isUserDashboard = computed(() => {
  return route.path.startsWith('/user/dashboard') || route.path === '/user' || route.path === '/user/'
})

const notificationVisible = ref(false)
const notifications = ref([])
const loginNotificationVisible = ref(false)
const unreadNotifications = ref([])

const notificationCount = computed(() => {
  return notifications.value.filter(n => n.status === '未读').length
})

const toggleNotification = async () => {
  notificationVisible.value = !notificationVisible.value
  if (notificationVisible.value) {
    await loadNotifications()
  }
}

const loadNotifications = async () => {
  const employeeId = userStore.userInfo?.employeeId
  if (!employeeId) return
  
  try {
    const response = await getNotifications(employeeId)
    notifications.value = response.data || []
  } catch (error) {
    console.error('获取通知失败:', error)
  }
}

// 加载并显示登录后的未读通知
const loadUnreadNotificationsOnLogin = async () => {
  const employeeId = userStore.userInfo?.employeeId
  if (!employeeId) return
  
  try {
    const response = await getUnreadNotifications(employeeId)
    unreadNotifications.value = response.data || []
    // 如果有未读通知，自动弹出对话框
    if (unreadNotifications.value.length > 0) {
      setTimeout(() => {
        loginNotificationVisible.value = true
      }, 500) // 延迟500ms弹出，等待页面加载完成
    }
  } catch (error) {
    console.error('获取未读通知失败:', error)
  }
}

// 关闭登录通知弹窗
const handleCloseLoginNotification = async () => {
  // 将所有未读通知标记为已读
  try {
    for (const notification of unreadNotifications.value) {
      await markNotificationAsRead(notification.id)
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
  loginNotificationVisible.value = false
  // 刷新通知列表
  await loadNotifications()
}

const handleNotificationClick = async (notification) => {
  if (notification.status === '未读') {
    try {
      await markNotificationAsRead(notification.id)
      notification.status = '已读'
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 用户菜单
const profileDialogVisible = ref(false)
const isEditMode = ref(false)
const profileForm = reactive({
  username: '',
  realName: '',
  role: '',
  roleName: ''
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

const handleCommand = async (command) => {
  if (command === 'profile') {
    profileForm.username = userStore.userInfo?.username || ''
    profileForm.realName = userStore.userInfo?.realName || ''
    profileForm.role = userStore.userInfo?.role || ''
    profileForm.roleName = '普通员工'
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
  } else if (command === 'changePassword') {
    router.push('/user/change-password')
  } else if (command === 'help') {
    ElMessage({
      message: '如有问题请联系人事部 ☎ 8001',
      type: 'info',
      duration: 3000
    })
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
      email: employeeForm.email,
      address: employeeForm.address
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
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background: #f7f9fc;
}

/* ============ 顶部栏 ============ */
.user-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  border-bottom: 1px solid #eef1f6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-inner {
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 28px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.2s;
}

.brand:hover {
  transform: translateX(-2px);
}

.brand-logo {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  transition: transform 0.3s;
}

.brand:hover .brand-logo {
  transform: rotate(-8deg) scale(1.05);
}

.brand-title {
  font-size: 17px;
  font-weight: 700;
  color: #1a2235;
  line-height: 1.2;
  letter-spacing: -0.3px;
}

.brand-sub {
  font-size: 12px;
  color: #8a94a6;
  margin-top: 3px;
}

/* ============ 导航（顶部水平下拉菜单） ============ */
.user-nav {
  flex: 1;
  border: none !important;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  font-weight: 500;
}

.user-nav :deep(.el-menu-item),
.user-nav :deep(.el-sub-menu__title) {
  height: 50px !important;
  line-height: 50px !important;
  padding: 0 18px !important;
  margin: 0 2px;
  border-radius: 12px;
  display: flex !important;
  align-items: center;
  gap: 8px;
  transition: all 0.2s !important;
  border-bottom: none !important;
}

.user-nav :deep(.el-menu-item:hover),
.user-nav :deep(.el-sub-menu__title:hover) {
  background: #f3f5fc;
  color: #667eea;
}

.user-nav :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #fff !important;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.35);
  border-bottom: none !important;
}

.user-nav :deep(.el-menu-item .el-icon),
.user-nav :deep(.el-sub-menu__title .el-icon) {
  font-size: 16px;
}

.user-nav :deep(.el-sub-menu__icon-arrow) {
  font-size: 12px;
  margin-left: 4px;
  position: static !important;
  transform: none !important;
}

.user-nav :deep(.el-sub-menu.is-active .el-sub-menu__icon-arrow) {
  transform: rotate(180deg) !important;
}

.user-nav :deep(.el-menu-item.is-active .el-icon) {
  color: #fff;
}

.user-nav :deep(.el-popper) {
  border-radius: 16px;
  border: 1px solid #eef1f6;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12);
  padding: 8px;
  margin-top: 12px !important;
}

.user-nav :deep(.el-dropdown-menu__item),
.user-nav :deep(.el-menu--popup .el-menu-item) {
  height: 40px !important;
  line-height: 40px !important;
  padding: 0 18px !important;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #1a2235;
}

.user-nav :deep(.el-menu--popup .el-menu-item:hover) {
  background: #f3f5fc;
  color: #667eea;
}

.user-nav :deep(.el-menu--popup .el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

/* ============ 右侧操作区 ============ */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.action-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  border: none;
  background: #f5f7fb;
  color: #5a6478;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.action-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(102, 126, 234, 0.3);
}

.badge-btn .action-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #f56565;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
  animation: badge-pop 2s ease-in-out infinite;
}

@keyframes badge-pop {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.15); }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 14px 6px 6px;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.2s;
  background: #f5f7fb;
}

.user-profile:hover {
  background: #eef1f8;
  transform: translateX(2px);
}

.user-avatar-ring {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 3px;
  box-shadow: 0 4px 14px rgba(102, 126, 234, 0.3);
}

.user-avatar-inner {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #fff;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
}

.user-detail {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.user-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a2235;
}

.user-role {
  font-size: 11.5px;
  color: #8a94a6;
  margin-top: 3px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ============ 下拉菜单 ============ */
.user-dropdown {
  padding: 10px !important;
  min-width: 260px !important;
  border-radius: 16px !important;
}

.dropdown-header {
  padding: 14px 14px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 8px;
}

.dropdown-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
  backdrop-filter: blur(10px);
}

.dropdown-info {
  flex: 1;
  color: #fff;
}

.dropdown-name {
  font-size: 14px;
  font-weight: 700;
}

.dropdown-role {
  font-size: 11.5px;
  opacity: 0.88;
  margin-top: 3px;
}

.dropdown-item-custom {
  height: 40px !important;
  display: flex !important;
  align-items: center !important;
  gap: 10px !important;
  padding: 0 12px !important;
  border-radius: 10px !important;
  font-size: 14px !important;
  color: #1a2235 !important;
  font-weight: 500;
}

.dropdown-item-custom:hover {
  background: #f3f5fc !important;
  color: #667eea !important;
}

.dropdown-item-logout {
  height: 40px !important;
  display: flex !important;
  align-items: center !important;
  gap: 10px !important;
  padding: 0 12px !important;
  border-radius: 10px !important;
  font-size: 14px !important;
  color: #dc2626 !important;
  font-weight: 500;
}

.dropdown-item-logout:hover {
  background: #fef2f2 !important;
}

/* ============ 主内容区 ============ */
.user-main {
  max-width: 1500px;
  margin: 0 auto;
  padding: 28px;
}

/* ============ 首页欢迎卡片（风景背景） ============ */
.page-hero {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  padding: 44px 48px;
  margin-bottom: 28px;
  min-height: 220px;
  box-shadow: 0 10px 40px rgba(30, 50, 90, 0.15);
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-image: url('https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1600&q=80');
  background-size: cover;
  background-position: center;
  z-index: 0;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg,
    rgba(30, 50, 90, 0.6) 0%,
    rgba(60, 80, 120, 0.45) 40%,
    rgba(90, 120, 160, 0.3) 100%);
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  gap: 60px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  background: rgba(255, 255, 255, 0.22);
  border-radius: 24px;
  font-size: 13px;
  font-weight: 500;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  font-size: 34px;
  font-weight: 700;
  margin: 16px 0 10px;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.hero-title span {
  font-weight: 300;
  opacity: 0.92;
}

.hero-quote {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  max-width: 520px;
  background: rgba(255, 255, 255, 0.15);
  padding: 14px 18px;
  border-radius: 14px;
  backdrop-filter: blur(10px);
  border-left: 3px solid #ffd93d;
  margin-bottom: 22px;
}

.hero-quote p {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  opacity: 0.95;
  font-weight: 400;
}

.hero-quote .el-icon {
  font-size: 18px;
  margin-top: 4px;
  opacity: 0.9;
  flex-shrink: 0;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-btn-primary,
.hero-btn-outline,
.hero-btn-light {
  height: 44px;
  padding: 0 22px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.25s;
}

.hero-btn-primary {
  background: #fff;
  color: #334155;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.hero-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.22);
}

.hero-btn-outline {
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  border: 1.5px solid rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(10px);
}

.hero-btn-outline:hover {
  background: rgba(255, 255, 255, 0.28);
  transform: translateY(-2px);
}

.hero-btn-light {
  background: rgba(255, 217, 61, 0.22);
  color: #fff;
  border: 1.5px solid rgba(255, 217, 61, 0.5);
}

.hero-btn-light:hover {
  background: rgba(255, 217, 61, 0.35);
  transform: translateY(-2px);
}

/* 右侧日期卡片 */
.hero-right {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 200px;
}

.date-card {
  background: rgba(255, 255, 255, 0.22);
  backdrop-filter: blur(15px);
  padding: 20px 28px;
  border-radius: 20px;
  text-align: center;
  border: 1.5px solid rgba(255, 255, 255, 0.35);
}

.date-day {
  font-size: 42px;
  font-weight: 700;
  line-height: 1;
}

.date-month {
  font-size: 13px;
  opacity: 0.9;
  margin-top: 6px;
  letter-spacing: 1px;
}

.date-week {
  font-size: 13px;
  opacity: 0.85;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.25);
}

.date-full {
  font-size: 11px;
  opacity: 0.7;
  margin-top: 3px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.22);
  backdrop-filter: blur(15px);
  padding: 14px 16px;
  border-radius: 20px;
  border: 1.5px solid rgba(255, 255, 255, 0.35);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-block {
  text-align: center;
  flex: 1;
}

.stat-num {
  font-size: 22px;
  font-weight: 700;
}

.stat-label {
  font-size: 11px;
  opacity: 0.85;
  margin-top: 2px;
}

.stat-split {
  width: 1px;
  height: 30px;
  background: rgba(255, 255, 255, 0.3);
}

/* ============ 页面标题 ============ */
.page-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
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

/* ============ 页面主体 ============ */
.page-body {
  position: relative;
}

.fade-up-enter-active,
.fade-up-leave-active {
  transition: all 0.3s ease;
}

.fade-up-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.fade-up-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}

.profile-form {
  padding-top: 10px;
}

.profile-container {
  padding: 10px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 20px;
}

.avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.header-info h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.role-text {
  margin: 5px 0 0 0;
  font-size: 14px;
  color: #8a94a6;
}

.section {
  margin-bottom: 10px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: span 2;
}

.info-item .label {
  font-size: 13px;
  color: #8a94a6;
}

.info-item .value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.info-item .value.readonly {
  color: #8a94a6;
  font-weight: normal;
}

.edit-input {
  width: 100%;
}

.user-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.user-dialog :deep(.el-divider) {
  margin: 16px 0;
}

/* 响应式 */
@media (max-width: 1100px) {
  .hero-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 28px;
  }
  .hero-right {
    flex-direction: row;
    width: 100%;
  }
  .date-card, .stat-card {
    flex: 1;
  }
}

/* 通知面板样式 */
.notification-popover {
  border-radius: 16px !important;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15) !important;
  border: 1px solid #eef1f6 !important;
  padding: 0 !important;
  overflow: hidden;
}

.notification-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.notification-title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
}

.notification-count {
  font-size: 12px;
  opacity: 0.85;
  background: rgba(255, 255, 255, 0.2);
  padding: 3px 10px;
  border-radius: 10px;
}

.notification-divider {
  margin: 0 !important;
  background: #f0f2f5;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f5f7fa;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #f8f9fc;
}

.notification-item.is-unread {
  background: #fff9f9;
}

.notification-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f0f5ff;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-content .notification-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a2235;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-desc {
  font-size: 13px;
  color: #8a94a6;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 6px;
}

.notification-time {
  font-size: 11.5px;
  color: #b0b8c4;
}

.notification-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f56565;
  flex-shrink: 0;
  margin-top: 8px;
}

.notification-empty {
  padding: 40px 20px;
  text-align: center;
  color: #8a94a6;
}

.notification-empty .empty-icon {
  color: #c9d2e0;
  margin-bottom: 10px;
}

.notification-empty p {
  margin: 0;
  font-size: 14px;
}

/* 登录通知弹窗样式 */
.notification-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 20px 24px;
  margin: 0;
}

.notification-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.notification-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
}

.notification-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.login-notification-content {
  max-height: 400px;
  overflow-y: auto;
}

.notification-list-in-dialog {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item-dialog {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 14px;
  background: #f8f9fc;
  border-radius: 12px;
  border: 1px solid #eef1f6;
  transition: all 0.2s;
}

.notification-item-dialog:hover {
  background: #f3f5fc;
  border-color: #667eea;
}

.notification-icon-dialog {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-text {
  flex: 1;
  min-width: 0;
}

.notification-title-text {
  font-size: 15px;
  font-weight: 600;
  color: #1a2235;
  margin-bottom: 6px;
}

.notification-content-text {
  font-size: 13px;
  color: #5a6478;
  line-height: 1.5;
  margin-bottom: 6px;
}

.notification-time-text {
  font-size: 12px;
  color: #8a94a6;
}

.no-notification {
  text-align: center;
  padding: 40px 20px;
  color: #8a94a6;
}

.no-notification p {
  margin: 12px 0 0 0;
  font-size: 14px;
}
</style>
