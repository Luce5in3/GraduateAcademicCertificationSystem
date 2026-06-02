<template>
  <div class="college-manage">
    <el-card>
      <template #header><div class="card-header"><span>🏫 学院管理</span><el-button type="primary" @click="openDialog()">新增学院</el-button></div></template>
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="collegeCode" label="编码" width="100" />
        <el-table-column prop="collegeName" label="学院名称" min-width="180" />
        <el-table-column prop="deanName" label="院长" width="100" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学院' : '新增学院'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="编码"><el-input v-model="form.collegeCode" placeholder="如：CS" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.collegeName" placeholder="学院名称" /></el-form-item>
        <el-form-item label="院长"><el-input v-model="form.deanName" placeholder="院长姓名" /></el-form-item>
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
import { getColleges, createCollege, updateCollege, deleteCollege } from '@/api/college'

const list = ref<any[]>([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<any>({ collegeCode: '', collegeName: '', deanName: '', phone: '', officeAddr: '', sortOrder: 0, isActive: 1 })

const loadList = async () => {
  loading.value = true
  try {
    const res: any = await getColleges({ current: current.value, size: size.value })
    if (res.code === 200 && res.data) { list.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { /* */ } finally { loading.value = false }
}

const openDialog = (row?: any) => {
  isEdit.value = !!row
  form.value = row ? { ...row } : { collegeCode: '', collegeName: '', deanName: '', phone: '', officeAddr: '', sortOrder: 0, isActive: 1 }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    const res: any = isEdit.value ? await updateCollege(form.value) : await createCollege(form.value)
    if (res.code === 200) { ElMessage.success(isEdit.value ? '更新成功' : '创建成功'); dialogVisible.value = false; loadList() }
  } catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row: any) => {
  try { await ElMessageBox.confirm('确认删除该学院?', '提示', { type: 'warning' }); await deleteCollege(row.pkCollege); ElMessage.success('删除成功'); loadList() } catch { /* */ }
}

onMounted(loadList)
</script>

<style scoped>
.college-manage { max-width: 1100px; margin: 0 auto; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 16px; }
</style>
