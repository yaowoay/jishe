<template>
  <div class="resume-template4">
    <!-- macOS窗口头部 -->
    <div class="macos-header">
      <div class="window-controls">
        <div class="control-btn close"></div>
        <div class="control-btn minimize"></div>
        <div class="control-btn maximize"></div>
      </div>
      <div class="window-title">简历 - {{ resume.fullName || '姓名' }}</div>
    </div>

    <!-- macOS侧边栏 -->
    <div class="macos-sidebar">
      <div class="sidebar-header">
        <div class="profile-avatar">
          <div class="avatar-initial">{{ (resume.fullName || '姓')[0] }}</div>
        </div>
        <div class="profile-info">
          <h2 class="profile-name">{{ resume.fullName || '姓名' }}</h2>
          <p class="profile-position">{{ resume.position || '期望职位' }}</p>
        </div>
      </div>

      <div class="sidebar-stats">
        <div class="stat-item" v-if="resume.workYears">
          <div class="stat-icon">⏱️</div>
          <div class="stat-content">
            <span class="stat-value">{{ resume.workYears }}年</span>
            <span class="stat-label">工作经验</span>
          </div>
        </div>
        <div class="stat-item" v-if="resume.skills && resume.skills.length">
          <div class="stat-icon">⚡</div>
          <div class="stat-content">
            <span class="stat-value">{{ resume.skills.length }}个</span>
            <span class="stat-label">技能专长</span>
          </div>
        </div>
      </div>

      <div class="sidebar-contact">
        <div class="contact-item" v-if="resume.phone">
          <div class="contact-icon">📱</div>
          <span>{{ resume.phone }}</span>
        </div>
        <div class="contact-item" v-if="resume.email">
          <div class="contact-icon">📧</div>
          <span>{{ resume.email }}</span>
        </div>
        <div class="contact-item" v-if="resume.location">
          <div class="contact-icon">📍</div>
          <span>{{ resume.location }}</span>
        </div>
      </div>
    </div>

    <!-- macOS主内容区域 -->
    <div class="macos-content">
      <!-- 个人简介 -->
      <div v-if="resume.profile" class="content-section">
        <div class="section-header">
          <div class="section-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
              <path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"/>
            </svg>
          </div>
          <h2 class="section-title">个人简介</h2>
        </div>
        <div class="section-content">
          <div class="macos-card">
            <div class="card-content">
              <p>{{ resume.profile }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 技能专长 -->
      <div v-if="resume.skills && resume.skills.length" class="content-section">
        <div class="section-header">
          <div class="section-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
              <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3z"/>
            </svg>
          </div>
          <h2 class="section-title">技能专长</h2>
        </div>
        <div class="section-content">
          <div class="skills-container">
            <div v-for="(skill, index) in resume.skills" :key="index" class="skill-item">
              <div class="skill-header">
                <span class="skill-name">{{ skill.skillName }}</span>
                <div class="skill-level">
                  <div class="level-indicator">
                    <div v-for="i in 5" :key="i" class="level-dot" :class="{ active: i <= skill.level }"></div>
                  </div>
                  <span class="level-text">{{ skill.level }}/5</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 工作经历 -->
      <div v-if="resume.workExperiences && resume.workExperiences.length" class="content-section">
        <div class="section-header">
          <div class="section-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
              <path d="M4 4a2 2 0 00-2 2v1h16V6a2 2 0 00-2-2H4zM18 9H2v5a2 2 0 002 2h12a2 2 0 002-2V9zM4 13a1 1 0 011-1h1a1 1 0 110 2H5a1 1 0 01-1-1zm5-1a1 1 0 100 2h1a1 1 0 100-2H9z"/>
            </svg>
          </div>
          <h2 class="section-title">工作经历</h2>
        </div>
        <div class="section-content">
          <div class="timeline-container">
            <div v-for="(work, index) in resume.workExperiences" :key="index" class="timeline-item">
              <div class="timeline-marker">
                <div class="marker-dot"></div>
                <div class="marker-line"></div>
              </div>
              <div class="timeline-content">
                <div class="work-card">
                  <div class="work-header">
                    <div class="work-info">
                      <h3 class="company-name">{{ work.company }}</h3>
                      <p class="job-title">{{ work.position }}</p>
                    </div>
                    <div class="work-period">
                      <span class="period-text">{{ formatDate(work.startDate) }} - {{ formatDate(work.endDate) }}</span>
                    </div>
                  </div>
                  <div v-if="work.responsibility || work.achievement" class="work-details">
                    <div v-if="work.responsibility" class="detail-section">
                      <h4>📋 工作职责</h4>
                      <p>{{ work.responsibility }}</p>
                    </div>
                    <div v-if="work.achievement" class="detail-section">
                      <h4>🏆 工作成果</h4>
                      <p>{{ work.achievement }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 教育经历 -->
      <div v-if="resume.educations && resume.educations.length" class="content-section">
        <div class="section-header">
          <div class="section-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
              <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838l-2.727 1.17 1.94.831a1 1 0 00.788 0l7-3a1 1 0 000-1.838l-7-3zM3.31 9.397L5 10.12v4.102a8.969 8.969 0 00-1.05-.174 1 1 0 01-.89-.89 11.115 11.115 0 01.25-3.762zM9.3 16.573A9.026 9.026 0 007 14.935v-3.957l1.818.78a3 3 0 002.364 0l5.508-2.361a11.026 11.026 0 01.25 3.762 1 1 0 01-.89.89 8.968 8.968 0 00-5.35 2.524 1 1 0 01-1.4 0zM6 18a1 1 0 001-1v-2.065a8.935 8.935 0 00-2-.712V17a1 1 0 001 1z"/>
            </svg>
          </div>
          <h2 class="section-title">教育经历</h2>
        </div>
        <div class="section-content">
          <div class="education-grid">
            <div v-for="(edu, index) in resume.educations" :key="index" class="education-card">
              <div class="education-header">
                <h3 class="school-name">{{ edu.school }}</h3>
                <span class="education-period">{{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) }}</span>
              </div>
              <p class="degree-info">{{ edu.major }} | {{ edu.degree }}</p>
              <p v-if="edu.description" class="education-description">{{ edu.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 项目经验 -->
      <div v-if="resume.projectExperiences && resume.projectExperiences.length" class="content-section">
        <div class="section-header">
          <div class="section-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <h2 class="section-title">项目经验</h2>
        </div>
        <div class="section-content">
          <div class="projects-grid">
            <div v-for="(project, index) in resume.projectExperiences" :key="index" class="project-card">
              <div class="project-header">
                <h3 class="project-name">{{ project.projectName }}</h3>
                <span class="project-period">{{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) }}</span>
              </div>
              <p class="project-role">角色：{{ project.role }}</p>
              <p v-if="project.description" class="project-description">{{ project.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 其他信息 -->
      <div v-if="resume.additionalInfos && resume.additionalInfos.length" class="content-section">
        <div class="section-header">
          <div class="section-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016zM13 9a2 2 0 11-4 0 2 2 0 014 0z"/>
            </svg>
          </div>
          <h2 class="section-title">其他信息</h2>
        </div>
        <div class="section-content">
          <div class="additional-grid">
            <div v-for="(info, index) in resume.additionalInfos" :key="index" class="additional-card">
              <h4 class="info-title">{{ info.name }}</h4>
              <p v-if="info.description" class="info-description">{{ info.description }}</p>
              <span v-if="info.time" class="info-time">{{ info.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResumeTemplate4',
  props: {
    resume: {
      type: Object,
      required: true
    }
  },
  setup() {
    const formatDate = (date) => {
      if (!date) return '至今'
      if (typeof date === 'string') {
        return date
      }
      return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit' })
    }

    return {
      formatDate
    }
  }
}
</script>

<style scoped>
.resume-template4 {
  max-width: 900px;
  margin: 0 auto;
  background: #1d1d1f;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.6;
  color: #f5f5f7;
  min-height: 100vh;
  position: relative;
}

/* macOS窗口头部 */
.macos-header {
  background: #000000;
  padding: 12px 16px;
  border-radius: 12px 12px 0 0;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #3d3d40;
}

.window-controls {
  display: flex;
  gap: 8px;
  margin-right: 16px;
}

.control-btn {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.control-btn:hover {
  opacity: 0.8;
}

.close {
  background: #ff5f56;
}

.minimize {
  background: #ffbd2e;
}

.maximize {
  background: #27ca3f;
}

.window-title {
  color: #f5f5f7;
  font-size: 14px;
  font-weight: 500;
  flex: 1;
  text-align: center;
}

/* macOS侧边栏 */
.macos-sidebar {
  width: 280px;
  background: #2d2d30;
  border-right: 1px solid #3d3d40;
  padding: 30px 20px;
  float: left;
  height: auto;
  min-height: 600px;
}

.sidebar-header {
  text-align: center;
  margin-bottom: 30px;
}

.profile-avatar {
  margin-bottom: 16px;
}

.avatar-initial {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #007aff, #5856d6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
  color: white;
  margin: 0 auto;
}

.profile-name {
  font-size: 24px;
  font-weight: 600;
  color: #f5f5f7;
  margin: 0 0 8px 0;
}

.profile-position {
  font-size: 16px;
  color: #8e8e93;
  margin: 0;
}

.sidebar-stats {
  margin-bottom: 30px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #3d3d40;
  padding: 12px 16px;
  border-radius: 12px;
  margin-bottom: 12px;
}

.stat-icon {
  font-size: 18px;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #f5f5f7;
}

.stat-label {
  font-size: 12px;
  color: #8e8e93;
}

.sidebar-contact {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #f5f5f7;
  background: #3d3d40;
  padding: 12px 16px;
  border-radius: 12px;
}

.contact-icon {
  font-size: 16px;
}

/* macOS主内容区域 */
.macos-content {
  margin-left: 280px;
  padding: 30px;
  background: #1d1d1f;
  min-height: 600px;
}

.content-section {
  margin-bottom: 30px;
  background: #2d2d30;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border: 1px solid #3d3d40;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #3d3d40;
}

.section-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #007aff;
  border-radius: 10px;
  color: white;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #f5f5f7;
  margin: 0;
}

.section-content {
  color: #f5f5f7;
}

/* 个人简介 */
.macos-card {
  background: #3d3d40;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #4d4d50;
}

.card-content p {
  margin: 0;
  color: #f5f5f7;
  line-height: 1.6;
  font-size: 16px;
}

/* 技能容器 */
.skills-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skill-item {
  background: #3d3d40;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #4d4d50;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.skill-name {
  font-weight: 600;
  color: #f5f5f7;
  font-size: 16px;
}

.skill-level {
  display: flex;
  align-items: center;
  gap: 12px;
}

.level-indicator {
  display: flex;
  gap: 4px;
}

.level-dot {
  width: 8px;
  height: 8px;
  background: #4d4d50;
  border-radius: 50%;
  transition: background 0.3s ease;
}

.level-dot.active {
  background: #007aff;
}

.level-text {
  font-size: 12px;
  color: #8e8e93;
  font-weight: 500;
}

/* 时间线容器 */
.timeline-container {
  position: relative;
  padding-left: 30px;
}

.timeline-container::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #007aff;
  border-radius: 1px;
}

.timeline-item {
  position: relative;
  margin-bottom: 30px;
}

.timeline-marker {
  position: absolute;
  left: -22px;
  top: 20px;
}

.marker-dot {
  width: 12px;
  height: 12px;
  background: #007aff;
  border-radius: 50%;
  border: 3px solid #1d1d1f;
  box-shadow: 0 0 0 2px #007aff;
}

.marker-line {
  position: absolute;
  left: 5px;
  top: 12px;
  width: 2px;
  height: 40px;
  background: #007aff;
  border-radius: 1px;
}

.timeline-content {
  margin-left: 0;
}

.work-card {
  background: #3d3d40;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #4d4d50;
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.work-info {
  flex: 1;
}

.company-name {
  font-size: 18px;
  font-weight: 600;
  color: #f5f5f7;
  margin: 0 0 8px 0;
}

.job-title {
  font-size: 16px;
  color: #007aff;
  margin: 0;
  font-weight: 500;
}

.work-period {
  flex-shrink: 0;
}

.period-text {
  background: #007aff;
  color: white;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.work-details {
  margin-top: 16px;
}

.detail-section {
  margin-bottom: 12px;
}

.detail-section h4 {
  font-size: 14px;
  color: #007aff;
  margin: 0 0 6px 0;
  font-weight: 600;
}

.detail-section p {
  font-size: 14px;
  color: #f5f5f7;
  margin: 0;
  line-height: 1.6;
}

/* 教育网格 */
.education-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.education-card {
  background: #3d3d40;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #4d4d50;
}

.education-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.school-name {
  font-size: 18px;
  font-weight: 600;
  color: #f5f5f7;
  margin: 0;
}

.education-period {
  font-size: 12px;
  color: #8e8e93;
  white-space: nowrap;
}

.degree-info {
  font-size: 14px;
  color: #007aff;
  margin: 8px 0;
  font-weight: 500;
}

.education-description {
  font-size: 14px;
  color: #f5f5f7;
  line-height: 1.5;
  margin: 8px 0 0 0;
}

/* 项目网格 */
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.project-card {
  background: #3d3d40;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #4d4d50;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.project-name {
  font-size: 18px;
  font-weight: 600;
  color: #f5f5f7;
  margin: 0;
}

.project-period {
  font-size: 12px;
  color: #8e8e93;
  white-space: nowrap;
}

.project-role {
  font-size: 14px;
  color: #007aff;
  margin: 8px 0;
  font-weight: 500;
}

.project-description {
  font-size: 14px;
  color: #f5f5f7;
  line-height: 1.5;
  margin: 8px 0 0 0;
}

/* 其他信息网格 */
.additional-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.additional-card {
  background: #3d3d40;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #4d4d50;
}

.info-title {
  font-size: 16px;
  font-weight: 600;
  color: #f5f5f7;
  margin: 0 0 8px 0;
}

.info-description {
  font-size: 14px;
  color: #f5f5f7;
  margin: 0 0 6px 0;
}

.info-time {
  font-size: 12px;
  color: #8e8e93;
}

/* 清除浮动 */
.resume-template4::after {
  content: '';
  display: table;
  clear: both;
}

@media print {
  .resume-template4 {
    background: white;
    color: #1d1d1f;
  }

  .macos-header {
    display: none;
  }

  .macos-sidebar {
    float: none;
    width: 100%;
    background: white;
    border: none;
    padding: 20px;
  }

  .macos-content {
    margin-left: 0;
    background: white;
  }

  .content-section {
    background: white;
    border: 1px solid #e5e5e7;
    color: #1d1d1f;
  }

  .macos-card,
  .skill-item,
  .work-card,
  .education-card,
  .project-card,
  .additional-card {
    background: #f8f9fa;
    border: 1px solid #e5e5e7;
    color: #1d1d1f;
  }

  .skill-name,
  .company-name,
  .school-name,
  .project-name,
  .info-title {
    color: #1d1d1f;
  }

  .card-content p,
  .detail-section p,
  .education-description,
  .project-description,
  .info-description {
    color: #1d1d1f;
  }
}

@media (max-width: 768px) {
  .resume-template4 {
    max-width: 100%;
  }

  .macos-sidebar {
    float: none;
    width: 100%;
    min-height: auto;
  }

  .macos-content {
    margin-left: 0;
  }

  .education-grid,
  .projects-grid,
  .additional-grid {
    grid-template-columns: 1fr;
  }
}
</style>
