<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">奖惩管理</h2>
      <div>
        <el-button type="primary" @click="openApplyDialog">
          {{ userStore.isAdmin || userStore.isHR ? '新增' : '申请' }}
        </el-button>
      </div>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable>
            <el-option label="奖励" value="奖励" />
            <el-option label="惩罚" value="惩罚" />
          </el-select>
        </el-form-item>
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
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="rpDate" label="日期" width="120" />
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="amount" label="金额" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status || '待审批' }}</el-tag>
          </template>
        </el-table-column>
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

    <el-dialog v-model="applyDialogVisible" :title="userStore.isAdmin || userStore.isHR ? '新增奖惩' : '申请奖惩'" width="500px">
      <el-form ref="applyFormRef" :model="applyForm" label-width="100px" :rules="applyRules">
        <el-form-item label="类型" prop="type">
          <el-select v-model="applyForm.type" placeholder="请选择" style="width: 100%">
            <el-option label="奖励" value="奖励" />
            <el-option label="惩罚" value="惩罚" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="rpDate">
          <el-date-picker v-model="applyForm.rpDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="applyForm.reason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="applyForm.amount" :min="0" style="width: 100%" />
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
import { getRewardPunishmentList, getMyRewardPunishmentList, createRewardPunishment, approveRewardPunishment } from '@/api/rewardPunishment'
import { getEmployeeList } from '@/api/employee'

const userStore = useUserStore()
const loading = ref(false)
const employeeList = ref([])

const searchForm = reactive({
  type: '',
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
  type: '',
  rpDate: '',
  reason: '',
  amount: 0
})

const applyRules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  rpDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入原因', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
}

const approveDialogVisible = ref(false)
const approveForm = reactive({
  id: null,
  status: ''
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
    if (searchForm.type) {
      params.type = searchForm.type
    }
    if (searchForm.status) {
      params.status = searchForm.status
    }

    let res
    if (userStore.isAdmin || userStore.isHR) {
      res = await getRewardPunishmentList(params)
    } else {
      res = await getMyRewardPunishmentList(params)
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
  searchForm.type = ''
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

const openApplyDialog = () => {
  applyForm.type = ''
  applyForm.rpDate = ''
  applyForm.reason = ''
  applyForm.amount = 0
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
    await createRewardPunishment(data)
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
  approveDialogVisible.value = true
}

const handleApproveSubmit = async () => {
  if (!approveForm.status) {
    ElMessage.warning('请选择审批结果')
    return
  }
  try {
    await approveRewardPunishment(approveForm.id, {
      status: approveForm.status
    })
    ElMessage.success('审批成功')
    approveDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('审批失败')
  }
}

onMounted(() => {
  loadData()
  if (userStore.isAdmin || userStore.isHR) {
    loadEmployees()
  }
})
</script>
