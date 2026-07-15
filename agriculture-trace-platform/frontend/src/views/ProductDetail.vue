<template>
  <div class="page-container" v-loading="loading">
    <el-button text @click="$router.back()" style="margin-bottom:16px;">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-row :gutter="32" v-if="product">
      <el-col :span="10">
        <div style="height:360px;background:linear-gradient(135deg,#e8f5e9,#c8e6c9);border-radius:12px;display:flex;align-items:center;justify-content:center;">
          <el-icon :size="80" color="#4caf50"><Goods /></el-icon>
        </div>
      </el-col>
      <el-col :span="14">
        <h1 style="font-size:28px;margin-bottom:8px;">{{ product.name }}</h1>
        <div style="display:flex;gap:12px;margin-bottom:16px;">
          <span :class="'trace-level-badge trace-level-' + (product.traceLevel || 'AA')">
            {{ product.traceLevel || 'AA' }}级溯源
          </span>
          <el-tag type="success" v-if="product.status === 1">在售</el-tag>
        </div>
        <div style="font-size:32px;color:#e53935;font-weight:bold;margin-bottom:16px;">
          ¥{{ product.price }}
        </div>
        <el-descriptions :column="1" border style="margin-bottom:20px;">
          <el-descriptions-item label="产地">{{ product.origin }}</el-descriptions-item>
          <el-descriptions-item label="规格">{{ product.spec }}</el-descriptions-item>
          <el-descriptions-item label="库存">{{ product.stock }}件</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ product.batchNo }}</el-descriptions-item>
          <el-descriptions-item label="溯源码">
            <el-button type="primary" link @click="$router.push('/chat')">AI 查询溯源 ></el-button>
          </el-descriptions-item>
        </el-descriptions>
        <p style="color:#666;margin-bottom:20px;line-height:1.8;">{{ product.description }}</p>
        <el-button type="primary" size="large" @click="addToCart" :disabled="product.stock <= 0">
          <el-icon><ShoppingCart /></el-icon> 加入购物车
        </el-button>
        <el-button type="success" size="large" @click="buyNow" :disabled="product.stock <= 0">
          立即购买
        </el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const product = ref(null)
const loading = ref(true)

const loadProduct = async () => {
  const res = await productApi.getById(route.params.id)
  if (res.code === 200) product.value = res.data
  loading.value = false
}

const addToCart = () => {
  if (product.value) cartStore.addItem(product.value)
}

const buyNow = () => {
  if (product.value) cartStore.addItem(product.value)
  router.push('/cart')
}

onMounted(loadProduct)
</script>
