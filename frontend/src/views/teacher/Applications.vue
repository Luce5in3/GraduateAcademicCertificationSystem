<template>
  <div class="applications-page">
    <el-card>
      <template #header>
        <h2>申请列表</h2>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="certificateType" label="证书类型" width="150" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
        <el-table-column prop="createTime" label="申请时间" width="180" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
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
import { getAllApplications } from '@/api/application'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getStatusType = (status: number) => {
  const types: any = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger',
    4: 'info',
  }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: any = {
    0: '待审批',
    1: '审批中',
    2: '已通过',
    3: '已拒绝',
    4: '已撤销',
  }
  return texts[status] || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getAllApplications({
      page: currentPage.value,
      size: pageSize.value,
    })
    if (res.code === 200) {
      tableData.value = res.data.records || res.data || []
      total.value = res.data.total || tableData.value.length
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.applications-page {
  max-width: 1400px;
  margin: 0 auto;
}

h2 {
  margin: 0;
  color: #303133;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
