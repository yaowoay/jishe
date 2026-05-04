<template>
  <div class="resume-skills">

    <div class="skills-list">
      <div
          v-for="(skill, index) in skillsList"
          :key="skill.id || index"
          class="skill-item"
      >
        <div class="skill-header">
          <span class="skill-index">{{ index + 1 }}</span>
          <el-button
              size="small"
              type="danger"
              @click="removeSkill(index)"
              :disabled="skillsList.length === 1"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>

        <el-form :model="skill" label-width="80px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>技能名称
                </template>
                <el-input
                    v-model="skill.name"
                    placeholder="如：JavaScript、项目管理"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="熟练程度">
                <el-rate
                    v-model="skill.level"
                    :max="5"
                    show-score
                    text-color="#ff9900"
                    score-template="{value}星"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="技能描述">
            <el-input
                v-model="skill.description"
                type="textarea"
                :rows="2"
                placeholder="详细描述技能掌握情况、使用经验、相关项目等"
                maxlength="200"
                show-word-limit
                @input="handleChange"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 空状态 -->
      <div v-if="skillsList.length === 0" class="empty-state">
        <el-empty description="还没有添加技能专长">
          <el-button type="primary" @click="addSkill">添加技能</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'

export default {
  name: 'ResumeSkills',
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
    const skillsList = reactive([...props.modelValue])

    // 监听props变化
    watch(() => props.modelValue, (newVal) => {
      skillsList.splice(0, skillsList.length, ...newVal)
    }, { deep: true })

    // 处理数据变化
    const handleChange = () => {
      emit('update:modelValue', [...skillsList])
      emit('change')
    }

    // 添加技能
    const addSkill = () => {
      const newSkill = {
        id: Date.now(),
        name: '',
        level: 0, // 默认0，保证评分可选
        description: ''
      }
      skillsList.push(newSkill)
      handleChange()
    }

    // 删除技能
    const removeSkill = (index) => {
      skillsList.splice(index, 1)
      handleChange()
    }

    // 处理星级点击 - 支持点击相同星级撤回
    const handleRateChange = (skill, value) => {
      // 如果点击的是当前已选中的星级，则撤回（设为null）
      if (skill.level === value) {
        skill.level = null
      } else {
        skill.level = value
      }
      handleChange()
    }

    // 初始化：如果没有技能，添加一个空的
    if (skillsList.length === 0) {
      addSkill()
    }

    return {
      skillsList,
      handleChange,
      addSkill,
      removeSkill,
      handleRateChange
    }
  }
}
</script>

<style scoped>
/* eslint-disable */
.resume-skills {
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
.el-button, .el-button--primary {
  font-size: 14px !important;
  height: 32px !important;
  padding: 0 16px !important;
}
.el-rate {
  font-size: 22px !important;
}
/* 迁移自frontend，将scss语法改为css - 技能专长组件样式 */
.resume-skills .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.resume-skills .section-header h3 {
  margin: 0;
  color: var(--el-text-color-primary);
}

.resume-skills .skills-list .skill-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #e9ecef;
  /* min-height: 0; 移除，避免高度被限制 */
  height: auto;
  box-sizing: border-box;
  overflow: visible; /* 保证内容不会被裁剪 */
}

.resume-skills .skills-list .skill-item .skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.resume-skills .skills-list .skill-item .skill-header .skill-index {
  background: var(--el-color-warning);
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

.resume-skills .empty-state {
  text-align: center;
  padding: 40px 0;
}
</style>
