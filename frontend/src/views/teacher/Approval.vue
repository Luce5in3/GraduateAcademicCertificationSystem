<template>
  <div class="approval-page">
    <el-card>
      <template #header>
        <h2>待审批列表</h2>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="certificateType" label="证书类型" width="150" />
        <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleApprove(row, 1)">
              通过
            </el-button>
            <el-button type="danger" size="small" @click="handleReject(row)">
              拒绝
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

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" title="审批意见" width="500px">
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="审批结果">
          <el-tag :type="approvalForm.result === 1 ? 'success' : 'danger'">
            {{ approvalForm.result === 1 ? '通过' : '拒绝' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprovalConfirm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getPendingApprovals, submitApproval } from '@/api/approval'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)

const approvalForm = reactive({
  applicationId: '',
  result: 1,
  comment: '',
})

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getPendingApprovals({
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

const handleApprove = (row: any, result: number) => {
  approvalForm.applicationId = row.id
  approvalForm.result = result
  approvalForm.comment = ''
  dialogVisible.value = true
}

const handleReject = (row: any) => {
  approvalForm.applicationId = row.id
  approvalForm.result = 0
  approvalForm.comment = ''
  dialogVisible.value = true
}

const submitApprovalConfirm = async () => {
  submitting.value = true
  try {
    const res: any = await submitApproval(approvalForm)
    if (res.code === 200) {
      ElMessage.success('审批成功')
      dialogVisible.value = false
      fetchData()
    }
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
