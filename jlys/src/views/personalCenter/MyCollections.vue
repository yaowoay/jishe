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

const currentUserId = ref(null)
const collectedJobs = ref([
  {
    jobId: 1,
    positionName: '前端开发工程师',
    companyName: '某科技公司',
    city: '北京',
    minSalary: 15,
    maxSalary: 25,
    experienceReq: '3-5年',
    educationReq: '本科',
    industryName: '互联网'
  },
  {
    jobId: 2,
    positionName: 'Vue开发工程师',
    companyName: '另一家公司',
    city: '上海',
    minSalary: 18,
    maxSalary: 28,
    experienceReq: '2-4年',
    educationReq: '本科',
    industryName: '互联网'
  }
])
const loading = ref(false)

const handleUncollect = (job) => {
  const jobId = job.jobId || job.id
  ElMessage.success('已取消收藏')
  collectedJobs.value = collectedJobs.value.filter(j => (j.jobId || j.id) !== jobId)
}

const handleViewDetail = (job) => {
  ElMessage.info(`查看职位详情: ${job.positionName}`)
}

onMounted(() => {
  const storedUserId = localStorage.getItem('userId')
  if (storedUserId) {
    currentUserId.value = parseInt(storedUserId, 10)
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
