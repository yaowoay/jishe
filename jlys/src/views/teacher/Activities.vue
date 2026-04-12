<template>
  <div class="teacher-activities">
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
              v-model="searchForm.keyword"
              placeholder="活动名称"
              clearable
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
              v-model="searchForm.status"
              placeholder="活动状态"
              clearable
          >
            <el-option label="草稿" value="draft" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            <span>查询</span>
          </el-button>
          <el-button type="success" @click="goToCreate" style="margin-left: 10px">
            <el-icon><Plus /></el-icon>
            <span>创建活动</span>
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
      <el-table
          :data="activitiesList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="title" label="活动名称" width="150" />
        <el-table-column prop="type" label="活动类型" width="100" />
        <el-table-column prop="mode" label="活动形式" width="100" />
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="150">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="currentParticipants" label="参与人数" width="100">
          <template #default="{ row }">
            {{ row.currentParticipants }}/{{ row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewActivity(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="editActivity(row)">
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleSearch"
          @size-change="handleSearch"
          style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getActivities } from '@/api/teacher'

const router = useRouter()
const loading = ref(false)
const activitiesList = ref([])
const searchForm = ref({
  keyword: '',
  status: ''
})
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const typeMap = {
    'draft': 'info',
    'ongoing': 'success',
    'ended': 'danger'
  }
  return typeMap[status] || 'info'
}

const handleSearch = async () => {
  loading.value = true
  try {
    const response = await getActivities()
    if (response.success) {
      let data = response.data || []
      
      if (searchForm.value.keyword) {
        data = data.filter(item => 
          item.title.includes(searchForm.value.keyword)
        )
      }
      if (searchForm.value.status) {
        data = data.filter(item => item.status === searchForm.value.status)
      }
      
      activitiesList.value = data
      pagination.value.total = data.length
    }
  } catch (error) {
    ElMessage.error('查询活动列表失败')
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/teacher/activities/create')
}

const viewActivity = (row) => {
  ElMessage.info(`查看活动: ${row.title}`)
}

const editActivity = (row) => {
  ElMessage.info(`编辑活动: ${row.title}`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-activities {
  .search-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  .table-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
