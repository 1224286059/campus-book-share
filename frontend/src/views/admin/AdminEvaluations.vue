<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-panel">
      <el-table v-loading="loading" :data="evaluations" empty-text="暂无评价数据">
        <el-table-column prop="evaluatorUsername" label="评价用户" min-width="120" />
        <el-table-column prop="targetUsername" label="被评价用户" min-width="120" />
        <el-table-column prop="orderId" label="订单编号" min-width="100" />
        <el-table-column prop="bookScore" label="书籍评分" min-width="100" />
        <el-table-column prop="userScore" label="用户评分" min-width="100" />
        <el-table-column prop="content" label="评价内容" min-width="260" />
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteEvaluation, getAdminEvaluations } from '../../api/admin'
import { formatDateTime } from '../../utils/format'

const loading = ref(false)
const evaluations = ref([])

function loadEvaluations() {
  loading.value = true
  getAdminEvaluations()
    .then(function (data) {
      evaluations.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function handleDelete(row) {
  ElMessageBox.confirm('确认删除这条评价吗？', '提示', { type: 'warning' })
    .then(function () {
      return deleteEvaluation(row.id)
    })
    .then(function () {
      ElMessage.success('删除成功')
      loadEvaluations()
    })
    .catch(function () {})
}

onMounted(loadEvaluations)
</script>
