<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">考勤管理</h2>
      <div>
        <template v-if="!userStore.isAdmin && !userStore.isHR">
          <el-button type="primary" @click="handleCheckIn" :disabled="hasCheckedIn && !hasCheckedOut">签到</el-button>
          <el-button type="success" @click="handleCheckOut" :disabled="!hasCheckedIn || hasCheckedOut">签退</el-button>
        </template>
        <template v-else>
          <el-button type="success" @click="handleExport">导出</el-button>
        </template>
      </div>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="日期">
          <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item label="状态" v-if="userStore.isAdmin || userStore.isHR">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="正常" value="正常" />
            <el-option label="迟到" value="迟到" />
            <el-option label="早退" value="早退" />
            <el-option label="缺勤" value="缺勤" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="employeeId" label="员工" width="120" :formatter="formatEmployee" v-if="userStore.isAdmin || userStore.isHR" />
        <el-table-column prop="attendanceDate" label="考勤日期" width="120" />
        <el-table-column prop="checkInTime" label="上班打卡" width="120" />
        <el-table-column prop="checkOutTime" label="下班打卡" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAttendanceList, getMyAttendance, exportAttendance, checkIn, checkOut } from '@/api/attendance'
import { getEmployeeList } from '@/api/employee'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()

const loading = ref(false)
const employeeList = ref([])

// 当天签到签退状态
const hasCheckedIn = ref(false)
const hasCheckedOut = ref(false)

const searchForm = reactive({
  dateRange: [],
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const formatEmployee = (row) => {
  const emp = employeeList.value.find(e => e.id === row.employeeId)
  return emp ? emp.name : '-'
}

const getStatusType = (status) => {
  const types = { '正常': 'success', '迟到': 'warning', '早退': 'warning', '缺勤': 'danger' }
  return types[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = dayjs(searchForm.dateRange[0]).format('YYYY-MM-DD')
      params.endDate = dayjs(searchForm.dateRange[1]).format('YYYY-MM-DD')
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }

    let res
    if (userStore.isAdmin || userStore.isHR) {
      res = await getAttendanceList(params)
    } else {
      res = await getMyAttendance(params)
    }
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 单独获取当天考勤状态（员工端）
const fetchTodayStatus = async () => {
  if (userStore.isAdmin || userStore.isHR) return
  try {
    const res = await getMyAttendance({
      pageNum: 1,
      pageSize: 50,
      startDate: dayjs().format('YYYY-MM-DD'),
      endDate: dayjs().format('YYYY-MM-DD')
    })
    const todayRecords = res.data?.records || []
    hasCheckedIn.value = todayRecords.some(record => record.checkInTime)
    hasCheckedOut.value = todayRecords.some(record => record.checkOutTime)
  } catch (error) {
    console.error('获取当天考勤状态失败:', error)
  }
}

const loadEmployees = async () => {
  try {
    const res = await getEmployeeList({ pageNum: 1, pageSize: 100 })
    employeeList.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.dateRange = []
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

const handleCheckIn = async () => {
  const employeeId = userStore.userInfo?.employeeId
  if (!employeeId) {
    ElMessage.error('未找到员工信息，请先完善个人信息')
    return
  }
  try {
    await checkIn({ employeeId: employeeId, checkInTime: dayjs().format('HH:mm:ss') })
    ElMessage.success('签到成功')
    hasCheckedIn.value = true
    hasCheckedOut.value = false
    loadData()
  } catch (error) {
    ElMessage.error('签到失败')
    console.error(error)
  }
}

const handleCheckOut = async () => {
  const employeeId = userStore.userInfo?.employeeId
  if (!employeeId) {
    ElMessage.error('未找到员工信息，请先完善个人信息')
    return
  }
  try {
    await checkOut({
      employeeId: employeeId,
      checkOutTime: dayjs().format('HH:mm:ss')
    })
    ElMessage.success('签退成功')
    hasCheckedOut.value = true
    loadData()
  } catch (error) {
    ElMessage.error('签退失败')
    console.error(error)
  }
}

const handleExport = async () => {
  try {
    const params = {}
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = dayjs(searchForm.dateRange[0]).format('YYYY-MM-DD')
      params.endDate = dayjs(searchForm.dateRange[1]).format('YYYY-MM-DD')
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }

    const res = await exportAttendance(params)
    const list = res.data

    // 创建CSV内容
    const headers = ['员工', '考勤日期', '上班打卡', '下班打卡', '状态', '备注']
    const csvContent = [headers.join(',')]
    for (const item of list) {
      const emp = employeeList.value.find(e => e.id === item.employeeId)
      const row = [
        emp ? emp.name : item.employeeId,
        item.attendanceDate,
        item.checkInTime || '',
        item.checkOutTime || '',
        item.status,
        item.remark || ''
      ]
      csvContent.push(row.map(v => `"${v}"`).join(','))
    }

    // 下载文件
    const blob = new Blob(['\ufeff' + csvContent.join('\n')], { type: 'text/csv;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `考勤记录_${dayjs().format('YYYY-MM-DD')}.csv`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
    console.error(error)
  }
}

onMounted(() => {
  loadData()
  if (userStore.isAdmin || userStore.isHR) {
    loadEmployees()
  } else {
    fetchTodayStatus()
  }
})
</script>
