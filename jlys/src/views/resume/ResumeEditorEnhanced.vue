<template>
  <div class="resume-editor-enhanced">
    <!-- 顶部导航栏 -->
    <div class="editor-header">
      <div class="header-left">
        <el-button @click="goBack" text class="back-btn">
          <el-icon>
            <ArrowLeft />
          </el-icon>
          返回
        </el-button>
        <div class="divider"></div>
        <el-input v-model="resumeForm.title" placeholder="请输入简历标题" class="title-input" @blur="autoSave" />
      </div>

      <div class="header-center">
        <div class="view-tabs">
          <div class="tab-item" :class="{ active: activeView === 'edit' }" @click="setActiveView('edit')">
            <el-icon>
              <Edit />
            </el-icon>
            <span>编辑</span>
          </div>
          <div class="tab-item" :class="{ active: activeView === 'preview' }" @click="setActiveView('preview')">
            <el-icon>
              <ViewIcon />
            </el-icon>
            <span>预览</span>
          </div>
        </div>
      </div>

      <div class="header-right">
        <el-button @click="togglePreviewPanel" type="link" class="preview-btn">
          <el-icon>
            <ViewIcon />
          </el-icon>
          实时预览
        </el-button>
        <!-- 顶部导航栏中的选择模板按钮 -->
        <el-button @click="showTemplateDialog" type="link" class="template-btn">
          <el-icon>
            <Grid />
          </el-icon>
          选择模板
        </el-button>
        <el-button @click="showAIGenerateDialog" type="success" class="ai-generate-btn">
          <el-icon>
            <MagicStick />
          </el-icon>
          AI生成简历
        </el-button>
        <el-button @click="optimizeResume" :loading="optimizing" class="ai-btn">
          <el-icon>
            <MagicStick />
          </el-icon>
          {{ optimizing ? 'AI优化中...' : 'AI优化' }}
        </el-button>
        <el-button @click="saveResume" type="primary" :loading="saving" class="save-btn">
          {{ saving ? '保存中...' : '保存' }}
        </el-button>
      </div>
    </div>

    <!-- 主体内容区域 -->
    <div class="editor-container">
      <!-- 左侧导航面板 -->
      <div class="sidebar" v-show="activeView === 'edit'" :class="{ collapsed: leftPanelCollapsed }">
        <!-- 面板头部 -->
        <div class="sidebar-header">
          <div class="sidebar-title" v-show="!leftPanelCollapsed">
            <el-icon>
              <MenuIcon />
            </el-icon>
            <span>简历模块</span>
          </div>
          <el-button @click="toggleLeftPanel" text class="collapse-btn">
            <el-icon>
              <component :is="leftPanelCollapsed ? 'Expand' : 'Fold'" />
            </el-icon>
          </el-button>
        </div>

        <!-- 进度条 -->
        <div class="progress-section" v-show="!leftPanelCollapsed">
          <div class="progress-header">
            <span class="progress-label">完成度</span>
            <span class="progress-value">{{ overallProgress }}%</span>
          </div>
          <el-progress :percentage="overallProgress" :color="getProgressColor(overallProgress)" :stroke-width="8"
            :show-text="false" />
        </div>

        <!-- 侧边栏内容 -->
        <div class="sidebar-content" v-show="!leftPanelCollapsed">
          <!-- 模块列表 -->
          <div class="module-list">
            <div v-for="module in modules" :key="module.key" class="module-item" :class="{
              'active': activeModule === module.key,
              'disabled': !module.enabled && !module.required
            }" @click="setActiveModule(module.key)">
              <div class="module-content">
                <div class="module-left">
                  <el-icon class="module-icon">
                    <component :is="module.icon" />
                  </el-icon>
                  <div class="module-info">
                    <div class="module-name">{{ module.name }}</div>
                    <div class="module-status">
                      <span class="completion-rate">{{ getModuleCompleteness(module.key) }}%</span>
                      <el-tag v-if="module.required" size="small" type="danger">必填</el-tag>
                    </div>
                  </div>
                </div>
                <div class="module-right">
                  <el-switch v-if="!module.required" v-model="module.enabled" size="small"
                    @change="handleModuleToggle(module)" />
                  <div class="module-indicator">
                    <div class="indicator-dot" :class="getModuleStatusClass(module.key)"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 收起状态的图标列表 -->
        <div class="sidebar-collapsed" v-show="leftPanelCollapsed">
          <div v-for="module in modules" :key="module.key" class="collapsed-item"
            :class="{ 'active': activeModule === module.key }" @click="setActiveModule(module.key)"
            :title="module.name">
            <el-icon>
              <component :is="module.icon" />
            </el-icon>
            <div class="indicator-dot" :class="getModuleStatusClass(module.key)"></div>
          </div>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="main-content" :class="{
        'with-preview': showPreviewPanel && activeView === 'edit',
        'with-template': showTemplatePanel && activeView === 'edit'
      }">
        <!-- 编辑区域 -->
        <div class="content-area" v-show="activeView === 'edit'" :style="{
          width: (showPreviewPanel || showTemplatePanel) ? `${editAreaWidth}px` : '100%'
        }">
          <!-- 模块头部 -->
          <div class="content-header">
            <div class="header-info">
              <el-icon class="header-icon">
                <component :is="getCurrentModule()?.icon" />
              </el-icon>
              <div class="header-text">
                <h2 class="header-title">{{ getCurrentModule()?.name }}</h2>
                <p class="header-desc">{{ getCurrentModule()?.description }}</p>
              </div>
            </div>
            <div class="header-actions">
              <el-button v-if="getCurrentModule()?.key !== 'basicInfo'" @click="addModuleItem" type="primary"
                size="small">
                <el-icon>
                  <Plus />
                </el-icon>
                添加{{ getCurrentModule()?.itemName || '项目' }}
              </el-button>
            </div>
          </div>

          <!-- 模块内容 -->
          <div class="content-body">
            <component :is="getCurrentModuleComponent()" v-model="resumeForm[activeModule]"
              @change="handleModuleChange" />
          </div>
        </div>

        <!-- 拖拽分隔条 -->
        <div class="resize-handle" v-show="activeView === 'edit' && (showPreviewPanel || showTemplatePanel)"
          @mousedown="startResize">
          <div class="resize-line"></div>
        </div>

        <!-- 预览面板 -->
        <div class="preview-panel" v-show="showPreviewPanel && activeView === 'edit'"
          :style="{ width: `${previewAreaWidth}px` }">
          <!-- 预览工具栏 -->
          <div class="preview-toolbar">
            <div class="toolbar-left">
              <span class="toolbar-title">实时预览</span>
            </div>
            <!-- 移除右侧切换按钮，只保留关闭按钮 -->
            <div class="toolbar-right">
              <el-button @click="togglePreviewPanel" text size="small">
                <el-icon>
                  <ArrowRight />
                </el-icon>
              </el-button>
            </div>
          </div>

          <!-- 预览内容 -->
          <div class="preview-panel-content">
            <div class="preview-wrapper" :style="{ transform: `scale(${previewScale})` }">
              <ResumePreview :resumeData="filteredResumeData" :template="resumeForm.template" :isMobile="false" />
            </div>
          </div>
        </div>

        <!-- 模板面板 -->
        <div class="template-panel" v-show="showTemplatePanel" :style="{ width: `${templateAreaWidth}px` }">
          <!--            v-show="showTemplatePanel && activeView === 'edit'"-->
          <!-- 模板工具栏 -->
          <div class="template-toolbar">
            <div class="toolbar-left">
              <span class="toolbar-title">选择模板</span>
            </div>
            <div class="toolbar-right">
              <el-button @click="toggleTemplatePanel" text size="small">
                <el-icon>
                  <ArrowRight />
                </el-icon>
              </el-button>
            </div>
          </div>

          <!-- 模板分类 -->
          <div class="template-categories">
            <div v-for="category in templateCategories" :key="category.key" class="category-item"
              :class="{ active: selectedCategory === category.key }" @click="selectedCategory = category.key">
              <el-icon>
                <component :is="category.icon" />
              </el-icon>
              <span>{{ category.name }}</span>
            </div>
          </div>

          <!-- 模板列表 -->
          <div class="template-list">
            <div v-for="template in filteredTemplates" :key="template.id" class="template-item"
              :class="{ selected: Number(selectedTemplate) === Number(template.id) }"
              @click="selectAndApplyTemplate(template.id)">
              <div class="template-preview">
                <img :src="template.preview" :alt="template.name" />
                <div class="template-overlay">
                  <el-icon>
                    <ViewIcon />
                  </el-icon>
                </div>
              </div>
              <div class="template-info">
                <h4 class="template-name">{{ template.name }}</h4>
                <p class="template-desc">{{ template.description }}</p>
                <div class="template-tags">
                  <el-tag v-for="tag in template.tags" :key="tag" size="small" type="info">
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 全屏预览 -->
        <div class="full-preview" v-show="activeView === 'preview'">
          <ResumePreview :resumeData="resumeForm" :template="resumeForm.template" :isMobile="false" />
        </div>
      </div>
    </div>

    <!-- AI生成简历对话框 -->
