<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">培训管理</h2>
      <el-button type="primary" @click="openDialog('create')" :disabled="!canOperate">新增培训</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="trainingCode" label="培训编号" width="120" />
        <el-table-column prop="trainingName" label="培训名称" />
        <el-table-column prop="trainingType" label="培训类型" width="100" />
        <el-table-column prop="trainingDate" label="培训日期" width="120" />
        <el-table-column prop="duration" label="时长(小时)" width="100" />
        <el-table-column prop="lecturer" label="讲师" width="100" />
        <el-table-column prop="location" label="地点" width="100" />
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
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="培训编号" prop="trainingCode">
          <el-input v-model="form.trainingCode" />
        </el-form-item>
        <el-form-item label="培训名称" prop="trainingName">
          <el-input v-model="form.trainingName" />
        </el-form-item>
        <el-form-item label="培训类型" prop="trainingType">
          <el-select v-model="form.trainingType" style="width: 100%">
            <el-option label="内部培训" value="内部培训" />
            <el-option label="外部培训" value="外部培训" />
            <el-option label="在线培训" value="在线培训" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="培训日期" prop="trainingDate">
              <el-date-picker v-model="form.trainingDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长" prop="duration">
              <el-input-number v-model="form.duration" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="讲师" prop="lecturer">
              <el-input v-model="form.lecturer" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地点" prop="location">
              <el-input v-model="form.location" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="培训内容">
          <el-input v-model="form.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="form.maxParticipants" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="计划中" value="计划中" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
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
import { getTrainingList, createTraining, updateTraining, deleteTraining } from '@/api/training'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const canOperate = computed(() => userStore.isAdmin || userStore.isHR)

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formType = ref('create')
const formRef = ref()
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const form = reactive({
  id: null, trainingCode: '', trainingName: '', trainingType: '', trainingDate: '', duration: 1, lecturer: '', location: '', content: '', maxParticipants: 50, status: '计划中'
})

const rules = {
  trainingCode: [{ required: true, message: '请输入培训编号', trigger: 'blur' }],
  trainingName: [{ required: true, message: '请输入培训名称', trigger: 'blur' }]
}

const tableData = ref([])

const getStatusType = (status) => {
  switch (status) {
    case '计划中':
      return 'primary'
    case '进行中':
      return 'warning'
    case '已结束':
      return 'success'
    default:
      return 'info'
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTrainingList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const openDialog = (type, row = null) => {
  formType.value = type
  dialogTitle.value = type === 'create' ? '新增培训' : '编辑培训'
  if (row) {
    Object.assign(form, row)
  } else {
    Object.keys(form).forEach(key => {
      if (key === 'id') form[key] = null
      else if (key === 'duration') form[key] = 1
      else if (key === 'maxParticipants') form[key] = 50
      else if (key === 'status') form[key] = '计划中'
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
        formType.value === 'create' ? await createTraining(form) : await updateTraining(form.id, form)
        ElMessage.success('操作成功')
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error(error) }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteTraining(row.id); ElMessage.success('删除成功'); loadData() })
    .catch(() => {})
}

onMounted(() => { loadData() })
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a2235;
}

.table-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
