<template>
  <div class="resume-basic-info">


    <el-form :model="formData" label-width="80px" @submit.prevent>
      <!-- 头像上传 -->
      <el-form-item label="头像">
        <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
            action="#"
        >
          <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <!-- 基本信息 -->
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item>
            <template #label>
              <span class="required-star">*</span>姓名
            </template>
            <el-input
                v-model="formData.name"
                placeholder="请输入姓名"
                @input="handleChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="期望职位">
            <el-input
                v-model="formData.position"
                placeholder="如：前端工程师，没有则填无"
                @input="handleChange"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item>
            <template #label>
              <span class="required-star">*</span>手机号
            </template>
            <el-input
                v-model="formData.phone"
                placeholder="请输入手机号"
                @input="handleChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              <span class="required-star">*</span>邮箱
            </template>
            <el-input
                v-model="formData.email"
                placeholder="请输入邮箱"
                @input="handleChange"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="工作年限">
            <el-select v-model="formData.workYears" placeholder="选择工作年限" @change="handleChange" style="height: 48px;">
              <el-option label="应届生" :value="0" />
              <el-option label="1年以下" :value="1" />
              <el-option label="1-3年" :value="2" />
              <el-option label="3-5年" :value="3" />
              <el-option label="5-10年" :value="4" />
              <el-option label="10年以上" :value="5" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所在地">
            <el-input
                v-model="formData.address"
                placeholder="如：北京市朝阳区，没有则填无"
                @input="handleChange"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 个人简介 -->
      <el-form-item class="intro-form-item">
        <template #label>
          <span class="required-star">*</span>个人简介
        </template>
        <div class="textarea-wrapper">
          <el-input
              v-model="formData.summary"
              type="textarea"
              :rows="4"
              placeholder="请简要介绍自己的专业背景、核心技能和职业目标（建议100-200字）"
              maxlength="500"
              show-word-limit
              @input="handleChange"
          />
          <!-- 字数统计默认已在el-input右下角，确保样式不被覆盖 -->
        </div>
        <div class="summary-tips">
          <el-alert
              title="写作建议"
              type="info"
              :closable="false"
              show-icon
          >
            <template #default>
              <ul>
                <li>突出核心技能和专业优势</li>
                <li>量化工作成果和项目经验</li>
                <li>体现职业发展目标</li>
                <li>保持简洁有力，避免空泛表述</li>
              </ul>
            </template>
          </el-alert>
        </div>
      </el-form-item>
    </el-form>

    <!-- AI优化建议弹窗 -->
    <el-dialog
        v-model="showAIOptimize"
        title="AI优化个人简介"
        width="600px"
    >
      <div class="ai-optimize-content">
        <el-alert
            title="AI分析建议"
            type="success"
            :closable="false"
            show-icon
        >
          基于您的信息，AI为您生成了以下优化建议：
        </el-alert>

        <div class="optimize-suggestions" v-if="aiSuggestions.length > 0">
          <div
              v-for="(suggestion, index) in aiSuggestions"
              :key="index"
              class="suggestion-card"
          >
            <div class="suggestion-header">
              <el-tag type="primary">建议 {{ index + 1 }}</el-tag>
              <el-button size="small" type="link" @click="applySuggestion(suggestion)">
                应用此建议
              </el-button>
            </div>
            <div class="suggestion-content">
              {{ suggestion }}
            </div>
          </div>
        </div>

        <div class="generate-section">
          <el-button
              type="primary"
              @click="generateAISuggestions"
              :loading="generating"
              style="width: 100%"
          >
            {{ generating ? '生成中...' : '生成AI优化建议' }}
          </el-button>
        </div>
      </div>

      <template #footer>
        <el-button @click="showAIOptimize = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, MagicStick } from '@element-plus/icons-vue'

export default {
  name: 'ResumeBasicInfo',
  components: {
    Plus,
    MagicStick
  },
  props: {
    modelValue: {
      type: Object,
      default: () => ({
        name: '',
        phone: '',
        email: '',
        address: '',
        avatar: '',
        summary: '',
        position: '',
        workYears: 0
      })
    }
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    const formData = reactive({ ...props.modelValue })
    const showAIOptimize = ref(false)
    const generating = ref(false)
    const aiSuggestions = ref([])

    // 监听props变化
    watch(() => props.modelValue, (newVal) => {
      Object.assign(formData, newVal)
    }, { deep: true })

    // 处理数据变化
    const handleChange = () => {
      emit('update:modelValue', { ...formData })
      emit('change')
    }

    // 头像上传前验证
    const beforeAvatarUpload = (file) => {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        ElMessage.error('头像只能是 JPG/PNG 格式!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('头像大小不能超过 2MB!')
        return false
      }
      return true
    }

    // 头像上传
    const handleAvatarUpload = (options) => {
      const { file } = options
      const reader = new FileReader()
      reader.onload = (e) => {
        formData.avatar = e.target?.result
        handleChange()
        ElMessage.success('头像上传成功')
      }
      reader.readAsDataURL(file)
    }

    // 生成AI建议
    const generateAISuggestions = async () => {
      generating.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 2000))

        const workYearsText = formData.workYears > 0 ? `${formData.workYears}年` : '应届'
        const positionText = formData.position || '软件开发'

        aiSuggestions.value = [
          `${workYearsText}${positionText}经验，熟练掌握相关技术栈，具备良好的编程基础和项目实战经验。擅长团队协作，注重代码质量，持续学习新技术。`,
          `具有${workYearsText}专业背景，在${positionText}领域有深入理解。善于分析问题、解决问题，具备良好的沟通能力和学习能力。`,
          `${positionText}专业出身，${formData.workYears > 0 ? '拥有' + formData.workYears + '年工作经验，' : ''}熟悉行业最佳实践，注重用户体验和产品质量。具备强烈的责任心和团队合作精神。`
        ]

        ElMessage.success('AI建议生成成功')
      } catch (error) {
        ElMessage.error('生成建议失败，请重试')
      } finally {
        generating.value = false
      }
    }

    // 应用建议
    const applySuggestion = (suggestion) => {
      formData.summary = suggestion
      handleChange()
      showAIOptimize.value = false
      ElMessage.success('建议已应用')
    }

    return {
      formData,
      showAIOptimize,
      generating,
      aiSuggestions,
      handleChange,
      beforeAvatarUpload,
      handleAvatarUpload,
      generateAISuggestions,
      applySuggestion
    }
  }
}
</script>

