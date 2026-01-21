<template>
  <div class="teacher-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教师信息管理</span>
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
        <el-select v-model="queryParams.approvalLevel" placeholder="审批级别" clearable style="width: 150px" class="filter-item" @change="handleQuery">
          <el-option label="1级 (导师/辅导员)" :value="1" />
          <el-option label="2级 (学院)" :value="2" />
          <el-option label="3级 (教务处/校级)" :value="3" />
        </el-select>
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="teacherNo" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="college" label="学院" min-width="150" />
        <el-table-column prop="department" label="部门/系" min-width="150" />
        <el-table-column prop="title" label="职称" width="120" />
        <el-table-column prop="approvalLevel" label="审批级别" width="120">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.approvalLevel)">
              {{ getLevelText(row.approvalLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="canApprove" label="审批权限" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.canApprove === 1" disabled />
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
    <el-dialog v-model="detailVisible" title="教师详情" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工号">{{ currentTeacher.teacherNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentTeacher.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentTeacher.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentTeacher.idCard }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ currentTeacher.college }}</el-descriptions-item>
        <el-descriptions-item label="部门/系">{{ currentTeacher.department }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentTeacher.title }}</el-descriptions-item>
        <el-descriptions-item label="职务">{{ currentTeacher.position }}</el-descriptions-item>
        <el-descriptions-item label="审批级别">{{ getLevelText(currentTeacher.approvalLevel) }}</el-descriptions-item>
        <el-descriptions-item label="审批权限">{{ currentTeacher.canApprove === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTeacher.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAdminTeachers } from '@/api/admin'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  current: 1,
  size: 10,
  college: '',
  approvalLevel: null
})

const detailVisible = ref(false)
const currentTeacher = ref<any>({})

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getAdminTeachers(queryParams)
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
  currentTeacher.value = row
  detailVisible.value = true
}

const getLevelType = (level: number) => {
  const types: any = { 1: 'info', 2: 'warning', 3: 'danger' }
  return types[level] || 'info'
}

const getLevelText = (level: number) => {
  const texts: any = { 1: '1级 (导师)', 2: '2级 (学院)', 3: '3级 (教务处)' }
  return texts[level] || '未设置'
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
