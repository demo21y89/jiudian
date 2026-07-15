<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
      <h2>📚 知识库管理</h2>
      <el-button type="primary" @click="showDialog = true; form = {}">新增文档</el-button>
    </div>

    <el-table :data="docs" v-loading="loading" border stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="source" label="来源" width="150" />
      <el-table-column prop="uploadTime" label="上传时间" width="180" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" title="新增知识文档" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category">
            <el-option label="法规" value="法规" />
            <el-option label="标准" value="标准" />
            <el-option label="规范" value="规范" />
            <el-option label="产品知识" value="产品知识" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源"><el-input v-model="form.source" /></el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

const docs = ref([])
const loading = ref(true)
const showDialog = ref(false)
const form = ref({})

const loadData = async () => {
  const res = await adminApi.knowledge.list()
  if (res.code === 200) docs.value = res.data
  loading.value = false
}

const handleSave = async () => {
  await adminApi.knowledge.save(form.value)
  ElMessage.success('添加成功')
  showDialog.value = false
  loadData()
}

const handleDelete = async (id) => {
  await adminApi.knowledge.delete(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
