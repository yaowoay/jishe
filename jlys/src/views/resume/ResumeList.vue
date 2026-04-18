<template>
  <div class="resume-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">我的简历</h1>
        <p class="page-subtitle">创建、上传、管理您的简历</p>
      </div>
      <div class="header-actions">
        <el-button type="success" @click="showUploadDialog">
          <el-icon><UploadFilled /></el-icon>
          上传简历
        </el-button>
        <el-button type="primary" @click="createNewResume">
          <el-icon><Plus /></el-icon>
          创建新简历
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stats-grid">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon created">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ resumes.length }}</div>
              <div class="stat-label">创建的简历</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon uploaded">
              <el-icon><UploadFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ uploadedResumeList.length }}</div>
              <div class="stat-label">上传的简历</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon submitted">
              <el-icon><Promotion /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ applicationCount }}</div>
              <div class="stat-label">投递次数</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Tab切换 -->
    <el-tabs v-model="activeTab" class="resume-tabs">
      <!-- 创建的简历 -->
      <el-tab-pane label="创建的简历" name="created">
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
      </el-tab-pane>

      <!-- 上传的简历 -->
      <el-tab-pane label="上传的简历" name="uploaded">
        <!-- 已上传简历列表 -->
        <div class="uploaded-resume-list">
          <el-card>
            <template #header>
              <div class="card-header-simple">
                <span>已上传的简历文件</span>
                <div class="header-actions">
                  <el-input
                    v-model="searchKeyword"
                    placeholder="搜索简历文件名..."
                    :prefix-icon="Search"
                    class="search-input"
                    @input="handleSearch"
                    clearable
                    style="width: 250px; margin-right: 12px;"
                  />
                  <el-button @click="loadUploadedResumeList" :icon="Refresh">
                    刷新
                  </el-button>
                </div>
              </div>
            </template>

            <el-table
              :data="filteredUploadedResumeList"
              v-loading="uploadedLoading"
              stripe
              style="width: 100%"
              empty-text="暂无上传的简历文件"
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
                      @click="previewUploadedResume(row)"
                      :icon="ViewIcon"
                    >
                      预览
                    </el-button>
                    <el-button
                      type="success"
                      size="small"
                      @click="downloadUploadedResume(row)"
                      :icon="Download"
                    >
                      下载
                    </el-button>
                    <el-button
                      type="warning"
                      size="small"
                      @click="analyzeUploadedResume(row)"
                      :icon="DataAnalysis"
                    >
                      分析
                    </el-button>
                    <el-button
                      type="danger"
                      size="small"
                      @click="deleteUploadedResume(row)"
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
      </el-tab-pane>
    </el-tabs>

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
            <el-button type="primary" @click="downloadUploadedResume(previewResumeData)">
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

    <!-- 分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享简历" width="400px" :close-on-click-modal="false">
      <div style="display:flex;align-items:center;gap:8px;">
        <el-input v-model="shareUrl" readonly style="flex:1;" />
        <el-button type="primary" @click="copyShareUrl">复制</el-button>
      </div>
      <template #footer>
        <el-button @click="shareDialogVisible=false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 上传简历对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传简历文件"
      width="700px"
      :before-close="closeUploadDialog"
    >
      <el-upload
        ref="uploadRef"
        class="upload-dragger"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="pendingFiles"
        :limit="5"
        :on-exceed="handleExceed"
        accept=".doc,.docx,.pdf,.jpg,.jpeg,.png,.gif"
        multiple
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击选择文件</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 Word(.doc/.docx)、PDF(.pdf)、图片(.jpg/.png/.gif) 格式，单个文件不超过10MB
          </div>
        </template>
      </el-upload>

      <!-- 待上传文件列表 -->
      <div v-if="pendingFiles.length > 0" class="pending-files-section">
        <h3>待上传文件</h3>
        <div v-for="(file, index) in pendingFiles" :key="index" class="pending-file-item">
          <div class="file-info">
            <el-icon class="file-icon">
              <Document v-if="isDocFile(file.name)" />
              <PictureIcon v-else-if="isImageFile(file.name)" />
              <Document v-else />
            </el-icon>
            <span class="original-name">{{ file.name }}</span>
          </div>

          <div class="filename-edit">
            <el-input
              v-model="file.customName"
              placeholder="输入自定义文件名（不含扩展名）"
              style="width: 250px;"
            >
              <template #prepend>📄</template>
              <template #append>{{ getFileExtension(file.name) }}</template>
            </el-input>
          </div>

          <div class="file-actions">
            <el-button
              type="danger"
              size="small"
              @click="removePendingFile(index)"
              :icon="Delete"
            >
              移除
            </el-button>
          </div>
        </div>
      </div>

      <div class="upload-progress" v-if="uploading">
        <el-progress :percentage="uploadProgress" :status="progressStatus" />
        <p class="progress-text">{{ progressText }}</p>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeUploadDialog">取消</el-button>
          <el-button @click="clearAllPendingFiles" v-if="pendingFiles.length > 0">清空列表</el-button>
          <el-button
            type="primary"
            @click="uploadAllFiles"
            :loading="uploading"
            :disabled="pendingFiles.length === 0"
          >
            {{ uploading ? '上传中...' : '开始上传' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  More,
  View as ViewIcon,
  Download,
  Grid,
  UploadFilled,
  Document,
  Picture as PictureIcon,
  Search,
  Refresh,
  DataAnalysis,
  Delete,
  Promotion
} from '@element-plus/icons-vue'
import {
  getMyResumes,
  generateShareUrl,
  deleteResume,
  copyResume,
  getResumeTemplates,
  getResumeList,
  deleteResumeById,
  getApplicationHistory
} from '@/api/resume'
import dayjs from 'dayjs'

export default {
  name: 'ResumeList',
  components: {
    Plus,
    More,
    ViewIcon,
    Download,
    Grid,
    UploadFilled,
    Document,
    PictureIcon,
    Search,
    Refresh,
    DataAnalysis,
    Delete,
    Promotion
  },
  setup() {
    const router = useRouter()
    const resumes = ref([])
    const loading = ref(false)
    const templates = ref([])

    // 上传相关
    const activeTab = ref('created')
    const uploadRef = ref(null)
    const uploading = ref(false)
    const uploadProgress = ref(0)
    const progressStatus = ref('')
    const progressText = ref('')
    const pendingFiles = ref([])
    const uploadDialogVisible = ref(false)

    // 已上传简历相关
    const uploadedResumeList = ref([])
    const uploadedLoading = ref(false)
    const searchKeyword = ref('')
    const applicationHistory = ref([])
    const previewVisible = ref(false)
    const analysisVisible = ref(false)
    const previewResumeData = ref(null)
    const analyzingResume = ref(null)

    const uploadUrl = process.env.NODE_ENV === 'production'
      ? '/api/resume/upload'
      : 'http://localhost:8089/resume/upload'

    const uploadHeaders = {
      Authorization: `Bearer ${localStorage.getItem('token')}`
    }

    // 计算属性
    const filteredUploadedResumeList = computed(() => {
      if (!searchKeyword.value) {
        return uploadedResumeList.value
      }

      const keyword = searchKeyword.value.toLowerCase()
      return uploadedResumeList.value.filter(resume =>
        resume.filename.toLowerCase().includes(keyword)
      )
    })

    const applicationCount = computed(() => {
      return applicationHistory.value.length
    })

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

    const loadResumes = async () => {
      loading.value = true
      try {
        const res = await getMyResumes()
        console.log('=== loadResumes 调试 ===')
        console.log('res:', res)
        console.log('res.code:', res.code)
        console.log('res.data:', res.data)
        console.log('res.data 是数组吗?', Array.isArray(res.data))
        console.log('res.data 长度:', res.data?.length)

        if (res.code === 0 && Array.isArray(res.data)) {
          resumes.value = res.data
          console.log('赋值后 resumes.value:', resumes.value)
        } else {
          resumes.value = []
          console.log('进入 else 分支')
          ElMessage.error(res.message || '加载简历列表失败')
        }
      } catch (error) {
        console.error('Load resumes error:', error)
        resumes.value = []
        ElMessage.error('加载简历列表失败')
      } finally {
        loading.value = false
      }
    }
    // 加载上传的简历列表
    const loadUploadedResumeList = async () => {
      uploadedLoading.value = true
      try {
        const response = await getResumeList()
        console.log('获取列表返回:', response)  // 👈 看返回什么
        // 兼容两种返回格式
        const isSuccess = response.success === true || response.code === 0
        if (isSuccess) {
          uploadedResumeList.value = response.data || []
          console.log('更新后的列表:', uploadedResumeList.value)  // 👈 看数据
        } else {
          ElMessage.error(response.message || '获取简历列表失败')
        }
      } catch (error) {
        console.error('Load uploaded resume list error:', error)
        ElMessage.error('获取简历列表失败')
      } finally {
        uploadedLoading.value = false
      }
    }

    // 加载投递历史
    const loadApplicationHistory = async () => {
      try {
        const response = await getApplicationHistory()
        // 兼容两种返回格式
        const isSuccess = response.success === true || response.code === 0
        if (isSuccess) {
          applicationHistory.value = response.data || []
        }
      } catch (error) {
        console.error('Load application history error:', error)
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
      window.open(`/applicant/resume/editor/${id}?download=1`, '_blank')
    }

    // 处理更多操作
    const handleMore = async (command, resume) => {
      if (command === 'delete') {
        ElMessageBox.confirm('确定要删除该简历吗？', '删除确认', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          try {
            await deleteResume(resume.id)
            ElMessage.success('删除成功')
            loadUploadedResumeList()
          } catch (e) {
            ElMessage.error('删除失败')
          }
        }).catch(() => {})
      } else if (command === 'copy') {
        try {
          await copyResume(resume.id)
          ElMessage.success('复制成功')
          loadUploadedResumeList()
        } catch (e) {
          ElMessage.error('复制失败')
        }
      } else if (command === 'share') {
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
        try {
          const result = await deleteResume(resume.id)  // 接收返回值
          console.log('删除返回结果:', result)  // 👈 看返回什么
          console.log('删除的ID:', resume.id)
          ElMessage.success('删除成功')
          await loadUploadedResumeList()  // 等待刷新完成
          console.log('刷新后的列表:', uploadedResumeList.value)  // 👈 看列表是否更新
        } catch (e) {
          console.error('删除失败:', e)
          ElMessage.error('删除失败')
        }
      }
    }

    // 上传相关方法
    const showUploadDialog = () => {
      uploadDialogVisible.value = true
    }

    const closeUploadDialog = () => {
      uploadDialogVisible.value = false
      clearAllPendingFiles()
    }

    const handleFileChange = (file, fileList) => {
      const allowedTypes = [
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/pdf',
        'image/jpeg',
        'image/jpg',
        'image/png',
        'image/gif'
      ]

      const isAllowedType = allowedTypes.includes(file.raw.type)
      const isLt10M = file.raw.size / 1024 / 1024 < 10

      if (!isAllowedType) {
        ElMessage.error('只支持 Word、PDF、图片格式的文件!')
        return false
      }
      if (!isLt10M) {
        ElMessage.error('文件大小不能超过 10MB!')
        return false
      }

      file.customName = getFileNameWithoutExtension(file.name)
      pendingFiles.value = fileList
    }

    const handleFileRemove = (file, fileList) => {
      pendingFiles.value = fileList
    }

    const removePendingFile = (index) => {
      pendingFiles.value.splice(index, 1)
    }

    const clearAllPendingFiles = () => {
      pendingFiles.value = []
      uploadRef.value.clearFiles()
    }

    const uploadAllFiles = async () => {
      if (pendingFiles.value.length === 0) {
        ElMessage.warning('请先选择文件')
        return
      }

      uploading.value = true
      uploadProgress.value = 0
      progressStatus.value = ''

      try {
        for (let i = 0; i < pendingFiles.value.length; i++) {
          const file = pendingFiles.value[i]

          progressText.value = `正在上传第 ${i + 1}/${pendingFiles.value.length} 个文件: ${file.name}`
          uploadProgress.value = Math.round((i / pendingFiles.value.length) * 100)

          await uploadSingleFile(file)
        }

        ElMessage.success('所有文件上传成功!')
        clearAllPendingFiles()
        loadUploadedResumeList()
        uploadDialogVisible.value = false
        activeTab.value = 'uploaded'
      } catch (error) {
        ElMessage.error('上传失败: ' + error.message)
      } finally {
        uploading.value = false
        uploadProgress.value = 100
        progressText.value = '上传完成'
      }
    }

    const uploadSingleFile = (file) => {
      return new Promise((resolve, reject) => {
        const formData = new FormData()
        formData.append('file', file.raw)

        const finalFilename = file.customName
          ? file.customName + getFileExtension(file.name)
          : file.name
        formData.append('filename', finalFilename)

        fetch(uploadUrl, {
          method: 'POST',
          headers: uploadHeaders,
          body: formData
        })
          .then(response => response.json())
          .then(data => {
            if (data.success) {
              resolve(data)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          })
          .catch(error => {
            reject(error)
          })
      })
    }

    const handleExceed = () => {
      ElMessage.warning('最多只能上传5个文件')
    }

    // 已上传简历相关方法
    const previewUploadedResume = (resume) => {
      if (isImageFile(resume.filename)) {
        window.open(resume.fileUrl, '_blank')
      } else {
        previewResumeData.value = resume
        previewVisible.value = true
      }
    }

    const closePreview = () => {
      previewVisible.value = false
      previewResumeData.value = null
    }

    const downloadUploadedResume = async (resume) => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('请先登录')
          return
        }

        const downloadUrl = process.env.NODE_ENV === 'production'
          ? `/api/resume/${resume.resumeId}/download`
          : `http://localhost:8089/api/resume/${resume.resumeId}/download`

        const response = await fetch(downloadUrl, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (!response.ok) {
          if (response.status === 403) {
            ElMessage.error('没有权限下载此文件')
          } else if (response.status === 404) {
            ElMessage.error('文件不存在')
          } else {
            ElMessage.error(`下载失败: ${response.status}`)
          }
          return
        }

        const blob = await response.blob()
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = resume.filename
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        ElMessage.success('下载成功')
      } catch (error) {
        console.error('Download error:', error)
        ElMessage.error('下载失败：' + error.message)
      }
    }

    const analyzeUploadedResume = (resume) => {
      analyzingResume.value = resume
      analysisVisible.value = true
    }

    const closeAnalysis = () => {
      analysisVisible.value = false
      analyzingResume.value = null
    }

    const deleteUploadedResume = async (resume) => {
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
          loadUploadedResumeList()
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

    // 获取模板预览图
    const getTemplatePreview = (resume) => {
      if (!resume.templateId) {
        return '/images/custom-resume-preview.svg'
      }

      const template = templates.value.find(t => t.template_id === resume.templateId)
      if (template?.preview_image) {
        return template.preview_image
      }

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

    // 工具方法
    const isDocFile = (filename) => {
      return /\.(doc|docx)$/i.test(filename)
    }

    const isImageFile = (filename) => {
      return /\.(jpg|jpeg|png|gif)$/i.test(filename)
    }

    const getFileExtension = (filename) => {
      const lastDotIndex = filename.lastIndexOf('.')
      return lastDotIndex !== -1 ? filename.substring(lastDotIndex) : ''
    }

    const getFileNameWithoutExtension = (filename) => {
      const lastDotIndex = filename.lastIndexOf('.')
      return lastDotIndex !== -1 ? filename.substring(0, lastDotIndex) : filename
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
      loadTemplates()
      loadResumes()
      loadUploadedResumeList()
      loadApplicationHistory()
    })

    return {
      activeTab,
      resumes,
      loading,
      templates,
      uploadRef,
      uploading,
      uploadProgress,
      progressStatus,
      progressText,
      pendingFiles,
      uploadDialogVisible,
      uploadedResumeList,
      uploadedLoading,
      searchKeyword,
      applicationHistory,
      previewVisible,
      analysisVisible,
      previewResumeData,
      analyzingResume,
      filteredUploadedResumeList,
      applicationCount,
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
      copyShareUrl,
      showUploadDialog,
      closeUploadDialog,
      handleFileChange,
      handleFileRemove,
      removePendingFile,
      clearAllPendingFiles,
      uploadAllFiles,
      handleExceed,
      loadUploadedResumeList,
      previewUploadedResume,
      closePreview,
      downloadUploadedResume,
      analyzeUploadedResume,
      closeAnalysis,
      deleteUploadedResume,
      getApplicationCount,
      handleSearch,
      isDocFile,
      isImageFile,
      getFileExtension,
      getFileNameWithoutExtension,
      formatFileSize,
      formatDate,
      Plus,
      More,
      ViewIcon,
      Download,
      Grid,
      UploadFilled,
      Document,
      PictureIcon,
      Search,
      Refresh,
      DataAnalysis,
      Delete,
      Promotion
    }
  }
}
</script>

<style scoped>
/* 容器样式 */
.resume-list-container {
  width: 100%;
  box-sizing: border-box;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.header-left {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #1e50c8;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #6682b4;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-icon.created {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-icon.uploaded {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-icon.submitted {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
}

/* Tab样式 */
.resume-tabs {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

/* 简历网格 */
.resume-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  width: 100%;
  box-sizing: border-box;
  margin-top: 20px;
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

/* 上传区域样式 */
.upload-section {
  margin-top: 20px;
}

.upload-card {
  margin-bottom: 24px;
  border: 1px solid #d0e0ff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.05);
  background-color: #ffffff;
}

.card-header-simple {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #1e50c8;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.upload-dragger {
  border: 2px dashed #d0e0ff !important;
  border-radius: 8px;
  padding: 48px 24px;
  transition: all 0.3s;
}

.upload-dragger:hover {
  border-color: #1e50c8 !important;
  background-color: #f0f7ff;
}

.el-icon--upload {
  font-size: 48px;
  color: #1e50c8;
  margin-bottom: 16px;
}

.el-upload__text {
  font-size: 14px;
  color: #606266;
}

.el-upload__text em {
  color: #1e50c8;
  font-style: normal;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

/* 待上传文件列表 */
.pending-files-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}

.pending-files-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1e50c8;
  margin: 0 0 16px 0;
}

.pending-file-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 12px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.file-icon {
  font-size: 20px;
  color: #1e50c8;
  flex-shrink: 0;
}

.original-name {
  font-size: 14px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.filename-edit {
  flex: 1;
}

.file-actions {
  flex-shrink: 0;
}

.upload-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  justify-content: center;
}

.upload-progress {
  margin-top: 24px;
  padding: 16px;
  background: #f0f7ff;
  border-radius: 8px;
}

.progress-text {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
  text-align: center;
}

/* 已上传简历列表 */
.uploaded-resume-list {
  margin-top: 24px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filename {
  font-weight: 500;
  color: #303133;
}

.search-input {
  width: 250px;
}

/* 预览对话框 */
.preview-content {
  min-height: 400px;
}

.image-preview {
  text-align: center;
}

.preview-image {
  max-width: 100%;
  max-height: 600px;
  border-radius: 8px;
}

.file-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.preview-placeholder {
  text-align: center;
}

.preview-icon {
  font-size: 80px;
  color: #909399;
  margin-bottom: 16px;
}

.preview-tip {
  color: #909399;
  margin: 16px 0;
}

/* 分析对话框 */
.analysis-content {
  min-height: 300px;
}

.analysis-header {
  text-align: center;
  margin-bottom: 24px;
}

.analysis-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.analysis-tip {
  color: #909399;
  font-size: 14px;
}

.analysis-placeholder {
  text-align: center;
  padding: 40px 20px;
}

.analysis-icon {
  font-size: 80px;
  color: #409eff;
  margin-bottom: 16px;
}

.feature-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
  margin-top: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .resume-list-container {
    max-width: 100%;
    padding: 16px;
  }

  .resume-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>