<!--    <el-dialog v-model="showAIDialog" title="AI生成简历" width="600px" :close-on-click-modal="false">
      <div class="ai-generate-content">
        <div class="ai-form">
          <el-form :model="aiForm" label-width="100px">
            <el-form-item label="用户信息">
              <el-input v-model="aiForm.userInfo" type="textarea" :rows="4"
                placeholder="请描述您的个人信息，包括：姓名、年龄、学历、工作经验、技能特长等" />
            </el-form-item>
            <el-form-item label="目标职位">
              <el-input v-model="aiForm.jobTitle" placeholder="请输入目标职位，如：Java开发工程师、产品经理等（可选）" />
            </el-form-item>
          </el-form>
        </div>

        <div v-if="aiGenerateResults && aiGenerateResults.length" class="ai-result">
          <div class="result-header">
            <el-icon class="success-icon">
              <CircleCheck />
            </el-icon>
            <h4>AI简历生成成功！</h4>
            <p>共生成 {{ aiGenerateResults.length }} 份简历，您可以查看或下载</p>
          </div>
          <div class="result-links">
            <div class="link-card" v-for="(item, idx) in aiGenerateResults" :key="idx"
              style="flex-direction: row; gap: 32px; align-items: stretch;">
              <div style="display: flex; flex-direction: column; flex: 1;">
                <div class="link-icon">
                  <el-icon>
                    <Document />
                  </el-icon>
                </div>
                <div class="link-content">
                  <h5>Word文档（第{{ idx + 1 }}份）</h5>
                  <p>可编辑的Word格式简历</p>
                </div>
                <div class="link-actions">
                  <el-button type="primary" size="small" @click="openLink(item.word_url)">
                    <el-icon>
                      <View />
                    </el-icon>
                    预览
                  </el-button>
                  <el-button type="success" size="small" @click="downloadFile(item.word_url, `AI简历${idx + 1}.docx`)">
                    <el-icon>
                      <Download />
                    </el-icon>
                    下载
                  </el-button>
                </div>
              </div>
              <div style="display: flex; flex-direction: column; flex: 1;">
                <div class="link-icon">
                  <el-icon>
                    <Picture />
                  </el-icon>
                </div>
                <div class="link-content">
                  <h5>简历图片（第{{ idx + 1 }}份）</h5>
                  <p>高清简历预览图片</p>
                </div>
                <div class="link-actions">
                  <el-button type="primary" size="small" @click="openLink(item.img_url)">
                    <el-icon>
                      <View />
                    </el-icon>
                    预览
                  </el-button>
                  <el-button type="success" size="small" @click="downloadFile(item.img_url, `AI简历${idx + 1}.png`)">
                    <el-icon>
                      <Download />
                    </el-icon>
                    下载
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAIDialog = false">关闭</el-button>
          <el-button v-if="!aiGenerateResults.length" @click="handleGenerateResumeWithAI" type="primary"
            :loading="aiGenerating">
            {{ aiGenerating ? '生成中...' : '生成简历' }}
          </el-button>
        </div>
      </template>
    </el-dialog>-->

    <!-- AI优化建议弹窗 -->
    <el-dialog v-model="showOptimizeDialog" title="AI优化建议" width="700px" :close-on-click-modal="false">
      <div class="optimize-dialog-content">
        <el-alert title="AI分析完成" type="success" :closable="false" show-icon>
          基于您的简历内容，AI为您生成了以下优化建议，请选择是否采纳：
        </el-alert>

        <div class="optimize-sections" v-if="optimizeSuggestions.length > 0">
          <div v-for="(section, index) in optimizeSuggestions" :key="index" class="optimize-section">
            <div class="section-header">
              <h4>{{ section.title }}</h4>
              <el-switch v-model="section.selected" active-text="采纳" inactive-text="跳过" />
            </div>

            <div class="content-comparison">
              <div class="original-content">
                <h5>原内容：</h5>
                <p>{{ section.original || '暂无内容' }}</p>
              </div>
              <div class="optimized-content">
                <h5>优化后：</h5>
                <p>{{ section.optimized }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="loading-content" v-if="optimizing">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          <p>AI正在分析您的简历内容...</p>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showOptimizeDialog = false">取消</el-button>
          <el-button type="primary" @click="applyOptimizations" :disabled="!hasSelectedOptimizations">
            应用选中的优化
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 模板选择对话框 -->
    <el-dialog v-model="templateDialogVisible" title="选择简历模板" width="800px" :close-on-click-modal="false">
      <TemplateSelectionDialog v-model="templateDialogVisible" :current-template="resumeForm.template"
        :templates="resumeTemplates" @template-selected="handleTemplateSelected" />
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, Edit, View as ViewIcon, MagicStick, Monitor, Iphone,
  User, Briefcase, School, FolderOpened, Star, More, ArrowRight,
  Menu as MenuIcon, Expand, Fold, Plus, Grid, Brush, Reading,
  Document, Picture, CircleCheck, View, Download, Loading
} from '@element-plus/icons-vue'

// 导入模块组件
import ResumeBasicInfoEnhanced from '@/components/resume/ResumeBasicInfoEnhanced.vue'
import ResumeWorkExperience from '@/components/resume/ResumeWorkExperience.vue'
import ResumeEducation from '@/components/resume/ResumeEducation.vue'
import ResumeProjects from '@/components/resume/ResumeProjects.vue'
import ResumeSkills from '@/components/resume/ResumeSkills.vue'
import ResumeOthers from '@/components/resume/ResumeOthers.vue'
import ResumePreview from '@/components/resume/ResumePreview.vue'
import TemplateSelectionDialog from '@/components/resume/TemplateSelectionDialog.vue'

