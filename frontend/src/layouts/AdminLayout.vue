<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-sidebar__brand">
        <strong>管理后台</strong>
        <span>校园书籍循环共享平台</span>
      </div>
      <el-menu :default-active="route.path" router class="admin-menu">
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/books">书籍审核</el-menu-item>
        <el-menu-item index="/admin/categories">分类管理</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
        <el-menu-item index="/admin/evaluations">评价管理</el-menu-item>
        <el-menu-item index="/admin/reports">举报管理</el-menu-item>
      </el-menu>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <div>
          <p class="admin-topbar__eyebrow">后台管理中心</p>
          <h1>{{ pageTitle }}</h1>
        </div>
        <div class="admin-topbar__actions">
          <div class="admin-user">
            <strong>{{ userStore.profile ? userStore.profile.username : '管理员' }}</strong>
            <span>角色：ADMIN</span>
          </div>
          <el-button @click="router.push('/')">返回前台首页</el-button>
          <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
        </div>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const titleMap = {
  '/admin/users': '用户管理',
  '/admin/books': '书籍审核',
  '/admin/categories': '分类管理',
  '/admin/orders': '订单管理',
  '/admin/evaluations': '评价管理',
  '/admin/reports': '举报管理'
}

const pageTitle = computed(function () {
  return titleMap[route.path] || '管理后台'
})

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>
