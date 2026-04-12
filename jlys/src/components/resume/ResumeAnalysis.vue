<template>
  <div class="resume-analysis">
    <div class="container">
      <div class="row">
        <div class="col-md-8 mx-auto text-center mb-5">
          <h1 class="display-5 fw-bold mb-3">智能简历匹配分析</h1>
          <p class="lead text-muted">上传您的简历和职位描述，获取详细的匹配度分析和优化建议</p>
        </div>
      </div>

      <!-- 上传和职位描述区域 -->
      <div class="row mb-5">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <i class="fas fa-upload me-2"></i>上传简历
            </div>
            <div class="card-body">
              <div class="upload-area" ref="dropArea" @click="triggerFileInput"
                   @dragover="handleDragOver" @dragenter="handleDragEnter"
                   @dragleave="handleDragLeave" @drop="handleDrop">
                <i class="fas fa-cloud-upload-alt"></i>
                <h5>点击或拖拽上传简历</h5>
                <p class="text-muted">支持PDF、DOC、DOCX文件格式</p>
                <input
                  type="file"
                  ref="fileInput"
                  class="d-none"
                  accept=".pdf,.doc,.docx"
                  @change="handleFileChange"
                >
                <button class="btn btn-outline-primary mt-3">选择文件</button>
              </div>
              <div class="mt-4">
                <p class="mb-1"><strong>已上传文件：</strong> <span>{{ fileName || '暂无' }}</span></p>
                <small class="text-muted">文件大小：{{ fileSize }}</small>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <i class="fas fa-briefcase me-2"></i>职位描述
            </div>
            <div class="card-body">
              <div class="mb-4">
                <label class="form-label fw-bold">职位名称</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="jobTitle"
                >
              </div>

              <div class="mb-4">
                <label class="form-label fw-bold">职位描述</label>
                <textarea
                  class="form-control"
                  rows="10"
                  v-model="jobDescription"
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 简历预览区域 -->
      <div class="row mb-5" v-if="fileName || showPreview">
        <div class="col-12">
          <div class="card resume-preview-card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <div>
                <i class="fas fa-file-alt me-2"></i>简历预览
              </div>
              <div class="preview-controls">
                <button
                    class="btn btn-sm btn-outline-primary me-2"
                    @click="togglePreviewSize"
                >
                  <i class="fas" :class="isFullPreview ? 'fa-compress' : 'fa-expand'"></i>
                  {{ isFullPreview ? '收起' : '展开' }}
                </button>
                <button
                    class="btn btn-sm btn-outline-secondary"
                    @click="hidePreview"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
            </div>
            <div class="card-body">
              <div class="resume-preview" :class="{ 'full-preview': isFullPreview }">
                <!-- 如果有API返回的简历内容，显示真实内容 -->
                <div v-if="resumeContent" class="resume-content">
                  <div class="resume-header-section" v-if="basicInfo.name">
                    <h4>{{ basicInfo.name }} - {{ basicInfo.position || '求职者' }}</h4>
                    <div class="contact-info-grid">
                      <div class="contact-column">
                        <div class="contact-item" v-if="basicInfo.education">
                          <i class="fas fa-graduation-cap me-1"></i> {{ basicInfo.education }}
                        </div>
                        <div class="contact-item" v-if="basicInfo.experience">
                          <i class="fas fa-briefcase me-1"></i> {{ basicInfo.experience }}
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 显示完整简历内容 -->
                  <div class="resume-full-content">
                    <pre class="resume-text">{{ resumeContent }}</pre>
                  </div>
                </div>

                <!-- 默认预览内容（当没有API数据时）- 修改为通用占位符 -->
                <div v-else>
                  <div class="resume-header-section">
                    <h4>
                      <i class="fas fa-user me-2"></i>
                      候选人姓名...
                      <small class="text-muted ms-3">应聘职位...</small>
                    </h4>
                    <div class="contact-info-grid">
                      <div class="contact-column">
                        <div class="contact-item">
                          <i class="fas fa-phone me-1"></i>
                          <span class="text-muted">电话号码</span>
                        </div>
                        <div class="contact-item">
                          <i class="fas fa-envelope me-1"></i>
                          <span class="text-muted">邮箱地址</span>
                        </div>
                        <div class="contact-item">
                          <i class="fas fa-map-marker-alt me-1"></i>
                          <span class="text-muted">所在城市</span>
                        </div>
                      </div>
                      <div class="contact-column">
                        <div class="contact-item">
                          <i class="fab fa-github me-1"></i>
                          <span class="text-muted">GitHub链接</span>
                        </div>
                        <div class="contact-item">
                          <i class="fab fa-linkedin me-1"></i>
                          <span class="text-muted">LinkedIn链接</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="resume-section">
                    <h5><i class="fas fa-code me-2"></i>专业技能</h5>
                    <ul class="skills-list">
                      <li class="text-muted">• 请上传简历查看具体技能信息</li>
                    </ul>
                  </div>

                  <div class="resume-section">
                    <h5><i class="fas fa-briefcase me-2"></i>工作经历</h5>
                    <div class="experience-item">
                      <div class="experience-header">
                        <strong class="text-muted">请上传简历查看工作经历</strong>
                      </div>
                      <p class="text-muted">上传简历后，这里将显示您的工作经验和项目经历</p>
                    </div>
                  </div>

                  <div class="resume-section">
                    <h5><i class="fas fa-graduation-cap me-2"></i>教育背景</h5>
                    <div class="education-item text-muted">
                      请上传简历查看教育背景信息
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 分析按钮区域 -->
            <div class="card-footer bg-light">
              <div class="text-center">
                <button
                    class="btn btn-primary btn-lg px-5"
                    @click="startAnalysis"
                    :disabled="analyzing || !fileName || !jobTitle.trim() || !jobDescription.trim()"
                >
                  <i class="fas fa-chart-line me-2"></i>
                  {{ analyzing ? '分析中...' : '开始智能匹配分析' }}
                </button>

                <div class="mt-2">
                  <small class="text-muted">
                    <i class="fas fa-info-circle me-1"></i>
                    请确保已上传简历并填写完整的职位信息
                  </small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分析结果区域 - 整合HTML报告样式 -->
      <div class="analysis-section" v-show="showAnalysis">
        <!-- 头部区域 -->
        <header class="report-header">
          <div class="header-content">
            <div class="header-icon">
              <i class="fas fa-file-alt"></i>
            </div>
            <h1>{{ jobTitle }} 简历分析报告</h1>
            <p class="subtitle">基于ATS系统解析与岗位需求匹配度的综合评估</p>
          </div>
        </header>

        <!-- 匹配度评分区 -->
        <section class="section">
          <div class="section-title">
            <div class="section-icon">
              <i class="fas fa-percentage"></i>
            </div>
            <h2>简历匹配度评分</h2>
          </div>

          <div class="score-section">
            <div class="score-card">
              <div class="overall-score">
                <div class="score-value" ref="scoreValue">{{ analysisData.overallScore.score }}%</div>
                <div class="score-label">整体岗位匹配度</div>
              </div>

              <div class="progress-container">
                <div class="progress-bar" ref="matchProgress" :style="{ width: analysisData.overallScore.score + '%' }"></div>
              </div>

              <div class="strengths">
                <h3><i class="fas fa-check-circle"></i> 优势分析</h3>
                <ul>
                  <li v-for="strength in analysisData.strengths" :key="strength">{{ strength }}</li>
                </ul>
              </div>

              <div class="weaknesses">
                <h3><i class="fas fa-exclamation-circle"></i> 待提升领域</h3>
                <ul>
                  <li v-for="weakness in analysisData.weaknesses" :key="weakness">{{ weakness }}</li>
                </ul>
              </div>
            </div>

            <div class="score-chart">
              <h3>能力维度雷达图</h3>
              <div v-if="analysisData.abilityRadar && analysisData.abilityRadar.labels && analysisData.abilityRadar.labels.length > 0" class="chart-container">
                <canvas ref="radarChart" id="radar-chart"></canvas>
                <p>基于简历内容生成的能力分析</p>
              </div>
              <div v-else class="empty-state">
                <p class="text-muted">暂无能力雷达图数据</p>
              </div>
            </div>
          </div>
        </section>

        <div class="divider"></div>

        <!-- ATS可搜索性分析区 -->
        <section class="section">
          <div class="section-title">
            <div class="section-icon">
              <i class="fas fa-search"></i>
            </div>
            <h2>ATS可搜索性分析</h2>
          </div>

          <table class="analysis-table">
            <thead>
            <tr>
              <th>检查项目</th>
              <th>状态</th>
              <th>建议</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in analysisData.atsAnalysis" :key="item.item">
              <td>{{ item.item }}</td>
              <td :class="item.statusClass">{{ item.status }}</td>
              <td>{{ item.suggestion }}</td>
            </tr>
            </tbody>
          </table>
        </section>

        <div class="divider"></div>

        <!-- 硬技能匹配分析区 -->
        <section class="section">
          <div class="section-title">
            <div class="section-icon">
              <i class="fas fa-laptop-code"></i>
            </div>
            <h2>硬技能匹配分析</h2>
          </div>

          <table class="analysis-table">
            <thead>
            <tr>
              <th>技能名称</th>
              <th>状态</th>
              <th>建议</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="skill in analysisData.hardSkills" :key="skill.skill">
              <td>{{ skill.skill }}</td>
              <td :class="skill.statusClass">{{ skill.status }}</td>
              <td>{{ skill.suggestion }}</td>
            </tr>
            </tbody>
          </table>
        </section>

        <div class="divider"></div>

        <!-- 软技能评估区 -->
        <section class="section">
          <div class="section-title">
            <div class="section-icon">
              <i class="fas fa-users"></i>
            </div>
            <h2>软技能评估</h2>
          </div>

          <table class="analysis-table">
            <thead>
            <tr>
              <th>能力维度</th>
              <th>状态</th>
              <th>建议</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="skill in analysisData.softSkills" :key="skill.skill">
              <td>{{ skill.skill }}</td>
              <td :class="skill.statusClass">{{ skill.status }}</td>
              <td>{{ skill.suggestion }}</td>
            </tr>
            </tbody>
          </table>
        </section>

        <div class="divider"></div>

        <!-- 关键改进建议区 -->
        <section class="section">
          <div class="section-title">
            <div class="section-icon">
              <i class="fas fa-lightbulb"></i>
            </div>
            <h2>关键改进建议</h2>
          </div>

          <ul class="suggestion-list">
            <li v-for="suggestion in analysisData.suggestions" :key="suggestion.title" class="suggestion-item">
              <div class="suggestion-icon">
                <i :class="suggestion.icon"></i>
              </div>
              <div class="suggestion-content">
                <div class="suggestion-title">{{ suggestion.title }}</div>
                <p>{{ suggestion.content }}</p>
              </div>
            </li>
          </ul>
        </section>

        <div class="divider"></div>

        <!-- 额外洞察区 -->
        <section class="section">
          <div class="insights">
            <div class="insight-title">
              <i class="fas fa-binoculars"></i> {{ analysisData.insights.title }}
            </div>
            <div class="insight-content">
              <p v-for="paragraph in analysisData.insights.content" :key="paragraph" v-html="paragraph"></p>
            </div>
          </div>
        </section>

        <div class="text-center mt-5">
          <button class="btn btn-primary btn-lg me-3">
            <i class="fas fa-download me-2"></i>下载完整报告
          </button>
          <button class="btn btn-outline-primary btn-lg" @click="resetAnalysis">
            <i class="fas fa-sync-alt me-2"></i>重新分析
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { analyzeResumeMatch } from '@/api/resume'

