<template>
  <div class="skill-association-rules">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><Link /></el-icon>
          技能关联规则分析
        </h1>
        <p class="subtitle">基于数据挖掘的技能关联规则发现与可视化</p>
      </div>
      <div class="action-section">
        <el-button type="primary" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button @click="exportData">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <template #header>
        <div class="card-header">
          <span>筛选条件</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="规则类型">
            <el-select v-model="filters.ruleType" placeholder="全部类型" clearable @change="handleFilterChange">
              <el-option label="全部类型" value="" />
              <el-option label="技能推荐" value="技能推荐" />
              <el-option label="岗位匹配" value="岗位匹配" />
              <el-option label="技能组合" value="技能组合" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="排序方式">
            <el-select v-model="filters.sortBy" placeholder="选择排序" @change="handleFilterChange">
              <el-option label="置信度降序" value="confidence" />
              <el-option label="提升度降序" value="lift" />
              <el-option label="支持度降序" value="support" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="前N条">
            <el-input-number v-model="filters.topN" :min="10" :max="100" :step="10" @change="handleFilterChange" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="技能搜索">
            <el-input
              v-model="filters.skillSearch"
              placeholder="输入技能名称"
              clearable
              @clear="handleFilterChange"
              @keyup.enter="handleFilterChange"
            >
              <template #append>
                <el-button @click="handleFilterChange">搜索</el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <!-- 职位技能要求搜索区域 -->
      <el-divider content-position="left">
        <span style="font-weight: 600; color: #409eff;">职位技能要求查询</span>
      </el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="职位名称">
            <el-input
              v-model="positionSearch"
              placeholder="输入职位名称，如：Java开发工程师"
              clearable
              @clear="clearPositionSkills"
              @keyup.enter="searchPositionSkills"
            >
              <template #append>
                <el-button type="primary" @click="searchPositionSkills" :loading="positionLoading">
                  <el-icon><Search /></el-icon>
                  查询技能
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <el-alert
            v-if="positionSearch && !positionSkills.length && !positionLoading"
            title="提示"
            type="info"
            :closable="false"
            show-icon
          >
            请输入职位名称并点击"查询技能"按钮，查看该职位需要的技能列表
          </el-alert>
        </el-col>
      </el-row>
    </el-card>

    <!-- 职位技能要求展示卡片 -->
    <el-card v-if="positionSkills.length > 0" class="position-skills-card">
      <template #header>
        <div class="card-header">
          <span>职位技能要求：{{ selectedPositionName }}</span>
          <el-tag type="success" size="small">共 {{ positionSkills.length }} 项技能</el-tag>
        </div>
      </template>
      <div class="position-skills-content">
        <div class="position-info">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="职位名称">{{ selectedPositionName }}</el-descriptions-item>
            <el-descriptions-item label="岗位数量">{{ positionStats.totalJobs }}</el-descriptions-item>
            <el-descriptions-item label="企业数量">{{ positionStats.totalCompanies }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="skills-display">
          <h4 style="margin: 20px 0 15px 0; color: #1d2129;">技能列表（按需求频次排序）</h4>
          <div class="skills-tags">
            <el-tag
              v-for="(skill, index) in positionSkills"
              :key="index"
              :type="getSkillTagType(skill.skillFreq)"
              size="large"
              effect="dark"
              style="margin: 8px; padding: 8px 16px; font-size: 14px;"
            >
              {{ skill.skillName }}
              <el-tooltip content="该技能在该职位中的出现频次" placement="top">
                <span style="margin-left: 8px; font-weight: bold;">({{ skill.skillFreq }})</span>
              </el-tooltip>
            </el-tag>
          </div>
        </div>
        <div class="skills-table">
          <el-table
            :data="positionSkills"
            stripe
            style="width: 100%"
            :default-sort="{ prop: 'skillFreq', order: 'descending' }"
          >
            <el-table-column prop="skillName" label="技能名称" width="200" />
            <el-table-column prop="skillFreq" label="需求频次" width="150" sortable>
              <template #default="{ row }">
                <el-tag :type="getSkillTagType(row.skillFreq)" size="small">
                  {{ row.skillFreq }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="jobCount" label="岗位数量" width="120" sortable />
            <el-table-column prop="companyCount" label="企业数量" width="120" sortable />
            <el-table-column prop="city" label="城市" width="120" />
          </el-table>
        </div>
      </div>
    </el-card>

    <!-- 统计概览 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6f7ff;">
              <el-icon color="#1890ff" :size="32"><Link /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalRules }}</div>
              <div class="stat-label">总规则数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f6ffed;">
              <el-icon color="#52c41a" :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.avgConfidence.toFixed(2) }}</div>
              <div class="stat-label">平均置信度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fff7e6;">
              <el-icon color="#fa8c16" :size="32"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.avgLift.toFixed(2) }}</div>
              <div class="stat-label">平均提升度</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fff1f0;">
              <el-icon color="#ff4d4f" :size="32"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.uniqueSkills }}</div>
              <div class="stat-label">涉及技能数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 关联规则网络图 -->
    <el-card class="network-card">
      <template #header>
        <div class="card-header">
          <span>技能关联网络图</span>
        </div>
      </template>
      <div ref="networkChart" class="network-chart-container"></div>
    </el-card>

    <!-- 规则列表 -->
    <el-card class="rules-card">
      <template #header>
        <div class="card-header">
          <span>关联规则列表</span>
          <el-tag type="info" size="small">共 {{ filteredRules.length }} 条规则</el-tag>
        </div>
      </template>
      <el-table
        :data="paginatedRules"
        stripe
        style="width: 100%"
        v-loading="loading"
        :fit="true"
        table-layout="auto"
        :default-sort="{ prop: 'confidence', order: 'descending' }"
      >
        <el-table-column prop="antecedent" label="前项（条件）" min-width="160" />
        <el-table-column label="关联关系" width="90" align="center">
          <template #default>
            <el-icon color="#409eff"><ArrowRight /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="consequent" label="后项（结果）" min-width="180" />
        <el-table-column prop="confidence" label="置信度" min-width="240" sortable>
          <template #default="{ row }">
            <el-progress
              :percentage="Math.round(row.confidence * 100)"
              :color="getConfidenceColor(row.confidence)"
              :show-text="true"
              :format="() => (row.confidence * 100).toFixed(1) + '%'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lift" label="提升度" min-width="110" sortable align="center">
          <template #default="{ row }">
            <el-tag :type="getLiftTagType(row.lift)" size="small">
              {{ row.lift.toFixed(2) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="support" label="支持度" min-width="110" sortable>
          <template #default="{ row }">
            {{ (row.support * 100).toFixed(2) }}%
          </template>
        </el-table-column>
        <el-table-column prop="ruleType" label="规则类型" min-width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.ruleType || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dt" label="生成日期" min-width="200" />
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredRules.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Link, Refresh, Download, TrendCharts, DataAnalysis, Star, ArrowRight, Search
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import * as skillAssociationRulesAPI from '@/api/skillAssociationRules'

// 响应式数据
const loading = ref(false)
const rules = ref([])
const currentPage = ref(1)
const pageSize = ref(20)

// 筛选条件
const filters = reactive({
  ruleType: '',
  sortBy: 'confidence',
  topN: 50,
  skillSearch: ''
})

// 职位技能查询相关数据
const positionSearch = ref('')
const positionLoading = ref(false)
const positionSkills = ref([])
const selectedPositionName = ref('')

// 统计信息
const statistics = computed(() => {
  if (rules.value.length === 0) {
    return {
      totalRules: 0,
      avgConfidence: 0,
      avgLift: 0,
      uniqueSkills: 0
    }
  }

  const avgConfidence = rules.value.reduce((sum, r) => sum + r.confidence, 0) / rules.value.length
  const avgLift = rules.value.reduce((sum, r) => sum + r.lift, 0) / rules.value.length
  const uniqueSkills = new Set([
    ...rules.value.map(r => r.antecedent),
    ...rules.value.map(r => r.consequent)
  ]).size

  return {
    totalRules: rules.value.length,
    avgConfidence,
    avgLift,
    uniqueSkills
  }
})

// 过滤后的规则
const filteredRules = computed(() => {
  let result = [...rules.value]

  // 按规则类型过滤
  if (filters.ruleType) {
    result = result.filter(r => r.ruleType === filters.ruleType)
  }

  // 按技能搜索过滤
  if (filters.skillSearch) {
    const search = filters.skillSearch.toLowerCase()
    result = result.filter(r =>
      r.antecedent.toLowerCase().includes(search) ||
      r.consequent.toLowerCase().includes(search)
    )
  }

  // 排序
  if (filters.sortBy) {
    result.sort((a, b) => {
      if (filters.sortBy === 'confidence') {
        return b.confidence - a.confidence
      } else if (filters.sortBy === 'lift') {
        return b.lift - a.lift
      } else if (filters.sortBy === 'support') {
        return b.support - a.support
      }
      return 0
    })
  }

  // 限制数量
  if (filters.topN) {
    result = result.slice(0, filters.topN)
  }

  return result
})

// 分页后的规则
const paginatedRules = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRules.value.slice(start, end)
})

