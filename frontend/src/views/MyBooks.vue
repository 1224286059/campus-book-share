<template>
  <div class="page-wrap">
    <section class="page-heading">
      <div>
        <p class="page-heading__eyebrow">我的发布</p>
        <h1>查看审核状态与共享进度</h1>
      </div>
      <el-button type="primary" @click="router.push('/books/publish')">继续发布</el-button>
    </section>

    <el-card shadow="never" class="section-card">
      <el-table v-loading="loading" :data="books" empty-text="你还没有发布过书籍">
        <el-table-column prop="title" label="书名" min-width="220" />
        <el-table-column label="共享方式" min-width="120">
          <template #default="{ row }">{{ shareTypeMap[row.shareType] || row.shareType }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="120">
          <template #default="{ row }">{{ bookStatusMap[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column label="发布时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push('/books/' + row.id)">查看详情</el-button>
            <el-button
              v-if="['PENDING', 'ON_SHELF', 'SHARING'].indexOf(row.status) > -1"
              link
              type="danger"
              @click="handleOffShelf(row)"
            >
              下架
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getMyPublishedBooks, offShelfBook } from '../api/book'
import { bookStatusMap, shareTypeMap } from '../utils/constants'
import { formatDateTime } from '../utils/format'

const router = useRouter()
const loading = ref(false)
const books = ref([])

function loadBooks() {
  loading.value = true
  getMyPublishedBooks()
    .then(function (data) {
      books.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function handleOffShelf(row) {
  ElMessageBox.confirm('确认下架这本书吗？', '提示', { type: 'warning' })
    .then(function () {
      return offShelfBook(row.id)
    })
    .then(function () {
      ElMessage.success('下架成功')
      loadBooks()
    })
    .catch(function () {})
}

onMounted(loadBooks)
</script>
