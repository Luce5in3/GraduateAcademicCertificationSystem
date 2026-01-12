<template>
  <div class="approval-page">
    <el-card class="approval-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2>✅ 审批管理</h2>
            <p>审批学生的证书申请，请仔细核对信息</p>
          </div>
          <el-badge :value="total" :max="99" class="badge">
            <el-tag type="warning" size="large">待审批</el-tag>
          </el-badge>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="studentName" label="学生姓名" width="120" align="center" />
        <el-table-column prop="studentId" label="学号" width="150" align="center" />
        <el-table-column prop="certificateType" label="证书类型" width="150" align="center" />
        <el-table-column prop="quantity" label="申请份数" width="100" align="center">
          <template #default="{ row }">
            {{ row.quantity || 1 }} 份
          </template>
        </el-table-column>
        <el-table-column prop="isUrgent" label="是否加急" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isUrgent" type="danger" size="small" effect="dark">加急</el-tag>
            <el-tag v-else type="info" size="small">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="申请时间" width="180" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button 
                type="primary" 
                size="small" 
                :icon="View"
                @click="handleViewDetail(row)"
              >
                查看
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                :icon="Select"
                @click="handleApprove(row, 1)"
              >
                通过
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                :icon="CloseBold"
                @click="handleReject(row)"
              >
                拒绝
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

    <!-- 审批对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="approvalForm.result === 1 ? '审批通过' : '审批拒绝'" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="申请信息">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="学生姓名">{{ currentApplication?.studentName }}</el-descriptions-item>
            <el-descriptions-item label="学号">{{ currentApplication?.studentId }}</el-descriptions-item>
            <el-descriptions-item label="证书类型">{{ currentApplication?.certificateType }}</el-descriptions-item>
            <el-descriptions-item label="申请份数">{{ currentApplication?.quantity || 1 }} 份</el-descriptions-item>
            <el-descriptions-item label="是否加急">
              <el-tag v-if="currentApplication?.isUrgent" type="danger" size="small">加急</el-tag>
              <el-tag v-else type="info" size="small">普通</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-form-item>
        <el-form-item label="审批结果">
          <el-tag :type="approvalForm.result === 1 ? 'success' : 'danger'" size="large" effect="dark">
            {{ approvalForm.result === 1 ? '通过' : '拒绝' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审批意见" :required="approvalForm.result === 0">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="approvalForm.result === 1 ? '请输入审批意见（选填）' : '拒绝原因（必填）'"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-space>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            :type="approvalForm.result === 1 ? 'success' : 'danger'" 
            @click="submitApprovalConfirm" 
            :loading="submitting"
          >
            确认{{ approvalForm.result === 1 ? '通过' : '拒绝' }}
          </el-button>
        </el-space>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ currentApplication?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentApplication?.studentId }}</el-descriptions-item>
        <el-descriptions-item label="证书类型">{{ currentApplication?.certificateType }}</el-descriptions-item>
        <el-descriptions-item label="申请份数">{{ currentApplication?.quantity || 1 }} 份</el-descriptions-item>
        <el-descriptions-item label="是否加急">
          <el-tag v-if="currentApplication?.isUrgent" type="danger" size="small">加急</el-tag>
          <el-tag v-else type="info" size="small">普通</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentApplication?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="申请理由" :span="2">{{ currentApplication?.reason }}</el-descriptions-item>
        <el-descriptions-item label="备注信息" :span="2">{{ currentApplication?.notes || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getPendingApprovals, submitApproval } from '@/api/approval'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Select, CloseBold } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentApplication = ref<any>(null)

const approvalForm = reactive({
  applicationId: '',
  result: 1,
  comment: '',
})

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getPendingApprovals({
      current: currentPage.value,
      size: pageSize.value,
    })
    if (res.code === 200) {
      tableData.value = res.data.records || res.data || []
      total.value = res.data.total || tableData.value.length
    }
  } catch (error: any) {
    console.error('获取待审批列表失败:', error)
    ElMessage.error(error.message || '获取列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleApprove = (row: any, result: number) => {
  currentApplication.value = row
  approvalForm.applicationId = row.id
  approvalForm.result = result
  approvalForm.comment = ''
  dialogVisible.value = true
}

const handleReject = (row: any) => {
  currentApplication.value = row
  approvalForm.applicationId = row.id
  approvalForm.result = 0
  approvalForm.comment = ''
  dialogVisible.value = true
}

const handleViewDetail = (row: any) => {
  currentApplication.value = row
  detailVisible.value = true
}

const submitApprovalConfirm = async () => {
  // 拒绝时必须填写理由
  if (approvalForm.result === 0 && !approvalForm.comment.trim()) {
    ElMessage.warning('请填写拒绝理由')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认${approvalForm.result === 1 ? '通过' : '拒绝'}该申请吗？`,
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    submitting.value = true
    const res: any = await submitApproval(approvalForm)
    if (res.code === 200) {
      ElMessage.success('审批成功')
      dialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    // 用户取消操作
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-page {
  max-width: 1400px;
  margin: 0 auto;
}

.approval-card {
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

.badge {
  margin-right: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
