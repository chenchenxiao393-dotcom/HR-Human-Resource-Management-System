<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">部门管理</h2>
      <el-button type="primary" @click="openDialog('create')" :disabled="!canOperate">新增部门</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="departmentCode" label="部门编码" width="150" />
        <el-table-column prop="departmentName" label="部门名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openDialog('edit', row)" :disabled="!canOperate">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :disabled="!canOperate">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="部门编码" prop="departmentCode">
          <el-input v-model="form.departmentCode" />
        </el-form-item>
        <el-form-item label="部门名称" prop="departmentName">
          <el-input v-model="form.departmentName" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { getDepartmentList, createDepartment, updateDepartment, deleteDepartment } from '@/api/department'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const canOperate = computed(() => userStore.isAdmin || userStore.isHR)

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formType = ref('create')

const form = reactive({
  id: null,
  departmentCode: '',
  departmentName: '',
  description: ''
})

const rules = {
  departmentCode: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
  departmentName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDepartmentList({ pageNum: 1, pageSize: 100 })
    tableData.value = res.data.records
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const openDialog = (type, row = null) => {
  formType.value = type
  dialogTitle.value = type === 'create' ? '新增部门' : '编辑部门'
  
  if (type === 'edit' && row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => {
      form[key] = key === 'id' ? null : ''
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
          await createDepartment(form)
          ElMessage.success('创建成功')
        } else {
          await updateDepartment(form.id, form)
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
  ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDepartment(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>
