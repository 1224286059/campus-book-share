<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-panel">
      <div class="admin-toolbar">
        <el-button type="primary" @click="openDialog()">新增分类</el-button>
      </div>

      <el-table v-loading="loading" :data="categories" empty-text="暂无分类数据">
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column prop="description" label="分类描述" min-width="260" />
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCategory, deleteCategory, updateCategory } from '../../api/admin'
import { getCategories } from '../../api/category'
import { formatDateTime } from '../../utils/format'

const loading = ref(false)
const submitting = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const form = reactive({
  name: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

function loadCategories() {
  loading.value = true
  getCategories()
    .then(function (data) {
      categories.value = data || []
    })
    .finally(function () {
      loading.value = false
    })
}

function openDialog(row) {
  editingId.value = row ? row.id : null
  form.name = row ? row.name : ''
  form.description = row ? row.description : ''
  dialogVisible.value = true
}

function submit() {
  formRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    submitting.value = true
    var request = editingId.value ? updateCategory(editingId.value, form) : createCategory(form)
    request
      .then(function () {
        ElMessage.success(editingId.value ? '分类更新成功' : '分类创建成功')
        dialogVisible.value = false
        loadCategories()
      })
      .finally(function () {
        submitting.value = false
      })
    return true
  })
}

function handleDelete(row) {
  ElMessageBox.confirm('确认删除分类“' + row.name + '”吗？', '提示', { type: 'warning' })
    .then(function () {
      return deleteCategory(row.id)
    })
    .then(function () {
      ElMessage.success('删除成功')
      loadCategories()
    })
    .catch(function () {})
}

onMounted(loadCategories)
</script>
