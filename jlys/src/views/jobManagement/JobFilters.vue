<template>
  <div class="job-filters">
    <el-card class="filter-card" shadow="never">
      <template #header>
        <div class="filter-header">
          <span class="filter-title">
            <el-icon><Filter /></el-icon>
            高级筛选
          </span>
          <div class="filter-header-actions">
            <el-button
                type="text"
                @click="toggleCollapse"
                class="collapse-btn"
            >
              <el-icon><component :is="isCollapse ? 'Expand' : 'Fold'" /></el-icon>
              {{ isCollapse ? '展开' : '收起' }}
            </el-button>
            <el-button type="text" @click="resetFilters" class="reset-btn">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选内容区域 - 支持展开收起 -->
      <transition name="filter-collapse">
        <div class="filter-content" v-show="!isCollapse">
          <!-- 分组容器 - 按类别分组，提升可读性 -->
          <div class="filter-groups">
            <!-- 基础筛选组 -->
            <div class="filter-group">
              <div class="filter-group-title">
                <span>基础信息</span>
              </div>
              <!-- 关键词搜索 -->
              <div class="filter-row">
                <label class="filter-label">关键词</label>
                <el-input
                    v-model="filters.keyword"
                    placeholder="搜索职位、公司..."
                    clearable
                    class="filter-input"
                    @input="handleFilterChange"
                />
              </div>

              <!-- 薪资范围 -->
              <div class="filter-row">
                <label class="filter-label">薪资范围<br>(k/月)</label>
                <div class="salary-range">
                  <div class="salary-input-group">
                    <el-input-number
                        v-model="minSalary"
                        :min="0"
                        :max="500"
                        :step="1"
                        placeholder="最低薪资"
                        size="default"
                        @change="handleSalaryChange"
                        class="salary-input"
                        controls-position="right"
                    />
                    <!--                    <span class="salary-separator">-</span>-->
                    <el-input-number
                        v-model="maxSalary"
                        :min="0"
                        :max="500"
                        :step="1"
                        placeholder="最高薪资"
                        size="default"
                        @change="handleSalaryChange"
                        class="salary-input"
                        controls-position="right"
                    />
                    <span class="salary-unit"> </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 地域筛选组 -->
            <div class="filter-group">
              <div class="filter-group-title">地域筛选</div>
              <!-- 城市等级选择 -->
              <div class="filter-row">
                <label class="filter-label">城市等级</label>
                <div class="tag-group">
                  <el-tag
                      v-for="tier in cityTiers"
                      :key="tier.key"
                      :type="filters.cityTier === tier.key ? 'warning' : ''"
                      :effect="filters.cityTier === tier.key ? 'dark' : 'plain'"
                      @click="handleCityTierSelect(tier.key)"
                      class="filter-tag"
                  >
                    {{ tier.label }}
                  </el-tag>
                </div>
              </div>

              <!-- 具体城市选择 -->
              <div class="filter-row">
                <label class="filter-label">具体城市</label>
                <el-select
                    v-model="filters.city"
                    placeholder="选择具体城市"
                    clearable
                    class="filter-select"
                    @change="handleFilterChange"
                    filterable
                >
                  <el-option-group
                      v-for="group in cityGroups"
                      :key="group.label"
                      :label="group.label"
                  >
                    <el-option
                        v-for="city in group.cities"
                        :key="city"
                        :label="city"
                        :value="city"
                    />
                  </el-option-group>
                </el-select>
              </div>
            </div>

            <!-- 职位要求组 -->
            <div class="filter-group">
              <div class="filter-group-title">职位要求</div>
              <!-- 行业选择 -->
              <div class="filter-row">
                <label class="filter-label">行业</label>
                <el-select
                    v-model="filters.companyTags"
                    placeholder="选择行业"
                    clearable
                    class="filter-select"
                    @change="handleFilterChange"
                >
                  <el-option
                      v-for="companyTags in filterOptions.industries"
                      :key="companyTags"
                      :label="companyTags"
                      :value="companyTags"
                  />
                </el-select>
              </div>

              <!-- 经验要求 -->
              <div class="filter-row">
                <label class="filter-label">经验要求</label>
                <div class="tag-group">
                  <el-tag
                      v-for="exp in filterOptions.experiences"
                      :key="exp"
                      :type="filters.experience === exp ? 'primary' : ''"
                      :effect="filters.experience === exp ? 'dark' : 'plain'"
                      @click="handleExperienceSelect(exp)"
                      class="filter-tag"
                  >
                    {{ exp }}
                  </el-tag>
                </div>
              </div>

              <!-- 学历要求 -->
              <div class="filter-row">
                <label class="filter-label">学历要求</label>
                <el-select
                    v-model="filters.education"
                    placeholder="选择学历"
                    clearable
                    class="filter-select"
                    @change="handleFilterChange"
                >
                  <el-option
                      v-for="edu in filterOptions.educations"
                      :key="edu"
                      :label="edu"
                      :value="edu"
                  />
                </el-select>
              </div>
            </div>

            <!-- 公司信息组 -->
            <div class="filter-group">
              <div class="filter-group-title">公司信息</div>
              <!-- 公司规模 -->
              <div class="filter-row">
                <label class="filter-label">公司规模</label>
                <div class="tag-group">
                  <el-tag
                      v-for="scale in filterOptions.companyScales"
                      :key="scale"
                      :type="filters.companyScale === scale ? 'success' : ''"
                      :effect="filters.companyScale === scale ? 'dark' : 'plain'"
                      @click="handleCompanyScaleSelect(scale)"
                      class="filter-tag"
                  >
                    {{ scale }}
                  </el-tag>
                </div>
              </div>

              <!-- 融资状态 -->
              <div class="filter-row">
                <label class="filter-label">公司类型</label>
                <div class="tag-group">
                  <el-tag
                      v-for="status in filterOptions.companyTypes"
                      :key="status"
                      :type="filters.companyType === status ? 'warning' : ''"
                      :effect="filters.companyType === status ? 'dark' : 'plain'"
                      @click="handleFinancingStatusSelect(status)"
                      class="filter-tag"
                  >
                    {{ status }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>

          <!-- 已选标签组合 -->
          <div class="selected-filters" v-if="selectedTags.length > 0">
            <div class="selected-filters-header">
              <span class="selected-filters-title">已选条件：</span>
              <el-button
                  type="text"
                  size="small"
                  @click="clearAllFilters"
                  class="clear-all-btn"
              >
                清空全部
              </el-button>
            </div>
            <div class="selected-tags-container">
              <el-tag
                  v-for="tag in selectedTags"
                  :key="tag.key"
                  :type="tag.type"
                  closable
                  @close="removeFilter(tag)"
                  class="selected-tag"
              >
                {{ tag.label }}
              </el-tag>
            </div>
          </div>
        </div>
      </transition>

      <!-- 收起状态下只显示已选标签 -->
      <div class="collapsed-tags" v-show="isCollapse && selectedTags.length > 0">
        <div class="selected-tags-container">
          <el-tag
              v-for="tag in selectedTags"
              :key="tag.key"
              :type="tag.type"
              closable
              @close="removeFilter(tag)"
              class="selected-tag"
          >
            {{ tag.label }}
          </el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { Filter, Refresh, Expand, Fold } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['search', 'filter-change'])

