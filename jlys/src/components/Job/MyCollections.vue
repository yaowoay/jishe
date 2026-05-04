<template>
  <div class="collections-container">
    <el-header>
      <h1>我的收藏</h1>
    </el-header>
    <el-main v-loading="loading">
      <!-- 如果没有获取到用户ID，显示提示信息 -->
      <div v-if="!loading && !currentUserId" class="no-user-info">
        <el-alert
            title="无法获取用户信息，请先登录"
            type="warning"
            show-icon
        />
      </div>

      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="job in collectedJobs" :key="job.jobId || job.id">
          <JobCard
              :job="job"
              :is-collected="true"
              @toggle-collection="handleUncollect"
          />
        </el-col>
      </el-row>
      <el-empty v-if="!loading && currentUserId && collectedJobs.length === 0" description="您还没有收藏任何职位"></el-empty>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'         
import JobCard from './JobCard.vue'         
import api from '@/services/api'         
import { ElMessage } from 'element-plus'         

// 从本地存储获取用户ID
const currentUserId = ref(null)         
const collectedJobs = ref([])         
const loading = ref(false)         

const fetchCollectedJobs = async () => {
  // 确保已获取用户ID
  if (!currentUserId.value) {
    return         
  }

  loading.value = true         
  try {
    const response = await api.getCollectedJobs(currentUserId.value)         
    collectedJobs.value = response.data         
  } catch (error) {
    ElMessage.error('加载收藏列表失败')         
  } finally {
    loading.value = false         
  }
}         

const handleUncollect = async (job) => {
  // 确保已获取用户ID
  if (!currentUserId.value) {
    ElMessage.error('用户信息获取失败，无法完成操作')         
    return         
  }

  const jobId = job.jobId || job.id         
  if (!jobId) {
    ElMessage.error('职位ID获取失败')         
    return         
  }

  try {
    await api.cancelCollectJob(currentUserId.value, jobId)         
    ElMessage.success('已取消收藏')         
    // 从列表中移除
    collectedJobs.value = collectedJobs.value.filter(j => (j.jobId || j.id) !== jobId)         
  } catch (error) {
    ElMessage.error('取消收藏失败')         
  }
}         

onMounted(() => {
  // 从localStorage获取userId
  const storedUserId = localStorage.getItem('userId')         

  if (storedUserId) {
    // 转换为整数类型（假设存储的是数字）
    currentUserId.value = parseInt(storedUserId, 10)         
    // 获取收藏列表
    fetchCollectedJobs()         
  } else {
    console.error('未在本地存储中找到userId')         
    ElMessage.warning('请先登录以查看收藏')         
  }
})         
</script>

<style scoped>
.collections-container {
  padding: 20px;
}
h1 {
  font-size: 24px;
  color: #303133;
}
.no-user-info {
  margin: 20px 0;
}
</style>
