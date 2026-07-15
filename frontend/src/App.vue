<template>
  <div class="app-container">
    <template v-if="isAdminPage">
      <admin-layout>
        <router-view />
      </admin-layout>
    </template>
    <template v-else>
      <main-header />
      <router-view />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import MainHeader from '@/components/MainHeader.vue'
import AdminLayout from '@/components/AdminLayout.vue'

const route = useRoute()
const isAdminPage = computed(() => route.path.startsWith('/admin'))
</script>
