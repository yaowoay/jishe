<template>
  <el-card class="job-card" shadow="hover">
    <!-- 卡片顶部：职位名称和薪资 -->
    <div class="header">
      <span class="position-name">{{ job.positionName || '暂无' }}</span>
      <SalaryDisplay :job="job" display-mode="simple" class="salary-info" />
    </div>

    <!-- 公司信息行 -->
    <div class="company-info">
      <div class="company-name">{{ job.companyName || '暂无' }}</div>
      <el-divider direction="vertical" class="divider"></el-divider>
      <div class="company-size" v-if="job.companySize">{{ job.companySize }}</div>
      <el-divider direction="vertical" class="divider" v-if="job.companySize"></el-divider>
      <div class="location">{{ job.city || job.cityName || '暂无' }}</div>
    </div>

    <!-- 标签区域：经验、学历要求和薪资范围 -->
    <div class="tags-container">
      <el-tag
          size="small"
          class="experience-tag"
      >
        {{ job.experienceReq || job.experienceRequirement || '暂无' }}
      </el-tag>
      <el-tag
          size="small"
          class="education-tag"
      >
        {{ job.educationReq || job.educationRequirement || '暂无' }}
      </el-tag>
      <!-- 薪资范围标签 -->
      <el-tag
          v-if="getSalaryRangeText(job)"
          size="small"
          class="salary-range-tag"
      >
        {{ getSalaryRangeText(job) }}
      </el-tag>
    </div>

    <!-- 底部：行业信息和操作按钮 -->
    <div class="footer">
      <span class="industry">{{ job.companyType || job.industryName || '暂无' }}</span>
      <div class="action-buttons">
        <button
            class="detail-btn"
            @click.stop="viewDetail"
            aria-label="查看详情"
        >
          <i class="detail-icon">👁</i>
        </button>
        <button
            class="favorite-btn"
            :class="{ collected: isCollected }"
            @click.stop="toggleCollection"
            aria-label="收藏职位"
        >
          <i class="star-icon">★</i>
        </button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import SalaryDisplay from '@/components/SalaryDisplay.vue'

// 定义组件接收的属性
const props = defineProps({
  job: {
    type: Object,
    required: true,
    description: '职位信息对象，包含职位名称、薪资等信息'
  },
  isCollected: {
    type: Boolean,
    default: false,
    description: '是否已收藏的状态'
  }
})

// 定义组件发出的事件
const emit = defineEmits(['toggle-collection', 'view-detail'])

// 切换收藏状态的方法
const toggleCollection = () => {
  emit('toggle-collection', props.job)
}

// 查看详情的方法
const viewDetail = () => {
  emit('view-detail', props.job)
}

// 获取薪资范围文本的方法（用于标签显示）
const getSalaryRangeText = (job) => {
  if (job.minSalary && job.maxSalary) {
    return `${job.minSalary}K-${job.maxSalary}K`
  }
  return null
}
</script>

<style scoped>
.job-card {
  margin-bottom: 16px;
  border-radius: 12px;
  border: none;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 18px 20px;
  cursor: pointer;
}

.job-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* 头部样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.position-name {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  transition: color 0.2s;
}

.job-card:hover .position-name {
  color: #1677ff;
}

.salary-info {
  font-size: 18px;
}

/* 公司信息样式 */
.company-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #4e5969;
  font-size: 14px;
}

.company-name {
  font-weight: 500;
  max-width: 60%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.divider {
  height: 14px;
  margin: 0 8px;
  background-color: #e5e6eb;
}

.company-size {
  color: #4e5969;
  font-size: 13px;
  background-color: #f2f3f5;
  padding: 2px 6px;
  border-radius: 4px;
}

.location {
  color: #86909c;
}

/* 标签容器样式 */
.tags-container {
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #f2f3f5;
}

.experience-tag {
  background-color: #f0f7ff;
  color: #1677ff;
  border-color: #d6eaff;
  margin-right: 8px;
}

.education-tag {
  background-color: #f6ffed;
  color: #52c41a;
  border-color: #d9f7be;
}

.salary-range-tag {
  background-color: #fff2e8;
  color: #fa8c16;
  border-color: #ffd591;
  margin-left: 8px;
}

/* 底部样式 */
.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #86909c;
  font-size: 13px;
}

.industry {
  max-width: 70%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 详情按钮样式 */
.detail-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  transition: transform 0.2s;
  border-radius: 4px;
}

.detail-btn:hover {
  transform: scale(1.15);
  background-color: rgba(22, 119, 255, 0.1);
}

.detail-icon {
  font-size: 18px;
  color: #1677ff;
  transition: color 0.3s;
}

/* 收藏按钮样式 */
.favorite-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  transition: transform 0.2s;
}

.favorite-btn:hover {
  transform: scale(1.15);
}

.star-icon {
  font-size: 20px;
  color: #c9cdD4;
  transition: color 0.3s, text-shadow 0.3s;
}

.favorite-btn.collected .star-icon {
  color: #ffd700;
  text-shadow: 0 0 8px rgba(255, 215, 0, 0.5);
}
</style>
