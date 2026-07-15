<template>
  <div>
    <h2 style="margin-bottom:20px;">💬 对话日志</h2>

    <el-table :data="dialogs" v-loading="loading" border stripe>
      <el-table-column prop="sessionId" label="会话ID" width="200" />
      <el-table-column prop="question" label="用户问题" width="300" show-overflow-tooltip />
      <el-table-column prop="answer" label="AI回复" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-html="row.answer?.substring(0, 100) + '...'"></span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const dialogs = ref([])
const loading = ref(true)

onMounted(async () => {
  const res = await adminApi.dialogs.list()
  if (res.code === 200) dialogs.value = res.data
  loading.value = false
})
</script>
