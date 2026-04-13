<template>
  <el-dialog
      v-model="visible"
      title="选择简历模板"
      width="800px"
      :close-on-click-modal="false"
      @close="handleClose"
  >
    <div class="template-grid">
      <div
          v-for="template in templates"
          :key="template.id"
          class="template-card"
          :class="{ active: selectedTemplate === template.id }"
          @click="selectTemplate(template.id)"
      >
        <div class="template-preview">
          <img 
            :src="template.preview" 
            :alt="template.name"
            @error="handleImageError"
          />
          <div class="template-overlay">
            <div class="overlay-content">
              <h3>{{ template.name }}</h3>
              <p>{{ template.description }}</p>
            </div>
          </div>
        </div>
        <div class="template-info">
          <h4>{{ template.name }}</h4>
          <p>{{ template.description }}</p>
          <div class="template-tags">
            <el-tag
                v-for="tag in template.tags"
                :key="tag"
                size="small"
                type="info"
                effect="plain"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="confirmSelection" :disabled="!selectedTemplate">
          确认选择
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { getResumeTemplates } from '@/api/resume'
import { ElMessage } from 'element-plus'

export default {
  name: 'TemplateSelectionDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    currentTemplate: {
      type: [String, Number],
      default: null
    }
  },
  emits: ['update:modelValue', 'template-selected'],
  setup(props, { emit }) {
    const visible = ref(props.modelValue)
    const selectedTemplate = ref(props.currentTemplate)
    const templates = ref([])

    // 监听modelValue变化
    watch(() => props.modelValue, (newVal) => {
      visible.value = newVal
      if (newVal) {
        loadTemplates()
      }
    })

    // 监听visible变化
    watch(visible, (newVal) => {
      emit('update:modelValue', newVal)
    })

    // 监听currentTemplate变化
    watch(() => props.currentTemplate, (newVal) => {
      selectedTemplate.value = newVal
    })

    // 加载模板列表
    const loadTemplates = async () => {
      try {
        const res = await getResumeTemplates()
        if (res.code === 0 && Array.isArray(res.data)) {
          templates.value = res.data.map(template => ({
            id: template.template_id,
            name: template.template_name,
            description: template.description || '暂无描述',
            preview: template.preview_image || '/images/default-template-preview.svg',
            type: template.template_type,
            path: template.template_path,
            category: template.category,
            tags: getTemplateTags(template)
          }))
        }
      } catch (error) {
        console.error('Load templates error:', error)
        ElMessage.error('加载模板失败')
      }
    }

    // 根据模板信息生成标签
    const getTemplateTags = (template) => {
      const tags = []
      
      // 根据模板类型添加标签
      if (template.template_type === 'latex') tags.push('LaTeX')
      if (template.template_type === 'html') tags.push('HTML')
      
      // 根据分类添加标签
      if (template.category === 'it') tags.push('IT')
      if (template.category === 'academic') tags.push('学术')
      if (template.category === 'professional') tags.push('专业')
      
      // 根据模板名称添加特色标签
      if (template.template_name.includes('照片') || template.template_name.includes('photo')) tags.push('带照片')
      if (template.template_name.includes('专业')) tags.push('专业')
      if (template.template_name.includes('简约')) tags.push('简约')
      if (template.template_name.includes('中文')) tags.push('中文')
      
      return tags.length > 0 ? tags : ['通用']
    }

    const selectTemplate = (templateId) => {
      selectedTemplate.value = templateId
    }

    const confirmSelection = () => {
      emit('template-selected', selectedTemplate.value)
      emit('update:modelValue', false)
    }

    const handleClose = () => {
      emit('update:modelValue', false)
    }

    // 处理图片加载错误
    const handleImageError = (event) => {
      event.target.src = '/images/default-template-preview.svg'
    }

    return {
      visible,
      selectedTemplate,
      templates,
      selectTemplate,
      confirmSelection,
      handleClose,
      handleImageError
    }
  }
}
</script>

<style scoped>
.template-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.template-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.template-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
  transform: translateY(-2px);
}

.template-card.active {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.template-preview {
  position: relative;
  height: 160px;
  overflow: hidden;
  background: #f8f9fa;
}

.template-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.template-card:hover .template-preview img {
  transform: scale(1.05);
}

.template-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.6), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: flex-start;
  padding: 16px;
}

.template-card:hover .template-overlay {
  opacity: 1;
}

.overlay-content {
  color: white;
}

.overlay-content h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}

.overlay-content p {
  margin: 0;
  font-size: 12px;
  opacity: 0.9;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}

.template-info {
  padding: 16px;
}

.template-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.template-info p {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

.template-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .template-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .template-preview {
    height: 120px;
  }
}
</style>
