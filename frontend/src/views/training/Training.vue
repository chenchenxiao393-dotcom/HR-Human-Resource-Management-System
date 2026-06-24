<template>
  <div class="training-container">
    <div class="training-header">
      <div class="header-left">
        <h2>培训管理</h2>
        <p class="subtitle">查看培训课程和报名记录</p>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="training-tabs">
      <el-tab-pane label="可报名培训" name="available">
        <div class="tab-content">
          <div class="training-cards">
            <div
              v-for="training in availableTrainings"
              :key="training.id"
              class="training-card"
            >
              <div class="card-header">
                <div class="training-info">
                  <h3>{{ training.trainingName }}</h3>
                  <span class="training-code">{{ training.trainingCode }}</span>
                </div>
                <el-tag :type="getStatusType(training.status)" class="status-tag">
                  {{ training.status }}
                </el-tag>
              </div>
              
              <div class="card-body">
                <div class="info-row">
                  <span class="label">培训类型</span>
                  <span class="value">{{ training.trainingType }}</span>
                </div>
                <div class="info-row">
                  <span class="label">培训日期</span>
                  <span class="value">{{ training.trainingDate }}</span>
                </div>
                <div class="info-row">
                  <span class="label">时长</span>
                  <span class="value">{{ training.duration }}小时</span>
                </div>
                <div class="info-row">
                  <span class="label">讲师</span>
                  <span class="value">{{ training.lecturer }}</span>
                </div>
                <div class="info-row">
                  <span class="label">地点</span>
                  <span class="value">{{ training.location }}</span>
                </div>
                <div class="info-row" v-if="training.content">
                  <span class="label">培训内容</span>
                  <span class="value content-value">{{ training.content }}</span>
                </div>
              </div>

              <div class="card-footer">
                <el-button
                  v-if="!training.enrolled"
                  type="primary"
                  @click="handleEnroll(training.id)"
                  :disabled="training.status !== '计划中'"
                >
                  <el-icon><Plus /></el-icon>
                  报名参加
                </el-button>
                <el-button v-else type="success" disabled>
                  <el-icon><Check /></el-icon>
                  已报名
                </el-button>
              </div>
            </div>
          </div>

          <el-empty v-if="!loading && availableTrainings.length === 0" description="暂无可报名的培训课程" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="培训记录" name="records">
        <div class="tab-content">
          <el-table :data="trainingRecords" border stripe v-loading="loading" class="records-table">
            <el-table-column prop="trainingCode" label="培训编号" width="120" />
            <el-table-column prop="trainingName" label="培训名称" />
            <el-table-column prop="trainingType" label="培训类型" width="100" />
            <el-table-column prop="trainingDate" label="培训日期" width="120" />
            <el-table-column prop="lecturer" label="讲师" width="100" />
            <el-table-column prop="location" label="地点" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getRecordStatusType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="成绩" width="100">
              <template #default="{ row }">
                <span v-if="row.score">{{ row.score }}</span>
                <span v-else class="empty-text">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="evaluation" label="评价" width="150">
              <template #default="{ row }">
                <span v-if="row.evaluation" class="evaluation-text">{{ row.evaluation }}</span>
                <span v-else class="empty-text">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="attendanceStatus" label="出勤状态" width="100">
              <template #default="{ row }">
                <span v-if="row.attendanceStatus">{{ row.attendanceStatus }}</span>
                <span v-else class="empty-text">-</span>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="!loading && trainingRecords.length === 0" description="暂无培训记录" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Check } from '@element-plus/icons-vue'
import { getAvailableTrainings, enrollTraining, getMyTrainingRecords } from '@/api/training'

const loading = ref(false)
const activeTab = ref('available')
const availableTrainings = ref([])
const trainingRecords = ref([])

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

const getRecordStatusType = (status) => {
  switch (status) {
    case '已报名':
      return 'primary'
    case '已完成':
      return 'success'
    case '缺席':
      return 'danger'
    default:
      return 'info'
  }
}

const fetchAvailableTrainings = async () => {
  loading.value = true
  try {
    const res = await getAvailableTrainings()
    if (res.data) {
      availableTrainings.value = res.data
    }
  } catch (error) {
    console.error('获取培训列表失败:', error)
    ElMessage.error('获取培训列表失败')
  } finally {
    loading.value = false
  }
}

const fetchTrainingRecords = async () => {
  loading.value = true
  try {
    const res = await getMyTrainingRecords()
    if (res.data) {
      trainingRecords.value = res.data
    }
  } catch (error) {
    console.error('获取培训记录失败:', error)
    ElMessage.error('获取培训记录失败')
  } finally {
    loading.value = false
  }
}

const handleEnroll = async (trainingId) => {
  try {
    const res = await enrollTraining(trainingId)
    if (res.code === 200) {
      ElMessage.success('报名成功')
      fetchAvailableTrainings()
      fetchTrainingRecords()
    } else {
      ElMessage.error(res.msg || '报名失败')
    }
  } catch (error) {
    console.error('报名失败:', error)
    ElMessage.error('报名失败')
  }
}

const handleTabChange = (tab) => {
  if (tab === 'records') {
    fetchTrainingRecords()
  }
}

onMounted(() => {
  fetchAvailableTrainings()
})
</script>

<style scoped>
.training-container {
  padding: 0;
}

.training-header {
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

.training-tabs {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.tab-content {
  padding: 24px;
}

/* 培训卡片 */
.training-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.training-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8ecf0;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.training-card:hover {
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

.training-info h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a2235;
}

.training-code {
  font-size: 12px;
  color: #8a94a6;
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
}

.card-body {
  padding: 16px 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed #f0f2f5;
}

.info-row:last-child {
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

.content-value {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-footer {
  padding: 16px 20px;
  background: #fafbfc;
  border-top: 1px solid #e8ecf0;
}

.card-footer .el-button {
  width: 100%;
}

/* 培训记录表格 */
.records-table {
  width: 100%;
}

.records-table :deep(.el-table__header th) {
  background: #f5f7fb;
  color: #1a2235;
  font-weight: 600;
}

.empty-text {
  color: #c0c4cc;
}

.evaluation-text {
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
}

@media (max-width: 768px) {
  .training-cards {
    grid-template-columns: 1fr;
  }
}
</style>