// 展开/收起状态
const isCollapse = ref(false)

// 切换展开/收起
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 筛选条件
const filters = reactive({
  keyword: '',
  city: '',
  cityTier: '', // 新增城市等级筛选
  companyTags: '',
  experience: '',
  education: '',
  companyScale: '',
  companyType: ''
})

// 薪资范围 - 直接输入
const minSalary = ref(null)
const maxSalary = ref(null)

// 城市等级定义
const cityTiers = ref([
  { key: 'tier1', label: '一线城市' },
  { key: 'new-tier1', label: '新一线城市' },
  { key: 'tier2', label: '二线城市' },
  { key: 'others', label: '其他城市' }
])

// 城市分组数据
const cityData = {
  tier1: ['北京', '上海', '深圳', '杭州', '广州'],
  'new-tier1': ['成都', '重庆', '天津', '苏州', '南京', '武汉', '西安'],
  tier2: ['大连', '青岛', '宁波', '厦门', '长沙', '郑州', '济南', '合肥', '福州', '昆明', '石家庄', '太原', '南昌', '贵阳', '南宁', '海口'],
  others: ['银川', '西宁', '兰州', '乌鲁木齐', '拉萨', '呼和浩特', '哈尔滨', '长春', '沈阳']
}

// 城市分组（用于下拉选择）
const cityGroups = ref([
  { label: '一线城市', cities: cityData.tier1 },
  { label: '新一线城市', cities: cityData['new-tier1'] },
  { label: '二线城市', cities: cityData.tier2 },
  { label: '其他城市', cities: cityData.others }
])