export default {
  name: 'ResumeAnalysis',
  setup() {
    const dropArea = ref(null)
    const fileInput = ref(null)
    const fileName = ref('')
    const fileSize = ref('0 KB')
    const uploadedFile = ref(null) // 存储上传的文件对象
    const jobTitle = ref('高级前端开发工程师')
    const jobDescription = ref(`职位描述：

我们正在寻找一位经验丰富的高级前端开发工程师加入我们的团队。您将负责构建高性能、可扩展的前端应用，并与跨职能团队合作，提供卓越的用户体验。

主要职责：
- 使用现代JavaScript框架（React/Vue）开发和维护复杂的前端应用
- 优化应用性能，确保跨平台兼容性和响应式设计
- 与UI/UX设计师和后端开发人员紧密合作，实现产品需求
- 编写高质量、可维护的代码，执行代码审查
- 参与技术选型，改进前端架构和工作流程

任职要求：
- 5年以上前端开发经验，精通HTML5、CSS3和JavaScript（ES6+）
- 具有React或Vue的实际项目经验
- 熟悉前端构建工具（Webpack、Vite）和版本控制（Git）
- 掌握TypeScript，有大型项目经验者优先
- 了解Node.js和后端技术者优先
- 计算机科学或相关领域本科及以上学历
- 良好的沟通能力和团队协作精神

加分项：
- 有开源项目贡献或个人技术博客
- 有性能优化经验
- 有移动端开发经验（React Native/Flutter）`)
    const analyzing = ref(false)
    const showAnalysis = ref(false)
    const showPreview = ref(true) // 默认显示预览
    const isFullPreview = ref(false)
    const resumeContent = ref('') // 存储简历内容
    const basicInfo = ref({}) // 存储基本信息

    // 引用DOM元素
    const scoreValue = ref(null)
    const matchProgress = ref(null)
    const radarChart = ref(null)

    // 分析数据 - 初始化为空结构，仅使用真实API数据
    const analysisData = ref({
      overallScore: { score: 0, level: '', description: '' },
      abilityRadar: {
        labels: [],
        currentLevel: [],
        requiredLevel: []
      },
      strengths: [],
      weaknesses: [],
      atsAnalysis: [],
      hardSkills: [],
      softSkills: [],
      suggestions: [],
      insights: {
        title: '',
        content: []
      }
    })

    let chart = null

    const triggerFileInput = () => {
      fileInput.value?.click()
    }

    // 拖拽上传功能
    const handleDragOver = (event) => {
      event.preventDefault()
      event.stopPropagation()
    }

    const handleDragEnter = (event) => {
      event.preventDefault()
      event.stopPropagation()
    }

    const handleDragLeave = (event) => {
      event.preventDefault()
      event.stopPropagation()
    }

    const handleDrop = (event) => {
      event.preventDefault()
      event.stopPropagation()

      const files = event.dataTransfer.files
      if (files.length > 0) {
        const file = files[0]
        // 模拟文件输入事件
        const fakeEvent = {
          target: {
            files: [file]
          }
        }
        handleFileChange(fakeEvent)
      }
    }

    const handleFileChange = (event) => {
      const file = event.target.files?.[0]
      if (file) {
        // 验证文件类型
        const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
        if (!allowedTypes.includes(file.type)) {
          ElMessage.error('只支持PDF、DOC、DOCX格式的文件')
          return
        }

        // 验证文件大小（10MB）
        if (file.size > 10 * 1024 * 1024) {
          ElMessage.error('文件大小不能超过10MB')
          return
        }

        uploadedFile.value = file
        fileName.value = file.name
        fileSize.value = `${(file.size / 1024).toFixed(2)} KB`
        showPreview.value = true // 上传文件后显示预览
        ElMessage.success('文件上传成功')
      }
    }


    const startAnalysis = async () => {
      // 验证必要参数
      if (!uploadedFile.value) {
        ElMessage.error('请先上传简历文件')
        return
      }

      if (!jobTitle.value.trim()) {
        ElMessage.error('请输入职位名称')
        return
      }

      if (!jobDescription.value.trim()) {
        ElMessage.error('请输入职位描述')
        return
      }

      analyzing.value = true

      // 创建FormData对象
      const formData = new FormData()
      formData.append('updownCV', uploadedFile.value)
      formData.append('jobPosition', jobTitle.value.trim())
      formData.append('jobDescription', jobDescription.value.trim())

      try {
        // 调用后端API进行简历匹配分析
        console.log('开始调用API，FormData内容:', {
          file: uploadedFile.value?.name,
          jobPosition: jobTitle.value.trim(),
          jobDescription: jobDescription.value.trim().substring(0, 100) + '...'
        })

        const response = await analyzeResumeMatch(formData)
        console.log('API响应:', response)

        // 检查响应格式 - 现在后端返回标准格式
        if (response && response.code === 0 && response.data) {
          updateAnalysisData(response.data)
          analyzing.value = false
          showAnalysis.value = true
          ElMessage.success('简历分析完成')

          nextTick(() => {
            const analysisSection = document.querySelector('.analysis-section')
            if (analysisSection) {
              analysisSection.scrollIntoView({ behavior: 'smooth' })
            }
            renderRadarChart()
          })
        } else {
          throw new Error(response.message || `分析失败: ${JSON.stringify(response)}`)
        }
      } catch (error) {
        analyzing.value = false
        console.error('简历分析失败:', error)
        console.error('错误详情:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status
        })

        let errorMessage = '简历分析失败，请稍后重试'
        if (error.response) {
          if (error.response.status === 504) {
            errorMessage = '分析超时，请稍后重试'
          } else if (error.response.status === 400) {
            errorMessage = '请求参数错误，请检查文件格式和内容'
          } else if (error.response.status === 500) {
            errorMessage = '服务器内部错误，请稍后重试'
          } else {
            errorMessage = `请求失败 (${error.response.status}): ${error.response.data?.message || error.message}`
          }
        } else if (error.message) {
          errorMessage = error.message
        }

        ElMessage.error(errorMessage)
      }
    }

    // 处理API响应数据，更新分析结果
    const updateAnalysisData = (apiData) => {
      try {
        console.log('处理API数据:', apiData)

        // 修正数据路径：检查不同的可能数据结构
        let outputs = null
        let analyze = null

        // 情况1: apiData.data.outputs (后端返回的标准格式)
        if (apiData.data && apiData.data.outputs) {
          outputs = apiData.data.outputs
          console.log('使用 apiData.data.outputs 路径')
        }
        // 情况2: apiData.outputs (直接格式)
        else if (apiData.outputs) {
          outputs = apiData.outputs
          console.log('使用 apiData.outputs 路径')
        }

        if (outputs && outputs.analyze) {
          analyze = outputs.analyze
          console.log('找到分析数据:', analyze)

          // 1. 更新简历内容（用于预览）
          if (outputs.resume && outputs.resume.length > 0) {
            resumeContent.value = outputs.resume[0]
            console.log('更新简历内容')
          }

          // 2. 更新基本信息
          if (analyze.basicInfo) {
            basicInfo.value = analyze.basicInfo
            console.log('更新基本信息:', basicInfo.value)
          }

          // 3. 更新总体评分
          if (analyze.overallScore) {
            scoreValue.value = analyze.overallScore.score || 85
            matchProgress.value = scoreValue.value
            analysisData.value.overallScore = analyze.overallScore
          }

          // 4. 更新能力雷达图数据
          if (analyze.abilityRadar) {
            analysisData.value.abilityRadar = analyze.abilityRadar
          }

          // 5. 更新ATS分析数据
          if (analyze.atsAnalysis) {
            analysisData.value.atsAnalysis = analyze.atsAnalysis
          }

          // 6. 更新硬技能分析
          if (analyze.hardSkills) {
            analysisData.value.hardSkills = analyze.hardSkills
          }

          // 7. 更新软技能分析
          if (analyze.softSkills) {
            analysisData.value.softSkills = analyze.softSkills
          }

          // 8. 更新改进建议
          if (analyze.suggestions) {
            analysisData.value.suggestions = analyze.suggestions
          }

          // 9. 更新优势和劣势
          if (analyze.strengths) {
            analysisData.value.strengths = analyze.strengths
          }
          if (analyze.weaknesses) {
            analysisData.value.weaknesses = analyze.weaknesses
          }

          // 10. 更新洞察报告
          if (analyze.insights) {
            analysisData.value.insights = analyze.insights
          }

          console.log('所有数据更新完成')
        } else {
          console.error('未找到分析数据，数据结构:', apiData)
          console.error('检查路径: apiData.data.outputs =', apiData.data?.outputs)
          console.error('检查路径: apiData.outputs =', apiData.outputs)
          ElMessage.warning('API响应数据格式不正确')
        }

        console.log('API数据处理完成:', analysisData.value)
      } catch (error) {
        console.error('处理API数据时出错:', error)
        ElMessage.warning('数据处理出现问题，使用默认分析数据')
      }
    }

    // 渲染雷达图 - 仅使用真实API数据
    const renderRadarChart = () => {
      if (!radarChart.value) return

      // 检查是否有真实的雷达图数据
      const radarData = analysisData.value.abilityRadar
      if (!radarData || !radarData.labels || radarData.labels.length === 0 ||
          !radarData.currentLevel || radarData.currentLevel.length === 0 ||
          !radarData.requiredLevel || radarData.requiredLevel.length === 0) {
        console.log('没有完整的雷达图数据，跳过渲染')
        return
      }

      // 动态导入Chart.js
      import('chart.js/auto').then((Chart) => {
        const ctx = radarChart.value.getContext('2d')

        // 如果已存在图表，先销毁
        if (chart) {
          chart.destroy()
        }

        chart = new Chart.default(ctx, {
          type: 'radar',
          data: {
            labels: radarData.labels,
            datasets: [{
              label: '当前水平',
              data: radarData.currentLevel,
              backgroundColor: 'rgba(44, 111, 187, 0.2)',
              borderColor: 'rgba(44, 111, 187, 1)',
              pointBackgroundColor: 'rgba(44, 111, 187, 1)',
              pointBorderColor: '#fff',
              pointHoverBackgroundColor: '#fff',
              pointHoverBorderColor: 'rgba(44, 111, 187, 1)'
            }, {
              label: '岗位要求',
              data: radarData.requiredLevel,
              backgroundColor: 'rgba(52, 152, 219, 0.2)',
              borderColor: 'rgba(52, 152, 219, 1)',
              pointBackgroundColor: 'rgba(52, 152, 219, 1)',
              pointBorderColor: '#fff',
              pointHoverBackgroundColor: '#fff',
              pointHoverBorderColor: 'rgba(52, 152, 219, 1)'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              r: {
                angleLines: {
                  display: true
                },
                suggestedMin: 0,
                suggestedMax: 100, // 调整为100，匹配API数据范围
                pointLabels: {
                  font: {
                    size: 12
                  }
                },
                ticks: {
                  display: false
                }
              }
            },
            plugins: {
              legend: {
                position: 'bottom',
                labels: {
                  padding: 20
                }
              }
            }
          }
        })
      }).catch(error => {
        console.error('Chart.js加载失败:', error)
      })
    }

    const resetAnalysis = () => {
      showAnalysis.value = false
      fileName.value = ''
      fileSize.value = '0 KB'
      showPreview.value = false
      uploadedFile.value = null
      resumeContent.value = ''
      basicInfo.value = {}

      // 重置分析数据为空结构，清除所有模拟数据
      analysisData.value = {
        overallScore: { score: 0, level: '', description: '' },
        abilityRadar: {
          labels: [],
          currentLevel: [],
          requiredLevel: []
        },
        strengths: [],
        weaknesses: [],
        atsAnalysis: [],
        hardSkills: [],
        softSkills: [],
        suggestions: [],
        insights: {
          title: '',
          content: []
        }
      }

      // 重置分数显示
      scoreValue.value = 0
      matchProgress.value = 0

      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const togglePreviewSize = () => {
      isFullPreview.value = !isFullPreview.value
    }

    const hidePreview = () => {
      showPreview.value = false
    }


    return {
      dropArea,
      fileInput,
      fileName,
      fileSize,
      uploadedFile,
      resumeContent,
      basicInfo,
      jobTitle,
      jobDescription,
      analyzing,
      showAnalysis,
      showPreview,
      isFullPreview,
      scoreValue,
      matchProgress,
      radarChart,
      analysisData,
      triggerFileInput,
      handleFileChange,
      handleDragOver,
      handleDragEnter,
      handleDragLeave,
      handleDrop,
      startAnalysis,
      resetAnalysis,
      togglePreviewSize,
      hidePreview,
      updateAnalysisData,
      renderRadarChart
    }
  }
}
</script>

<style scoped>
:root {
  --primary: #2c6fbb;
  --primary-light: #e6f0fa;
  --secondary: #3498db;
  --accent: #1abc9c;
  --warning: #e74c3c;
  --success: #27ae60;
  --gray-light: #f8f9fa;
  --gray: #e9ecef;
  --gray-dark: #6c757d;
  --text: #343a40;
  --shadow: rgba(0, 0, 0, 0.1);
  --border: #dee2e6;
  --light: #f8f9fa;
  --dark: #212529;
}

.resume-analysis {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
  color: #333;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  min-height: 100vh;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

.row {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -15px;
}

.col-md-4, .col-md-6, .col-md-8, .col-12 {
  padding: 0 15px;
}

.col-md-4 { flex: 0 0 33.333333%; max-width: 33.333333%; }
.col-md-6 { flex: 0 0 50%; max-width: 50%; }
.col-md-8 { flex: 0 0 66.666667%; max-width: 66.666667%; }
.col-12 { flex: 0 0 100%; max-width: 100%; }

.mx-auto { margin-left: auto; margin-right: auto; }
.text-center { text-align: center; }
.mb-3 { margin-bottom: 1rem; }
.mb-4 { margin-bottom: 1.5rem; }
.mb-5 { margin-bottom: 3rem; }
.mt-2 { margin-top: 0.5rem; }
.mt-3 { margin-top: 1rem; }
.mt-4 { margin-top: 1.5rem; }
.me-1 { margin-right: 0.25rem; }
.me-2 { margin-right: 0.5rem; }

.display-5 {
  font-size: 3rem;
  font-weight: 300;
  line-height: 1.2;
}

.fw-bold { font-weight: 700; }
.lead { font-size: 1.25rem; font-weight: 300; }
.text-muted { color: #6c757d; }

.card {
  border-radius: 15px;
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
  border: none;
  transition: transform 0.3s;
  margin-bottom: 25px;
  background-color: rgba(255, 255, 255, 0.9);
}

.card:hover {
  transform: translateY(-5px);
}

.card-header {
  background: linear-gradient(to right, var(--primary), var(--secondary));
  color: white;
  border-radius: 15px 15px 0 0 !important;
  font-weight: 600;
  padding: 15px 20px;
}

.card-body {
  padding: 20px;
}

.card-footer {
  border-top: 1px solid #e3f2fd;
  padding: 20px;
  border-radius: 0 0 15px 15px !important;
}

.bg-light {
  background-color: #f8f9fa !important;
}

.upload-area {
  border: 2px dashed #ccc;
  border-radius: 10px;
  padding: 40px 20px;
  text-align: center;
  background: #f9f9f9;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: var(--primary);
  background: #f0f5ff;
}

.upload-area i {
  font-size: 3rem;
  color: var(--primary);
  margin-bottom: 15px;
}

.btn {
  padding: 10px 25px;
  font-weight: 600;
  border-radius: 30px;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(to right, var(--primary), var(--secondary));
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(to right, #3a56e0, #3832b8);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-outline-primary {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.btn-lg {
  padding: 12px 30px;
  font-size: 1.1rem;
}

.px-5 {
  padding-left: 3rem;
  padding-right: 3rem;
}

.form-control {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 0.2rem rgba(67, 97, 238, 0.25);
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.d-grid {
  display: grid;
}

.d-flex {
  display: flex;
}

.justify-content-between {
  justify-content: space-between;
}

.align-items-center {
  align-items: center;
}

.d-none {
  display: none;
}

/* 简历预览卡片 */
.resume-preview-card {
  border: 2px solid #e3f2fd;
  transition: all 0.3s ease;
}

.resume-preview-card:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
}

/* 简历内容样式 */
.resume-text {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  margin: 0;
  max-height: 600px;
  overflow-y: auto;
}

.resume-full-content {
  margin-top: 20px;
}

.preview-controls .btn {
  border-radius: 20px;
  font-size: 0.85rem;
}

.resume-preview {
  background: #fafafa;
  border-radius: 10px;
  padding: 25px;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);
  max-height: 500px;
  overflow-y: auto;
  transition: max-height 0.3s ease;
}

.resume-preview.full-preview {
  max-height: none;
}

.resume-header-section {
  border-bottom: 2px solid var(--primary);
  padding-bottom: 20px;
  margin-bottom: 25px;
}

.resume-header-section h4 {
  color: var(--primary);
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 15px;
  text-align: center;
}

.contact-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

.contact-column {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  color: #555;
  font-size: 0.95rem;
}

.contact-item i {
  color: var(--primary);
  width: 20px;
}

.resume-section {
  margin-bottom: 25px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  border-left: 4px solid var(--primary);
}

.resume-section h5 {
  color: var(--primary);
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.resume-section h5 i {
  color: var(--secondary);
}

.skills-list {
  list-style: none;
  padding: 0;
}

.skills-list li {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  margin-bottom: 8px;
  padding: 10px 15px;
  border-radius: 6px;
  border-left: 3px solid var(--success);
  font-size: 0.95rem;
}

.experience-item {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.experience-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.experience-header strong {
  color: var(--dark);
  font-size: 1.1rem;
}

.experience-date {
  color: var(--primary);
  font-weight: 600;
  font-size: 0.9rem;
  margin-left: auto;
}

.experience-item ul {
  margin: 0;
  padding-left: 20px;
}

.experience-item li {
  margin-bottom: 6px;
  color: #555;
  line-height: 1.5;
}

.education-item {
  background: #f0f5ff;
  padding: 12px 15px;
  margin-bottom: 10px;
  border-radius: 6px;
  border-left: 3px solid var(--info);
}

.education-item strong {
  color: var(--primary);
}

/* 报告头部样式 */
.report-header {
  text-align: center;
  padding: 30px 0;
  margin-bottom: 30px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  border-radius: 10px;
  color: white;
  position: relative;
  overflow: hidden;
}

.report-header::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 70%);
}

.header-content {
  position: relative;
  z-index: 2;
  padding: 20px;
}

.header-icon {
  font-size: 48px;
  margin-bottom: 20px;
  color: white;
  background: rgba(255, 255, 255, 0.2);
  width: 100px;
  height: 100px;
  line-height: 100px;
  border-radius: 50%;
  display: inline-flex;
  justify-content: center;
  align-items: center;
}

.header-content h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 15px;
}

.subtitle {
  font-size: 1.2rem;
  font-weight: 300;
  max-width: 700px;
  margin: 0 auto;
  opacity: 0.9;
}

/* 分隔线样式 */
.divider {
  height: 1px;
  background: linear-gradient(to right, transparent, var(--gray-dark), transparent);
  margin: 40px 0;
}

/* 模块标题样式 */
.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary);
}

