<template>
  <div class="auth-page">
    <div class="auth-panel">
      <div class="auth-hero">
        <span class="auth-badge">Campus Book Loop</span>
        <h1>让旧书继续被需要</h1>
        <p>登录后即可发布书籍、发起借阅、交换、捐赠领取，并追踪每一本书的流转过程。</p>
        <div class="auth-tags">
          <span>出售共享</span>
          <span>借阅共享</span>
          <span>交换共享</span>
          <span>捐赠共享</span>
          <span>再次共享</span>
        </div>
      </div>
      <el-card class="auth-card" shadow="never">
        <template #header>
          <div class="auth-card__header">
            <strong>用户登录</strong>
            <router-link to="/register">没有账号？去注册</router-link>
          </div>
        </template>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button type="primary" class="full-width" :loading="submitting" @click="submit">登录</el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const submitting = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function submit() {
  formRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    submitting.value = true
    userStore.login(form)
      .then(function () {
        ElMessage.success('登录成功')
        router.push(route.query.redirect || (userStore.isAdmin ? '/admin' : '/'))
      })
      .finally(function () {
        submitting.value = false
      })
    return true
  })
}
</script>