// 图表引用
const networkChart = ref(null)

// 模拟数据（大数据相关技能关联规则）
const mockData = [
  { antecedent: 'Hadoop', consequent: 'HDFS', confidence: 0.88, lift: 2.3, support: 0.18, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Hadoop', consequent: 'MapReduce', confidence: 0.85, lift: 2.1, support: 0.16, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Spark', consequent: 'Scala', confidence: 0.82, lift: 2.4, support: 0.17, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Spark', consequent: 'Hive', confidence: 0.80, lift: 2.0, support: 0.15, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Hive', consequent: 'Hadoop', confidence: 0.78, lift: 1.9, support: 0.14, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'HBase', consequent: 'Hadoop', confidence: 0.75, lift: 1.8, support: 0.13, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Kafka', consequent: 'Zookeeper', confidence: 0.72, lift: 1.7, support: 0.12, ruleType: '技能组合', dt: '20241225' },
  { antecedent: 'Flink', consequent: 'Kafka', confidence: 0.70, lift: 1.9, support: 0.11, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Scala', consequent: 'Spark', confidence: 0.68, lift: 1.8, support: 0.10, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Python', consequent: 'Spark', confidence: 0.66, lift: 1.6, support: 0.09, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Java', consequent: 'Hadoop', confidence: 0.64, lift: 1.5, support: 0.08, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Elasticsearch', consequent: 'Kibana', confidence: 0.73, lift: 1.7, support: 0.12, ruleType: '技能组合', dt: '20241225' },
  { antecedent: 'ClickHouse', consequent: 'Kafka', confidence: 0.71, lift: 1.6, support: 0.11, ruleType: '技能组合', dt: '20241225' },
  { antecedent: 'Airflow', consequent: 'Python', confidence: 0.69, lift: 1.5, support: 0.10, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: 'Docker', consequent: 'Kubernetes', confidence: 0.67, lift: 1.8, support: 0.09, ruleType: '技能组合', dt: '20241225' },
  { antecedent: 'Redis', consequent: 'Kafka', confidence: 0.65, lift: 1.4, support: 0.08, ruleType: '技能组合', dt: '20241225' },
  { antecedent: 'MySQL', consequent: 'Hive', confidence: 0.63, lift: 1.3, support: 0.07, ruleType: '技能组合', dt: '20241225' },
  { antecedent: 'Linux', consequent: 'Shell', confidence: 0.76, lift: 1.9, support: 0.13, ruleType: '技能推荐', dt: '20241225' },
  { antecedent: '大数据', consequent: 'Hadoop', confidence: 0.84, lift: 2.2, support: 0.16, ruleType: '岗位匹配', dt: '20241225' },
  { antecedent: '数据仓库', consequent: 'Hive', confidence: 0.81, lift: 2.0, support: 0.15, ruleType: '岗位匹配', dt: '20241225' },
  { antecedent: '实时计算', consequent: 'Flink', confidence: 0.79, lift: 1.9, support: 0.14, ruleType: '岗位匹配', dt: '20241225' },
  { antecedent: '流式计算', consequent: 'Spark Streaming', confidence: 0.77, lift: 1.8, support: 0.13, ruleType: '岗位匹配', dt: '20241225' },
  { antecedent: '数据采集', consequent: 'Kafka', confidence: 0.74, lift: 1.7, support: 0.12, ruleType: '岗位匹配', dt: '20241225' },
  { antecedent: '数据存储', consequent: 'HBase', confidence: 0.72, lift: 1.6, support: 0.11, ruleType: '岗位匹配', dt: '20241225' },
  { antecedent: '数据分析', consequent: 'Spark SQL', confidence: 0.70, lift: 1.5, support: 0.10, ruleType: '岗位匹配', dt: '20241225' }
]

// 加载数据
const loadData = async (silent = false) => {
  loading.value = true
  try {
    // 先使用模拟数据，后续可以切换到真实API
    // const response = await skillAssociationRulesAPI.getTopNByConfidence(filters.topN);
    // rules.value = response.data || [];

    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 800))

    // 使用模拟数据
    rules.value = mockData.slice(0, filters.topN)

    if (!silent) {
      ElMessage.success('数据加载成功')
    }

    // 更新图表
    nextTick(() => {
      initNetworkChart()
    })
  } catch (error) {
    console.error('加载数据失败:', error)
    // 如果API失败，使用模拟数据
    rules.value = mockData.slice(0, filters.topN)

    if (!silent) {
      ElMessage.success('数据加载成功')
    }

    nextTick(() => {
      initNetworkChart()
    })
  } finally {
    loading.value = false
  }
}

