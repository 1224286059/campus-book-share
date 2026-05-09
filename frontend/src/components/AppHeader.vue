<template>
  <header class="app-header">
    <div class="app-header__inner">
      <router-link to="/" class="brand">
        <span class="brand__badge">BOOK LOOP</span>
        <div>
          <strong>校园二手书籍循环共享平台</strong>
          <small>出售 / 借阅 / 交换 / 捐赠 / 再次共享</small>
        </div>
      </router-link>

      <nav class="main-nav">
        <router-link to="/">首页</router-link>
        <router-link v-if="userStore.isLoggedIn && !userStore.isAdmin" to="/books/publish">发布书籍</router-link>
        <router-link v-if="userStore.isLoggedIn && !userStore.isAdmin" to="/my/books">我的发布</router-link>
        <router-link v-if="userStore.isLoggedIn && !userStore.isAdmin" to="/my/owned-books">我的持有书籍</router-link>
        <router-link v-if="userStore.isLoggedIn && !userStore.isAdmin" to="/my/orders">我的订单</router-link>
        <router-link v-if="userStore.isLoggedIn && !userStore.isAdmin" to="/my/borrows">我的借阅</router-link>
        <router-link v-if="userStore.isLoggedIn && !userStore.isAdmin" to="/my/points">我的积分</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin">管理后台入口</router-link>
      </nav>

      <div class="header-actions">
        <template v-if="userStore.isLoggedIn">
          <router-link to="/profile" class="profile-link">
            {{ userStore.profile ? userStore.profile.username : '个人中心' }}
          </router-link>
          <el-button type="danger" plain @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <router-link to="/login">
            <el-button type="primary">登录</el-button>
          </router-link>
          <router-link to="/register">
            <el-button plain>注册</el-button>
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>
