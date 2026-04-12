<template>
  <div>
    <div class="resume-list">
      <div class="page-header">
        <h1>我的简历</h1>
        <el-button type="primary" @click="createNewResume">
          <el-icon><Plus /></el-icon>
          创建新简历
        </el-button>
      </div>

      <div class="resume-grid">
        <div 
          v-for="(resume, idx) in resumes" 
          :key="resume.id"
          class="resume-card"
          @click="previewResume(resume.id)"
        >
          <div class="card-header">
            <h3>{{ resume.title }}</h3>
            <div class="actions">
              <el-tag :type="resume.status === 'PUBLISHED' ? 'success' : resume.status === 'DRAFT' ? 'info' : 'warning'" size="small" effect="plain">
                {{ getStatusText(resume.status) }}
              </el-tag>
              <el-button type="primary" size="small" @click.stop="previewResume(resume.id)">预览</el-button>
              <el-button type="default" size="small" @click.stop="editResume(resume.id)">编辑</el-button>
              <el-button type="success" size="small" @click.stop="downloadPDF(resume.id)">下载</el-button>
              <el-dropdown @command="(cmd) => handleMore(cmd, resume)" trigger="click">
                <el-button type="link" @click.stop>
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="share">分享简历</el-dropdown-item>
                    <el-dropdown-item command="delete">删除简历</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          
          <div class="card-content">
            <p class="description">
              {{ `我的简历${idx + 1}` }}
            </p>
            <div v-if="resume.skills && resume.skills.length" class="skills">
              <el-tag
                v-for="(skill, sidx) in resume.skills"
                :key="sidx"
                size="small"
                effect="plain"
                style="margin-right: 4px; margin-bottom: 2px;"
              >{{ skill.skillName || skill.name }}</el-tag>
            </div>
          </div>
          
          <div class="card-footer">
            <div class="stats">
              <span><el-icon><ViewIcon /></el-icon> {{ resume.viewCount || 0 }}</span>
              <span><el-icon><Download /></el-icon> {{ resume.downloadCount || 0 }}</span>
            </div>
            <span class="update-time">{{ formatTime(resume.updatedAt) }}</span>
            <el-tag v-if="resume.isDefault" type="success" size="small">默认</el-tag>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="resumes.length === 0" class="empty-state">
          <el-empty description="还没有简历，快来创建第一份简历吧！">
            <el-button type="primary" @click="createNewResume">创建简历</el-button>
          </el-empty>
        </div>
      </div>
    </div>
    <el-dialog v-model="shareDialogVisible" title="分享简历" width="400px" :close-on-click-modal="false">
      <div style="display:flex;align-items:center;gap:8px;">
        <el-input v-model="shareUrl" readonly style="flex:1;" />
        <el-button type="primary" @click="copyShareUrl">复制</el-button>
      </div>
      <template #footer>
        <el-button @click="shareDialogVisible=false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, More, View as ViewIcon, Download } from '@element-plus/icons-vue'
import { getMyResumes, generateShareUrl, deleteResume } from '@/api/resume'
import dayjs from 'dayjs'

