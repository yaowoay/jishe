<template>
  <div class="template-selector">
    <div class="selector-header">
      <h3>选择模板</h3>
      <el-button @click="$emit('close')" text>
        <el-icon><Close /></el-icon>
      </el-button>
    </div>
    
    <div class="template-grid">
      <div
        v-for="template in templates"
        :key="template.id"
        class="template-option"
        :class="{ active: selectedTemplate === template.id }"
        @click="selectTemplate(template.id)"
      >
        <div class="template-preview">
          <div class="preview-content" :class="template.id">
            <div class="preview-header">
              <h4>{{ template.name }}</h4>
              <p>{{ template.description }}</p>
            </div>
          </div>
        </div>
        <div class="template-info">
          <h5>{{ template.name }}</h5>
          <p>{{ template.description }}</p>
          <div class="template-tags">
            <el-tag
              v-for="tag in template.tags"
              :key="tag"
              size="small"
              :type="tag.type"
              effect="plain"
            >
              {{ tag.text }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="selector-footer">
      <el-button @click="$emit('close')">取消</el-button>
      <el-button type="primary" @click="confirmSelection" :disabled="!selectedTemplate">
        应用模板
      </el-button>
    </div>
  </div>
</template>

<script>
import { ref, defineProps, defineEmits } from 'vue'
import { Close } from '@element-plus/icons-vue'

export default {
  name: 'ResumeTemplateSelector',
  components: {
    Close
  },
  props: {
    currentTemplate: {
      type: String,
      default: 'template1'
    }
  },
  emits: ['close', 'template-selected'],
  setup(props, { emit }) {
    const selectedTemplate = ref(props.currentTemplate)

    const templates = ref([
      {
        id: 'template1',
        name: '经典商务',
        description: '简洁大方的商务风格，适合大多数职位',
        tags: [
          { text: '商务', type: 'primary' },
          { text: '简洁', type: 'success' },
          { text: '通用', type: 'info' }
        ]
      },
      {
        id: 'template2',
        name: '现代简约',
        description: '现代感十足，突出个人特色',
        tags: [
          { text: '现代', type: 'warning' },
          { text: '简约', type: 'success' },
          { text: '时尚', type: 'danger' }
        ]
      },
      {
        id: 'template3',
        name: '创意设计',
        description: '富有创意的设计风格，适合创意类职位',
        tags: [
          { text: '创意', type: 'danger' },
          { text: '设计', type: 'warning' },
          { text: '艺术', type: 'info' }
        ]
      },
      {
        id: 'template4',
        name: '专业技术',
        description: '突出技术能力，适合技术类职位',
        tags: [
          { text: '技术', type: 'primary' },
          { text: '专业', type: 'success' },
          { text: '严谨', type: 'info' }
        ]
      }
    ])

    const selectTemplate = (templateId) => {
      selectedTemplate.value = templateId
    }

    const confirmSelection = () => {
      emit('template-selected', selectedTemplate.value)
      emit('close')
    }

    return {
      selectedTemplate,
      templates,
      selectTemplate,
      confirmSelection
    }
  }
}
</script>

<style scoped>
.template-selector {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
}

.selector-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2c3e50;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.template-option {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.template-option:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.template-option.active {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.template-preview {
  margin-bottom: 12px;
}

.preview-content {
  height: 100px;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.preview-content.template1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.preview-content.template2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.preview-content.template3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.preview-content.template4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.preview-header {
  padding: 12px;
  color: white;
}

.preview-header h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
}

.preview-header p {
  margin: 0;
  font-size: 10px;
  opacity: 0.9;
}

.template-info h5 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.template-info p {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.template-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.selector-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  background: #f8f9fa;
}
</style>
