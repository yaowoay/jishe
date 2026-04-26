<template>
  <div
    class="step-flex-card"
    :class="{ active: hovered || active }"
    @mouseenter="$emit('mouseenter')"
    @mouseleave="$emit('mouseleave')"
  >
    <div class="step-badge"><span>②</span></div>
    <div v-show="hovered || active">
      <div class="step-header"><el-icon class="step-icon"><i class="el-icon-edit"></i></el-icon> 第二步：笔试设置</div>
      <el-form
        :model="localSettings"
        :rules="examRules"
        ref="examFormRef"
        label-width="120px"
        class="exam-form"
      >
        <el-form-item label="职位名称" prop="jobPosition">
          <el-input v-model="localSettings.jobPosition" placeholder="请输入目标职位" clearable @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="技能要求" prop="skills">
          <el-input v-model="localSettings.skills" placeholder="请输入相关技能，用逗号分隔" clearable @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="工作经验" prop="experience">
          <el-select v-model="localSettings.experience" placeholder="请选择工作经验" style="width: 100%" @change="emitUpdate">
            <el-option label="不足一年" value="LESS_THAN_ONE_YEAR" />
            <el-option label="1-3年" value="ONE_TO_THREE_YEARS" />
            <el-option label="3-5年" value="THREE_TO_FIVE_YEARS" />
            <el-option label="5-10年" value="FIVE_TO_TEN_YEARS" />
            <el-option label="10年以上" value="MORE_THAN_TEN_YEARS" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目数量" prop="questionCount">
          <el-input-number v-model="localSettings.questionCount" :min="3" :max="20" :step="1" style="width: 100%" @change="emitUpdate" />
          <div class="form-tip">建议选择 5-15 道题目，考试时间约 15-45 分钟</div>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficultyLevel">
          <el-select v-model="localSettings.difficultyLevel" placeholder="请选择难度等级" style="width: 100%" @change="emitUpdate">
            <el-option label="初级" value="JUNIOR">
              <span style="float: left">初级</span>
              <span style="float: right; color: #8492a6; font-size: 13px">适合新手</span>
            </el-option>
            <el-option label="中级" value="INTERMEDIATE">
              <span style="float: left">中级</span>
              <span style="float: right; color: #8492a6; font-size: 13px">有一定经验</span>
            </el-option>
            <el-option label="高级" value="SENIOR">
              <span style="float: left">高级</span>
              <span style="float: right; color: #8492a6; font-size: 13px">经验丰富</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="重点领域" prop="focusArea">
          <el-input v-model="localSettings.focusArea" placeholder="请输入重点考察领域" clearable @change="emitUpdate" />
        </el-form-item>
      </el-form>
    </div>

    <div v-show="!(hovered || active)">
      <div class="step-header mini"><el-icon class="step-icon"><i class="el-icon-edit"></i></el-icon> 笔试设置</div>
      <div v-if="isComplete" class="mini-brief set">
        <el-icon class="mini-done"><i class="el-icon-check"></i></el-icon>
        <span>{{ localSettings.jobPosition }} / {{ localSettings.questionCount }}题</span>
      </div>
      <div v-else class="mini-brief">配置职位、技能等</div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  hovered: Boolean,
  active: Boolean,
  settings: Object,
  isComplete: Boolean
})

const emit = defineEmits(['mouseenter', 'mouseleave', 'update'])

const examFormRef = ref()
const localSettings = ref({
  jobPosition: '',
  skills: '',
  experience: '',
  questionCount: 5,
  difficultyLevel: '',
  focusArea: ''
})

watch(() => props.settings, (newVal) => {
  if (newVal) {
    localSettings.value = { ...newVal }
  }
}, { immediate: true, deep: true })

const examRules = {
  jobPosition: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '职位名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  skills: [
    { required: true, message: '请输入技能要求', trigger: 'blur' },
    { min: 3, max: 200, message: '技能要求长度在 3 到 200 个字符', trigger: 'blur' }
  ],
  experience: [
    { required: true, message: '请选择工作经验', trigger: 'change' }
  ],
  questionCount: [
    { required: true, message: '请选择题目数量', trigger: 'blur' }
  ],
  difficultyLevel: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  focusArea: [
    { required: true, message: '请输入重点领域', trigger: 'blur' },
    { min: 2, max: 100, message: '重点领域长度在 2 到 100 个字符', trigger: 'blur' }
  ]
}

function emitUpdate() {
  emit('update', localSettings.value)
}
</script>

<style scoped>
.step-flex-card {
  background: rgba(255,255,255,0.92);
  border-radius: 32px;
  box-shadow: 0 12px 48px 0 rgba(80,120,200,0.13), 0 2px 16px #e6e6e6;
  transition: all 0.35s cubic-bezier(.4,2,.6,1);
  width: 420px;
  min-height: 340px;
  padding: 56px 38px 32px 38px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  backdrop-filter: blur(8px);
  border: 2px solid #e0e7ff;
}

.step-flex-card.active {
  z-index: 2;
  box-shadow: 0 24px 64px #b3c0d1, 0 4px 24px #409eff44;
  transform: scale(1.09) translateY(-8px);
  cursor: default;
  border: 3px solid #409eff;
  background: rgba(255,255,255,0.98);
}

.step-badge {
  position: absolute;
  top: 18px;
  left: 28px;
  background: linear-gradient(135deg, #e3f0ff 60%, #b3d8ff 100%);
  color: #409eff;
  font-size: 1.3rem;
  font-weight: 700;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px #e0e7ff;
  border: 2px solid #b3d8ff;
  z-index: 2;
}

.step-header {
  font-size: 26px;
  font-weight: 700;
  color: #222;
  margin-bottom: 18px;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.step-header.mini {
  font-size: 22px;
  margin-bottom: 12px;
}

.step-icon {
  font-size: 1.5em;
  color: #409eff;
}

.exam-form {
  padding: 20px 0;
}

.form-tip {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

.mini-brief {
  font-size: 18px;
  color: #8a98b3;
  margin-top: 12px;
  font-weight: 500;
  text-align: left;
  letter-spacing: 1px;
}

.mini-brief.set {
  color: #67c23a;
  font-weight: 600;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.mini-done {
  color: #67c23a;
  font-size: 1.2em;
}
</style>
