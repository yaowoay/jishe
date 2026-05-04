<template>
  <div class="resume-generator-test">
    <div class="test-container">
      <h2>简历生成测试</h2>
      <p class="description">测试AI简历生成功能，验证JSON格式处理</p>
      
      <div class="test-form">
        <div class="form-section">
          <h3>基本信息</h3>
          <div class="form-row">
            <div class="form-group">
              <label>姓名:</label>
              <input v-model="testData.name" type="text" placeholder="张三">
            </div>
            <div class="form-group">
              <label>期望职位:</label>
              <input v-model="testData.position" type="text" placeholder="前端开发工程师">
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>手机:</label>
              <input v-model="testData.phone" type="tel" placeholder="13800138000">
            </div>
            <div class="form-group">
              <label>邮箱:</label>
              <input v-model="testData.email" type="email" placeholder="zhangsan@example.com">
            </div>
          </div>
          
          <div class="form-group full-width">
            <label>工作经验:</label>
            <textarea v-model="testData.experience" placeholder="请描述您的工作经验" rows="3"></textarea>
          </div>
          
          <div class="form-group full-width">
            <label>技能特长:</label>
            <textarea v-model="testData.skills" placeholder="JavaScript, Vue.js, React, Node.js" rows="2"></textarea>
          </div>
          
          <div class="form-group full-width">
            <label>教育背景:</label>
            <textarea v-model="testData.education" placeholder="请描述您的教育背景" rows="2"></textarea>
          </div>
        </div>
        
        <div class="test-actions">
          <button @click="testGenerate" :disabled="generating" class="test-btn">
            <span v-if="generating">生成中...</span>
            <span v-else>测试生成</span>
          </button>
          <button @click="testWithMockData" class="mock-btn">使用模拟数据</button>
          <button @click="resetTest" class="reset-btn">重置</button>
        </div>
      </div>
      
      <!-- 生成状态 -->
      <div v-if="generating" class="generating-status">
        <div class="loading-spinner"></div>
        <p>正在生成简历...</p>
      </div>
      
      <!-- 生成结果 -->
      <div v-if="generatedContent" class="test-result">
        <h3>生成结果</h3>
        
        <!-- 原始数据 -->
        <div class="result-section">
          <h4>原始响应数据</h4>
          <div class="raw-data">
            <pre>{{ generatedContent }}</pre>
          </div>
        </div>
        
        <!-- 格式化显示 -->
        <div class="result-section">
          <h4>格式化显示</h4>
          <div class="formatted-display" v-html="formattedContent"></div>
        </div>
        
        <div class="result-actions">
          <button @click="downloadResult" class="download-btn">下载简历</button>
          <button @click="copyResult" class="copy-btn">复制内容</button>
        </div>
      </div>
      
      <!-- 错误信息 -->
      <div v-if="errorMessage" class="error-display">
        <h4>错误信息</h4>
        <p>{{ errorMessage }}</p>
        <button @click="errorMessage = ''" class="close-error">关闭</button>
      </div>
    </div>
  </div>
</template>

<script>
import { generateResume } from '@/api/resume'

