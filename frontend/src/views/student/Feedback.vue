<template>
  <div class="feedback-page">
    <el-card>
      <template #header><div class="card-header"><span>💬 我的反馈</span><el-button type="primary" @click="showCreate">发起反馈</el-button></div></template>
      <div v-loading="loading">
        <el-empty v-if="list.length === 0" description="暂无反馈记录" />
        <div v-else class="feedback-list">
          <div v-for="item in list" :key="item.pkFeedback" class="feedback-item" @click="showDetail(item)">
            <div class="fb-header">
              <span class="fb-type">
                <el-tag :type="getTypeTag(item.feedbackType)" size="small">{{ getTypeLabel(item.feedbackType) }}</el-tag>
              </span>
              <el-tag :type="getStatusTag(item.status)" size="small">{{ getStatusLabel(item.status) }}</el-tag>
              <span class="fb-time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="fb-description">{{ item.description }}</div>
            <div class="fb-reply" v-if="item.replyContent">
              <strong>管理员回复：</strong>{{ item.replyContent }}
            </div>
          </div>
        </div>
        <div class="pagination-wrap" v-if="total > 0">
          <el-pagination v-model:current-page="current" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadList" />
        </div>
      </div>
    </el-card>

    <!-- 发起反馈弹窗 -->
    <el-dialog v-model="createVisible" title="发起反馈" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="反馈类型">
          <el-select v-model="form.feedbackType"><el-option label="咨询" :value="1" /><el-option label="建议" :value="2" /><el-option label="申诉" :value="3" /><el-option label="其他" :value="4" /></el-select>
        </el-form-item>
        <el-form-item label="关联申请">
          <el-select v-model="form.pkCa" placeholder="可选" clearable filterable>
            <el-option v-for="a in applications" :key="a.pkCa" :label="`[${a.certificateType}] ${new Date(a.createTime).toLocaleString('zh-CN')}`" :value="a.pkCa" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="5" placeholder="描述您的问题或建议" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="反馈详情" width="550px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="类型">{{ getTypeLabel(detail.feedbackType) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusLabel(detail.status) }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ detail.description }}</el-descriptions-item>
          <el-descriptions-item label="提交时间" :span="2">{{ formatTime(detail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="管理员回复" :span="2" v-if="detail.replyContent">{{ detail.replyContent }}</el-descriptions-item>
          <el-descriptions-item label="回复时间" :span="2" v-if="detail.replyTime">{{ formatTime(detail.replyTime) }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyFeedbacks, createFeedback } from '@/api/feedback'
import { getMyApplications } from '@/api/application'

const list = ref<any[]>([])
const applications = ref<any[]>([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const createVisible = ref(false)
const detailVisible = ref(false)
const detail = ref<any>(null)
const form = ref({ feedbackType: 1, pkCa: '', description: '' })

const loadList = async () => {
  loading.value = true
  try {
    const res: any = await getMyFeedbacks({ current: current.value, size: size.value })
    if (res.code === 200 && res.data) { list.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { /* */ } finally { loading.value = false }
}

const loadApplications = async () => {
  try { const res: any = await getMyApplications(); if (res.code === 200 && res.data) applications.value = res.data.records || [] } catch { /* */ }
}

const showCreate = () => { form.value = { feedbackType: 1, pkCa: '', description: '' }; createVisible.value = true }
const showDetail = (item: any) => { detail.value = item; detailVisible.value = true }

const handleSubmit = async () => {
  try {
    const res: any = await createFeedback(form.value)
    if (res.code === 200) { ElMessage.success('反馈提交成功'); createVisible.value = false; loadList() }
  } catch { ElMessage.error('提交失败') }
}

const getTypeTag = (t: number) => t === 1 ? '' : t === 2 ? 'success' : t === 3 ? 'danger' : 'info'
const getTypeLabel = (t: number) => t === 1 ? '咨询' : t === 2 ? '建议' : t === 3 ? '申诉' : '其他'
const getStatusTag = (s: number) => s === 0 ? 'warning' : s === 1 ? 'success' : 'info'
const getStatusLabel = (s: number) => s === 0 ? '处理中' : s === 1 ? '已回复' : '已关闭'
const formatTime = (time: string) => time ? new Date(time).toLocaleString('zh-CN') : ''

onMounted(() => { loadList(); loadApplications() })
</script>

<style scoped>
.feedback-page { max-width: 900px; margin: 0 auto; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.feedback-item { padding: 16px 12px; border-bottom: 1px solid #ebeef5; cursor: pointer; transition: background .2s; }
.feedback-item:hover { background: #f5f7fa; }
.fb-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.fb-type { flex-shrink: 0; }
.fb-time { font-size: 12px; color: #909399; margin-left: auto; }
.fb-description { font-size: 14px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-bottom: 6px; }
.fb-reply { font-size: 13px; color: #409eff; background: #ecf5ff; padding: 8px 12px; border-radius: 4px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 16px; }
</style>
