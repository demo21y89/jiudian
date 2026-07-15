<template>
  <div>
    <h2 style="margin-bottom:20px;">🔗 溯源管理</h2>

    <el-card style="margin-bottom:20px;">
      <el-input v-model="batchNo" placeholder="输入批次号查询（如 BATCH20260701001）" style="width:400px;margin-right:12px;" />
      <el-button type="primary" @click="queryTrace" :loading="queryLoading">查询</el-button>
    </el-card>

    <el-card v-if="traceInfo" v-loading="queryLoading">
      <h3>批次信息</h3>
      <el-descriptions :column="2" border style="margin:16px 0;">
        <el-descriptions-item label="批次号">{{ traceInfo.batch?.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="产地">{{ traceInfo.batch?.farmAddress }}</el-descriptions-item>
        <el-descriptions-item label="种植面积">{{ traceInfo.batch?.farmArea }}</el-descriptions-item>
        <el-descriptions-item label="土壤类型">{{ traceInfo.batch?.soilType }}</el-descriptions-item>
        <el-descriptions-item label="生产日期">{{ traceInfo.batch?.produceDate }}</el-descriptions-item>
        <el-descriptions-item label="采收日期">{{ traceInfo.batch?.harvestDate }}</el-descriptions-item>
      </el-descriptions>

      <h3 style="margin-top:20px;">📅 农事记录</h3>
      <el-timeline style="margin:16px 0;">
        <el-timeline-item v-for="r in traceInfo.traceRecords" :key="r.id" :timestamp="r.recordTime">
          <b>{{ r.recordType }}</b>：{{ r.content }}
        </el-timeline-item>
      </el-timeline>

      <h3 style="margin-top:20px;">🧪 农残检测报告</h3>
      <el-table :data="traceInfo.pesticideReports" border stripe style="margin:16px 0;">
        <el-table-column prop="itemName" label="检测项目" />
        <el-table-column prop="result" label="检测结果" />
        <el-table-column prop="standardLimit" label="标准限值" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column label="是否达标">
          <template #default="{ row }">
            <el-tag :type="row.isCompliant ? 'success' : 'danger'">{{ row.isCompliant ? '达标' : '超标' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-alert v-if="traceInfo.overallCompliant" title="该批次所有检测项目均达标，符合GB 2763标准" type="success" show-icon />
      <el-alert v-else title="该批次存在超标项" type="warning" show-icon />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { adminApi } from '@/api'

const batchNo = ref('')
const traceInfo = ref(null)
const queryLoading = ref(false)

const queryTrace = async () => {
  if (!batchNo.value.trim()) return
  queryLoading.value = true
  try {
    const res = await adminApi.trace.getBatch(batchNo.value)
    if (res.code === 200) traceInfo.value = res.data
  } finally {
    queryLoading.value = false
  }
}
</script>
