<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">合同管理</h2>
      <el-button type="primary" @click="openDialog('create')" :disabled="!canOperate">新增合同</el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="生效中" value="生效中" />
            <el-option label="已过期" value="已过期" />
            <el-option label="已终止" value="已终止" />
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
        <el-table-column prop="contractCode" label="合同编号" width="150" />
        <el-table-column prop="employeeId" label="员工" width="120" :formatter="formatEmployee" />
        <el-table-column prop="contractType" label="合同类型" width="120" />
        <el-table-column prop="signDate" label="签订日期" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="到期日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
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
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="合同编号" prop="contractCode">
          <el-input v-model="form.contractCode" />
        </el-form-item>
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="form.employeeId" style="width: 100%">
            <el-option
              v-for="emp in employeeList"
              :key="emp.id"
              :label="emp.name"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="合同类型" prop="contractType">
          <el-select v-model="form.contractType" style="width: 100%">
            <el-option label="固定期限" value="固定期限" />
            <el-option label="无固定期限" value="无固定期限" />
            <el-option label="实习" value="实习" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="签订日期" prop="signDate">
              <el-date-picker v-model="form.signDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="到期日期" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="生效中" value="生效中" />
                <el-option label="已过期" value="已过期" />
                <el-option label="已终止" value="已终止" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getContractList, createContract, updateContract, deleteContract } from '@/api/contract'
import { getEmployeeList } from '@/api/employee'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const canOperate = computed(() => userStore.isAdmin || userStore.isHR)

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formType = ref('create')
const employeeList = ref([])

const searchForm = reactive({
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  contractCode: '',
  employeeId: null,
  contractType: '固定期限',
  signDate: '',
  startDate: '',
  endDate: '',
  status: '生效中',
  remark: ''
})

const rules = {
  contractCode: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }]
}

const tableData = ref([])

const formatEmployee = (row) => {
  const emp = employeeList.value.find(e => e.id === row.employeeId)
  return emp ? emp.name : '-'
}

const getStatusType = (status) => {
  const types = { '生效中': 'success', '已过期': 'warning', '已终止': 'info' }
  return types[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getContractList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
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

const openDialog = (type, row = null) => {
  formType.value = type
  dialogTitle.value = type === 'create' ? '新增合同' : '编辑合同'
  
  if (type === 'edit' && row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => {
      if (key === 'employeeId') form[key] = null
      else if (key === 'status') form[key] = '生效中'
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
        if (formType.value === 'create') {
          await createContract(form)
          ElMessage.success('创建成功')
        } else {
          await updateContract(form.id, form)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该合同吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteContract(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadEmployees()
})
</script>
