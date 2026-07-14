<template>
  <div class="page-container">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
      <h1 style="font-size:24px;color:#1a6b3c;">🌾 农产品商城</h1>
      <el-input v-model="searchKeyword" placeholder="搜索商品..." style="width:300px;" clearable @keyup.enter="handleSearch">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange" style="margin-bottom:16px;">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="水果" name="水果" />
      <el-tab-pane label="蔬菜" name="蔬菜" />
      <el-tab-pane label="粮食" name="粮食" />
      <el-tab-pane label="茶叶" name="茶叶" />
      <el-tab-pane label="畜禽" name="畜禽" />
    </el-tabs>

    <el-row :gutter="20">
      <el-col v-for="item in products" :key="item.id" :xs="12" :sm="8" :md="6" style="margin-bottom:20px;">
        <el-card class="product-card" shadow="hover" @click="$router.push(`/products/${item.id}`)">
          <div style="height:160px;background:linear-gradient(135deg,#e8f5e9,#c8e6c9);display:flex;align-items:center;justify-content:center;border-radius:8px;margin-bottom:12px;">
            <el-icon :size="48" color="#4caf50"><Goods /></el-icon>
          </div>
          <h3 style="font-size:16px;margin-bottom:6px;">{{ item.name }}</h3>
          <div style="display:flex;justify-content:space-between;align-items:center;">
            <span style="color:#e53935;font-size:18px;font-weight:bold;">¥{{ item.price }}</span>
            <span :class="'trace-level-badge trace-level-' + (item.traceLevel || 'AA')">
              {{ item.traceLevel || 'AA' }}级溯源
            </span>
          </div>
          <div style="color:#999;font-size:13px;margin-top:6px;">
            {{ item.origin }} · {{ item.spec }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div v-if="products.length === 0" style="text-align:center;padding:60px;color:#999;">
      <el-icon :size="48"><WarningFilled /></el-icon>
      <p style="margin-top:12px;">暂无商品</p>
    </div>

    <div style="text-align:center;margin-top:20px;">
      <el-pagination v-if="total > 0" background layout="prev, pager, next" :total="total" :page-size="size" v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi } from '@/api'
import { useCartStore } from '@/stores/cart'

const cartStore = useCartStore()
const products = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const activeCategory = ref('')
const searchKeyword = ref('')

const loadData = async () => {
  const res = await productApi.page({ page: page.value, size: size.value, category: activeCategory.value || undefined })
  if (res.code === 200) {
    products.value = res.data.records
    total.value = res.data.total
  }
}

const handleCategoryChange = () => {
  page.value = 1
  loadData()
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadData()
    return
  }
  const res = await productApi.search(searchKeyword.value)
  if (res.code === 200) {
    products.value = res.data
  }
}

onMounted(loadData)
</script>
