<template>
  <div>
    <header class="main-header">
      <div class="header-inner">
        <div class="logo">
          <el-icon :size="28"><GraduationCap /></el-icon>
          <span>农溯AI · 管理后台</span>
        </div>
        <div style="display:flex;gap:16px;align-items:center;">
          <el-tag :type="roleTagType" size="small">{{ roleLabel }}</el-tag>
          <router-link to="/products" style="color:rgba(255,255,255,0.9);font-size:14px;">
            <el-icon><HomeFilled /></el-icon> 返回商城
          </router-link>
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </header>
    <div class="admin-layout">
      <aside class="admin-sidebar">
        <el-menu :default-active="currentRoute" router>
          <el-menu-item index="/admin">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <!-- 管理员和生产者均可管理商品和溯源 -->
          <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/trace">
            <el-icon><Link /></el-icon>
            <span>溯源管理</span>
          </el-menu-item>
          <!-- 仅管理员可见 -->
          <el-menu-item v-if="userStore.isAdmin" index="/admin/orders">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin/knowledge">
            <el-icon><Notebook /></el-icon>
            <span>知识库管理</span>
          </el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin/dialogs">
            <el-icon><ChatDotSquare /></el-icon>
            <span>对话日志</span>
          </el-menu-item>
        </el-menu>
      </aside>
      <main class="admin-main">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentRoute = computed(() => route.path)

const roleLabel = computed(() => {
  const map = { ADMIN: '管理员', PRODUCER: '生产者', CONSUMER: '消费者' }
  return map[userStore.role] || userStore.role
})

const roleTagType = computed(() => {
  const map = { ADMIN: 'danger', PRODUCER: 'warning', CONSUMER: 'success' }
  return map[userStore.role] || 'info'
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>
