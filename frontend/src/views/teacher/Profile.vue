<template>
  <div class="profile-page">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2>👨‍🏫 个人信息</h2>
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
        <!-- 基本信息（只读） -->
        <el-divider content-position="left">基本信息（系统维护）</el-divider>
        <el-descriptions :column="2" border class="info-section">
          <el-descriptions-item label="用户名" label-class-name="desc-label">
            {{ userStore.userInfo?.username || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="工号" label-class-name="desc-label">
            {{ teacherInfo.teacherNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="姓名" label-class-name="desc-label">
            {{ teacherInfo.name || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="审批权限" label-class-name="desc-label">
            <el-tag :type="teacherInfo.canApprove === 1 ? 'success' : 'info'" size="small">
              {{ teacherInfo.canApprove === 1 ? '有审批权' : '无审批权' }}
            </el-tag>
            <el-tag v-if="teacherInfo.approvalLevel" type="primary" size="small" style="margin-left: 8px">
              {{ getApprovalLevelText(teacherInfo.approvalLevel) }}
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
              <el-form-item label="所属学院" prop="college">
                <el-input 
                  v-model="formData.college" 
                  placeholder="请输入所属学院"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属部门" prop="department">
                <el-input 
                  v-model="formData.department" 
                  placeholder="请输入所属部门"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="职称" prop="title">
                <el-select v-model="formData.title" placeholder="请选择职称" style="width: 100%">
                  <el-option label="教授" value="教授" />
                  <el-option label="副教授" value="副教授" />
                  <el-option label="讲师" value="讲师" />
                  <el-option label="助教" value="助教" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位" prop="position">
                <el-select v-model="formData.position" placeholder="请选择职位" style="width: 100%">
                  <el-option label="院长" value="院长" />
                  <el-option label="副院长" value="副院长" />
                  <el-option label="系主任" value="系主任" />
                  <el-option label="副系主任" value="副系主任" />
                  <el-option label="辅导员" value="辅导员" />
                  <el-option label="普通教师" value="普通教师" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <!-- 时间信息 -->
        <el-divider content-position="left">系统信息</el-divider>
        <el-descriptions :column="2" border class="info-section">
          <el-descriptions-item label="创建时间" label-class-name="desc-label">
            {{ teacherInfo.createTime || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" label-class-name="desc-label">
            {{ teacherInfo.updateTime || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getCurrentTeacher, updateTeacher } from '@/api/teacher'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const isEditing = ref(false)
const loading = ref(false)
const pageLoading = ref(false)

const teacherInfo = ref<any>({})
const formData = reactive({
  idCard: '',
  gender: null as number | null,
  college: '',
  department: '',
  title: '',
  position: '',
})

const rules: FormRules = {
  idCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
}

const getApprovalLevelText = (level: number) => {
  const texts: any = { 1: '初审', 2: '复审', 3: '终审' }
  return texts[level] || ''
}

const fetchTeacherInfo = async () => {
  pageLoading.value = true
  try {
    const res: any = await getCurrentTeacher()
    if (res.code === 200 && res.data) {
      teacherInfo.value = res.data
      // 填充表单数据
      Object.assign(formData, {
        idCard: res.data.idCard || '',
        gender: res.data.gender,
        college: res.data.college || '',
        department: res.data.department || '',
        title: res.data.title || '',
        position: res.data.position || '',
      })
    } else {
      ElMessage.warning('暂无教师信息，请先完善基本信息')
    }
  } catch (error: any) {
    console.error('获取教师信息失败:', error)
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
    idCard: teacherInfo.value.idCard || '',
    gender: teacherInfo.value.gender,
    college: teacherInfo.value.college || '',
    department: teacherInfo.value.department || '',
    title: teacherInfo.value.title || '',
    position: teacherInfo.value.position || '',
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
        const res: any = await updateTeacher(formData)
        if (res.code === 200) {
          ElMessage.success('保存成功')
          isEditing.value = false
          await fetchTeacherInfo()
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
  fetchTeacherInfo()
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
</style>
