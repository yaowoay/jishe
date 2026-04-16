<template>
  <div class="resume-upload-container">
    <div class="content-wrapper">
    <div class="page-header">
      <h1 class="page-title">简历上传</h1>
      <p class="page-subtitle">支持上传Word、PDF、图片格式的简历文件</p>
    </div>

    <div class="upload-section">
      <el-card class="upload-card">
        <template #header>
          <div class="card-header">
            <span>上传简历文件</span>
          </div>
        </template>

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
                style="width: 300px;"
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

          <div class="upload-actions">
            <el-button
              type="primary"
              @click="uploadAllFiles"
              :loading="uploading"
              :disabled="pendingFiles.length === 0"
            >
              {{ uploading ? '上传中...' : '保存所有文件' }}
            </el-button>
            <el-button @click="clearAllPendingFiles">清空列表</el-button>
          </div>
        </div>

        <div class="upload-progress" v-if="uploading">
          <el-progress :percentage="uploadProgress" :status="progressStatus" />
          <p class="progress-text">{{ progressText }}</p>
        </div>
      </el-card>
    </div>

    <!-- 已上传简历列表 -->
    <div class="resume-list-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>我的简历</span>
            <el-button type="primary" @click="loadResumeList" :icon="Refresh">
              刷新
            </el-button>
          </div>
        </template>

        <el-table
          :data="resumeList"
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
                <span>{{ row.filename }}</span>
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

          <el-table-column label="操作" width="200" fixed="right">
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
  </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  UploadFilled,
  Document,
  Picture as PictureIcon,
  View as ViewIcon,
  Download,
  Delete,
  Refresh
} from '@element-plus/icons-vue'
import { getResumeList, deleteResumeById, downloadResume as downloadResumeApi } from '@/api/resume'
export default {
  name: 'ResumeUpload',
  components: {
    UploadFilled,
    Document,
    PictureIcon,
    ViewIcon,
    Download,
    Delete,
    Refresh
  },
  setup() {
    const uploadRef = ref(null)
    const loading = ref(false)
    const uploading = ref(false)
    const uploadProgress = ref(0)
    const progressStatus = ref('')
    const progressText = ref('')
    const currentUploadIndex = ref(0)

    const pendingFiles = ref([])
    const resumeList = ref([])

    const uploadUrl = process.env.NODE_ENV === 'production'
      ? '/api/resume/upload'
      : 'http://localhost:8089/api/resume/upload'

    const uploadHeaders = {
      Authorization: `Bearer ${localStorage.getItem('token')}`
    }

    // 文件选择变化处理
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

      // 添加自定义名称字段
      file.customName = getFileNameWithoutExtension(file.name)
      pendingFiles.value = fileList
    }

    // 移除文件
    const handleFileRemove = (file, fileList) => {
      pendingFiles.value = fileList
    }

    // 移除待上传文件
    const removePendingFile = (index) => {
      pendingFiles.value.splice(index, 1)
    }

    // 清空所有待上传文件
    const clearAllPendingFiles = () => {
      pendingFiles.value = []
      uploadRef.value.clearFiles()
    }

    // 上传所有文件
    const uploadAllFiles = async () => {
      if (pendingFiles.value.length === 0) {
        ElMessage.warning('请先选择文件')
        return
      }

      uploading.value = true
      uploadProgress.value = 0
      progressStatus.value = ''
      currentUploadIndex.value = 0

      try {
        for (let i = 0; i < pendingFiles.value.length; i++) {
          currentUploadIndex.value = i
          const file = pendingFiles.value[i]

          progressText.value = `正在上传第 ${i + 1}/${pendingFiles.value.length} 个文件: ${file.name}`
          uploadProgress.value = Math.round((i / pendingFiles.value.length) * 100)

          await uploadSingleFile(file)
        }

        ElMessage.success('所有文件上传成功!')
        clearAllPendingFiles()
        loadResumeList()
      } catch (error) {
        ElMessage.error('上传失败: ' + error.message)
      } finally {
        uploading.value = false
        uploadProgress.value = 100
        progressText.value = '上传完成'
      }
    }

    // 上传单个文件
    const uploadSingleFile = (file) => {
      return new Promise((resolve, reject) => {
        const formData = new FormData()
        formData.append('file', file.raw)

        // 使用自定义文件名或原始文件名
        const finalFilename = file.customName
          ? file.customName + getFileExtension(file.name)
          : file.name
        formData.append('filename', finalFilename)

        fetch(uploadUrl, {
          method: 'POST',
          headers: uploadHeaders,
          body: formData
        })
          .then(response => {
            console.log('Upload response status:', response.status)
            console.log('Upload response headers:', response.headers)

            // 先检查 HTTP 状态码
            if (!response.ok) {
              return response.json().then(data => {
                throw new Error(`HTTP ${response.status}: ${data.message || '上传失败'}`)
              }).catch(err => {
                throw new Error(`HTTP ${response.status}: 上传失败`)
              })
            }

            return response.json()
          })
          .then(data => {
            console.log('Upload response data:', data)
            console.log('Upload response data.success:', data.success, 'type:', typeof data.success)

            // 检查业务状态 - success 可能是布尔值或字符串
            const isSuccess = data.success === true || data.success === 'true' || data.success === 1

            if (isSuccess) {
              console.log('Upload success:', data)
              resolve(data)
            } else {
              console.error('Upload failed - success is false:', data)
              reject(new Error(data.message || '上传失败'))
            }
          })
          .catch(error => {
            console.error('Upload error:', error)
            reject(error)
          })
      })
    }

    // 文件数量超出限制
    const handleExceed = () => {
      ElMessage.warning('最多只能上传5个文件')
    }

    // 加载简历列表
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

    // 预览简历
    const previewResume = (resume) => {
      // 根据文件类型决定预览方式
      if (isImageFile(resume.filename)) {
        // 图片文件直接在新窗口打开
        window.open(resume.fileUrl, '_blank')
      } else {
        // PDF和Word文件的预览
        ElMessage.info('预览功能开发中...')
      }
    }

    // 下载简历 - 修复权限问题
    const downloadResume = async (resume) => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('请先登录')
          return
        }

        const downloadUrl = process.env.NODE_ENV === 'production'
          ? `/api/resume/${resume.resumeId}/download`
          : `http://localhost:8089/api/resume/${resume.resumeId}/download`

        // 使用 fetch 获取文件，正确传递 Authorization 头
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

    // 删除简历
    const deleteResume = async (resume) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除简历"${resume.filename}"吗？`,
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
      loadResumeList()
    })

    return {
      uploadRef,
      loading,
      uploading,
      uploadProgress,
      progressStatus,
      progressText,
      currentUploadIndex,
      pendingFiles,
      resumeList,
      uploadUrl,
      uploadHeaders,
      handleFileChange,
      handleFileRemove,
      removePendingFile,
      clearAllPendingFiles,
      uploadAllFiles,
      handleExceed,
      loadResumeList,
      previewResume,
      downloadResume,
      deleteResume,
      isDocFile,
      isImageFile,
      getFileExtension,
      getFileNameWithoutExtension,
      formatFileSize,
      formatDate,
      // 图标
      UploadFilled,
      Document,
      PictureIcon,
      ViewIcon,
      Download,
      Delete,
      Refresh
    }
  }
}
</script>


<style scoped>
/* 全局容器样式 - 保持背景，新增居中布局 */
.resume-upload-container {
  width: 100%;
  min-height: 100vh;
  padding: 24px 0; /* 调整上下内边距，取消左右内边距 */
  background-color: #f0f7ff;
  box-sizing: border-box;
  display: flex;
  justify-content: center; /* 水平居中 */
}

/* 新增固定宽度内容容器 - 核心修改点 */
.content-wrapper {
  width: 1200px; /* 固定宽度，适配主流屏幕 */
  max-width: 100%; /* 小屏幕下自适应 */
  padding: 0 24px; /* 小屏幕时保留左右内边距 */
  box-sizing: border-box;
}

/* 页面头部样式 - 调整适配固定宽度容器 */
.page-header {
  margin-bottom: 32px;
}
.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1e50c8;
  margin: 0 0 8px 0;
}
.page-subtitle {
  font-size: 14px;
  color: #6682b4;
  margin: 0;
}

/* 卡片通用样式 - 白色背景+蓝色边框（不变） */
.upload-card,
.resume-list-section .el-card {
  margin-bottom: 24px;
  border: 1px solid #d0e0ff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(30, 80, 200, 0.05);
  background-color: #ffffff;
}

/* 卡片头部样式（不变） */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #1e50c8;
}
.card-header .el-button {
  --el-button-primary-bg-color: #1e50c8;
  --el-button-primary-border-color: #1e50c8;
  --el-button-primary-hover-bg-color: #1940a1;
  --el-button-primary-active-bg-color: #143585;
}

/* 上传区域样式（不变） */
.upload-section {
  margin-bottom: 32px;
}
.upload-dragger {
  border: 2px dashed #d0e0ff !important;
  border-radius: 8px;
  padding: 48px 24px;
  margin-bottom: 24px;
  transition: all 0.3s;
}
.upload-dragger:hover {
  border-color: #1e50c8 !important;
  background-color: #f7faff;
}
.upload-dragger .el-icon--upload {
  color: #1e50c8 !important;
  font-size: 48px !important;
  margin-bottom: 16px;
}
.upload-dragger .el-upload__text {
  color: #6682b4 !important;
  font-size: 14px;
}
.upload-dragger .el-upload__text em {
  color: #1e50c8 !important;
  font-style: normal;
  font-weight: 500;
}
.el-upload__tip {
  color: #6682b4 !important;
  font-size: 12px;
  text-align: center;
}

/* 待上传文件列表样式（不变） */
.pending-files-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #d0e0ff;
}
.pending-files-section h3 {
  font-size: 16px;
  font-weight: 500;
  color: #1e50c8;
  margin: 0 0 16px 0;
}
.pending-file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 6px;
  background-color: #f7faff;
  transition: background-color 0.3s;
}
.pending-file-item:hover {
  background-color: #edf3ff;
}
.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.file-icon {
  color: #1e50c8 !important;
  font-size: 20px;
}
.original-name {
  color: #334e68;
  font-size: 14px;
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.filename-edit .el-input {
  --el-input-border-color: #d0e0ff;
  --el-input-focus-border-color: #1e50c8;
  --el-input-hover-border-color: #99b9ff;
}
.filename-edit .el-input__prepend,
.filename-edit .el-input__append {
  background-color: #f0f7ff;
  border-color: #d0e0ff;
  color: #1e50c8;
}
.file-actions .el-button {
  --el-button-danger-bg-color: #ff4d4f;
  --el-button-danger-border-color: #ff4d4f;
  --el-button-danger-hover-bg-color: #f5222d;
}

/* 上传操作按钮区域（不变） */
.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}
.upload-actions .el-button--primary {
  --el-button-primary-bg-color: #1e50c8;
  --el-button-primary-border-color: #1e50c8;
  --el-button-primary-hover-bg-color: #1940a1;
}
.upload-actions .el-button--default {
  --el-button-bg-color: #f0f7ff;
  --el-button-border-color: #d0e0ff;
  --el-button-text-color: #1e50c8;
  --el-button-hover-bg-color: #edf3ff;
  --el-button-hover-border-color: #99b9ff;
}

/* 上传进度条样式（不变） */
.upload-progress {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #d0e0ff;
}
.el-progress {
  --el-progress-bar-bg-color: #edf3ff;
  --el-progress-success-color: #1e50c8;
  height: 8px !important;
  margin-bottom: 8px;
}
.progress-text {
  font-size: 14px;
  color: #6682b4;
  margin: 0;
  text-align: right;
}

/* 表格样式 - 蓝白主题适配（不变） */
.resume-list-section .el-table {
  --el-table-bg-color: #ffffff;
  --el-table-header-text-color: #1e50c8;
  --el-table-header-bg-color: #f0f7ff;
  --el-table-header-border-color: #d0e0ff;
  --el-table-row-hover-bg-color: #f7faff;
  --el-table-border-color: #d0e0ff;
  --el-table-text-color: #334e68;
}
.el-table__empty-text {
  color: #6682b4 !important;
}
.action-buttons {
  display: flex;
  gap: 8px;
}
.action-buttons .el-button--primary {
  --el-button-primary-bg-color: #1e50c8;
  --el-button-primary-border-color: #1e50c8;
  --el-button-primary-hover-bg-color: #1940a1;
}
.action-buttons .el-button--success {
  --el-button-success-bg-color: #00b42a;
  --el-button-success-border-color: #00b42a;
  --el-button-success-hover-bg-color: #00a124;
}
.action-buttons .el-button--danger {
  --el-button-danger-bg-color: #ff4d4f;
  --el-button-danger-border-color: #ff4d4f;
  --el-button-danger-hover-bg-color: #f5222d;
}

/* 加载状态样式适配（不变） */
.el-loading-mask {
  --el-loading-background: rgba(255, 255, 255, 0.8);
}
.el-loading-spinner .path {
  stroke: #1e50c8 !important;
}

/* 响应式适配 - 小屏幕下优化 */
@media (max-width: 1200px) {
  .content-wrapper {
    width: 100%;
    padding: 0 16px;
  }
  .filename-edit .el-input {
    width: 220px !important; /* 小屏幕缩小输入框宽度 */
  }
}
@media (max-width: 768px) {
  .pending-file-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 16px;
  }
  .filename-edit .el-input {
    width: 100% !important; /* 超小屏幕输入框占满宽度 */
  }
  .file-actions {
    align-self: flex-end; /* 移除按钮靠右 */
  }
}
</style>


