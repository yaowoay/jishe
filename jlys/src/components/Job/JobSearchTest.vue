<template>
  <div class="job-search-test">
    <div class="test-header">
      <h2>职位搜索功能测试</h2>
      <p>测试高级筛选功能的各项特性</p>
    </div>

    <!-- 测试按钮组 -->
    <div class="test-controls">
      <el-button-group>
        <el-button type="primary" @click="testBasicSearch">基础搜索</el-button>
        <el-button type="success" @click="testSalaryFilter">薪资筛选</el-button>
        <el-button type="warning" @click="testIndustryFilter">行业筛选</el-button>
        <el-button type="info" @click="testAdvancedFilter">高级筛选</el-button>
        <el-button @click="clearTest">清空测试</el-button>
      </el-button-group>
    </div>

    <!-- 当前测试状态 -->
    <div class="test-status" v-if="currentTest">
      <el-alert
        :title="`当前测试: ${currentTest}`"
        type="info"
        :description="testDescription"
        show-icon
        :closable="false"
      />
    </div>

    <!-- 职位列表组件 -->
    <JobList ref="jobListRef" />
  </div>
</template>

<script setup>
import { ref } from 'vue'         
import JobList from './JobList.vue'         
import { ElMessage } from 'element-plus'         

const jobListRef = ref(null)         
const currentTest = ref('')         
const testDescription = ref('')         

// 基础搜索测试
const testBasicSearch = () => {
  currentTest.value = '基础搜索测试'         
  testDescription.value = '测试关键词搜索功能，搜索"Java"相关职位'         

  const filters = {
    keyword: 'Java'
  }         
  
  // 触发搜索
  if (jobListRef.value) {
    jobListRef.value.handleAdvancedSearch(filters)         
  }
  
  ElMessage.success('已执行基础搜索测试')         
}         

// 薪资筛选测试
const testSalaryFilter = () => {
  currentTest.value = '薪资筛选测试'         
  testDescription.value = '测试薪资范围筛选，筛选25K-35K的职位'         

  const filters = {
    minSalary: 25000,
    maxSalary: 35000
  }         
  
  if (jobListRef.value) {
    jobListRef.value.handleAdvancedSearch(filters)         
  }
  
  ElMessage.success('已执行薪资筛选测试')         
}         

// 行业筛选测试
const testIndustryFilter = () => {
  currentTest.value = '行业筛选测试'         
  testDescription.value = '测试行业筛选功能，筛选互联网行业职位'         
  
  const filters = {
    industry: '互联网'
  }         
  
  if (jobListRef.value) {
    jobListRef.value.handleAdvancedSearch(filters)         
  }
  
  ElMessage.success('已执行行业筛选测试')         
}         

// 高级筛选测试
const testAdvancedFilter = () => {
  currentTest.value = '高级筛选测试'         
  testDescription.value = '测试多条件组合筛选：互联网行业 + 本科学历 + 3-5年经验 + 已上市公司'         

  const filters = {
    industry: '互联网',
    education: '本科',
    experience: '3-5年',
    financingStatus: '已上市'
  }         
  
  if (jobListRef.value) {
    jobListRef.value.handleAdvancedSearch(filters)         
  }
  
  ElMessage.success('已执行高级筛选测试')         
}         

// 清空测试
const clearTest = () => {
  currentTest.value = ''         
  testDescription.value = ''         
  
  if (jobListRef.value) {
    jobListRef.value.resetSearch()         
  }
  
  ElMessage.info('已清空测试条件')         
}         
</script>

<style scoped>
.job-search-test {
  padding: 20px;
}

.test-header {
  text-align: center;
  margin-bottom: 30px;
}

.test-header h2 {
  color: #303133;
  margin-bottom: 8px;
}

.test-header p {
  color: #909399;
  margin: 0;
}

.test-controls {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.test-status {
  margin-bottom: 20px;
}
</style>
