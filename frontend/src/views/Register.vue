<template>
  <div class="auth-page">
    <div class="auth-panel auth-panel--register">
      <div class="auth-hero">
        <span class="auth-badge">Join The Loop</span>
        <h1>加入校园旧书循环计划</h1>
        <p>完成注册后，你可以发布闲置教材、申请领取捐赠书籍，并通过再次共享让知识持续流转。</p>
      </div>
      <el-card class="auth-card" shadow="never">
        <template #header>
          <div class="auth-card__header">
            <strong>用户注册</strong>
            <router-link to="/login">已有账号？去登录</router-link>
          </div>
        </template>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" placeholder="请输入用户名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="常用联系地址" prop="address">
                <el-input v-model="form.address" placeholder="请输入常用联系地址，如南校区3号宿舍楼" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学院" prop="college">
                <el-input v-model="form.college" placeholder="如：信息工程学院" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业" prop="major">
                <el-input v-model="form.major" placeholder="如：计算机科学与技术" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年级" prop="grade">
                <el-input v-model="form.grade" placeholder="如：2022级" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-button type="primary" class="full-width" :loading="submitting" @click="submit">注册并返回登录</el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const form = reactive({
  username: '',
  password: '',
  phone: '',
  address: '',
  college: '',
  major: '',
  grade: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于 6 位', trigger: 'blur' }
  ],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  address: [{ max: 255, message: '常用联系地址不能超过 255 个字符', trigger: 'blur' }],
  college: [{ required: true, message: '请输入学院', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  grade: [{ required: true, message: '请输入年级', trigger: 'blur' }]
}

function submit() {
  formRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    submitting.value = true
    register(form)
      .then(function () {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      })
      .finally(function () {
        submitting.value = false
      })
    return true
  })
}
</script>
