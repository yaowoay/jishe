<template>
  <div class="resume-projects">


    <div class="projects-list">
      <div
          v-for="(project, index) in projectsList"
          :key="project.id || index"
          class="project-item"
      >
        <div class="project-item-header">
          <span class="project-index">{{ index + 1 }}</span>
          <el-button
              size="small"
              type="danger"
              text
              @click="removeProject(index)"
              :disabled="projectsList.length === 1"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>

        <el-form :model="project" label-width="100px">
          <!-- 基本信息 -->
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <span style="color: red;">*</span>项目名称
                </template>
                <el-input
                    v-model="project.name"
                    placeholder="请输入项目名称"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目角色">
                <el-input
                    v-model="project.role"
                    placeholder="如：前端开发工程师、项目负责人，没有则填无"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="开始时间" required>
                <el-date-picker
                    v-model="project.startDate"
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
                    v-model="project.endDate"
                    type="month"
                    placeholder="选择结束时间"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    @change="handleChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="技术栈">
                <el-input
                    v-model="project.technologies"
                    placeholder="如：Vue.js, Node.js"
                    @input="handleChange"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 项目描述 -->
          <el-form-item label="项目描述">
            <el-input
                v-model="project.description"
                type="textarea"
                :rows="4"
                placeholder="请描述项目背景、目标、主要功能、个人职责和项目成果，没有则填无"
                maxlength="800"
                show-word-limit
                @input="handleChange"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 空状态 -->
      <div v-if="projectsList.length === 0" class="empty-state">
        <el-empty description="还没有添加项目经验">
          <el-button type="primary" @click="addProject">添加项目经验</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'

export default {
  name: 'ResumeProjects',
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
    const projectsList = reactive([...props.modelValue])

    // 监听props变化
    watch(() => props.modelValue, (newVal) => {
      projectsList.splice(0, projectsList.length, ...newVal)
    }, { deep: true })

    // 处理数据变化
    const handleChange = () => {
      emit('update:modelValue', [...projectsList])
      emit('change')
    }

    // 添加项目经验
    const addProject = () => {
      const newProject = {
        id: Date.now(),
        name: '',
        role: '',
        startDate: '',
        endDate: '',
        technologies: '',
        description: ''
      }
      projectsList.push(newProject)
      handleChange()
    }

    // 删除项目经验
    const removeProject = (index) => {
      projectsList.splice(index, 1)
      handleChange()
    }

    // 初始化：如果没有项目经验，添加一个空的
    if (projectsList.length === 0) {
      addProject()
    }

    return {
      projectsList,
      handleChange,
      addProject,
      removeProject
    }
  }
}
</script>

<style scoped>
/* eslint-disable */
.resume-projects {
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
/* 迁移自frontend，将scss语法改为css - 项目经验组件样式 */
.resume-projects .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.resume-projects .section-header h3 {
  margin: 0;
  color: var(--el-text-color-primary);
}

.resume-projects .section-header {
  margin-top: 0 !important;
  padding-top: 0 !important;
}

.resume-projects .projects-list .project-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #e9ecef;
  /* min-height: 0; 移除，避免高度被限制 */
  height: auto;
  min-height: 370px; /* 新增：提升卡片最小高度 */
  box-sizing: border-box;
  overflow: visible; /* 保证内容不会被裁剪 */
}

.resume-projects .projects-list .project-item .project-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.resume-projects .projects-list .project-item .project-item-header .project-index {
  background: var(--el-color-info);
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

.resume-projects .empty-state {
  text-align: center;
  padding: 40px 0;
}
</style>
