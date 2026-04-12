<template>
  <div class="video-analysis-container">
    <!-- 视频上传区域 -->
    <div class="upload-section" v-if="!analysisResult">
      <el-card class="upload-card">
        <template #header>
          <div class="card-header">
            <span>面试视频分析</span>
            <el-tag type="info">支持 MP4, AVI, MOV, MKV 格式</el-tag>
          </div>
        </template>
        
        <el-upload
          ref="uploadRef"
          class="video-upload"
          drag
          :action="uploadUrl"
          :data="uploadData"
          :before-upload="beforeUpload"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          :on-progress="onUploadProgress"
          :show-file-list="false"
          accept="video/*"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将面试视频拖拽到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 MP4, AVI, MOV, MKV 格式，文件大小不超过 100MB
            </div>
          </template>
        </el-upload>
        
        <!-- 转录文本输入 -->
        <div class="transcript-section">
          <el-form-item label="转录文本（可选）">
            <el-input
              v-model="transcript"
              type="textarea"
              :rows="4"
              placeholder="如果有面试对话的文字转录，可以在此输入以提高分析准确性"
            />
          </el-form-item>
        </div>
        
        <!-- 上传进度 -->
        <div v-if="uploading" class="upload-progress">
          <el-progress :percentage="uploadProgress" :status="uploadStatus" />
          <p class="progress-text">{{ progressText }}</p>
        </div>
      </el-card>
    </div>
    
    <!-- 分析结果展示区域 -->
    <div class="analysis-section" v-if="analysisResult">
      <el-card class="result-card">
        <template #header>
          <div class="card-header">
            <span>视频分析结果</span>
            <div>
              <el-button type="primary" size="small" @click="resetAnalysis">重新分析</el-button>
              <el-button size="small" @click="closeAnalysis">关闭</el-button>
            </div>
          </div>
        </template>
        
        <!-- 综合评分 -->
        <div class="score-overview">
          <h3>综合评分</h3>
          <div class="score-grid">
            <div class="score-item">
              <div class="score-label">综合得分</div>
              <div class="score-value overall">{{ analysisResult.scores?.overall || 0 }}</div>
            </div>
            <div class="score-item">
              <div class="score-label">表情评分</div>
              <div class="score-value">{{ analysisResult.scores?.expression || 0 }}</div>
            </div>
            <div class="score-item">
              <div class="score-label">音频情感</div>
              <div class="score-value">{{ analysisResult.scores?.audio || 0 }}</div>
            </div>
            <div class="score-item">
              <div class="score-label">背景环境</div>
              <div class="score-value">{{ analysisResult.scores?.background || 0 }}</div>
            </div>
          </div>
        </div>
        
        <!-- AI反馈 -->
        <div class="ai-feedback" v-if="analysisResult.aiFeedback">
          <h3>AI 专业反馈</h3>
          <div class="feedback-content">
            {{ analysisResult.aiFeedback }}
          </div>
        </div>
        
        <!-- 音频分析详情 -->
        <div class="audio-analysis" v-if="analysisResult.audioAnalysis">
          <h3>音频情感分析</h3>
          <div class="analysis-content">
            {{ analysisResult.audioAnalysis.summary }}
          </div>
        </div>
        
        <!-- 视频分析详情 -->
        <div class="video-analysis" v-if="analysisResult.videoAnalysis">
          <h3>表情姿态分析</h3>
          <div class="analysis-content">
            {{ analysisResult.videoAnalysis.multiFrameSummary }}
          </div>
        </div>
        
        <!-- 转录文本 -->
        <div class="transcript-result" v-if="analysisResult.transcript">
          <h3>面试转录</h3>
          <div class="transcript-content">
            {{ analysisResult.transcript }}
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { videoAnalysisApi } from '@/api/interview'

// Props
const props = defineProps({
  interviewId: {
    type: Number,
    required: true
  }
})

// Emits
const emit = defineEmits(['close'])

// 响应式数据
const uploadRef = ref()
const transcript = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadStatus = ref('')
const progressText = ref('')
const analysisResult = ref(null)

// 计算属性
const uploadUrl = computed(() => '/api/video-analysis/analyze')
const uploadData = computed(() => ({
  interviewId: props.interviewId,
  transcript: transcript.value
}))

// 上传前验证
const beforeUpload = (file) => {
  // 验证文件类型
  const validTypes = ['video/mp4', 'video/avi', 'video/mov', 'video/quicktime', 'video/x-msvideo']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('仅支持 MP4, AVI, MOV, MKV 格式的视频文件')
    return false
  }
  
  // 验证文件大小（100MB）
  const maxSize = 100 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('视频文件大小不能超过 100MB')
    return false
  }
  
  uploading.value = true
  uploadProgress.value = 0
  uploadStatus.value = ''
  progressText.value = '正在上传视频文件...'
  
  return true
}

// 上传进度
const onUploadProgress = (event) => {
  uploadProgress.value = Math.round((event.loaded / event.total) * 100)
  
  if (uploadProgress.value === 100) {
    uploadStatus.value = 'success'
    progressText.value = '上传完成，正在分析视频...'
  }
}

// 上传成功
const onUploadSuccess = (response) => {
  uploading.value = false
  
  if (response.success) {
    analysisResult.value = response.data
    ElMessage.success('视频分析完成')
  } else {
    ElMessage.error('分析失败: ' + response.message)
  }
}

// 上传失败
const onUploadError = (error) => {
  uploading.value = false
  uploadStatus.value = 'exception'
  progressText.value = '上传失败'
  
  console.error('Upload error:', error)
  ElMessage.error('视频上传失败，请重试')
}

// 重新分析
const resetAnalysis = () => {
  ElMessageBox.confirm('确定要重新分析吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    analysisResult.value = null
    transcript.value = ''
    uploadProgress.value = 0
    uploadStatus.value = ''
    progressText.value = ''
  })
}

// 关闭分析
const closeAnalysis = () => {
  emit('close')
}

// 加载已有的分析结果
const loadExistingAnalysis = async () => {
  try {
    const response = await videoAnalysisApi.getAnalysisByInterviewId(props.interviewId)
    if (response.data && response.data.scores) {
      analysisResult.value = response.data
    }
  } catch (error) {
    console.error('加载分析结果失败:', error)
  }
}

// 组件挂载时加载已有结果
loadExistingAnalysis()
</script>

<style scoped>
.video-analysis-container {
  padding: 20px;
}

.upload-card, .result-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.video-upload {
  margin-bottom: 20px;
}

.transcript-section {
  margin-top: 20px;
}

.upload-progress {
  margin-top: 20px;
  text-align: center;
}

.progress-text {
  margin-top: 10px;
  color: #666;
}

.score-overview {
  margin-bottom: 30px;
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-top: 15px;
}

.score-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
}

.score-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.score-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.score-value.overall {
  color: #67c23a;
  font-size: 36px;
}

.ai-feedback, .audio-analysis, .video-analysis, .transcript-result {
  margin-bottom: 25px;
}

.ai-feedback h3, .audio-analysis h3, .video-analysis h3, .transcript-result h3 {
  color: #303133;
  margin-bottom: 15px;
  font-size: 16px;
}

.feedback-content, .analysis-content, .transcript-content {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  line-height: 1.6;
  color: #606266;
}

.transcript-content {
  max-height: 200px;
  overflow-y: auto;
}
</style>