export default {
  name: 'ResumeList',
  components: {
    Plus,
    More,
    ViewIcon,
    Download
  },
  setup() {
    const router = useRouter()
    const resumes = ref([])
    const loading = ref(false)

    // 加载简历列表
    const loadResumes = async () => {
      loading.value = true
      try {
        const res = await getMyResumes()
        if (res.code === 0 && Array.isArray(res.data)) {
          resumes.value = res.data
        } else {
          resumes.value = []
          ElMessage.error(res.message || '加载简历列表失败')
        }
      } catch (error) {
        resumes.value = []
        ElMessage.error('加载简历列表失败')
        console.error('Load resumes error:', error)
      } finally {
        loading.value = false
      }
    }

    // 创建新简历
    const createNewResume = () => {
      router.push('/applicant/resume/editor')
    }

    // 编辑简历
    const editResume = (id) => {
      router.push(`/applicant/resume/editor/${id}`)
    }

    // 预览简历
    const previewResume = (id) => {
      router.push(`/applicant/resume/editor/${id}`)
    }

    // 下载PDF
    const downloadPDF = (id) => {
      // 跳转到预览页并自动下载PDF
      // 这里采用 window.open 新窗口方式，预览页需实现自动下载
      window.open(`/applicant/resume/editor/${id}?download=1`, '_blank')
    }

    // 处理更多操作
    const handleMore = async (command, resume) => {
      if (command === 'delete') {
        // 删除简历
        ElMessageBox.confirm('确定要删除该简历吗？', '删除确认', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          try {
            await deleteResume(resume.id)
            ElMessage.success('删除成功')
            fetchResumeList()
          } catch (e) {
            ElMessage.error('删除失败')
          }
        }).catch(() => {})
      } else if (command === 'share') {
        // 分享简历
        try {
          const res = await generateShareUrl(resume.id)
          if (res.code === 0 && res.data) {
            shareUrl.value = res.data
            shareDialogVisible.value = true
          } else {
            ElMessage.error(res.message || '生成分享链接失败')
          }
        } catch (e) {
          ElMessage.error('生成分享链接失败')
        }
      }
    }

    // 分享弹窗相关
    const shareDialogVisible = ref(false)
    const shareUrl = ref('')
    const copyShareUrl = () => {
      if (!shareUrl.value) return
      navigator.clipboard.writeText(shareUrl.value)
      ElMessage.success('已复制到剪贴板')
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        DRAFT: '草稿',
        PUBLISHED: '已发布',
        ARCHIVED: '已归档'
      }
      return statusMap[status] || '草稿'
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return ''
      return dayjs(time).format('YYYY-MM-DD HH:mm')
    }

    // 新增：查找简历列表接口
    const fetchResumeList = async () => {
      loading.value = true
      try {
        const res = await getMyResumes()
        if (res.code === 0 && Array.isArray(res.data)) {
          resumes.value = res.data
        } else {
          resumes.value = []
          ElMessage.error(res.message || '查找简历列表失败')
        }
      } catch (error) {
        resumes.value = []
        ElMessage.error('查找简历列表失败')
        console.error('Fetch resume list error:', error)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchResumeList()
    })

    return {
      resumes,
      loading,
      createNewResume,
      editResume,
      previewResume,
      downloadPDF,
      handleMore,
      getStatusText,
      formatTime,
      shareDialogVisible,
      shareUrl,
      copyShareUrl
    }
  }
}
</script>

<style scoped>
/* 迁移自frontend，将scss语法改为css - 简历列表页面样式 */
.resume-list {
  width: 100%;
  box-sizing: border-box;
  padding: 16px; /* 统一为16px，避免多余间距 */
  max-width: 1200px;
  margin: 0 auto;
  background: #f5f5f5;
  margin-top: 0 !important;
  padding-top: 0 !important;
  margin-bottom: 0 !important;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.resume-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  width: 100%;
  box-sizing: border-box;
}

.resume-card {
  background: #fff;
  border-radius: 18px;
  padding: 22px 22px 16px 22px;
  border: 1px solid var(--el-border-color-light);
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  position: relative;
}
.resume-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.10);
  transform: translateY(-3px) scale(1.01);
  border-color: #a0cfff;
}
.resume-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}
.resume-card .card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  flex: 1;
  line-height: 1.2;
  letter-spacing: 0.5px;
}
.resume-card .card-header .actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.resume-card .card-content {
  margin-bottom: 12px;
}
.resume-card .card-content .description {
  margin: 0 0 8px 0;
  color: #888;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
}
.resume-card .card-content .skills {
  display: flex;
  flex-wrap: wrap;
  gap: 2px 4px;
}
.resume-card .card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 2px;
}
.resume-card .card-footer .stats {
  display: flex;
  gap: 14px;
}
.resume-card .card-footer .stats span {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: #bbb;
}
.resume-card .card-footer .stats span .el-icon {
  font-size: 13px;
}
.resume-card .card-footer .update-time {
  font-size: 11px;
  color: #aaa;
  margin-left: 8px;
}
.resume-card .card-footer .el-tag {
  margin-left: 8px;
}
.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
}
@media (max-width: 1024px) {
  .resume-list {
    max-width: 100%;
    padding: 16px;
  }
  .resume-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}
</style>
