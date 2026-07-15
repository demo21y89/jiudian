<template>
  <div class="page-container">
    <h1 style="font-size:24px;color:#1a6b3c;margin-bottom:20px;">📋 我的订单</h1>

    <el-table :data="orders" v-loading="loading" style="width:100%" v-if="orders.length > 0">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column label="金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收货人" width="120">
        <template #default="{ row }">{{ row.receiverName }}</template>
      </el-table-column>
      <el-table-column label="下单时间" width="180">
        <template #default="{ row }">{{ row.createTime }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push('/orders/' + row.id)">详情</el-button>
          <el-button v-if="row.status === 'PENDING'" type="success" link @click="handlePay(row)">支付</el-button>
          <el-button v-if="row.status === 'PENDING'" type="danger" link @click="handleCancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-else style="text-align:center;padding:80px 0;color:#999;">
      <el-icon :size="48"><List /></el-icon>
      <p style="margin-top:12px;">暂无订单</p>
      <el-button type="primary" style="margin-top:12px;" @click="$router.push('/products')">去购物</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api'
import { ElMessage } from 'element-plus'

const orders = ref([])
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

const loadOrders = async () => {
  const res = await orderApi.list()
  if (res.code === 200) orders.value = res.data
  loading.value = false
}

const handlePay = async (order) => {
  await orderApi.pay(order.id)
  ElMessage.success('支付成功')
  loadOrders()
}

const handleCancel = async (order) => {
  await orderApi.cancel(order.id)
  ElMessage.success('已取消')
  loadOrders()
}

onMounted(loadOrders)
</script>
