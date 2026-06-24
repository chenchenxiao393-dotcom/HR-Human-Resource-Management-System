<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">考核管理</h2>
      <el-button type="primary" @click="openDialog('create')" :disabled="!canOperate">新增考核</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="employeeId" label="员工" width="120" :formatter="formatEmployee" />
        <el-table-column prop="appraisalDate" label="考核日期" width="120" />
        <el-table-column prop="appraisalPeriod" label="考核周期" width="120" />
        <el-table-column prop="appraisalType" label="考核类型" width="100" />
        <el-table-column prop="score" label="得分" width="80" />
        <el-table-column prop="grade" label="等级" width="80" />
        <el-table-column prop="evaluator" label="评价人" width="100" />
        <el-table-column prop="evaluation" label="评价内容" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openDialog('edit', row)" :disabled="!canOperate">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :disabled="!canOperate">删除</el-button>
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考核日期" prop="appraisalDate">
              <el-date-picker v-model="form.appraisalDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考核周期" prop="appraisalPeriod">
              <el-select v-model="form.appraisalPeriod" style="width: 100%">
                <el-option label="月度" value="月度" />
                <el-option label="季度" value="季度" />
                <el-option label="年度" value="年度" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考核类型" prop="appraisalType">
              <el-select v-model="form.appraisalType" style="width: 100%">
                <el-option label="绩效" value="绩效" />
                <el-option label="能力" value="能力" />
                <el-option label="态度" value="态度" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="得分" prop="score">
              <el-input-number v-model="form.score" :min="0" :max="100" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="等级">
          <el-select v-model="form.grade" style="width: 100%">
            <el-option label="A" value="A" />
            <el-option label="B" value="B" />
            <el-option label="C" value="C" />
            <el-option label="D" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价人">
          <el-input v-model="form.evaluator" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="form.evaluation" type="textarea" :rows="3" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAppraisalList, createAppraisal, updateAppraisal, deleteAppraisal } from '@/api/appraisal'
import { getEmployeeList } from '@/api/employee'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const canOperate = computed(() => userStore.isAdmin || userStore.isHR)

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formType = ref('create')
const formRef = ref()
const employeeList = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const form = reactive({
  id: null, employeeId: null, appraisalDate: '', appraisalPeriod: '', appraisalType: '', score: 0, grade: '', evaluator: '', evaluation: '', remark: ''
})

const rules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  appraisalDate: [{ required: true, message: '请选择考核日期', trigger: 'change' }]
}

const tableData = ref([])

const formatEmployee = (row) => {
  const emp = employeeList.value.find(e => e.id === row.employeeId)
  return emp ? emp.name : '-'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAppraisalList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
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
  formType.value = type
  dialogTitle.value = type === 'create' ? '新增考核' : '编辑考核'
  if (row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => {
      if (key === 'employeeId') form[key] = null
      else if (key === 'score') form[key] = 0
      else form[key] = ''
    })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        formType.value === 'create' ? await createAppraisal(form) : await updateAppraisal(form.id, form)
        ElMessage.success('操作成功')
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error(error) }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteAppraisal(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(() => { loadData(); loadEmployees() })
</script>
