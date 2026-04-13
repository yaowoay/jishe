<template>
  <el-dialog
    v-model="visible"
    title="完善学籍信息"
    width="500px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="false"
    center
    class="profile-guide-dialog"
  >
    <div class="dialog-content">
      <div class="icon-wrapper">
        <el-icon size="60" color="#409eff">
          <User />
        </el-icon>
      </div>
      
      <h3>{{ title }}</h3>
      
      <p>{{ description }}</p>
      
      <div class="benefits">
        <div class="benefit-item">
          <el-icon color="#67c23a"><Check /></el-icon>
          <span>精准岗位匹配</span>
        </div>
        <div class="benefit-item">
          <el-icon color="#67c23a"><Check /></el-icon>
          <span>个性化推荐</span>
        </div>
        <div class="benefit-item">
          <el-icon color="#67c23a"><Check /></el-icon>
          <span>提升求职效率</span>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button
          size="large"
          @click="handleLater"
        >
          稍后再说
        </el-button>
        <el-button
          type="primary"
          size="large"
          @click="handleComplete"
        >
          立即完善
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { User, Check } from '@element-plus/icons-vue'

export default {
  name: 'ProfileGuideDialog',
  components: {
    User,
    Check
  },
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '完善学籍信息'
    },
    description: {
      type: String,
      default: '完善您的学籍信息后，系统将为您推荐更加匹配的实习和就业机会，提升求职成功率。'
    }
  },
  emits: ['update:modelValue', 'complete', 'later'],
  computed: {
    visible: {
      get() {
        return this.modelValue
      },
      set(value) {
        this.$emit('update:modelValue', value)
      }
    }
  },
  methods: {
    handleComplete() {
      this.$emit('complete')
      this.visible = false
    },
    
    handleLater() {
      this.$emit('later')
      this.visible = false
    }
  }
}
</script>

<style scoped>
.profile-guide-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.profile-guide-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
  margin: 0;
}

.profile-guide-dialog :deep(.el-dialog__title) {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
}

.profile-guide-dialog :deep(.el-dialog__body) {
  padding: 30px 24px 20px;
}

.dialog-content {
  text-align: center;
}

.icon-wrapper {
  margin-bottom: 20px;
}

.dialog-content h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
  line-height: 1.4;
}

.dialog-content p {
  color: #7f8c8d;
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 24px;
}

.benefits {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.benefit-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 0.95rem;
  color: #34495e;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 0 24px 24px;
}

.dialog-footer .el-button {
  padding: 12px 24px;
  font-size: 1rem;
  border-radius: 8px;
  min-width: 100px;
}

@media (max-width: 768px) {
  .profile-guide-dialog :deep(.el-dialog) {
    width: 90vw !important;
    margin: 0 auto;
  }
  
  .dialog-footer {
    flex-direction: column;
  }
  
  .dialog-footer .el-button {
    width: 100%;
  }
}
</style>