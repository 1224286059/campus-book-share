<template>
  <div class="page-wrap">
    <section class="page-heading">
      <div>
        <p class="page-heading__eyebrow">发布共享书籍</p>
        <h1>填写书籍信息并提交审核</h1>
      </div>
    </section>

    <el-card shadow="never" class="section-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="24">
          <el-col :md="12" :sm="24">
            <el-form-item label="书名" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="作者">
              <el-input v-model="form.author" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="出版社">
              <el-input v-model="form.publisher" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="课程">
              <el-input v-model="form.courseName" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="专业">
              <el-input v-model="form.major" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="品相">
              <el-input v-model="form.conditionLevel" placeholder="如：九成新" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="书籍位置" prop="bookLocation">
              <el-input
                v-model="form.bookLocation"
                placeholder="请输入书籍当前存放位置，如图书馆一楼、南校区宿舍区、计算机学院楼"
              />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="封面 URL">
              <el-input v-model="form.coverUrl" placeholder="可填写网络图片地址" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="价格">
              <el-input-number v-model="form.price" :min="0" :precision="2" class="full-width" />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="共享方式" prop="shareType">
              <el-select v-model="form.shareType" placeholder="请选择共享方式">
                <el-option v-for="item in shareTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="5" maxlength="300" show-word-limit />
        </el-form-item>
        <div class="form-actions">
          <el-button @click="router.push('/my/books')">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submit">发布并提交审核</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { publishBook } from '../api/book'
import { getCategories } from '../api/category'
import { shareTypeOptions } from '../utils/constants'

const router = useRouter()
const formRef = ref()
const categories = ref([])
const submitting = ref(false)
const form = reactive({
  categoryId: null,
  title: '',
  author: '',
  publisher: '',
  courseName: '',
  major: '',
  conditionLevel: '',
  bookLocation: '',
  coverUrl: '',
  price: 0,
  shareType: '',
  description: ''
})

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  bookLocation: [{ max: 255, message: '书籍位置不能超过 255 个字符', trigger: 'blur' }],
  shareType: [{ required: true, message: '请选择共享方式', trigger: 'change' }]
}

function loadCategories() {
  getCategories().then(function (data) {
    categories.value = data || []
  })
}

function submit() {
  formRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    submitting.value = true
    publishBook(form)
      .then(function () {
        ElMessage.success('发布成功，等待管理员审核')
        router.push('/my/books')
      })
      .finally(function () {
        submitting.value = false
      })
    return true
  })
}

onMounted(loadCategories)
</script>