.section-icon {
  background-color: var(--primary);
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 18px;
}

.section-title h2 {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--primary);
}

.analysis-section {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  margin-top: 30px;
}

/* 匹配度评分区 */
.score-section {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
}

.score-card {
  flex: 1;
  min-width: 300px;
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 5px 15px var(--shadow);
  border-top: 4px solid var(--primary);
}

.score-chart {
  flex: 1;
  min-width: 300px;
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 5px 15px var(--shadow);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.overall-score {
  text-align: center;
  margin-bottom: 25px;
}

.score-value {
  font-size: 4rem;
  font-weight: 700;
  color: var(--primary);
  line-height: 1;
  margin-bottom: 10px;
}

.score-label {
  font-size: 1.2rem;
  color: var(--gray-dark);
  margin-bottom: 20px;
}

.progress-container {
  height: 20px;
  background-color: var(--gray);
  border-radius: 10px;
  margin: 20px 0;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--primary), var(--secondary));
  border-radius: 10px;
  width: 0;
  transition: width 1.5s ease-in-out;
}

.strengths, .weaknesses {
  margin-top: 25px;
}

.strengths h3, .weaknesses h3 {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  font-size: 1.2rem;
}

.strengths h3 {
  color: var(--success);
}

.weaknesses h3 {
  color: var(--warning);
}

