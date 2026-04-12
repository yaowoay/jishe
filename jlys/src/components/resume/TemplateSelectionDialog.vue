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
          <div class="preview-content" :class="template.id">
            <div class="preview-header">
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
                :type="tag.type"
                effect="plain"
            >
              {{ tag.text }}
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
import { ref, watch } from 'vue'

export default {
  name: 'TemplateSelectionDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    currentTemplate: {
      type: String,
      default: 'template1'
    }
  },
  emits: ['update:modelValue', 'template-selected'],
  setup(props, { emit }) {
    const visible = ref(props.modelValue)
    const selectedTemplate = ref(props.currentTemplate)

    // 监听modelValue变化
    watch(() => props.modelValue, (newVal) => {
      visible.value = newVal
    })

    // 监听visible变化
    watch(visible, (newVal) => {
      emit('update:modelValue', newVal)
    })

    // 监听currentTemplate变化
    watch(() => props.currentTemplate, (newVal) => {
      selectedTemplate.value = newVal
    })

    const templates = ref([
      {
        id: 'template1',
        name: '经典商务',
        description: '简洁大方的商务风格，适合大多数职位',
        preview: require('@/assets/images/feature2.png'), // 注意：需替换为实际图片路径
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
        preview: require('@/assets/images/feature1.png'), // 注意：需替换为实际图片路径
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
        preview: require('@/assets/images/feature3.png'), // 注意：需替换为实际图片路径
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
        preview: require('@/assets/images/feature5.png'), // 注意：需替换为实际图片路径
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
      emit('update:modelValue', false)
    }

    const handleClose = () => {
      emit('update:modelValue', false)
    }

    return {
      visible,
      selectedTemplate,
      templates,
      selectTemplate,
      confirmSelection,
      handleClose
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
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.template-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.template-card.active {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.template-preview {
  margin-bottom: 12px;
}

.preview-content {
  height: 120px;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
  background-size: cover;
  background-position: center;
}

.preview-content.template1 {
  background-image: url('@/assets/images/feature2.png');
}

.preview-content.template2 {
  background-image: url('@/assets/images/feature1.png');
}

.preview-content.template3 {
  background-image: url('@/assets/images/feature3.png');
}

.preview-content.template4 {
  background-image: url('@/assets/images/feature5.png');
}

.preview-header {
  padding: 16px;
  color: white;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.5), transparent);
}

.preview-header h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.preview-header p {
  margin: 0;
  font-size: 12px;
  opacity: 0.9;
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
</style>
