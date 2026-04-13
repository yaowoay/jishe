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
          <!-- 模板预览图 -->
          <div class="template-preview">
            <img 
              :src="getTemplatePreview(resume)" 
              :alt="resume.title"
              @error="handleImageError"
            />
            <div class="template-badge" v-if="!resume.templateId">
              自定义
            </div>
          </div>
          
          <div class="card-header">
            <h3>{{ resume.title || resume.name || `我的简历${idx + 1}` }}</h3>
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
                    <el-dropdown-item command="copy">复制简历</el-dropdown-item>
                    <el-dropdown-item command="share">分享简历</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除简历</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          
          <div class="card-content">
            <p class="template-info">
              <el-icon><Grid /></el-icon>
              {{ getTemplateName(resume) }}
            </p>
            <p class="description">
              {{ resume.profile || resume.summary || `我的简历${idx + 1}` }}
            </p>
            <div v-if="resume.skills && resume.skills.length" class="skills">
              <el-tag
                v-for="(skill, sidx) in resume.skills.slice(0, 3)"
                :key="sidx"
                size="small"
                effect="plain"
                style="margin-right: 4px; margin-bottom: 2px;"
              >{{ skill.skillName || skill.name }}</el-tag>
              <el-tag v-if="resume.skills.length > 3" size="small" type="info" effect="plain">
                +{{ resume.skills.length - 3 }}
              </el-tag>
            </div>
          </div>
          
          <div class="card-footer">
            <div class="stats">
              <span><el-icon><ViewIcon /></el-icon> {{ resume.viewCount || 0 }}</span>
              <span><el-icon><Download /></el-icon> {{ resume.downloadCount || 0 }}</span>
            </div>
            <span class="update-time">{{ formatTime(resume.updatedAt || resume.updated_at) }}</span>
            <el-tag v-if="resume.isDefault || resume.is_default" type="success" size="small">默认</el-tag>
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
import { Plus, More, View as ViewIcon, Download, Grid } from '@element-plus/icons-vue'
import { getMyResumes, generateShareUrl, deleteResume, copyResume, getResumeTemplates } from '@/api/resume'
import dayjs from 'dayjs'

export default {
  name: 'ResumeList',
  components: {
    Plus,
    More,
    ViewIcon,
    Download,
    Grid
  },
  setup() {
    const router = useRouter()
    const resumes = ref([])
    const loading = ref(false)
    const templates = ref([]) // 模板列表

    // 加载模板列表
    const loadTemplates = async () => {
      try {
        const res = await getResumeTemplates()
        if (res.code === 0 && Array.isArray(res.data)) {
          templates.value = res.data
        }
      } catch (error) {
        console.error('Load templates error:', error)
      }
    }

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
      } else if (command === 'copy') {
        // 复制简历
        try {
          await copyResume(resume.id)
          ElMessage.success('复制成功')
          fetchResumeList()
        } catch (e) {
          ElMessage.error('复制失败')
        }
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

    // 获取模板预览图
    const getTemplatePreview = (resume) => {
      if (!resume.templateId) {
        return '/images/custom-resume-preview.svg' // 自定义简历的默认预览图
      }
      
      const template = templates.value.find(t => t.template_id === resume.templateId)
      if (template?.preview_image) {
        return template.preview_image
      }
      
      // 根据模板名称和类型返回不同的默认预览图
      const templateName = template?.template_name || ''
      if (templateName.includes('照片') || templateName.includes('photo')) {
        return '/images/photo-template-preview.svg'
      } else if (template?.template_type === 'latex' || templateName.includes('LaTeX')) {
        return '/images/latex-template-preview.svg'
      } else if (template?.category === 'it' || templateName.includes('IT')) {
        return '/images/it-template-preview.svg'
      }
      
      return '/images/default-template-preview.svg'
    }

    // 获取模板名称
    const getTemplateName = (resume) => {
      if (!resume.templateId) {
        return '自定义简历'
      }
      
      const template = templates.value.find(t => t.template_id === resume.templateId)
      return template?.template_name || '未知模板'
    }

    // 处理图片加载错误
    const handleImageError = (event) => {
      event.target.src = '/images/default-template-preview.svg'
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
      loadTemplates() // 先加载模板
      fetchResumeList()
    })

    return {
      resumes,
      loading,
      templates,
      createNewResume,
      editResume,
      previewResume,
      downloadPDF,
      handleMore,
      getStatusText,
      formatTime,
      getTemplatePreview,
      getTemplateName,
      handleImageError,
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
  padding: 0;
  border: 1px solid var(--el-border-color-light);
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  position: relative;
  overflow: hidden;
}

.resume-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.10);
  transform: translateY(-3px) scale(1.01);
  border-color: #a0cfff;
}

.resume-card .template-preview {
  position: relative;
  height: 160px;
  overflow: hidden;
  background: #f8f9fa;
}

.resume-card .template-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.resume-card:hover .template-preview img {
  transform: scale(1.05);
}

.resume-card .template-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.resume-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  padding: 16px 22px 0 22px;
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
  padding: 0 22px;
}

.resume-card .card-content .template-info {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.resume-card .card-content .template-info .el-icon {
  font-size: 14px;
  color: #999;
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
  padding: 0 22px 16px 22px;
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