<style scoped>
/* eslint-disable */
.resume-basic-info {
  font-size: 15px;
}
.section-header {
  font-size: 16px;
  margin-bottom: 20px;
}
.section-header h3 {
  font-size: 18px;
}
.el-form-item__label {
  font-size: 14px !important;
}
.el-input, .el-select, .el-date-picker {
  font-size: 14px !important;
  height: 32px !important;
  min-height: 32px !important;
}
/* 移除对 .el-textarea 和 .el-textarea__inner 的固定高度限制 */
/* .el-textarea, .el-textarea__inner {
  font-size: 20px !important;
  height: 48px !important;
  min-height: 48px !important;
} */
.el-input__inner {
  font-size: 14px !important;
  height: 32px !important;
  min-height: 32px !important;
}
.el-button, .el-button--primary {
  font-size: 14px !important;
  height: 32px !important;
  padding: 0 16px !important;
}
.avatar-uploader-icon {
  font-size: 32px !important;
}
.avatar {
  width: 48px !important;
  height: 48px !important;
}
/* 迁移自frontend，将scss语法改为css - 基本信息组件样式 */
.resume-basic-info .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.resume-basic-info .section-header h3 {
  margin: 0;
  color: var(--el-text-color-primary);
}

.resume-basic-info .avatar-uploader .avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.resume-basic-info .avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.resume-basic-info .avatar-uploader .avatar-uploader-icon:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.resume-basic-info .summary-tips {
  margin-top: 24px !important;
}

.resume-basic-info .summary-tips ul {
  margin: 8px 0 0 0;
  padding-left: 20px;
}

.resume-basic-info .summary-tips ul li {
  margin-bottom: 4px;
  font-size: 12px;
  color: var(--el-text-color-regular);
}

.ai-optimize-content .optimize-suggestions {
  margin: 20px 0;
}

.ai-optimize-content .suggestion-card {
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.ai-optimize-content .suggestion-card .suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.ai-optimize-content .suggestion-card .suggestion-content {
  line-height: 1.6;
  color: var(--el-text-color-regular);
}

.ai-optimize-content .generate-section {
  margin-top: 20px;
}
/* 美化 el-select 下拉框 */
.el-select {
  font-size: 20px !important;
  height: 48px !important;
}
.el-select .el-input__inner {
  font-size: 20px !important;
  height: 48px !important;
  line-height: 48px !important;
}
.el-select-dropdown {
  font-size: 20px !important;
}
.el-select__wrapper {
  height: 48px !important;
  min-height: 48px !important;
  line-height: 48px !important;
  display: flex !important;
  align-items: center !important;
  box-sizing: border-box;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
}
/* 移除本地对el-select高度的强制设置，避免与全局样式冲突 */
/* .el-select,
.el-select__wrapper,
.el-select .el-input__wrapper {
  background: transparent !important;
  box-shadow: none !important;
  height: 48px !important;
  min-height: 48px !important;
  line-height: 48px !important;
} */
/* 让个人简介输入框外层相对定位，字数统计绝对定位 */
.intro-form-item {
  position: relative;
  display: flex;
  flex-direction: column;
}
.textarea-wrapper {
  position: relative;
  width: 100%;
}
.summary-tips {
  margin-top: 8px !important;
  display: block;
  width: 100%;
  z-index: 1;
}
.el-input__count {
  position: absolute !important;
  right: 16px !important;
  bottom: 12px !important;
  background: transparent !important;
  font-size: 16px !important;
  color: #888 !important;
  z-index: 2;
  pointer-events: none;
}
/* 写作建议卡片紧贴输入框下方 */
.summary-tips {
  margin-top: 12px !important;
  display: block;
}
/* 修正el-select下拉框与输入框背景、边框、阴影一致性 */
:deep(.el-select),
:deep(.el-select .el-input__wrapper),
:deep(.el-select .el-input__inner) {
  background: #fff !important;
  border: 1px solid #dcdfe6 !important;
  box-shadow: none !important;
  border-radius: 8px !important;
  height: 48px !important;
  min-height: 48px !important;
  line-height: 48px !important;
  box-sizing: border-box;
}
</style>
/* 必填项星号渐变色（淡蓝+淡粉） */
.required-star {
font-weight: bold;
background: linear-gradient(90deg, #7ecbff 0%, #ffb6c1 100%);
-webkit-background-clip: text;
-webkit-text-fill-color: transparent;
background-clip: text;
text-fill-color: transparent;
font-size: 18px;
margin-right: 2px;
}
