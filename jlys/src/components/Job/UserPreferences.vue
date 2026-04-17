<template>
  <div class="user-preferences">
    <el-card class="preference-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>个人偏好设置</span>
          <el-button type="primary" @click="savePreferences" :loading="saving">
            <el-icon><Check /></el-icon>
            保存设置
          </el-button>
        </div>
      </template>

      <el-form :model="preferences" label-width="120px" class="preference-form">
        <!-- 薪资偏好 -->
        <el-form-item label="期望薪资">
          <el-row :gutter="10">
            <el-col :span="11">
              <el-input-number 
                v-model="preferences.minSalary" 
                :min="0" 
                :max="100" 
                placeholder="最低薪资"
                style="width: 100%"
              />
            </el-col>
            <el-col :span="2" style="text-align: center">
              <span>-</span>
            </el-col>
            <el-col :span="11">
              <el-input-number 
                v-model="preferences.maxSalary" 
                :min="0" 
                :max="100" 
                placeholder="最高薪资"
                style="width: 100%"
              />
            </el-col>
          </el-row>
          <div class="form-tip">单位：千元/月</div>
        </el-form-item>

        <!-- 城市偏好 -->
        <el-form-item label="偏好城市">
          <el-select 
            v-model="preferences.preferredCities" 
            multiple 
            placeholder="选择偏好城市"
            style="width: 100%"
          >
            <el-option 
              v-for="city in cityOptions" 
              :key="city.value" 
              :label="city.label" 
              :value="city.value"
            />
          </el-select>
        </el-form-item>

        <!-- 行业偏好 -->
        <el-form-item label="偏好行业">
          <el-select 
            v-model="preferences.preferredIndustries" 
            multiple 
            placeholder="选择偏好行业"
            style="width: 100%"
          >
            <el-option 
              v-for="industry in industryOptions" 
              :key="industry.value" 
              :label="industry.label" 
              :value="industry.value"
            />
          </el-select>
        </el-form-item>

        <!-- 公司规模偏好 -->
        <el-form-item label="公司规模">
          <el-checkbox-group v-model="preferences.companyScales">
            <el-checkbox label="startup">创业公司</el-checkbox>
            <el-checkbox label="small">小型公司(50人以下)</el-checkbox>
            <el-checkbox label="medium">中型公司(50-500人)</el-checkbox>
            <el-checkbox label="large">大型公司(500人以上)</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <!-- 工作经验要求 -->
        <el-form-item label="经验要求">
          <el-radio-group v-model="preferences.experienceLevel">
            <el-radio label="fresh">应届生</el-radio>
            <el-radio label="junior">1-3年</el-radio>
            <el-radio label="mid">3-5年</el-radio>
            <el-radio label="senior">5年以上</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 推荐算法权重 -->
        <el-form-item label="推荐策略">
          <div class="algorithm-weights">
            <div class="weight-item">
              <span>协同过滤</span>
              <el-slider 
                v-model="preferences.algorithmWeights.collaborative" 
                :min="0" 
                :max="100"
                show-input
              />
            </div>
            <div class="weight-item">
              <span>内容推荐</span>
              <el-slider 
                v-model="preferences.algorithmWeights.content" 
                :min="0" 
                :max="100"
                show-input
              />
            </div>
            <div class="weight-item">
              <span>热门推荐</span>
              <el-slider 
                v-model="preferences.algorithmWeights.trending" 
                :min="0" 
                :max="100"
                show-input
              />
            </div>
          </div>
        </el-form-item>

        <!-- 技能标签 -->
        <el-form-item label="技能标签">
          <div class="skill-tags">
            <el-tag
              v-for="skill in preferences.skills"
              :key="skill"
              closable
              @close="removeSkill(skill)"
              style="margin-right: 8px; margin-bottom: 8px;"
            >
              {{ skill }}
            </el-tag>
            <el-input
              v-if="inputVisible"
              ref="inputRef"
              v-model="inputValue"
              class="skill-input"
              size="small"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            <el-button v-else class="button-new-tag" size="small" @click="showInput">
              + 添加技能
            </el-button>
          </div>
        </el-form-item>

        <!-- 推荐频率 -->
        <el-form-item label="推荐频率">
          <el-radio-group v-model="preferences.recommendationFrequency">
            <el-radio label="realtime">实时推荐</el-radio>
            <el-radio label="daily">每日推荐</el-radio>
            <el-radio label="weekly">每周推荐</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 偏好预览 -->
    <el-card class="preview-card" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <span>偏好预览</span>
      </template>
      <div class="preference-preview">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="期望薪资">
            {{ preferences.minSalary }}K - {{ preferences.maxSalary }}K
          </el-descriptions-item>
          <el-descriptions-item label="偏好城市">
            {{ preferences.preferredCities.join(', ') || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="偏好行业">
            {{ preferences.preferredIndustries.join(', ') || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="公司规模">
            {{ preferences.companyScales.join(', ') || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="经验要求">
            {{ getExperienceLevelText(preferences.experienceLevel) }}
          </el-descriptions-item>
          <el-descriptions-item label="技能数量">
            {{ preferences.skills.length }} 个技能
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'           
import { ElMessage } from 'element-plus'           
import { Check } from '@element-plus/icons-vue'           

// 响应式数据
const saving = ref(false)           
const inputVisible = ref(false)           
const inputValue = ref('')           
const inputRef = ref()           

// 用户偏好数据
const preferences = reactive({
  minSalary: 10,
  maxSalary: 20,
  preferredCities: ['北京', '上海'],
  preferredIndustries: ['互联网', '软件开发'],
  companyScales: ['medium', 'large'],
  experienceLevel: 'junior',
  algorithmWeights: {
    collaborative: 40,
    content: 40,
    trending: 20
  },
  skills: ['Java', 'Spring', 'Vue.js'],
  recommendationFrequency: 'realtime'
})

// 选项数据
const cityOptions = ref([
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '深圳', value: '深圳' },
  { label: '杭州', value: '杭州' },
  { label: '广州', value: '广州' },
  { label: '成都', value: '成都' },
  { label: '武汉', value: '武汉' },
  { label: '西安', value: '西安' }
])

const industryOptions = ref([
  { label: '互联网', value: '互联网' },
  { label: '软件开发', value: '软件开发' },
  { label: '人工智能', value: '人工智能' },
  { label: '金融科技', value: '金融科技' },
  { label: '电子商务', value: '电子商务' },
  { label: '游戏', value: '游戏' },
  { label: '教育', value: '教育' },
  { label: '医疗健康', value: '医疗健康' }
])

// 方法定义
const savePreferences = async () => {
  saving.value = true
  try {
    // 这里应该调用API保存用户偏好
    await new Promise(resolve => setTimeout(resolve, 1000))            // 模拟API调用
    ElMessage.success('偏好设置保存成功！')           
    
    // 触发推荐更新事件
    emit('preferences-updated', preferences)           
  } catch (error) {
    ElMessage.error('保存失败，请重试')           
  } finally {
    saving.value = false           
  }
}           

const removeSkill = (skill) => {
  const index = preferences.skills.indexOf(skill)           
  if (index > -1) {
    preferences.skills.splice(index, 1)           
  }
}           

const showInput = () => {
  inputVisible.value = true           
  nextTick(() => {
    inputRef.value.input.focus()           
  })           
}           

const handleInputConfirm = () => {
  if (inputValue.value && !preferences.skills.includes(inputValue.value)) {
    preferences.skills.push(inputValue.value)           
  }
  inputVisible.value = false           
  inputValue.value = ''           
}           

const getExperienceLevelText = (level) => {
  const levelMap = {
    fresh: '应届生',
    junior: '1-3年',
    mid: '3-5年',
    senior: '5年以上'
  }           
  return levelMap[level] || '未设置'           
}           

// 事件定义
const emit = defineEmits(['preferences-updated'])           

// 组件挂载时加载用户偏好
onMounted(() => {
  // 这里可以从API加载用户的现有偏好设置
  console.log('加载用户偏好设置')           
})           
</script>

<style scoped>
.user-preferences {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preference-form {
  max-width: 800px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.algorithm-weights {
  width: 100%;
}

.weight-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.weight-item span {
  width: 100px;
  margin-right: 20px;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.skill-input {
  width: 90px;
  margin-right: 8px;
  vertical-align: bottom;
}

.button-new-tag {
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.preference-preview {
  margin-top: 10px;
}
</style>
