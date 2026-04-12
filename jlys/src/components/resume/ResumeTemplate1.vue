<template>
  <div class="resume-template1">
    <!-- 头部区域 -->
    <div class="resume-header">
      <div class="avatar-container">
        <div class="avatar">
          {{ resume.fullName ? resume.fullName.charAt(0) : '' }}
        </div>
      </div>
      <div class="header-content">
        <h1 class="name">{{ resume.fullName || '姓名' }}</h1>
        <p class="position">{{ resume.position || '期望职位' }}</p>
        <div class="contact-info">
          <div class="contact-item" v-if="resume.phone">
            <svg class="icon icon-phone" viewBox="0 0 24 24">
              <path d="M20.01 15.38c-1.23 0-2.42-.2-3.53-.56-.35-.12-.74-.03-1.01.24l-1.57 1.97c-2.83-1.35-5.48-3.9-6.89-6.83l1.95-1.66c.27-.28.35-.67.24-1.02-.37-1.11-.56-2.3-.56-3.53 0-.54-.45-.99-.99-.99H4.19C3.65 3 3 3.24 3 3.99 3 13.28 10.73 21 20.01 21c.71 0 .99-.63.99-1.18v-3.45c0-.54-.45-.99-.99-.99z"/>
            </svg>
            {{ resume.phone }}
          </div>
          <div class="contact-item" v-if="resume.email">
            <svg class="icon icon-email" viewBox="0 0 24 24">
              <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
            </svg>
            {{ resume.email }}
          </div>
          <div class="contact-item" v-if="resume.location">
            <svg class="icon icon-location" viewBox="0 0 24 24">
              <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
            </svg>
            {{ resume.location }}
          </div>
        </div>
      </div>
    </div>

    <!-- 主体内容 - 左右分栏 -->
    <div class="resume-content">
      <!-- 左侧栏 -->
      <div class="left-column">
        <!-- 个人简介 -->
        <div v-if="resume.profile" class="section">
          <h2 class="section-title">
            <svg class="icon icon-section" viewBox="0 0 24 24">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
            </svg>
            个人简介
          </h2>
          <div class="section-content">
            <p class="profile-text">{{ resume.profile }}</p>
          </div>
        </div>

        <!-- 技能专长 -->
        <div v-if="resume.skills && resume.skills.length" class="section">
          <h2 class="section-title">
            <svg class="icon icon-section" viewBox="0 0 24 24">
              <path d="M12 3L1 9l11 6 9-4.91V17h2V9M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82z"/>
            </svg>
            技能专长
          </h2>
          <div class="section-content">
            <div class="skills-list">
              <div v-for="(skill, index) in resume.skills" :key="index" class="skill-item">
                <div class="skill-header">
                  <span class="skill-name">{{ skill.skillName }}</span>
                  <span class="skill-level">{{ skill.level }}/5</span>
                </div>
                <div class="skill-bar">
                  <div class="skill-progress" :style="{ width: (skill.level / 5) * 100 + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 其他信息 -->
        <div v-if="resume.additionalInfos && resume.additionalInfos.length" class="section">
          <h2 class="section-title">
            <svg class="icon icon-section" viewBox="0 0 24 24">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
            </svg>
            其他信息
          </h2>
          <div class="section-content">
            <div v-for="(info, index) in resume.additionalInfos" :key="index" class="additional-item">
              <h4 class="info-title">{{ info.name }}</h4>
              <p v-if="info.description" class="info-description">{{ info.description }}</p>
              <span v-if="info.time" class="info-time">{{ info.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧栏 -->
      <div class="right-column">
        <!-- 工作经历 -->
        <div v-if="resume.workExperiences && resume.workExperiences.length" class="section">
          <h2 class="section-title">
            <svg class="icon icon-section" viewBox="0 0 24 24">
              <path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/>
            </svg>
            工作经历
          </h2>
          <div class="section-content">
            <div v-for="(work, index) in resume.workExperiences" :key="index" class="work-item">
              <div class="work-header">
                <h3 class="company">{{ work.company }}</h3>
                <span class="period">{{ formatDate(work.startDate) }} - {{ formatDate(work.endDate) }}</span>
              </div>
              <p class="job-title">{{ work.position }}</p>
              <div v-if="work.responsibility" class="responsibility">
                <h4>工作职责：</h4>
                <p>{{ work.responsibility }}</p>
              </div>
              <div v-if="work.achievement" class="achievement">
                <h4>工作成果：</h4>
                <p>{{ work.achievement }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 教育经历 -->
        <div v-if="resume.educations && resume.educations.length" class="section">
          <h2 class="section-title">
            <svg class="icon icon-section" viewBox="0 0 24 24">
              <path d="M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82zM12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/>
            </svg>
            教育经历
          </h2>
          <div class="section-content">
            <div v-for="(edu, index) in resume.educations" :key="index" class="education-item">
              <div class="education-header">
                <h3 class="school">{{ edu.school }}</h3>
                <span class="period">{{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) }}</span>
              </div>
              <p class="major">{{ edu.major }} | {{ edu.degree }}</p>
              <p v-if="edu.description" class="description">{{ edu.description }}</p>
            </div>
          </div>
        </div>

        <!-- 项目经验 -->
        <div v-if="resume.projectExperiences && resume.projectExperiences.length" class="section">
          <h2 class="section-title">
            <svg class="icon icon-section" viewBox="0 0 24 24">
              <path d="M10 4v3h2.21l-3.42 8H6v3h8v-3h-2.21l3.42-8H18V4z"/>
            </svg>
            项目经验
          </h2>
          <div class="section-content">
            <div v-for="(project, index) in resume.projectExperiences" :key="index" class="project-item">
              <div class="project-header">
                <h3 class="project-name">{{ project.projectName }}</h3>
                <span class="period">{{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) }}</span>
              </div>
              <p class="role">角色：{{ project.role }}</p>
              <p v-if="project.description" class="description">{{ project.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResumeTemplate1',
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
.resume-template1 {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.08);
  font-family: 'Helvetica Neue', 'Arial', sans-serif;
  line-height: 1.5;
  color: #333;
}

/* 头部样式 - 更精致的商务风格 */
.resume-header {
  background: linear-gradient(135deg, #0d2b4e 0%, #1a3a6e 100%);
  color: white;
  padding: 40px 50px;
  display: flex;
  align-items: center;
  gap: 40px;
  position: relative;
}

.resume-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #4fc3f7, #1a3a6e);
}

.avatar-container {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
  flex-shrink: 0;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #f0f4f8;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a3a6e;
  font-size: 50px;
  font-weight: bold;
}

.header-content {
  flex: 1;
}

.header-content .name {
  font-size: 34px;
  font-weight: 600;
  margin: 0 0 10px 0;
  letter-spacing: 0.5px;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.header-content .position {
  font-size: 18px;
  color: #d1e0f7;
  margin: 0 0 25px 0;
  font-weight: 400;
  position: relative;
  display: inline-block;
  padding-bottom: 8px;
}

.header-content .position::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 2px;
  background: #4fc3f7;
}

.contact-info {
  display: flex;
  flex-wrap: wrap;
  gap: 25px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #d1e0f7;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.contact-item:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 图标样式 */
.icon {
  width: 16px;
  height: 16px;
  margin-right: 8px;
  vertical-align: middle;
  fill: currentColor;
}

.icon-phone {
  color: #4fc3f7;
}

.icon-email {
  color: #4fc3f7;
}

.icon-location {
  color: #4fc3f7;
}

.icon-section {
  width: 18px;
  height: 18px;
  margin-right: 10px;
  vertical-align: text-bottom;
}

/* 主体内容 - 更专业的商务布局 */
.resume-content {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 0;
}

.left-column {
  background: #f8fafc;
  padding: 40px 30px;
  border-right: 1px solid #e6edf4;
}

.right-column {
  padding: 40px 35px;
}

/* 通用section样式 - 更精致的商务细节 */
.section {
  margin-bottom: 35px;
  position: relative;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #0d2b4e;
  margin: 0 0 18px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #e6edf4;
  letter-spacing: 0.5px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 60px;
  height: 2px;
  background: #1a3a6e;
}

.section-content {
  padding-left: 5px;
}

/* 左侧栏特殊样式 */
.left-column .section-title {
  color: #0d2b4e;
}

.left-column .section-title::after {
  background: #1a3a6e;
}

.profile-text {
  color: #555;
  line-height: 1.8;
  margin: 0;
  font-size: 14px;
  text-align: justify;
}

/* 技能样式 - 更专业的展示 */
.skills-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.skill-item {
  background: white;
  padding: 15px;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  border-left: 3px solid #1a3a6e;
  transition: all 0.3s ease;
}

.skill-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.skill-name {
  font-weight: 500;
  color: #333;
  font-size: 14px;
  letter-spacing: 0.3px;
}

.skill-level {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

.skill-bar {
  height: 6px;
  background: #e6edf4;
  border-radius: 3px;
  overflow: hidden;
}

.skill-progress {
  height: 100%;
  background: linear-gradient(90deg, #1a3a6e, #2a568a);
  transition: width 0.5s ease;
}

/* 其他信息样式 - 更精致的卡片 */
.additional-item {
  background: white;
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  border-left: 3px solid #1a3a6e;
  transition: all 0.3s ease;
}

.additional-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
}

.info-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 6px 0;
  letter-spacing: 0.3px;
}

.info-description {
  font-size: 13px;
  color: #555;
  margin: 0 0 6px 0;
  line-height: 1.6;
}

.info-time {
  font-size: 12px;
  color: #7f8c8d;
  display: block;
  font-weight: 500;
}

/* 右侧栏样式 - 更专业的排版 */
.work-item, .education-item, .project-item {
  margin-bottom: 30px;
  padding-bottom: 25px;
  border-bottom: 1px solid #e6edf4;
  position: relative;
}

.work-item:last-child, .education-item:last-child, .project-item:last-child {
  border-bottom: none;
}

.work-header, .education-header, .project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.company, .school, .project-name {
  font-size: 17px;
  font-weight: 600;
  color: #0d2b4e;
  margin: 0;
  letter-spacing: 0.3px;
}

.period {
  font-size: 14px;
  color: #7f8c8d;
  white-space: nowrap;
  font-weight: 500;
  background: #f0f4f8;
  padding: 3px 8px;
  border-radius: 4px;
}

.job-title, .major, .role {
  font-size: 15px;
  color: #2a568a;
  margin: 10px 0;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.responsibility, .achievement {
  margin: 15px 0;
}

.responsibility h4, .achievement h4 {
  font-size: 14px;
  color: #333;
  margin: 0 0 8px 0;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.responsibility p, .achievement p {
  font-size: 14px;
  color: #555;
  line-height: 1.7;
  margin: 0;
  padding-left: 15px;
  position: relative;
}

.responsibility p::before, .achievement p::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #1a3a6e;
}

.description {
  font-size: 14px;
  color: #555;
  line-height: 1.7;
  margin: 10px 0 0 0;
}

/* 打印样式 */
@media print {
  .resume-template1 {
    box-shadow: none;
    margin: 0;
  }

  .left-column {
    background: #f8fafc !important;
  }

  .resume-header::after,
  .section-title::after,
  .header-content .position::after {
    display: none;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-header {
    flex-direction: column;
    text-align: center;
    padding: 30px;
    gap: 25px;
  }

  .header-content .position::after {
    left: 50%;
    transform: translateX(-50%);
  }

  .avatar-container {
    margin-bottom: 15px;
  }

  .resume-content {
    grid-template-columns: 1fr;
  }

  .left-column {
    border-right: none;
    border-bottom: 1px solid #e6edf4;
  }

  .contact-info {
    justify-content: center;
  }

  .section-title::after {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>