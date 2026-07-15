<template>
  <div>
    <h2 style="margin-bottom:20px;">📊 数据概览</h2>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover" style="text-align:center;margin-bottom:20px;">
          <div><el-icon :size="32" :color="item.color"><component :is="item.icon" /></el-icon></div>
          <div style="font-size:28px;font-weight:bold;margin:8px 0;">{{ item.value }}</div>
          <div style="color:#999;">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const stats = ref([
  { label: '商品总数', value: 0, icon: 'Goods', color: '#4caf50' },
  { label: '订单总数', value: 0, icon: 'List', color: '#2196f3' },
  { label: '用户总数', value: 0, icon: 'User', color: '#ff9800' },
  { label: '对话次数', value: 0, icon: 'ChatDotSquare', color: '#9c27b0' }
])

onMounted(async () => {
  const res = await adminApi.dashboard.stats()
  if (res.code === 200) {
    const data = res.data
    stats.value[0].value = data.productCount || 0
    stats.value[1].value = data.orderCount || 0
    stats.value[2].value = data.userCount || 0
    stats.value[3].value = data.dialogCount || 0
  }
})
</script>
