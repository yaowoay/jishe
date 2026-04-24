<template>
  <div class="resume-match-test">
    <div class="container">
      <h1>简历匹配分析测试页面</h1>
      
      <div class="test-section">
        <h2>API连接测试</h2>
        <el-button @click="testConnection" :loading="testing">测试后端连接</el-button>
        <p v-if="connectionStatus" :class="connectionStatus.type">{{ connectionStatus.message }}</p>
      </div>

      <div class="test-section">
        <h2>简历匹配分析组件</h2>
        <ResumeAnalysis />
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import ResumeAnalysis from '@/components/resumes/ResumeAnalysis.vue'
import request from '@/utils/request'

export default {
  name: 'ResumeMatchTest',
  components: {
    ResumeAnalysis
  },
  setup() {
    const testing = ref(false)
    const connectionStatus = ref(null)

    const testConnection = async () => {
      testing.value = true
      connectionStatus.value = null

      try {
        // 测试后端连接
        const response = await request.get('/health') // 假设有健康检查接口
        connectionStatus.value = {
          type: 'success',
          message: '后端连接正常'
        }
        ElMessage.success('后端连接测试成功')
      } catch (error) {
        connectionStatus.value = {
          type: 'error',
          message: `后端连接失败: ${error.message}`
        }
        ElMessage.error('后端连接测试失败')
      } finally {
        testing.value = false
      }
    }

    return {
      testing,
      connectionStatus,
      testConnection
    }
  }
}
</script>

<style scoped>
.resume-match-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.test-section {
  margin-bottom: 40px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.test-section h2 {
  margin-top: 0;
  color: #303133;
}

.success {
  color: #67c23a;
  font-weight: bold;
}

.error {
  color: #f56c6c;
  font-weight: bold;
}
</style>
