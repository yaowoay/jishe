<template>
  <div
    class="step-flex-card"
    :class="{ active: hovered || active }"
    @mouseenter="$emit('mouseenter')"
    @mouseleave="$emit('mouseleave')"
  >
    <div class="step-badge"><span>①</span></div>
    <template v-if="hovered || active">
      <div class="step-header"><el-icon class="step-icon"><Upload /></el-icon> 第一步：上传简历</div>
      <div class="upload-section">
        <el-upload
          class="upload-demo"
          drag
          :auto-upload="false"
          :show-file-list="true"
          :on-change="handleFileChange"
          :before-upload="beforeUpload"
          accept=".pdf,.doc,.docx"
        >
          <div class="upload-content">
            <el-icon class="upload-icon"><Upload /></el-icon>
            <div class="upload-text">将简历文件拖拽到此处，或点击上传</div>
            <div class="upload-tip">支持 PDF、DOC、DOCX 格式，文件大小不超过 10MB</div>
          </div>
        </el-upload>
        <div v-if="resumeFile" class="resume-info">
          <el-alert
            title="简历上传成功"
            type="success"
            :closable="false"
            show-icon
          >
            <template #default>
              <p>文件名：{{ resumeFile.name }}</p>
              <p>文件大小：{{ formatFileSize(resumeFile.size) }}</p>
            </template>
          </el-alert>
        </div>
      </div>
    </template>
    <template v-else>
      <div class="step-header mini"><el-icon class="step-icon"><Upload /></el-icon> 上传简历</div>
      <div v-if="resumeFile" class="mini-brief set">
        <el-icon class="mini-done"><i class="el-icon-check"></i></el-icon>
        <span>已上传：{{ resumeFile.name }}</span>
      </div>
      <div v-else class="mini-brief">上传个人简历</div>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'  
import { ElMessage } from 'element-plus'  
import { Upload } from '@element-plus/icons-vue'  

const props = defineProps({
  hovered: Boolean,
  active: Boolean,
  resumeFile: Object
})  

const emit = defineEmits(['mouseenter', 'mouseleave', 'file-change'])  

function handleFileChange(file) {
  if (file.raw) {
    emit('file-change', file.raw)  
  } else {
    emit('file-change', file)  
  }
}

function beforeUpload(file) {
  const validTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ]  

  let fileType = file.type  
  if (!fileType && file.name) {
    const extension = file.name.toLowerCase().split('.').pop()  
    switch (extension) {
    case 'pdf':
      fileType = 'application/pdf'
      break
    case 'doc':
      fileType = 'application/msword'
      break
    case 'docx':
      fileType = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      break
    }
  }

  const isValidType = validTypes.includes(fileType)  
  const isLt10M = file.size / 1024 / 1024 < 10  

  if (!isValidType) {
    ElMessage.error('只能上传 PDF、DOC、DOCX 格式的文件！')
    return false  
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB！')  
    return false  
  }

  return true  
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'  
  const k = 1024  
  const sizes = ['Bytes', 'KB', 'MB', 'GB']  
  const i = Math.floor(Math.log(bytes) / Math.log(k))  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]  
}
</script>

<style scoped>
.step-flex-card {
  background: rgba(255,255,255,0.92);
  border-radius: 32px;
  box-shadow: 0 12px 48px 0 rgba(80,120,200,0.13), 0 2px 16px #e6e6e6;
  transition: all 0.35s cubic-bezier(.4,2,.6,1);
  width: 420px;
  min-height: 340px;
  padding: 56px 38px 32px 38px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  backdrop-filter: blur(8px);
  border: 2px solid #e0e7ff;
}

.step-flex-card.active {
  z-index: 2;
  box-shadow: 0 24px 64px #b3c0d1, 0 4px 24px #409eff44;
  transform: scale(1.09) translateY(-8px);
  cursor: default;
  border: 3px solid #409eff;
  background: rgba(255,255,255,0.98);
}

.step-badge {
  position: absolute;
  top: 18px;
  left: 28px;
  background: linear-gradient(135deg, #e3f0ff 60%, #b3d8ff 100%);
  color: #409eff;
  font-size: 1.3rem;
  font-weight: 700;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px #e0e7ff;
  border: 2px solid #b3d8ff;
  z-index: 2;
}

.step-header {
  font-size: 26px;
  font-weight: 700;
  color: #222;
  margin-bottom: 18px;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.step-header.mini {
  font-size: 22px;
  margin-bottom: 12px;
}

.step-icon {
  font-size: 1.5em;
  color: #409eff;
}

.upload-section {
  padding: 20px 0;
}

.upload-content {
  text-align: center;
  padding: 40px 20px;
}

.upload-icon {
  font-size: 54px;
  color: #409eff;
  margin-bottom: 18px;
}

.upload-text {
  font-size: 18px;
  color: #303133;
  margin-bottom: 10px;
}

.upload-tip {
  font-size: 14px;
  color: #909399;
}

.resume-info {
  margin-top: 20px;
}

.mini-brief {
  font-size: 18px;
  color: #8a98b3;
  margin-top: 12px;
  font-weight: 500;
  text-align: left;
  letter-spacing: 1px;
}

.mini-brief.set {
  color: #67c23a;
  font-weight: 600;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.mini-done {
  color: #67c23a;
  font-size: 1.2em;
}
</style>
