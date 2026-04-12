<template>
  <div class="resume-education">

    <div class="education-list">
      <div
          v-for="(edu, index) in educationList"
          :key="edu.id || index"
          class="education-item"
      >
        <div class="education-item-header">
          <span class="education-index">{{ index + 1 }}</span>
          <el-button
              size="small"
              type="danger"
              text
              @click="removeEducation(index)"
              :disabled="educationList.length === 1"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>

        <el-form :model="edu" label-width="100px">
          <!-- 基本信息 -->
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>学校名称
                </template>
                <el-input
                    v-model="edu.school"
                    placeholder="请输入学校名称"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>专业名称
                </template>
                <el-input
                    v-model="edu.major"
                    placeholder="请输入专业名称"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>学历层次
                </template>
                <el-select v-model="edu.degree" placeholder="选择学历" @change="handleChange" style="height: 48px;">
                  <el-option label="高中" value="高中" />
                  <el-option label="中专/技校" value="中专" />
                  <el-option label="大专" value="大专" />
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="开始时间">
                <el-date-picker
                    v-model="edu.startDate"
                    type="month"
                    placeholder="选择开始时间，没有则不填"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    @change="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="结束时间">
                <el-date-picker
                    v-model="edu.endDate"
                    type="month"
                    placeholder="选择结束时间，没有则不填"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    @change="handleChange"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 在校经历 -->
          <el-form-item label="在校经历">
            <el-input
                v-model="edu.description"
                type="textarea"
                :rows="3"
                placeholder="请描述您的在校经历，如：主要课程、获奖情况、社团活动等"
                maxlength="500"
                show-word-limit
                @input="handleChange"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 空状态 -->
      <div v-if="educationList.length === 0" class="empty-state">
        <el-empty description="还没有添加教育背景">
          <el-button type="primary" @click="addEducation">添加教育经历</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'

export default {
  name: 'ResumeEducation',
  components: {
    Plus,
    Delete
  },
  props: {
    modelValue: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    const educationList = reactive([...props.modelValue])

    // 监听props变化
    watch(() => props.modelValue, (newVal) => {
      educationList.splice(0, educationList.length, ...newVal)
    }, { deep: true })

    // 处理数据变化
    const handleChange = () => {
      emit('update:modelValue', [...educationList])
      emit('change')
    }

    // 添加教育经历
    const addEducation = () => {
      const newEducation = {
        id: Date.now(),
        school: '',
        major: '',
        degree: '',
        startDate: '',
        endDate: '',
        description: ''
      }
      educationList.push(newEducation)
      handleChange()
    }

    // 删除教育经历
    const removeEducation = (index) => {
      educationList.splice(index, 1)
      handleChange()
    }

    // 初始化：如果没有教育背景，添加一个空的
    if (educationList.length === 0) {
      addEducation()
    }

    return {
      educationList,
      handleChange,
      addEducation,
      removeEducation
    }
  }
}
</script>

<style scoped>
/* eslint-disable */
.resume-education {
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
.el-input__inner, .el-textarea__inner {
  font-size: 14px !important;
  height: 32px !important;
  min-height: 32px !important;
}
.el-button, .el-button--primary {
  font-size: 14px !important;
  height: 32px !important;
  padding: 0 16px !important;
}
/* 迁移自frontend，将scss语法改为css - 教育背景组件样式 */
.resume-education .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.resume-education .section-header h3 {
  margin: 0;
  color: var(--el-text-color-primary);
}

.resume-education .section-header {
  margin-top: 0 !important;
  padding-top: 0 !important;
}

.resume-education .education-list .education-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #e9ecef;
  /* min-height: 0; 移除，避免高度被限制 */
  height: auto;
  min-height: 350px; /* 新增：提升卡片最小高度 */
  box-sizing: border-box;
  overflow: visible; /* 保证内容不会被裁剪 */
}

.resume-education .education-list .education-item .education-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.resume-education .education-list .education-item .education-item-header .education-index {
  background: var(--el-color-success);
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.resume-education .empty-state {
  text-align: center;
  padding: 40px 0;
}

.el-select,
.el-select__wrapper,
.el-select .el-input__wrapper {
  background: transparent !important;
  box-shadow: none !important;
  height: 58px !important;
  min-height: 58px !important;
  line-height: 58px !important;
}
</style>
