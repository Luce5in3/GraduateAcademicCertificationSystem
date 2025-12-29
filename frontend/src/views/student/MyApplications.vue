<template>
  <div class="my-applications">
    <el-card>
      <template #header>
        <div class="header">
          <h2>我的申请</h2>
          <el-button type="primary" @click="router.push('/student/application')">
            新建申请
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              :disabled="row.status !== 0"
              @click="handleCancel(row.id)"
            >
              撤销
            </el-button>
          </template>
        </el-table-column>
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
import { useRouter } from 'vue-router'
import { getMyApplications, cancelApplication } from '@/api/application'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
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
    const res: any = await getMyApplications({
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

const handleCancel = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要撤销此申请吗？', '提示', {
      type: 'warning',
    })
    
    const res: any = await cancelApplication(id)
    if (res.code === 200) {
      ElMessage.success('撤销成功')
      fetchData()
    }
  } catch (error) {
    // 用户取消操作
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.my-applications {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  color: #303133;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
