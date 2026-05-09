<template>
  <div class="page-wrap">
    <section class="hero-banner">
      <div>
        <p class="hero-banner__eyebrow">循环共享不是一次交易，而是一段书籍旅程</p>
        <h1>出售、借阅、交换、捐赠，再到再次共享</h1>
        <p class="hero-banner__desc">
          平台围绕校园教材与资料流转设计，帮助每一本书在不同同学之间持续共享，让“拥有”与“使用”更灵活。
        </p>
      </div>
      <div class="hero-banner__stats">
        <div class="stat-box">
          <strong>{{ pagination.total }}</strong>
          <span>可浏览书籍</span>
        </div>
        <div class="stat-box">
          <strong>5</strong>
          <span>核心共享模式</span>
        </div>
        <div class="stat-box">
          <strong>RESHARE</strong>
          <span>再次共享机制</span>
        </div>
      </div>
    </section>

    <section class="filter-panel">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="书名 / 作者 / 描述" clearable @keyup.enter="loadBooks" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.categoryId" placeholder="全部分类" clearable>
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="filters.major" placeholder="如：软件工程" clearable />
        </el-form-item>
        <el-form-item label="课程">
          <el-input v-model="filters.courseName" placeholder="如：数据库原理" clearable />
        </el-form-item>
        <el-form-item label="共享方式">
          <el-select v-model="filters.shareType" placeholder="全部方式" clearable>
            <el-option v-for="item in shareTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadBooks">筛选</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section v-loading="loading" class="book-grid-section">
      <div v-if="books.length" class="book-grid">
        <router-link v-for="book in books" :key="book.id" :to="'/books/' + book.id" class="book-link">
          <BookCard :book="book" />
        </router-link>
      </div>
      <el-empty v-else description="暂时没有符合条件的书籍，换个条件试试吧" />
    </section>

    <div class="pagination-bar">
      <el-pagination
        layout="prev, pager, next, total"
        :current-page="filters.page"
        :page-size="filters.size"
        :total="pagination.total"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getBookList } from '../api/book'
import { getCategories } from '../api/category'
import BookCard from '../components/BookCard.vue'
import { shareTypeOptions } from '../utils/constants'

const loading = ref(false)
const books = ref([])
const categories = ref([])
const filters = reactive({
  keyword: '',
  categoryId: null,
  major: '',
  courseName: '',
  shareType: '',
  page: 1,
  size: 8
})
const pagination = reactive({
  total: 0
})

function loadCategories() {
  return getCategories().then(function (data) {
    categories.value = data || []
  })
}

function loadBooks() {
  loading.value = true
  return getBookList(filters)
    .then(function (data) {
      books.value = data.records || []
      pagination.total = data.total || 0
    })
    .finally(function () {
      loading.value = false
    })
}

function resetFilters() {
  filters.keyword = ''
  filters.categoryId = null
  filters.major = ''
  filters.courseName = ''
  filters.shareType = ''
  filters.page = 1
  loadBooks()
}

function handlePageChange(page) {
  filters.page = page
  loadBooks()
}

onMounted(function () {
  loadCategories()
  loadBooks()
})
</script>
