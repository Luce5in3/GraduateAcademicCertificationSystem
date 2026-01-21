<template>
  <div class="certificate-detail">
    <el-card class="detail-card">
      <template #header>
        <div class="header">
          <div class="header-left">
            <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
            <h2>📜 证明详情</h2>
          </div>
          <div class="header-right">
            <el-button type="success" @click="handlePreview" v-if="detail && detail.status === 2">
              <el-icon><View /></el-icon>
              预览证明
            </el-button>
            <el-button type="primary" @click="handleExport" v-if="detail && detail.status === 2">
              <el-icon><Download /></el-icon>
              导出电子版
            </el-button>
          </div>
        </div>
      </template>

      <el-skeleton :loading="loading" :rows="10" animated>
        <div v-if="detail" class="detail-content">
          <!-- 申请信息 -->
          <div class="info-section">
            <h3 class="section-title">申请信息</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="申请编号">{{ detail.applicationNo }}</el-descriptions-item>
              <el-descriptions-item label="申请状态">
                <el-tag :type="getStatusType(detail.status)">{{ getStatusText(detail.status) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="证明类型">{{ detail.certificateType }}</el-descriptions-item>
              <el-descriptions-item label="申请份数">{{ detail.copies || 1 }} 份</el-descriptions-item>
              <el-descriptions-item label="是否加急">
                <el-tag :type="detail.urgent ? 'danger' : 'info'" size="small">
                  {{ detail.urgent ? '加急' : '普通' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="申请时间">{{ detail.createTime }}</el-descriptions-item>
              <el-descriptions-item label="申请理由" :span="2">
                {{ detail.applicationReason || '无' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 电子证明预览 -->
          <div class="certificate-section" v-if="detail.status === 2">
            <h3 class="section-title">电子证明</h3>
            <div class="certificate-preview" id="certificate-preview">
              <!-- 成绩证明模板 -->
              <div v-if="detail.certificateType === '成绩证明'" class="certificate-content grade-certificate" id="certificate-content">
                <h1 class="certificate-title">成绩证明</h1>
                <div class="certificate-body">
                  <p class="certificate-intro">兹证明：</p>
                  <div class="student-info">
                    <p><strong>姓名：</strong>{{ detail.studentName }}</p>
                    <p><strong>学号：</strong>{{ detail.studentNo }}</p>
                    <p><strong>学院：</strong>{{ detail.college }}</p>
                    <p><strong>专业：</strong>{{ detail.major }}</p>
                    <p><strong>班级：</strong>{{ detail.className }}</p>
                    <p><strong>年级：</strong>{{ detail.grade }}</p>
                    <p><strong>学历层次：</strong>{{ detail.educationLevel }}</p>
                    <p><strong>学习形式：</strong>{{ detail.studyType }}</p>
                  </div>
                  
                  <div class="grade-info" v-if="applicationDataParsed">
                    <p class="main-text">
                      该生于 <strong>{{ applicationDataParsed.enrollmentDate || detail.enrollmentDate }}</strong> 
                      入学，预计于 <strong>{{ applicationDataParsed.graduationDate || detail.graduationDate }}</strong> 毕业。
                    </p>
                    <p class="main-text">
                      在校期间学习成绩优良，特此证明。
                    </p>
                  </div>
                  
                  <div class="certificate-footer">
                    <p class="issue-unit">{{ detail.college || '学校名称' }}</p>
                    <p class="issue-date">开具日期：{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
                  </div>
                </div>
              </div>

              <!-- 学历证明模板 -->
              <div v-else-if="detail.certificateType === '学历证明'" class="certificate-content degree-certificate" id="certificate-content">
                <h1 class="certificate-title">学历证明</h1>
                <div class="certificate-body">
                  <p class="certificate-intro">兹证明：</p>
                  <div class="student-info">
                    <p><strong>姓名：</strong>{{ detail.studentName }}</p>
                    <p><strong>学号：</strong>{{ detail.studentNo }}</p>
                    <p><strong>学院：</strong>{{ detail.college }}</p>
                    <p><strong>专业：</strong>{{ detail.major }}</p>
                  </div>
                  
                  <div class="degree-info" v-if="applicationDataParsed">
                    <p class="main-text">
                      该生于 <strong>{{ applicationDataParsed.enrollmentDate || detail.enrollmentDate }}</strong> 
                      至 <strong>{{ applicationDataParsed.graduationDate || detail.graduationDate }}</strong> 
                      在我校 <strong>{{ detail.major }}</strong> 专业学习，
                      学制 <strong>{{ applicationDataParsed.studyYears || '4' }}</strong> 年，
                      学习形式为 <strong>{{ detail.studyType }}</strong>，
                      修完教学计划规定的全部课程，成绩合格，准予毕业。
                    </p>
                    <p class="main-text">
                      符合《中华人民共和国学位条例》规定条件，
                      授予 <strong>{{ applicationDataParsed.degree || '学士' }}</strong> 学位。
                    </p>
                  </div>
                  
                  <div class="certificate-footer">
                    <p class="issue-unit">{{ detail.college || '学校名称' }}</p>
                    <p class="issue-date">开具日期：{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
                  </div>
                </div>
              </div>

              <!-- 在读证明模板 -->
              <div v-else-if="detail.certificateType === '在读证明'" class="certificate-content study-certificate" id="certificate-content">
                <h1 class="certificate-title">在读证明</h1>
                <div class="certificate-body">
                  <p class="certificate-intro">兹证明：</p>
                  <div class="student-info">
                    <p><strong>姓名：</strong>{{ detail.studentName }}</p>
                    <p><strong>学号：</strong>{{ detail.studentNo }}</p>
                    <p><strong>学院：</strong>{{ detail.college }}</p>
                    <p><strong>专业：</strong>{{ detail.major }}</p>
                    <p><strong>年级：</strong>{{ detail.grade }}</p>
                  </div>
                  
                  <div class="study-info" v-if="applicationDataParsed">
                    <p class="main-text">
                      该生系我校 <strong>{{ detail.educationLevel }}</strong> 在读学生，
                      于 <strong>{{ applicationDataParsed.enrollmentDate || detail.enrollmentDate }}</strong> 入学，
                      预计于 <strong>{{ applicationDataParsed.graduationDate || detail.graduationDate }}</strong> 毕业。
                      学习形式为 <strong>{{ detail.studyType }}</strong>。
                    </p>
                    <p class="main-text">特此证明。</p>
                  </div>
                  
                  <div class="certificate-footer">
                    <p class="issue-unit">{{ detail.college || '学校名称' }}</p>
                    <p class="issue-date">开具日期：{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
                  </div>
                </div>
              </div>

              <!-- 其他证明类型的通用模板 -->
              <div v-else class="certificate-content general-certificate" id="certificate-content">
                <h1 class="certificate-title">{{ detail.certificateType }}</h1>
                <div class="certificate-body">
                  <p class="certificate-intro">兹证明：</p>
                  <div class="student-info">
                    <p><strong>姓名：</strong>{{ detail.studentName }}</p>
                    <p><strong>学号：</strong>{{ detail.studentNo }}</p>
                    <p><strong>学院：</strong>{{ detail.college }}</p>
                    <p><strong>专业：</strong>{{ detail.major }}</p>
                  </div>
                  
                  <div class="general-info">
                    <p class="main-text">{{ detail.applicationReason || '相关证明内容' }}</p>
                    <p class="main-text">特此证明。</p>
                  </div>
                  
                  <div class="certificate-footer">
                    <p class="issue-unit">{{ detail.college || '学校名称' }}</p>
                    <p class="issue-date">开具日期：{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 审批未通过提示 -->
          <el-alert
            v-if="detail.status !== 2"
            :title="getAlertTitle()"
            :type="getAlertType()"
            :description="getAlertDescription()"
            show-icon
            :closable="false"
            style="margin-top: 20px"
          />
        </div>
      </el-skeleton>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Download, View } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const detail = ref<any>(null)

// 解析 applicationData JSON
const applicationDataParsed = computed(() => {
  if (!detail.value?.applicationData) return null
  try {
    return JSON.parse(detail.value.applicationData)
  } catch (e) {
    console.error('解析 applicationData 失败:', e)
    return null
  }
})

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

const getGender = (gender: number) => {
  return gender === 1 ? '男' : '女'
}

const getCurrentDate = () => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

const getAlertTitle = () => {
  if (detail.value?.status === 0) return '等待审批中'
  if (detail.value?.status === 1) return '审批处理中'
  if (detail.value?.status === 3) return '审批未通过'
  if (detail.value?.status === 4) return '申请已撤销'
  return '提示'
}

const getAlertType = () => {
  if (detail.value?.status === 0 || detail.value?.status === 1) return 'warning'
  if (detail.value?.status === 3 || detail.value?.status === 4) return 'error'
  return 'info'
}

const getAlertDescription = () => {
  if (detail.value?.status === 0) return '您的申请正在等待审批，请耐心等待。'
  if (detail.value?.status === 1) return '您的申请正在审批流程中，请耐心等待。'
  if (detail.value?.status === 3) {
    return `拒绝原因：${detail.value.rejectReason || '未提供拒绝原因'}`
  }
  if (detail.value?.status === 4) return '该申请已被撤销，如有需要请重新提交申请。'
  return ''
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const pkCa = route.params.pkCa as string
    const res: any = await request({
      url: `/application/${pkCa}/detail`,
      method: 'get',
    })
    
    if (res.code === 200) {
      detail.value = res.data
    } else {
      ElMessage.error(res.message || '获取详情失败')
    }
  } catch (error: any) {
    console.error('获取证明详情失败:', error)
    ElMessage.error(error.message || '获取详情失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const handlePreview = () => {
  // 在新窗口打开证明预览页面
  const pkCa = route.params.pkCa as string
  const previewUrl = `/certificate-preview/${pkCa}`
  window.open(previewUrl, '_blank')
}

const handleExport = () => {
  // 直接打开预览页面，用户在那里点击打印/导出PDF
  handlePreview()
  ElMessage.success('请在新打开的窗口中点击“打印/导出PDF”按钮')
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.certificate-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.detail-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.header-right {
  display: flex;
  gap: 12px;
}

.detail-content {
  padding: 20px 0;
}

.info-section {
  margin-bottom: 30px;
}

.section-title {
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #409EFF;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.certificate-section {
  margin-top: 30px;
}

.certificate-preview {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}

.certificate-content {
  background: white;
  padding: 60px 80px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-height: 800px;
  position: relative;
}

.certificate-title {
  text-align: center;
  font-size: 36px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 40px;
  letter-spacing: 8px;
}

.certificate-body {
  line-height: 2;
  font-size: 16px;
  color: #303133;
}

.certificate-intro {
  font-size: 18px;
  margin-bottom: 20px;
  text-indent: 2em;
}

.student-info {
  margin: 30px 0;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.student-info p {
  margin: 0;
  font-size: 16px;
}

.student-info strong {
  color: #409EFF;
}

.grade-info,
.degree-info,
.study-info,
.general-info {
  margin: 30px 0;
}

.main-text {
  font-size: 17px;
  line-height: 2.2;
  text-indent: 2em;
  margin: 20px 0;
  text-align: justify;
}

.main-text strong {
  color: #409EFF;
  font-weight: 600;
}

.certificate-footer {
  margin-top: 80px;
  text-align: right;
}

.issue-unit {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 30px;
}

.issue-date {
  font-size: 16px;
  color: #606266;
}

/* 打印样式 */
@media print {
  /* 隐藏所有其他内容 */
  .header,
  .info-section,
  .section-title,
  .el-card__header,
  .detail-content > *:not(.certificate-section) {
    display: none !important;
  }
  
  /* 证明区域占满整个页面 */
  .certificate-detail,
  .detail-card,
  .detail-content,
  .certificate-section {
    margin: 0 !important;
    padding: 0 !important;
    box-shadow: none !important;
    border: none !important;
    background: white !important;
  }
  
  /* 证明预览背景去除 */
  .certificate-preview {
    background: white !important;
    padding: 0 !important;
  }

  /* 证明内容样式优化 */
  .certificate-content {
    padding: 40px !important;
    box-shadow: none !important;
    border: none !important;
    min-height: auto !important;
    page-break-after: avoid;
  }
  
  /* 确保证明标题和内容打印 */
  .certificate-title,
  .certificate-body,
  .student-info,
  .certificate-footer {
    display: block !important;
  }
}
</style>
