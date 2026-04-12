<template>
  <div class="resume-manage-container">
    <div class="page-header">
      <h1 class="page-title">简历管理</h1>
      <p class="page-subtitle">查看、下载、分析已上传的简历</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stats-grid">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ resumeList.length }}</div>
              <div class="stat-label">简历总数</div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Promotion /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ applicationCount }}</div>
              <div class="stat-label">投递次数</div>
            </div>
          </div>
        </el-card>
        
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">0</div>
              <div class="stat-label">已分析</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 操作工具栏 -->
    <div class="toolbar-section">
      <el-card>
        <div class="toolbar">
          <div class="toolbar-left">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索简历文件名..."
              :prefix-icon="Search"
              class="search-input"
              @input="handleSearch"
              clearable
            />
          </div>
          <div class="toolbar-right">
            <el-button type="primary" @click="$router.push('/applicant/resume/upload')" :icon="Plus">
              上传简历
            </el-button>
            <el-button @click="loadResumeList" :icon="Refresh">
              刷新
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 简历列表 -->
    <div class="resume-list-section">
      <el-card>
        <el-table
          :data="filteredResumeList"
          v-loading="loading"
          stripe
          style="width: 100%"
          empty-text="暂无简历文件"
        >
          <el-table-column prop="filename" label="文件名" min-width="200">
            <template #default="{ row }">
              <div class="file-info">
                <el-icon class="file-icon">
                  <Document v-if="isDocFile(row.filename)" />
                  <PictureIcon v-else-if="isImageFile(row.filename)" />
                  <Document v-else />
                </el-icon>
                <span class="filename">{{ row.filename }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="文件大小" width="120">
            <template #default>
              <span>-</span>
            </template>
          </el-table-column>

          <el-table-column prop="uploadDate" label="上传时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.uploadDate) }}
            </template>
          </el-table-column>
          
          <el-table-column label="投递次数" width="100">
            <template #default="{ row }">
              <el-tag type="info">{{ getApplicationCount(row.resumeId) }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="100">
            <template #default>
              <el-tag type="success">可用</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="small"
                  @click="previewResume(row)"
                  :icon="ViewIcon"
                >
                  预览
                </el-button>
                <el-button
                  type="success"
                  size="small"
                  @click="downloadResume(row)"
                  :icon="Download"
                >
                  下载
                </el-button>
                <el-button
                  type="warning"
                  size="small"
                  @click="analyzeResume(row)"
                  :icon="DataAnalysis"
                >
                  分析
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="deleteResume(row)"
                  :icon="Delete"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 简历预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="简历预览"
      width="80%"
      :before-close="closePreview"
    >
      <div v-if="previewResumeData" class="preview-content">
        <div v-if="isImageFile(previewResumeData.filename)" class="image-preview">
          <img :src="previewResumeData.fileUrl" alt="简历预览" class="preview-image" />
        </div>
        <div v-else class="file-preview">
          <div class="preview-placeholder">
            <el-icon class="preview-icon"><Document /></el-icon>
            <p>{{ previewResumeData.filename }}</p>
            <p class="preview-tip">此文件类型暂不支持在线预览，请下载后查看</p>
            <el-button type="primary" @click="downloadResume(previewResumeData)">
              下载文件
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 简历分析对话框 -->
    <el-dialog
      v-model="analysisVisible"
      title="简历分析"
      width="800px"
      :before-close="closeAnalysis"
    >
      <div v-if="analyzingResume" class="analysis-content">
        <div class="analysis-header">
          <h3>{{ analyzingResume.filename }}</h3>
          <p class="analysis-tip">AI正在分析您的简历，请稍候...</p>
        </div>
        
        <div class="analysis-placeholder">
          <el-icon class="analysis-icon"><DataAnalysis /></el-icon>
          <p>简历分析功能正在开发中</p>
          <p class="feature-description">
            即将支持：
            <br>• 简历内容完整性分析
            <br>• 关键词匹配度检测
            <br>• 格式规范性建议
            <br>• 职位匹配度评估
          </p>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAnalysis">关闭</el-button>
          <el-button type="primary" disabled>
            开始分析（开发中）
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Picture as PictureIcon,
  Search,
  Plus,
  Refresh,
  View as ViewIcon,
  Download,
  DataAnalysis,
  Delete,
  Promotion
} from '@element-plus/icons-vue'
import { getResumeList, deleteResumeById, getApplicationHistory } from '@/api/resume'

export default {
  name: 'ResumeManage',
  components: {
    Document,
    PictureIcon,
    Search,
    Plus,
    Refresh,
    ViewIcon,
    Download,
    DataAnalysis,
    Delete,
    Promotion
  },
  setup() {
    const loading = ref(false)
    const previewVisible = ref(false)
    const analysisVisible = ref(false)
    const searchKeyword = ref('')
    
    const resumeList = ref([])
    const applicationHistory = ref([])
    const previewResumeData = ref(null)
    const analyzingResume = ref(null)
    
    // 计算属性
    const filteredResumeList = computed(() => {
      if (!searchKeyword.value) {
        return resumeList.value
      }
      
      const keyword = searchKeyword.value.toLowerCase()
      return resumeList.value.filter(resume =>
        resume.filename.toLowerCase().includes(keyword)
      )
    })
    
    const applicationCount = computed(() => {
      return applicationHistory.value.length
    })
    
    // 方法
    const loadResumeList = async () => {
      loading.value = true
      try {
        const response = await getResumeList()
        if (response.success) {
          resumeList.value = response.data || []
        } else {
          ElMessage.error(response.message || '获取简历列表失败')
        }
      } catch (error) {
        console.error('Load resume list error:', error)
        ElMessage.error('获取简历列表失败')
      } finally {
        loading.value = false
      }
    }
    
    const loadApplicationHistory = async () => {
      try {
        const response = await getApplicationHistory()
        if (response.success) {
          applicationHistory.value = response.data || []
        }
      } catch (error) {
        console.error('Load application history error:', error)
      }
    }
    
    const previewResume = (resume) => {
      previewResumeData.value = resume
      previewVisible.value = true
    }

    const closePreview = () => {
      previewVisible.value = false
      previewResumeData.value = null
    }
    
    const downloadResume = (resume) => {
      const link = document.createElement('a')
      link.href = resume.fileUrl
      link.download = resume.filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      ElMessage.success('下载开始')
    }
    
    const analyzeResume = (resume) => {
      analyzingResume.value = resume
      analysisVisible.value = true
    }
    
    const closeAnalysis = () => {
      analysisVisible.value = false
      analyzingResume.value = null
    }
    
    const deleteResume = async (resume) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除简历"${resume.filename}"吗？删除后将无法恢复。`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const response = await deleteResumeById(resume.resumeId)
        if (response.success) {
          ElMessage.success('删除成功')
          loadResumeList()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Delete resume error:', error)
          ElMessage.error('删除失败')
        }
      }
    }
    
    const getApplicationCount = (resumeId) => {
      return applicationHistory.value.filter(app => app.resumeId === resumeId).length
    }
    
    const handleSearch = () => {
      // 搜索逻辑已在计算属性中实现
    }
    
    // 工具方法
    const isDocFile = (filename) => {
      return /\.(doc|docx)$/i.test(filename)
    }
    
    const isImageFile = (filename) => {
      return /\.(jpg|jpeg|png|gif)$/i.test(filename)
    }
    
    const formatFileSize = (size) => {
      if (!size) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB']
      let index = 0
      while (size >= 1024 && index < units.length - 1) {
        size /= 1024
        index++
      }
      return `${size.toFixed(1)} ${units[index]}`
    }
    
    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      loadResumeList()
      loadApplicationHistory()
    })
    
    return {
      loading,
      previewVisible,
      analysisVisible,
      searchKeyword,
      resumeList,
      applicationHistory,
      previewResumeData,
      analyzingResume,
      filteredResumeList,
      applicationCount,
      loadResumeList,
      loadApplicationHistory,
      previewResume,
      closePreview,
      downloadResume,
      analyzeResume,
      closeAnalysis,
      deleteResume,
      getApplicationCount,
      handleSearch,
      isDocFile,
      isImageFile,
      formatFileSize,
      formatDate,
      // 图标
      Document,
      PictureIcon,
      Search,
      Plus,
      Refresh,
      ViewIcon,
      Download,
      DataAnalysis,
      Delete,
      Promotion
    }
  }
}
</script>
<style scoped>
.resume-manage-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f9ff 0%, #e0efff 100%);
  min-height: 100vh;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a6fc4;
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #5a84b3;
  margin: 0;
  font-weight: 500;
}

.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.stat-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(26, 111, 196, 0.2);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1a6fc4;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #5a84b3;
  font-weight: 500;
}

.toolbar-section {
  margin-bottom: 24px;
}

.toolbar-section .el-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.toolbar-left {
  flex: 1;
}

.search-input {
  max-width: 320px;
}

.search-input :deep(.el-input__inner) {
  border-radius: 20px;
  border: 1px solid #c2dcff;
  background: #f0f7ff;
  padding-left: 40px;
  height: 40px;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__inner:focus) {
  border-color: #1a6fc4;
  box-shadow: 0 0 0 2px rgba(26, 111, 196, 0.2);
}

.search-input :deep(.el-input__prefix) {
  left: 12px;
  color: #5a84b3;
}

.toolbar-right {
  display: flex;
  gap: 12px;
}

.toolbar-right .el-button {
  border-radius: 8px;
  height: 40px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.toolbar-right .el-button--primary {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
}

.toolbar-right .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(26, 111, 196, 0.4);
}

.resume-list-section .el-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  overflow: hidden;
}

:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
}

:deep(.el-table th) {
  background: transparent;
  color: #1a6fc4;
  font-weight: 600;
  height: 56px;
}

:deep(.el-table tr) {
  background: transparent;
  transition: all 0.3s ease;
}

:deep(.el-table .el-table__row:hover) {
  background: #f0f7ff;
}

:deep(.el-table td) {
  border-bottom: 1px solid #e6f1ff;
  padding: 16px 0;
  height: 68px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-icon {
  font-size: 24px;
  color: #1a6fc4;
  background: #e0f0ff;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.filename {
  font-weight: 500;
  color: #2c3e50;
}

:deep(.el-tag) {
  border-radius: 6px;
  font-weight: 500;
  padding: 0 10px;
  height: 26px;
  line-height: 26px;
}

:deep(.el-tag--info) {
  background: #e0f0ff;
  color: #1a6fc4;
  border-color: #c2dcff;
}

:deep(.el-tag--success) {
  background: #e8f7f0;
  color: #36c28f;
  border-color: #c2eedb;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-buttons .el-button--primary {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
}

.action-buttons .el-button--success {
  background: linear-gradient(135deg, #36c28f 0%, #36c2cf 100%);
  border: none;
}

.action-buttons .el-button--warning {
  background: linear-gradient(135deg, #f39c12 0%, #f1c40f 100%);
  border: none;
  color: white;
}

.action-buttons .el-button--danger {
  background: linear-gradient(135deg, #e74c3c 0%, #ff7979 100%);
  border: none;
}

.action-buttons .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(26, 111, 196, 0.2);
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
  margin: 0;
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: #1a6fc4;
  font-weight: 600;
  font-size: 20px;
}

:deep(.el-dialog__body) {
  padding: 30px;
}

:deep(.el-dialog__footer) {
  padding: 20px 30px;
  border-top: 1px solid #e6f1ff;
}

.preview-content {
  text-align: center;
}

.image-preview {
  max-height: 600px;
  overflow: auto;
}

.preview-image {
  max-width: 100%;
  height: auto;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.file-preview {
  padding: 40px;
}

.preview-placeholder {
  text-align: center;
}

.preview-icon {
  font-size: 64px;
  color: #1a6fc4;
  margin-bottom: 16px;
  opacity: 0.7;
}

.preview-tip {
  color: #5a84b3;
  margin: 16px 0;
  font-size: 15px;
}

.analysis-content {
  text-align: center;
  padding: 20px;
}

.analysis-header h3 {
  margin-bottom: 8px;
  color: #1a6fc4;
  font-weight: 600;
}

.analysis-tip {
  color: #5a84b3;
  margin-bottom: 32px;
}

.analysis-placeholder {
  padding: 40px;
  background: #f9fbfd;
  border-radius: 12px;
  margin-top: 20px;
}

.analysis-icon {
  font-size: 64px;
  color: #1a6fc4;
  margin-bottom: 16px;
  opacity: 0.7;
}

.feature-description {
  color: #5a84b3;
  line-height: 1.6;
  margin-top: 16px;
  text-align: left;
  display: inline-block;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-manage-container {
    padding: 16px;
  }

  .page-title {
    font-size: 26px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .search-input {
    max-width: none;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
    margin-bottom: 8px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  :deep(.el-table td) {
    padding: 12px 0;
  }

  :deep(.el-dialog) {
    width: 90% !important;
    margin: 20px auto;
  }
}

/* 加载动画 */
:deep(.el-loading-mask) {
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.8);
}

:deep(.el-loading-spinner .path) {
  stroke: #1a6fc4;
}
</style>