// 筛选选项（基于数据库实际数据）
const filterOptions = reactive({
  // 37个城市选项（保持原有格式以兼容现有代码）
  cities: [
    ...cityData.tier1,
    ...cityData['new-tier1'],
    ...cityData.tier2,
    ...cityData.others
  ],
  // 30个行业选项
  industries: [
    // 技术类
    '计算机软件', '通信/电信/网络设备', '电子技术/半导体/集成电路', '互联网/电子商务', '计算机服务(系统、数据服)',
    // 金融类
    '金融/投资/证券', '银行',
    // 服务类
    '外包服务', '影视/媒体/艺术/文化传播', '政府/公共事业', '专业服务(咨询、人力资源)',
    // 制造类
    '新能源', '制药/生物工程', '机械/设备/重工', '电气/电力/水利', '汽车', '石油/化工/矿产/地质', '原材料和加工',
    // 其他
    '航天/航空', '快速消费品(食品、饮料、)'
  ],
  // 6个经验等级
  experiences: [
    '无需经验', '在校生/应届生', '1年经验', '2年经验', '3-4年经验', '5-7年经验', '8-9年经验', '10年以上经验'
  ],
  // 6个学历等级
  educations: ['大专', '本科', '硕士', '博士'],
  // 8个规模等级
  companyScales: [
    '少于50人', '50-150人', '150-500人', '500-1000人',
    '1000-5000人', '5000-10000人', '10000人以上'
  ],
  // 15个融资状态
  companyTypes: [
    '合资', '国企', '外资（欧美）', '外资（非欧美）', '已上市','民营','其他'
  ]
})

// 初始化筛选选项数据（直接使用数据库实际值，不依赖后端API）
const initFilterOptions = () => {
  // 筛选选项已在 filterOptions 中定义，直接使用数据库实际值
  console.log('使用数据库中的实际筛选选项')
  console.log('城市数量:', filterOptions.cities.length)
  console.log('行业数量:', filterOptions.industries.length)
  console.log('经验等级数量:', filterOptions.experiences.length)
  console.log('学历等级数量:', filterOptions.educations.length)
  console.log('公司规模数量:', filterOptions.companyScales.length)
  console.log('公司类型数量:', filterOptions.companyTypes.length)
}

// 防抖定时器
let debounceTimer = null

// 处理筛选条件变化 - 自动触发搜索
const handleFilterChange = () => {
  // 清除之前的定时器
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }

  // 设置防抖，300ms后执行搜索
  debounceTimer = setTimeout(() => {
    emit('search', getFilterParams())
  }, 300)
}

// 处理薪资范围变化
const handleSalaryChange = () => {
  handleFilterChange()
}

// 处理经验选择
const handleExperienceSelect = (exp) => {
  filters.experience = filters.experience === exp ? '' : exp
  handleFilterChange()
}

// 处理公司规模选择
const handleCompanyScaleSelect = (scale) => {
  filters.companyScale = filters.companyScale === scale ? '' : scale
  handleFilterChange()
}

// 处理融资状态选择
const handleFinancingStatusSelect = (status) => {
  filters.companyType = filters.companyType === status ? '' : status
  handleFilterChange()
}

