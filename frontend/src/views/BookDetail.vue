<template>
  <div class="page-wrap" v-loading="loading">
    <div v-if="book" class="detail-layout">
      <section class="detail-main">
        <div class="detail-cover">
          <img v-if="book.coverUrl" :src="book.coverUrl" :alt="book.title" />
          <div v-else class="detail-cover__fallback">{{ shareLabel }}</div>
        </div>
        <div class="detail-info">
          <div class="detail-tags">
            <el-tag :type="shareTagType" effect="dark">{{ shareLabel }}</el-tag>
            <el-tag>{{ statusLabel }}</el-tag>
            <span class="detail-count">流转 {{ book.circulationCount || 0 }} 次</span>
          </div>
          <h1>{{ book.title }}</h1>
          <p class="detail-subtitle">{{ book.author || '作者未填写' }} · {{ book.publisher || '出版社未填写' }}</p>
          <div class="detail-grid">
            <div><span>分类</span><strong>{{ book.categoryName || '-' }}</strong></div>
            <div><span>专业</span><strong>{{ book.major || '-' }}</strong></div>
            <div><span>课程</span><strong>{{ book.courseName || '-' }}</strong></div>
            <div><span>品相</span><strong>{{ book.conditionLevel || '-' }}</strong></div>
            <div><span>发布者</span><strong>{{ book.ownerUsername || '-' }}</strong></div>
            <div><span>价格说明</span><strong>{{ actionText }}</strong></div>
          </div>
          <div class="detail-description">
            <h3>书籍描述</h3>
            <p>{{ book.description || '发布者暂未填写描述。' }}</p>
          </div>
          <div class="detail-actions">
            <el-button type="primary" :disabled="isOwnBook" @click="openOrderDialog">{{ actionButtonText }}</el-button>
            <el-button v-if="userStore.isLoggedIn && !isOwnBook" type="danger" plain @click="showReportDialog = true">举报书籍</el-button>
            <span v-if="isOwnBook" class="detail-tip">这是你当前持有或发布的书籍，不能对自己发起申请。</span>
          </div>
        </div>
      </section>

      <section class="detail-side">
        <el-card shadow="never" class="detail-side__card">
          <template #header>
            <strong>发布者信息</strong>
          </template>
          <p>用户名：{{ book.ownerUsername || '-' }}</p>
          <p>共享方式：{{ shareLabel }}</p>
          <p>当前状态：{{ statusLabel }}</p>
          <p>发布时间：{{ formatDateTime(book.createTime) }}</p>
        </el-card>
      </section>
    </div>

    <section class="section-card">
      <div class="section-card__header">
        <h2>书籍评价</h2>
      </div>
      <div v-if="evaluations.length" class="evaluation-list">
        <el-card v-for="item in evaluations" :key="item.id" shadow="never" class="evaluation-item">
          <div class="evaluation-item__header">
            <strong>{{ item.evaluatorUsername }}</strong>
            <span>{{ formatDateTime(item.createTime) }}</span>
          </div>
          <div class="evaluation-item__scores">
            <span>书籍评分：{{ item.bookScore }}/5</span>
            <span>用户评分：{{ item.userScore }}/5</span>
          </div>
          <p>{{ item.content || '该评价未填写文字内容。' }}</p>
        </el-card>
      </div>
      <el-empty v-else description="这本书暂时还没有评价" />
    </section>

    <el-dialog v-model="orderDialogVisible" :title="actionButtonText" width="560px">
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="110px">
        <el-form-item v-if="book && book.shareType === 'EXCHANGE'" label="交换书籍" prop="exchangeBookId">
          <el-select v-model="orderForm.exchangeBookId" placeholder="请选择我的一本书">
            <el-option
              v-for="item in exchangeOptions"
              :key="item.id"
              :label="item.title + '（' + (item.shareType || '-') + '）'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="book && book.shareType === 'BORROW'" label="预计归还时间" prop="expectedReturnTime">
          <el-date-picker
            v-model="orderForm.expectedReturnTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择预计归还时间"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.remark" type="textarea" :rows="4" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submittingOrder" @click="submitOrder">提交申请</el-button>
      </template>
    </el-dialog>

    <ReportDialog
      v-if="book"
      v-model="showReportDialog"
      target-type="BOOK"
      :target-id="book.id"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getBookDetail, getMyOwnedBooks } from '../api/book'