export default {
  name: 'ResumeGeneratorTest',
  data() {
    return {
      generating: false,
      generatedContent: '',
      errorMessage: '',
      testData: {
        name: '',
        position: '',
        phone: '',
        email: '',
        experience: '',
        skills: '',
        education: ''
      }
    }
  },
  computed: {
    formattedContent() {
      if (!this.generatedContent) return ''
      return this.formatResumeForDisplay(this.generatedContent)
    }
  },
  methods: {
    async testGenerate() {
      if (!this.testData.name || !this.testData.position) {
        this.errorMessage = '请至少填写姓名和期望职位'
        return
      }
      
      this.generating = true
      this.errorMessage = ''
      this.generatedContent = ''
      
      try {
        const requestData = {
          prompt: '请生成一份专业的简历',
          personalInfo: `姓名：${this.testData.name}\n手机：${this.testData.phone}\n邮箱：${this.testData.email}`,
          workExperience: this.testData.experience,
          education: this.testData.education,
          skills: this.testData.skills,
          targetPosition: this.testData.position,
          templateType: 'standard',
          style: 'professional'
        }
        
        console.log('发送请求数据:', requestData)
        
        const response = await generateResume(requestData)
        console.log('收到响应:', response)
        
        if (response && response.data && response.data.success) {
          this.generatedContent = response.data.resumeContent
        } else {
          throw new Error(response?.data?.message || 'API响应格式异常')
        }
        
      } catch (error) {
        console.error('生成失败:', error)
        this.errorMessage = `生成失败: ${error.message}`
      } finally {
        this.generating = false
      }
    },
    
    testWithMockData() {
      this.testData = {
        name: '张三',
        position: '前端开发工程师',
        phone: '13800138000',
        email: 'zhangsan@example.com',
        experience: '3年前端开发经验，熟悉Vue.js、React等框架，有丰富的项目开发经验',
        skills: 'JavaScript, TypeScript, Vue.js, React, Node.js, HTML5, CSS3, Webpack',
        education: '计算机科学与技术本科，2020年毕业于某某大学'
      }
    },
    
    resetTest() {
      this.testData = {
        name: '',
        position: '',
        phone: '',
        email: '',
        experience: '',
        skills: '',
        education: ''
      }
      this.generatedContent = ''
      this.errorMessage = ''
    },
    
    downloadResult() {
      if (!this.generatedContent) return
      
      const blob = new Blob([this.generatedContent], { type: 'application/json;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${this.testData.name || '简历'}_${new Date().toISOString().slice(0, 10)}.json`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
    },
    
    async copyResult() {
      if (!this.generatedContent) return
      
      try {
        await navigator.clipboard.writeText(this.generatedContent)
        alert('内容已复制到剪贴板')
      } catch (error) {
        console.error('复制失败:', error)
        alert('复制失败，请手动复制')
      }
    },
    
    // 格式化简历内容用于显示（复用主组件的方法）
    formatResumeForDisplay(content) {
      try {
        let resumeData
        if (typeof content === 'string') {
          resumeData = JSON.parse(content)
        } else {
          resumeData = content
        }
        
        if (resumeData.format === 'text') {
          return `<div class="resume-content text-format">
            <div class="format-notice">
              <p><strong>注意：</strong> ${resumeData.message}</p>
            </div>
            <pre style="white-space: pre-wrap; font-family: Arial, sans-serif; line-height: 1.6;">${resumeData.content}</pre>
          </div>`
        }
        
        return this.formatStructuredResume(resumeData)
        
      } catch (e) {
        return `<pre style="white-space: pre-wrap; font-family: Arial, sans-serif; line-height: 1.6;">${content}</pre>`
      }
    },
    
    // 简化版的结构化简历格式化
    formatStructuredResume(resumeData) {
      let html = '<div class="resume-content structured-format">'
      
      // 个人信息
      if (resumeData.personalInfo) {
        html += '<div class="section"><h3>👤 个人信息</h3>'
        const info = resumeData.personalInfo
        if (info.name) html += `<p><strong>姓名：</strong>${info.name}</p>`
        if (info.phone) html += `<p><strong>电话：</strong>${info.phone}</p>`
        if (info.email) html += `<p><strong>邮箱：</strong>${info.email}</p>`
        if (info.summary) html += `<p><strong>简介：</strong>${info.summary}</p>`
        html += '</div>'
      }
      
      // 求职意向
      if (resumeData.objective) {
        html += `<div class="section"><h3>🎯 求职意向</h3><p>${resumeData.objective}</p></div>`
      }
      
      // 工作经验
      if (resumeData.workExperience && Array.isArray(resumeData.workExperience)) {
        html += '<div class="section"><h3>💼 工作经验</h3>'
        resumeData.workExperience.forEach(work => {
          html += '<div style="margin-bottom: 15px; padding: 10px; background: #f8f9fa; border-radius: 4px;">'
          if (work.company) html += `<p><strong>${work.company}</strong>`
          if (work.position) html += ` - ${work.position}`
          if (work.period) html += ` (${work.period})`
          html += '</p>'
          if (work.responsibilities) {
            html += '<p><strong>职责：</strong></p><ul>'
            work.responsibilities.forEach(resp => html += `<li>${resp}</li>`)
            html += '</ul>'
          }
          html += '</div>'
        })
        html += '</div>'
      }
      
      // 技能
      if (resumeData.skills) {
        html += '<div class="section"><h3>🛠️ 专业技能</h3>'
        const skills = resumeData.skills
        if (skills.technical) html += `<p><strong>技术技能：</strong>${skills.technical.join(', ')}</p>`
        if (skills.professional) html += `<p><strong>专业技能：</strong>${skills.professional.join(', ')}</p>`
        html += '</div>'
      }
      
      html += '</div>'
      return html
    }
  }
}
</script>

<style scoped>
.resume-generator-test {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.test-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.test-container h2 {
  text-align: center;
  margin-bottom: 10px;
  color: #333;
}

.description {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}

.form-section {
  margin-bottom: 25px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.form-section h3 {
  margin: 0 0 15px 0;
  color: #333;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-group label {
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

.form-group input,
.form-group textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.test-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.test-btn,
.mock-btn,
.reset-btn {
  padding: 12px 25px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

.test-btn {
  background: #007bff;
  color: white;
}

.test-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.mock-btn {
  background: #28a745;
  color: white;
}

.reset-btn {
  background: #6c757d;
  color: white;
}

.generating-status {
  text-align: center;
  padding: 40px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.test-result {
  margin-top: 30px;
}

.result-section {
  margin-bottom: 25px;
}

.result-section h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.raw-data {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  padding: 15px;
  max-height: 300px;
  overflow-y: auto;
}

.raw-data pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.formatted-display {
  border: 1px solid #e9ecef;
  border-radius: 4px;
  padding: 20px;
  background: white;
  max-height: 500px;
  overflow-y: auto;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.download-btn,
.copy-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.download-btn {
  background: #28a745;
  color: white;
}

.copy-btn {
  background: #17a2b8;
  color: white;
}

.error-display {
  margin-top: 20px;
  padding: 15px;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  color: #721c24;
}

.close-error {
  background: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .test-actions,
  .result-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
