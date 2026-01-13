<template>
  <div class="certificate-preview-page">
    <el-skeleton :loading="loading" :rows="10" animated>
      <div v-if="detail" class="certificate-wrapper">
        <!-- 操作按钮区域 - 仅在屏幕显示时可见 -->
        <div class="action-bar no-print">
          <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
          <el-button type="primary" @click="handlePrint" :icon="Printer">打印/导出PDF</el-button>
        </div>

        <!-- 证明内容 -->
        <div class="certificate-container">
          <!-- 1. 在读证明模板 -->
          <div v-if="detail.certificateType === '在读证明'" class="certificate-content">
            <div class="school-header">
              <h1 class="school-name">{{ detail.schoolName || '学校名称' }}</h1>
            </div>
            <h1 class="certificate-title">在读证明</h1>
            <div class="certificate-body">
              <p class="certificate-intro">兹证明：</p>
              <div class="student-info">
                <p><span class="info-label">姓名：</span><span class="info-value">{{ detail.studentName }}</span></p>
                <p><span class="info-label">性别：</span><span class="info-value">{{ detail.gender === 1 ? '男' : '女' }}</span></p>
                <p><span class="info-label">身份证号：</span><span class="info-value">{{ detail.idCard }}</span></p>
                <p><span class="info-label">学号：</span><span class="info-value">{{ detail.studentNo }}</span></p>
              </div>
              
              <div class="certificate-text">
                <p class="main-text">
                  系我校 <strong>{{ detail.college }}</strong> <strong>{{ detail.major }}</strong> 专业 
                  <strong>{{ detail.grade }}</strong> 级学生，现在我校就读，
                  学制 <strong>{{ applicationDataParsed?.duration || '4' }}</strong> 年，
                  入学时间为 <strong>{{ formatDate(detail.enrollmentDate) }}</strong>。
                </p>
                <p class="main-text">特此证明。</p>
              </div>
              
              <div class="certificate-footer">
                <div class="seal-area">
                  <p class="seal-text">[学校公章]</p>
                </div>
                <p class="issue-date">{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
              </div>
            </div>
          </div>

          <!-- 2. 成绩证明模板 -->
          <div v-else-if="detail.certificateType === '成绩证明'" class="certificate-content">
            <div class="school-header">
              <h1 class="school-name">{{ detail.schoolName || '学校名称' }}</h1>
            </div>
            <h1 class="certificate-title">成绩证明</h1>
            <div class="certificate-body">
              <p class="certificate-intro">兹证明：</p>
              <div class="student-info">
                <p><span class="info-label">姓名：</span><span class="info-value">{{ detail.studentName }}</span></p>
                <p><span class="info-label">性别：</span><span class="info-value">{{ detail.gender === 1 ? '男' : '女' }}</span></p>
                <p><span class="info-label">学号：</span><span class="info-value">{{ detail.studentNo }}</span></p>
              </div>
              
              <div class="certificate-text">
                <p class="main-text">
                  系我校 <strong>{{ detail.college }}</strong> <strong>{{ detail.major }}</strong> 专业学生。
                  该生在校期间成绩如下：
                </p>
                <div class="score-details" v-if="applicationDataParsed?.scoreDetails">
                  <p class="score-text">{{ applicationDataParsed.scoreDetails }}</p>
                </div>
                <p class="main-text">以上成绩属实。</p>
                <p class="main-text">特此证明。</p>
              </div>
              
              <div class="certificate-footer">
                <div class="seal-area">
                  <p class="seal-text">[教务处公章]</p>
                </div>
                <p class="issue-date">{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
              </div>
            </div>
          </div>

          <!-- 3. 学历证明模板 -->
          <div v-else-if="detail.certificateType === '学历证明'" class="certificate-content">
            <div class="school-header">
              <h1 class="school-name">{{ detail.schoolName || '学校名称' }}</h1>
            </div>
            <h1 class="certificate-title">学历证明</h1>
            <div class="certificate-body">
              <p class="certificate-intro">兹证明：</p>
              <div class="student-info">
                <p><span class="info-label">姓名：</span><span class="info-value">{{ detail.studentName }}</span></p>
                <p><span class="info-label">性别：</span><span class="info-value">{{ detail.gender === 1 ? '男' : '女' }}</span></p>
                <p><span class="info-label">身份证号：</span><span class="info-value">{{ detail.idCard }}</span></p>
              </div>
              
              <div class="certificate-text">
                <p class="main-text">
                  于 <strong>{{ formatDate(detail.enrollmentDate) }}</strong> 至 
                  <strong>{{ formatDate(applicationDataParsed?.graduationDate) }}</strong> 
                  在我校 <strong>{{ detail.college }}</strong> <strong>{{ detail.major }}</strong> 专业学习，
                  学制 <strong>{{ applicationDataParsed?.duration || '4' }}</strong> 年，
                  修完教学计划规定的全部课程，成绩合格，准予毕业。
                </p>
                <div class="certificate-numbers" v-if="applicationDataParsed">
                  <p class="cert-number">毕业证书编号：{{ applicationDataParsed.certificateNo || '_______________' }}</p>
                  <p class="cert-number" v-if="applicationDataParsed.degreeNo">学位证书编号：{{ applicationDataParsed.degreeNo }}</p>
                </div>
                <p class="main-text">特此证明。</p>
              </div>
              
              <div class="certificate-footer">
                <div class="seal-area">
                  <p class="seal-text">[学校公章]</p>
                </div>
                <p class="issue-date">{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
              </div>
            </div>
          </div>

          <!-- 4. 学位证明模板 -->
          <div v-else-if="detail.certificateType === '学位证明'" class="certificate-content">
            <div class="school-header">
              <h1 class="school-name">{{ detail.schoolName || '学校名称' }}</h1>
            </div>
            <h1 class="certificate-title">学位证明</h1>
            <div class="certificate-body">
              <p class="certificate-intro">兹证明：</p>
              <div class="student-info">
                <p><span class="info-label">姓名：</span><span class="info-value">{{ detail.studentName }}</span></p>
                <p><span class="info-label">性别：</span><span class="info-value">{{ detail.gender === 1 ? '男' : '女' }}</span></p>
                <p><span class="info-label">身份证号：</span><span class="info-value">{{ detail.idCard }}</span></p>
              </div>
              
              <div class="certificate-text">
                <p class="main-text">
                  于 <strong>{{ formatDate(applicationDataParsed?.graduationDate) }}</strong> 毕业于我校 
                  <strong>{{ detail.college }}</strong> <strong>{{ detail.major }}</strong> 专业，
                  经审核符合《中华人民共和国学位条例》规定，
                  授予 <strong>{{ applicationDataParsed?.degreeType || '学士' }}</strong> 学位。
                </p>
                <div class="certificate-numbers" v-if="applicationDataParsed?.degreeNo">
                  <p class="cert-number">学位证书编号：{{ applicationDataParsed.degreeNo }}</p>
                </div>
                <p class="main-text">特此证明。</p>
              </div>
              
              <div class="certificate-footer">
                <div class="seal-area">
                  <p class="seal-text">[学位办公章]</p>
                </div>
                <p class="issue-date">{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
              </div>
            </div>
          </div>

          <!-- 5. 毕业证明模板 -->
          <div v-else-if="detail.certificateType === '毕业证明'" class="certificate-content">
            <div class="school-header">
              <h1 class="school-name">{{ detail.schoolName || '学校名称' }}</h1>
            </div>
            <h1 class="certificate-title">毕业证明</h1>
            <div class="certificate-body">
              <p class="certificate-intro">兹证明：</p>
              <div class="student-info">
                <p><span class="info-label">姓名：</span><span class="info-value">{{ detail.studentName }}</span></p>
                <p><span class="info-label">性别：</span><span class="info-value">{{ detail.gender === 1 ? '男' : '女' }}</span></p>
                <p><span class="info-label">学号：</span><span class="info-value">{{ detail.studentNo }}</span></p>
                <p><span class="info-label">身份证号：</span><span class="info-value">{{ detail.idCard }}</span></p>
              </div>
              
              <div class="certificate-text">
                <p class="main-text">
                  于 <strong>{{ formatDate(detail.enrollmentDate) }}</strong> 至 
                  <strong>{{ formatDate(applicationDataParsed?.graduationDate) }}</strong> 
                  在我校 <strong>{{ detail.college }}</strong> <strong>{{ detail.major }}</strong> 专业学习，
                  学制 <strong>{{ applicationDataParsed?.duration || '4' }}</strong> 年，
                  修完教学计划规定的全部课程，成绩合格，准予毕业。
                </p>
                <div class="certificate-numbers" v-if="applicationDataParsed?.certificateNo">
                  <p class="cert-number">毕业证书编号：{{ applicationDataParsed.certificateNo }}</p>
                </div>
                <p class="main-text">特此证明。</p>
              </div>
              
              <div class="certificate-footer">
                <div class="seal-area">
                  <p class="seal-text">[学校公章]</p>
                </div>
                <p class="issue-date">{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
              </div>
            </div>
          </div>

          <!-- 其他证明类型的通用模板 -->
          <div v-else class="certificate-content">
            <div class="school-header">
              <h1 class="school-name">{{ detail.schoolName || '学校名称' }}</h1>
            </div>
            <h1 class="certificate-title">{{ detail.certificateType }}</h1>
            <div class="certificate-body">
              <p class="certificate-intro">兹证明：</p>
              <div class="student-info">
                <p><span class="info-label">姓名：</span><span class="info-value">{{ detail.studentName }}</span></p>
                <p><span class="info-label">学号：</span><span class="info-value">{{ detail.studentNo }}</span></p>
                <p><span class="info-label">学院：</span><span class="info-value">{{ detail.college }}</span></p>
                <p><span class="info-label">专业：</span><span class="info-value">{{ detail.major }}</span></p>
              </div>
              
              <div class="certificate-text">
                <p class="main-text">{{ detail.applicationReason || '相关证明内容' }}</p>
                <p class="main-text">特此证明。</p>
              </div>
              
              <div class="certificate-footer">
                <div class="seal-area">
                  <p class="seal-text">[学校公章]</p>
                </div>
                <p class="issue-date">{{ formatDate(detail.issueDate) || getCurrentDate() }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Printer } from '@element-plus/icons-vue'
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

const getCurrentDate = () => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const pkCa = route.params.pkCa as string
    console.log('请求证明详情, pkCa:', pkCa)
    
    const res: any = await request({
      url: `/application/${pkCa}/detail`,
      method: 'get',
    })
    
    console.log('证明详情响应:', res)
    
    if (res.code === 200) {
      detail.value = res.data
      console.log('证明详情数据:', detail.value)
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

const handlePrint = () => {
  window.print()
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.certificate-preview-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

/* 操作按钮区域 */
.action-bar {
  max-width: 1000px;
  margin: 0 auto 20px;
  display: flex;
  gap: 12px;
  justify-content: flex-start;
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 证明容器 */
.certificate-wrapper {
  max-width: 1000px;
  margin: 0 auto;
}

.certificate-container {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 证明内容样式 */
.certificate-content {
  background: white;
  padding: 60px 80px;
  position: relative;
}

/* 学校头部 */
.school-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #d32f2f;
}

.school-name {
  font-size: 28px;
  font-weight: bold;
  color: #d32f2f;
  letter-spacing: 4px;
  margin: 0;
}

/* 证明标题 */
.certificate-title {
  text-align: center;
  font-size: 40px;
  font-weight: bold;
  color: #2c3e50;
  margin: 40px 0;
  letter-spacing: 12px;
}

.certificate-body {
  line-height: 2;
  font-size: 16px;
  color: #303133;
}

.certificate-intro {
  font-size: 18px;
  margin-bottom: 25px;
  font-weight: 500;
}

/* 学生信息区域 */
.student-info {
  margin: 30px 0;
  padding: 25px 30px;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-left: 4px solid #409EFF;
  border-radius: 4px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.student-info p {
  margin: 0;
  font-size: 16px;
  line-height: 1.8;
}

.info-label {
  display: inline-block;
  min-width: 100px;
  font-weight: 600;
  color: #606266;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

/* 证明文字区域 */
.certificate-text {
  margin: 35px 0;
}

.main-text {
  font-size: 17px;
  line-height: 2.5;
  text-indent: 2em;
  margin: 25px 0;
  text-align: justify;
  color: #303133;
}

.main-text strong {
  color: #d32f2f;
  font-weight: 600;
  padding: 0 3px;
}

/* 成绩详情 */
.score-details {
  margin: 25px 0;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 4px;
}

.score-text {
  font-size: 16px;
  line-height: 2;
  white-space: pre-line;
  color: #303133;
}

/* 证书编号 */
.certificate-numbers {
  margin: 25px 0;
  padding: 20px;
  background: #fff9e6;
  border-left: 4px solid #e6a23c;
  border-radius: 4px;
}

.cert-number {
  font-size: 16px;
  line-height: 2;
  margin: 8px 0;
  color: #606266;
  font-weight: 500;
}

/* 证明底部 */
.certificate-footer {
  margin-top: 80px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.seal-area {
  margin-bottom: 40px;
}

.seal-text {
  font-size: 16px;
  color: #d32f2f;
  font-weight: bold;
  padding: 10px 20px;
  border: 2px solid #d32f2f;
  border-radius: 50%;
  display: inline-block;
  transform: rotate(-15deg);
}

.issue-date {
  font-size: 16px;
  color: #606266;
  font-weight: 500;
}

/* 打印样式 - 只打印证明内容 */
@media print {
  /* 隐藏页面背景和操作按钮 */
  body {
    margin: 0;
    padding: 0;
  }
  
  .certificate-preview-page {
    background: white;
    padding: 0;
    margin: 0;
  }
  
  .action-bar,
  .no-print {
    display: none !important;
  }
  
  .certificate-wrapper {
    max-width: none;
    margin: 0;
  }
  
  .certificate-container {
    padding: 0;
    margin: 0;
    box-shadow: none;
    border-radius: 0;
    background: white;
  }
  
  .certificate-content {
    padding: 50px 60px;
    min-height: auto;
    page-break-after: avoid;
    page-break-inside: avoid;
  }
  
  /* 学校头部打印样式 */
  .school-header {
    border-bottom: 2px solid #000;
  }
  
  .school-name {
    color: #000;
  }
  
  /* 保持颜色 */
  .main-text strong {
    color: #000;
    font-weight: bold;
  }
  
  .seal-text {
    color: #d32f2f;
    border-color: #d32f2f;
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
  
  /* 确保内容在一页内 */
  @page {
    margin: 20mm;
    size: A4;
  }
}
</style>