// 只保留必要的接口导入
import { getResume, createResume, updateResume, optimizeResumeWithAI, generateResumeWithAI, getResumeTemplates } from '@/api/resume'

export default {
  name: 'ResumeEditorEnhanced',
  components: {
    ArrowLeft, Edit, ViewIcon, MagicStick, Monitor, Iphone,
    User, Briefcase, School, FolderOpened, Star, More, ArrowRight,
    MenuIcon, Expand, Fold, Plus, Grid, Brush, Reading,
    Document, Picture, CircleCheck, View, Download,
    ResumeBasicInfoEnhanced,
    ResumeWorkExperience,
    ResumeEducation,
    ResumeProjects,
    ResumeSkills,
    ResumeOthers,
    ResumePreview,
    TemplateSelectionDialog
  },
  props: {
    // 接收当前选中的模板ID
    modelValue: {
      type: String,
      default: 'modern'
    }
  },
  emits: ['update:modelValue', 'templateSelected'],
  setup() {
    const route = useRoute()
    const router = useRouter()

    // 响应式数据
    const resumeId = ref(null)
    const saving = ref(false)
    const optimizing = ref(false)
    const activeView = ref('edit') // edit | preview
    const activeModule = ref('basicInfo')
    const previewScale = ref(0.8)

    // AI生成相关状态
    const showAIDialog = ref(false)
    const aiGenerating = ref(false)
    const aiGenerateResults = ref([]) // 支持多份
    const aiForm = reactive({
      userInfo: '',
      jobTitle: ''
    })

    // AI优化相关状态
    const showOptimizeDialog = ref(false)
    const optimizeSuggestions = ref([])
    const hasSelectedOptimizations = computed(() => {
      return optimizeSuggestions.value.some(section => section.selected)
    })

    // 面板控制
    const leftPanelCollapsed = ref(false)
    const showPreviewPanel = ref(false)
    const showTemplatePanel = ref(false)
    const editAreaWidth = ref(800)
    const previewAreaWidth = ref(600)
    const templateAreaWidth = ref(400)

    // 模板相关
    const selectedTemplate = ref(null)
    const selectedCategory = ref('all')
    // 拖拽相关
    const isResizing = ref(false)
    const minEditWidth = ref(400)
    const minPreviewWidth = ref(300)
    const minTemplateWidth = ref(300)


    // 模块配置
    const modules = reactive([
      {
        key: 'basicInfo',
        name: '基本信息',
        icon: 'User',
        description: '姓名、联系方式、个人简介等基础信息',
        required: true,
        enabled: true
      },
      {
        key: 'workExperience',
        name: '工作经历',
        icon: 'Briefcase',
        description: '工作经验、职位描述、工作成果',
        itemName: '工作经历',
        required: false,
        enabled: false
      },
      {
        key: 'education',
        name: '教育背景',
        icon: 'School',
        description: '学历、专业、毕业院校、在校经历',
        itemName: '教育经历',
        required: false,
        enabled: false
      },
      {
        key: 'projects',
        name: '项目经验',
        icon: 'FolderOpened',
        description: '参与项目、项目职责、技术栈、项目成果',
        itemName: '项目',
        required: false,
        enabled: false
      },
      {
        key: 'skills',
        name: '技能专长',
        icon: 'Star',
        description: '专业技能、技能熟练度、证书认证',
        itemName: '技能',
        required: false,
        enabled: false
      },
      {
        key: 'others',
        name: '其他信息',
        icon: 'More',
        description: '获奖经历、语言能力、兴趣爱好等',
        itemName: '信息',
        required: false,
        enabled: false
      }
    ])

    // 模板分类
    const templateCategories = reactive([
      { key: 'all', name: '全部', icon: 'Grid' },  // 添加这一行
      { key: 'it', name: 'IT技术', icon: 'Monitor' },
      { key: 'professional', name: '商务专业', icon: 'Briefcase' },
      { key: 'academic', name: '学术研究', icon: 'Reading' },
      { key: 'general', name: '通用模板', icon: 'Grid' }
    ])

    // 简历模板 - 从后端动态获取
    const resumeTemplates = ref([])

    // 加载模板列表
    const loadTemplates = async () => {
      try {
        const res = await getResumeTemplates()
        if (res.code === 0 && res.data) {
          resumeTemplates.value = res.data.map(template => {
            let previewUrl = '/images/default-template-preview.svg'
            // 改成 template.templateId（注意大小写）
            if (template.templateId === 1) previewUrl = '/template/resume-it-master/resume.png'
            if (template.templateId === 2) previewUrl = '/template/resume-zh_CN/images/resume_example.jpg'
            if (template.templateId === 3) previewUrl = '/template/resume_with_photo-main/photo.jpg'

            return {
              id: template.templateId,  // 改成 template.templateId
              name: template.templateName,
              description: template.description || '暂无描述',
              preview: previewUrl,
              type: template.templateType,
              path: template.templatePath,
              category: template.category,
              tags: getTemplateTags(template)
            }
          })
        }
      } catch (error) {
        ElMessage.error('加载模板失败')
      }
    }
    // 根据模板信息生成标签
    /*const getTemplateTags = (template) => {
      const tags = []
      if (template.category === 'it') tags.push('技术')
      if (template.template_type === 'latex') tags.push('LaTeX')
      if (template.template_type === 'html') tags.push('HTML')
      if (template.template_name.includes('专业')) tags.push('专业')
      if (template.template_name.includes('简约')) tags.push('简约')
      return tags.length > 0 ? tags : ['通用']
    }*/

    // 从后端动态获取模

    onMounted(async () => {
      const id = route.params.id
      await loadTemplates()  // 确保这一行存在
      if (id && id !== 'new') {
        resumeId.value = parseInt(id)
        await loadResume()
      }
      window.addEventListener('resize', handleWindowResize)
    })
    const getTemplateTags = (template) => {
      const tags = []
      const name = template.templateName || ''  // 改成 template.templateName

      if (template.templateType === 'latex') tags.push('LaTeX')  // 改成 template.templateType
      if (template.templateType === 'html') tags.push('HTML')
      if (template.category === 'it') tags.push('IT')
      if (template.category === 'general') tags.push('通用')
      if (name.includes('照片')) tags.push('带照片')
      if (name.includes('专业')) tags.push('专业')
      if (name.includes('中文')) tags.push('中文')

      return tags.length ? tags : ['通用']
    }
    // 模板对话框
    const templateDialogVisible = ref(false)

    // 简历表单数据
    const resumeForm = reactive({
      title: '我的简历',
      template: 'template1', // 默认使用template1
      templateId: null, // 模板ID，可以为null
      basicInfo: {
        name: '',
        phone: '',
        email: '',
        address: '',
        avatar: '',
        summary: '',
        position: '',
        workYears: 0
      },
      workExperience: [],
      education: [],
      projects: [],
      skills: [],
      others: {
        certificates: [],
        awards: [],
        hobbies: []
      }
    })

    const toggleTemplatePanel = () => {
      showTemplatePanel.value = !showTemplatePanel.value
      if (showTemplatePanel.value) {
        showPreviewPanel.value = false
        nextTick(() => {
          initializePanelWidths()
        })
      }
    }
    // 自动保存定时器
    let autoSaveTimer = null

    // 页面初始化
    onMounted(async () => {
      const id = route.params.id
      await loadTemplates() // 先加载模板
      if (id && id !== 'new') {
        resumeId.value = parseInt(id)
        await loadResume()
      }
      // 监听窗口大小变化
      window.addEventListener('resize', handleWindowResize)
    })

    // 清理事件监听器
    onUnmounted(() => {
      window.removeEventListener('resize', handleWindowResize)
      if (autoSaveTimer) {
        clearTimeout(autoSaveTimer)
      }
    })

    // 加载简历数据
    const loadResume = async () => {
      if (!resumeId.value) return

      try {
        const response = await getResume(resumeId.value)
        console.log('API返回的原始数据:', response)

        // 检查响应结构
        if (response.code !== 0) {
          throw new Error(response.message || '获取简历失败')
        }

        const data = response.data // 获取实际的数据部分
        console.log('解析后的简历数据:', data)

        // 将后端数据结构转换为前端resumeForm结构
        resumeForm.title = data.name || ''
        resumeForm.template = data.template || 'template1'
        resumeForm.templateId = data.templateId || null // 支持null值
        console.log('加载的简历数据:', data)
        console.log('设置的模板:', resumeForm.template, '模板ID:', resumeForm.templateId)
        console.log('基本信息:', data.fullName, data.phone, data.email, data.position)

        // 基本信息
        resumeForm.basicInfo = {
          name: data.fullName || '',
          phone: data.phone || '',
          email: data.email || '',
          address: data.location || '',
          summary: data.profile || '',
          position: data.position || '',
          workYears: data.workYears || 0
        }
        console.log('转换后的基本信息:', resumeForm.basicInfo)

        // 工作经历
        resumeForm.workExperience = (data.workExperiences || []).map(work => ({
          company: work.company || '',
          position: work.position || '',
          startDate: work.startDate || '',
          endDate: work.endDate || '',
          responsibilities: work.responsibility || '',
          achievements: work.achievement || ''
        }))

        // 教育经历
        resumeForm.education = (data.educations || []).map(edu => ({
          school: edu.school || '',
          major: edu.major || '',
          degree: edu.degree || '',
          startDate: edu.startDate || '',
          endDate: edu.endDate || '',
          description: edu.description || ''
        }))

        // 项目经历
        resumeForm.projects = (data.projectExperiences || []).map(project => ({
          name: project.projectName || '',
          role: project.role || '',
          startDate: project.startDate || '',
          endDate: project.endDate || '',
          description: project.description || ''
        }))

        // 技能
        resumeForm.skills = (data.skills || []).map(skill => ({
          name: skill.skillName || '',
          level: skill.level || skill.proficiency || 1,
          description: skill.description || ''
        }))

        // 其他信息
        resumeForm.others = {
          certificates: [],
          awards: [],
          hobbies: []
        }

        // 处理其他信息
        if (data.additionalInfos) {
          data.additionalInfos.forEach(info => {
            if (info.type === 'certificate') {
              resumeForm.others.certificates.push({
                name: info.name || '',
                issuer: info.description || '',
                date: info.time || ''
              })
            } else if (info.type === 'award') {
              resumeForm.others.awards.push({
                name: info.name || '',
                level: info.description || '',
                date: info.time || ''
              })
            } else if (info.type === 'hobby') {
              resumeForm.others.hobbies.push(info.name || '')
            }
          })
        }

        // 如果有content字段，只更新缺失的字段，不覆盖已有的数据
        if (data.content) {
          try {
            const contentData = JSON.parse(data.content)
            // 只更新resumeForm中为空的字段
            Object.keys(contentData).forEach(key => {
              if (resumeForm[key] === undefined || resumeForm[key] === null || resumeForm[key] === '') {
                resumeForm[key] = contentData[key]
              }
            })
          } catch (e) {
            console.warn('Failed to parse resume content:', e)
          }
        }
      } catch (error) {
        ElMessage.error('加载简历失败')
        console.error('Load resume error:', error)
      }
    }

    // 日期修正工具
    function fixDate(val) {
      if (!val) return ''
      if (/^\d{4}-\d{2}-\d{2}$/.test(val)) return val
      if (/^\d{4}-\d{2}$/.test(val)) return val + '-01'
      return val
    }
    function fixDateList(list, fields = ['startDate', 'endDate']) {
      if (!Array.isArray(list)) return []
      return list.map(item => {
        const newItem = { ...item }
        fields.forEach(f => {
          if (newItem[f]) newItem[f] = fixDate(newItem[f])
        })
        return newItem
      })
    }
    // 保存简历
    const saveResume = async () => {
      if (saving.value) return

      if (resumeId.value) {
        // 对于已存在的简历，使用自动保存功能
        autoSave(true)
      } else {
        // 对于新简历，使用原有的创建逻辑
        saving.value = true
        try {
          const saveData = {
            name: resumeForm.basicInfo.name,
            fullName: resumeForm.basicInfo.name,
            phone: resumeForm.basicInfo.phone,
            email: resumeForm.basicInfo.email,
            position: resumeForm.basicInfo.position,
            workYears: resumeForm.basicInfo.workYears,
            location: resumeForm.basicInfo.address,
            profile: resumeForm.basicInfo.summary,
            template: resumeForm.template || 'template1',
            templateId: resumeForm.templateId, // 添加模板ID字段
            workExperiences: fixDateList(resumeForm.workExperience),
            educations: fixDateList(resumeForm.education),
            projectExperiences: fixDateList(resumeForm.projects),
            skills: resumeForm.skills.map(s => ({
              skillName: s.name,
              proficiency: s.proficiency,
              description: s.description
            })),
            additionalInfos: [
              ...(resumeForm.others.certificates?.map(c => ({ type: 'certificate', ...c })) || []),
              ...(resumeForm.others.awards?.map(a => ({ type: 'award', ...a })) || []),
              ...(resumeForm.others.hobbies?.map(h => ({ type: 'hobby', name: h })) || [])
            ]
          }

          const result = await createResume(saveData)
          resumeId.value = result.id
          ElMessage.success('简历创建成功')
          router.push('/applicant/resume/list')
        } catch (error) {
          ElMessage.error('保存失败')
          console.error('Save resume error:', error)
        } finally {
          saving.value = false
        }
      }
    }

    // 自动保存
    const autoSave = (showMessage = false) => {
      if (autoSaveTimer) {
        clearTimeout(autoSaveTimer)
      }
      autoSaveTimer = setTimeout(async () => {
        if (resumeId.value && !saving.value) {
          saving.value = true
          try {
            // 结构转换，适配后端 ResumeCreateRequest
            const saveData = {
              name: resumeForm.basicInfo.name,
              fullName: resumeForm.basicInfo.name,
              phone: resumeForm.basicInfo.phone,
              email: resumeForm.basicInfo.email,
              position: resumeForm.basicInfo.position,
              workYears: resumeForm.basicInfo.workYears,
              location: resumeForm.basicInfo.address,
              profile: resumeForm.basicInfo.summary,
              template: resumeForm.template || 'template1',
              templateId: resumeForm.templateId, // 添加模板ID字段
              workExperiences: fixDateList(resumeForm.workExperience),
              educations: fixDateList(resumeForm.education),
              projectExperiences: fixDateList(resumeForm.projects),
              skills: resumeForm.skills.map(s => ({
                skillName: s.name,
                proficiency: s.proficiency,
                description: s.description
              })),
              additionalInfos: [
                ...(resumeForm.others.certificates?.map(c => ({ type: 'certificate', ...c })) || []),
                ...(resumeForm.others.awards?.map(a => ({ type: 'award', ...a })) || []),
                ...(resumeForm.others.hobbies?.map(h => ({ type: 'hobby', name: h })) || [])
              ]
            }

            console.log('正在保存简历数据:', saveData)
            await updateResume(resumeId.value, saveData)
            console.log('简历保存成功')
            if (showMessage) {
              ElMessage.success('简历保存成功')
            }
          } catch (error) {
            if (showMessage) {
              ElMessage.error('保存失败')
            }
            console.error('Auto save error:', error)
          } finally {
            saving.value = false
          }
        }
      }, 2000)
    }

    // 计算总体进度
    const overallProgress = computed(() => {
      const enabledModules = modules.filter(m => m.enabled)
      if (enabledModules.length === 0) return 0

      // 模块权重定义
      const moduleWeights = {
        basicInfo: 30,      // 基本信息最重要
        workExperience: 25, // 工作经历很重要
        education: 20,      // 教育背景重要
        skills: 15,         // 技能中等重要
        projects: 10,       // 项目经验中等重要
        others: 5           // 其他信息较次要
      }

      let totalWeight = 0
      let weightedProgress = 0

      enabledModules.forEach(module => {
        const weight = moduleWeights[module.key] || 10 // 默认权重
        const progress = getModuleCompleteness(module.key)

        totalWeight += weight
        weightedProgress += (progress * weight)
      })

      return totalWeight > 0 ? Math.round(weightedProgress / totalWeight) : 0
    })

    // 获取模块完成度
    const getModuleCompleteness = (moduleKey) => {
      const data = resumeForm[moduleKey]

      switch (moduleKey) {
      case 'basicInfo': {
        const basicFields = ['name', 'phone', 'email', 'summary']
        const filledBasic = basicFields.filter(field => data[field]?.trim()).length
        return Math.round((filledBasic / basicFields.length) * 100)
      }

      case 'workExperience': {
        if (!data || data.length === 0) return 0
        const workFields = ['company', 'position', 'startDate', 'responsibilities']
        const avgWork = data.reduce((acc, work) => {
          const filled = workFields.filter(field => work[field]?.trim()).length
          return acc + (filled / workFields.length)
        }, 0) / data.length
        return Math.round(avgWork * 100)
      }

      case 'education': {
        if (!data || data.length === 0) return 0
        const eduFields = ['school', 'major', 'degree']
        const avgEdu = data.reduce((acc, edu) => {
          const filled = eduFields.filter(field => edu[field]?.trim()).length
          return acc + (filled / eduFields.length)
        }, 0) / data.length
        return Math.round(avgEdu * 100)
      }

      case 'projects': {
        if (!data || data.length === 0) return 0
        const projectFields = ['name', 'description']
        const avgProject = data.reduce((acc, project) => {
          const filled = projectFields.filter(field => project[field]?.trim()).length
          return acc + (filled / projectFields.length)
        }, 0) / data.length
        return Math.round(avgProject * 100)
      }

      case 'skills': {
        if (!data || data.length === 0) return 0
        const skillFields = ['name', 'level']
        const avgSkill = data.reduce((acc, skill) => {
          const filled = skillFields.filter(field => skill[field]).length
          return acc + (filled / skillFields.length)
        }, 0) / data.length
        return Math.round(avgSkill * 100)
      }

      case 'others': {
        if (!data) return 0
        const hasContent = (data.certificates && data.certificates.length > 0) ||
            (data.awards && data.awards.length > 0) ||
            (data.hobbies && data.hobbies.length > 0)
        return hasContent ? 100 : 0
      }

      default:
        return 0
      }
    }

    // 获取进度颜色
    const getProgressColor = (progress) => {
      if (progress < 30) return '#f56c6c'
      if (progress < 70) return '#e6a23c'
      return '#67c23a'
    }

    // 视图切换
    const setActiveView = (view) => {
      activeView.value = view
    }

    // 模块切换
    const setActiveModule = (moduleKey) => {
      activeModule.value = moduleKey
    }

    // 获取当前模块
    const getCurrentModule = () => {
      return modules.find(m => m.key === activeModule.value)
    }

    // 获取当前模块组件
    const getCurrentModuleComponent = () => {
      const componentMap = {
        basicInfo: 'ResumeBasicInfoEnhanced',
        workExperience: 'ResumeWorkExperience',
        education: 'ResumeEducation',
        projects: 'ResumeProjects',
        skills: 'ResumeSkills',
        others: 'ResumeOthers'
      }
      return componentMap[activeModule.value]
    }

    // 面板控制方法
    const toggleLeftPanel = () => {
      leftPanelCollapsed.value = !leftPanelCollapsed.value
    }

    const togglePreviewPanel = () => {
      showPreviewPanel.value = !showPreviewPanel.value
      if (showPreviewPanel.value) {
        showTemplatePanel.value = false // 关闭模板面板
        nextTick(() => {
          initializePanelWidths()
        })
      }
    }

    const showTemplateDialog = async () => {
      if (resumeTemplates.value.length === 0) {
        await loadTemplates()
      }
      templateDialogVisible.value = true
    }

    // 初始化面板宽度
    const initializePanelWidths = () => {
      const containerWidth = window.innerWidth - 320 // 减去左侧面板宽度
      if (showPreviewPanel.value) {
        editAreaWidth.value = Math.floor(containerWidth * 0.6)
        previewAreaWidth.value = Math.floor(containerWidth * 0.4)
      } else if (showTemplatePanel.value) {
        editAreaWidth.value = Math.floor(containerWidth * 0.7)
        templateAreaWidth.value = Math.floor(containerWidth * 0.3)
      }
    }

    const filteredTemplates = computed(() => {
      if (!resumeTemplates.value.length) return []
      if (selectedCategory.value === 'all') {
        return resumeTemplates.value
      }
      return resumeTemplates.value.filter(t => t.category === selectedCategory.value)
    })
    const selectAndApplyTemplate = (templateId) => {
      // 确保转成数字类型
      const id = Number(templateId)
      selectedTemplate.value = id
      resumeForm.templateId = id

      const template = resumeTemplates.value.find(t => Number(t.id) === id)
      if (template) {
        resumeForm.template = template.name
        ElMessage.success(`已应用模板：${template.name}`)
      } else {
        ElMessage.success('模板已应用')
      }
    }

    // 窗口大小变化处理
    const handleWindowResize = () => {
      if (showPreviewPanel.value || showTemplatePanel.value) {
        initializePanelWidths()
      }
    }

    // 拖拽调整宽度
    const startResize = (e) => {
      isResizing.value = true
      const startX = e.clientX
      const startEditWidth = editAreaWidth.value
      const startPanelWidth = showPreviewPanel.value ? previewAreaWidth.value : templateAreaWidth.value
      let animationId = null

      // 添加拖拽状态类
      document.body.classList.add('dragging')
      document.documentElement.classList.add('dragging')

      const handleMouseMove = (e) => {
        if (!isResizing.value) return

        // 取消之前的动画帧
        if (animationId) {
          cancelAnimationFrame(animationId)
        }

        // 使用 requestAnimationFrame 优化性能
        animationId = requestAnimationFrame(() => {
          const deltaX = e.clientX - startX
          const newEditWidth = startEditWidth + deltaX
          const newPanelWidth = startPanelWidth - deltaX

          // 检查最小宽度限制
          const minPanelWidth = showPreviewPanel.value ? minPreviewWidth.value : minTemplateWidth.value
          if (newEditWidth >= minEditWidth.value && newPanelWidth >= minPanelWidth) {
            editAreaWidth.value = newEditWidth
            if (showPreviewPanel.value) {
              previewAreaWidth.value = newPanelWidth
            } else {
              templateAreaWidth.value = newPanelWidth
            }
          }
        })
      }

      const handleMouseUp = () => {
        isResizing.value = false
        if (animationId) {
          cancelAnimationFrame(animationId)
        }

        // 移除拖拽状态类
        document.body.classList.remove('dragging')
        document.documentElement.classList.remove('dragging')

        document.removeEventListener('mousemove', handleMouseMove)
        document.removeEventListener('mouseup', handleMouseUp)
      }

      // 阻止默认行为和事件冒泡
      e.preventDefault()
      e.stopPropagation()

      document.addEventListener('mousemove', handleMouseMove, { passive: false })
      document.addEventListener('mouseup', handleMouseUp)
    }

    // 模块切换处理
    const handleModuleToggle = (module) => {
      if (module.required && !module.enabled) {
        module.enabled = true
        ElMessage.warning('基本信息为必填模块，不能关闭')
      }
    }

    // 获取模块状态类
    const getModuleStatusClass = (moduleKey) => {
      const completeness = getModuleCompleteness(moduleKey)
      if (completeness === 0) return 'status-empty'
      if (completeness < 30) return 'status-incomplete'
      if (completeness < 70) return 'status-partial'
      return 'status-complete'
    }

    // 模块数据变化处理
    const handleModuleChange = () => {
      autoSave()
    }

    // 生成模拟优化建议
    const generateOptimizeSuggestions = (resumeData) => {
      const suggestions = []

      // 个人简介优化
      if (resumeData.basicInfo) {
        const originalSummary = resumeData.basicInfo.summary || ''
        const position = resumeData.basicInfo.position || '软件开发工程师'

        let optimizedSummary = ''
        if (position.includes('前端')) {
          optimizedSummary = '3年前端开发经验，精通React、Vue等主流框架，熟练掌握JavaScript、TypeScript、HTML5、CSS3等技术栈。具备响应式设计和移动端开发经验，注重用户体验和代码质量，擅长与设计师和后端工程师协作。'
        } else if (position.includes('后端')) {
          optimizedSummary = '3年后端开发经验，精通Java、Spring Boot等技术栈，具备微服务架构设计和高并发系统开发经验。熟悉MySQL、Redis、消息队列等技术，注重代码质量和系统性能，具备良好的问题分析和解决能力。'
        } else if (position.includes('产品')) {
          optimizedSummary = '3年产品管理经验，具备丰富的产品规划和项目管理经验。擅长用户需求分析、产品设计和数据分析，具备敏锐的市场洞察力。熟悉敏捷开发流程，具备优秀的跨部门协作和团队管理能力。'
        } else {
          optimizedSummary = '具备丰富的专业经验和扎实的技术基础，熟练掌握相关技术栈和业务流程。注重团队协作和持续学习，具备良好的沟通能力和问题解决能力，能够在快节奏的工作环境中高效完成任务。'
        }

        suggestions.push({
          title: '个人简介',
          original: originalSummary,
          optimized: optimizedSummary,
          selected: true,
          field: 'basicInfo.summary'
        })
      }

      // 工作经验优化
      if (resumeData.workExperience && resumeData.workExperience.length > 0) {
        const firstExp = resumeData.workExperience[0]
        if (firstExp) {
          const originalResp = firstExp.responsibilities || ''
          const optimizedResp = `负责${firstExp.position || '核心业务'}的开发和维护工作，参与系统架构设计和技术方案制定。通过技术优化和流程改进，显著提升了系统性能和开发效率，获得团队和客户的一致好评。`

          suggestions.push({
            title: '工作职责',
            original: originalResp,
            optimized: optimizedResp,
            selected: true,
            field: 'workExperience.0.responsibilities'
          })
        }
      }

      // 项目经验优化
      if (resumeData.projects && resumeData.projects.length > 0) {
        const firstProject = resumeData.projects[0]
        if (firstProject) {
          const originalDesc = firstProject.description || ''
          const optimizedDesc = `${firstProject.name || '核心项目'}采用现代化技术栈进行开发，负责核心功能模块的设计和实现。项目成功上线后，用户体验显著提升，业务指标达到预期目标，为公司创造了可观的商业价值。`

          suggestions.push({
            title: '项目描述',
            original: originalDesc,
            optimized: optimizedDesc,
            selected: true,
            field: 'projects.0.description'
          })
        }
      }

      return suggestions
    }

    // AI优化
    const optimizeResume = async () => {
      optimizing.value = true
      showOptimizeDialog.value = true

      try {
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 2000))

        // 生成优化建议
        optimizeSuggestions.value = generateOptimizeSuggestions(resumeForm)

        ElMessage.success('AI分析完成，请查看优化建议')
      } catch (error) {
        ElMessage.error('AI分析失败，请重试')
        showOptimizeDialog.value = false
      } finally {
        optimizing.value = false
      }
    }

    // 应用优化建议
    const applyOptimizations = () => {
      let appliedCount = 0

      optimizeSuggestions.value.forEach(suggestion => {
        if (suggestion.selected) {
          const fieldPath = suggestion.field.split('.')
          let target = resumeForm

          // 导航到目标字段
          for (let i = 0; i < fieldPath.length - 1; i++) {
            const key = fieldPath[i]
            if (key.includes('[') && key.includes(']')) {
              // 处理数组索引，如 workExperience[0]
              const arrayKey = key.split('[')[0]
              const index = parseInt(key.split('[')[1].split(']')[0])
              target = target[arrayKey][index]
            } else if (!isNaN(fieldPath[i + 1])) {
              // 下一个是数字索引
              target = target[key]
            } else {
              target = target[key]
            }
          }

          // 设置最终值
          const finalKey = fieldPath[fieldPath.length - 1]
          if (target && typeof target === 'object') {
            target[finalKey] = suggestion.optimized
            appliedCount++
          }
        }
      })

      if (appliedCount > 0) {
        ElMessage.success(`已应用 ${appliedCount} 项优化建议`)
        showOptimizeDialog.value = false
        // 触发自动保存
        autoSave()
      } else {
        ElMessage.warning('请至少选择一项优化建议')
      }
    }

    // 显示AI生成对话框
    const showAIGenerateDialog = () => {
      const basic = resumeForm.basicInfo

      const formData = {
        // 基本信息 - 字段名要和 Generation.vue 中的一致
        personalInfo: {
          name: basic.name || '',
          phone: basic.phone || '',
          email: basic.email || '',
          address: basic.address || '',
          summary: basic.summary || '',
          gender: '',
          age: ''
        },
        targetPosition: basic.position || '',
        // 教育和工作经历
        educations: resumeForm.education || [],
        workExperiences: resumeForm.workExperience || []
      }

      sessionStorage.setItem('aiResumeFormData', JSON.stringify(formData))
      sessionStorage.setItem('aiResumeStep', 3)

      router.push('/applicant/resume/generation')
    }
    /*// AI生成简历
    const handleGenerateResumeWithAI = async () => {
      if (!aiForm.userInfo.trim()) {
        ElMessage.warning('请填写用户信息')
        return
      }

      aiGenerating.value = true
      try {
        const res = await generateResumeWithAI(aiForm)
        if (res.code === 0 && res.data && res.data.links && res.data.links.length > 0) {
          aiGenerateResults.value = res.data.links
          ElMessage.success('AI生成简历成功')
        } else {
          ElMessage.error(res.message || 'AI生成简历失败')
        }
      } catch (error) {
        ElMessage.error('AI生成简历失败')
        console.error('AI generate resume error:', error)
      } finally {
        aiGenerating.value = false
      }
    }*/

    // AI生成并保存简历

    /*const generateAndSaveResume = async () => {
      aiSaving.value = true
      try {
        const res = await generateAndSaveResumeWithAI(aiForm)
        if (res.code === 0) {
          ElMessage.success('简历已保存到我的简历列表')
          showAIDialog.value = false
          // 刷新简历列表或跳转到简历列表页
          router.push('/layout/editResume/list')
        } else {
          ElMessage.error(res.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
        console.error('Save AI resume error:', error)
      } finally {
        aiSaving.value = false
      }
    }*/


    // 打开链接
    const openLink = (url) => {
      if (url) {
        window.open(url, '_blank')
      } else {
        ElMessage.warning('链接不可用')
      }
    }

    // 下载文件
    const downloadFile = async (url, filename) => {
      if (!url) {
        ElMessage.warning('下载链接不可用')
        return
      }

      try {
        const response = await fetch(url)
        if (!response.ok) {
          throw new Error('下载失败')
        }

        const blob = await response.blob()
        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        link.download = filename
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(downloadUrl)

        ElMessage.success('文件下载成功')
      } catch (error) {
        console.error('Download error:', error)
        ElMessage.error('下载失败，请稍后重试')
      }
    }

    // 添加模块项目
    const addModuleItem = () => {
      const module = getCurrentModule()
      if (module?.key === 'workExperience') {
        resumeForm.workExperience.push({
          id: Date.now(),
          company: '',
          position: '',
          startDate: '',
          endDate: '',
          isCurrent: false,
          responsibilities: '',
          achievements: ''
        })
      } else if (module?.key === 'education') {
        resumeForm.education.push({
          id: Date.now(),
          school: '',
          major: '',
          degree: '',
          startDate: '',
          endDate: '',
          description: ''
        })
      } else if (module?.key === 'projects') {
        resumeForm.projects.push({
          id: Date.now(),
          name: '',
          role: '',
          description: '',
          technologies: '',
          startDate: '',
          endDate: ''
        })
      } else if (module?.key === 'skills') {
        resumeForm.skills.push({
          id: Date.now(),
          name: '',
          level: 3,
          description: ''
        })
      }
    }

    // 模板选择处理
    const handleTemplateSelected = (templateId) => {
      const id = Number(templateId)
      resumeForm.templateId = id
      selectedTemplate.value = id

      const template = resumeTemplates.value.find(t => Number(t.id) === id)
      if (template) {
        resumeForm.template = template.name
      }
      ElMessage.success('模板已选择')
    }
    // 返回
    const goBack = () => {
      router.back()
    }

    const filteredResumeData = computed(() => {
      // 复制一份简历数据
      const data = JSON.parse(JSON.stringify(resumeForm))
      // 根据 modules 配置隐藏未启用的模块
      modules.forEach(module => {
        if (!module.enabled && !module.required) {
          if (Array.isArray(data[module.key])) {
            data[module.key] = []
          } else if (typeof data[module.key] === 'object') {
            data[module.key] = {}
          } else {
            data[module.key] = undefined
          }
        }
      })
      return data
    })

    return {
      resumeId,
      saving,
      optimizing,
      activeView,
      activeModule,
      previewScale,
      leftPanelCollapsed,
      showPreviewPanel,
      showTemplatePanel,
      editAreaWidth,
      previewAreaWidth,
      templateAreaWidth,
      isResizing,
      modules,
      resumeForm,
      overallProgress,

      // AI优化相关
      showOptimizeDialog,
      optimizeSuggestions,
      hasSelectedOptimizations,
      // 模板相关
      templateDialogVisible,
      selectedTemplate,
      selectedCategory,
      templateCategories,
      resumeTemplates,
      filteredTemplates,
      filteredResumeData,
      // 方法
      loadResume,
      loadTemplates,
      saveResume,
      autoSave,
      setActiveView,
      setActiveModule,
      getProgressColor,
      getModuleCompleteness,
      getCurrentModule,
      getCurrentModuleComponent,
      toggleLeftPanel,
      togglePreviewPanel,
      toggleTemplatePanel,
      showTemplateDialog,
      selectAndApplyTemplate,
      initializePanelWidths,
      handleWindowResize,
      handleModuleToggle,
      handleModuleChange,
      getModuleStatusClass,
      addModuleItem,
      startResize,
      optimizeResume,
      applyOptimizations,
      showAIGenerateDialog,
      goBack
    }
  }
}
</script>