// 处理城市等级选择
const handleCityTierSelect = (tier) => {
  filters.cityTier = filters.cityTier === tier ? '' : tier
  // 清空具体城市选择，避免冲突
  if (filters.cityTier) {
    filters.city = ''
  }
  handleFilterChange()
}

// 获取筛选参数
const getFilterParams = () => {
  const params = { ...filters }

  // 处理城市等级筛选
  if (params.cityTier && !params.city) {
    // 如果选择了城市等级但没有选择具体城市，则传递该等级的所有城市
    const tierCities = cityData[params.cityTier]
    if (tierCities && tierCities.length > 0) {
      // 将城市数组作为cities参数传递
      params.cities = tierCities
      delete params.city // 删除单个城市参数
    }
  }

  // 清理不需要的参数
  delete params.cityTier

  // 添加薪资范围（数据库中avg_salary已经是以千为单位，不需要再乘以1000）
  if (minSalary.value !== null && minSalary.value > 0) {
    params.minSalary = minSalary.value
  }
  if (maxSalary.value !== null && maxSalary.value > 0) {
    params.maxSalary = maxSalary.value
  }

  return params
}

// 计算已选标签
const selectedTags = computed(() => {
  const tags = []

  // 关键词
  if (filters.keyword) {
    tags.push({
      key: 'keyword',
      label: `关键词：${filters.keyword}`,
      type: 'info',
      value: filters.keyword
    })
  }

  // 城市等级
  if (filters.cityTier) {
    const tierLabel = cityTiers.value.find(tier => tier.key === filters.cityTier)?.label || filters.cityTier
    tags.push({
      key: 'cityTier',
      label: `城市等级：${tierLabel}`,
      type: 'warning',
      value: filters.cityTier
    })
  }

  // 具体城市
  if (filters.city) {
    tags.push({
      key: 'city',
      label: `城市：${filters.city}`,
      type: 'info',
      value: filters.city
    })
  }

  // 行业
  if (filters.companyTags) {
    tags.push({
      key: 'companyTags',
      label: `行业：${filters.companyTags}`,
      type: 'success',
      value: filters.companyTags
    })
  }

  // 经验要求
  if (filters.experience) {
    tags.push({
      key: 'experience',
      label: `经验：${filters.experience}`,
      type: 'primary',
      value: filters.experience
    })
  }

  // 学历要求
  if (filters.education) {
    tags.push({
      key: 'education',
      label: `学历：${filters.education}`,
      type: 'info',
      value: filters.education
    })
  }

  // 公司规模
  if (filters.companyScale) {
    tags.push({
      key: 'companyScale',
      label: `公司规模：${filters.companyScale}`,
      type: 'success',
      value: filters.companyScale
    })
  }

  // 融资状态
  if (filters.companyType) {
    tags.push({
      key: 'companyType',
      label: `公司类型：${filters.companyType}`,
      type: 'warning',
      value: filters.companyType
    })
  }

  // 薪资范围
  if (minSalary.value !== null || maxSalary.value !== null) {
    let salaryLabel = '薪资：'
    if (minSalary.value !== null && maxSalary.value !== null) {
      salaryLabel += `${minSalary.value}K-${maxSalary.value}K`
    } else if (minSalary.value !== null) {
      salaryLabel += `${minSalary.value}K以上`
    } else if (maxSalary.value !== null) {
      salaryLabel += `${maxSalary.value}K以下`
    }

    tags.push({
      key: 'salary',
      label: salaryLabel,
      type: 'danger',
      value: 'salary'
    })
  }

  return tags
})

// 移除单个筛选条件
const removeFilter = (tag) => {
  if (tag.key === 'salary') {
    minSalary.value = null
    maxSalary.value = null
  } else {
    filters[tag.key] = ''
  }
  handleFilterChange()
}

// 清空所有筛选条件
const clearAllFilters = () => {
  Object.keys(filters).forEach(key => {
    filters[key] = ''
  })
  minSalary.value = null
  maxSalary.value = null
  handleFilterChange()
}

