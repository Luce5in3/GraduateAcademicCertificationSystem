<template>
  <div class="student-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生信息管理</span>
        </div>
      </template>

      <div class="filter-container">
        <el-input
          v-model="queryParams.college"
          placeholder="学院"
          style="width: 200px"
          class="filter-item"
          clearable
          @clear="handleQuery"
        />
        <el-input
          v-model="queryParams.major"
          placeholder="专业"
          style="width: 200px"
          class="filter-item"
          clearable
          @clear="handleQuery"
        />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="college" label="学院" min-width="150" />
        <el-table-column prop="major" label="专业" min-width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="graduationStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.graduationStatus)">
              {{ getStatusText(row.graduationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="学生详情" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ currentStudent.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentStudent.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentStudent.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentStudent.idCard }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ currentStudent.college }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ currentStudent.major }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ currentStudent.className }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ currentStudent.grade }}</el-descriptions-item>
        <el-descriptions-item label="入学时间">{{ currentStudent.enrollmentDate }}</el-descriptions-item>
        <el-descriptions-item label="毕业时间">{{ currentStudent.graduationDate }}</el-descriptions-item>
        <el-descriptions-item label="学历层次">{{ currentStudent.educationLevel }}</el-descriptions-item>
        <el-descriptions-item label="学习形式">{{ currentStudent.studyType }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentStudent.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAdminStudents } from '@/api/admin'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  current: 1,
  size: 10,
  college: '',
  major: ''
})

const detailVisible = ref(false)
const currentStudent = ref<any>({})

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getAdminStudents(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.current = 1
  fetchData()
}

const handleDetail = (row: any) => {
  currentStudent.value = row
  detailVisible.value = true
}

const getStatusType = (status: number) => {
  const types: any = { 0: 'success', 1: 'info', 2: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: any = { 0: '在读', 1: '已毕业', 2: '结业' }
  return texts[status] || '未知'
}

onMounted(fetchData)
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
