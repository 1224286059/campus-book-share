<template>
  <div class="page-wrap">
    <section class="page-heading">
      <div>
        <p class="page-heading__eyebrow">我的积分</p>
        <h1>查看共享行为带来的积分变化</h1>
      </div>
    </section>

    <div class="points-summary">
      <el-card shadow="never" class="points-card">
        <span>当前积分</span>
        <strong>{{ summary.points || 0 }}</strong>
      </el-card>
    </div>

    <el-card shadow="never" class="section-card">
      <el-table v-loading="loading" :data="records" empty-text="暂无积分记录">
        <el-table-column prop="pointsChange" label="积分变化" min-width="120" />
        <el-table-column prop="sourceType" label="来源类型" min-width="150" />
        <el-table-column prop="description" label="变化原因" min-width="280" />
        <el-table-column label="变化时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getMyPointRecords, getMyPoints } from '../api/point'
import { formatDateTime } from '../utils/format'

const loading = ref(false)
const summary = ref({})
const records = ref([])

function loadPoints() {
  loading.value = true
  Promise.all([getMyPoints(), getMyPointRecords()])
    .then(function (data) {
      summary.value = data[0] || {}
      records.value = data[1] || []
    })
    .finally(function () {
      loading.value = false
    })
}

onMounted(loadPoints)
</script>
