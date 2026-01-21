<template>
  <div class="all-applications">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>全站申请记录</span>
        </div>
      </template>

      <div class="filter-container">
        <el-select v-model="queryParams.status" placeholder="申请状态" clearable style="width: 150px" class="filter-item" @change="handleQuery">
          <el-option label="待审批" :value="0" />
          <el-option label="审批中" :value="1" />
          <el-option label="已通过" :value="2" />
          <el-option label="已拒绝" :value="3" />
          <el-option label="已撤销" :value="4" />
        </el-select>
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="applicationNo" label="申请编号" width="180" />
        <el-table-column prop="studentName" label="申请人" width="100" />
        <el-table-column prop="certificateType" label="证明类型" width="120" />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column prop="currentApprovalLevel" label="当前级别" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentApp.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="证明类型">{{ currentApp.certificateType }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentApp.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentApp.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">{{ currentApp.statusDesc }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentApp.createTime }}</el-descriptions-item>
        <el-descriptions-item label="当前级别">{{ currentApp.currentApprovalLevel }}</el-descriptions-item>
        <el-descriptions-item label="申请理由" :span="2">{{ currentApp.applicationReason || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAdminAllApplications } from '@/api/admin'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  current: 1,
  size: 10,
  status: null
})

const detailVisible = ref(false)
const currentApp = ref<any>({})

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getAdminAllApplications(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.current = 1
  fetchData()
}

const handleDetail = (row: any) => {
  currentApp.value = row
  detailVisible.value = true
}

const getStatusType = (status: number) => {
  const types: any = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
  return types[status] || 'info'
}

onMounted(fetchData)
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
