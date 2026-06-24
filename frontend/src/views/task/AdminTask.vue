<template>
  <div class="admin-task-container">
    <div class="page-header">
      <div class="header-left">
        <h2>任务管理</h2>
        <p class="subtitle">管理和分配员工任务</p>
      </div>
      <el-button type="primary" @click="openCreateModal">
        <el-icon><Plus /></el-icon>
        分配任务
      </el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchForm.taskName"
        placeholder="任务名称"
        class="search-input"
        @keyup.enter="fetchTasks"
      />
      <el-select v-model="searchForm.status" placeholder="任务状态">
        <el-option label="全部" value="" />
        <el-option label="未开始" value="未开始" />
        <el-option label="进行中" value="进行中" />
        <el-option label="已完成" value="已完成" />
      </el-select>
      <el-button type="primary" @click="fetchTasks">搜索</el-button>
    </div>

    <el-table :data="tasks" border stripe v-loading="loading" class="task-table">
      <el-table-column prop="taskName" label="任务名称" />
      <el-table-column prop="description" label="任务描述" :show-overflow-tooltip="true" />
      <el-table-column prop="employeeName" label="分配员工" />
      <el-table-column prop="deadline" label="截止日期" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="progress" label="进度">
        <template #default="{ row }">
          <div class="progress-wrapper">
            <el-progress :percentage="row.progress" :color="getProgressColor(row.progress)" :show-text="false" />
            <span class="progress-text">{{ row.progress }}%</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级">
        <template #default="{ row }">
          <span class="priority-tag" :class="getPriorityClass(row.priority)">
            {{ row.priority }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="creator" label="创建人" />
      <el-table-column prop="createTime" label="创建时间">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openUpdateModal(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      :total="total"
      :page-size="pageSize"
      :current-page.sync="pageNum"
      @current-change="fetchTasks"
      layout="total, prev, pager, next, jumper"
      class="pagination"
    />

    <el-dialog title="分配任务" v-model="createModalVisible" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="任务名称" required>
          <el-input v-model="createForm.taskName" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input type="textarea" v-model="createForm.description" rows="3" />
        </el-form-item>
        <el-form-item label="分配员工" required>
          <el-select v-model="createForm.employeeId" filterable>
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="createForm.deadline" type="date" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="createForm.priority" placeholder="请选择">
            <el-option label="紧急" value="紧急" />
            <el-option label="重要" value="重要" />
            <el-option label="普通" value="普通" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createModalVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">分配</el-button>
      </template>
    </el-dialog>

    <el-dialog title="编辑任务" v-model="updateModalVisible" width="500px">
      <el-form :model="updateForm" label-width="100px">
        <el-form-item label="任务名称">
          <el-input v-model="updateForm.taskName" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input type="textarea" v-model="updateForm.description" rows="3" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="updateForm.deadline" type="date" />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="updateForm.status">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务进度">
          <el-slider v-model="updateForm.progress" :min="0" :max="100" :step="10" />
          <span class="progress-value">{{ updateForm.progress }}%</span>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="updateForm.priority">
            <el-option label="紧急" value="紧急" />
            <el-option label="重要" value="重要" />
            <el-option label="普通" value="普通" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="updateModalVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getTaskList, createTask, updateTask, deleteTask } from '@/api/task'
import { getEmployeeList } from '@/api/employee'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const tasks = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const userStore = useUserStore()

const searchForm = reactive({
  taskName: '',
  status: ''
})

const createModalVisible = ref(false)
const createForm = reactive({
  taskName: '',
  description: '',
  employeeId: '',
  deadline: '',
  priority: '普通',
  creator: ''
})

const updateModalVisible = ref(false)
const updateForm = reactive({
  id: null,
  taskName: '',
  description: '',
  deadline: '',
  status: '未开始',
  progress: 0,
  priority: '普通'
})

const employees = ref([])

const getStatusType = (status) => {
  switch (status) {
    case '未开始':
      return 'info'
    case '进行中':
      return 'warning'
    case '已完成':
      return 'success'
    default:
      return 'info'
  }
}

const getPriorityClass = (priority) => {
  switch (priority) {
    case '紧急':
      return 'priority-urgent'
    case '重要':
      return 'priority-important'
    case '普通':
      return 'priority-normal'
    default:
      return 'priority-normal'
  }
}

const getProgressColor = (progress) => {
  if (progress >= 100) return '#67c23a'
  if (progress >= 50) return '#e6a23c'
  return '#f56c6c'
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const fetchTasks = async () => {
  loading.value = true
  try {
    const res = await getTaskList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      taskName: searchForm.taskName || undefined,
      status: searchForm.status || undefined
    })
    if (res && res.code === 200 && res.data) {
      tasks.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchEmployees = async () => {
  try {
    const res = await getEmployeeList({ pageNum: 1, pageSize: 100 })
    if (res && res.code === 200 && res.data && res.data.records) {
      employees.value = res.data.records
    }
  } catch (error) {
    console.error('获取员工列表失败:', error)
  }
}

const openCreateModal = () => {
  createForm.taskName = ''
  createForm.description = ''
  createForm.employeeId = ''
  createForm.deadline = ''
  createForm.priority = '普通'
  createForm.creator = userStore.userInfo.realName || userStore.userInfo.username || '管理员'
  createModalVisible.value = true
}

const handleCreate = async () => {
  if (!createForm.taskName || !createForm.employeeId) {
    ElMessage.error('请填写必填项')
    return
  }
  try {
    const res = await createTask({
      taskName: createForm.taskName,
      description: createForm.description,
      employeeId: createForm.employeeId,
      deadline: createForm.deadline,
      priority: createForm.priority,
      creator: createForm.creator
    })
    if (res.code === 200) {
      ElMessage.success('分配成功')
      createModalVisible.value = false
      fetchTasks()
    } else {
      ElMessage.error(res.msg || '分配失败')
    }
  } catch (error) {
    console.error('创建任务失败:', error)
    ElMessage.error('创建任务失败')
  }
}

const openUpdateModal = (row) => {
  updateForm.id = row.id
  updateForm.taskName = row.taskName
  updateForm.description = row.description
  updateForm.deadline = row.deadline
  updateForm.status = row.status
  updateForm.progress = row.progress || 0
  updateForm.priority = row.priority
  updateModalVisible.value = true
}

const handleUpdate = async () => {
  try {
    const res = await updateTask(updateForm.id, {
      taskName: updateForm.taskName,
      description: updateForm.description,
      deadline: updateForm.deadline,
      status: updateForm.status,
      progress: updateForm.progress,
      priority: updateForm.priority
    })
    if (res.code === 200) {
      ElMessage.success('更新成功')
      updateModalVisible.value = false
      fetchTasks()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error) {
    console.error('更新任务失败:', error)
    ElMessage.error('更新任务失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteTask(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchTasks()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除任务失败:', error)
      ElMessage.error('删除任务失败')
    }
  }
}

onMounted(() => {
  fetchTasks()
  fetchEmployees()
})
</script>

<style scoped>
.admin-task-container {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #1a2235;
}

.subtitle {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #8a94a6;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.search-input {
  width: 200px;
}

.task-table {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.progress-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-text {
  font-size: 12px;
  color: #667eea;
  font-weight: 600;
  width: 36px;
}

.priority-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.priority-urgent {
  background: #fef0f0;
  color: #dc3545;
}

.priority-important {
  background: #fff7e6;
  color: #f59e0b;
}

.priority-normal {
  background: #f0f9ff;
  color: #3b82f6;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.progress-value {
  margin-left: 10px;
  color: #667eea;
  font-weight: 600;
}
</style>