.strengths ul, .weaknesses ul {
  padding-left: 25px;
}

.strengths li {
  color: var(--success);
  margin-bottom: 5px;
}

.weaknesses li {
  color: var(--warning);
  margin-bottom: 5px;
}

.chart-container {
  width: 100%;
  max-width: 400px;
  height: 300px;
}

.result-section {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  margin-top: 30px;
}

.match-score {
  font-size: 4rem;
  font-weight: 700;
  color: var(--primary);
  text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
}

/* 表格区域 */
.analysis-table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.analysis-table th {
  background-color: var(--primary);
  color: white;
  padding: 15px;
  text-align: left;
  font-weight: 500;
}

.analysis-table td {
  padding: 15px;
  border-bottom: 1px solid var(--border);
}

.analysis-table tr:nth-child(even) {
  background-color: var(--gray-light);
}

.status-pass {
  color: var(--success);
  font-weight: 600;
}

.status-fail {
  color: var(--warning);
  font-weight: 600;
}

.status-missing {
  color: var(--gray-dark);
  font-weight: 600;
}

.status-partial {
  color: var(--secondary);
  font-weight: 600;
}

/* 建议列表区 */
.suggestion-list {
  list-style-type: none;
  margin: 20px 0;
}

.suggestion-item {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 3px 10px var(--shadow);
  display: flex;
  align-items: center;
  transition: transform 0.3s, box-shadow 0.3s;
  border-left: 4px solid var(--accent);
}

