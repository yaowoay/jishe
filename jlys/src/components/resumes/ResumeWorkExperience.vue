<template>
  <div class="resume-work-experience">


    <div class="work-list">
      <div
          v-for="(work, index) in workList"
          :key="work.id || index"
          class="work-item"
      >
        <div class="work-item-header">
          <span class="work-index">{{ index + 1 }}</span>
          <el-button
              size="small"
              type="danger"
              text
              @click="removeWorkExperience(index)"
              :disabled="workList.length === 1"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>

        <el-form :model="work" label-width="100px">
          <!-- 基本信息 -->
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>公司名称
                </template>
                <el-input
                    v-model="work.company"
                    placeholder="请输入公司名称"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>职位名称
                </template>
                <el-input
                    v-model="work.position"
                    placeholder="请输入职位名称"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>开始时间
                </template>
                <el-date-picker
                    v-model="work.startDate"
                    type="month"
                    placeholder="选择开始时间"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    @change="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="结束时间">
                <el-date-picker
                    v-model="work.endDate"
                    type="month"
                    placeholder="选择结束时间，在职则勾选至今"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    :disabled="work.isCurrent"
                    @change="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="至今">
                <el-checkbox
                    v-model="work.isCurrent"
                    @change="handleCurrentChange(work)"
                >
                  目前在职
                </el-checkbox>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 工作描述 -->
          <el-form-item label="工作职责" class="work-resp-item">
            <el-input
                v-model="work.responsibilities"
                type="textarea"
                :rows="3"
                placeholder="请详细描述您的工作职责和日常工作内容，没有则填无"
                maxlength="500"
                show-word-limit
                @input="handleChange"
            />
          </el-form-item>

          <el-form-item label="工作成果">
            <el-input
                v-model="work.achievements"
                type="textarea"
                :rows="4"
                placeholder="请描述您在该职位上取得的主要成果和业绩（建议量化描述），没有则填无"
                maxlength="800"
                show-word-limit
                @input="handleChange"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 空状态 -->
      <div v-if="workList.length === 0" class="empty-state">
        <el-empty description="还没有添加工作经历">
          <el-button type="primary" @click="addWorkExperience">添加第一份工作经历</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'

export default {
  name: 'ResumeWorkExperience',
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
    const workList = reactive([...props.modelValue])

    // 监听props变化
    watch(() => props.modelValue, (newVal) => {
      workList.splice(0, workList.length, ...newVal)
    }, { deep: true })

    // 处理数据变化
    const handleChange = () => {
      emit('update:modelValue', [...workList])
      emit('change')
    }

    // 添加工作经历
    const addWorkExperience = () => {
      const newWork = {
        id: Date.now(),
        company: '',
        position: '',
        startDate: '',
        endDate: '',
        isCurrent: false,
        responsibilities: '',
        achievements: ''
      }
      workList.push(newWork)
      handleChange()
    }

    // 删除工作经历
    const removeWorkExperience = (index) => {
      workList.splice(index, 1)
      handleChange()
    }

    // 处理"至今"变化
    const handleCurrentChange = (work) => {
      if (work.isCurrent) {
        work.endDate = ''
      }
      handleChange()
    }

    // 初始化：如果没有工作经历，添加一个空的
    if (workList.length === 0) {
      addWorkExperience()
    }

    return {
      workList,
      handleChange,
      addWorkExperience,
      removeWorkExperience,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
/* eslint-disable */
.resume-work-experience {
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
.el-input, .el-select, .el-date-picker, .el-textarea {
  font-size: 14px !important;
  height: 32px !important;
  min-height: 32px !important;
}
.el-input__inner, .el-textarea__inner {
  font-size: 14px !important;
  height: 32px !important;
  min-height: 32px !important;
}
.el-input__wrapper, .el-date-editor.el-input__wrapper {
  height: 32px !important;
  min-height: 32px !important;
  font-size: 14px !important;
  box-sizing: border-box;
  align-items: center;
}
.el-button, .el-button--primary {
  font-size: 14px !important;
  height: 32px !important;
  padding: 0 16px !important;
}
/* 迁移自frontend，将scss语法改为css - 工作经历组件样式 */
.resume-work-experience .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.resume-work-experience .section-header h3 {
  margin: 0;
  color: var(--el-text-color-primary);
}

.resume-work-experience .work-list .work-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 32px 24px 32px 24px;
  margin-bottom: 16px;
  border: 1px solid #e9ecef;
  /* min-height: 0; 移除，避免高度被限制 */
  height: auto;
  min-height: 500px; /* 新增：提升卡片最小高度 */
  box-sizing: border-box;
  overflow: visible; /* 保证内容不会被裁剪 */
}

.resume-work-experience .work-list .work-item .work-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.resume-work-experience .work-list .work-item .work-item-header .work-index {
  background: var(--el-color-primary);
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

.resume-work-experience .empty-state {
  text-align: center;
  padding: 40px 0;
}

.resume-work-experience, .resume-work-experience .section-header, .resume-work-experience .work-item {
  margin-top: 0 !important;
  padding-top: 0 !important;
  margin-bottom: 0 !important;
}
/* 建议卡片样式优化 */
.advice-card {
  font-size: 20px;
  line-height: 1.8;
}
.advice-card .advice-title {
  font-size: 22px;
  font-weight: bold;
}
/* 在样式中为工作职责和工作成果输入框之间添加更大间隔 */
.work-resp-item {
  margin-bottom: 80px !important; /* 增大输入框间距 */
}
</style>
