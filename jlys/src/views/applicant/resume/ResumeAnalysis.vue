<template>
  <div class="resume-analysis-container">
    <!-- 页面标题 -->
    <div class="header-section">
      <h1 class="main-title">
        <i class="icon-document"></i>
        智能简历分析系统
      </h1>
      <p class="subtitle">上传简历文件，获得专业的匹配度分析和优化建议</p>
    </div>

    <!-- 选择分析方式 -->
    <div class="analysis-mode-section" v-if="!analysisResult">
      <div class="mode-selector">
        <div class="mode-tabs">
          <button
              class="mode-tab"
              :class="{ active: analysisMode === 'existing' }"
              @click="analysisMode = 'existing'"
          >
            选择已上传简历
          </button>
          <button
              class="mode-tab"
              :class="{ active: analysisMode === 'upload' }"
              @click="analysisMode = 'upload'"
          >
            上传新简历
          </button>
        </div>
      </div>
    </div>

    <!-- 选择已上传简历 -->
    <div class="existing-resume-section" v-if="!analysisResult && analysisMode === 'existing'">
      <div class="existing-resume-card">
        <div class="card-header">
          <h3>选择要分析的简历</h3>
          <button class="refresh-btn" @click="loadUserResumes" :disabled="loadingResumes">
            <span v-if="loadingResumes">🔄</span>
            <span v-else>🔄</span>
            刷新列表
          </button>
        </div>

        <div v-if="loadingResumes" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载简历列表中...</p>
        </div>

        <div v-else-if="userResumes.length === 0" class="empty-state">
          <div class="empty-icon">📄</div>
          <p>暂无已上传的简历</p>
          <button class="upload-first-btn" @click="analysisMode = 'upload'">
            立即上传简历
          </button>
        </div>

        <div v-else class="resume-list">
          <div
              v-for="resume in userResumes"
              :key="resume.resumeId"
              class="resume-item"
              :class="{ selected: selectedResumeId === resume.resumeId }"
              @click="selectedResumeId = resume.resumeId"
          >
            <div class="resume-info">
              <div class="resume-name">{{ resume.filename }}</div>
              <div class="resume-date">上传时间: {{ formatDate(resume.uploadDate) }}</div>
            </div>
            <div class="resume-actions">
              <span class="file-size">{{ getFileExtension(resume.filename) }}</span>
            </div>
          </div>
        </div>

        <!-- 分析按钮 -->
        <button
            class="analyze-btn"
            :disabled="!selectedResumeId || !userId || isUploading"
            @click="analyzeExistingResume"
        >
          <i class="icon-analyze">🔍</i>
          {{ isUploading ? '分析中...' : '开始分析' }}
        </button>
      </div>
    </div>

    <!-- 上传区域 -->
    <div class="upload-section" v-if="!analysisResult && analysisMode === 'upload'">
      <div class="upload-card">
        <div
            class="upload-area"
            :class="{ 'drag-over': isDragOver, 'uploading': isUploading }"
            @drop="handleDrop"
            @dragover.prevent="isDragOver = true"
            @dragleave="isDragOver = false"
            @click="triggerFileInput"
        >
          <input
              ref="fileInput"
              type="file"
              accept=".pdf,.doc,.docx,.txt"
              @change="handleFileSelect"
              style="display: none"
          />

          <div v-if="!isUploading" class="upload-content">
            <div class="upload-icon">📄</div>
            <h3>拖拽文件到此处或点击上传</h3>
            <p>支持 PDF、DOC、DOCX、TXT 格式，最大 50MB</p>
            <div v-if="selectedFile" class="selected-file">
              <span class="file-name">{{ selectedFile.name }}</span>
              <span class="file-size">({{ formatFileSize(selectedFile.size) }})</span>
            </div>
          </div>

          <div v-else class="uploading-content">
            <div class="loading-spinner"></div>
            <p>{{ uploadStatus }}</p>
          </div>
        </div>

        <!-- 上传按钮 -->
        <button
            class="upload-btn"
            :disabled="!selectedFile || !userId || isUploading"
            @click="uploadAndAnalyze"
        >
          <i class="icon-analyze">🔍</i>
          {{ isUploading ? '分析中...' : '开始分析' }}
        </button>
      </div>
    </div>

    <!-- 分析结果展示 -->
    <div v-if="analysisResult" class="analysis-result-container">
      <div class="result-header">
        <h2>
          <i class="icon-result">📊</i>
          简历分析报告
        </h2>
        <button class="new-analysis-btn" @click="resetAnalysis">
          <i class="icon-refresh">🔄</i>
          重新分析
        </button>
      </div>

      <!-- 使用新的分析报告组件 -->
      <ResumeAnalysisReport :analysis-data="analysisResult" />

    </div>

  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import ResumeAnalysisReport from '@/components/ResumeAnalysisReport.vue'

