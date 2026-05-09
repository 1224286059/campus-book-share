<template>
  <el-dialog v-model="visible" title="提交评价" width="520px" @closed="handleClosed">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="书籍评分" prop="bookScore">
        <el-rate v-model="form.bookScore" />
      </el-form-item>
      <el-form-item label="用户评分" prop="userScore">
        <el-rate v-model="form.userScore" />
      </el-form-item>
      <el-form-item label="评价内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="4" maxlength="200" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">提交评价</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createEvaluation } from '../api/evaluation'

const props = defineProps({
  modelValue: Boolean,
  order: {
    type: Object,
    default: function () {
      return null
    }
  },
  currentUserId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const submitting = ref(false)
const formRef = ref()
const form = reactive({
  orderId: null,
  targetUserId: null,
  bookScore: 5,
  userScore: 5,
  content: ''
})

const rules = {
  bookScore: [{ required: true, message: '请选择书籍评分', trigger: 'change' }],
  userScore: [{ required: true, message: '请选择用户评分', trigger: 'change' }]
}

watch(
  function () {
    return props.modelValue
  },
  function (value) {
    visible.value = value
    if (value && props.order) {
      form.orderId = props.order.id
      form.targetUserId = props.currentUserId === props.order.ownerId ? props.order.applicantId : props.order.ownerId
      form.bookScore = 5
      form.userScore = 5
      form.content = ''
    }
  }
)

watch(visible, function (value) {
  emit('update:modelValue', value)
})

function handleClosed() {
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

function submit() {
  formRef.value.validate(function (valid) {
    if (!valid) {
      return false
    }
    submitting.value = true
    createEvaluation(form)
      .then(function () {
        ElMessage.success('评价提交成功')
        visible.value = false
        emit('success')
      })
      .finally(function () {
        submitting.value = false
      })
    return true
  })
}
</script>