<style scoped>
/* 迁移自frontend，将scss语法改为css - 简历编辑器样式 */
.resume-editor-enhanced {
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.editor-header {
  height: 60px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.editor-header .header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.editor-header .header-left .back-btn {
  color: #6b7280;
}

.editor-header .header-left .divider {
  width: 1px;
  height: 24px;
  background: #e5e7eb;
}

.editor-header .header-left .title-input {
  width: 200px;
}

.editor-header .header-center .view-tabs {
  display: flex;
  background: #f3f4f6;
  border-radius: 8px;
  padding: 4px;
}

.editor-header .header-center .view-tabs .tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
}

.editor-header .header-center .view-tabs .tab-item.active {
  background: white;
  color: #3b82f6;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.editor-header .header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.editor-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 280px;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;
  border-bottom: 1px solid #e5e7eb;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.sidebar-title .el-icon {
  font-size: 16px;
  color: #6b7280;
}

.collapse-btn {
  color: #6b7280;
}

.collapse-btn:hover {
  color: #374151;
  background: #f3f4f6;
}

.progress-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.progress-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.progress-value {
  font-size: 16px;
  font-weight: 700;
  color: #3b82f6;
}

/* 侧边栏内容 */
.sidebar-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

/* 模块列表 */
.module-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.module-item {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.2s ease;
  overflow: hidden;
}

.module-item:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.module-item.active {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.module-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.module-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
}

.module-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.module-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #6b7280;
  transition: all 0.2s ease;
}