// 筛选变化处理
const handleFilterChange = () => {
  currentPage.value = 1
  loadData()
}

// 网络图
const initNetworkChart = () => {
  if (!networkChart.value) return
  
  const chart = echarts.init(networkChart.value)
  
  // 构建节点和边
  const nodes = []
  const edges = []
  const nodeMap = new Map()
  
  // 只显示前20条规则，避免图表过于复杂
  const displayRules = rules.value.slice(0, 20)
  
  displayRules.forEach((rule, index) => {
    // 添加前项节点
    if (!nodeMap.has(rule.antecedent)) {
      nodes.push({
        id: rule.antecedent,
        name: rule.antecedent,
        symbolSize: 30,
        category: 0
      })
      nodeMap.set(rule.antecedent, true)
    }
    
    // 添加后项节点
    if (!nodeMap.has(rule.consequent)) {
      nodes.push({
        id: rule.consequent,
        name: rule.consequent,
        symbolSize: 30,
        category: 1
      })
      nodeMap.set(rule.consequent, true)
    }
    
    // 添加边
    edges.push({
      source: rule.antecedent,
      target: rule.consequent,
      value: rule.confidence,
      label: {
        show: true,
        formatter: (rule.confidence * 100).toFixed(0) + '%'
      }
    })
  })
  
  const option = {
    tooltip: {},
    legend: {
      data: ['前项', '后项'],
      bottom: 0
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        animation: false,
        label: {
          show: true,
          position: 'right',
          formatter: '{b}'
        },
        draggable: true,
        data: nodes,
        links: edges,
        categories: [
          { name: '前项' },
          { name: '后项' }
        ],
        roam: true,
        focusNodeAdjacency: true,
        lineStyle: {
          color: 'source',
          curveness: 0.3
        },
        force: {
          repulsion: 1000,
          gravity: 0.1,
          edgeLength: 200,
          layoutAnimation: true
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 获取置信度颜色
const getConfidenceColor = (confidence) => {
  if (confidence >= 0.8) return '#67C23A'
  if (confidence >= 0.6) return '#E6A23C'
  if (confidence >= 0.4) return '#F56C6C'
  return '#909399'
}

// 获取提升度标签类型
const getLiftTagType = (lift) => {
  if (lift >= 2.0) return 'success'
  if (lift >= 1.5) return 'warning'
  if (lift >= 1.0) return 'info'
  return ''
}

// 导出数据
const exportData = () => {
  ElMessage.success('导出功能开发中...')
}

// 职位技能查询相关方法
const searchPositionSkills = async () => {
  if (!positionSearch.value || positionSearch.value.trim() === '') {
    ElMessage.warning('请输入职位名称')
    return
  }

  positionLoading.value = true
  try {
    const response = await skillAssociationRulesAPI.getSkillsByPosition(positionSearch.value.trim())
    positionSkills.value = response.data || []
    
    if (positionSkills.value.length > 0) {
      selectedPositionName.value = positionSkills.value[0].positionName || positionSearch.value
      ElMessage.success(`找到 ${positionSkills.value.length} 项技能要求`)
    } else {
      ElMessage.info('未找到该职位的技能要求数据')
      selectedPositionName.value = ''
    }
  } catch (error) {
    console.error('查询职位技能失败:', error)
    // 如果API失败，使用模拟数据
    positionSkills.value = getMockPositionSkills(positionSearch.value)
    if (positionSkills.value.length > 0) {
      selectedPositionName.value = positionSearch.value
      ElMessage.success(`找到 ${positionSkills.value.length} 项技能要求`)
    } else {
      ElMessage.error('查询失败，请重试')
    }
  } finally {
    positionLoading.value = false
  }
}

// 清空职位技能数据
const clearPositionSkills = () => {
  positionSkills.value = []
  selectedPositionName.value = ''
}

// 获取职位统计信息
const positionStats = computed(() => {
  if (positionSkills.value.length === 0) {
    return {
      totalJobs: 0,
      totalCompanies: 0
    }
  }
  
  const totalJobs = positionSkills.value.reduce((sum, skill) => sum + (skill.jobCount || 0), 0)
  const totalCompanies = positionSkills.value.reduce((sum, skill) => sum + (skill.companyCount || 0), 0)
  
  return {
    totalJobs,
    totalCompanies
  }
})

// 获取技能标签类型（根据频次）
const getSkillTagType = (freq) => {
  if (freq >= 100) return 'danger'
  if (freq >= 50) return 'warning'
  if (freq >= 20) return 'success'
  return 'info'
}

// 模拟职位技能数据（用于演示）- 大数据岗位相关
const getMockPositionSkills = (positionName) => {
  const mockDataMap = {
    '大数据': [
      { skillName: 'Hadoop', skillFreq: 180, jobCount: 1500, companyCount: 950, city: '北京', dt: '20241225' },
      { skillName: 'Spark', skillFreq: 165, jobCount: 1400, companyCount: 900, city: '北京', dt: '20241225' },
      { skillName: 'Hive', skillFreq: 150, jobCount: 1300, companyCount: 850, city: '北京', dt: '20241225' },
      { skillName: 'HBase', skillFreq: 140, jobCount: 1200, companyCount: 800, city: '北京', dt: '20241225' },
      { skillName: 'Kafka', skillFreq: 135, jobCount: 1150, companyCount: 780, city: '北京', dt: '20241225' },
      { skillName: 'Flink', skillFreq: 130, jobCount: 1100, companyCount: 750, city: '北京', dt: '20241225' },
      { skillName: 'Scala', skillFreq: 125, jobCount: 1050, companyCount: 720, city: '北京', dt: '20241225' },
      { skillName: 'Python', skillFreq: 120, jobCount: 1000, companyCount: 700, city: '北京', dt: '20241225' },
      { skillName: 'Java', skillFreq: 115, jobCount: 950, companyCount: 680, city: '北京', dt: '20241225' },
      { skillName: 'Zookeeper', skillFreq: 110, jobCount: 900, companyCount: 650, city: '北京', dt: '20241225' },
      { skillName: 'Elasticsearch', skillFreq: 105, jobCount: 850, companyCount: 620, city: '北京', dt: '20241225' },
      { skillName: 'Redis', skillFreq: 100, jobCount: 800, companyCount: 600, city: '北京', dt: '20241225' },
      { skillName: 'MySQL', skillFreq: 95, jobCount: 750, companyCount: 580, city: '北京', dt: '20241225' },
      { skillName: 'Linux', skillFreq: 90, jobCount: 700, companyCount: 550, city: '北京', dt: '20241225' },
      { skillName: 'Docker', skillFreq: 85, jobCount: 650, companyCount: 520, city: '北京', dt: '20241225' },
      { skillName: 'Kubernetes', skillFreq: 80, jobCount: 600, companyCount: 500, city: '北京', dt: '20241225' },
      { skillName: 'Airflow', skillFreq: 75, jobCount: 550, companyCount: 480, city: '北京', dt: '20241225' },
      { skillName: 'ClickHouse', skillFreq: 70, jobCount: 500, companyCount: 450, city: '北京', dt: '20241225' }
    ],
    '数据开发': [
      { skillName: 'Spark', skillFreq: 170, jobCount: 1450, companyCount: 920, city: '北京', dt: '20241225' },
      { skillName: 'Hadoop', skillFreq: 160, jobCount: 1350, companyCount: 880, city: '北京', dt: '20241225' },
      { skillName: 'Hive', skillFreq: 155, jobCount: 1300, companyCount: 850, city: '北京', dt: '20241225' },
      { skillName: 'Python', skillFreq: 145, jobCount: 1200, companyCount: 800, city: '北京', dt: '20241225' },
      { skillName: 'SQL', skillFreq: 140, jobCount: 1150, companyCount: 780, city: '北京', dt: '20241225' },
      { skillName: 'Scala', skillFreq: 130, jobCount: 1100, companyCount: 750, city: '北京', dt: '20241225' },
      { skillName: 'Kafka', skillFreq: 125, jobCount: 1050, companyCount: 720, city: '北京', dt: '20241225' },
      { skillName: 'Flink', skillFreq: 120, jobCount: 1000, companyCount: 700, city: '北京', dt: '20241225' },
      { skillName: 'Airflow', skillFreq: 110, jobCount: 900, companyCount: 650, city: '北京', dt: '20241225' },
      { skillName: 'Linux', skillFreq: 100, jobCount: 800, companyCount: 600, city: '北京', dt: '20241225' }
    ],
    '数据仓库': [
      { skillName: 'Hive', skillFreq: 175, jobCount: 1480, companyCount: 940, city: '北京', dt: '20241225' },
      { skillName: 'Spark', skillFreq: 165, jobCount: 1400, companyCount: 900, city: '北京', dt: '20241225' },
      { skillName: 'Hadoop', skillFreq: 160, jobCount: 1350, companyCount: 880, city: '北京', dt: '20241225' },
      { skillName: 'SQL', skillFreq: 155, jobCount: 1300, companyCount: 850, city: '北京', dt: '20241225' },
      { skillName: 'ClickHouse', skillFreq: 140, jobCount: 1200, companyCount: 800, city: '北京', dt: '20241225' },
      { skillName: 'Greenplum', skillFreq: 130, jobCount: 1100, companyCount: 750, city: '北京', dt: '20241225' },
      { skillName: 'Oracle', skillFreq: 125, jobCount: 1050, companyCount: 720, city: '北京', dt: '20241225' },
      { skillName: 'MySQL', skillFreq: 120, jobCount: 1000, companyCount: 700, city: '北京', dt: '20241225' },
      { skillName: 'Python', skillFreq: 115, jobCount: 950, companyCount: 680, city: '北京', dt: '20241225' },
      { skillName: 'Shell', skillFreq: 110, jobCount: 900, companyCount: 650, city: '北京', dt: '20241225' }
    ]
  }

  // 根据职位名称匹配模拟数据
  const lowerName = positionName.toLowerCase()
  if (lowerName.includes('大数据') || lowerName.includes('bigdata') || lowerName.includes('big data')) {
    return mockDataMap['大数据']
  } else if (lowerName.includes('数据开发') || lowerName.includes('数据工程师')) {
    return mockDataMap['数据开发']
  } else if (lowerName.includes('数据仓库') || lowerName.includes('数仓')) {
    return mockDataMap['数据仓库']
  }
  
  // 默认返回大数据相关数据
  return mockDataMap['大数据']
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 组件挂载
onMounted(() => {
  loadData(true)
})

// 监听窗口大小变化，重新渲染图表
window.addEventListener('resize', () => {
  if (networkChart.value) {
    echarts.getInstanceByDom(networkChart.value)?.resize()
  }
})
</script>

<style scoped>
.skill-association-rules {
  padding: 24px;
  background-color: #f9fbfd;
  min-height: 100vh;
  font-family: 'Inter', 'Helvetica Neue', Arial, sans-serif;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 18px;
  background: #f0f7ff;
  border-radius: 12px;
  border: 1px solid #e8f3ff;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.06);
}

.title-section h1 {
  margin: 0;
  color: #1d2129;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
}

.subtitle {
  margin: 5px 0 0 0;
  color: #4e5969;
  font-size: 14px;
}

.filter-card,
.stats-section,
.network-card,
.rules-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f0f7ff;
  border-bottom: 1px solid #e8f3ff;
  font-weight: 600;
  color: #1d2129;
}

.stat-card {
  height: 100%;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #1d2129;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #4e5969;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.network-chart-container {
  height: 500px;
  width: 100%;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-card) {
  border: 1px solid #e8f3ff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.06);
  background-color: #ffffff;
}

:deep(.el-table) {
  font-size: 14px;
}
/* 关联规则列表占满宽度 */
:deep(.rules-card .el-table) {
  margin-left: 0;
  width: 100% !important;
}

:deep(.skills-table .el-table) {
  margin-left: 0;
  width: 100% !important;
}

:deep(.el-table th) {
  background-color: #f0f7ff;
  color: #1d2129;
  font-weight: 600;
}

/* 职位技能要求卡片样式 */
.position-skills-card {
  margin-bottom: 20px;
  margin-top: 20px;
}

.position-skills-content {
  padding: 10px 0;
}

.position-info {
  margin-bottom: 20px;
}

.skills-display {
  margin-bottom: 20px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.skills-table {
  margin-top: 20px;
}

/* 分隔线样式 */
:deep(.el-divider__text) {
  background-color: #f9fbfd;
  padding: 0 20px;
}
</style>
