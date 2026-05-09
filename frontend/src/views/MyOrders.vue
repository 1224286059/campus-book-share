<template>
  <div class="page-wrap">
    <section class="page-heading">
      <div>
        <p class="page-heading__eyebrow">我的订单</p>
        <h1>跟踪申请与处理状态</h1>
      </div>
    </section>

    <el-card shadow="never" class="section-card">
      <el-tabs v-model="activeTab" @tab-change="loadOrders">
        <el-tab-pane label="我发起的订单" name="created" />
        <el-tab-pane label="我收到的订单" name="received" />
      </el-tabs>

      <el-table v-loading="loading" :data="tableData" empty-text="暂无订单数据">
        <el-table-column prop="id" label="订单编号" min-width="120" />
        <el-table-column prop="bookTitle" label="书籍名称" min-width="200" />
        <el-table-column label="订单类型" min-width="120">
          <template #default="{ row }">{{ orderTypeMap[row.orderType] || row.orderType }}</template>
        </el-table-column>
        <el-table-column label="订单状态" min-width="120">
          <template #default="{ row }">{{ orderStatusMap[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="260" fixed="right">
          <template #default="{ row }">
            <template v-if="activeTab === 'received'">
              <el-button v-if="row.status === 'PENDING'" link type="success" @click="handleReceivedAction('accept', row)">同意</el-button>
              <el-button v-if="row.status === 'PENDING'" link type="danger" @click="handleReceivedAction('reject', row)">拒绝</el-button>
              <el-button link type="primary" @click="router.push('/books/' + row.bookId)">查看书籍</el-button>
              <el-button link type="danger" @click="openReport(row)">举报订单</el-button>
            </template>
            <template v-else>
              <el-button v-if="row.status === 'PENDING'" link type="warning" @click="handleCreatedAction('cancel', row)">取消</el-button>
              <el-button v-if="row.status === 'ACCEPTED'" link type="success" @click="handleCreatedAction('complete', row)">确认完成</el-button>
              <el-button v-if="row.status === 'COMPLETED'" link type="primary" @click="openEvaluation(row)">去评价</el-button>
              <el-button link type="primary" @click="router.push('/books/' + row.bookId)">查看书籍</el-button>
              <el-button link type="danger" @click="openReport(row)">举报订单</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <EvaluationDialog
      v-model="evaluationVisible"
      :order="currentOrder"
      :current-user-id="userStore.profile ? userStore.profile.id : null"
      @success="loadOrders"
    />

    <ReportDialog
      v-if="currentOrder"
      v-model="reportVisible"
      target-type="ORDER"
      :target-id="currentOrder.id"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  acceptOrder,
  cancelOrder,
  completeOrder,
  getMyCreatedOrders,
  getMyReceivedOrders,
  rejectOrder
} from '../api/order'
import EvaluationDialog from '../components/EvaluationDialog.vue'
import ReportDialog from '../components/ReportDialog.vue'
import { useUserStore } from '../stores/user'
import { formatDateTime } from '../utils/format'
import { orderStatusMap, orderTypeMap } from '../utils/constants'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('created')
const loading = ref(false)
const createdOrders = ref([])
const receivedOrders = ref([])
const evaluationVisible = ref(false)
const reportVisible = ref(false)
const currentOrder = ref(null)

const tableData = computed(function () {
  return activeTab.value === 'created' ? createdOrders.value : receivedOrders.value
})

function loadOrders() {
  loading.value = true
  Promise.all([getMyCreatedOrders(), getMyReceivedOrders()])
    .then(function (data) {
      createdOrders.value = data[0] || []
      receivedOrders.value = data[1] || []
    })
    .finally(function () {
      loading.value = false
    })
}

function handleReceivedAction(action, row) {
  var request = action === 'accept' ? acceptOrder(row.id) : rejectOrder(row.id)
  request.then(function () {
    ElMessage.success(action === 'accept' ? '订单已同意' : '订单已拒绝')
    loadOrders()
  })
}

function handleCreatedAction(action, row) {
  var label = action === 'cancel' ? '取消' : '确认完成'
  ElMessageBox.confirm('确认执行“' + label + '”操作吗？', '提示', { type: 'warning' })
    .then(function () {
      return action === 'cancel' ? cancelOrder(row.id) : completeOrder(row.id)
    })
    .then(function () {
      ElMessage.success('操作成功')
      loadOrders()
    })
    .catch(function () {})
}

function openEvaluation(row) {
  currentOrder.value = row
  evaluationVisible.value = true
}

function openReport(row) {
  currentOrder.value = row
  reportVisible.value = true
}

onMounted(loadOrders)
</script>
