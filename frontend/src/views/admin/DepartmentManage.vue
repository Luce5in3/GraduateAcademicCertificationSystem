<template>
  <div class="department-manage">
    <el-card>
      <template #header><div class="card-header"><span>🏢 部门管理</span><el-button type="primary" @click="openDialog()">新增部门</el-button></div></template>
      <div class="filter-bar">
        <el-select v-model="filterCollege" placeholder="筛选学院" clearable @change="loadList" style="width:200px">
          <el-option v-for="c in colleges" :key="c.pkCollege" :label="c.collegeName" :value="c.pkCollege" />
        </el-select>
      </div>
      <el-table :data="list" v-loading="loading" border style="margin-top:12px">
        <el-table-column prop="departmentCode" label="编码" width="100" />
        <el-table-column prop="departmentName" label="部门名称" min-width="180" />
        <el-table-column prop="leaderName" label="负责人" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.isActive === 1 ? 'success' : 'danger'" size="small">{{ row.isActive === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination v-model:current-page="current" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadList" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '新增部门'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="所属学院">
          <el-select v-model="form.pkCollege" style="width:100%">
            <el-option v-for="c in colleges" :key="c.pkCollege" :label="c.collegeName" :value="c.pkCollege" />
          </el-select>
        </el-form-item>
        <el-form-item label="编码"><el-input v-model="form.departmentCode" placeholder="如：CS01" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.departmentName" placeholder="部门名称" /></el-form-item>
        <el-form-item label="负责人"><el-input v-model="form.leaderName" placeholder="负责人" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" placeholder="联系电话" /></el-form-item>
        <el-form-item label="办公地址"><el-input v-model="form.officeAddr" placeholder="办公地址" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.isActive" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDepartments, createDepartment, updateDepartment, deleteDepartment } from '@/api/college'
import { getActiveColleges } from '@/api/college'

const list = ref<any[]>([])
const colleges = ref<any[]>([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const filterCollege = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<any>({ pkCollege: '', departmentCode: '', departmentName: '', leaderName: '', phone: '', officeAddr: '', sortOrder: 0, isActive: 1 })

const loadColleges = async () => {
  try { const res: any = await getActiveColleges(); if (res.code === 200) colleges.value = res.data || [] } catch { /* */ }
}

const loadList = async () => {
  loading.value = true
  try {
    const res: any = await getDepartments({ current: current.value, size: size.value, pkCollege: filterCollege.value || undefined })
    if (res.code === 200 && res.data) { list.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { /* */ } finally { loading.value = false }
}

const openDialog = (row?: any) => {
  isEdit.value = !!row
  form.value = row ? { ...row } : { pkCollege: '', departmentCode: '', departmentName: '', leaderName: '', phone: '', officeAddr: '', sortOrder: 0, isActive: 1 }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    const res: any = isEdit.value ? await updateDepartment(form.value) : await createDepartment(form.value)
    if (res.code === 200) { ElMessage.success(isEdit.value ? '更新成功' : '创建成功'); dialogVisible.value = false; loadList() }
  } catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row: any) => {
  try { await ElMessageBox.confirm('确认删除?', '提示', { type: 'warning' }); await deleteDepartment(row.pkDepartment); ElMessage.success('删除成功'); loadList() } catch { /* */ }
}

onMounted(() => { loadColleges(); loadList() })
</script>

<style scoped>
.department-manage { max-width: 1100px; margin: 0 auto; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { margin-bottom: 12px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 16px; }
</style>
