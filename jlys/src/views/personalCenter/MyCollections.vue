<template>
  <div class="collections-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>我的收藏</h2>
        </div>
      </template>
      <div v-loading="loading">
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
                @view-detail="handleViewDetail"
            />
          </el-col>
        </el-row>
        <el-empty v-if="!loading && currentUserId && collectedJobs.length === 0" description="您还没有收藏任何职位"></el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import JobCard from '@/components/Job/JobCard.vue'
import { jobAPI } from '@/api/visualization'

const currentUserId = ref(null)
const collectedJobs = ref([])
const loading = ref(false)

// 加载收藏列表
const loadCollections = async () => {
  if (!currentUserId.value) {
    console.error('用户ID不存在')
    return
  }

  loading.value = true
  try {
    const response = await jobAPI.getCollectedJobs(currentUserId.value)
    console.log('收藏列表API响应:', response)

    // 兼容两种返回格式
    const result = response.data || response
    console.log('收藏列表结果:', result)

    if (result.code === 0) {
      collectedJobs.value = result.data || []
      console.log('收藏的职位列表:', collectedJobs.value)
      console.log('收藏数量:', collectedJobs.value.length)

      if (collectedJobs.value.length > 0) {
        console.log('第一个收藏职位示例:', collectedJobs.value[0])
      }
    } else {
      console.error('获取收藏列表失败:', result.message)
      ElMessage.error(result.message || '获取收藏列表失败')
    }
  } catch (error) {
    console.error('获取收藏列表异常:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 取消收藏
const handleUncollect = async (job) => {
  const jobId = job.jobId || job.id
  if (!currentUserId.value || !jobId) {
    ElMessage.error('操作失败')
    return
  }

  try {
    const response = await jobAPI.cancelCollectJob(currentUserId.value, jobId)
    console.log('取消收藏响应:', response)

    const result = response.data || response

    if (result.code === 0) {
      ElMessage.success('已取消收藏')
      // 从列表中移除
      collectedJobs.value = collectedJobs.value.filter(j => (j.jobId || j.id) !== jobId)
    } else {
      ElMessage.error(result.message || '取消收藏失败')
    }
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleViewDetail = (job) => {
  ElMessage.info(`查看职位详情: ${job.title || job.positionName}`)
}

onMounted(() => {
  const storedUserId = localStorage.getItem('userId')
  console.log('从 localStorage 获取的 userId:', storedUserId)

  if (storedUserId) {
    currentUserId.value = parseInt(storedUserId, 10)
    console.log('当前用户ID:', currentUserId.value)
    // 加载收藏列表
    loadCollections()
  } else {
    console.error('未找到用户ID，请先登录')
  }
})
</script>

<style scoped>
.collections-container {
  padding: 20px;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.no-user-info {
  margin: 20px 0;
}
</style>
