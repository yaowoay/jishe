<template>
  <div class="resume-analyzer">
    <!-- 页面标题 -->
    <div class="header">
      <h1 class="title">
        <i class="icon-document"></i>
        智能简历分析
      </h1>
      <p class="subtitle">上传简历，获得专业的匹配度分析和优化建议</p>
    </div>

    <!-- 上传区域 -->
    <div class="upload-section" v-if="!analysisResult">
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
            <div class="upload-icon">
              <i class="icon-upload"></i>
            </div>
            <h3>拖拽文件到此处或点击上传</h3>
            <p>支持 PDF、DOC、DOCX、TXT 格式，最大 50MB</p>
          </div>
          
          <div v-else class="uploading-content">
            <div class="loading-spinner"></div>
            <p>{{ uploadStatus }}</p>
          </div>
        </div>

        <!-- 用户信息输入 -->
        <div class="user-info">
          <label for="userId">用户标识：</label>
          <input
            id="userId"
            v-model="userId"
            type="text"
            placeholder="请输入用户标识"
            class="user-input"
          />
        </div>

        <!-- 上传按钮 -->
        <button
          class="upload-btn"
          :disabled="!selectedFile || !userId || isUploading"
          @click="uploadAndAnalyze"
        >
          <i class="icon-analyze"></i>
          {{ isUploading ? '分析中...' : '开始分析' }}
        </button>
      </div>
    </div>

    <!-- 分析结果展示 -->
    <div class="analysis-result" v-if="analysisResult">
      <div class="result-header">
        <h2>
          <i class="icon-chart"></i>
          简历分析报告
        </h2>
        <button class="new-analysis-btn" @click="resetAnalysis">
          <i class="icon-refresh"></i>
          重新分析
        </button>
      </div>

      <!-- 匹配度雷达 -->
      <div class="radar-section">
        <h3><i class="icon-radar"></i>匹配度雷达</h3>
        <div class="radar-content">
          <div class="score-cards">
            <div class="score-card primary">
              <div class="score-value">{{ radarData.jobMatch }}/100</div>
              <div class="score-label">岗位契合度</div>
            </div>
            <div class="score-card secondary">
              <div class="score-value">{{ radarData.atsReadability }}/100</div>
              <div class="score-label">ATS可读性</div>
            </div>
          </div>
          
          <div class="keywords-section">
            <h4>关键词命中</h4>
            <div class="keywords-list">
              <span 
                v-for="keyword in radarData.keywords" 
                :key="keyword.name"
                class="keyword-tag"
                :class="getKeywordClass(keyword.count)"
              >
                {{ keyword.name }}({{ keyword.count }})
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 亮点提炼 -->
      <div class="highlights-section">
        <h3><i class="icon-star"></i>亮点提炼</h3>
        <div class="highlights-list">
          <div 
            v-for="(highlight, index) in highlights" 
            :key="index"
            class="highlight-item"
          >
            <span class="highlight-icon">{{ highlight.icon }}</span>
            <span class="highlight-text">{{ highlight.text }}</span>
          </div>
        </div>
      </div>

      <!-- 风险预警 -->
      <div class="risks-section">
        <h3><i class="icon-warning"></i>风险预警</h3>
        <div class="risks-content">
          <div class="risk-category">
            <h4>缺失信息</h4>
            <p>{{ risks.missing }}</p>
          </div>
          <div class="risk-category">
            <h4>易扣分项</h4>
            <p>{{ risks.deductions }}</p>
          </div>
          <div class="risk-category">
            <h4>隐性雷区</h4>
            <p>{{ risks.hidden }}</p>
          </div>
        </div>
      </div>

      <!-- 即刻优化 -->
      <div class="optimization-section">
        <h3><i class="icon-edit"></i>即刻优化</h3>
        <div class="optimization-example">
          <div class="example-before">
            <h4>原句：</h4>
            <p>{{ optimization.before }}</p>
          </div>
          <div class="example-after">
            <h4>✅ 改为：</h4>
            <p>{{ optimization.after }}</p>
          </div>
        </div>
      </div>

      <!-- 推荐动词替换 -->
      <div class="vocabulary-section">
        <h3><i class="icon-book"></i>推荐动词替换</h3>
        <div class="vocabulary-table">
          <table>
            <thead>
              <tr>
                <th>原文词汇</th>
                <th>升级方案</th>
                <th>场景示例</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in vocabularyReplacements" :key="index">
                <td class="original-word">{{ item.original }}</td>
                <td class="upgraded-word">{{ item.upgraded }}</td>
                <td class="example">{{ item.example }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">
      <i class="icon-error"></i>
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'

export default {
  name: 'ResumeAnalyzer',
  setup() {
    // 响应式数据
    const selectedFile = ref(null)
    const userId = ref('test-user-123')
    const isDragOver = ref(false)
    const isUploading = ref(false)
    const uploadStatus = ref('')
    const analysisResult = ref(null)
    const errorMessage = ref('')
    const fileInput = ref(null)

    // API配置
    const API_BASE_URL = 'http://81.70.20.30/v1'
    const API_KEY = 'app-iXQ9Euj9CEboby4yWxMBIYq8' // 请替换为实际的API密钥

    // 解析后的数据
    const radarData = computed(() => {
      if (!analysisResult.value) return {}
      
      const content = analysisResult.value.end_resume || ''
      
      // 解析岗位契合度
      const jobMatchMatch = content.match(/岗位契合度.*?(\d+)\/100/)
      const jobMatch = jobMatchMatch ? parseInt(jobMatchMatch[1]) : 0
      
      // 解析ATS可读性
      const atsMatch = content.match(/ATS可读性.*?(\d+)\/100/)
      const atsReadability = atsMatch ? parseInt(atsMatch[1]) : 0
      
      // 解析关键词
      const keywordsMatch = content.match(/关键词命中.*?：(.*?)(?=\n|$)/)
      const keywords = []
      if (keywordsMatch) {
        const keywordStr = keywordsMatch[1]
        const keywordMatches = keywordStr.match(/([^(]+)\((\d+)\)/g)
        if (keywordMatches) {
          keywordMatches.forEach(match => {
            const parts = match.match(/([^(]+)\((\d+)\)/)
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
      if (!analysisResult.value) return []
      
      const content = analysisResult.value.end_resume || ''
      const highlightSection = content.match(/## 亮点提炼\n(.*?)(?=\n---|\n##|$)/s)
      
      if (!highlightSection) return []
      
      const highlightLines = highlightSection[1].split('\n').filter(line => line.trim().startsWith('-'))
      
      return highlightLines.map(line => {
        const match = line.match(/- ([💡🚀📊]) (.+)/)
        return match ? {
          icon: match[1],
          text: match[2]
        } : null
      }).filter(Boolean)
    })

    const risks = computed(() => {
      if (!analysisResult.value) return {}
      
      const content = analysisResult.value.end_resume || ''
      const riskSection = content.match(/## 风险预警\n(.*?)(?=\n---|\n##|$)/s)
      
      if (!riskSection) return {}
      
      const riskContent = riskSection[1]
      
      const missingMatch = riskContent.match(/缺失信息.*?：(.*?)(?=\n|$)/)
      const deductionsMatch = riskContent.match(/易扣分项.*?：(.*?)(?=\n|$)/)
      const hiddenMatch = riskContent.match(/隐性雷区.*?：(.*?)(?=\n|$)/)
      
      return {
        missing: missingMatch ? missingMatch[1].trim() : '',
        deductions: deductionsMatch ? deductionsMatch[1].trim() : '',
        hidden: hiddenMatch ? hiddenMatch[1].trim() : ''
      }
    })

    const optimization = computed(() => {
      if (!analysisResult.value) return {}
      
      const content = analysisResult.value.end_resume || ''
      const optimizationSection = content.match(/## 即刻优化\n(.*?)(?=\n---|\n##|$)/s)
      
      if (!optimizationSection) return {}
      
      const beforeMatch = optimizationSection[1].match(/原句：「(.*?)」/)
      const afterMatch = optimizationSection[1].match(/✅ 改为：「(.*?)」/)
      
      return {
        before: beforeMatch ? beforeMatch[1] : '',
        after: afterMatch ? afterMatch[1] : ''
      }
    })

    const vocabularyReplacements = computed(() => {
      if (!analysisResult.value) return []
      
      const content = analysisResult.value.end_resume || ''
      const vocabSection = content.match(/## 推荐动词替换\n(.*?)(?=\n##|$)/s)
      
      if (!vocabSection) return []
      
      const tableRows = vocabSection[1].split('\n').filter(line => line.includes('|') && !line.includes('---'))
      
      return tableRows.slice(1).map(row => {
        const cells = row.split('|').map(cell => cell.trim()).filter(cell => cell)
        return cells.length >= 3 ? {
          original: cells[0],
          upgraded: cells[1],
          example: cells[2]
        } : null
      }).filter(Boolean)
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

    const uploadAndAnalyze = async () => {
      if (!selectedFile.value || !userId.value) {
        errorMessage.value = '请选择文件并输入用户标识'
        return
      }

      isUploading.value = true
      errorMessage.value = ''
      
      try {
        // 步骤1: 上传文件
        uploadStatus.value = '正在上传文件...'
        const uploadResponse = await uploadFile()
        
        if (!uploadResponse.success) {
          throw new Error(uploadResponse.message || '文件上传失败')
        }

        // 步骤2: 执行工作流
        uploadStatus.value = '正在分析简历...'
        const workflowResponse = await runWorkflow(uploadResponse.id)
        
        if (!workflowResponse.success) {
          throw new Error(workflowResponse.message || '简历分析失败')
        }

        // 设置分析结果
        analysisResult.value = workflowResponse.data
        
      } catch (error) {
        console.error('分析失败:', error)
        errorMessage.value = error.message || '分析过程中发生错误'
      } finally {
        isUploading.value = false
        uploadStatus.value = ''
      }
    }

    const uploadFile = async () => {
      const formData = new FormData()
      formData.append('file', selectedFile.value)
      formData.append('user', userId.value)

      const response = await fetch(`${API_BASE_URL}/files/upload`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${API_KEY}`
        },
        body: formData
      })

      return await response.json()
    }

    const runWorkflow = async (fileId) => {
      const payload = {
        inputs: {
          resume_files: [{
            transfer_method: 'local_file',
            upload_file_id: fileId,
            type: 'document'
          }]
        },
        response_mode: 'streaming',
        user: userId.value
      }

      const response = await fetch(`${API_BASE_URL}/workflows/run`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${API_KEY}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      return await response.json()
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

    return {
      // 响应式数据
      selectedFile,
      userId,
      isDragOver,
      isUploading,
      uploadStatus,
      analysisResult,
      errorMessage,
      fileInput,
      
      // 计算属性
      radarData,
      highlights,
      risks,
      optimization,
      vocabularyReplacements,
      
      // 方法
      triggerFileInput,
      handleFileSelect,
      handleDrop,
      uploadAndAnalyze,
      resetAnalysis,
      getKeywordClass
    }
  }
}
</script>

<style scoped>
.resume-analyzer {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 页面标题 */
.header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.subtitle {
  font-size: 1.1rem;
  color: #7f8c8d;
  margin: 0;
}

/* 上传区域 */
.upload-section {
  margin-bottom: 40px;
}

.upload-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
  border: 1px solid #e1e8ed;
}

.upload-area {
  border: 2px dashed #cbd5e0;
  border-radius: 8px;
  padding: 60px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.upload-area:hover {
  border-color: #4299e1;
  background-color: #f7fafc;
}

.upload-area.drag-over {
  border-color: #3182ce;
  background-color: #ebf8ff;
}

.upload-area.uploading {
  border-color: #38a169;
  background-color: #f0fff4;
}

.upload-content h3 {
  margin: 20px 0 10px;
  color: #2d3748;
  font-size: 1.3rem;
}

.upload-content p {
  color: #718096;
  margin: 0;
}

.upload-icon {
  font-size: 3rem;
  color: #4299e1;
  margin-bottom: 10px;
}

.uploading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #4299e1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.user-info {
  margin-bottom: 20px;
}

.user-info label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2d3748;
}

.user-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #cbd5e0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.user-input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.upload-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.upload-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.upload-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 分析结果 */
.analysis-result {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.result-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 25px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-header h2 {
  margin: 0;
  font-size: 1.8rem;
  display: flex;
  align-items: center;
  gap: 12px;
}

.new-analysis-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.new-analysis-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 各个分析部分 */
.radar-section,
.highlights-section,
.risks-section,
.optimization-section,
.vocabulary-section {
  padding: 30px;
  border-bottom: 1px solid #e2e8f0;
}

.radar-section:last-child,
.highlights-section:last-child,
.risks-section:last-child,
.optimization-section:last-child,
.vocabulary-section:last-child {
  border-bottom: none;
}

.radar-section h3,
.highlights-section h3,
.risks-section h3,
.optimization-section h3,
.vocabulary-section h3 {
  color: #2d3748;
  font-size: 1.5rem;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 匹配度雷达 */
.radar-content {
  display: grid;
  gap: 25px;
}

.score-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.score-card {
  text-align: center;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.score-card.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.score-card.secondary {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.score-value {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 8px;
}

.score-label {
  font-size: 1.1rem;
  opacity: 0.9;
}

.keywords-section h4 {
  color: #4a5568;
  margin-bottom: 15px;
}

.keywords-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.keyword-tag {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.keyword-tag.high {
  background: #c6f6d5;
  color: #22543d;
}

.keyword-tag.medium {
  background: #fef5e7;
  color: #c05621;
}

.keyword-tag.low {
  background: #fed7d7;
  color: #c53030;
}

/* 亮点提炼 */
.highlights-list {
  display: grid;
  gap: 15px;
}

.highlight-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 20px;
  background: #f7fafc;
  border-radius: 8px;
  border-left: 4px solid #4299e1;
}

.highlight-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.highlight-text {
  color: #2d3748;
  line-height: 1.6;
}

/* 风险预警 */
.risks-content {
  display: grid;
  gap: 20px;
}

.risk-category {
  padding: 20px;
  background: #fff5f5;
  border-radius: 8px;
  border-left: 4px solid #f56565;
}

.risk-category h4 {
  color: #c53030;
  margin-bottom: 10px;
  font-size: 1.1rem;
}

.risk-category p {
  color: #2d3748;
  margin: 0;
  line-height: 1.6;
}

/* 即刻优化 */
.optimization-example {
  display: grid;
  gap: 20px;
}

.example-before,
.example-after {
  padding: 20px;
  border-radius: 8px;
}

.example-before {
  background: #fff5f5;
  border-left: 4px solid #f56565;
}

.example-after {
  background: #f0fff4;
  border-left: 4px solid #38a169;
}

.example-before h4,
.example-after h4 {
  margin-bottom: 10px;
  font-size: 1rem;
}

.example-before h4 {
  color: #c53030;
}

.example-after h4 {
  color: #38a169;
}

.example-before p,
.example-after p {
  margin: 0;
  line-height: 1.6;
  color: #2d3748;
}

/* 推荐动词替换 */
.vocabulary-table {
  overflow-x: auto;
}

.vocabulary-table table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.vocabulary-table th {
  background: #4a5568;
  color: white;
  padding: 15px;
  text-align: left;
  font-weight: 600;
}

.vocabulary-table td {
  padding: 15px;
  border-bottom: 1px solid #e2e8f0;
  vertical-align: top;
}

.vocabulary-table tr:last-child td {
  border-bottom: none;
}

.vocabulary-table tr:nth-child(even) {
  background: #f7fafc;
}

.original-word {
  color: #e53e3e;
  font-weight: 500;
}

.upgraded-word {
  color: #38a169;
  font-weight: 500;
}

.example {
  color: #4a5568;
  font-style: italic;
}

/* 错误提示 */
.error-message {
  background: #fed7d7;
  color: #c53030;
  padding: 15px 20px;
  border-radius: 8px;
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #feb2b2;
}

/* 图标样式 */
.icon-document::before { content: "📄"; }
.icon-upload::before { content: "⬆️"; }
.icon-analyze::before { content: "🔍"; }
.icon-chart::before { content: "📊"; }
.icon-refresh::before { content: "🔄"; }
.icon-radar::before { content: "🎯"; }
.icon-star::before { content: "⭐"; }
.icon-warning::before { content: "⚠️"; }
.icon-edit::before { content: "✏️"; }
.icon-book::before { content: "📚"; }
.icon-error::before { content: "❌"; }

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-analyzer {
    padding: 15px;
  }

  .title {
    font-size: 2rem;
  }

  .upload-card,
  .radar-section,
  .highlights-section,
  .risks-section,
  .optimization-section,
  .vocabulary-section {
    padding: 20px;
  }

  .score-cards {
    grid-template-columns: 1fr;
  }

  .result-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .vocabulary-table {
    font-size: 0.9rem;
  }
}
</style>
