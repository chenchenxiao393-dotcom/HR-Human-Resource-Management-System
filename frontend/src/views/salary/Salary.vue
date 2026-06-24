<template>
  <div class="salary-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="32"><Money /></el-icon>
        </div>
        <div class="header-text">
          <h1>工资查询</h1>
          <p class="subtitle">查看您的薪资信息</p>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="salary-content">
      <el-empty v-if="!salaryData" description="暂无薪资数据" />
      
      <template v-else>
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stat-cards">
          <el-col :xs="24" :sm="8">
            <div class="stat-card primary">
              <div class="stat-icon">
                <el-icon :size="28"><Coin /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">基本薪资</div>
                <div class="stat-value">¥{{ formatNumber(salaryData.salary) }}</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card warning">
              <div class="stat-icon">
                <el-icon :size="28"><Timer /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">本月加班</div>
                <div class="stat-value">{{ salaryData.overtimeHours }} 小时</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card info">
              <div class="stat-icon">
                <el-icon :size="28"><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">统计月份</div>
                <div class="stat-value">{{ formatMonth(salaryData.salaryMonth) }}</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 薪资构成 -->
        <el-card class="salary-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon :size="20"><DataAnalysis /></el-icon>
              <span>薪资构成</span>
            </div>
          </template>
          
          <div class="salary-breakdown">
            <div class="breakdown-item">
              <div class="item-left">
                <el-icon :size="18"><Coin /></el-icon>
                <span>基本薪资</span>
              </div>
              <div class="item-right">¥{{ formatNumber(salaryData.salary) }}</div>
            </div>
            
            <el-divider />
            
            <div class="breakdown-total">
              <div class="total-label">应发合计</div>
              <div class="total-value">¥{{ formatNumber(salaryData.salary) }}</div>
            </div>
          </div>
        </el-card>

        <!-- 加班明细 -->
        <el-card class="overtime-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon :size="20"><Timer /></el-icon>
              <span>加班明细</span>
              <el-tag type="warning" size="small" class="ml-auto">本月统计</el-tag>
            </div>
          </template>
          
          <el-empty v-if="!salaryData.overtimeList || salaryData.overtimeList.length === 0" description="本月暂无加班记录" />
          
          <template v-else>
            <el-table :data="salaryData.overtimeList" border stripe class="overtime-table">
              <el-table-column prop="overtimeDate" label="加班日期" width="120" align="center">
                <template #default="{ row }">
                  {{ formatDate(row.overtimeDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="startTime" label="开始时间" width="120" align="center" />
              <el-table-column prop="endTime" label="结束时间" width="120" align="center" />
              <el-table-column prop="hours" label="加班时长" align="center">
                <template #default="{ row }">
                  <el-tag type="warning">{{ row.hours }} 小时</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="reason" label="加班原因" show-overflow-tooltip />
              <el-table-column prop="status" label="审批状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="overtime-summary">
              <div class="summary-item">
                <el-icon :size="18"><Timer /></el-icon>
                <span>本月加班总时长：<strong>{{ salaryData.overtimeHours }} 小时</strong></span>
              </div>
              <div class="summary-tip">
                <el-icon :size="14"><Warning /></el-icon>
                <span>加班费由财务部门根据公司规定核算后统一发放</span>
              </div>
            </div>
          </template>
        </el-card>

        <!-- 注意事项 -->
        <el-card class="notice-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon :size="20"><InfoFilled /></el-icon>
              <span>注意事项</span>
            </div>
          </template>
          
          <ul class="notice-list">
            <li>
              <el-icon :size="16"><Check /></el-icon>
              <span>基本薪资为您在公司的固定月薪标准</span>
            </li>
            <li>
              <el-icon :size="16"><Check /></el-icon>
              <span>加班费由财务部门根据公司规定核算后统一发放</span>
            </li>
            <li>
              <el-icon :size="16"><Check /></el-icon>
              <span>如有疑问，请联系人事部门（8001）</span>
            </li>
          </ul>
        </el-card>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Money, Coin, Timer, Calendar, DataAnalysis, Warning, InfoFilled, Check } from '@element-plus/icons-vue'
import { getMySalary } from '@/api/salary'

const loading = ref(false)
const salaryData = ref(null)

const formatNumber = (num) => {
  if (!num && num !== 0) return '0.00'
  return Number(num).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const formatMonth = (monthStr) => {
  if (!monthStr) return '-'
  const [year, month] = monthStr.split('-')
  return `${year}年${parseInt(month)}月`
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getStatusType = (status) => {
  const types = {
    '通过': 'success',
    '待审批': 'warning',
    '拒绝': 'danger'
  }
  return types[status] || 'info'
}

const fetchSalary = async () => {
  loading.value = true
  try {
    const res = await getMySalary()
    if (res && res.code === 200 && res.data) {
      salaryData.value = res.data
    } else {
      ElMessage.warning(res?.message || '获取薪资数据失败')
    }
  } catch (error) {
    console.error('获取薪资数据失败:', error)
    ElMessage.error('获取薪资数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchSalary()
})
</script>

<style scoped>
.salary-page {
  padding: 0;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.salary-content {
  min-height: 400px;
}

.stat-cards {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-card.primary {
  border-left: 4px solid #667eea;
}

.stat-card.primary .stat-icon {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.stat-card.warning {
  border-left: 4px solid #e6a23c;
}

.stat-card.warning .stat-icon {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-card.info {
  border-left: 4px solid #409eff;
}

.stat-card.info .stat-icon {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.salary-card,
.overtime-card,
.notice-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.ml-auto {
  margin-left: auto;
}

.salary-breakdown {
  padding: 16px 0;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #606266;
}

.item-right {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.breakdown-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 16px;
  padding: 20px;
}

.total-label {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.total-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
}

.overtime-table {
  margin-bottom: 20px;
}

.overtime-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: #fdf6ec;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #e6a23c;
  font-size: 16px;
}

.summary-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 13px;
}

.notice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  color: #606266;
  font-size: 14px;
}

.notice-list li:not(:last-child) {
  border-bottom: 1px solid #f0f0f0;
}

.notice-list li .el-icon {
  color: #67c23a;
}

@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 16px;
  }
  
  .stat-value {
    font-size: 20px;
  }
}
</style>
