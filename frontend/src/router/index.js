import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue')
  },
  {
    path: '/change-password',
    name: 'ChangePasswordGlobal',
    component: () => import('@/views/password/ChangePassword.vue'),
    meta: { title: '修改密码' }
  },
  {
    path: '/admin',
    component: () => import('@/views/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { roles: ['ADMIN', 'HR'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '工作台', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'personnel',
        name: 'Personnel',
        component: () => import('@/views/personnel/Personnel.vue'),
        meta: { title: '人员管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/department/Department.vue'),
        meta: { title: '部门管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: () => import('@/views/contract/Contract.vue'),
        meta: { title: '合同管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/views/attendance/Attendance.vue'),
        meta: { title: '考勤管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'overtime',
        name: 'Overtime',
        component: () => import('@/views/overtime/Overtime.vue'),
        meta: { title: '加班管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'business-trip',
        name: 'BusinessTrip',
        component: () => import('@/views/businessTrip/BusinessTrip.vue'),
        meta: { title: '出差管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'personnel-change',
        name: 'PersonnelChange',
        component: () => import('@/views/personnelChange/PersonnelChange.vue'),
        meta: { title: '人事变动', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'resignation',
        name: 'Resignation',
        component: () => import('@/views/resignation/Resignation.vue'),
        meta: { title: '离职管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'training',
        name: 'Training',
        component: () => import('@/views/training/AdminTraining.vue'),
        meta: { title: '培训管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'appraisal',
        name: 'Appraisal',
        component: () => import('@/views/appraisal/Appraisal.vue'),
        meta: { title: '考核管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'reward-punishment',
        name: 'RewardPunishment',
        component: () => import('@/views/rewardPunishment/RewardPunishment.vue'),
        meta: { title: '奖惩管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('@/views/system/System.vue'),
        meta: { title: '系统维护', roles: ['ADMIN'] }
      },
      {
        path: 'task',
        name: 'AdminTask',
        component: () => import('@/views/task/AdminTask.vue'),
        meta: { title: '任务管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'notification',
        name: 'AdminNotification',
        component: () => import('@/views/notification/AdminNotification.vue'),
        meta: { title: '通知管理', roles: ['ADMIN'] }
      }
    ]
  },
  {
    path: '/user',
    component: () => import('@/views/layout/UserLayout.vue'),
    redirect: '/user/dashboard',
    meta: { roles: ['EMPLOYEE'] },
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'employee',
        name: 'UserEmployee',
        component: () => import('@/views/employee/Employee.vue'),
        meta: { title: '员工信息' }
      },
      {
        path: 'department',
        name: 'UserDepartment',
        component: () => import('@/views/department/Department.vue'),
        meta: { title: '部门结构' }
      },
      {
        path: 'contract',
        name: 'UserContract',
        component: () => import('@/views/contract/Contract.vue'),
        meta: { title: '合同信息' }
      },
      {
        path: 'attendance',
        name: 'UserAttendance',
        component: () => import('@/views/attendance/Attendance.vue'),
        meta: { title: '考勤记录' }
      },
      {
        path: 'overtime',
        name: 'UserOvertime',
        component: () => import('@/views/overtime/Overtime.vue'),
        meta: { title: '加班申请' }
      },
      {
        path: 'business-trip',
        name: 'UserBusinessTrip',
        component: () => import('@/views/businessTrip/BusinessTrip.vue'),
        meta: { title: '出差申请' }
      },
      {
        path: 'personnel-change',
        name: 'UserPersonnelChange',
        component: () => import('@/views/personnelChange/PersonnelChange.vue'),
        meta: { title: '人事变动' }
      },
      {
        path: 'resignation',
        name: 'UserResignation',
        component: () => import('@/views/resignation/Resignation.vue'),
        meta: { title: '离职管理' }
      },
      {
        path: 'training',
        name: 'UserTraining',
        component: () => import('@/views/training/Training.vue'),
        meta: { title: '培训管理' }
      },
      {
        path: 'appraisal',
        name: 'UserAppraisal',
        component: () => import('@/views/appraisal/Appraisal.vue'),
        meta: { title: '考核记录' }
      },
      {
        path: 'reward-punishment',
        name: 'UserRewardPunishment',
        component: () => import('@/views/rewardPunishment/RewardPunishment.vue'),
        meta: { title: '奖惩记录' }
      },
      {
        path: 'salary',
        name: 'UserSalary',
        component: () => import('@/views/salary/Salary.vue'),
        meta: { title: '工资查询' }
      },
      {
        path: 'task',
        name: 'UserTask',
        component: () => import('@/views/task/Task.vue'),
        meta: { title: '任务管理' }
      },
      {
        path: 'change-password',
        name: 'ChangePassword',
        component: () => import('@/views/password/ChangePassword.vue'),
        meta: { title: '修改密码' }
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const userInfo = userStore.userInfo
  const userRole = userInfo?.role

  if (to.path === '/login') {
    if (token && userRole) {
      if (userRole === 'EMPLOYEE') {
        next('/user/dashboard')
      } else {
        next('/admin/dashboard')
      }
    } else {
      next()
    }
    return
  }

  if (!token) {
    next('/login')
    return
  }

  if (!userRole) {
    userStore.logout()
    next('/login')
    return
  }

  if (to.meta.roles && to.meta.roles.length > 0) {
    if (to.meta.roles.includes(userRole)) {
      next()
    } else {
      if (userRole === 'EMPLOYEE') {
        next('/user/dashboard')
      } else if (userRole === 'ADMIN' || userRole === 'HR') {
        next('/admin/dashboard')
      } else {
        next('/login')
      }
    }
  } else {
    next()
  }
})

export default router
