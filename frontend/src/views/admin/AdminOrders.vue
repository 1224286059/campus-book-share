<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-panel">
      <div class="admin-toolbar admin-toolbar--wrap">
        <el-select v-model="filters.orderType" placeholder="按订单类型筛选" clearable class="admin-toolbar__input">
          <el-option label="购买" value="SALE" />
          <el-option label="借阅" value="BORROW" />
          <el-option label="交换" value="EXCHANGE" />
          <el-option label="捐赠领取" value="DONATE" />
        </el-select>
        <el-select v-model="filters.status" placeholder="按订单状态筛选" clearable class="admin-toolbar__input">
          <el-option label="待确认" value="PENDING" />
          <el-option label="进行中" value="ACCEPTED" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="loadOrders">筛选</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="orders" empty-text="暂无订单数据">
        <el-table-column prop="id" label="订单编号" min-width="110" />
        <el-table-column prop="bookTitle" label="书籍名称" min-width="180" />
        <el-table-column prop="ownerUsername" label="发布者" min-width="110" />
        <el-table-column prop="applicantUsername" label="申请者" min-width="110" />
        <el-table-column label="订单类型" min-width="110">
          <template #default="{ row }">{{ orderTypeMap[row.orderType] || row.orderType }}</template>
        </el-table-column>
        <el-table-column label="订单状态" min-width="110">
          <template #default="{ row }">{{ orderStatusMap[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="完成时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.finishTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewOrder(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="640px">
      <div v-if="currentOrder" class="admin-detail-list">
        <p>订单编号：{{ currentOrder.id }}</p>
        <p>书籍名称：{{ currentOrder.bookTitle }}</p>
        <p>发布者：{{ currentOrder.ownerUsername }}</p>
        <p>申请者：{{ currentOrder.applicantUsername }}</p>
        <p>订单类型：{{ orderTypeMap[currentOrder.orderType] || currentOrder.orderType }}</p>
        <p>订单状态：{{ orderStatusMap[currentOrder.status] || currentOrder.status }}</p>
        <p>交换书籍：{{ currentOrder.exchangeBookTitle || '-' }}</p>
        <p>备注：{{ currentOrder.remark || '无' }}</p>
        <p>创建时间：{{ formatDateTime(currentOrder.createTime) }}</p>
        <p>完成时间：{{ formatDateTime(currentOrder.finishTime) }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getAdminOrders } from '../../api/admin'
import { formatDateTime } from '../../utils/format'
import { orderStatusMap, orderTypeMap } from '../../utils/constants'

const loading = ref(false)
const orders = ref([])
const detailVisible = ref(false)
const currentOrder = ref(null)
const filters = reactive({
  orderType: '',
  status: ''
})

function loadOrders() {
  loading.value = true
  getAdminOrders(filters)
    .then(function (data) {
      orders.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function resetFilters() {
  filters.orderType = ''
  filters.status = ''
  loadOrders()
}

function viewOrder(row) {
  currentOrder.value = row
  detailVisible.value = true
}

onMounted(loadOrders)
</script>
