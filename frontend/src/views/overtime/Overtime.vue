<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">加班管理</h2>
      <div>
        <el-button v-if="!userStore.isAdmin && !userStore.isHR" type="primary" @click="openApplyDialog">申请</el-button>
        <el-button v-if="userStore.isAdmin || userStore.isHR" type="success" @click="handleExport">
          <el-icon><Download /></el-icon> 导出
        </el-button>
      </div>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="待审批" />
            <el-option label="通过" value="通过" />
            <el-option label="驳回" value="驳回" />
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
        <el-table-column prop="overtimeDate" label="加班日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="hours" label="加班时长" width="100" />
        <el-table-column prop="reason" label="加班原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status || '待审批' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveRemark" label="审批意见" v-if="userStore.isAdmin || userStore.isHR" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <template v-if="userStore.isAdmin || userStore.isHR">
              <el-button type="success" size="small" v-if="!row.status || row.status === '待审批'" @click="handleApprove(row)">审批</el-button>
            </template>
          </template>
        </el-table-column>
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

    <el-dialog v-model="applyDialogVisible" :title="userStore.isAdmin || userStore.isHR ? '新增加班' : '申请加班'" width="500px">
      <el-form ref="applyFormRef" :model="applyForm" label-width="100px" :rules="applyRules">
        <el-form-item label="加班日期" prop="overtimeDate">
          <el-date-picker v-model="applyForm.overtimeDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="applyForm.startTime" placeholder="选择时间" format="HH:mm:ss" value-format="HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker v-model="applyForm.endTime" placeholder="选择时间" format="HH:mm:ss" value-format="HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="加班时长(小时)" prop="hours">
          <el-input-number v-model="applyForm.hours" :min="0" :step="0.5" style="width: 100%" />
        </el-form-item>
        <el-form-item label="加班原因" prop="reason">
          <el-input v-model="applyForm.reason" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApplySubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="approveDialogVisible" title="审批" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="审批结果">
          <el-select v-model="approveForm.status" placeholder="请选择" style="width: 100%">
            <el-option label="通过" value="通过" />
            <el-option label="驳回" value="驳回" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.approveRemark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApproveSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getOvertimeList, getMyOvertimeList, createOvertime, approveOvertime } from '@/api/overtime'
import { Download } from '@element-plus/icons-vue'
import { getEmployeeList } from '@/api/employee'

const userStore = useUserStore()
const loading = ref(false)
const employeeList = ref([])

const searchForm = reactive({
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const applyDialogVisible = ref(false)
const applyFormRef = ref()
const applyForm = reactive({
  overtimeDate: '',
  startTime: '',
  endTime: '',
  hours: 0,
  reason: ''
})

const applyRules = {
  overtimeDate: [{ required: true, message: '请选择加班日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  hours: [{ required: true, message: '请输入加班时长', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入加班原因', trigger: 'blur' }]
}

const approveDialogVisible = ref(false)
const approveForm = reactive({
  id: null,
  status: '',
  approveRemark: ''
})

const formatEmployee = (row) => {
  const emp = employeeList.value.find(e => e.id === row.employeeId)
  return emp ? emp.name : '-'
}

const getStatusType = (status) => {
  if (!status) return 'warning'
  const types = { '待审批': 'warning', '通过': 'success', '驳回': 'danger' }
  return types[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }

    let res
    if (userStore.isAdmin || userStore.isHR) {
      res = await getOvertimeList(params)
    } else {
      res = await getMyOvertimeList(params)
    }
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
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
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

const openApplyDialog = () => {
  applyForm.overtimeDate = ''
  applyForm.startTime = ''
  applyForm.endTime = ''
  applyForm.hours = 0
  applyForm.reason = ''
  applyDialogVisible.value = true
}

const handleApplySubmit = async () => {
  try {
    const data = { ...applyForm }
    if (!userStore.isAdmin && !userStore.isHR) {
      const empId = userStore.userInfo?.employeeId
      if (!empId) {
        ElMessage.error('未找到员工信息，请先在个人信息中关联员工账号')
        return
      }
      data.employeeId = empId
    }
    await createOvertime(data)
    ElMessage.success('提交成功')
    applyDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const handleApprove = (row) => {
  approveForm.id = row.id
  approveForm.status = ''
  approveForm.approveRemark = ''
  approveDialogVisible.value = true
}

const handleApproveSubmit = async () => {
  if (!approveForm.status) {
    ElMessage.warning('请选择审批结果')
    return
  }
  try {
    await approveOvertime(approveForm.id, {
      status: approveForm.status,
      approveRemark: approveForm.approveRemark
    })
    ElMessage.success('审批成功')
    approveDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('审批失败')
  }
}

const handleExport = () => {
  const headers = ['员工姓名', '加班日期', '开始时间', '结束时间', '加班时长', '加班原因', '状态', '审批意见']
  const rows = tableData.value.map(row => [
    formatEmployee(row),
    row.overtimeDate,
    row.startTime,
    row.endTime,
    row.hours,
    row.reason,
    row.status || '待审批',
    row.approveRemark || ''
  ])

  const csvContent = [headers.join(','), ...rows.map(r => r.map(v => `"${v}"`).join(','))].join('\n')
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `加班记录_${new Date().toLocaleDateString()}.csv`
  link.click()
  ElMessage.success('导出成功')
}

onMounted(() => {
  loadData()
  if (userStore.isAdmin || userStore.isHR) {
    loadEmployees()
  }
})
</script>
