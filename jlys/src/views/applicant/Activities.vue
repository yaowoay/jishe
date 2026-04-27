<template>
  <div class="student-activities">
    <el-card class="mb16">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部状态" style="width: 160px">
            <el-option label="已发布" value="published" />
            <el-option label="进行中" value="ongoing" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="query.keyword" clearable placeholder="活动标题关键词" style="width: 240px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" v-loading="loading">
      <el-col v-for="item in list" :key="item.activityId" :xs="24" :sm="12" :md="8">
        <el-card class="mb16" shadow="hover">
          <h4>{{ item.title }}</h4>
          <p>类型：{{ item.type }} ｜ 形式：{{ item.mode }}</p>
          <p>时间：{{ fmt(item.startTime) }} - {{ fmt(item.endTime) }}</p>
          <p>地点：{{ item.location || '线上' }}</p>
          <p>人数：{{ item.currentParticipants || 0 }}/{{ item.maxParticipants || '不限' }}</p>
          <div style="margin-top: 10px; display: flex; gap: 8px">
            <el-tag v-if="item.registered" type="success">已报名</el-tag>
            <el-button v-if="!item.registered" size="small" type="primary" @click="register(item)">报名</el-button>
            <el-button v-else size="small" @click="signIn(item)">签到</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentActivities, registerStudentActivity, signInStudentActivity } from '@/api/student'

const loading = ref(false)
const list = ref([])
const query = ref({ status: null, keyword: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudentActivities(query.value)
    if (res.success) list.value = res.data || []
  } catch (e) {
    ElMessage.error('加载活动失败')
  } finally {
    loading.value = false
  }
}

const register = async (row) => {
  try {
    await registerStudentActivity(row.activityId)
    ElMessage.success('报名成功')
    loadData()
  } catch (e) {
    ElMessage.error(e?.message || '报名失败')
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

<style scoped>
.mb16 { margin-bottom: 16px; }
</style>
