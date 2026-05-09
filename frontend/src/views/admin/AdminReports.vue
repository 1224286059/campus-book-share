<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-panel">
      <el-table v-loading="loading" :data="reports" empty-text="暂无举报数据">
        <el-table-column prop="reporterUsername" label="举报人" min-width="120" />
        <el-table-column label="对象类型" min-width="110">
          <template #default="{ row }">{{ reportTargetTypeMap[row.targetType] || row.targetType }}</template>
        </el-table-column>
        <el-table-column prop="targetId" label="对象编号" min-width="100" />
        <el-table-column prop="reason" label="举报原因" min-width="260" />
        <el-table-column label="处理状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PROCESSED' ? 'success' : 'warning'">
              {{ row.status === 'PROCESSED' ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="处理时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.handleTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" link type="primary" @click="handleProcess(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminReports, processReport } from '../../api/admin'
import { reportTargetTypeMap } from '../../utils/constants'
import { formatDateTime } from '../../utils/format'

const loading = ref(false)
const reports = ref([])

function loadReports() {
  loading.value = true
  getAdminReports()
    .then(function (data) {
      reports.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function handleProcess(row) {
  processReport(row.id)
    .then(function () {
      ElMessage.success('举报处理成功')
      loadReports()
    })
}

onMounted(loadReports)
</script>
