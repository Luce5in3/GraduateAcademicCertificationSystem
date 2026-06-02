<template>
  <div class="notification-center">
    <el-card body-style="padding: 0">
      <template #header>
        <div class="card-header">
          <span>🔔 消息中心</span>
          <el-button type="primary" link v-if="activeTab === 'notification' && unreadCount > 0" @click="handleMarkAll">全部已读</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="msg-tabs">
        <!-- 消息通知 -->
        <el-tab-pane name="notification">
          <template #label>
            <span class="tab-label">📬 消息通知</span>
          </template>
          <div v-loading="loading">
            <el-empty v-if="list.length === 0" description="暂无通知" :image-size="80" />
            <div v-else class="notification-list">
              <div
                v-for="item in list"
                :key="item.pkNotification"
                class="noti-card"
                :class="{ unread: item.isRead === 0 }"
                @click="handleRead(item)"
              >
                <div class="noti-icon" :style="{ background: getNotiBg(item.notificationType) }">
                  <el-icon :size="22"><component :is="getNotiIcon(item.notificationType)" /></el-icon>
                </div>
                <div class="noti-body">
                  <div class="noti-row">
                    <span class="noti-title">{{ item.title }}</span>
                    <span class="noti-time">{{ formatTime(item.createTime) }}</span>
                  </div>
                  <div class="noti-text">{{ item.content }}</div>
                  <div class="noti-badge" v-if="item.isRead === 0">
                    <span class="unread-tag">未读</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="pagination-wrap" v-if="total > 0">
              <el-pagination v-model:current-page="current" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadNotifications" />
            </div>
          </div>
        </el-tab-pane>

        <!-- 系统公告 -->
        <el-tab-pane name="announcement">
          <template #label>
            <span class="tab-label">📢 系统公告</span>
          </template>
          <div v-loading="annLoading">
            <el-empty v-if="annList.length === 0" description="暂无公告" :image-size="80" />
            <div v-else class="announcement-list">
              <div v-for="item in annList" :key="item.pkAnnouncement" class="ann-card" @click="showAnnDetail(item)">
                <div class="ann-card-left" :class="item.isPinned === 1 ? 'pinned' : ''">
                  <div class="ann-priority-dot" :class="item.priority === 2 ? 'urgent' : item.priority === 1 ? 'important' : 'normal'"></div>
                </div>
                <div class="ann-card-body">
                  <div class="ann-card-row">
                    <span class="ann-card-title">{{ item.title }}</span>
                    <span class="ann-card-time">{{ formatTime(item.publishTime) }}</span>
                  </div>
                  <div class="ann-card-tags">
                    <el-tag v-if="item.isPinned === 1" type="danger" size="small" effect="dark">置顶</el-tag>
                    <el-tag v-if="item.priority === 2" type="danger" size="small">紧急</el-tag>
                    <el-tag v-else-if="item.priority === 1" type="warning" size="small">重要</el-tag>
                    <el-tag v-else type="info" size="small">普通</el-tag>
                    <el-tag :type="item.targetRole === 'ALL' ? 'success' : ''" size="small">{{ roleLabel(item.targetRole) }}</el-tag>
                  </div>
                </div>
              </div>
            </div>
            <div class="pagination-wrap" v-if="annTotal > 0">
              <el-pagination v-model:current-page="annCurrent" :page-size="annSize" :total="annTotal" layout="prev, pager, next" @current-change="loadAnnouncements" />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="annDetail?.title" width="700px">
      <div class="detail-content" v-html="annDetail?.content" v-if="annDetail"></div>
      <div class="detail-meta" v-if="annDetail">
        <span>发布时间: {{ formatTime(annDetail.publishTime) }}</span>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Check, Warning, Promotion } from '@element-plus/icons-vue'
import { getNotifications, markNotificationRead, markAllNotificationsRead, getUnreadNotificationCount, getAnnouncements } from '@/api/notification'

const router = useRouter()
const activeTab = ref('notification')

// 通知
const list = ref<any[]>([])
const loading = ref(false)
const unreadCount = ref(0)
const current = ref(1)
const size = ref(10)
const total = ref(0)

// 公告
const annList = ref<any[]>([])
const annLoading = ref(false)
const annCurrent = ref(1)
const annSize = ref(10)
const annTotal = ref(0)
const detailVisible = ref(false)
const annDetail = ref<any>(null)

// 通知类型: 1=审批 2=系统 3=反馈
const getNotiIcon = (type: number) => type === 1 ? Check : type === 3 ? Promotion : Bell
const getNotiBg = (type: number) => type === 1 ? '#e8f5e9' : type === 3 ? '#fff3e0' : '#e3f2fd'

