<template>
  <div class="my-student-activities">
    <el-card>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="title" label="活动名称" min-width="220" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="mode" label="形式" width="100" />
        <el-table-column label="活动时间" width="200">
          <template #default="{ row }">{{ fmt(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="报名状态" width="120">
          <template #default="{ row }">{{ row.registrationStatus || '未报名' }}</template>
        </el-table-column>
        <el-table-column label="签到时间" width="180">
          <template #default="{ row }">{{ fmt(row.signInTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.registrationStatus !== 'signed_in'"
              size="small"
              type="primary"
              @click="signIn(row)"
            >签到</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyStudentActivities, signInStudentActivity } from '@/api/student'

const loading = ref(false)
const list = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyStudentActivities()
    if (res.success) list.value = res.data || []
  } catch (e) {
    ElMessage.error('加载我的活动失败')
  } finally {
    loading.value = false
  }
}

const signIn = async (row) => {
  try {
    await signInStudentActivity(row.activityId, 'qrcode')
    ElMessage.success('签到成功')
    loadData()
  } catch (e) {
    ElMessage.error(e?.message || '签到失败')
  }
}

const fmt = (v) => (v ? new Date(v).toLocaleString('zh-CN') : '-')
onMounted(loadData)
</script>
