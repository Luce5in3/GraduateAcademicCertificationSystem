<template>
  <div class="application-page">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <h2>📝 证书申请</h2>
          <p>请仔细填写申请信息，我们将尽快处理</p>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="application-form"
      >
        <el-form-item label="证书类型" prop="certificateType">
          <el-select 
            v-model="form.certificateType" 
            placeholder="请选择需要申请的证书类型" 
            style="width: 100%"
            size="large"
            :loading="templatesLoading"
          >
            <el-option 
              v-for="template in certificateTemplates" 
              :key="template.value" 
              :label="template.label" 
              :value="template.value" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="申请理由" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明申请该证书的具体理由（必填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-divider content-position="left">补充信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请份数" prop="quantity">
              <el-input-number 
                v-model="form.quantity" 
                :min="1" 
                :max="10"
                style="width: 100%"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否加急" prop="isUrgent">
              <el-switch 
                v-model="form.isUrgent"
                active-text="是"
                inactive-text="否"
                size="large"
              />
              <el-text type="info" size="small" style="margin-left: 12px">
                加急申请将优先处理
              </el-text>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注信息">
          <el-input
            v-model="form.notes"
            type="textarea"
            :rows="2"
            placeholder="其他需要说明的信息（选填）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-space>
            <el-button 
              type="primary" 
              @click="handleSubmit" 
              :loading="loading"
              size="large"
            >
              <el-icon><DocumentChecked /></el-icon>
              提交申请
            </el-button>
            <el-button @click="handleReset" size="large">
              <el-icon><RefreshLeft /></el-icon>
              重置表单
            </el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { submitApplication, getAvailableTemplates, type CertificateTemplate } from '@/api/application'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentChecked, RefreshLeft } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const templatesLoading = ref(false)
const certificateTemplates = ref<CertificateTemplate[]>([])

const form = reactive({
  certificateType: '',
  reason: '',
  quantity: 1,
  isUrgent: false,
  notes: '',
})

const rules: FormRules = {
  certificateType: [
    { required: true, message: '请选择证书类型', trigger: 'change' },
  ],
  reason: [
    { required: true, message: '请输入申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由至少填写10个字', trigger: 'blur' },
  ],
  quantity: [
    { required: true, message: '请输入申请份数', trigger: 'blur' },
  ],
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 获取选中的模板信息
        const selectedTemplate = certificateTemplates.value.find(t => t.value === form.certificateType)
        const templateName = selectedTemplate?.label || form.certificateType
        
        await ElMessageBox.confirm(
          `您将申请 ${form.quantity} 份「${templateName}」${form.isUrgent ? '（加急）' : ''}，确认提交？`,
          '确认信息',
          {
            confirmButtonText: '确认提交',
            cancelButtonText: '取消',
            type: 'info',
          }
        )
        
        loading.value = true
        // 构建提交数据，使用 pkCt 而不是 certificateType
        const submitData = {
          pkCt: form.certificateType, // 模板ID
          applicationReason: form.reason,
          copies: form.quantity,
          urgent: form.isUrgent ? 1 : 0,
          applicationData: JSON.stringify({
            notes: form.notes
          })
        }
        
        const res: any = await submitApplication(submitData)
        if (res.code === 200) {
          ElMessage.success('申请提交成功，请耐心等待审批')
          handleReset()
          router.push('/student/my-applications')
        }
      } catch (error) {
        // 用户取消操作
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
}

// 加载证书模板列表
const loadTemplates = async () => {
  try {
    templatesLoading.value = true
    const res: any = await getAvailableTemplates()
    if (res.code === 200) {
      certificateTemplates.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载证书类型失败')
  } finally {
    templatesLoading.value = false
  }
}

// 页面加载时获取模板列表
onMounted(() => {
  loadTemplates()
})
</script>

<style scoped>
.application-page {
  max-width: 900px;
  margin: 0 auto;
}

.form-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.application-form {
  padding: 20px 0;
}

:deep(.el-divider__text) {
  font-weight: 500;
  color: #606266;
}
</style>
