<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">通知管理</h2>
    </div>

    <div class="notify-card">
      <div class="card-header">
        <el-icon :size="20"><Bell /></el-icon>
        <span>发送通知</span>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="notify-form">
        <el-form-item label="接收人" prop="employeeId">
          <el-select v-model="form.employeeId" placeholder="请选择接收人" style="width: 100%">
            <el-option label="全体员工" :value="0" />
            <el-option
              v-for="emp in employeeList"
              :key="emp.id"
              :label="`${emp.name} (${emp.employeeCode})`"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" />
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="系统通知" value="系统通知" />
            <el-option label="公告" value="公告" />
            <el-option label="提醒" value="提醒" />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请输入通知内容"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            <el-icon><Promotion /></el-icon>
            发送通知
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="history-card">
      <div class="card-header">
        <el-icon :size="20"><Document /></el-icon>
        <span>发送记录</span>
      </div>
      <el-table :data="historyList" border stripe v-loading="historyLoading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="接收人数" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.count }} 人</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="已读" width="80" align="center">
          <template #default="{ row }">
            {{ row.readCount || 0 }}/{{ row.count || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, Promotion, Document } from '@element-plus/icons-vue'
import { sendNotification, getAllNotifications } from '@/api/notification'
import { getEmployeeList } from '@/api/employee'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const historyLoading = ref(false)
const formRef = ref()
const employeeList = ref([])
const historyList = ref([])

const form = reactive({
  employeeId: '',
  title: '',
  type: '系统通知',
  content: ''
})

const rules = {
  employeeId: [{ required: true, message: '请选择接收人', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const loadEmployees = async () => {
  try {
    const res = await getEmployeeList({ pageNum: 1, pageSize: 1000 })
    if (res && res.code === 200 && res.data) {
      employeeList.value = res.data.records || []
    }
  } catch (error) {
    console.error(error)
  }
}

const loadHistory = async () => {
  historyLoading.value = true
  try {
    const res = await getAllNotifications()
    if (res && res.code === 200 && res.data) {
      const raw = res.data.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      const grouped = {}
      for (const n of raw) {
        const key = n.title + '||' + n.content + '||' + n.type
        if (!grouped[key]) {
          grouped[key] = {
            title: n.title,
            type: n.type,
            content: n.content,
            count: 0,
            readCount: 0,
            createTime: n.createTime
          }
        }
        grouped[key].count++
        if (n.status === '已读') {
          grouped[key].readCount++
        }
      }
      historyList.value = Object.values(grouped).slice(0, 50)
    }
  } catch (error) {
    console.error(error)
  } finally {
    historyLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (form.employeeId === 0) {
        // 发送给全体员工，排除当前管理员
        const currentEmployeeId = userStore.userInfo?.employeeId
        let sendCount = 0
        for (const emp of employeeList.value) {
          if (emp.id !== currentEmployeeId) {
            await sendNotification({
              employeeId: emp.id,
              title: form.title,
              type: form.type,
              content: form.content
            })
            sendCount++
          }
        }
        ElMessage.success(`已发送给 ${sendCount} 名员工`)
      } else {
        await sendNotification(form)
        ElMessage.success('发送成功')
      }
      handleReset()
      loadHistory()
    } catch (error) {
      console.error(error)
      ElMessage.error('发送失败')
    } finally {
      loading.value = false
    }
  })
}

const handleReset = () => {
  form.employeeId = ''
  form.title = ''
  form.type = '系统通知'
  form.content = ''
  formRef.value?.resetFields()
}

onMounted(() => {
  loadEmployees().then(() => loadHistory())
})
</script>

<style scoped>
.notify-card,
.history-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.notify-form {
  max-width: 600px;
}
</style>
