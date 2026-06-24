<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 应用启动时清除登录状态，强制用户重新登录
onMounted(() => {
  userStore.logout()
})
</script>
