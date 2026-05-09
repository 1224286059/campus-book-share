<template>
  <el-dialog v-model="visible" :title="title" width="500px" @closed="handleClosed">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="举报原因" prop="reason">
        <el-input v-model="form.reason" type="textarea" :rows="4" maxlength="200" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="danger" :loading="submitting" @click="submit">提交举报</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createReport } from '../api/report'
import { reportTargetTypeMap } from '../utils/constants'

const props = defineProps({
  modelValue: Boolean,
  targetType: {
    type: String,
    default: 'BOOK'
  },
  targetId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const submitting = ref(false)
const formRef = ref()
const form = reactive({
  reason: ''
})

const rules = {
  reason: [{ required: true, message: '请填写举报原因', trigger: 'blur' }]
}

watch(
  function () {
    return props.modelValue
  },
  function (value) {
    visible.value = value
    if (value) {
      form.reason = ''
    }
  }
)

watch(visible, function (value) {
  emit('update:modelValue', value)
})

const title = computed(function () {
  return '举报' + (reportTargetTypeMap[props.targetType] || '内容')
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
    createReport({
      targetType: props.targetType,
      targetId: props.targetId,
      reason: form.reason
    })
      .then(function () {
        ElMessage.success('举报已提交')
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
