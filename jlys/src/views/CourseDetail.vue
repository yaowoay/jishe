<template>
  <div class="course-detail-page">
    <div class="page-header">
      <button @click="goBack" class="back-btn">← 返回</button>
      <h1>{{ courseName }}</h1>
      <p class="course-subtitle">课程详情页面 (模拟)</p>
    </div>
    
    <div class="course-content">
      <div class="course-info">
        <div class="course-banner">
          <div class="course-image">
            <div class="placeholder-image">📚</div>
          </div>
          <div class="course-meta">
            <h2>{{ courseName }}</h2>
            <p class="course-provider">提供方：极客时间</p>
            <div class="course-stats">
              <span class="stat-item">⭐ 4.8分</span>
              <span class="stat-item">👥 1.2万人学习</span>
              <span class="stat-item">⏱ 40小时</span>
            </div>
            <div class="course-tags">
              <span class="tag">前端开发</span>
              <span class="tag">进阶课程</span>
              <span class="tag">实战项目</span>
            </div>
          </div>
        </div>
        
        <div class="course-description">
          <h3>课程介绍</h3>
          <p>这是一个模拟的课程详情页面。在实际应用中，这里会显示课程的详细介绍、学习目标、课程大纲、讲师信息等内容。</p>
          
          <h3>学习目标</h3>
          <ul>
            <li>掌握核心技术概念和原理</li>
            <li>能够独立完成相关项目开发</li>
            <li>具备解决实际问题的能力</li>
            <li>提升技术水平和职业竞争力</li>
          </ul>
          
          <h3>适合人群</h3>
          <ul>
            <li>有一定基础的开发者</li>
            <li>希望提升技能的在职人员</li>
            <li>准备求职面试的候选人</li>
          </ul>
        </div>
        
        <div class="course-actions">
          <button class="btn-primary">立即学习</button>
          <button class="btn-secondary">加入收藏</button>
          <button class="btn-secondary">分享课程</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const courseName = computed(() => {
  return decodeURIComponent(route.params.courseName || '未知课程')
})

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.course-detail-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.back-btn {
  position: absolute;
  left: 20px;
  top: 20px;
  background: #667eea;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.back-btn:hover {
  background: #5a67d8;
}

h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
}

.course-subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.course-content {
  max-width: 800px;
  margin: 0 auto;
}

.course-info {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.course-banner {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.course-image {
  width: 200px;
  height: 150px;
  background: #f0f0f0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-image {
  font-size: 48px;
}

.course-meta {
  flex: 1;
}

.course-meta h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 10px 0;
}

.course-provider {
  color: #666;
  margin: 0 0 15px 0;
}

.course-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.stat-item {
  font-size: 14px;
  color: #666;
}

.course-tags {
  display: flex;
  gap: 8px;
}

.tag {
  background: #e6f7ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.course-description {
  margin-bottom: 30px;
}

.course-description h3 {
  color: #333;
  margin: 20px 0 10px 0;
}

.course-description ul {
  padding-left: 20px;
}

.course-description li {
  margin-bottom: 5px;
  color: #666;
}

.course-actions {
  display: flex;
  gap: 15px;
}

.btn-primary {
  background: #667eea;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background: #5a67d8;
  transform: translateY(-1px);
}

.btn-secondary {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #e9ecef;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .course-banner {
    flex-direction: column;
  }
  
  .course-image {
    width: 100%;
    height: 200px;
  }
  
  .course-actions {
    flex-direction: column;
  }
}
</style>
