<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-panel">
      <div class="admin-toolbar admin-toolbar--wrap">
        <el-select v-model="filters.status" placeholder="按状态筛选" clearable class="admin-toolbar__input">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已上架" value="ON_SHELF" />
          <el-option label="共享中" value="SHARING" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已下架" value="OFF_SHELF" />
          <el-option label="审核驳回" value="REJECTED" />
        </el-select>
        <el-select v-model="filters.shareType" placeholder="按共享方式筛选" clearable class="admin-toolbar__input">
          <el-option v-for="item in shareTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-input v-model="filters.keyword" placeholder="按书名 / 作者搜索" clearable class="admin-toolbar__input" />
        <el-button type="primary" @click="loadBooks">筛选</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>

      <el-tabs v-model="tab" @tab-change="handleTabChange">
        <el-tab-pane label="待审核书籍" name="pending" />
        <el-tab-pane label="全部书籍" name="all" />
      </el-tabs>

      <el-table v-loading="loading" :data="books" empty-text="暂无书籍数据">
        <el-table-column label="封面" width="96">
          <template #default="{ row }">
            <img v-if="row.coverUrl" :src="row.coverUrl" class="admin-book-cover" :alt="row.title" />
            <div v-else class="admin-book-cover admin-book-cover--empty">无图</div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="180" />
        <el-table-column prop="ownerUsername" label="发布用户" min-width="120" />
        <el-table-column prop="categoryName" label="分类" min-width="100" />
        <el-table-column label="共享方式" min-width="110">
          <template #default="{ row }">
            <el-tag :type="shareTypeTagMap[row.shareType] || 'info'">{{ shareTypeMap[row.shareType] || row.shareType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="110">
          <template #default="{ row }">
            <el-tag>{{ bookStatusMap[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bookLocation" label="书籍位置" min-width="180" show-overflow-tooltip />
        <el-table-column prop="major" label="专业" min-width="130" />
        <el-table-column prop="courseName" label="课程" min-width="140" />
        <el-table-column label="流转次数" min-width="90">
          <template #default="{ row }">{{ row.circulationCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewBook(row)">详情</el-button>
            <el-button v-if="row.status === 'PENDING'" link type="success" @click="changeStatus('approve', row)">通过</el-button>
            <el-button v-if="row.status === 'PENDING'" link type="danger" @click="changeStatus('reject', row)">驳回</el-button>
            <el-button v-if="row.status === 'ON_SHELF'" link type="warning" @click="changeStatus('off', row)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="书籍详情" width="700px">
      <div v-if="currentBook" class="admin-book-detail">
        <img v-if="currentBook.coverUrl" :src="currentBook.coverUrl" class="admin-book-detail__cover" :alt="currentBook.title" />
        <div class="admin-book-detail__info">
          <h3>{{ currentBook.title }}</h3>
          <p>发布者：{{ currentBook.ownerUsername }}</p>
          <p>共享方式：{{ shareTypeMap[currentBook.shareType] || currentBook.shareType }}</p>
          <p>状态：{{ bookStatusMap[currentBook.status] || currentBook.status }}</p>
          <p>书籍位置：{{ currentBook.bookLocation || '待线下沟通' }}</p>
          <p>专业：{{ currentBook.major || '-' }}</p>
          <p>课程：{{ currentBook.courseName || '-' }}</p>
          <p>品相：{{ currentBook.conditionLevel || '-' }}</p>
          <p>描述：{{ currentBook.description || '暂无描述' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminOffShelfBook, approveBook, getAdminBooks, getPendingBooks, rejectBook } from '../../api/admin'
import { bookStatusMap, shareTypeMap, shareTypeOptions, shareTypeTagMap } from '../../utils/constants'

const loading = ref(false)
const books = ref([])
const tab = ref('pending')
const detailVisible = ref(false)
const currentBook = ref(null)
const filters = reactive({
  status: '',
  shareType: '',
  keyword: ''
})

function loadBooks() {
  loading.value = true
  var request = tab.value === 'pending'
    ? getPendingBooks()
    : getAdminBooks({
      status: filters.status,
      shareType: filters.shareType,
      keyword: filters.keyword
    })
  request
    .then(function (data) {
      books.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function handleTabChange() {
  if (tab.value === 'pending') {
    filters.status = ''
  }
  loadBooks()
}

function resetFilters() {
  filters.status = ''
  filters.shareType = ''
  filters.keyword = ''
  loadBooks()
}

function viewBook(row) {
  currentBook.value = row
  detailVisible.value = true
}

function changeStatus(action, row) {
  var actionTextMap = {
    approve: '通过',
    reject: '驳回',
    off: '下架'
  }
  ElMessageBox.confirm('确认' + actionTextMap[action] + '书籍“' + row.title + '”吗？', '提示', { type: 'warning' })
    .then(function () {
      if (action === 'approve') {
        return approveBook(row.id)
      }
      if (action === 'reject') {
        return rejectBook(row.id)
      }
      return adminOffShelfBook(row.id)
    })
    .then(function () {
      ElMessage.success('操作成功')
      loadBooks()
    })
    .catch(function () {})
}

onMounted(loadBooks)
</script>
