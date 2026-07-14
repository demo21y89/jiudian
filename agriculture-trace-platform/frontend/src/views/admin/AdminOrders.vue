<template>
  <div>
    <h2 style="margin-bottom:20px;">📋 订单管理</h2>

    <div style="margin-bottom:16px;">
      <el-radio-group v-model="statusFilter" @change="loadData">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PENDING">待支付</el-radio-button>
        <el-radio-button label="PAID">已支付</el-radio-button>
        <el-radio-button label="SHIPPED">已发货</el-radio-button>
        <el-radio-button label="COMPLETED">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="orders" v-loading="loading" border stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="receiverName" label="收货人" width="100" />
      <el-table-column prop="totalAmount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="logisticsNo" label="物流单号" width="150" />
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PAID'" type="primary" @click="showShip(row)">发货</el-button>
          <el-button v-if="row.status === 'SHIPPED'" type="success" @click="handleComplete(row.id)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="shipDialog" title="填写物流信息" width="400px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司"><el-input v-model="shipForm.company" /></el-form-item>
        <el-form-item label="物流单号"><el-input v-model="shipForm.no" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialog = false">取消</el-button>
        <el-button type="primary" @click="handleShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(true)
const statusFilter = ref('')
const shipDialog = ref(false)
const shipForm = ref({ company: '', no: '' })
const currentOrder = ref(null)

const statusMap = {
  PENDING: { text: '待支付', type: 'warning' },
  PAID: { text: '已支付', type: 'primary' },
  SHIPPED: { text: '已发货', type: 'info' },
  COMPLETED: { text: '已完成', type: 'success' },
  CANCELLED: { text: '已取消', type: 'danger' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusType = (s) => statusMap[s]?.type || ''

const loadData = async () => {
  const res = await adminApi.orders.page({ status: statusFilter.value || undefined })
  if (res.code === 200) orders.value = res.data.records
  loading.value = false
}

const showShip = (row) => {
  currentOrder.value = row
  shipDialog.value = true
  shipForm.value = { company: '', no: '' }
}

const handleShip = async () => {
  await adminApi.orders.ship(currentOrder.value.id, shipForm.value)
  ElMessage.success('发货成功')
  shipDialog.value = false
  loadData()
}

const handleComplete = async (id) => {
  await adminApi.orders.complete(id)
  ElMessage.success('已完成')
  loadData()
}

onMounted(loadData)
</script>