.suggestion-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.suggestion-icon {
  width: 50px;
  height: 50px;
  min-width: 50px;
  background-color: var(--primary-light);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 20px;
  color: var(--primary);
}

.suggestion-content {
  flex-grow: 1;
}

.suggestion-title {
  font-weight: 600;
  color: var(--primary);
  margin-bottom: 5px;
  font-size: 1.2rem;
}

/* 洞察区 */
.insights {
  background-color: var(--primary-light);
  border-left: 4px solid var(--primary);
  padding: 25px;
  border-radius: 0 8px 8px 0;
  margin: 30px 0;
}

.insight-title {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: var(--primary);
  display: flex;
  align-items: center;
}

.insight-title i {
  margin-right: 10px;
}

.insight-content p {
  margin-bottom: 15px;
}

.highlight {
  background-color: rgba(44, 111, 187, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 600;
  color: var(--primary);
  border-bottom: 2px solid var(--primary);
}

.progress {
  height: 12px;
  border-radius: 10px;
  margin: 10px 0;
  background-color: #e9ecef;
  overflow: hidden;
}

.progress-bar {
  background-color: var(--primary);
  height: 100%;
  transition: width 0.6s ease;
}

.status-card {
  text-align: center;
  padding: 20px;
  border-radius: 15px;
  background: white;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}

.status-icon {
  font-size: 2.5rem;
  color: var(--primary);
  margin-bottom: 15px;
}

.text-success { color: #28a745; }
.text-primary { color: var(--primary); }
.text-info { color: #17a2b8; }
.text-warning { color: #ffc107; }

.section-title {
  position: relative;
  padding-bottom: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid var(--primary);
  color: var(--primary);
}

.skill-container {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
}

.skill-badge {
  background: linear-gradient(to right, var(--success), #2a9d8f);
  color: white;
  font-weight: 500;
  padding: 8px 15px;
  border-radius: 20px;
  margin: 5px;
  display: inline-block;
}

.recommendation {
  background: #e3f2fd;
  border-left: 4px solid var(--primary);
  padding: 15px;
  border-radius: 0 8px 8px 0;
  margin: 15px 0;
}

.recommendation h5 {
  margin-bottom: 10px;
  font-weight: 600;
}

.recommendation ul {
  margin-bottom: 0;
  padding-left: 20px;
}

.recommendation li {
  margin-bottom: 8px;
}

.flex-wrap {
  flex-wrap: wrap;
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.section {
  animation: fadeIn 0.5s ease-out forwards;
}

.section:nth-child(1) { animation-delay: 0.1s; }
.section:nth-child(2) { animation-delay: 0.2s; }
.section:nth-child(3) { animation-delay: 0.3s; }
.section:nth-child(4) { animation-delay: 0.4s; }
.section:nth-child(5) { animation-delay: 0.5s; }
.section:nth-child(6) { animation-delay: 0.6s; }

/* 响应式设计 */
@media (max-width: 768px) {
  .col-md-4, .col-md-6, .col-md-8, .col-12 {
    flex: 0 0 100%;
    max-width: 100%;
  }

  .display-5 {
    font-size: 2rem;
  }

  .header-content h1 {
    font-size: 1.8rem;
  }

  .subtitle {
    font-size: 1rem;
  }

  .section-title h2 {
    font-size: 1.4rem;
  }

  .score-section {
    flex-direction: column;
  }

  .score-value {
    font-size: 3rem;
  }

  .analysis-table {
    display: block;
    overflow-x: auto;
  }

  .match-score {
    font-size: 3rem;
  }

  .skill-badge {
    margin: 2px;
    padding: 6px 12px;
    font-size: 0.9rem;
  }

  .contact-info-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .resume-preview {
    padding: 15px;
  }

  .resume-section {
    padding: 15px;
    margin-bottom: 20px;
  }

  .experience-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .experience-date {
    margin-left: 0;
  }

  .preview-controls {
    display: flex;
    gap: 5px;
  }

  .preview-controls .btn {
    font-size: 0.8rem;
    padding: 4px 8px;
  }
}
</style>