.module-item.active .module-icon {
  background: #dbeafe;
  color: #3b82f6;
}

.module-info {
  flex: 1;
}

.module-name {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 4px;
}

.module-item.active .module-name {
  color: #1e40af;
}

.module-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.completion-rate {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.module-item.active .completion-rate {
  color: #3b82f6;
}

.module-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.module-indicator {
  position: relative;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.indicator-dot.status-empty {
  background: #d1d5db;
}

.indicator-dot.status-incomplete {
  background: #f59e0b;
}

.indicator-dot.status-partial {
  background: #3b82f6;
}

.indicator-dot.status-complete {
  background: #10b981;
}

/* 收起状态 */
.sidebar-collapsed {
  padding: 16px 8px;
}

.collapsed-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 8px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.collapsed-item:hover {
  background: #f8fafc;
}

.collapsed-item.active {
  background: #eff6ff;
}

.collapsed-item.active .el-icon {
  color: #3b82f6;
}

.collapsed-item .el-icon {
  font-size: 20px;
  color: #6b7280;
  transition: color 0.2s ease;
}

.collapsed-item .indicator-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  position: absolute;
  top: 8px;
  right: 8px;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
  position: relative;
  align-items: flex-start;

}

.main-content.with-preview {
  flex-direction: row;
}

.main-content.with-template {
  flex-direction: row;
}

.main-content.dragging {
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.main-content.dragging * {
  user-select: none !important;
  -webkit-user-select: none !important;
  -moz-user-select: none !important;
  -ms-user-select: none !important;
}

.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #ffffff;
}

.main-content.with-preview .content-area,
.main-content.with-template .content-area {
  flex: none;
  border-right: 1px solid #e5e7eb;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: #eff6ff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3b82f6;
  font-size: 24px;
}

.header-text .header-title {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.header-text .header-desc {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.content-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f8fafc;
}

/* 拖拽分隔条样式 - 迁移自frontend，将scss语法改为css */
.resize-handle {
  width: 4px;
  background: transparent;
  cursor: col-resize;
  position: relative;
  flex-shrink: 0;
  transition: background-color 0.2s ease;
}

.resize-handle:hover {
  background: #e5e7eb;
}

.resize-handle:hover .resize-line {
  background: #3b82f6;
}

.resize-line {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 2px;
  height: 40px;
  background: #d1d5db;
  border-radius: 1px;
  transition: all 0.2s ease;
}

.preview-panel {
  background: white;
  border-left: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.preview-toolbar {
  height: 60px;
  padding: 0 20px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toolbar-title {
  font-weight: 600;
  color: #374151;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.preview-panel-content {
  flex: 1;
  overflow: auto;
  padding: 20px;
  background: #f5f5f5;
  height: calc(100% - 50px);
  /* 减去工具栏高度 */
  overflow: auto;
  /* 内容超出时显示滚动条 */
}

.preview-wrapper {
  transform-origin: top center;
  transition: transform 0.3s ease;
}

.full-preview {
  flex: 1;
  overflow: auto;
  background: #f5f5f5;
  height: calc(100vh - 60px);
  /* 减去固定导航栏高度 */
  overflow: auto;
  /* 允许滚动 */
  box-sizing: border-box;
  /* 确保padding不影响高度计算 */
  padding: 20px;
  /* 可选：添加内边距时，box-sizing会保证高度正确 */
}

/* 模板面板样式 - 迁移自frontend，将scss语法改为css */
.template-panel {
  background: white;
  border-left: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
}

.template-toolbar {
  height: 60px;
  padding: 0 20px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.template-toolbar .toolbar-title {
  font-weight: 600;
  color: #374151;
}

.template-toolbar .toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.template-categories {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.template-categories .category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  transition: all 0.2s ease;
}

.template-categories .category-item:hover {
  background: #f3f4f6;
  color: #374151;
}

.template-categories .category-item.active {
  background: #eff6ff;
  color: #3b82f6;
  border: 1px solid #bfdbfe;
}

.template-categories .category-item.active .el-icon {
  color: #3b82f6;
}

.template-categories .category-item .el-icon {
  font-size: 14px;
  color: #9ca3af;
  transition: color 0.2s ease;
}

.template-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.template-list .template-item {
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #e5e7eb;
  background: #ffffff;
}

.template-list .template-item:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
  transform: translateY(-2px);
}

.template-list .template-item.selected {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
  transform: translateY(-2px);
}

.template-list .template-item.selected .template-overlay {
  opacity: 1;
}

.template-list .template-item .template-preview {
  position: relative;
  height: 120px;
  overflow: hidden;
  background: #f8f9fa;
}

.template-list .template-item .template-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.template-list .template-item .template-preview .template-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(59, 130, 246, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.template-list .template-item .template-preview .template-overlay .el-icon {
  font-size: 24px;
  color: white;
}

.template-list .template-item .template-preview:hover img {
  transform: scale(1.05);
}

.template-list .template-item .template-preview:hover .template-overlay {
  opacity: 1;
}

.template-list .template-item .template-info {
  padding: 12px;
}

.template-list .template-item .template-info .template-name {
  margin: 0 0 6px 0;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.template-list .template-item .template-info .template-desc {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #6b7280;
  line-height: 1.4;
}

.template-list .template-item .template-info .template-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.template-list .template-item .template-info .template-tags .el-tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f3f4f6;
  color: #6b7280;
  border: none;
}

/* 自定义滚动条 */
.template-list::-webkit-scrollbar {
  width: 4px;
}

.template-list::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.template-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 2px;
}

.template-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

@media (max-width: 1200px) {
  .sidebar {
    width: 280px;
  }

  .sidebar.collapsed {
    width: 60px;
  }
}

@media (max-width: 768px) {
  .editor-header {
    padding: 0 16px;
  }

  .editor-header .header-left .title-input {
    width: 150px;
  }

  .sidebar {
    position: absolute;
    left: 0;
    top: 60px;
    height: calc(100vh - 60px);
    z-index: 50;
    transform: translateX(-100%);
  }

  .sidebar:not(.collapsed) {
    transform: translateX(0);
  }

  .module-content-wrapper {
    padding: 20px;
  }
}
</style>

<style>
/* 全局拖拽样式（不使用scoped） - 迁移自frontend，将scss语法改为css */
body.dragging,
html.dragging {
  cursor: col-resize !important;
  user-select: none !important;
  -webkit-user-select: none !important;
  -moz-user-select: none !important;
  -ms-user-select: none !important;
}

body.dragging *,
html.dragging * {
  cursor: col-resize !important;
  user-select: none !important;
  -webkit-user-select: none !important;
  -moz-user-select: none !important;
  -ms-user-select: none !important;
}

/* AI生成简历对话框样式 */
.ai-generate-content {
  padding: 20px 0;
}

.ai-form {
  margin-bottom: 30px;
}

.ai-form .el-form-item {
  margin-bottom: 20px;
}

.ai-form .el-form-item__label {
  font-weight: 600;
  color: #374151;
}

.ai-result {
  border-top: 1px solid #e5e7eb;
  padding-top: 24px;
}

.result-header {
  text-align: center;
  margin-bottom: 24px;
}

.result-header .success-icon {
  font-size: 48px;
  color: #10b981;
  margin-bottom: 12px;
}

.result-header h4 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.result-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.result-links {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.link-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #f9fafb;
  transition: all 0.3s ease;
}

.link-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
}

.link-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.link-icon .el-icon {
  font-size: 24px;
  color: #3b82f6;
}

.link-content {
  flex: 1;
  margin-right: 16px;
}

.link-content h5 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.link-content p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.link-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.link-actions .el-button {
  padding: 8px 16px;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .link-card {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .link-icon {
    margin-right: 0;
  }

  .link-content {
    margin-right: 0;
  }

  .link-actions {
    width: 100%;
    justify-content: center;
  }
}

/* AI优化弹窗样式 */
.optimize-dialog-content {
  max-height: 500px;
  overflow-y: auto;
}

.optimize-dialog-content .el-alert {
  margin-bottom: 20px;
}

.optimize-sections {
  margin-top: 20px;
}

.optimize-section {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  background: #fafafa;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.content-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 12px;
}

.original-content,
.optimized-content {
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
}

.original-content {
  background: #fff2f0;
  border-color: #ffccc7;
}

.optimized-content {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.original-content h5,
.optimized-content h5 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.original-content p,
.optimized-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #303133;
  word-break: break-word;
}

.loading-content {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.loading-content .el-icon {
  font-size: 32px;
  margin-bottom: 16px;
}

.loading-content p {
  margin: 0;
  font-size: 16px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .content-comparison {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .optimize-dialog-content {
    max-height: 400px;
  }
}
</style>
