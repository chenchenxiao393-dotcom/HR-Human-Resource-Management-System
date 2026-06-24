<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">系统维护</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="panel">
          <div class="panel-header">
            <h3>数据备份</h3>
          </div>
          <div class="panel-content">
            <p style="color: #909399; margin-bottom: 20px;">定期备份数据库，确保数据安全</p>
            <el-button type="primary" :loading="backupLoading" @click="handleBackup">立即备份</el-button>
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="panel">
          <div class="panel-header">
            <h3>数据恢复</h3>
          </div>
          <div class="panel-content">
            <p style="color: #909399; margin-bottom: 20px;">从备份文件恢复数据库（谨慎操作）</p>
            <el-button type="warning" @click="handleRestore">选择备份文件恢复</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <div class="panel">
          <div class="panel-header">
            <h3>系统信息</h3>
          </div>
          <div class="panel-content">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="系统名称">企业人事管理系统</el-descriptions-item>
              <el-descriptions-item label="版本">1.0.0</el-descriptions-item>
              <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
              <el-descriptions-item label="后端框架">Spring Boot 2.7</el-descriptions-item>
              <el-descriptions-item label="前端框架">Vue 3</el-descriptions-item>
              <el-descriptions-item label="UI组件">Element Plus</el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <div class="panel">
          <div class="panel-header">
            <h3>操作日志</h3>
          </div>
          <div class="panel-content">
            <p style="color: #909399;">系统操作日志记录</p>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { backup } from '@/api/system'

const backupLoading = ref(false)

const handleBackup = async () => {
  try {
    backupLoading.value = true
    await backup()
    ElMessage.success('备份成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('备份失败')
  } finally {
    backupLoading.value = false
  }
}

const handleRestore = () => {
  ElMessageBox.alert('请在服务器上使用备份文件进行恢复操作', '提示', {
    confirmButtonText: '确定',
    type: 'warning'
  })
}
</script>

<style scoped>
.panel {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.panel-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.panel-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.panel-content {
  padding: 10px 0;
}
</style>
