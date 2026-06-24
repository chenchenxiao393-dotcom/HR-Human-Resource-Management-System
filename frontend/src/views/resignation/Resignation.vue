<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">离职管理</h2>
      <el-button type="primary" @click="openDialog('create')" :disabled="!canOperate">新增离职</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="employeeId" label="员工" width="120" :formatter="formatEmployee" />
        <el-table-column prop="resignationDate" label="离职日期" width="120" />
        <el-table-column prop="resignationType" label="离职类型" width="120" />
        <el-table-column prop="reason" label="离职原因" />
        <el-table-column prop="handoverPerson" label="交接人" width="100" />
        <el-table-column prop="approveStatus" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.approveStatus)">{{ row.approveStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.approveStatus === '待审批'" type="success" size="small" @click="handleApprove(row)" :disabled="!canOperate">审批</el-button>
            <el-button type="primary" size="small" @click="openDialog('edit', row)" :disabled="!canOperate">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="form.employeeId" style="width: 100%">
            <el-option v-for="emp in employeeList" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="离职日期" prop="resignationDate">
          <el-date-picker v-model="form.resignationDate" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="离职类型" prop="resignationType">
          <el-select v-model="form.resignationType" style="width: 100%">
            <el-option label="主动离职" value="主动离职" />
            <el-option label="被动离职" value="被动离职" />
          </el-select>
        </el-form-item>
        <el-form-item label="离职原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="交接人">
          <el-input v-model="form.handoverPerson" />
        </el-form-item>
        <el-form-item v-if="dialogType === 'approve'" label="审批备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :disabled="!canOperate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getResignationList, createResignation, updateResignation, approveResignation } from '@/api/resignation'
import { getEmployeeList } from '@/api/employee'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const canOperate = computed(() => userStore.isAdmin || userStore.isHR)

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogType = ref('create')
const formRef = ref()
const employeeList = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const form = reactive({
  id: null, employeeId: null, resignationDate: '', resignationType: '', reason: '', handoverPerson: '', approveStatus: '待审批', remark: ''
})

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  resignationDate: [{ required: true, message: '请选择离职日期', trigger: 'change' }]
}

const tableData = ref([])

const formatEmployee = (row) => {
  const emp = employeeList.value.find(e => e.id === row.employeeId)
  return emp ? emp.name : '-'
}

const getStatusType = (status) => {
  const types = { '待审批': 'warning', '已批准': 'success', '已拒绝': 'danger' }
  return types[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getResignationList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const loadEmployees = async () => {
  try {
    const res = await getEmployeeList({ pageNum: 1, pageSize: 100 })
    employeeList.value = res.data.records
  } catch (error) { console.error(error) }
}

const openDialog = (type, row = null) => {
  dialogType.value = type
  dialogTitle.value = type === 'create' ? '新增离职' : (type === 'approve' ? '审批离职' : '编辑离职')
  if (row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => {
      if (key === 'employeeId') form[key] = null
      else if (key === 'approveStatus') form[key] = '待审批'
      else form[key] = ''
    })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (dialogType.value === 'approve') {
    await approveResignation(form.id, form)
    ElMessage.success('审批成功')
  } else {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
      if (valid) {
        dialogType.value === 'create' ? await createResignation(form) : await updateResignation(form.id, form)
        ElMessage.success('操作成功')
      }
    })
  }
  dialogVisible.value = false
  loadData()
}

const handleApprove = (row) => { openDialog('approve', row) }

onMounted(() => { loadData(); loadEmployees() })
</script>
