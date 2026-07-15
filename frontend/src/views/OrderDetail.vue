<template>
  <div class="page-container" v-loading="loading">
    <el-button text @click="$router.back()" style="margin-bottom:16px;">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-card v-if="order">
      <h2>订单详情</h2>
      <el-descriptions :column="2" border style="margin-top:16px;">
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ order.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ order.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ order.logisticsNo || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="物流公司">{{ order.logisticsCompany || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { orderApi } from '@/api'

const route = useRoute()
const order = ref(null)
const loading = ref(true)

const statusMap = {
  PENDING: { text: '待支付', type: 'warning' },
  PAID: { text: '已支付', type: 'primary' },
  SHIPPED: { text: '已发货', type: 'info' },
  COMPLETED: { text: '已完成', type: 'success' },
  CANCELLED: { text: '已取消', type: 'danger' }
}

const statusText = (s) => statusMap[s]?.text || s
const statusType = (s) => statusMap[s]?.type || ''

onMounted(async () => {
  const res = await orderApi.getById(route.params.id)
  if (res.code === 200) order.value = res.data
  loading.value = false
})
</script>
