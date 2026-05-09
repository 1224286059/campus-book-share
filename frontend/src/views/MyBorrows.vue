<template>
  <div class="page-wrap">
    <section class="page-heading">
      <div>
        <p class="page-heading__eyebrow">我的借阅</p>
        <h1>借阅记录与归还状态</h1>
      </div>
    </section>

    <el-card shadow="never" class="section-card">
      <el-table v-loading="loading" :data="records" empty-text="暂无借阅记录">
        <el-table-column prop="bookTitle" label="书籍名称" min-width="200" />
        <el-table-column prop="lenderUsername" label="出借人" min-width="120" />
        <el-table-column label="借阅时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.borrowTime) }}</template>
        </el-table-column>
        <el-table-column label="应还时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.expectedReturnTime) }}</template>
        </el-table-column>
        <el-table-column label="实际归还时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.actualReturnTime) }}</template>
        </el-table-column>
        <el-table-column label="借阅状态" min-width="120">
          <template #default="{ row }">{{ borrowStatusMap[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.status === 'BORROWING'" link type="success" @click="handleReturn(row)">归还</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBorrows, returnBorrow } from '../api/borrow'
import { borrowStatusMap } from '../utils/constants'
import { formatDateTime } from '../utils/format'

const loading = ref(false)
const records = ref([])

function loadBorrows() {
  loading.value = true
  getMyBorrows()
    .then(function (data) {
      records.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function handleReturn(row) {
  ElMessageBox.confirm('确认已归还这本书吗？', '提示', { type: 'warning' })
    .then(function () {
      return returnBorrow(row.id)
    })
    .then(function () {
      ElMessage.success('归还成功')
      loadBorrows()
    })
    .catch(function () {})
}

onMounted(loadBorrows)
</script>
