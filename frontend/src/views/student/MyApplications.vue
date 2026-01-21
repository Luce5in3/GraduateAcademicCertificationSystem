<template>
  <div class="my-applications">
    <el-card class="list-card">
      <template #header>
        <div class="header">
          <div class="header-left">
            <h2>📄 我的申请</h2>
            <p>查看和管理您的所有证书申请</p>
          </div>
          <el-button type="primary" @click="router.push('/student/application')" size="large">
            <el-icon><Plus /></el-icon>
            新建申请
          </el-button>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-bar">
        <el-space wrap>
          <el-select v-model="filterStatus" placeholder="筛选状态" clearable @change="fetchData" style="width: 140px">
            <el-option label="待审批" :value="0" />
            <el-option label="审批中" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已拒绝" :value="3" />
            <el-option label="已撤销" :value="4" />
          </el-select>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="fetchData"
            style="width: 240px"
          />
          <el-button @click="resetFilter" :icon="RefreshLeft">重置筛选</el-button>
        </el-space>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe style="margin-top: 16px">
        <el-table-column prop="certificateType" label="证书类型" width="150" align="center" />
        <el-table-column prop="status" label="审批状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="dark">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="copies" label="申请份数" width="100" align="center">
          <template #default="{ row }">
            {{ row.copies || 1 }} 份
          </template>
        </el-table-column>
        <el-table-column prop="urgent" label="是否加急" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.urgent" type="danger" size="small">加急</el-tag>
            <el-tag v-else type="info" size="small">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicationReason" label="申请理由" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="申请时间" width="180" align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button
                type="primary"
                size="small"
                link
                @click="handleView(row)"
              >
                查看详情
              </el-button>
              <el-button
                type="danger"
                size="small"
                link
                :disabled="row.status !== 0"
                @click="handleCancel(row)"
              >
                撤销申请
              </el-button>
            </el-space>
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
import { Plus, RefreshLeft } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref<number | null>(null)
const dateRange = ref<[Date, Date] | null>(null)

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
    const params: any = {
      current: currentPage.value,
      size: pageSize.value,
    }
    
    // 添加筛选条件
    if (filterStatus.value !== null) {
      params.status = filterStatus.value
    }
    
    const res: any = await getMyApplications(params)
    if (res.code === 200) {
      tableData.value = res.data.records || res.data || []
      total.value = res.data.total || tableData.value.length
    }
  } catch (error: any) {
    console.error('获取申请列表失败:', error)
    ElMessage.error(error.message || '获取列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleCancel = async (row: any) => {
  console.log('点击撤销，整行数据:', row)  // 查看完整数据
  console.log('row 的所有字段:', Object.keys(row))  // 查看有哪些字段
  
  // 尝试多种可能的 ID 字段名
  const id = row.pkCa || row.id || row.applicationId || row.pkApplication
  
  console.log('提取的 ID:', id)
  
  if (!id) {
    console.error('找不到 ID 字段，行数据:', row)
    ElMessage.error('申请 ID 无效，请刷新页面重试')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要撤销此申请吗？撤销后将无法恢复', '提示', {
      type: 'warning',
      confirmButtonText: '确认撤销',
      cancelButtonText: '取消',
    })
    
    console.log('准备调用 API 撤销，使用 ID:', id)
    const res: any = await cancelApplication(id)
    console.log('撤销 API 响应:', res)
    
    if (res.code === 200) {
      ElMessage.success('撤销成功')
      fetchData()
    } else {
      ElMessage.error(res.message || '撤销失败')
    }
  } catch (error: any) {
    // 如果不是用户取消操作，则显示错误
    if (error !== 'cancel') {
      console.error('撤销申请错误:', error)
      ElMessage.error(error.message || '请求出错，请稍后重试')
    }
  }
}

const handleView = (row: any) => {
  // 跳转到证明详情页面
  const pkCa = row.pkCa || row.id || row.applicationId || row.pkApplication
  if (!pkCa) {
    ElMessage.error('申请 ID 无效，请刷新页面重试')
    return
  }
  router.push(`/student/certificate-detail/${pkCa}`)
}

const resetFilter = () => {
  filterStatus.value = null
  dateRange.value = null
  fetchData()
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

.list-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header {
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

.filter-bar {
  padding: 16px;
  background: #F5F7FA;
  border-radius: 8px;
  margin-bottom: 16px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
