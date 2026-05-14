<template>
  <div class="profile-page">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2>👤 个人信息</h2>
            <p>查看和编辑您的个人信息</p>
          </div>
          <el-button 
            v-if="!isEditing" 
            type="primary" 
            :icon="Edit"
            @click="handleEdit"
          >
            编辑信息
          </el-button>
          <el-space v-else>
            <el-button @click="handleCancel">
              取消
            </el-button>
            <el-button 
              type="primary" 
              :loading="loading"
              @click="handleSave"
            >
              保存修改
            </el-button>
          </el-space>
        </div>
      </template>

      <div v-loading="pageLoading">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="studentInfo.imageUrl" class="user-avatar">
              <span style="font-size: 36px">{{ studentInfo.name ? studentInfo.name.charAt(0) : '👤' }}</span>
            </el-avatar>
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <el-button size="small" type="primary" :loading="avatarLoading" style="margin-top: 12px">
                {{ avatarLoading ? '上传中...' : '更换头像' }}
              </el-button>
            </el-upload>
          </div>
          <div class="avatar-tips">
            <p>支持 JPG、PNG、GIF 格式，最大 5MB</p>
          </div>
        </div>

        <!-- 基本信息（只读） -->
        <el-divider content-position="left">基本信息（系统维护）</el-divider>
        <el-descriptions :column="2" border class="info-section">
          <el-descriptions-item label="用户名" label-class-name="desc-label">
            {{ userStore.userInfo?.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="学号" label-class-name="desc-label">
            {{ studentInfo.studentNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="姓名" label-class-name="desc-label">
            {{ studentInfo.name || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="毕业状态" label-class-name="desc-label">
            <el-tag :type="getGraduationStatusType(studentInfo.graduationStatus)" size="small">
              {{ getGraduationStatusText(studentInfo.graduationStatus) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 可编辑信息 -->
        <el-divider content-position="left">个人详细信息</el-divider>
        <el-form 
          ref="formRef"
          :model="formData" 
          :rules="rules"
          label-width="120px" 
          class="profile-form"
          :disabled="!isEditing"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input 
                  v-model="formData.idCard" 
                  placeholder="请输入身份证号"
                  maxlength="18"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="formData.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="0">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学院" prop="college">
                <el-input 
                  v-model="formData.college" 
                  placeholder="请输入学院"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业" prop="major">
                <el-input 
                  v-model="formData.major" 
                  placeholder="请输入专业"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="班级" prop="className">
                <el-input 
                  v-model="formData.className" 
                  placeholder="请输入班级"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年级" prop="grade">
                <el-input 
                  v-model="formData.grade" 
                  placeholder="如：2021级"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="入学日期" prop="enrollmentDate">
                <el-date-picker
                  v-model="formData.enrollmentDate"
                  type="date"
                  placeholder="选择入学日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="毕业日期" prop="graduationDate">
                <el-date-picker
                  v-model="formData.graduationDate"
                  type="date"
                  placeholder="选择预计毕业日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学历层次" prop="educationLevel">
                <el-select v-model="formData.educationLevel" placeholder="请选择学历层次" style="width: 100%">
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学习形式" prop="studyType">
                <el-select v-model="formData.studyType" placeholder="请选择学习形式" style="width: 100%">
                  <el-option label="全日制" value="全日制" />
                  <el-option label="非全日制" value="非全日制" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <!-- 时间信息 -->
        <el-divider content-position="left">系统信息</el-divider>
        <el-descriptions :column="2" border class="info-section">
          <el-descriptions-item label="创建时间" label-class-name="desc-label">
            {{ studentInfo.createTime || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" label-class-name="desc-label">
            {{ studentInfo.updateTime || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="profile-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2>🔒 修改密码</h2>
            <p>定期修改密码可以提高账户安全性</p>
          </div>
        </div>
      </template>
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="120px"
        style="max-width: 500px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码（6-20位）"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
            确认修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getCurrentStudent, updateStudent, uploadAvatar } from '@/api/student'
import { changePassword } from '@/api/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const isEditing = ref(false)
const loading = ref(false)
const pageLoading = ref(false)
const avatarLoading = ref(false)
const passwordLoading = ref(false)

const studentInfo = ref<any>({})
const formData = reactive({
  idCard: '',
  gender: null as number | null,
  college: '',
  major: '',
  className: '',
  grade: '',
  enrollmentDate: '',
  graduationDate: '',
  educationLevel: '',
  studyType: '',
})

const rules: FormRules = {
  idCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
}

// 修改密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次密码输入不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        const res: any = await changePassword(passwordForm)
        if (res.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
          passwordFormRef.value?.resetFields()
        }
      } catch (error: any) {
        ElMessage.error(error.message || '密码修改失败')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

const beforeAvatarUpload = (file: any) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG/GIF 格式的图片')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleAvatarUpload = async (options: any) => {
  avatarLoading.value = true
  try {
    const res: any = await uploadAvatar(options.file)
    if (res.code === 200 && res.data) {
      studentInfo.value.imageUrl = res.data.imageUrl
      userStore.setAvatarUrl(res.data.imageUrl)
      ElMessage.success('头像更新成功')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  } finally {
    avatarLoading.value = false
  }
}

const getGraduationStatusType = (status: number) => {
  const types: any = { 0: 'success', 1: 'info', 2: 'warning' }
  return types[status] || 'info'
}

const getGraduationStatusText = (status: number) => {
  const texts: any = { 0: '在读', 1: '已毕业', 2: '结业' }
  return texts[status] || '未知'
}

const fetchStudentInfo = async () => {
  pageLoading.value = true
  try {
    const res: any = await getCurrentStudent()
    if (res.code === 200 && res.data) {
      studentInfo.value = res.data
      // 填充表单数据
      Object.assign(formData, {
        idCard: res.data.idCard || '',
        gender: res.data.gender,
        college: res.data.college || '',
        major: res.data.major || '',
        className: res.data.className || '',
        grade: res.data.grade || '',
        enrollmentDate: res.data.enrollmentDate || '',
        graduationDate: res.data.graduationDate || '',
        educationLevel: res.data.educationLevel || '',
        studyType: res.data.studyType || '',
      })
    } else {
      ElMessage.warning('暂无学生信息，请先完善基本信息')
    }
  } catch (error: any) {
    console.error('获取学生信息失败:', error)
    ElMessage.error(error.message || '获取信息失败，请稍后再试')
  } finally {
    pageLoading.value = false
  }
}

const handleEdit = () => {
  isEditing.value = true
}

const handleCancel = () => {
  // 恢复原始数据
  Object.assign(formData, {
    idCard: studentInfo.value.idCard || '',
    gender: studentInfo.value.gender,
    college: studentInfo.value.college || '',
    major: studentInfo.value.major || '',
    className: studentInfo.value.className || '',
    grade: studentInfo.value.grade || '',
    enrollmentDate: studentInfo.value.enrollmentDate || '',
    graduationDate: studentInfo.value.graduationDate || '',
    educationLevel: studentInfo.value.educationLevel || '',
    studyType: studentInfo.value.studyType || '',
  })
  isEditing.value = false
  formRef.value?.clearValidate()
}

const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await ElMessageBox.confirm(
          '确认保存修改的信息吗？',
          '确认保存',
          {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'info',
          }
        )
        
        loading.value = true
        const res: any = await updateStudent(formData)
        if (res.code === 200) {
          ElMessage.success('保存成功')
          isEditing.value = false
          await fetchStudentInfo()
        }
      } catch (error) {
        // 用户取消操作
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchStudentInfo()
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
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

.info-section {
  margin-bottom: 24px;
}

.profile-form {
  padding: 20px 0;
}

:deep(.el-divider__text) {
  font-weight: 500;
  color: #606266;
  font-size: 15px;
}

:deep(.desc-label) {
  font-weight: 500;
}

:deep(.el-form-item.is-disabled .el-input__wrapper) {
  background-color: #f5f7fa;
  cursor: not-allowed;
}

:deep(.el-form-item.is-disabled .el-select .el-input__wrapper) {
  background-color: #f5f7fa;
  cursor: not-allowed;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px 0;
}

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-avatar {
  border: 3px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-tips p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}
</style>