export default {
  name: 'ResumeAnalysisComponent',
  components: {
    ResumeAnalysisReport
  },
  setup() {
    // 响应式数据
    const analysisMode = ref('existing') // 'existing' 或 'upload'
    const selectedFile = ref(null)
    const userId = ref('user-123')
    const variableName = ref('Resume_analysis')
    const documentType = ref('document')
    const responseMode = ref('blocking')
    const isDragOver = ref(false)
    const isUploading = ref(false)
    const uploadStatus = ref('')
    const analysisResult = ref(null)
    const errorMessage = ref('')
    const fileInput = ref(null)

    // 已上传简历相关
    const userResumes = ref([])
    const selectedResumeId = ref(null)
    const loadingResumes = ref(false)

    // API配置
    const API_BASE_URL = 'http://localhost:8089/api/external-resume'

    // 解析后的数据
    const radarData = computed(() => {
      if (!analysisResult.value?.data?.analysis?.data?.outputs?.end_resume) return { jobMatch: 0, atsReadability: 0, keywords: [] }

      const content = analysisResult.value.data.analysis.data.outputs.end_resume

      // 解析岗位契合度
      const jobMatchMatch = content.match(/岗位契合度：(\d+)\/100/)
      const jobMatch = jobMatchMatch ? parseInt(jobMatchMatch[1]) : 0

      // 解析ATS可读性
      const atsMatch = content.match(/ATS\s*可读性：(\d+)\/100/)
      const atsReadability = atsMatch ? parseInt(atsMatch[1]) : 0

      // 解析关键词
      const keywordsMatch = content.match(/关键词命中：([^-]+?)(?=\n-|$)/s)
      const keywords = []
      if (keywordsMatch) {
        const keywordStr = keywordsMatch[1]
        const keywordMatches = keywordStr.match(/([^(,]+?)\((\d+)\)/g)
        if (keywordMatches) {
          keywordMatches.forEach(match => {
            const parts = match.match(/([^(]+?)\((\d+)\)/)
            if (parts) {
              keywords.push({
                name: parts[1].trim(),
                count: parseInt(parts[2])
              })
            }
          })
        }
      }

      return { jobMatch, atsReadability, keywords }
    })

    const highlights = computed(() => {
      if (!analysisResult.value?.data?.analysis?.data?.outputs?.end_resume) return []

      const content = analysisResult.value.data.analysis.data.outputs.end_resume
      const highlightSection = content.match(/## 亮点提炼\n(.*?)(?=\n##|$)/s)

      if (!highlightSection) return []

      const highlightLines = highlightSection[1].split('\n').filter(line => line.trim().startsWith('-'))

      return highlightLines.map(line => {
        const cleanLine = line.replace(/^- /, '').trim()
        return {
          icon: '✅',
          text: cleanLine
        }
      }).filter(item => item.text.trim())
    })

    const risks = computed(() => {
      if (!analysisResult.value?.data?.analysis?.data?.outputs?.end_resume) return []

      const content = analysisResult.value.data.analysis.data.outputs.end_resume
      const riskSection = content.match(/## 风险预警\n(.*?)(?=\n##|$)/s)

      if (!riskSection) return []

      const riskLines = riskSection[1].split('\n').filter(line => line.trim().startsWith('-'))

      return riskLines.map(line => {
        const cleanLine = line.replace(/^- /, '').trim()
        return {
          icon: '🚩',
          text: cleanLine
        }
      }).filter(item => item.text.trim())
    })

    const optimization = computed(() => {
      if (!analysisResult.value?.data?.analysis?.data?.outputs?.end_resume) return {}

      const content = analysisResult.value.data.analysis.data.outputs.end_resume
      const optimizationSection = content.match(/## 即刻优化\n(.*?)$/s)

      if (!optimizationSection) return {}

      // 解析模板示例
      const templateMatch = optimizationSection[1].match(/将「(.*?)」改为「(.*?)」/)

      // 解析推荐动词
      const wordsMatch = optimizationSection[1].match(/推荐动词：\s*\n([^\n]+)/)

      // 解析示例替换
      const exampleMatches = optimizationSection[1].match(/原句「([^」]+)」→「([^」]+)」/g)

      const result = {}

      if (templateMatch) {
        result.before = templateMatch[1]
        result.after = templateMatch[2]
      }

      if (wordsMatch) {
        result.words = wordsMatch[1].split(/\s*\/\s*/).map(word => word.trim()).filter(word => word)
      }

      if (exampleMatches && exampleMatches.length > 0) {
        result.examples = exampleMatches.map(example => {
          const match = example.match(/原句「([^」]+)」→「([^」]+)」/)
          return match ? {
            before: match[1],
            after: match[2]
          } : null
        }).filter(Boolean)
      }

      return result
    })

    const detailedAnalysis = computed(() => {
      // 这个功能暂时不需要，因为当前的 end_resume 内容已经包含了所有必要信息
      return null
    })

    // 方法
    const triggerFileInput = () => {
      fileInput.value?.click()
    }

    const handleFileSelect = (event) => {
      const file = event.target.files[0]
      if (file) {
        selectedFile.value = file
      }
    }

    const handleDrop = (event) => {
      event.preventDefault()
      isDragOver.value = false

      const files = event.dataTransfer.files
      if (files.length > 0) {
        selectedFile.value = files[0]
      }
    }

    const formatFileSize = (bytes) => {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }

    const uploadAndAnalyze = async () => {
      if (!selectedFile.value || !userId.value) {
        errorMessage.value = '请选择文件并输入用户标识'
        return
      }

      isUploading.value = true
      errorMessage.value = ''

      try {
        uploadStatus.value = '正在上传和分析简历...'

        const formData = new FormData()
        formData.append('file', selectedFile.value)
        formData.append('user', userId.value)
        formData.append('variable_name', variableName.value)
        formData.append('document_type', documentType.value)
        formData.append('response_mode', responseMode.value)

        const response = await fetch(`${API_BASE_URL}/upload-and-analyze`, {
          method: 'POST',
          body: formData
        })

        const result = await response.json()

        if (result.success) {
          analysisResult.value = result
          console.log('分析成功:', result)
        } else {
          throw new Error(result.message || '分析失败')
        }

      } catch (error) {
        console.error('分析失败:', error)
        errorMessage.value = error.message || '分析过程中发生错误'
      } finally {
        isUploading.value = false
        uploadStatus.value = ''
      }
    }

    const resetAnalysis = () => {
      selectedFile.value = null
      analysisResult.value = null
      errorMessage.value = ''
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const getKeywordClass = (count) => {
      if (count >= 7) return 'high'
      if (count >= 4) return 'medium'
      return 'low'
    }

    // 加载用户已上传的简历列表
    const loadUserResumes = async () => {
      loadingResumes.value = true
      errorMessage.value = ''

      try {
        const token = localStorage.getItem('token')
        if (!token) {
          throw new Error('请先登录')
        }

        const response = await fetch(`${API_BASE_URL}/resumes`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        const result = await response.json()

        if (result.success) {
          userResumes.value = result.data || []
          console.log('加载简历列表成功:', result.data)
        } else {
          throw new Error(result.message || '获取简历列表失败')
        }

      } catch (error) {
        console.error('加载简历列表失败:', error)
        errorMessage.value = error.message || '加载简历列表失败'
      } finally {
        loadingResumes.value = false
      }
    }

    // 分析已上传的简历
    const analyzeExistingResume = async () => {
      if (!selectedResumeId.value || !userId.value) {
        errorMessage.value = '请选择简历并输入用户标识'
        return
      }

      isUploading.value = true
      errorMessage.value = ''

      try {
        uploadStatus.value = '正在分析简历...'

        const formData = new FormData()
        formData.append('resume_id', selectedResumeId.value)
        formData.append('user', userId.value)
        formData.append('variable_name', variableName.value)
        formData.append('document_type', documentType.value)
        formData.append('response_mode', responseMode.value)

        console.log('调用分析接口:', `${API_BASE_URL}/analyze-existing`)
        console.log('请求参数:', {
          resume_id: selectedResumeId.value,
          user: userId.value,
          variable_name: variableName.value,
          document_type: documentType.value,
          response_mode: responseMode.value
        })

        // 不需要认证头，因为接口已设置为permitAll
        const response = await fetch(`${API_BASE_URL}/analyze-existing`, {
          method: 'POST',
          body: formData
        })

        console.log('响应状态:', response.status, response.statusText)

        if (!response.ok) {
          const errorText = await response.text()
          console.error('HTTP错误:', response.status, errorText)
          throw new Error(`HTTP ${response.status}: ${errorText}`)
        }

        const result = await response.json()
        console.log('API响应:', result)

        if (result.success) {
          analysisResult.value = result
          console.log('分析成功:', result)
        } else {
          throw new Error(result.message || '分析失败')
        }

      } catch (error) {
        console.error('分析失败:', error)
        errorMessage.value = error.message || '分析过程中发生错误'
      } finally {
        isUploading.value = false
        uploadStatus.value = ''
      }
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 获取文件扩展名
    const getFileExtension = (filename) => {
      if (!filename) return ''
      const ext = filename.split('.').pop()
      return ext ? ext.toUpperCase() : ''
    }

    // 组件挂载时加载简历列表
    onMounted(() => {
      if (analysisMode.value === 'existing') {
        loadUserResumes()
      }
    })

    return {
      // 响应式数据
      analysisMode,
      selectedFile,
      userId,
      variableName,
      documentType,
      responseMode,
      isDragOver,
      isUploading,
      uploadStatus,
      analysisResult,
      errorMessage,
      fileInput,
      userResumes,
      selectedResumeId,
      loadingResumes,

      // 计算属性
      radarData,
      highlights,
      risks,
      optimization,
      detailedAnalysis,

      // 方法
      triggerFileInput,
      handleFileSelect,
      handleDrop,
      formatFileSize,
      uploadAndAnalyze,
      resetAnalysis,
      getKeywordClass,
      loadUserResumes,
      analyzeExistingResume,
      formatDate,
      getFileExtension
    }
  }
}
</script>
<style scoped>
.resume-analysis-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f9ff 0%, #e0efff 100%);
  //min-height: 100vh;
}

/* 页面标题区域 */
.header-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 0;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.main-title {
  font-size: 36px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.main-title::before {
  content: "📄";
  font-size: 32px;
}

.subtitle {
  font-size: 18px;
  color: #5a84b3;
  margin: 0;
  font-weight: 500;
}

/* 分析方式选择 */
.analysis-mode-section {
  margin-bottom: 32px;
}

.mode-selector {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.mode-tabs {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.mode-tab {
  padding: 16px 32px;
  border: 2px solid #e6f1ff;
  background: white;
  border-radius: 12px;
  font-weight: 500;
  color: #5a84b3;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 16px;
}

.mode-tab:hover {
  border-color: #1a6fc4;
  color: #1a6fc4;
  transform: translateY(-2px);
}

.mode-tab.active {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border-color: #1a6fc4;
  color: white;
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
}

/* 已上传简历区域 */
.existing-resume-section {
  margin-bottom: 32px;
}

.existing-resume-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.refresh-btn {
  padding: 8px 16px;
  border: 1px solid #e6f1ff;
  background: #f0f7ff;
  color: #1a6fc4;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.refresh-btn:hover:not(:disabled) {
  background: #e0f0ff;
  transform: translateY(-1px);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-state {
  text-align: center;
  padding: 40px 0;
  color: #5a84b3;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e6f1ff;
  border-top: 3px solid #1a6fc4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #5a84b3;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.upload-first-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 16px;
}

.upload-first-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
}

.resume-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
  max-height: 300px;
  overflow-y: auto;
}

.resume-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 2px solid #e6f1ff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.resume-item:hover {
  border-color: #c2dcff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.1);
}

.resume-item.selected {
  border-color: #1a6fc4;
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.2);
}

.resume-info {
  flex: 1;
}

.resume-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.resume-date {
  font-size: 12px;
  color: #5a84b3;
}

.file-size {
  padding: 4px 8px;
  background: #e0f0ff;
  color: #1a6fc4;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.analyze-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.analyze-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(26, 111, 196, 0.4);
}

.analyze-btn:disabled {
  background: #c2dcff;
  cursor: not-allowed;
  transform: none;
}

/* 上传区域 */
.upload-section {
  margin-bottom: 32px;
}

.upload-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.upload-area {
  border: 2px dashed #c2dcff;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f0f7ff;
  margin-bottom: 24px;
}

.upload-area:hover {
  border-color: #1a6fc4;
  background: #e0f0ff;
}

.upload-area.drag-over {
  border-color: #1a6fc4;
  background: #e0f0ff;
  transform: scale(1.02);
}

.upload-area.uploading {
  border-color: #1a6fc4;
  background: #e0f0ff;
}

.upload-content {
  color: #5a84b3;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.upload-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.upload-content p {
  margin: 0 0 16px 0;
  font-size: 14px;
}

.selected-file {
  background: white;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.file-name {
  font-weight: 500;
  color: #2c3e50;
}

.uploading-content {
  color: #5a84b3;
}

.upload-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.upload-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(26, 111, 196, 0.4);
}

.upload-btn:disabled {
  background: #c2dcff;
  cursor: not-allowed;
  transform: none;
}

/* 分析结果区域 */
.analysis-result-container {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e6f1ff;
}

.result-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.new-analysis-btn {
  padding: 12px 24px;
  border: 1px solid #e6f1ff;
  background: #f0f7ff;
  color: #1a6fc4;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.new-analysis-btn:hover {
  background: #e0f0ff;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-analysis-container {
    padding: 16px;
  }

  .header-section {
    padding: 24px 16px;
  }

  .main-title {
    font-size: 28px;
    flex-direction: column;
    gap: 8px;
  }

  .subtitle {
    font-size: 16px;
  }

  .mode-tabs {
    flex-direction: column;
  }

  .mode-tab {
    padding: 12px 20px;
  }

  .existing-resume-card,
  .upload-card,
  .analysis-result-container {
    padding: 24px 16px;
  }

  .card-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .upload-area {
    padding: 24px 16px;
  }

  .result-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .new-analysis-btn {
    width: 100%;
    justify-content: center;
  }
}

/* 滚动条样式 */
.resume-list::-webkit-scrollbar {
  width: 6px;
}

.resume-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.resume-list::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border-radius: 3px;
}

.resume-list::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #0d4a89 0%, #2aa3b8 100%);
}
</style>