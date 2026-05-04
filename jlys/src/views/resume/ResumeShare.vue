<template>
  <div class="resume-share">
    <div class="share-header">
      <h1>简历分享</h1>
      <div class="share-actions">
        <el-button @click="downloadPdf">下载PDF</el-button>
        <el-button type="primary" @click="copyLink">复制链接</el-button>
      </div>
    </div>

    <div class="share-content" v-if="resume">
      <div class="resume-preview">
        <!-- 这里可以放置简历预览组件 -->
        <ResumePreview :resumeData="resume" />
      </div>
    </div>

    <div class="loading-state" v-else>
      <el-loading text="加载中..." />
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getResumeByShareUrl } from '@/api/resume'
import ResumePreview from '@/components/resumes/ResumePreview.vue'

export default {
  name: 'ResumeShare',
  components: {
    ResumePreview
  },
  setup() {
    const route = useRoute()
    const resume = ref(null)
    const loading = ref(false)

    const loadResume = async () => {
      const shareUrl = route.params.shareUrl
      if (!shareUrl) {
        ElMessage.error('分享链接无效')
        return
      }

      loading.value = true
      try {
        resume.value = await getResumeByShareUrl(shareUrl)
      } catch (error) {
        ElMessage.error('加载简历失败')
        console.error('Load shared resume error:', error)
      } finally {
        loading.value = false
      }
    }

    const downloadPdf = () => {
      ElMessage.info('PDF下载功能开发中...')
    }

    const copyLink = async () => {
      try {
        await navigator.clipboard.writeText(window.location.href)
        ElMessage.success('链接已复制到剪贴板')
      } catch (error) {
        ElMessage.error('复制失败')
      }
    }

    onMounted(() => {
      loadResume()
    })

    return {
      resume,
      loading,
      downloadPdf,
      copyLink
    }
  }
}
</script>

<style scoped>
/* 迁移自frontend，将scss语法改为css - 简历分享页面样式 */
.resume-share {
  width: 100%;
  background: #f5f5f5;
  box-sizing: border-box;
  padding: 0; /* 精简为0，避免顶部留白 */
}

.share-header {
  background: white;
  padding: 16px 24px; /* 统一为16px 24px，避免多余间距 */
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
}

.share-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.share-actions {
  display: flex;
  gap: 12px;
}

.share-content {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 16px; /* 统一为16px，避免多余间距 */
  box-sizing: border-box;
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.share-content, .share-header {
  margin-top: 0 !important;
  padding-top: 0 !important;
  margin-bottom: 0 !important;
}

@media (max-width: 1024px) {
  .share-content {
    max-width: 100%;
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .share-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 16px;
  }

  .share-header h1 {
    text-align: center;
  }

  .share-content {
    padding: 12px;
  }
}
</style>
