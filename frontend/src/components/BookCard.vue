<template>
  <div class="book-card-shell" @click="$emit('open')">
    <el-card class="book-card" shadow="hover">
      <div class="book-cover">
        <img v-if="book.coverUrl" :src="book.coverUrl" :alt="book.title" />
        <div v-else class="book-cover__fallback">{{ shareLabel }}</div>
      </div>
      <div class="book-card__body">
        <div class="book-card__meta">
          <el-tag :type="tagType" effect="dark">{{ shareLabel }}</el-tag>
          <span class="book-card__count">流转 {{ book.circulationCount || 0 }} 次</span>
        </div>
        <h3>{{ book.title }}</h3>
        <p>{{ book.major || '未填写专业' }} · {{ book.courseName || '未填写课程' }}</p>
        <p>品相：{{ book.conditionLevel || '未说明' }}</p>
        <div class="book-card__footer">
          <span class="book-card__price">{{ priceText }}</span>
          <span class="book-card__owner">{{ book.ownerUsername || '匿名发布' }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { formatPrice } from '../utils/format'
import { shareTypeMap, shareTypeTagMap } from '../utils/constants'

defineEmits(['open'])

const props = defineProps({
  book: {
    type: Object,
    required: true
  }
})

const shareLabel = computed(function () {
  return shareTypeMap[props.book.shareType] || props.book.shareType
})

const tagType = computed(function () {
  return shareTypeTagMap[props.book.shareType] || 'info'
})

const priceText = computed(function () {
  if (props.book.shareType === 'DONATE') {
    return '免费领取'
  }
  if (props.book.shareType === 'EXCHANGE') {
    return '以书换书'
  }
  if (props.book.shareType === 'BORROW') {
    return '按期归还'
  }
  return formatPrice(props.book.price)
})
</script>
