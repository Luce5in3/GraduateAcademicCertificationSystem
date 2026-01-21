<template>
  <div class="template-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>证明模板管理</span>
          <el-button type="primary" @click="handleCreate">新增模板</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="templateName" label="模板名称" min-width="150" />
        <el-table-column prop="templateCode" label="编码" width="120" />
        <el-table-column prop="templateType" label="类型" width="120" />
        <el-table-column prop="approvalFlow" label="审批等级" width="100" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive === 1 ? 'success' : 'danger'">
              {{ row.isActive === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 编辑/新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '新增模板' : '编辑模板'"
      width="60%"
    >
      <el-form :model="formData" label-width="100px" ref="formRef" :rules="rules">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" />
        </el-form-item>
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="formData.templateCode" />
        </el-form-item>
        <el-form-item label="模板类型" prop="templateType">
          <el-input v-model="formData.templateType" />
        </el-form-item>
        <el-form-item label="审批等级" prop="approvalFlow">
          <el-select v-model="formData.approvalFlow" placeholder="选择审批等级">
            <el-option label="1级 (导师/辅导员)" value="1" />
            <el-option label="2级 (学院)" value="2" />
            <el-option label="3级 (教务处/校级)" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-radio-group v-model="formData.isActive">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="模板内容" prop="templateContent">
          <el-input
            v-model="formData.templateContent"
            type="textarea"
            :rows="10"
            placeholder="支持使用 {studentName}, {studentNo}, {college} 等变量"
          />
        </el-form-item>
        <el-form-item label="必填字段" prop="requiredFields">
          <el-input v-model="formData.requiredFields" placeholder='JSON数组, 如 ["studentName", "studentNo"]' />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAdminTemplates, createAdminTemplate, updateAdminTemplate, deleteAdminTemplate } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  current: 1,
  size: 10
})

const dialogVisible = ref(false)
const dialogType = ref('create')
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  pkCt: '',
  templateName: '',
  templateCode: '',
  templateType: '',
  templateContent: '',
  requiredFields: '',
  approvalFlow: '1',
  isActive: 1,
  sortOrder: 1,
  remark: ''
})

const rules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  templateType: [{ required: true, message: '请输入模板类型', trigger: 'blur' }],
  templateContent: [{ required: true, message: '请输入模板内容', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getAdminTemplates(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  dialogType.value = 'create'
  Object.assign(formData, {
    pkCt: '',
    templateName: '',
    templateCode: '',
    templateType: '',
    templateContent: '',
    requiredFields: '',
    approvalFlow: '1',
    isActive: 1,
    sortOrder: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该模板吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    const res: any = await deleteAdminTemplate(row.pkCt)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  })
}

const submitForm = async () => {
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = dialogType.value === 'create' ? createAdminTemplate : updateAdminTemplate
        const res: any = await api(formData)
        if (res.code === 200) {
          ElMessage.success('操作成功')
          dialogVisible.value = false
          fetchData()
        }
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(fetchData)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
