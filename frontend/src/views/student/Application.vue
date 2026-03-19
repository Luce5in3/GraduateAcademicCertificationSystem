<template>
  <div class="application-page">
    <el-card class="form-card" :class="{ 'card-enter': true }">
      <template #header>
        <div class="card-header">
          <div class="header-icon">
            <el-icon :size="32" color="var(--primary-color)"><DocumentAdd /></el-icon>
          </div>
          <h2>证书申请</h2>
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
        <el-form-item label="证书类型" prop="certificateType" class="form-item">
          <el-select 
            v-model="form.certificateType" 
            placeholder="请选择需要申请的证书类型" 
            style="width: 100%"
            size="large"
            :loading="templatesLoading"
            class="form-select"
          >
            <el-option 
              v-for="template in certificateTemplates" 
              :key="template.value" 
              :label="template.label" 
              :value="template.value" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="申请理由" prop="reason" class="form-item">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明申请该证书的具体理由（必填）"
            maxlength="500"
            show-word-limit
            size="large"
            class="form-textarea"
          />
        </el-form-item>

        <el-divider content-position="left">补充信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请份数" prop="quantity" class="form-item">
              <el-input-number 
                v-model="form.quantity" 
                :min="1" 
                :max="10"
                style="width: 100%"
                size="large"
                class="form-input-number"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否加急" prop="isUrgent" class="form-item">
              <div class="switch-container">
                <el-switch 
                  v-model="form.isUrgent"
                  active-text="是"
                  inactive-text="否"
                  size="large"
                  class="form-switch"
                />
                <el-text type="info" size="small" class="switch-hint">
                  加急申请将优先处理
                </el-text>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注信息" class="form-item">
          <el-input
            v-model="form.notes"
            type="textarea"
            :rows="2"
            placeholder="其他需要说明的信息（选填）"
            maxlength="200"
            show-word-limit
            size="large"
            class="form-textarea"
          />
        </el-form-item>

        <el-form-item class="form-item">
          <div class="form-actions">
            <el-button 
              type="primary" 
              @click="handleSubmit" 
              :loading="loading"
              size="large"
              class="submit-button"
            >
              <el-icon><DocumentChecked /></el-icon>
              提交申请
            </el-button>
            <el-button @click="handleReset" size="large" class="reset-button">
              <el-icon><RefreshLeft /></el-icon>
              重置表单
            </el-button>
          </div>
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
import { DocumentChecked, RefreshLeft, DocumentAdd } from '@element-plus/icons-vue'
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
  
  // 添加表单元素动画效果
  setTimeout(() => {
    const formItems = document.querySelectorAll('.form-item')
    formItems.forEach((item, index) => {
      setTimeout(() => {
        (item as HTMLElement).style.opacity = '1'
        (item as HTMLElement).style.transform = 'translateY(0)'
      }, 150 * (index + 1))
    })
  }, 100)
})
</script>

<style scoped>
.application-page {
  max-width: 900px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.form-card {
  box-shadow: var(--shadow-sm);
  border-radius: var(--border-radius-md);
  overflow: hidden;
  transform: translateY(0);
  opacity: 1;
  animation: cardEnter 0.8s ease-out forwards;
}

@keyframes cardEnter {
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.card-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xl) 0;
  text-align: center;
  background: linear-gradient(135deg, rgba(22, 119, 255, 0.05) 0%, rgba(0, 80, 179, 0.05) 100%);
  border-bottom: 1px solid var(--border-color-light);
}

.header-icon {
  margin-bottom: var(--spacing-md);
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

.card-header h2 {
  margin: 0 0 8px 0;
  color: var(--text-color);
  font-size: 24px;
  font-weight: 600;
  animation: textFadeIn 1s ease-out forwards;
}

.card-header p {
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

.application-form {
  padding: var(--spacing-xl);
}

.form-item {
  margin-bottom: var(--spacing-lg);
  opacity: 1;
  transform: translateY(0);
  transition: all 0.5s ease-out;
}

/* 表单元素样式 */
.form-select,
.form-textarea,
.form-input-number {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-select .el-input__wrapper) {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

:deep(.el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.2);
}

:deep(.el-textarea__inner) {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-textarea__inner:hover) {
  border-color: var(--primary-color);
}

:deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.2);
}

:deep(.el-input-number__decrease:hover,
.el-input-number__increase:hover) {
  color: var(--primary-color);
}

/* 开关样式 */
.switch-container {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.switch-hint {
  flex: 1;
  margin-left: 0 !important;
}

:deep(.el-switch__core) {
  transition: all var(--transition-fast);
}

:deep(.el-switch.is-active .el-switch__core) {
  background-color: var(--primary-color);
}

/* 分割线 */
:deep(.el-divider__text) {
  font-weight: 500;
  color: var(--text-color-secondary);
  background-color: var(--card-bg);
  padding: 0 var(--spacing-sm);
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.submit-button {
  flex: 1;
  border-radius: var(--border-radius-md);
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.3);
}

.submit-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.submit-button:hover::before {
  left: 100%;
}

.reset-button {
  border-radius: var(--border-radius-md);
  padding: 12px 24px;
  transition: all var(--transition-normal);
}

.reset-button:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .application-page {
    padding: var(--spacing-md);
  }
  
  .application-form {
    padding: var(--spacing-lg);
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .el-col {
    width: 100% !important;
  }
  
  .el-form-item {
    margin-bottom: var(--spacing-md);
  }
}
</style>
