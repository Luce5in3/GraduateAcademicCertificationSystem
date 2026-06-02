<template>
  <div class="announcement-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📢 公告管理</span>
          <el-button type="primary" @click="openDialog()">新增公告</el-button>
        </div>
      </template>
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="targetRole" label="目标角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.targetRole === 'ALL' ? 'success' : 'warning'" size="small">{{ roleLabel(row.targetRole) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="row.priority === 2 ? 'danger' : row.priority === 1 ? 'warning' : 'info'" size="small">{{ priLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.publishStatus === 1 ? 'success' : row.publishStatus === 2 ? 'danger' : 'info'" size="small">{{ statusLabel(row.publishStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160">
          <template #default="{ row }">{{ row.publishTime ? new Date(row.publishTime).toLocaleString('zh-CN') : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.publishStatus === 0" type="success" size="small" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.publishStatus === 1" type="warning" size="small" @click="handleRevoke(row)">撤回</el-button>
            <el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination v-model:current-page="current" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadList" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" placeholder="公告标题" /></el-form-item>
        <el-form-item label="目标角色">
          <el-select v-model="form.targetRole"><el-option label="全部" value="ALL" /><el-option label="学生" value="STUDENT" /><el-option label="教师" value="TEACHER" /></el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority"><el-option label="普通" :value="0" /><el-option label="重要" :value="1" /><el-option label="紧急" :value="2" /></el-select>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="form.isPinned" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" placeholder="支持HTML" /></el-form-item>
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
import { getAdminAnnouncements, createAnnouncement, updateAnnouncement, deleteAnnouncement, publishAnnouncement, revokeAnnouncement } from '@/api/notification'

const list = ref<any[]>([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<any>({ title: '', content: '', targetRole: 'ALL', priority: 0, isPinned: 0 })

const loadList = async () => {
  loading.value = true
  try {
    const res: any = await getAdminAnnouncements({ current: current.value, size: size.value })
    if (res.code === 200 && res.data) { list.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { /* */ } finally { loading.value = false }
}

const openDialog = (row?: any) => {
  isEdit.value = !!row
  form.value = row ? { ...row } : { title: '', content: '', targetRole: 'ALL', priority: 0, isPinned: 0 }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    const res: any = isEdit.value ? await updateAnnouncement(form.value) : await createAnnouncement(form.value)
    if (res.code === 200) { ElMessage.success(isEdit.value ? '更新成功' : '创建成功'); dialogVisible.value = false; loadList() }
  } catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row: any) => {
  try { await ElMessageBox.confirm('确认删除?', '提示', { type: 'warning' }); await deleteAnnouncement(row.pkAnnouncement); ElMessage.success('删除成功'); loadList() } catch { /* */ }
}

const handlePublish = async (row: any) => {
  try { await publishAnnouncement(row.pkAnnouncement); ElMessage.success('发布成功'); loadList() } catch { ElMessage.error('发布失败') }
}

const handleRevoke = async (row: any) => {
  try { await revokeAnnouncement(row.pkAnnouncement); ElMessage.success('已撤回'); loadList() } catch { ElMessage.error('操作失败') }
}

const roleLabel = (r: string) => r === 'ALL' ? '全部' : r === 'STUDENT' ? '学生' : '教师'
const priLabel = (p: number) => p === 2 ? '紧急' : p === 1 ? '重要' : '普通'
const statusLabel = (s: number) => s === 1 ? '已发布' : s === 2 ? '已撤回' : '草稿'

onMounted(loadList)
</script>

<style scoped>
.announcement-manage { max-width: 1100px; margin: 0 auto; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 16px; }
</style>