// 重置筛选条件
const resetFilters = () => {
  clearAllFilters()
}

// 组件挂载时初始化筛选选项
onMounted(() => {
  initFilterOptions()
})

// 组件销毁时清理定时器
onUnmounted(() => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
    debounceTimer = null
  }
})
</script>

<style scoped>
.job-filters {
  margin-bottom: 20px;
}

.filter-card {
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.filter-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 8px;
}

.filter-header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1f2937;
  font-size: 16px;
  letter-spacing: -0.01em;
}

.collapse-btn {
  color: #409eff;
  padding: 0;
  font-size: 14px;
}

.collapse-btn:hover {
  color: #66b1ff;
}

.reset-btn {
  color: #909399;
  padding: 0;
}

.reset-btn:hover {
  color: #409eff;
}

/* 折叠动画 */
.filter-collapse-enter-from,
.filter-collapse-leave-to {
  max-height: 0;
  opacity: 0;
}

.filter-collapse-enter-to,
.filter-collapse-leave-from {
  max-height: 2000px;
  opacity: 1;
}

.filter-collapse-enter-active,
.filter-collapse-leave-active {
  transition: all 0.3s cubic-bezier(0.38, 0.1, 0.36, 0.9);
  overflow: hidden;
}

.filter-content {
  padding: 0 16px 16px;
}

/* 分组样式 */
.filter-groups {
  display: flex;
  gap: 20px;
  margin-top: 16px;
}

.filter-group {

  padding: 0 0 16px 0;
  border-radius: 12px;
  border: 1px solid #a7c7ec;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
  transition: all 0.25s ease;
  overflow: hidden;
}
/* 第一个筛选组（基础信息）宽度 */
.filter-group:nth-child(1) {
  width: 500px;
}


.filter-group-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
  padding-bottom: 8px;
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
  background-color: #f0f7ff;
}

.filter-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 16px;
  padding: 0  16px 0px 5px;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-label {
  min-width: 80px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-weight: 600;
  color: #1f2937;
  line-height: 26px;
  flex-shrink: 0;
  font-size: 14px;
}

.filter-input,
.filter-select {
  flex: 1;
  max-width: 300px;
}

