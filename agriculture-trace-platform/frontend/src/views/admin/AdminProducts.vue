<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
      <h2>📦 商品管理</h2>
      <el-button type="primary" @click="showDialog = true; isEdit = false; form = {}">新增商品</el-button>
    </div>

    <el-table :data="products" v-loading="loading" border stripe>
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="category" label="分类" width="80" />
      <el-table-column prop="origin" label="产地" width="100" />
      <el-table-column prop="price" label="价格" width="80">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="batchNo" label="批次号" width="160" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="editProduct(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑商品' : '新增商品'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category">
            <el-option label="水果" value="水果" /><el-option label="蔬菜" value="蔬菜" />
            <el-option label="粮食" value="粮食" /><el-option label="茶叶" value="茶叶" />
            <el-option label="畜禽" value="畜禽" />
          </el-select>
        </el-form-item>
        <el-form-item label="产地"><el-input v-model="form.origin" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="form.spec" /></el-form-item>
        <el-form-item label="批次号"><el-input v-model="form.batchNo" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const products = ref([])
const loading = ref(true)
const showDialog = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = ref({})

const loadData = async () => {
  const res = await adminApi.products.list()
  if (res.code === 200) products.value = res.data
  loading.value = false
}

const editProduct = (row) => {
  form.value = { ...row }
  isEdit.value = true
  showDialog.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    if (isEdit.value) {
      await adminApi.products.update(form.value)
      ElMessage.success('更新成功')
    } else {
      await adminApi.products.save(form.value)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id) => {
  await adminApi.products.delete(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
