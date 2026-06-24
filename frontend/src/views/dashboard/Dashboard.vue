<template>
  <div class="dashboard">
    <!-- 管理员视角 -->
    <template v-if="isAdmin">
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.employeeTotal }}</div>
              <div class="stat-label">员工总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon :size="32"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.departmentTotal }}</div>
              <div class="stat-label">部门数量</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.attendanceNormal }}</div>
              <div class="stat-label">今日考勤正常</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.contractExpiring }}</div>
              <div class="stat-label">合同即将到期</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="content-row">
        <el-col :span="16">
          <div class="panel admin-fixed-panel">
            <div class="panel-header">
              <h3>最新人事变动</h3>
              <el-button type="primary" link @click="router.push(prefix + '/personnel-change')">查看全部</el-button>
            </div>
            <div class="table-scroll-wrapper">
              <el-table :data="recentChanges" style="width: 100%" height="100%">
                <el-table-column prop="employeeName" label="员工" width="120" />
                <el-table-column prop="changeType" label="变动类型" width="100" />
                <el-table-column prop="originalValue" label="原值" />
                <el-table-column prop="newValue" label="新值" />
                <el-table-column prop="changeDate" label="变动日期" width="120" />
              </el-table>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="panel">
            <div class="panel-header">
              <h3>快捷操作</h3>
            </div>
            <div class="quick-actions">
              <div class="action-item" @click="router.push(prefix + '/personnel')">
                <div class="action-icon" style="background: #ecf5ff; color: #409eff;">
                  <el-icon :size="22"><User /></el-icon>
                </div>
                <span class="action-label">人员管理</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/department')">
                <div class="action-icon" style="background: #f0f9eb; color: #67c23a;">
                  <el-icon :size="22"><OfficeBuilding /></el-icon>
                </div>
                <span class="action-label">部门管理</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/contract')">
                <div class="action-icon" style="background: #fdf6ec; color: #e6a23c;">
                  <el-icon :size="22"><Document /></el-icon>
                </div>
                <span class="action-label">合同管理</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/attendance')">
                <div class="action-icon" style="background: #fef0f0; color: #f56c6c;">
                  <el-icon :size="22"><Clock /></el-icon>
                </div>
                <span class="action-label">考勤管理</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/overtime')">
                <div class="action-icon" style="background: #f4f4f5; color: #909399;">
                  <el-icon :size="22"><Timer /></el-icon>
                </div>
                <span class="action-label">加班管理</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/training')">
                <div class="action-icon" style="background: #ecf5ff; color: #409eff;">
                  <el-icon :size="22"><Reading /></el-icon>
                </div>
                <span class="action-label">培训管理</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="content-row">
        <el-col :span="12">
          <div class="panel admin-fixed-panel">
            <div class="panel-header">
              <h3>合同到期提醒</h3>
              <el-button type="primary" link @click="router.push(prefix + '/contract')">查看全部</el-button>
            </div>
            <div class="table-scroll-wrapper">
              <el-table :data="expiringContracts" style="width: 100%" height="100%">
                <el-table-column prop="employeeName" label="员工" />
                <el-table-column prop="contractType" label="合同类型" />
                <el-table-column prop="endDate" label="到期日期" />
              </el-table>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="panel admin-fixed-panel">
            <div class="panel-header">
              <h3>待审批事项</h3>
              <el-button type="primary" link @click="router.push(prefix + '/overtime')">查看全部</el-button>
            </div>
            <div class="table-scroll-wrapper">
              <el-table :data="pendingApprovals" style="width: 100%" height="100%">
                <el-table-column prop="type" label="类型" width="80" />
                <el-table-column prop="employeeName" label="员工" width="100" />
                <el-table-column prop="reason" label="原因" />
                <el-table-column label="操作" width="140" fixed="right">
                  <template #default="{ row }">
                    <div class="action-btns">
                      <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
                      <el-button type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-col>
      </el-row>
    </template>

    <!-- 员工视角 -->
    <template v-else>
      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ userStats.monthlyOvertime }}</div>
              <div class="stat-label">本月加班时长(小时)</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ userStats.trainingCount }}</div>
              <div class="stat-label">已报名培训</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card">
            <div class="stat-icon" :style="{ background: todayStatusColor }">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ todayStatusText }}</div>
              <div class="stat-label">今日考勤</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="content-row">
        <el-col :span="24">
          <div class="panel">
            <div class="panel-header">
              <h3>快捷操作</h3>
            </div>
            <div class="quick-actions">
              <div class="action-item" @click="handleQuickCheckIn" :class="{ disabled: hasCheckedInToday && !hasCheckedOutToday }">
                <div class="action-icon" :style="hasCheckedInToday && !hasCheckedOutToday ? 'background: #f4f4f5; color: #c0c4cc;' : 'background: #ecf5ff; color: #409eff;'">
                  <el-icon :size="22"><Top /></el-icon>
                </div>
                <span class="action-label">签到</span>
              </div>
              <div class="action-item" @click="handleQuickCheckOut" :class="{ disabled: !hasCheckedInToday || hasCheckedOutToday }">
                <div class="action-icon" :style="!hasCheckedInToday || hasCheckedOutToday ? 'background: #f4f4f5; color: #c0c4cc;' : 'background: #f0f9eb; color: #67c23a;'">
                  <el-icon :size="22"><Bottom /></el-icon>
                </div>
                <span class="action-label">签退</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/attendance')">
                <div class="action-icon" style="background: #fdf6ec; color: #e6a23c;">
                  <el-icon :size="22"><Calendar /></el-icon>
                </div>
                <span class="action-label">我的考勤</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/overtime')">
                <div class="action-icon" style="background: #fef0f0; color: #f56c6c;">
                  <el-icon :size="22"><Timer /></el-icon>
                </div>
                <span class="action-label">加班申请</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/training')">
                <div class="action-icon" style="background: #f4f4f5; color: #909399;">
                  <el-icon :size="22"><Reading /></el-icon>
                </div>
                <span class="action-label">培训课程</span>
              </div>
              <div class="action-item" @click="router.push(prefix + '/salary')">
                <div class="action-icon" style="background: #ecf5ff; color: #409eff;">
                  <el-icon :size="22"><Money /></el-icon>
                </div>
                <span class="action-label">工资查询</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="content-row">
        <el-col :span="12">
          <div class="panel fixed-height-panel">
            <div class="panel-header">
              <h3>我的合同</h3>
            </div>
            <div class="table-scroll-wrapper">
              <el-table :data="myContracts" style="width: 100%" v-if="myContracts.length > 0" height="100%">
                <el-table-column prop="contractType" label="合同类型" />
                <el-table-column prop="startDate" label="开始日期" />
                <el-table-column prop="endDate" label="到期日期" />
                <el-table-column prop="status" label="状态">
                  <template #default="{ row }">
                    <el-tag :type="row.isExpiring ? 'warning' : 'success'" size="small">
                      {{ row.isExpiring ? '即将到期' : '正常' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无合同信息" :image-size="60" />
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="panel fixed-height-panel">
            <div class="panel-header">
              <h3>我的申请记录</h3>
            </div>
            <div class="table-scroll-wrapper">
              <el-table :data="myApplyItems" style="width: 100%" v-if="myApplyItems.length > 0" height="100%" :row-class-name="getApplyRowClassName">
                <el-table-column prop="type" label="类型" width="100" />
                <el-table-column prop="reason" label="原因/内容">
                  <template #default="{ row }">
                    <span>{{ row.reason }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="申请时间" width="110" />
              </el-table>
              <el-empty v-else description="暂无申请记录" :image-size="60" />
            </div>
          </div>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEmployeeList } from '@/api/employee'
import { getAllDepartments } from '@/api/department'
import { getExpiringContracts } from '@/api/contract'
import { getMyAttendance } from '@/api/attendance'
import { checkIn, checkOut } from '@/api/attendance'
import { getMyContracts } from '@/api/contract'
import { getOvertimeList, approveOvertime, getMyOvertimeList } from '@/api/overtime'
import { getPersonnelChangeList } from '@/api/personnelChange'
import { getMyTrainingRecords } from '@/api/training'
import { getAttendanceStatistics } from '@/api/attendance'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin || userStore.isHR)
const prefix = computed(() => isAdmin.value ? '/admin' : '/user')

// 管理员统计数据
const stats = ref({
  employeeTotal: 0,
  departmentTotal: 0,
  attendanceNormal: 0,
  contractExpiring: 0
})

const recentChanges = ref([])
const expiringContracts = ref([])
const pendingApprovals = ref([])

// 员工统计数据
const userStats = ref({
  monthlyOvertime: 0,
  trainingCount: 0
})

// 员工合同和申请记录
const myContracts = ref([])
const myApplyItems = ref([])

// 状态类型映射
const getStatusType = (status) => {
  if (status === '通过' || status === '已通过') return 'success'
  if (status === '拒绝' || status === '已拒绝') return 'danger'
  return 'warning'
}

// 申请记录行样式
const getApplyRowClassName = ({ row }) => {
  if (row.status === '通过' || row.status === '已通过' || row.status === '拒绝' || row.status === '已拒绝') {
    return 'row-strikethrough'
  }
  return ''
}

// 当天签到签退状态
const hasCheckedInToday = ref(false)
const hasCheckedOutToday = ref(false)

const todayStatusText = computed(() => {
  if (!hasCheckedInToday.value) return '未签到'
  if (hasCheckedOutToday.value) return '已签退'
  return '已签到'
})

const todayStatusColor = computed(() => {
  if (!hasCheckedInToday.value) return '#909399'
  if (hasCheckedOutToday.value) return '#67c23a'
  return '#409eff'
})

// 获取当天考勤状态
const fetchTodayStatus = async () => {
  if (isAdmin.value) return
  try {
    const res = await getMyAttendance({
      pageNum: 1,
      pageSize: 50,
      startDate: dayjs().format('YYYY-MM-DD'),
      endDate: dayjs().format('YYYY-MM-DD')
    })
    const records = res.data?.records || []
    hasCheckedInToday.value = records.some(record => record.checkInTime)
    hasCheckedOutToday.value = records.some(record => record.checkOutTime)
  } catch (error) {
    console.error('获取考勤状态失败:', error)
  }
}

// 获取员工本月加班时长
const fetchMyOvertime = async () => {
  if (isAdmin.value) return
  try {
    const currentMonth = dayjs().format('YYYY-MM')
    const res = await getMyOvertimeList({ pageNum: 1, pageSize: 100 })
    const allOvertime = res.data?.records || []
    // 筛选当月已通过的加班
    const monthlyOvertime = allOvertime
      .filter(item => item.status === '通过' && item.overtimeDate && String(item.overtimeDate).startsWith(currentMonth))
      .reduce((sum, item) => sum + (item.hours || 0), 0)
    userStats.value.monthlyOvertime = monthlyOvertime
  } catch (error) {
    console.error('获取加班数据失败:', error)
  }
}

// 获取员工已报名的培训数量
const fetchMyTraining = async () => {
  if (isAdmin.value) return
  try {
    const res = await getMyTrainingRecords()
    const records = res.data || []
    userStats.value.trainingCount = records.length
  } catch (error) {
    console.error('获取培训记录失败:', error)
  }
}

// 获取员工自己的合同
const fetchMyContracts = async () => {
  if (isAdmin.value) return
  try {
    const res = await getMyContracts()
    const contracts = res.data || []
    // 计算是否即将到期（30天内）
    myContracts.value = contracts.map(c => ({
      ...c,
      isExpiring: c.endDate && dayjs(c.endDate).diff(dayjs(), 'day') <= 30
    })).slice(0, 5)
  } catch (error) {
    console.error('获取合同失败:', error)
  }
}

// 获取员工的申请记录
const fetchMyApplyItems = async () => {
  if (isAdmin.value) return
  try {
    const items = []

    // 获取加班申请
    try {
      const overtimeRes = await getMyOvertimeList({ pageNum: 1, pageSize: 100 })
      const allOvertime = overtimeRes.data?.records || []
      allOvertime.forEach(item => {
        items.push({
          type: '加班',
          reason: item.reason || '加班申请',
          status: item.status || '待审批',
          createTime: item.createTime ? dayjs(item.createTime).format('YYYY-MM-DD') : '-'
        })
      })
    } catch (e) {}

    // 按时间倒序
    items.sort((a, b) => b.createTime.localeCompare(a.createTime))
    myApplyItems.value = items.slice(0, 10)
  } catch (error) {
    console.error('获取申请记录失败:', error)
  }
}

// 快捷签到
const handleQuickCheckIn = async () => {
  const employeeId = userStore.userInfo?.employeeId
  if (!employeeId) {
    ElMessage.error('未找到员工信息')
    return
  }
  try {
    await checkIn({ employeeId, checkInTime: dayjs().format('HH:mm:ss') })
    ElMessage.success('签到成功')
    hasCheckedInToday.value = true
    hasCheckedOutToday.value = false
  } catch (error) {
    ElMessage.error('签到失败')
    console.error(error)
  }
}

// 快捷签退
const handleQuickCheckOut = async () => {
  const employeeId = userStore.userInfo?.employeeId
  if (!employeeId) {
    ElMessage.error('未找到员工信息')
    return
  }
  try {
    await checkOut({ employeeId, checkOutTime: dayjs().format('HH:mm:ss') })
    ElMessage.success('签退成功')
    hasCheckedOutToday.value = true
  } catch (error) {
    ElMessage.error('签退失败')
    console.error(error)
  }
}

const fetchPendingApprovals = async () => {
  if (!isAdmin.value) return
  try {
    const overtimeRes = await getOvertimeList({
      pageNum: 1,
      pageSize: 10,
      status: '待审批'
    })
    const overtimeList = overtimeRes.data?.data?.records || overtimeRes.data?.records || []
    const items = overtimeList.map(item => ({
      id: item.id,
      type: '加班',
      employeeId: item.employeeId,
      employeeName: item.employeeName || item.employeeId,
      reason: item.reason || '加班申请',
      category: 'overtime'
    }))
    pendingApprovals.value = items.slice(0, 5)
  } catch (error) {
    console.error('获取待审批事项失败:', error)
  }
}

const fetchRecentChanges = async () => {
  if (!isAdmin.value) return
  try {
    const res = await getPersonnelChangeList({
      pageNum: 1,
      pageSize: 10
    })
    const list = res.data?.data?.records || res.data?.records || []
    recentChanges.value = list.slice(0, 5).map(item => ({
      ...item,
      employeeName: item.employeeName || item.employeeId
    }))
  } catch (error) {
    console.error('获取人事变动失败:', error)
  }
}

const handleApprove = async (row) => {
  try {
    if (row.category === 'overtime') {
      await approveOvertime(row.id, { status: '通过', approveRemark: '同意' })
    }
    ElMessage.success('审批通过')
    await fetchPendingApprovals()
  } catch (error) {
    ElMessage.error('审批失败')
    console.error(error)
  }
}

const handleReject = async (row) => {
  try {
    if (row.category === 'overtime') {
      await approveOvertime(row.id, { status: '驳回', approveRemark: '拒绝' })
    }
    ElMessage.success('已拒绝')
    await fetchPendingApprovals()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  }
}

onMounted(async () => {
  if (isAdmin.value) {
    // 管理员视角
    try {
      const [empRes, deptRes, contractRes, attendanceStatRes] = await Promise.all([
        getEmployeeList({ pageNum: 1, pageSize: 1 }),
        getAllDepartments(),
        getExpiringContracts(),
        getAttendanceStatistics({
          startDate: dayjs().format('YYYY-MM-DD'),
          endDate: dayjs().format('YYYY-MM-DD')
        })
      ])

      stats.value.employeeTotal = empRes.data?.total || empRes.data?.data?.total || 0
      stats.value.departmentTotal = deptRes.data?.length || deptRes.data?.data?.length || 0
      stats.value.contractExpiring = contractRes.data?.length || contractRes.data?.data?.length || 0
      stats.value.attendanceNormal = attendanceStatRes.data?.checkedInCount || attendanceStatRes.data?.normal || 0

      expiringContracts.value = (contractRes.data?.data || contractRes.data || []).slice(0, 5)
      
      await Promise.all([
        fetchPendingApprovals(),
        fetchRecentChanges()
      ])
    } catch (error) {
      console.error(error)
    }
  } else {
    // 员工视角
    await Promise.all([
      fetchTodayStatus(),
      fetchMyOvertime(),
      fetchMyTraining(),
      fetchMyContracts(),
      fetchMyApplyItems()
    ])
  }
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.content-row {
  margin-bottom: 20px;
}

.panel {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.panel-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.fixed-height-panel {
  height: 320px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.fixed-height-panel .panel-header {
  flex-shrink: 0;
}

.admin-fixed-panel {
  height: 300px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-fixed-panel .panel-header {
  flex-shrink: 0;
}

.table-scroll-wrapper {
  flex: 1;
  overflow: hidden;
}

.action-btns {
  display: flex;
  gap: 6px;
  align-items: center;
  white-space: nowrap;
}

:deep(.row-strikethrough .cell) {
  text-decoration: line-through;
  color: #909399;
}

:deep(.row-strikethrough .el-tag) {
  text-decoration: line-through;
  opacity: 0.7;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: transparent;
}

.action-item:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}

.action-item:active {
  transform: translateY(0);
}

.action-item.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.action-item.disabled:hover {
  background: transparent;
  transform: none;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  transition: all 0.2s ease;
}

.action-item:hover .action-icon {
  transform: scale(1.05);
}

.action-item.disabled:hover .action-icon {
  transform: none;
}

.action-label {
  font-size: 13px;
  color: #606266;
  font-weight: 400;
}
</style>
