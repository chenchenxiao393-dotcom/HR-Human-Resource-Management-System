import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

function getStoredUserInfo() {
  try {
    const stored = localStorage.getItem('userInfo')
    if (stored && stored !== 'undefined' && stored !== 'null') {
      const parsed = JSON.parse(stored)
      if (parsed && typeof parsed === 'object' && parsed.role) {
        return parsed
      }
    }
  } catch (e) {
    console.error('解析用户信息失败:', e)
  }
  return {}
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(getStoredUserInfo())

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isHR = computed(() => userInfo.value?.role === 'HR' || isAdmin.value)

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    if (info && info.role) {
      userInfo.value = info
      localStorage.setItem('userInfo', JSON.stringify(info))
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isHR,
    setToken,
    setUserInfo,
    logout
  }
})
