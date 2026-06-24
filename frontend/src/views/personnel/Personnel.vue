<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">人员管理</h2>
      <el-button type="primary" @click="openDialog('create')" :disabled="!canOperate">新增人员</el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="员工编号">
          <el-input v-model="searchForm.employeeCode" placeholder="请输入编号" clearable />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="searchForm.departmentId" placeholder="请选择" clearable>
            <el-option
              v-for="dept in departmentList"
              :key="dept.id"
              :label="dept.departmentName"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择" clearable>
            <el-option label="系统管理员" value="ADMIN" />
            <el-option label="人事专员" value="HR" />
            <el-option label="普通员工" value="EMPLOYEE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.employeeStatus" placeholder="请选择" clearable>
            <el-option label="在职" value="在职" />
            <el-option label="离职" value="离职" />
            <el-option label="退休" value="退休" />
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
        <el-table-column prop="employeeCode" label="员工编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="departmentName" label="部门" width="120" />
        <el-table-column prop="position" label="职位" width="100" />
        <el-table-column prop="role" label="系统角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="employeeStatus" label="在职状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.employeeStatus === '在职' ? 'success' : 'info'">
              {{ row.employeeStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userStatus" label="账号状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.userStatus === 'NORMAL' ? 'success' : 'danger'">
              {{ row.userStatus === 'NORMAL' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="needChangePassword" label="首次登录" width="80">
          <template #default="{ row }">
            <el-tag :type="row.needChangePassword ? 'warning' : 'success'" size="small">
              {{ row.needChangePassword ? '需改密' : '已改密' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
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
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      class="form-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工编号" prop="employeeCode">
              <el-input v-model="form.employeeCode" :disabled="formType === 'edit'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="form.birthDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">任职信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="departmentId">
              <el-select v-model="form.departmentId" style="width: 100%">
                <el-option
                  v-for="dept in departmentList"
                  :key="dept.id"
                  :label="dept.departmentName"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker v-model="form.hireDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="在职状态" prop="employeeStatus">
              <el-select v-model="form.employeeStatus" style="width: 100%">
                <el-option label="在职" value="在职" />
                <el-option label="离职" value="离职" />
                <el-option label="退休" value="退休" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input-number v-model="form.salary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">其他信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="户口类型" prop="householdType">
              <el-input v-model="form.householdType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="政治面貌" prop="politicalAffiliation">
              <el-input v-model="form.politicalAffiliation" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况" prop="healthStatus">
              <el-input v-model="form.healthStatus" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">系统账号</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="系统角色" prop="role">
              <el-select v-model="form.role" style="width: 100%">
                <el-option label="系统管理员" value="ADMIN" />
                <el-option label="人事专员" value="HR" />
                <el-option label="普通员工" value="EMPLOYEE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号状态" prop="userStatus">
              <el-select v-model="form.userStatus" style="width: 100%">
                <el-option label="正常" value="NORMAL" />
                <el-option label="禁用" value="DISABLED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="formType === 'create'">
            <el-alert
              title="账号信息"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                登录账号：<strong>{{ form.name || '姓名' }}</strong>，
                初始密码：<strong>123456</strong>（首次登录需修改密码）
              </template>
            </el-alert>
          </el-col>
        </el-row>
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
import { getEmployeeList, createEmployee, updateEmployee, deleteEmployee } from '@/api/employee'
import { getAllDepartments } from '@/api/department'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const canOperate = computed(() => userStore.isAdmin || userStore.isHR)

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formType = ref('create')

const departmentList = ref([])

const searchForm = reactive({
  name: '',
  employeeCode: '',
  departmentId: '',
  employeeStatus: '',
  role: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  userId: null,
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
  departmentId: null,
  position: '',
  employeeStatus: '在职',
  salary: 0,
  role: 'EMPLOYEE',
  userStatus: 'NORMAL'
})

const rules = {
  employeeCode: [{ required: true, message: '请输入员工编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  role: [{ required: true, message: '请选择系统角色', trigger: 'change' }]
}

const tableData = ref([])

const getRoleName = (role) => {
  const names = { ADMIN: '管理员', HR: '人事专员', EMPLOYEE: '员工' }
  return names[role] || role
}

const getRoleTagType = (role) => {
  const types = { ADMIN: 'danger', HR: 'warning', EMPLOYEE: 'success' }
  return types[role] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getEmployeeList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    // 扩展数据，添加用户相关信息
    tableData.value = res.data.records.map(item => ({
      ...item,
      departmentName: getDepartmentName(item.departmentId),
      userStatus: item.userStatus || 'NORMAL',
      role: item.role || 'EMPLOYEE'
    }))
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadDepartments = async () => {
  try {
    const res = await getAllDepartments()
    departmentList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const getDepartmentName = (deptId) => {
  if (!deptId) return '-'
  const dept = departmentList.value.find(d => d.id === deptId)
  return dept ? dept.departmentName : '-'
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.pageNum = 1
  loadData()
}

const openDialog = (type, row = null) => {
  formType.value = type
  dialogTitle.value = type === 'create' ? '新增人员' : '编辑人员'
  
  if (type === 'edit' && row) {
    Object.assign(form, {
      id: row.id,
      userId: row.userId || null,
      employeeCode: row.employeeCode,
      name: row.name,
      gender: row.gender || '',
      birthDate: row.birthDate || '',
      phone: row.phone || '',
      email: row.email || '',
      address: row.address || '',
      householdType: row.householdType || '',
      politicalAffiliation: row.politicalAffiliation || '',
      healthStatus: row.healthStatus || '',
      hireDate: row.hireDate || '',
      departmentId: row.departmentId,
      position: row.position || '',
      employeeStatus: row.employeeStatus || '在职',
      salary: row.salary || 0,
      role: row.role || 'EMPLOYEE',
      userStatus: row.userStatus || 'NORMAL'
    })
  } else {
    Object.keys(form).forEach(key => {
      if (key === 'employeeStatus' || key === 'salary') {
        form[key] = key === 'employeeStatus' ? '在职' : 0
      } else if (key === 'departmentId') {
        form[key] = null
      } else if (key === 'role') {
        form[key] = 'EMPLOYEE'
      } else if (key === 'userStatus') {
        form[key] = 'NORMAL'
      } else if (key === 'id' || key === 'userId') {
        form[key] = null
      } else {
        form[key] = ''
      }
    })
  }
  
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          employeeCode: form.employeeCode,
          name: form.name,
          gender: form.gender,
          birthDate: form.birthDate,
          phone: form.phone,
          email: form.email,
          address: form.address,
          householdType: form.householdType,
          politicalAffiliation: form.politicalAffiliation,
          healthStatus: form.healthStatus,
          hireDate: form.hireDate,
          departmentId: form.departmentId,
          position: form.position,
          employeeStatus: form.employeeStatus,
          salary: form.salary || null,
          role: form.role,
          userStatus: form.userStatus
        }
        
        if (formType.value === 'create') {
          await createEmployee(submitData)
          ElMessage.success('创建成功')
        } else {
          submitData.id = form.id
          submitData.userId = form.userId
          await updateEmployee(form.id, submitData)
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
  ElMessageBox.confirm('确定要删除该人员吗？该操作将同时删除关联的系统账号！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteEmployee(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadDepartments()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.search-form {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.table-container {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-dialog :deep(.el-divider) {
  margin: 16px 0 20px;
}

.form-dialog :deep(.el-divider__text) {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
</style>