import { getBookEvaluations } from '../api/evaluation'
import { createOrder } from '../api/order'
import { useUserStore } from '../stores/user'
import { bookStatusMap, shareTypeMap, shareTypeTagMap } from '../utils/constants'
import { formatDateTime } from '../utils/format'
import ReportDialog from '../components/ReportDialog.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const book = ref(null)
const evaluations = ref([])
const exchangeOptions = ref([])
const orderDialogVisible = ref(false)
const showReportDialog = ref(false)
const submittingOrder = ref(false)
const orderFormRef = ref()
const orderForm = reactive({
  exchangeBookId: null,
  expectedReturnTime: '',
  remark: ''
})

const orderRules = {
  exchangeBookId: [{ required: true, message: '请选择交换书籍', trigger: 'change' }],
  expectedReturnTime: [{ required: true, message: '请选择预计归还时间', trigger: 'change' }]
}

const shareLabel = computed(function () {
  return shareTypeMap[book.value ? book.value.shareType : ''] || (book.value ? book.value.shareType : '')
})

const shareTagType = computed(function () {
  return shareTypeTagMap[book.value ? book.value.shareType : ''] || 'info'
})

const statusLabel = computed(function () {
  return bookStatusMap[book.value ? book.value.status : ''] || (book.value ? book.value.status : '')
})

const isOwnBook = computed(function () {
  return !!(userStore.profile && book.value && userStore.profile.id === book.value.ownerId)
})

const actionButtonText = computed(function () {
  if (!book.value) {
    return '申请'
  }
  if (book.value.shareType === 'SALE') {
    return '立即购买'
  }
  if (book.value.shareType === 'BORROW') {
    return '申请借阅'
  }
  if (book.value.shareType === 'EXCHANGE') {
    return '申请交换'
  }
  return '申请领取'
})

const actionText = computed(function () {
  if (!book.value) {
    return '-'
  }
  if (book.value.shareType === 'DONATE') {
    return '免费领取'
  }
  if (book.value.shareType === 'EXCHANGE') {
    return '交换一本自己的书'
  }
  if (book.value.shareType === 'BORROW') {
    return '按时归还'
  }
  return '¥' + Number(book.value.price || 0).toFixed(2)
})

function loadBookDetail() {
  loading.value = true
  return Promise.all([
    getBookDetail(route.params.id),
    getBookEvaluations(route.params.id)
  ])
    .then(function (results) {
      book.value = results[0]
      evaluations.value = results[1] || []
    })
    .finally(function () {
      loading.value = false
    })
}

function openOrderDialog() {
  if (!userStore.isLoggedIn) {
    router.push({
      path: '/login',
      query: {
        redirect: route.fullPath
      }
    })
    return
  }
  if (userStore.isAdmin) {
    ElMessage.warning('管理员账号不能发起共享申请，请使用普通用户账号操作')
    return
  }
  if (isOwnBook.value) {
    ElMessage.warning('不能申请自己的书籍')
    return
  }
  orderForm.exchangeBookId = null
  orderForm.expectedReturnTime = ''
  orderForm.remark = ''
  if (book.value.shareType === 'EXCHANGE') {
    getMyOwnedBooks().then(function (data) {
      exchangeOptions.value = (data || []).filter(function (item) {
        return item.id !== book.value.id
      })
      orderDialogVisible.value = true
    })
    return
  }
  orderDialogVisible.value = true
}

function submitOrder() {
  orderFormRef.value.validate(function (valid) {
    if (book.value.shareType === 'SALE' || book.value.shareType === 'DONATE') {
      valid = true
    }
    if (!valid) {
      return false
    }
    submittingOrder.value = true
    createOrder({
      bookId: book.value.id,
      orderType: book.value.shareType,
      exchangeBookId: orderForm.exchangeBookId,
      expectedReturnTime: orderForm.expectedReturnTime || null,
      remark: orderForm.remark
    })
      .then(function () {
        ElMessage.success('申请已提交')
        orderDialogVisible.value = false
      })
      .finally(function () {
        submittingOrder.value = false
      })
    return true
  })
}

onMounted(function () {
  loadBookDetail()
})
</script>
