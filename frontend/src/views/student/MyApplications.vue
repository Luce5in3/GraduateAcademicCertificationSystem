<template>
  <div class="my-applications">
    <el-card class="list-card" :class="{ 'card-enter': true }">
      <template #header>
        <div class="header">
          <div class="header-left">
            <div class="header-icon">
              <el-icon :size="28" color="var(--primary-color)"><DocumentCopy /></el-icon>
            </div>
            <h2>我的申请</h2>
            <p>查看和管理您的所有证书申请</p>
          </div>
          <el-button type="primary" @click="router.push('/student/application')" size="large" class="new-application-button">
            <el-icon><Plus /></el-icon>
            新建申请
          </el-button>
        </div>
      </template>

      <!-- 筛选条件 -->
      <div class="filter-bar" :class="{ 'filter-enter': true }">
        <el-space wrap>
          <el-select v-model="filterStatus" placeholder="筛选状态" clearable @change="fetchData" style="width: 140px" size="large">
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
            size="large"
          />
          <el-button @click="resetFilter" :icon="RefreshLeft" size="large" class="reset-button">
            重置筛选
          </el-button>
        </el-space>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe style="margin-top: 16px" class="applications-table">
        <el-table-column prop="certificateType" label="证书类型" width="150" align="center">
          <template #default="{ row }">
            <div class="certificate-type">{{ row.certificateType }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审批状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="dark" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="copies" label="申请份数" width="100" align="center">
          <template #default="{ row }">
            <span class="copy-count">{{ row.copies || 1 }} 份</span>
          </template>
        </el-table-column>
        <el-table-column prop="urgent" label="是否加急" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.urgent" type="danger" size="small" class="urgent-tag">加急</el-tag>
            <el-tag v-else type="info" size="small" class="normal-tag">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicationReason" label="申请理由" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="application-reason">{{ row.applicationReason }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" align="center">
          <template #default="{ row }">
            <div class="create-time">{{ row.createTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button
                type="primary"
                size="small"
                link
                @click="handleView(row)"
                class="view-button"
              >
                查看详情
              </el-button>
              <el-button
                type="danger"
                size="small"
                link
                :disabled="row.status !== 0"
                @click="handleCancel(row)"
                class="cancel-button"
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
          size="large"
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
import { Plus, RefreshLeft, DocumentCopy } from '@element-plus/icons-vue'

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
  // 尝试多种可能的 ID 字段名
  const id = row.pkCa || row.id || row.applicationId || row.pkApplication
  
  if (!id) {
    ElMessage.error('申请 ID 无效，请刷新页面重试')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要撤销此申请吗？撤销后将无法恢复', '提示', {
      type: 'warning',
      confirmButtonText: '确认撤销',
      cancelButtonText: '取消',
    })
    
    loading.value = true
    const res: any = await cancelApplication(id)
    
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
  } finally {
    loading.value = false
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
  
  // 添加动画效果
  setTimeout(() => {
    const card = document.querySelector('.list-card')
    if (card) {
      (card as HTMLElement).style.opacity = '1'
      (card as HTMLElement).style.transform = 'translateY(0)'
    }
    
    const filterBar = document.querySelector('.filter-bar')
    if (filterBar) {
      (filterBar as HTMLElement).style.opacity = '1'
      (filterBar as HTMLElement).style.transform = 'translateY(0)'
    }
  }, 100)
})
</script>

<style scoped>
.my-applications {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.list-card {
  box-shadow: var(--shadow-sm);
  border-radius: var(--border-radius-md);
  overflow: hidden;
  transform: translateY(0);
  opacity: 1;
  transition: all 0.8s ease-out;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  background: linear-gradient(135deg, rgba(22, 119, 255, 0.05) 0%, rgba(0, 80, 179, 0.05) 100%);
  border-bottom: 1px solid var(--border-color-light);
}

.header-left {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.header-icon {
  margin-bottom: var(--spacing-sm);
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.header-left h2 {
  margin: 0 0 4px 0;
  color: var(--text-color);
  font-size: 24px;
  font-weight: 600;
  animation: textFadeIn 1s ease-out forwards;
}

.header-left p {
  margin: 0;
  color: var(--text-color-light);
  font-size: 14px;
  animation: textFadeIn 1s ease-out 0.3s forwards;
  opacity: 0;
}

@keyframes textFadeIn {
  to {
    opacity: 1;
  }
}

.new-application-button {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-normal);
}

.new-application-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.3);
}

.filter-bar {
  padding: var(--spacing-lg);
  background: var(--bg-color);
  border-radius: var(--border-radius-md);
  margin: var(--spacing-lg);
  transform: translateY(0);
  opacity: 1;
  transition: all 0.6s ease-out 0.2s;
}

.reset-button {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-normal);
}

.reset-button:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.applications-table {
  margin: 0 var(--spacing-lg) var(--spacing-lg);
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

:deep(.el-table__header-wrapper th) {
  background-color: rgba(22, 119, 255, 0.05);
  font-weight: 600;
  color: var(--text-color);
}

:deep(.el-table__row:hover) {
  background-color: rgba(22, 119, 255, 0.02);
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.urgent-tag {
  border-radius: 10px;
}

.normal-tag {
  border-radius: 10px;
}

.certificate-type,
.copy-count,
.application-reason,
.create-time {
  transition: all var(--transition-fast);
}

.view-button:hover {
  color: var(--primary-color);
}

.cancel-button:hover:not(:disabled) {
  color: var(--danger-color);
}

.pagination {
  margin: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

:deep(.el-pagination__item:hover) {
  color: var(--primary-color);
}

:deep(.el-pagination__item.active) {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .my-applications {
    padding: var(--spacing-md);
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
    padding: var(--spacing-md);
  }
  
  .filter-bar {
    margin: var(--spacing-md);
    padding: var(--spacing-md);
  }
  
  .applications-table {
    margin: 0 var(--spacing-md) var(--spacing-md);
  }
  
  .pagination {
    margin: var(--spacing-md);
  }
}
</style>
