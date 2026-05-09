<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-panel">
      <div class="admin-toolbar">
        <el-input v-model="filters.username" placeholder="按用户名搜索" clearable class="admin-toolbar__input" />
        <el-button type="primary" @click="loadUsers">搜索</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="users" empty-text="暂无用户数据">
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="college" label="学院" min-width="180" />
        <el-table-column prop="major" label="专业" min-width="160" />
        <el-table-column prop="grade" label="年级" min-width="100" />
        <el-table-column prop="points" label="积分" min-width="90" />
        <el-table-column prop="creditScore" label="信用分" min-width="90" />
        <el-table-column label="角色" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">{{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '已禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              link
              type="danger"
              :disabled="row.role === 'ADMIN'"
              @click="updateStatus(row, 0)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              link
              type="success"
              :disabled="row.role === 'ADMIN'"
              @click="updateStatus(row, 1)"
            >
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { disableAdminUser, enableAdminUser, getAdminUsers } from '../../api/admin'

const loading = ref(false)
const users = ref([])
const filters = reactive({
  username: ''
})

function loadUsers() {
  loading.value = true
  getAdminUsers(filters)
    .then(function (data) {
      users.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function resetFilters() {
  filters.username = ''
  loadUsers()
}

function updateStatus(row, status) {
  var actionText = status === 1 ? '恢复' : '禁用'
  ElMessageBox.confirm('确认' + actionText + '用户“' + row.username + '”吗？', '提示', { type: 'warning' })
    .then(function () {
      return status === 1 ? enableAdminUser(row.id) : disableAdminUser(row.id)
    })
    .then(function () {
      ElMessage.success(actionText + '成功')
      loadUsers()
    })
    .catch(function () {})
}

onMounted(loadUsers)
</script>
