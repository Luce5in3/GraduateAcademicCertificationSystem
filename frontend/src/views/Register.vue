<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>毕业学术证书管理系统</h2>
          <p>用户注册</p>
        </div>
      </template>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
      >
        <el-form-item label="用户类型" prop="userType">
          <el-radio-group v-model="registerForm.userType">
            <el-radio :label="1">学生</el-radio>
            <el-radio :label="2">教师</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="registerForm.userType === 1"
          label="学号"
          prop="studentId"
        >
          <el-input
            v-model="registerForm.studentId"
            placeholder="请输入学号"
            clearable
          />
        </el-form-item>

        <el-form-item
          v-if="registerForm.userType === 2"
          label="教师工号"
          prop="teacherId"
        >
          <el-input
            v-model="registerForm.teacherId"
            placeholder="请输入教师工号"
            clearable
          />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="registerForm.name"
            placeholder="请输入真实姓名"
            clearable
          />
        </el-form-item>

        <el-form-item label="电子邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入电子邮箱"
            clearable
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            clearable
          />
        </el-form-item>

        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码(至少6位)"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            style="width: 100%"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="footer-links">
            <router-link to="/login">已有账号？去登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  userType: 1,
  studentId: '',
  teacherId: '',
  name: '',
  email: '',
  phone: '',
})

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' },
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
  ],
  teacherId: [
    { required: true, message: '请输入教师工号', trigger: 'blur' },
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data: any = {
          username: registerForm.username,
          password: registerForm.password,
          confirmPassword: registerForm.confirmPassword,
          userType: registerForm.userType,
          realName: registerForm.name,  // 后端期望的字段名是 realName
          email: registerForm.email,
          phone: registerForm.phone,
        }
        
        if (registerForm.userType === 1) {
          data.studentId = registerForm.studentId
        } else {
          data.teacherId = registerForm.teacherId
        }
        
        const res: any = await register(data)
        if (res.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1677FF 0%, #0050B3 100%);
  padding: 20px 0;
}

.register-card {
  width: 500px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #1677FF;
  font-size: 28px;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.footer-links {
  width: 100%;
  text-align: center;
}

.footer-links a {
  color: #1677FF;
  text-decoration: none;
}

.footer-links a:hover {
  text-decoration: underline;
}
</style>