/* 统一控件高度 - 与标签高度保持一致 */
.filter-input :deep(.el-input__wrapper),
.filter-select :deep(.el-input__wrapper) {
  height: 26px;
  min-height: 26px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.filter-input :deep(.el-input__wrapper):hover,
.filter-select :deep(.el-input__wrapper):hover {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.filter-input :deep(.el-input__wrapper).is-focus,
.filter-select :deep(.el-input__wrapper).is-focus {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.filter-input :deep(.el-input__inner),
.filter-select :deep(.el-input__inner) {
  height: 26px;
  line-height: 26px;
}

/* 输入框和选择框字体样式 */
.filter-input :deep(.el-input__inner),
.filter-select :deep(.el-select__input),
.filter-select :deep(.el-input__inner) {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 14px;
  font-weight: 400;
  letter-spacing: -0.005em;
  color: #374151;
}

.filter-input :deep(.el-input__inner)::placeholder,
.filter-select :deep(.el-input__inner)::placeholder {
  color: #9ca3af;
  font-weight: 400;
}

.salary-range {
  flex: 1;
  max-width: 400px;
}

.salary-input-group {
  display: flex;
  align-items: center;
  gap: 5px;
  flex-wrap: wrap;
}

.salary-input {
  width: 130px;
}

/* 统一薪资输入框高度 */
.salary-input :deep(.el-input__wrapper) {
  height: 26px;
  min-height: 26px;
  border-radius: 6px;
}

.salary-input :deep(.el-input__inner) {
  height: 26px;
  line-height: 26px;
}

/* 自定义数字输入框的上下箭头样式 */
.salary-input :deep(.el-input-number__increase),
.salary-input :deep(.el-input-number__decrease) {
  width: 24px;
  height: 16px;
  line-height: 16px;
  border: none;
  background: transparent;
  color: #909399;
  transition: all 0.2s ease;
}

.salary-input :deep(.el-input-number__increase):active,
.salary-input :deep(.el-input-number__decrease):active {
  color: #1d4ed8;
  transform: scale(1.1);
}

.salary-input :deep(.el-input-number__increase i),
.salary-input :deep(.el-input-number__decrease i) {
  font-size: 12px;
  font-weight: bold;
}

.salary-input :deep(.el-input-number__increase):active i,
.salary-input :deep(.el-input-number__decrease):active i {
  font-weight: 900;
  font-size: 13px;
}

.salary-separator {
  color: #606266;
  font-weight: 600;
  font-size: 16px;
  margin: 0 4px;
}

.salary-unit {
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  margin-left: 8px;
  letter-spacing: -0.005em;
}

/* 薪资输入框字体样式 */
.salary-input :deep(.el-input__inner) {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 13px;
  font-weight: 400;
  letter-spacing: -0.005em;
  color: #374151;
}

.salary-input :deep(.el-input__inner)::placeholder {
  color: #9ca3af;
  font-weight: 400;
}

/* 已选标签组合样式 */
.selected-filters {
  margin-top: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
  border: 1px solid #e1e8ff;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.selected-filters-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.selected-filters-title {
  font-size: 13px;
  font-weight: 600;
  color: #1f2937;
  letter-spacing: -0.01em;
}

.clear-all-btn {
  color: #f56c6c;
  font-size: 12px;
  padding: 0;
  height: auto;
  line-height: 1;
  font-weight: 500;
  letter-spacing: -0.005em;
}

.clear-all-btn:hover {
  color: #f78989;
}

/* 收起状态下的标签展示 */
.collapsed-tags {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}

.selected-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.selected-tag {
  margin: 0;
  font-size: 12px;
  border-radius: 16px;
  padding: 4px 12px;
  transition: all 0.3s ease;
  font-weight: 500;
  letter-spacing: -0.005em;
}

.selected-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* Element Plus Slider 自定义样式 */
.salary-slider :deep(.el-slider__runway) {
  background: linear-gradient(90deg, #e4e7ed 0%, #f5f7fa 100%);
  height: 8px;
  border-radius: 4px;
}

.salary-slider :deep(.el-slider__bar) {
  background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
  height: 8px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.salary-slider :deep(.el-slider__button) {
  width: 20px;
  height: 20px;
  background: white;
  border: 3px solid #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.3s ease;
}

.salary-slider :deep(.el-slider__button:hover) {
  transform: scale(1.2);
  border-color: #67c23a;
  box-shadow: 0 6px 16px rgba(103, 194, 58, 0.4);
}

.salary-slider :deep(.el-slider__button-wrapper:hover .el-slider__button) {
  transform: scale(1.2);
}

.salary-slider :deep(.el-slider__stop) {
  background: #c0c4cc;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  opacity: 0.6;
}

.salary-slider :deep(.el-slider__marks-text) {
  color: #909399;
  font-size: 12px;
  font-weight: 500;
}

.tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
}

.filter-tag {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  height: 24px;
  line-height: 26px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  border-radius: 6px;
}

.filter-tag:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25),
  0 0 0 2px rgba(64, 158, 255, 0.15),
  0 0 16px rgba(64, 158, 255, 0.1);
}

/* 不同类型标签的特定悬浮效果 */
.filter-tag.el-tag--warning:hover {
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.25),
  0 0 0 2px rgba(230, 162, 60, 0.15),
  0 0 16px rgba(230, 162, 60, 0.1);
}

.filter-tag.el-tag--primary:hover {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25),
  0 0 0 2px rgba(64, 158, 255, 0.15),
  0 0 16px rgba(64, 158, 255, 0.1);
}

.filter-tag.el-tag--success:hover {
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.25),
  0 0 0 2px rgba(103, 194, 58, 0.15),
  0 0 16px rgba(103, 194, 58, 0.1);
}
</style>