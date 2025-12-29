<template>
  <div class="application-page">
    <el-card>
      <template #header>
        <h2>证书申请</h2>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 600px"
      >
        <el-form-item label="证书类型" prop="certificateType">
          <el-select v-model="form.certificateType" placeholder="请选择证书类型" style="width: 100%">
            <el-option label="在读证明" value="在读证明" />
            <el-option label="成绩单" value="成绩单" />
            <el-option label="毕业证明" value="毕业证明" />
            <el-option label="学位证明" value="学位证明" />
          </el-select>
        </el-form-item>

        <el-form-item label="申请理由" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入申请理由（选填）"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            提交申请
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { submitApplication } from '@/api/application'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  certificateType: '',
  reason: '',
})

const rules: FormRules = {
  certificateType: [
    { required: true, message: '请选择证书类型', trigger: 'change' },
  ],
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res: any = await submitApplication(form)
        if (res.code === 200) {
          ElMessage.success('申请提交成功')
          handleReset()
          router.push('/student/my-applications')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
}
</script>

<style scoped>
.application-page {
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  margin: 0;
  color: #303133;
}
</style>
