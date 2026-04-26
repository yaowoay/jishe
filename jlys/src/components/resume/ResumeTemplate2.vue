<template>
  <div class="resume-template2">
    <!-- 头部卡片 -->
    <div class="header-card">
      <div class="header-content">
        <div class="name-section">
          <h1 class="name">{{ resume.fullName || '姓名' }}</h1>
          <p class="position">{{ resume.position || '期望职位' }}</p>
        </div>
        <div class="contact-section">
          <div class="contact-grid">
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
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="resume-content">
      <!-- 个人简介卡片 -->
      <div v-if="resume.profile" class="content-card">
        <div class="card-header">
          <div class="card-icon">👤</div>
          <h2 class="card-title">个人简介</h2>
        </div>
        <div class="card-content">
          <p class="profile-text">{{ resume.profile }}</p>
        </div>
      </div>

      <!-- 工作经历卡片 -->
      <div v-if="resume.workExperiences && resume.workExperiences.length" class="content-card">
        <div class="card-header">
          <div class="card-icon">💼</div>
          <h2 class="card-title">工作经历</h2>
        </div>
        <div class="card-content">
          <div class="timeline">
            <div v-for="(work, index) in resume.workExperiences" :key="index" class="timeline-item">
              <div class="timeline-marker"></div>
              <div class="timeline-content">
                <div class="work-card">
                  <div class="work-header">
                    <h3 class="company">{{ work.company }}</h3>
                    <span class="period">{{ formatDate(work.startDate) }} - {{ formatDate(work.endDate) }}</span>
                  </div>
                  <p class="job-title">{{ work.position }}</p>
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

      <!-- 教育经历卡片 -->
      <div v-if="resume.educations && resume.educations.length" class="content-card">
        <div class="card-header">
          <div class="card-icon">🎓</div>
          <h2 class="card-title">教育经历</h2>
        </div>
        <div class="card-content">
          <div class="education-grid">
            <div v-for="(edu, index) in resume.educations" :key="index" class="education-card">
              <div class="education-header">
                <h3 class="school">{{ edu.school }}</h3>
                <span class="period">{{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) }}</span>
              </div>
              <p class="major">{{ edu.major }} | {{ edu.degree }}</p>
              <p v-if="edu.description" class="description">{{ edu.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 技能专长卡片 -->
      <div v-if="resume.skills && resume.skills.length" class="content-card">
        <div class="card-header">
          <div class="card-icon">⚡</div>
          <h2 class="card-title">技能专长</h2>
        </div>
        <div class="card-content">
          <div class="skills-grid">
            <div v-for="(skill, index) in resume.skills" :key="index" class="skill-card">
              <div class="skill-header">
                <span class="skill-name">{{ skill.skillName }}</span>
                <div class="skill-level">
                  <div class="level-dots">
                    <span v-for="i in 5" :key="i" class="dot" :class="{ active: i <= skill.level }"></span>
                  </div>
                  <span class="level-text">{{ skill.level }}/5</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 项目经验卡片 -->
      <div v-if="resume.projectExperiences && resume.projectExperiences.length" class="content-card">
        <div class="card-header">
          <div class="card-icon">🚀</div>
          <h2 class="card-title">项目经验</h2>
        </div>
        <div class="card-content">
          <div class="projects-grid">
            <div v-for="(project, index) in resume.projectExperiences" :key="index" class="project-card">
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

      <!-- 其他信息卡片 -->
      <div v-if="resume.additionalInfos && resume.additionalInfos.length" class="content-card">
        <div class="card-header">
          <div class="card-icon">🏆</div>
          <h2 class="card-title">其他信息</h2>
        </div>
        <div class="card-content">
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
  name: 'ResumeTemplate2',
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
.resume-template2 {
  max-width: 900px;
  margin: 0 auto;
  background: #f8f9fa;
  padding: 20px;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  line-height: 1.6;
  color: #333;
}

/* 头部卡片 */
.header-card {
  background: linear-gradient(135deg, #afccec 0%, #2d4ed3 100%);
  border-radius: 20px;
  padding: 40px;
  color: white;
  margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 30px;
}

.name-section .name {
  font-size: 42px;
  font-weight: bold;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.name-section .position {
  font-size: 20px;
  color: #e8eaf6;
  margin: 0;
  font-weight: 300;
}

.contact-section {
  flex-shrink: 0;
}

.contact-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #e8eaf6;
}

.contact-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 主体内容 */
.resume-content {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

/* 内容卡片 */
.content-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.content-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.card-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  color: white;
}

.card-title {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0;
}

.card-content {
  color: #555;
}

/* 个人简介 */
.profile-text {
  font-size: 16px;
  line-height: 1.8;
  margin: 0;
  color: #555;
}

/* 时间线样式 */
.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, #667eea, #764ba2);
}

.timeline-item {
  position: relative;
  margin-bottom: 30px;
}

.timeline-marker {
  position: absolute;
  left: -22px;
  top: 8px;
  width: 12px;
  height: 12px;
  background: #667eea;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 0 0 3px #667eea;
}

.work-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  border-left: 4px solid #667eea;
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.company {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0;
}

.period {
  font-size: 14px;
  color: #7f8c8d;
  white-space: nowrap;
}

.job-title {
  font-size: 16px;
  color: #667eea;
  margin: 8px 0;
  font-weight: 500;
}

.work-details {
  margin-top: 15px;
}

.detail-section {
  margin-bottom: 12px;
}

.detail-section h4 {
  font-size: 14px;
  color: #555;
  margin: 0 0 5px 0;
  font-weight: 600;
}

.detail-section p {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.6;
}

/* 教育经历网格 */
.education-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.education-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  border-left: 4px solid #764ba2;
}

.education-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.school {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0;
}

.major {
  font-size: 14px;
  color: #764ba2;
  margin: 8px 0;
  font-weight: 500;
}

.description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin: 8px 0 0 0;
}

/* 技能网格 */
.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.skill-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px;
  border-left: 4px solid #667eea;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skill-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.skill-level {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-dots {
  display: flex;
  gap: 3px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ddd;
  transition: background-color 0.3s ease;
}

.dot.active {
  background: #667eea;
}

.level-text {
  font-size: 12px;
  color: #7f8c8d;
}

/* 项目网格 */
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.project-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  border-left: 4px solid #764ba2;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.project-name {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0;
}

.role {
  font-size: 14px;
  color: #764ba2;
  margin: 8px 0;
  font-weight: 500;
}

/* 其他信息网格 */
.additional-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.additional-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px;
  border-left: 4px solid #e74c3c;
}

.info-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 5px 0;
}

.info-description {
  font-size: 12px;
  color: #555;
  margin: 0 0 5px 0;
}

.info-time {
  font-size: 11px;
  color: #7f8c8d;
}

@media print {
  .resume-template2 {
    background: white;
    padding: 0;
  }

  .content-card {
    box-shadow: none;
    border: 1px solid #e0e0e0;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
  }

  .contact-grid {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .education-grid,
  .skills-grid,
  .projects-grid,
  .additional-grid {
    grid-template-columns: 1fr;
  }
}
</style>
