<template>
  <div class="history-page">
    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2>📋 审批历史</h2>
            <p>查看您的所有审批记录</p>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="applicationNo" label="申请编号" width="160" align="center" />
        <el-table-column prop="approverName" label="审批人" width="100" align="center" />
        <el-table-column prop="approvalLevel" label="审批级别" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.approvalLevel }}级</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalResult" label="审批结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getResultType(row.approvalResult)" size="small">
              {{ getResultText(row.approvalResult) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalOpinion" label="审批意见" min-width="200" show-overflow-tooltip />
        <el-table-column prop="approvalTime" label="审批时间" width="180" align="center" />
        <el-table-column prop="timeCost" label="耗时(分钟)" width="100" align="center" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getApprovalHistory } from '@/api/approval'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getResultType = (result: number) => {
  const types: any = { 1: 'success', 2: 'danger', 3: 'warning' }
  return types[result] || 'info'
}

const getResultText = (result: number) => {
  const texts: any = { 1: '通过', 2: '拒绝', 3: '退回' }
  return texts[result] || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getApprovalHistory({
      current: currentPage.value,
      size: pageSize.value,
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取审批历史失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.history-page {
  max-width: 1400px;
  margin: 0 auto;
}

.history-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h2 {
  margin: 0 0 4px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.header-left p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
