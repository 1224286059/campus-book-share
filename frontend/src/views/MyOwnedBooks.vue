<template>
  <div class="page-wrap">
    <section class="page-heading">
      <div>
        <p class="page-heading__eyebrow">我的持有书籍</p>
        <h1>从“拥有”继续走向“再次共享”</h1>
      </div>
    </section>

    <el-card shadow="never" class="section-card">
      <el-table v-loading="loading" :data="books" empty-text="你当前没有持有中的书籍">
        <el-table-column prop="title" label="书名" min-width="220" />
        <el-table-column label="当前共享方式" min-width="120">
          <template #default="{ row }">{{ shareTypeMap[row.shareType] || row.shareType }}</template>
        </el-table-column>
        <el-table-column label="当前状态" min-width="120">
          <template #default="{ row }">{{ bookStatusMap[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column prop="bookLocation" label="书籍位置" min-width="180" show-overflow-tooltip />
        <el-table-column label="流转次数" min-width="100">
          <template #default="{ row }">{{ row.circulationCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push('/books/' + row.id)">查看详情</el-button>
            <el-button link type="success" @click="openReshare(row)">再次共享</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="再次共享" width="560px">
      <div class="dialog-tip">这是“循环共享”的关键功能。重新选择共享方式后，系统会重新进入审核，并生成一条 RESHARE 流转记录。</div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="共享方式" prop="shareType">
          <el-select v-model="form.shareType" placeholder="请选择新的共享方式">
            <el-option v-for="item in shareTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" class="full-width" />
        </el-form-item>
        <el-form-item label="品相">
          <el-input v-model="form.conditionLevel" />
        </el-form-item>
        <el-form-item label="书籍位置" prop="bookLocation">
          <el-input
            v-model="form.bookLocation"
            placeholder="请输入书籍当前存放位置，如图书馆一楼、南校区宿舍区、计算机学院楼"
          />
        </el-form-item>
        <el-form-item label="封面 URL">
          <el-input v-model="form.coverUrl" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReshare">提交再次共享申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getMyOwnedBooks, reshareBook } from '../api/book'
import { bookStatusMap, shareTypeMap, shareTypeOptions } from '../utils/constants'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const books = ref([])
const dialogVisible = ref(false)
const currentBookId = ref(null)
const formRef = ref()
const form = reactive({
  shareType: '',
  price: 0,
  description: '',
  conditionLevel: '',
  bookLocation: '',
  coverUrl: ''
})

const rules = {
  shareType: [{ required: true, message: '请选择共享方式', trigger: 'change' }],
  bookLocation: [{ max: 255, message: '书籍位置不能超过 255 个字符', trigger: 'blur' }]
}

function loadBooks() {
  loading.value = true
  getMyOwnedBooks()
    .then(function (data) {
      books.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function openReshare(row) {
  currentBookId.value = row.id
  form.shareType = row.shareType || ''
  form.price = row.price || 0
  form.description = row.description || ''
  form.conditionLevel = row.conditionLevel || ''
  form.bookLocation = row.bookLocation || ''
  form.coverUrl = row.coverUrl || ''
  dialogVisible.value = true
}

function submitReshare() {
  formRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    submitting.value = true
    reshareBook(currentBookId.value, form)
      .then(function () {
        ElMessage.success('已提交再次共享申请，等待管理员审核')
        dialogVisible.value = false
        loadBooks()
      })
      .finally(function () {
        submitting.value = false
      })
    return true
  })
}

onMounted(loadBooks)
</script>
