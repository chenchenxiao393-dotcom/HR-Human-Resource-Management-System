<template>
  <div class="task-container">
    <div class="task-header">
      <div class="header-left">
        <h2>我的任务</h2>
        <p class="subtitle">查看和管理分配给我的工作任务</p>
      </div>
    </div>

    <div class="task-cards">
      <div
        v-for="task in tasks"
        :key="task.id"
        class="task-card"
      >
        <div class="card-header">
          <div class="task-info">
            <h3>{{ task.taskName }}</h3>
            <span class="priority-tag" :class="getPriorityClass(task.priority)">
              {{ task.priority }}
            </span>
          </div>
          <el-tag :type="getStatusType(task.status)" class="status-tag">
            {{ task.status }}
          </el-tag>
        </div>

        <div class="card-body">
          <p class="description" v-if="task.description">{{ task.description }}</p>
          
          <div class="info-row">
            <span class="label">截止日期</span>
            <span class="value deadline" :class="{ 'overdue': isOverdue(task.deadline) }">
              {{ task.deadline || '未设置' }}
            </span>
          </div>
          
          <div class="info-row">
            <span class="label">创建人</span>
            <span class="value">{{ task.creator || '-' }}</span>
          </div>

          <div class="progress-section">
            <div class="progress-header">
              <span class="label">任务进度</span>
              <span class="value">{{ task.progress }}%</span>
            </div>
            <el-progress :percentage="task.progress" :color="getProgressColor(task.progress)" />
          </div>
        </div>

        <div class="card-footer">
          <div class="action-buttons">
            <el-button
              v-if="task.status !== '已完成'"
              type="primary"
              @click="openUpdateModal(task)"
              size="small"
            >
              <el-icon><Edit /></el-icon>
              更新进度
            </el-button>
            <el-button
              v-if="task.status !== '已完成'"
              type="success"
              @click="markCompleted(task.id)"
              size="small"
            >
              <el-icon><Check /></el-icon>
              标记完成
            </el-button>
            <el-button v-else type="success" disabled size="small">
              <el-icon><Check /></el-icon>
              已完成
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && tasks.length === 0" description="暂无分配的任务" />

    <el-dialog title="更新任务进度" v-model="updateModalVisible" width="400px">
      <el-form :model="updateForm" label-width="80px">
        <el-form-item label="任务进度">
          <el-slider v-model="updateForm.progress" :min="0" :max="100" :step="10" />
          <span class="progress-value">{{ updateForm.progress }}%</span>
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="updateForm.status">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Check } from '@element-plus/icons-vue'
import { getMyTasks, updateTask } from '@/api/task'

const loading = ref(false)
const tasks = ref([])
const updateModalVisible = ref(false)
const updateForm = ref({
  id: null,
  progress: 0,
  status: '未开始'
})

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

const isOverdue = (deadline) => {
  if (!deadline) return false
  const today = new Date()
  const deadlineDate = new Date(deadline)
  return deadlineDate < today
}

const fetchTasks = async () => {
  loading.value = true
  try {
    const res = await getMyTasks()
    if (res.data) {
      tasks.value = res.data
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

const openUpdateModal = (task) => {
  updateForm.value = {
    id: task.id,
    progress: task.progress || 0,
    status: task.status || '未开始'
  }
  updateModalVisible.value = true
}

const handleUpdate = async () => {
  try {
    const res = await updateTask(updateForm.value.id, {
      progress: updateForm.value.progress,
      status: updateForm.value.status
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

const markCompleted = async (taskId) => {
  try {
    const res = await updateTask(taskId, {
      progress: 100,
      status: '已完成'
    })
    if (res.code === 200) {
      ElMessage.success('任务已标记为完成')
      fetchTasks()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('标记完成失败:', error)
    ElMessage.error('标记完成失败')
  }
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-container {
  padding: 0;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
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

.task-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.task-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8ecf0;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.task-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f5f7fb 0%, #fff 100%);
  border-bottom: 1px solid #e8ecf0;
}

.task-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a2235;
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

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
}

.card-body {
  padding: 16px 20px;
}

.description {
  margin: 0 0 16px 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #f0f2f5;
}

.info-row:last-of-type {
  border-bottom: none;
}

.info-row .label {
  font-size: 13px;
  color: #8a94a6;
}

.info-row .value {
  font-size: 13px;
  color: #1a2235;
  font-weight: 500;
}

.deadline.overdue {
  color: #dc3545;
}

.progress-section {
  margin-top: 16px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-value {
  font-size: 13px;
  color: #667eea;
  font-weight: 600;
}

.card-footer {
  padding: 16px 20px;
  background: #fafbfc;
  border-top: 1px solid #e8ecf0;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-buttons .el-button {
  flex: 1;
}

@media (max-width: 768px) {
  .task-cards {
    grid-template-columns: 1fr;
  }
}
</style>