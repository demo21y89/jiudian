<template>
  <header class="main-header">
    <div class="header-inner">
      <div class="logo">
        <el-icon :size="28"><GraduationCap /></el-icon>
        <span>农溯AI</span>
      </div>
      <nav class="nav-links">
        <!-- 所有用户可见 -->
        <router-link to="/products">商城</router-link>
        <router-link to="/chat">AI溯源助手</router-link>

        <!-- 登录用户可见 -->
        <template v-if="userStore.isLoggedIn">
          <router-link to="/orders">我的订单</router-link>
          <router-link to="/cart">
            <el-badge :value="cartStore.totalCount" :hidden="cartStore.totalCount === 0">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>
        </template>

        <!-- 管理员/生产者可见 -->
        <router-link v-if="userStore.isAdminOrProducer" to="/admin">管理后台</router-link>

        <!-- 用户信息 -->
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span style="color:white;cursor:pointer;display:flex;align-items:center;gap:4px;">
              <el-icon><User /></el-icon>
              {{ userStore.username }}
              <el-tag size="small" style="margin-left:4px;" :type="roleTagType">{{ roleLabel }}</el-tag>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/orders')">我的订单</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdminOrProducer" @click="$router.push('/admin')">管理后台</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const cartStore = useCartStore()
const router = useRouter()

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
