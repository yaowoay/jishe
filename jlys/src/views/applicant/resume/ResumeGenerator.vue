<template>
  <div class="resume-generator">
    <div class="generator-container">
      <!-- 标题区域包含返回按钮 -->
      <div class="header-section">
        <button @click="goBack" class="back-button">
          <span class="back-icon">←</span>
          返回
        </button>
        <div class="title-area">
          <h2>AI简历生成器</h2>
          <p class="description">填写您的信息，AI将为您生成专业的简历</p>
        </div>
      </div>
      
      <!-- 步骤指示器 -->
      <div class="steps">
        <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
          <div class="step-number">1</div>
          <div class="step-title">基本信息</div>
        </div>
        <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
          <div class="step-number">2</div>
          <div class="step-title">详细信息</div>
        </div>
        <div class="step" :class="{ active: currentStep >= 3, completed: currentStep > 3 }">
          <div class="step-number">3</div>
          <div class="step-title">生成设置</div>
        </div>
        <div class="step" :class="{ active: currentStep >= 4 }">
          <div class="step-number">4</div>
          <div class="step-title">生成简历</div>
        </div>
      </div>
      
      <!-- 步骤1: 基本信息 -->
      <div v-if="currentStep === 1" class="step-content">
        <h3>基本信息</h3>
        <div class="form-grid">
          <div class="form-group">
            <label>姓名 *</label>
            <input v-model="formData.personalInfo.name" type="text" placeholder="请输入您的姓名" required>
          </div>
          <div class="form-group">
            <label>性别</label>
            <select v-model="formData.personalInfo.gender">
              <option value="">请选择</option>
              <option value="男">男</option>
              <option value="女">女</option>
            </select>
          </div>
          <div class="form-group">
            <label>年龄</label>
            <input v-model="formData.personalInfo.age" type="number" placeholder="请输入年龄">
          </div>
          <div class="form-group">
            <label>手机号码 *</label>
            <input v-model="formData.personalInfo.phone" type="tel" placeholder="请输入手机号码" required>
          </div>
          <div class="form-group">
            <label>邮箱 *</label>
            <input v-model="formData.personalInfo.email" type="email" placeholder="请输入邮箱地址" required>
          </div>
          <div class="form-group">
            <label>居住地址</label>
            <input v-model="formData.personalInfo.address" type="text" placeholder="请输入居住地址">
          </div>
        </div>
        
        <div class="form-group full-width">
          <label>期望职位 *</label>
          <input v-model="formData.targetPosition" type="text" placeholder="请输入期望职位" required>
        </div>
        
        <div class="form-group full-width">
          <label>个人简介</label>
          <textarea v-model="formData.personalInfo.summary" placeholder="请简要介绍您的背景和优势" rows="4"></textarea>
        </div>
        
        <div class="step-actions">
          <button @click="nextStep" :disabled="!canProceedStep1" class="next-btn">下一步</button>
        </div>
      </div>
      
      <!-- 步骤2: 详细信息 -->
      <div v-if="currentStep === 2" class="step-content">
        <h3>详细信息</h3>
        
        <!-- 教育背景 -->
        <div class="section">
          <h4>教育背景</h4>
          <div v-for="(edu, index) in formData.education" :key="index" class="education-item">
            <div class="form-grid">
              <div class="form-group">
                <label>学校名称</label>
                <input v-model="edu.school" type="text" placeholder="请输入学校名称">
              </div>
              <div class="form-group">
                <label>专业</label>
                <input v-model="edu.major" type="text" placeholder="请输入专业">
              </div>
              <div class="form-group">
                <label>学历</label>
                <select v-model="edu.degree">
                  <option value="">请选择</option>
                  <option value="高中">高中</option>
                  <option value="大专">大专</option>
                  <option value="本科">本科</option>
                  <option value="硕士">硕士</option>
                  <option value="博士">博士</option>
                </select>
              </div>
              <div class="form-group">
                <label>毕业时间</label>
                <input v-model="edu.graduationDate" type="month" placeholder="请选择毕业时间">
              </div>
            </div>
            <button @click="removeEducation(index)" class="remove-btn" v-if="formData.education.length > 1">删除</button>
          </div>
          <button @click="addEducation" class="add-btn">+ 添加教育经历</button>
        </div>
        
        <!-- 工作经验 -->
        <div class="section">
          <h4>工作经验</h4>
          <div v-for="(work, index) in formData.workExperience" :key="index" class="work-item">
            <div class="form-grid">
              <div class="form-group">
                <label>公司名称</label>
                <input v-model="work.company" type="text" placeholder="请输入公司名称">
              </div>
              <div class="form-group">
                <label>职位</label>
                <input v-model="work.position" type="text" placeholder="请输入职位">
              </div>
              <div class="form-group">
                <label>开始时间</label>
                <input v-model="work.startDate" type="month" placeholder="请选择开始时间">
              </div>
              <div class="form-group">
                <label>结束时间</label>
                <input v-model="work.endDate" type="month" placeholder="请选择结束时间">
              </div>
            </div>
            <div class="form-group full-width">
              <label>工作描述</label>
              <textarea v-model="work.description" placeholder="请描述您的工作内容和成就" rows="3"></textarea>
            </div>
            <button @click="removeWork(index)" class="remove-btn" v-if="formData.workExperience.length > 1">删除</button>
          </div>
          <button @click="addWork" class="add-btn">+ 添加工作经历</button>
        </div>
        
        <div class="step-actions">
          <button @click="prevStep" class="prev-btn">上一步</button>
          <button @click="nextStep" class="next-btn">下一步</button>
        </div>
      </div>
      
      <!-- 步骤3: 生成设置 -->
      <!-- 步骤3: 生成设置 -->
      <div v-if="currentStep === 3" class="step-content">
        <h3>生成设置</h3>

        <div class="form-grid">
          <div class="form-group">
            <label>简历模板</label>
            <select v-model="generationSettings.templateType">
              <option v-for="(template, key) in templates" :key="key" :value="key">
                {{ template }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>简历风格</label>
            <select v-model="generationSettings.style">
              <option v-for="(style, key) in styles" :key="key" :value="key">
                {{ style }}
              </option>
            </select>
          </div>
        </div>

        <div class="step-actions">
          <button @click="prevStep" class="prev-btn">上一步</button>
          <button @click="nextStep" class="next-btn">下一步</button>
        </div>
      </div>
      <!-- 步骤4: 生成简历 -->
      <div v-if="currentStep === 4" class="step-content">
        <h3>生成简历</h3>
        
        <div v-if="!generating && !generated" class="generation-start">
          <div class="summary">
            <h4>信息确认</h4>
            <div class="summary-item">
              <strong>姓名:</strong> {{ formData.personalInfo.name }}
            </div>
            <div class="summary-item">
              <strong>期望职位:</strong> {{ formData.targetPosition }}
            </div>
<!--            <div class="summary-item">-->
<!--              <strong>简历模板:</strong> {{ templates[generationSettings.templateType] }}-->
<!--            </div>-->
<!--            <div class="summary-item">-->
<!--              <strong>简历风格:</strong> {{ styles[generationSettings.style] }}-->
<!--            </div>-->
          </div>
          <div class="generate-buttons">
            <button @click="generateResume" class="generate-btn direct">直接生成</button>
            <button @click="showTemplateDialog" class="generate-btn template">按模板生成</button>
          </div>
        </div>
        
        <div v-if="generating" class="generation-progress">
          <div class="loading-spinner"></div>
          <p>AI正在为您生成简历，请稍候...</p>
          <div class="progress-text">{{ progressText }}</div>
        </div>
        
        <div v-if="generated" class="generation-result">
          <div class="success-message">
            <div class="success-icon">✓</div>
            <h4>简历生成成功！</h4>
          </div>

<!--          &lt;!&ndash; 显示链接信息 &ndash;&gt;-->
<!--          <div v-if="resumeLinks.format === 'links'" class="links-info">-->
<!--            <p class="links-description">您的简历已生成完成，可通过以下方式查看和下载：</p>-->
<!--            <div class="links-grid">-->
<!--              <div v-if="resumeLinks.imgUrl" class="link-item">-->
<!--                <div class="link-icon">🖼️</div>-->
<!--                <div class="link-info">-->
<!--                  <h5>图片预览</h5>-->
<!--                  <p>查看简历图片版本</p>-->
<!--                </div>-->
<!--              </div>-->
<!--              <div v-if="resumeLinks.wordUrl" class="link-item">-->
<!--                <div class="link-icon">📄</div>-->
<!--                <div class="link-info">-->
<!--                  <h5>Word文档</h5>-->
<!--                  <p>下载可编辑的Word版本</p>-->
<!--                </div>-->
<!--              </div>-->
<!--            </div>-->
<!--          </div>-->

          <div class="result-actions">
            <button @click="previewResume" class="preview-btn">
              {{ resumeLinks.format === 'links' ? '预览图片' : '预览简历' }}
            </button>
            <button @click="downloadResume" class="download-btn">下载简历</button>
            <button @click="regenerateResume" class="regenerate-btn">重新生成</button>
          </div>
        </div>
        
        <div class="step-actions">
          <button @click="prevStep" :disabled="generating" class="prev-btn">上一步</button>
          <button @click="startOver" class="start-over-btn">重新开始</button>
        </div>
      </div>
    </div>
    
    <!-- 简历预览模态框 -->
    <div v-if="showPreview" class="modal-overlay" @click="closePreview">
      <div class="modal-content preview-modal" @click.stop>
        <div class="modal-header">
          <h3>简历预览</h3>
          <button @click="closePreview" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="resume-preview" v-html="formattedResumeContent"></div>
        </div>
        <div class="modal-footer">
          <button @click="downloadResume" class="download-btn">下载简历</button>
          <button @click="closePreview" class="close-btn">关闭</button>
        </div>
      </div>
    </div>

    <!-- 模板选择弹窗 -->
    <div v-if="showTemplateSelection" class="modal-overlay" @click="closeTemplateDialog">
      <div class="modal-content template-modal" @click.stop>
        <div class="modal-header">
          <h3>选择简历模板</h3>
          <button @click="closeTemplateDialog" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="template-section">
            <h4>简历模板</h4>
            <div class="template-grid">
              <div
                v-for="(template, key) in templateOptions"
                :key="key"
                class="template-card"
                :class="{ selected: selectedTemplate === key }"
                @click="selectTemplate(key)"
              >
                <div class="template-preview">
                  <div class="template-icon">{{ template.icon }}</div>
                  <div class="template-badge" v-if="template.recommended">推荐</div>
                </div>
                <div class="template-info">
                  <h5>{{ template.name }}</h5>
                  <p>{{ template.description }}</p>
                  <div class="template-features">
                    <span v-for="feature in template.features" :key="feature" class="feature-tag">
                      {{ feature }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="style-section">
            <h4>简历风格</h4>
            <div class="style-grid">
              <div
                v-for="(style, key) in styleOptions"
                :key="key"
                class="style-card"
                :class="{ selected: selectedStyle === key }"
                @click="selectStyle(key)"
              >
                <div class="style-preview" :style="{ background: style.color }">
                  <div class="style-icon">{{ style.icon }}</div>
                </div>
                <div class="style-info">
                  <h5>{{ style.name }}</h5>
                  <p>{{ style.description }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="preview-section">
            <h4>预览效果</h4>
            <div class="template-preview-large">
              <div class="preview-header">
                <div class="preview-name">张三</div>
                <div class="preview-contact">手机：138****8888 | 邮箱：zhang@example.com</div>
              </div>
              <div class="preview-section-item">
                <div class="preview-title">工作经验</div>
                <div class="preview-content">
                  <div class="preview-item">
                    <div class="preview-company">某科技公司</div>
                    <div class="preview-position">前端开发工程师</div>
                    <div class="preview-time">2020.06 - 2023.08</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeTemplateDialog" class="cancel-btn">取消</button>
          <button @click="generateWithTemplate" class="confirm-btn" :disabled="!selectedTemplate || !selectedStyle">
            确认生成
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { 
  generateResume,
  validateGenerationConfig 
} from '@/api/resume'

export default {
  name: 'ResumeGenerator',
  data() {
    return {
      currentStep: 1,
      generating: false,
      generated: false,
      showPreview: false,
      showTemplateSelection: false,
      selectedTemplate: '',
      selectedStyle: '',
      progressText: '',
      generatedContent: '',

      // 简历链接信息
      resumeLinks: {
        imgUrl: null,
        wordUrl: null,
        format: null
      },

      // 数据持久化标识
      storageKey: 'resume_generator_state',

      // 表单数据
      formData: {
        personalInfo: {
          name: '',
          gender: '',
          age: '',
          phone: '',
          email: '',
          address: '',
          summary: ''
        },
        targetPosition: '',
        education: [{
          school: '',
          major: '',
          degree: '',
          graduationDate: ''
        }],
        workExperience: [{
          company: '',
          position: '',
          startDate: '',
          endDate: '',
          description: ''
        }],
        skills: '',
        projects: ''
      },

      // 生成设置
      generationSettings: {
        templateType: 'standard',
        style: 'professional',
        prompt: ''
      },

      // 模板和风格选项
      templates: {
        standard: '标准简历模板',
        creative: '创意简历模板',
        technical: '技术简历模板',
        executive: '高管简历模板',
        fresh_graduate: '应届生简历模板'
      },
      styles: {
        professional: '专业风格',
        modern: '现代风格',
        classic: '经典风格',
        creative: '创意风格',
        minimal: '简约风格'
      },

      // 模板选择选项
      templateOptions: {
        standard: {
          name: '标准模板',
          description: '适合大多数职位的通用模板',
          icon: '📄',
          features: ['简洁明了', '结构清晰', '通用性强'],
          recommended: true
        },
        creative: {
          name: '创意模板',
          description: '适合设计、创意类职位',
          icon: '🎨',
          features: ['视觉突出', '个性化强', '创意设计']
        },
        technical: {
          name: '技术模板',
          description: '专为技术岗位设计',
          icon: '💻',
          features: ['技能突出', '项目展示', '逻辑清晰']
        },
        executive: {
          name: '高管模板',
          description: '适合管理层和高级职位',
          icon: '👔',
          features: ['商务正式', '成就导向', '权威感强']
        },
        fresh_graduate: {
          name: '应届生模板',
          description: '专为应届毕业生设计',
          icon: '🎓',
          features: ['教育突出', '潜力展示', '简洁大方']
        }
      },

      // 风格选择选项
      styleOptions: {
        professional: {
          name: '专业风格',
          description: '正式商务，适合传统行业',
          icon: '💼',
          color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        },
        modern: {
          name: '现代风格',
          description: '时尚简约，适合互联网行业',
          icon: '🚀',
          color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
        },
        classic: {
          name: '经典风格',
          description: '传统稳重，适合金融法律',
          icon: '🏛️',
          color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
        },
        creative: {
          name: '创意风格',
          description: '个性鲜明，适合设计创意',
          icon: '🎭',
          color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
        },
        minimal: {
          name: '简约风格',
          description: '极简设计，突出内容',
          icon: '⚪',
          color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
        }
      }
    }
  },
  computed: {
    canProceedStep1() {
      return this.formData.personalInfo.name &&
             this.formData.personalInfo.phone &&
             this.formData.personalInfo.email &&
             this.formData.targetPosition
    },

    formattedResumeContent() {
      if (!this.generatedContent) return ''

      try {
        const content = JSON.parse(this.generatedContent)
        return this.formatResumeForDisplay(content)
      } catch (e) {
        return `<pre>${this.generatedContent}</pre>`
      }
    }
  },
  props: {
    initialData: {
      type: Object,
      default: () => ({})
    },
    isFloatMode: {
      type: Boolean,
      default: false
    }
  },

  async mounted() {
    try {
      console.log('ResumeGenerator组件初始化...')

      // 优先使用 props 传入的数据（弹窗模式）
      if (this.initialData && Object.keys(this.initialData).length > 0) {
        console.log('使用 props 数据:', this.initialData)

        // 填充基本信息
        if (this.initialData.personalInfo) {
          this.formData.personalInfo.name = this.initialData.personalInfo.name || ''
          this.formData.personalInfo.phone = this.initialData.personalInfo.phone || ''
          this.formData.personalInfo.email = this.initialData.personalInfo.email || ''
          this.formData.personalInfo.address = this.initialData.personalInfo.address || ''
          this.formData.personalInfo.summary = this.initialData.personalInfo.summary || ''
        }
        this.formData.targetPosition = this.initialData.targetPosition || ''

        // 教育和工作经历
        if (this.initialData.educations && this.initialData.educations.length) {
          this.formData.education = this.initialData.educations
        }
        if (this.initialData.workExperiences && this.initialData.workExperiences.length) {
          this.formData.workExperience = this.initialData.workExperiences
        }

        // 弹窗模式下直接跳转到第3步
        if (this.isFloatMode) {
          this.currentStep = 3
        }
      }
      // 否则检查 sessionStorage（页面跳转模式）
      else {
        const savedFormData = sessionStorage.getItem('aiResumeFormData')
        const savedStep = sessionStorage.getItem('aiResumeStep')

        if (savedFormData) {
          const formData = JSON.parse(savedFormData)

          if (formData.personalInfo) {
            this.formData.personalInfo.name = formData.personalInfo.name || ''
            this.formData.personalInfo.phone = formData.personalInfo.phone || ''
            this.formData.personalInfo.email = formData.personalInfo.email || ''
            this.formData.personalInfo.address = formData.personalInfo.address || ''
            this.formData.personalInfo.summary = formData.personalInfo.summary || ''
          }
          this.formData.targetPosition = formData.targetPosition || ''

          if (formData.educations && formData.educations.length) {
            this.formData.education = formData.educations
          }
          if (formData.workExperiences && formData.workExperiences.length) {
            this.formData.workExperience = formData.workExperiences
          }

          if (savedStep) {
            this.currentStep = parseInt(savedStep)
          }

          sessionStorage.removeItem('aiResumeFormData')
          sessionStorage.removeItem('aiResumeStep')
        } else {
          // 页面加载时恢复状态
          this.loadState()
        }
      }

      await Promise.allSettled([this.checkApiConfig()])
      console.log('ResumeGenerator组件初始化完成')
    } catch (error) {
      console.error('组件初始化失败:', error)
    }
  },
  beforeUnmount() {
    // 页面卸载前保存状态
    this.saveState()
  },
  methods: {
    // 返回上一个组件
    goBack() {
      // 清除保存的状态
      this.clearState()
      // 使用Vue Router返回上一页，或者触发父组件事件
      if (this.$router) {
        this.$router.go(-1)
      } else {
        // 如果没有路由，触发父组件事件
        this.$emit('go-back')
      }
    },

    // 显示模板选择弹窗
    showTemplateDialog() {
      if (!this.validateForm()) {
        return
      }
      this.showTemplateSelection = true
    },
    // 格式化简历对象为可读文本
    formatResumeObject(data) {
      let text = ''

      // 基本信息
      if (data.basicInfo || data.fullName || data.name) {
        text += '========== 基本信息 ==========\n'
        text += '姓名：' + (data.fullName || data.name || data.basicInfo?.name || '') + '\n'
        text += '电话：' + (data.phone || data.basicInfo?.phone || '') + '\n'
        text += '邮箱：' + (data.email || data.basicInfo?.email || '') + '\n'
        text += '求职意向：' + (data.position || data.basicInfo?.position || '') + '\n'
        if (data.basicInfo?.summary) text += '个人简介：' + data.basicInfo.summary + '\n'
        text += '\n'
      }

      // 工作经历
      const works = data.workExperiences || data.workExperience || []
      if (works.length > 0) {
        text += '========== 工作经历 ==========\n'
        works.forEach((work, i) => {
          text += '\n' + (i + 1) + '. ' + (work.company || '') + '\n'
          text += '   职位：' + (work.position || '') + '\n'
          if (work.startDate || work.endDate) {
            text += '   时间：' + (work.startDate || '') + ' - ' + (work.endDate || '至今') + '\n'
          }
          if (work.responsibility || work.description) {
            text += '   职责：' + (work.responsibility || work.description) + '\n'
          }
        })
        text += '\n'
      }

      // 教育经历
      const edus = data.educations || data.education || []
      if (edus.length > 0) {
        text += '========== 教育经历 ==========\n'
        edus.forEach((edu, i) => {
          text += '\n' + (i + 1) + '. ' + (edu.school || '') + '\n'
          text += '   专业：' + (edu.major || '') + '\n'
          text += '   学历：' + (edu.degree || '') + '\n'
          if (edu.startDate || edu.endDate) {
            text += '   时间：' + (edu.startDate || '') + ' - ' + (edu.endDate || '') + '\n'
          }
        })
        text += '\n'
      }

      // 技能
      const skills = data.skills || []
      if (skills.length > 0) {
        text += '========== 技能专长 ==========\n'
        skills.forEach(skill => {
          const skillName = skill.skillName || skill.name || ''
          const level = skill.level || skill.proficiency || ''
          text += '• ' + skillName + (level ? ' (' + level + '/5)' : '') + '\n'
        })
        text += '\n'
      }

      return text || JSON.stringify(data, null, 2)
    },
    validateForm() {
      // 检查必要字段是否填写
      if (!this.formData.personalInfo.name) {
        this.$message.warning('请填写姓名')
        return false
      }
      if (!this.formData.personalInfo.phone) {
        this.$message.warning('请填写手机号码')
        return false
      }
      if (!this.formData.personalInfo.email) {
        this.$message.warning('请填写邮箱')
        return false
      }
      if (!this.formData.targetPosition) {
        this.$message.warning('请填写期望职位')
        return false
      }
      return true
    },
    // 关闭模板选择弹窗
    closeTemplateDialog() {
      this.showTemplateSelection = false
      this.selectedTemplate = ''
      this.selectedStyle = ''
    },

    // 选择模板
    selectTemplate(templateKey) {
      this.selectedTemplate = templateKey
    },

    // 选择风格
    selectStyle(styleKey) {
      this.selectedStyle = styleKey
    },

    // 按模板生成简历
    generateWithTemplate() {
      this.closeTemplateDialog()
      this.generating = true
      this.generated = false
      this.progressText = `正在使用${this.templateOptions[this.selectedTemplate].name}和${this.styleOptions[this.selectedStyle].name}生成简历...`

      // 模拟生成过程
      setTimeout(() => {
        this.progressText = '正在应用模板样式...'
        setTimeout(() => {
          this.progressText = '正在优化布局...'
          setTimeout(() => {
            this.progressText = '生成完成！'
            this.generating = false
            this.generated = true
            this.generatedContent = this.generateResumeContentWithTemplate()
          }, 1000)
        }, 1500)
      }, 1000)
    },


    async checkApiConfig() {
      try {
        console.log('检查API配置...')
        const response = await validateGenerationConfig()
        console.log('配置验证响应:', response)

        // 检查响应结构
        if (response && response.data) {
          if (response.data.success === false) {
            this.$message && this.$message.warning(response.data.message || 'API配置检查失败')
          } else if (response.data.configValid === false) {
            this.$message && this.$message.warning('AI服务配置不完整，请联系管理员')
          }
        }
      } catch (error) {
        console.error('检查API配置失败:', error)
        // 不显示错误消息，避免影响用户体验
        console.log('API配置检查失败，可能是服务未启动')
      }
    },

    // 步骤导航
    nextStep() {
      if (this.currentStep < 4) {
        this.currentStep++
      }
    },

    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--
      }
    },

    startOver() {
      this.currentStep = 1
      this.generating = false
      this.generated = false
      this.generatedContent = ''

      // 重置链接信息
      this.resumeLinks = {
        imgUrl: null,
        wordUrl: null,
        format: null
      }
      // 重置表单数据
      this.formData = {
        personalInfo: {
          name: '',
          gender: '',
          age: '',
          phone: '',
          email: '',
          address: '',
          summary: ''
        },
        targetPosition: '',
        education: [{
          school: '',
          major: '',
          degree: '',
          graduationDate: ''
        }],
        workExperience: [{
          company: '',
          position: '',
          startDate: '',
          endDate: '',
          description: ''
        }],
        skills: '',
        projects: ''
      }

      // 清除保存的状态
      this.clearState()
    },

    // 教育经历管理
    addEducation() {
      this.formData.education.push({
        school: '',
        major: '',
        degree: '',
        graduationDate: ''
      })
    },

    removeEducation(index) {
      if (this.formData.education.length > 1) {
        this.formData.education.splice(index, 1)
      }
    },

    // 工作经历管理
    addWork() {
      this.formData.workExperience.push({
        company: '',
        position: '',
        startDate: '',
        endDate: '',
        description: ''
      })
    },

    removeWork(index) {
      if (this.formData.workExperience.length > 1) {
        this.formData.workExperience.splice(index, 1)
      }
    },

    // 生成简历
    async generateResume() {
      this.generating = true
      this.generated = false
      this.progressText = '正在准备数据...'

      try {
        const requestData = this.buildRequestData()
        console.log('请求数据:', requestData)

        this.progressText = '正在调用AI生成简历...'

        const response = await generateResume(requestData)
        console.log('生成响应:', response)

        let isSuccess = false
        let resultData = null
        let errorMsg = ''

        if (response.code !== undefined) {
          if (response.code === 0) {
            isSuccess = true
            resultData = response.data
          } else {
            errorMsg = response.message || '简历生成失败'
          }
        } else if (response.success !== undefined) {
          if (response.success) {
            isSuccess = true
            resultData = response.data
          } else {
            errorMsg = response.message || '简历生成失败'
          }
        } else if (response.data && response.data.success !== undefined) {
          if (response.data.success) {
            isSuccess = true
            resultData = response.data
          } else {
            errorMsg = response.data.message || '简历生成失败'
          }
        } else {
          // 直接返回数据的情况
          isSuccess = true
          resultData = response
        }

        if (isSuccess && resultData) {
          console.log('处理结果数据:', resultData)

          // 检查是否是 links 格式
          if (resultData.links && resultData.links.length) {
            this.resumeLinks = {
              format: 'links',
              imgUrl: resultData.links[0]?.img_url,
              wordUrl: resultData.links[0]?.word_url
            }
            this.generatedContent = '简历生成成功，请点击预览或下载'
          }
          // 检查是否直接包含简历数据
          else if (resultData.fullName || resultData.name || resultData.workExperiences || resultData.educations) {
            // 直接格式化简历对象
            const formattedText = this.formatResumeObject(resultData)
            this.generatedContent = formattedText
            this.resumeLinks.format = 'text'
          }
          else if (resultData.resumeContent) {
            this.generatedContent = resultData.resumeContent
            this.processGeneratedContent(resultData.resumeContent)
          }
          else {
            // 尝试将整个对象格式化
            const formattedText = this.formatResumeObject(resultData)
            this.generatedContent = formattedText
            this.resumeLinks.format = 'text'
          }

          this.generated = true
          this.progressText = '简历生成完成！'
          this.$message.success('简历生成成功！')
          this.saveState()
        } else {
          throw new Error(errorMsg)
        }
      } catch (error) {
        console.error('简历生成失败:', error)
        this.progressText = '生成失败'
        this.$message.error('简历生成失败: ' + (error.message || '未知错误'))
      } finally {
        this.generating = false
      }
    },
    // 按模板生成简历内容
    generateResumeContentWithTemplate() {
      const template = this.templateOptions[this.selectedTemplate]
      const style = this.styleOptions[this.selectedStyle]

      // 根据选择的模板和风格生成不同的简历内容
      const baseContent = this.buildRequestData()

      // 添加模板和风格信息
      const templateContent = {
        ...baseContent,
        template: {
          name: template.name,
          type: this.selectedTemplate,
          features: template.features
        },
        style: {
          name: style.name,
          type: this.selectedStyle,
          description: style.description
        },
        generatedAt: new Date().toISOString(),
        generationType: 'template'
      }

      return JSON.stringify(templateContent, null, 2)
    },

    // 重新生成简历
    async regenerateResume() {
      this.generated = false
      await this.generateResume()
    },

    // 构建请求数据
    buildRequestData() {
      const info = this.formData.personalInfo

      // 构建 userInfo 字符串
      const userInfoStr = [
        info.name ? `姓名：${info.name}` : '',
        this.formData.targetPosition ? `目标岗位：${this.formData.targetPosition}` : '',
        info.phone ? `电话：${info.phone}` : '',
        info.email ? `邮箱：${info.email}` : '',
        info.summary ? `个人简介：${info.summary}` : ''
      ].filter(Boolean).join('\n')

      return {
        prompt: this.generationSettings.prompt || `请生成一份${this.styles[this.generationSettings.style]}的简历`,
        userInfo: userInfoStr  // 必须是字符串，不能是对象
      }
    },
    // 处理生成的内容
    processGeneratedContent(content) {
      try {
        const parsedContent = JSON.parse(content)

        // 检查是否是links格式
        if (parsedContent.format === 'links' && parsedContent.content) {
          this.resumeLinks.format = 'links'
          this.resumeLinks.imgUrl = parsedContent.content.img_url
          this.resumeLinks.wordUrl = parsedContent.content.word_url
          this.generatedContent = content  // 添加这行

          console.log('检测到links格式响应:', this.resumeLinks)
        } else {
          // 重置链接信息
          this.resumeLinks.format = 'text'
          this.resumeLinks.imgUrl = null
          this.resumeLinks.wordUrl = null
          this.generatedContent = content  // 添加这行
        }
      } catch (e) {
        // 如果不是JSON格式，当作普通文本处理
        this.resumeLinks.format = 'text'
        this.resumeLinks.imgUrl = null
        this.resumeLinks.wordUrl = null
        this.generatedContent = content  // 添加这行
        console.log('普通文本格式响应')
      }
    },

    // 格式化个人信息
    formatPersonalInfo() {
      const info = this.formData.personalInfo
      let text = `姓名：${info.name}\n`
      if (info.gender) text += `性别：${info.gender}\n`
      if (info.age) text += `年龄：${info.age}\n`
      text += `手机：${info.phone}\n`
      text += `邮箱：${info.email}\n`
      if (info.address) text += `地址：${info.address}\n`
      if (info.summary) text += `个人简介：${info.summary}\n`
      return text
    },

    // 格式化教育背景
    formatEducation() {
      return this.formData.education
        .filter(edu => edu.school || edu.major)
        .map(edu => {
          let text = ''
          if (edu.school) text += `学校：${edu.school} `
          if (edu.major) text += `专业：${edu.major} `
          if (edu.degree) text += `学历：${edu.degree} `
          if (edu.graduationDate) text += `毕业时间：${edu.graduationDate}`
          return text.trim()
        })
        .join('\n')
    },

    // 格式化工作经验
    formatWorkExperience() {
      return this.formData.workExperience
        .filter(work => work.company || work.position)
        .map(work => {
          let text = ''
          if (work.company) text += `公司：${work.company} `
          if (work.position) text += `职位：${work.position} `
          if (work.startDate || work.endDate) {
            text += `时间：${work.startDate || ''}至${work.endDate || '至今'} `
          }
          if (work.description) text += `\n工作内容：${work.description}`
          return text.trim()
        })
        .join('\n\n')
    },

    // 预览简历
    previewResume() {
      if (this.resumeLinks.format === 'links' && this.resumeLinks.imgUrl) {
        // 如果有图片链接，在新窗口中打开图片预览
        window.open(this.resumeLinks.imgUrl, '_blank')
      } else {
        // 普通文本格式，显示模态框预览
        this.showPreview = true
      }
    },

    // 关闭预览
    closePreview() {
      this.showPreview = false
    },


    // 下载简历
    downloadResume() {
      if (!this.generatedContent) {
        this.$message.error('没有可下载的简历内容')
        return
      }

      try {
        if (this.resumeLinks.format === 'links') {
          // 如果是links格式，显示下载选项
          this.showDownloadOptions()
        } else {
          // 普通文本格式，直接下载
          this.downloadTextResume()
        }
      } catch (error) {
        console.error('下载失败:', error)
        this.$message.error('下载失败，请重试')
      }
    },

    // 显示下载选项
    showDownloadOptions() {
      const options = []

      if (this.resumeLinks.imgUrl) {
        options.push('图片版本')
      }

      if (this.resumeLinks.wordUrl) {
        options.push('Word文档')
      }

      if (options.length === 0) {
        this.$message.error('没有可用的下载链接')
        return
      }

      // 如果只有一个选项，直接下载
      if (options.length === 1) {
        if (this.resumeLinks.imgUrl) {
          this.downloadFromUrl(this.resumeLinks.imgUrl, '简历图片.jpg')
        } else if (this.resumeLinks.wordUrl) {
          this.downloadFromUrl(this.resumeLinks.wordUrl, '简历文档.docx')
        }
        return
      }

      // 多个选项，让用户选择
      this.$confirm('请选择下载格式：', '下载简历', {
        distinguishCancelAndClose: true,
        confirmButtonText: '图片版本',
        cancelButtonText: 'Word文档',
        type: 'info'
      }).then(() => {
        // 选择图片版本
        this.downloadFromUrl(this.resumeLinks.imgUrl, '简历图片.jpg')
      }).catch(action => {
        if (action === 'cancel') {
          // 选择Word文档
          this.downloadFromUrl(this.resumeLinks.wordUrl, '简历文档.docx')
        }
      })
    },

    // 从URL下载文件
    downloadFromUrl(url, filename) {
      if (!url) {
        this.$message.error('下载链接无效')
        return
      }

      const link = document.createElement('a')
      link.href = url
      link.download = filename
      link.target = '_blank'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      this.$message.success('开始下载简历！')
    },

    // 下载文本格式简历
    downloadTextResume() {
      const blob = new Blob([this.generatedContent], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${this.formData.personalInfo.name || '简历'}_${new Date().toISOString().slice(0, 10)}.txt`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)

      this.$message.success('简历下载成功！')
    },

    // 保存状态到localStorage
    saveState() {
      try {
        const state = {
          currentStep: this.currentStep,
          generated: this.generated,
          generatedContent: this.generatedContent,
          resumeLinks: this.resumeLinks,
          formData: this.formData,
          generationSettings: this.generationSettings,
          timestamp: Date.now()
        }
        localStorage.setItem(this.storageKey, JSON.stringify(state))
        console.log('状态已保存')
      } catch (error) {
        console.error('保存状态失败:', error)
      }
    },

    // 从localStorage恢复状态
    loadState() {
      try {
        const savedState = localStorage.getItem(this.storageKey)
        if (savedState) {
          const state = JSON.parse(savedState)

          // 检查状态是否过期（24小时）
          const isExpired = Date.now() - state.timestamp > 24 * 60 * 60 * 1000
          if (isExpired) {
            console.log('保存的状态已过期，清除状态')
            this.clearState()
            return
          }

          // 恢复状态
          this.currentStep = state.currentStep || 1
          this.generated = state.generated || false
          this.generatedContent = state.generatedContent || ''
          this.resumeLinks = state.resumeLinks || { imgUrl: null, wordUrl: null, format: null }

          // 恢复表单数据
          if (state.formData) {
            this.formData = { ...this.formData, ...state.formData }
          }

          // 恢复生成设置
          if (state.generationSettings) {
            this.generationSettings = { ...this.generationSettings, ...state.generationSettings }
          }

          console.log('状态已恢复:', state)
        }
      } catch (error) {
        console.error('恢复状态失败:', error)
        this.clearState()
      }
    },

    // 清除保存的状态
    clearState() {
      try {
        localStorage.removeItem(this.storageKey)
        console.log('状态已清除')
      } catch (error) {
        console.error('清除状态失败:', error)
      }
    },

    // 格式化简历内容用于显示
    formatResumeForDisplay(content) {
      if (!content) return ''

      try {
        // 如果 content 已经是对象，直接使用
        let data = typeof content === 'string' ? JSON.parse(content) : content

        // 如果是 links 格式
        if (data.format === 'links') {
          return '<div class="resume-content">简历生成成功，请点击预览或下载</div>'
        }

        let html = '<div class="resume-content" style="font-family: system-ui, sans-serif; line-height: 1.6;">'

        // 基本信息区域
        const name = data.fullName || data.name || data.basicInfo?.name || ''
        const phone = data.phone || data.basicInfo?.phone || ''
        const email = data.email || data.basicInfo?.email || ''
        const position = data.position || data.basicInfo?.position || ''
        const summary = data.profile || data.basicInfo?.summary || ''

        if (name) {
          html += '<h1 style="font-size: 24px; margin-bottom: 8px;">' + name + '</h1>'
        }
        if (position) {
          html += '<p style="font-size: 16px; color: #3b82f6; margin-bottom: 12px;"><strong>求职意向：</strong> ' + position + '</p>'
        }
        if (phone || email) {
          html += '<p style="margin-bottom: 16px;">'
          if (phone) html += '<span>📞 ' + phone + '</span>'
          if (phone && email) html += '&nbsp;&nbsp;|&nbsp;&nbsp;'
          if (email) html += '<span>✉️ ' + email + '</span>'
          html += '</p>'
        }
        if (summary) {
          html += '<div style="background: #f3f4f6; padding: 12px; border-radius: 8px; margin-bottom: 20px;">'
          html += '<p style="margin: 0;"><strong>个人简介：</strong>' + summary + '</p>'
          html += '</div>'
        }

        // 工作经历
        const works = data.workExperiences || data.workExperience || []
        if (works.length > 0) {
          html += '<h3 style="font-size: 18px; border-bottom: 2px solid #3b82f6; padding-bottom: 6px; margin: 20px 0 16px;">工作经历</h3>'
          works.forEach(work => {
            html += '<div style="margin-bottom: 16px;">'
            html += '<div style="font-weight: 600;">' + (work.company || '') + ' - ' + (work.position || '') + '</div>'
            if (work.startDate || work.endDate) {
              html += '<div style="font-size: 13px; color: #6b7280; margin: 4px 0;">' + (work.startDate || '') + ' - ' + (work.endDate || '至今') + '</div>'
            }
            if (work.responsibility || work.description) {
              html += '<p style="margin: 8px 0 0 0; color: #4b5563;">' + (work.responsibility || work.description) + '</p>'
            }
            html += '</div>'
          })
        }

        // 教育经历
        const edus = data.educations || data.education || []
        if (edus.length > 0) {
          html += '<h3 style="font-size: 18px; border-bottom: 2px solid #3b82f6; padding-bottom: 6px; margin: 20px 0 16px;">教育背景</h3>'
          edus.forEach(edu => {
            html += '<div style="margin-bottom: 12px;">'
            html += '<div style="font-weight: 600;">' + (edu.school || '') + '</div>'
            html += '<div style="font-size: 14px; color: #4b5563;">' + (edu.major || '') + ' ' + (edu.degree || '') + '</div>'
            if (edu.startDate || edu.endDate) {
              html += '<div style="font-size: 13px; color: #6b7280;">' + (edu.startDate || '') + ' - ' + (edu.endDate || '') + '</div>'
            }
            html += '</div>'
          })
        }

        // 技能
        const skills = data.skills || []
        if (skills.length > 0) {
          html += '<h3 style="font-size: 18px; border-bottom: 2px solid #3b82f6; padding-bottom: 6px; margin: 20px 0 16px;">技能专长</h3>'
          html += '<div style="display: flex; flex-wrap: wrap; gap: 8px;">'
          skills.forEach(skill => {
            const skillName = skill.skillName || skill.name || ''
            html += '<span style="background: #e5e7eb; padding: 4px 12px; border-radius: 20px; font-size: 14px;">' + skillName + '</span>'
          })
          html += '</div>'
        }

        html += '</div>'
        return html

      } catch (e) {
        // 解析失败，作为纯文本显示
        return '<pre style="white-space: pre-wrap; font-family: monospace;">' + content + '</pre>'
      }
    }
  }
}
</script>


<style scoped>
/* 基础容器样式 */
.resume-generator {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding: 24px;
}
.generator-container {
  max-width: 1200px;
  margin: 0 auto;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 32px;
  /* 添加这些 */
  max-height: 70vh;
  overflow-y: auto;
}

/* 标题区域样式 */
.header-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  border-bottom: 1px solid #eff2f5;
  padding-bottom: 24px;
}
.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #ffffff;
  border: 1px solid #dbeafe;
  color: #3b82f6;
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.back-button:hover {
  background-color: #eff6ff;
  border-color: #3b82f6;
}
.back-icon {
  font-size: 16px;
}
.title-area h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 8px;
}
.title-area .description {
  font-size: 14px;
  color: #6b7785;
}

/* 步骤指示器样式 */
.steps {
  display: flex;
  justify-content: space-between;
  margin-bottom: 32px;
  position: relative;
}
.steps::before {
  content: '';
  position: absolute;
  top: 24px;
  left: 40px;
  right: 40px;
  height: 2px;
  background-color: #eff2f5;
  z-index: 1;
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
  width: 25%;
}
.step-number {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #eff2f5;
  color: #6b7785;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}
.step-title {
  font-size: 14px;
  color: #6b7785;
  text-align: center;
  transition: all 0.3s ease;
}
.step.active .step-number {
  background-color: #3b82f6;
  color: #ffffff;
}
.step.active .step-title {
  color: #3b82f6;
  font-weight: 500;
}
.step.completed .step-number {
  background-color: #10b981;
  color: #ffffff;
}
.step.completed .step-title {
  color: #10b981;
}

/* 步骤内容区域样式 */
.step-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 24px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eff2f5;
}

/* 表单布局样式 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #4e5969;
}
.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  color: #1d2129;
  transition: border-color 0.3s ease;
}
.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}
.form-group.full-width {
  grid-column: 1 / -1;
}
.form-group textarea {
  resize: vertical;
}

/* 详细信息区域样式 */
.section {
  margin-bottom: 24px;
  padding: 16px;
  border-radius: 8px;
  background-color: #f9fafb;
}
.section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 16px;
}
.education-item,
.work-item {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #eff2f5;
}
.education-item:last-child,
.work-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}
.remove-btn {
  background-color: #ffffff;
  border: 1px solid #fee2e2;
  color: #ef4444;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
}
.remove-btn:hover {
  background-color: #fee2e2;
}
.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #ffffff;
  border: 1px solid #dbeafe;
  color: #3b82f6;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
}
.add-btn:hover {
  background-color: #eff6ff;
  border-color: #3b82f6;
}

/* 步骤操作按钮样式 */
.step-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 24px;
}
.prev-btn {
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  color: #4e5969;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}
.prev-btn:hover {
  border-color: #c5c6c7;
  color: #1d2129;
}
.next-btn {
  background-color: #3b82f6;
  border: 1px solid #3b82f6;
  color: #ffffff;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}
.next-btn:hover {
  background-color: #2563eb;
  border-color: #2563eb;
}
.next-btn:disabled {
  background-color: #dbeafe;
  border-color: #dbeafe;
  cursor: not-allowed;
}

/* 生成步骤样式 */
.generation-start {
  margin-bottom: 24px;
}
.summary {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}
.summary h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eff2f5;
}
.summary-item {
  font-size: 14px;
  color: #4e5969;
  margin-bottom: 8px;
  display: flex;
  gap: 8px;
}
.summary-item strong {
  color: #1d2129;
  font-weight: 500;
}
.generate-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}
.generate-btn {
  padding: 14px 28px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}
.generate-btn.direct {
  background-color: #3b82f6;
  border: 1px solid #3b82f6;
  color: #ffffff;
}
.generate-btn.direct:hover {
  background-color: #2563eb;
  border-color: #2563eb;
}
.generate-btn.template {
  background-color: #ffffff;
  border: 1px solid #3b82f6;
  color: #3b82f6;
}
.generate-btn.template:hover {
  background-color: #eff6ff;
}

/* 生成进度样式 */
.generation-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  gap: 16px;
}
.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #eff2f5;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.generation-progress p {
  font-size: 16px;
  color: #4e5969;
}
.progress-text {
  font-size: 14px;
  color: #6b7785;
}

/* 生成结果样式 */
.generation-result {
  padding: 24px;
  border-radius: 8px;
  background-color: #f9fafb;
  margin-bottom: 24px;
}
.success-message {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}
.success-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #ecfdf5;
  color: #10b981;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
}
.success-message h4 {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}
.result-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 24px;
}
.preview-btn,
.download-btn,
.regenerate-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}
.preview-btn {
  background-color: #ffffff;
  border: 1px solid #3b82f6;
  color: #3b82f6;
}
.preview-btn:hover {
  background-color: #eff6ff;
}
.download-btn {
  background-color: #3b82f6;
  border: 1px solid #3b82f6;
  color: #ffffff;
}
.download-btn:hover {
  background-color: #2563eb;
  border-color: #2563eb;
}
.regenerate-btn {
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  color: #4e5969;
}
.regenerate-btn:hover {
  border-color: #c5c6c7;
  color: #1d2129;
}
.start-over-btn {
  background-color: #ffffff;
  border: 1px solid #fee2e2;
  color: #ef4444;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}
.start-over-btn:hover {
  background-color: #fee2e2;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 1000px;
  max-height: 90vh;
  overflow: hidden;
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid #eff2f5;
}
.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}
.close-btn {
  background-color: transparent;
  border: none;
  color: #6b7785;
  font-size: 20px;
  cursor: pointer;
  transition: color 0.3s ease;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.close-btn:hover {
  color: #ef4444;
  background-color: #fee2e2;
}
.modal-body {
  padding: 24px;
  max-height: calc(90vh - 120px);
  overflow-y: auto;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 16px 24px;
  border-top: 1px solid #eff2f5;
}

/* 简历预览模态框样式 */
.preview-modal .modal-body {
  padding: 0;
}
.resume-preview {
  padding: 24px;
  background-color: #ffffff;
  min-height: 500px;
}
.resume-content {
  line-height: 1.8;
  color: #1d2129;
}
.text-format .format-notice {
  background-color: #eff6ff;
  color: #3b82f6;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 14px;
}

/* 模板选择弹窗样式 */
.template-section,
.style-section,
.preview-section {
  margin-bottom: 24px;
}
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  margin-top: 16px;
}
.template-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.template-card:hover {
  border-color: #3b82f6;
  background-color: #f9fafb;
}
.template-card.selected {
  border-color: #3b82f6;
  background-color: #eff6ff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}
.template-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.template-icon {
  font-size: 24px;
  color: #3b82f6;
}
.template-badge {
  background-color: #3b82f6;
  color: #ffffff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
}
.template-info h5 {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 4px;
}
.template-info p {
  font-size: 12px;
  color: #6b7785;
  margin-bottom: 8px;
}
.template-features {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.feature-tag {
  font-size: 12px;
  color: #6b7785;
  background-color: #f3f4f6;
  padding: 2px 8px;
  border-radius: 4px;
}
.style-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
  margin-top: 16px;
}
.style-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.style-card:hover {
  border-color: #3b82f6;
  background-color: #f9fafb;
}
.style-card.selected {
  border-color: #3b82f6;
  background-color: #eff6ff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}
.style-preview {
  width: 100%;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}
.style-icon {
  font-size: 24px;
  color: #ffffff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
.style-info h5 {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 4px;
  text-align: center;
}
.style-info p {
  font-size: 12px;
  color: #6b7785;
  text-align: center;
}
.preview-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 16px;
}
.template-preview-large {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 24px;
  background-color: #ffffff;
}
.preview-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eff2f5;
}
.preview-name {
  font-size: 20px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 8px;
}
.preview-contact {
  font-size: 14px;
  color: #6b7785;
}
.preview-section-item {
  margin-bottom: 16px;
}
.preview-title {
  font-size: 16px;
  font-weight: 600;
  color: #3b82f6;
  margin-bottom: 8px;
}
.preview-content {
  padding-left: 8px;
}
.preview-item {
  margin-bottom: 12px;
}
.preview-company {
  font-size: 14px;
  font-weight: 500;
  color: #1d2129;
}
.preview-position {
  font-size: 14px;
  color: #6b7785;
  margin-bottom: 4px;
}
.preview-time {
  font-size: 12px;
  color: #9ca3af;
}
.cancel-btn {
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  color: #4e5969;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}
.cancel-btn:hover {
  border-color: #c5c6c7;
  color: #1d2129;
}
.confirm-btn {
  background-color: #3b82f6;
  border: 1px solid #3b82f6;
  color: #ffffff;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}
.confirm-btn:hover {
  background-color: #2563eb;
  border-color: #2563eb;
}
.confirm-btn:disabled {
  background-color: #dbeafe;
  border-color: #dbeafe;
  cursor: not-allowed;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .generator-container {
    padding: 16px;
  }
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  .steps {
    flex-direction: column;
    gap: 16px;
  }
  .steps::before {
    display: none;
  }
  .step {
    flex-direction: row;
    gap: 12px;
    width: 100%;
    align-items: center;
  }
  .step-number {
    margin-bottom: 0;
  }
  .form-grid {
    grid-template-columns: 1fr;
  }
  .step-actions {
    flex-direction: column;
  }
  .prev-btn,
  .next-btn,
  .start-over-btn {
    width: 100%;
  }
  .generate-buttons {
    flex-direction: column;
  }
  .generate-btn {
    width: 100%;
    justify-content: center;
  }
  .result-actions {
    flex-direction: column;
  }
  .preview-btn,
  .download-btn,
  .regenerate-btn {
    width: 100%;
    justify-content: center;
  }
  .template-grid,
  .style-grid {
    grid-template-columns: 1fr;
  }
}
</style>
