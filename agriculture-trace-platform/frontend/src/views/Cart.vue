<template>
  <div class="page-container">
    <h1 style="font-size:24px;color:#1a6b3c;margin-bottom:20px;">🛒 购物车</h1>

    <el-card v-if="cartStore.items.length > 0">
      <el-table :data="cartStore.items" style="width:100%">
        <el-table-column label="商品">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:12px;">
              <el-icon :size="32" color="#4caf50"><Goods /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" width="180">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" :max="row.stock" size="small"
              @change="cartStore.save()" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="danger" link @click="cartStore.removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display:flex;justify-content:space-between;align-items:center;margin-top:20px;padding:16px 0;">
        <div>
          <el-button @click="cartStore.clear()">清空购物车</el-button>
        </div>
        <div style="display:flex;align-items:center;gap:20px;">
          <span>合计：<b style="font-size:24px;color:#e53935;">¥{{ cartStore.totalAmount.toFixed(2) }}</b></span>
          <el-button type="success" size="large" @click="showCheckout = true">去结算</el-button>
        </div>
      </div>
    </el-card>

    <div v-else style="text-align:center;padding:80px 0;color:#999;">
      <el-icon :size="48"><ShoppingCart /></el-icon>
      <p style="margin-top:12px;">购物车是空的</p>
      <el-button type="primary" style="margin-top:12px;" @click="$router.push('/products')">去逛逛</el-button>
    </div>

    <!-- 结算对话框 -->
    <el-dialog v-model="showCheckout" title="填写收货信息" width="500px">
      <el-form :model="checkoutForm" label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="checkoutForm.name" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="checkoutForm.phone" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="checkoutForm.address" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCheckout = false">取消</el-button>
        <el-button type="success" @click="submitOrder" :loading="submitting">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { orderApi } from '@/api'
import { ElMessage } from 'element-plus'

const cartStore = useCartStore()
const userStore = useUserStore()
const router = useRouter()
const showCheckout = ref(false)
const submitting = ref(false)

const checkoutForm = ref({ name: '', phone: '', address: '' })

const submitOrder = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  submitting.value = true
  try {
    const res = await orderApi.create({
      productIds: cartStore.items.map(i => i.id),
      quantities: cartStore.items.map(i => i.quantity),
      receiverName: checkoutForm.value.name,
      receiverPhone: checkoutForm.value.phone,
      receiverAddress: checkoutForm.value.address
    })
    if (res.code === 200) {
      ElMessage.success('下单成功！')
      cartStore.clear()
      showCheckout.value = false
      router.push('/orders')
    }
  } finally {
    submitting.value = false
  }
}
</script>
