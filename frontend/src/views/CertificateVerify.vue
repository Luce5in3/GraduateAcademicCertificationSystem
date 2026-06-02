<template>
  <div class="certificate-verify-page">
    <div class="verify-box">
      <h1 class="title">🔍 证书真伪验证</h1>
      <p class="subtitle">输入证书编号以验证其真实性</p>

      <div class="input-area">
        <el-input
          v-model="certificateId"
          placeholder="请输入证书编号"
          size="large"
          clearable
          @keyup.enter="handleVerify"
        >
          <template #prepend>证书编号</template>
        </el-input>
        <el-button type="primary" size="large" @click="handleVerify" :loading="verifying">验证</el-button>
      </div>

      <!-- 验证结果 -->
      <el-result v-if="verified && !verifying" :type="result.valid ? 'success' : 'error'" :title="result.valid ? '证书验证通过' : '证书无效'">
        <template #sub-title v-if="result.valid">
          此证书由我校教务处颁发，真实有效
        </template>
        <template #sub-title v-else>
          {{ result.message || '未查询到该证书信息，请核实编号后重试' }}
        </template>
      </el-result>

      <el-card v-if="result.valid && !verifying" class="cert-info-card">
        <el-descriptions title="证书信息" :column="2" border>
          <el-descriptions-item label="学生姓名">{{ result.studentName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ result.studentNum }}</el-descriptions-item>
          <el-descriptions-item label="证书类型">{{ result.certificateType }}</el-descriptions-item>
          <el-descriptions-item label="颁发日期">{{ result.issueDate }}</el-descriptions-item>
          <el-descriptions-item label="证书状态" :span="2">
            <el-tag type="success">有效</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const certificateId = ref('')
const verifying = ref(false)
const verified = ref(false)
const result = ref<any>({ valid: false, message: '' })

const handleVerify = async () => {
  if (!certificateId.value.trim()) return
  verifying.value = true
  verified.value = false
  try {
    const res = await axios.get(`/api/certificate/verify/${certificateId.value.trim()}`)
    if (res.data && res.data.code === 200) {
      result.value = res.data.data
      verified.value = true
    }
  } catch {
    result.value = { valid: false, message: '验证失败，请稍后重试' }
    verified.value = true
  } finally { verifying.value = false }
}
</script>

<style scoped>
.certificate-verify-page {
  min-height: calc(100vh - 60px); display: flex; justify-content: center; align-items: flex-start; padding-top: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.verify-box {
  width: 700px; max-width: 95vw; background: #fff; border-radius: 12px; padding: 40px 36px; box-shadow: 0 20px 60px rgba(0,0,0,.2);
}
.title { text-align: center; font-size: 28px; color: #303133; margin-bottom: 8px; }
.subtitle { text-align: center; font-size: 14px; color: #909399; margin-bottom: 28px; }
.input-area { display: flex; gap: 12px; margin-bottom: 24px; }
.input-area .el-input { flex: 1; }
.cert-info-card { margin-top: 20px; }
</style>