const loadNotifications = async () => {
  loading.value = true
  try {
    const res: any = await getNotifications({ current: current.value, size: size.value })
    if (res.code === 200 && res.data) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    }
    const countRes: any = await getUnreadNotificationCount()
    if (countRes.code === 200) unreadCount.value = countRes.data || 0
  } catch { /* */ } finally { loading.value = false }
}

const loadAnnouncements = async () => {
  annLoading.value = true
  try {
    const res: any = await getAnnouncements({ current: annCurrent.value, size: annSize.value })
    if (res.code === 200 && res.data) {
      annList.value = res.data.records || []
      annTotal.value = res.data.total || 0
    }
  } catch { /* */ } finally { annLoading.value = false }
}

const handleTabChange = (tab: string) => {
  if (tab === 'notification' && list.value.length === 0) loadNotifications()
  else if (tab === 'announcement' && annList.value.length === 0) loadAnnouncements()
}

const handleRead = async (item: any) => {
  if (item.isRead === 0) {
    try {
      await markNotificationRead(item.pkNotification)
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch { /* */ }
  }
  if (item.notificationType === 1 && item.relatedId) {
    router.push(`/student/certificate-detail/${item.relatedId}`)
  }
}

const handleMarkAll = async () => {
  try {
    await markAllNotificationsRead()
    list.value.forEach(n => n.isRead = 1)
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch { ElMessage.error('操作失败') }
}

const showAnnDetail = (item: any) => { annDetail.value = item; detailVisible.value = true }

const roleLabel = (r: string) => r === 'ALL' ? '全体' : r === 'STUDENT' ? '学生' : '教师'

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => { loadNotifications(); loadAnnouncements() })
</script>

<style scoped>
.notification-center { max-width: 1100px; margin: 0 auto; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.msg-tabs { padding: 0 24px; }
.msg-tabs :deep(.el-tabs__header) { margin-bottom: 0; }
.tab-label { font-size: 15px; font-weight: 600; }

/* --- 通知卡片 --- */
.notification-list { display: flex; flex-direction: column; padding: 8px 0 16px; }
.noti-card {
  display: flex; gap: 14px; padding: 16px 20px; cursor: pointer;
  border-radius: 8px; margin: 0 4px; transition: all .2s;
}
.noti-card:hover { background: #f5f7fa; }
.noti-card.unread { background: linear-gradient(90deg, #ecf5ff 0%, #fafafa 100%); }
.noti-icon {
  width: 44px; height: 44px; border-radius: 12px; display: flex;
  align-items: center; justify-content: center; flex-shrink: 0; color: #409eff;
}
.noti-body { flex: 1; min-width: 0; }
.noti-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px; }
.noti-title { font-size: 15px; font-weight: 600; color: #303133; }
.noti-time { font-size: 12px; color: #909399; flex-shrink: 0; margin-left: 16px; }
.noti-text { font-size: 13px; color: #606266; line-height: 1.5; margin-bottom: 4px; }
.noti-badge { margin-top: 2px; }
.unread-tag { display: inline-block; font-size: 11px; color: #409eff; background: #ecf5ff; padding: 1px 8px; border-radius: 10px; }

/* --- 公告卡片 --- */
.announcement-list { display: flex; flex-direction: column; padding: 8px 0 16px; }
.ann-card {
  display: flex; gap: 14px; padding: 16px 20px; cursor: pointer;
  border-radius: 8px; margin: 0 4px; transition: all .2s;
}
.ann-card:hover { background: #f5f7fa; }
.ann-card-left {
  width: 6px; border-radius: 3px; flex-shrink: 0; background: #c0c4cc;
}
.ann-card-left.pinned { background: #f56c6c; }
.ann-priority-dot { display: none; }
.ann-card-body { flex: 1; min-width: 0; }
.ann-card-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.ann-card-title { font-size: 15px; font-weight: 600; color: #303133; }
.ann-card-time { font-size: 12px; color: #909399; flex-shrink: 0; margin-left: 16px; }
.ann-card-tags { display: flex; gap: 6px; flex-wrap: wrap; }

/* --- 弹窗 --- */
.detail-content { padding: 12px 0; line-height: 1.8; font-size: 14px; color: #303133; }
.detail-meta { margin-top: 16px; font-size: 12px; color: #909399; text-align: right; }

.pagination-wrap { display: flex; justify-content: center; margin-top: 16px; }
</style>
