<template>
  <div class="debug-auth">
    <h1>认证调试页面</h1>
    
    <div class="section">
      <h2>本地存储状态</h2>
      <div class="info-grid">
        <div class="info-item">
          <strong>Token:</strong> {{ localToken || '未找到' }}
        </div>
        <div class="info-item">
          <strong>User Role:</strong> {{ localUserRole || '未找到' }}
        </div>
        <div class="info-item">
          <strong>User ID:</strong> {{ localUserId || '未找到' }}
        </div>
      </div>
    </div>
    
    <div class="section">
      <h2>Vuex状态</h2>
      <div class="info-grid">
        <div class="info-item">
          <strong>Is Authenticated:</strong> {{ $store.getters.isAuthenticated }}
        </div>
        <div class="info-item">
          <strong>Token:</strong> {{ $store.getters.token || '未找到' }}
        </div>
        <div class="info-item">
          <strong>User Role:</strong> {{ $store.getters.userRole || '未找到' }}
        </div>
      </div>
    </div>
    
    <div class="section">
      <h2>API测试</h2>
      <div class="test-buttons">
        <el-button @click="testPublicAPI" :loading="testing.public">
          测试公共API
        </el-button>
        <el-button @click="testAuthAPI" :loading="testing.auth">
          测试认证API
        </el-button>
        <el-button @click="testResumeAPI" :loading="testing.resume">
          测试简历API
        </el-button>
      </div>
      
      <div v-if="testResults.length > 0" class="test-results">
        <h3>测试结果</h3>
        <div v-for="(result, index) in testResults" :key="index" class="test-result">
          <div class="result-header">
            <span class="result-type" :class="result.success ? 'success' : 'error'">
              {{ result.type }}
            </span>
            <span class="result-status">{{ result.success ? '成功' : '失败' }}</span>
          </div>
          <div class="result-content">
            <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
          </div>
        </div>
      </div>
    </div>
    
    <div class="section">
      <h2>快速登录</h2>
      <div class="login-buttons">
        <el-button @click="quickLogin('applicant')" type="primary">
          登录为求职者
        </el-button>
        <el-button @click="quickLogin('company')" type="success">
          登录为企业
        </el-button>
        <el-button @click="logout" type="danger">
          退出登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'DebugAuth',
  setup() {
    const localToken = ref('')
    const localUserRole = ref('')
    const localUserId = ref('')
    const testResults = ref([])
    
    const testing = ref({
      public: false,
      auth: false,
      resume: false
    })
    
    const loadLocalStorage = () => {
      localToken.value = localStorage.getItem('token')
      localUserRole.value = localStorage.getItem('userRole')
      localUserId.value = localStorage.getItem('userId')
    }
    
    const testPublicAPI = async () => {
      testing.value.public = true
      try {
        const response = await axios.get('/api/public/health')
        testResults.value.unshift({
          type: '公共API测试',
          success: true,
          data: response.data
        })
      } catch (error) {
        testResults.value.unshift({
          type: '公共API测试',
          success: false,
          data: error.response?.data || error.message
        })
      } finally {
        testing.value.public = false
      }
    }
    
    const testAuthAPI = async () => {
      testing.value.auth = true
      try {
        const response = await axios.get('/api/auth-test/protected', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        testResults.value.unshift({
          type: '认证API测试',
          success: true,
          data: response.data
        })
      } catch (error) {
        testResults.value.unshift({
          type: '认证API测试',
          success: false,
          data: error.response?.data || error.message
        })
      } finally {
        testing.value.auth = false
      }
    }
    
    const testResumeAPI = async () => {
      testing.value.resume = true
      try {
        const response = await axios.get('/api/resume/list', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        })
        testResults.value.unshift({
          type: '简历API测试',
          success: true,
          data: response.data
        })
      } catch (error) {
        testResults.value.unshift({
          type: '简历API测试',
          success: false,
          data: error.response?.data || error.message
        })
      } finally {
        testing.value.resume = false
      }
    }
    
    const quickLogin = async (role) => {
      try {
        const credentials = role === 'applicant' 
          ? { username: 'testuser', password: 'password123' }
          : { username: 'testcompany', password: 'password123' }
        
        const response = await axios.post('/api/auth/login', credentials)
        
        if (response.data.success) {
          const { token, userId, email, role: userRole } = response.data.data
          
          // 保存到localStorage
          localStorage.setItem('token', token)
          localStorage.setItem('userRole', userRole)
          localStorage.setItem('userId', userId)
          
          // 更新Vuex
          this.$store.dispatch('login', {
            token,
            user: { userId, email, role: userRole }
          })
          
          loadLocalStorage()
          ElMessage.success('登录成功')
        } else {
          ElMessage.error('登录失败: ' + response.data.message)
        }
      } catch (error) {
        ElMessage.error('登录失败: ' + (error.response?.data?.message || error.message))
      }
    }
    
    const logout = () => {
      localStorage.clear()
      this.$store.dispatch('logout')
      loadLocalStorage()
      ElMessage.success('已退出登录')
    }
    
    onMounted(() => {
      loadLocalStorage()
    })
    
    return {
      localToken,
      localUserRole,
      localUserId,
      testResults,
      testing,
      testPublicAPI,
      testAuthAPI,
      testResumeAPI,
      quickLogin,
      logout
    }
  }
}
</script>

<style scoped>
.debug-auth {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 10px;
}

.info-item {
  padding: 10px;
  background: white;
  border-radius: 4px;
  word-break: break-all;
}

.test-buttons, .login-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.test-results {
  margin-top: 20px;
}

.test-result {
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.result-header {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.result-type {
  font-weight: bold;
}

.result-type.success {
  color: #67c23a;
}

.result-type.error {
  color: #f56c6c;
}

.result-content {
  padding: 10px;
  background: white;
}

.result-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: 12px;
}
</style>
