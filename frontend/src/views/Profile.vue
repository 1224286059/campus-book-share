<template>
  <div class="page-wrap">
    <section class="page-heading">
      <div>
        <p class="page-heading__eyebrow">个人中心</p>
        <h1>维护个人资料与账号安全</h1>
      </div>
    </section>

    <div class="profile-grid">
      <el-card shadow="never" class="section-card">
        <template #header>
          <strong>基本资料</strong>
        </template>
        <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="90px">
          <el-form-item label="用户名">
            <el-input :model-value="userStore.profile ? userStore.profile.username : ''" disabled />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="profileForm.phone" />
          </el-form-item>
          <el-form-item label="常用地址" prop="address">
            <el-input v-model="profileForm.address" placeholder="请输入常用联系地址，如南校区3号宿舍楼" />
          </el-form-item>
          <el-form-item label="学院" prop="college">
            <el-input v-model="profileForm.college" />
          </el-form-item>
          <el-form-item label="专业" prop="major">
            <el-input v-model="profileForm.major" />
          </el-form-item>
          <el-form-item label="年级" prop="grade">
            <el-input v-model="profileForm.grade" />
          </el-form-item>
          <el-button type="primary" :loading="profileSubmitting" @click="submitProfile">保存资料</el-button>
        </el-form>
      </el-card>

      <el-card shadow="never" class="section-card">
        <template #header>
          <strong>修改密码</strong>
        </template>
        <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="90px">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-button type="primary" :loading="passwordSubmitting" @click="submitPassword">修改密码</el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { updatePassword, updateProfile } from '../api/user'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const profileSubmitting = ref(false)
const passwordSubmitting = ref(false)
const profileForm = reactive({
  phone: '',
  address: '',
  college: '',
  major: '',
  grade: ''
})
const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const profileRules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  address: [{ max: 255, message: '常用联系地址不能超过 255 个字符', trigger: 'blur' }],
  college: [{ required: true, message: '请输入学院', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  grade: [{ required: true, message: '请输入年级', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少 6 位', trigger: 'blur' }
  ]
}

watch(
  function () {
    return userStore.profile
  },
  function (profile) {
    if (!profile) {
      return
    }
    profileForm.phone = profile.phone || ''
    profileForm.address = profile.address || ''
    profileForm.college = profile.college || ''
    profileForm.major = profile.major || ''
    profileForm.grade = profile.grade || ''
  },
  { immediate: true }
)

function submitProfile() {
  profileFormRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    profileSubmitting.value = true
    updateProfile(profileForm)
      .then(function (data) {
        userStore.setProfile(data)
        ElMessage.success('资料已更新')
      })
      .finally(function () {
        profileSubmitting.value = false
      })
    return true
  })
}

function submitPassword() {
  passwordFormRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    passwordSubmitting.value = true
    updatePassword(passwordForm)
      .then(function () {
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        ElMessage.success('密码修改成功，请牢记新密码')
      })
      .finally(function () {
        passwordSubmitting.value = false
      })
    return true
  })
}
</script>
