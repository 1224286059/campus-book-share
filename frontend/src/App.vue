<template>
  <div class="app-layout">
    <AppHeader v-if="!route.meta.hideHeader" />
    <router-view />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import AppHeader from './components/AppHeader.vue'

const route = useRoute()
</script>
