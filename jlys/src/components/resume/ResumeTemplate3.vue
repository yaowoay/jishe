<template>
  <div class="resume-template3">
    <!-- 创意头部 -->
    <div class="creative-header">
      <div class="header-background">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
        </div>
        <div class="header-content">
          <div class="name-container">
            <div class="name-circle">
              <h1 class="creative-name">{{ resume.fullName || '姓名' }}</h1>
            </div>
            <div class="title-line"></div>
            <p class="creative-position">{{ resume.position || '期望职位' }}</p>
          </div>
          <div class="contact-container">
            <div class="contact-bubbles">
              <div v-if="resume.phone" class="contact-bubble">
                <span class="contact-icon">📱</span>
                <span class="contact-text">{{ resume.phone }}</span>
              </div>
              <div v-if="resume.email" class="contact-bubble">
                <span class="contact-icon">📧</span>
                <span class="contact-text">{{ resume.email }}</span>
              </div>
              <div v-if="resume.location" class="contact-bubble">
                <span class="contact-icon">📍</span>
                <span class="contact-text">{{ resume.location }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创意内容区域 -->
    <div class="creative-content">
      <!-- 个人简介 -->
      <div v-if="resume.profile" class="creative-section">
        <div class="section-decoration">
          <div class="decoration-line"></div>
          <div class="decoration-icon">🎯</div>
          <div class="decoration-line"></div>
        </div>
        <h2 class="section-title">个人简介</h2>
        <div class="profile-container">
          <div class="profile-card">
            <div class="card-decoration"></div>
            <p class="profile-text">{{ resume.profile }}</p>
          </div>
        </div>
      </div>

      <!-- 工作经历 -->
      <div v-if="resume.workExperiences && resume.workExperiences.length" class="creative-section">
        <div class="section-decoration">
          <div class="decoration-line"></div>
          <div class="decoration-icon">💼</div>
          <div class="decoration-line"></div>
        </div>
        <h2 class="section-title">工作经历</h2>
        <div class="timeline-container">
          <div class="creative-timeline">
            <div v-for="(work, index) in resume.workExperiences" :key="index" class="timeline-node">
              <div class="node-marker">
                <div class="node-dot"></div>
                <div class="node-line"></div>
              </div>
              <div class="timeline-content">
                <div class="work-bubble">
                  <div class="work-header">
                    <h3 class="company-name">{{ work.company }}</h3>
                    <span class="time-badge">{{ formatDate(work.startDate) }} - {{ formatDate(work.endDate) }}</span>
                  </div>
                  <p class="job-title">{{ work.position }}</p>
                  <div v-if="work.responsibility || work.achievement" class="work-details">
                    <div v-if="work.responsibility" class="detail-item">
                      <span class="detail-icon">📋</span>
                      <div class="detail-content">
                        <h4>工作职责</h4>
                        <p>{{ work.responsibility }}</p>
                      </div>
                    </div>
                    <div v-if="work.achievement" class="detail-item">
                      <span class="detail-icon">🏆</span>
                      <div class="detail-content">
                        <h4>工作成果</h4>
                        <p>{{ work.achievement }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 技能专长 -->
      <div v-if="resume.skills && resume.skills.length" class="creative-section">
        <div class="section-decoration">
          <div class="decoration-line"></div>
          <div class="decoration-icon">⚡</div>
          <div class="decoration-line"></div>
        </div>
        <h2 class="section-title">技能专长</h2>
        <div class="skills-container">
          <div v-for="(skill, index) in resume.skills" :key="index" class="skill-bubble">
            <div class="skill-header">
              <span class="skill-name">{{ skill.skillName }}</span>
              <div class="skill-visual">
                <div class="skill-circles">
                  <div v-for="i in 5" :key="i" class="skill-circle" :class="{ filled: i <= skill.level }"></div>
                </div>
                <span class="skill-level">{{ skill.level }}/5</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 项目经验 -->
      <div v-if="resume.projectExperiences && resume.projectExperiences.length" class="creative-section">
        <div class="section-decoration">
          <div class="decoration-line"></div>
          <div class="decoration-icon">🚀</div>
          <div class="decoration-line"></div>
        </div>
        <h2 class="section-title">项目经验</h2>
        <div class="projects-container">
          <div v-for="(project, index) in resume.projectExperiences" :key="index" class="project-bubble">
            <div class="project-header">
              <h3 class="project-name">{{ project.projectName }}</h3>
              <span class="time-badge">{{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) }}</span>
            </div>
            <p class="project-role">角色：{{ project.role }}</p>
            <p v-if="project.description" class="project-description">{{ project.description }}</p>
          </div>
        </div>
      </div>

      <!-- 其他信息 -->
      <div v-if="resume.additionalInfos && resume.additionalInfos.length" class="creative-section">
        <div class="section-decoration">
          <div class="decoration-line"></div>
          <div class="decoration-icon">🏆</div>
          <div class="decoration-line"></div>
        </div>
        <h2 class="section-title">其他信息</h2>
        <div class="additional-container">
          <div v-for="(info, index) in resume.additionalInfos" :key="index" class="info-bubble">
            <h4 class="info-title">{{ info.name }}</h4>
            <p v-if="info.description" class="info-description">{{ info.description }}</p>
            <span v-if="info.time" class="info-time">{{ info.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResumeTemplate3',
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
.resume-template3 {
  max-width: 900px;
  margin: 0 auto;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Poppins', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #2c3e50;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 创意头部 */
.creative-header {
  position: relative;
  padding: 60px 40px;
  color: white;
}

.header-background {
  position: relative;
  z-index: 2;
}

.floating-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 100px;
  height: 100px;
  top: 20px;
  right: 80px;
  animation-delay: 0s;
}

.shape-2 {
  width: 60px;
  height: 60px;
  top: 120px;
  right: 40px;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  top: 60px;
  right: 160px;
  animation-delay: 4s;
}

.shape-4 {
  width: 40px;
  height: 40px;
  top: 180px;
  right: 120px;
  animation-delay: 6s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(180deg); }
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
}

.name-container {
  flex: 1;
  text-align: center;
}

.name-circle {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.1));
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.creative-name {
  font-size: 32px;
  font-weight: 800;
  margin: 0;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  letter-spacing: -1px;
}

.title-line {
  width: 100px;
  height: 4px;
  background: linear-gradient(90deg, #ff6b6b, #feca57);
  margin: 0 auto 20px;
  border-radius: 2px;
  animation: expand 2s ease-in-out infinite;
}

@keyframes expand {
  0%, 100% { width: 100px; }
  50% { width: 150px; }
}

.creative-position {
  font-size: 20px;
  color: #e0e7ff;
  margin: 0;
  font-weight: 300;
}

.contact-container {
  flex-shrink: 0;
}

.contact-bubbles {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.contact-bubble {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.15);
  padding: 15px 20px;
  border-radius: 30px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease;
}

.contact-bubble:hover {
  transform: translateX(10px);
}

.contact-icon {
  font-size: 18px;
}

.contact-text {
  font-size: 14px;
  color: #e0e7ff;
}

/* 创意内容区域 */
.creative-content {
  background: white;
  border-radius: 50px 50px 0 0;
  padding: 50px 40px;
  position: relative;
  margin-top: -30px;
}

/* 创意部分 */
.creative-section {
  margin-bottom: 50px;
}

.section-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
}

.decoration-line {
  height: 3px;
  width: 80px;
  background: linear-gradient(90deg, transparent, #667eea, transparent);
  border-radius: 2px;
}

.decoration-icon {
  font-size: 28px;
  margin: 0 25px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  color: white;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.section-title {
  text-align: center;
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 30px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 个人简介 */
.profile-container {
  display: flex;
  justify-content: center;
}

.profile-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 35px;
  border-radius: 25px;
  border-left: 8px solid #667eea;
  max-width: 600px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.card-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  opacity: 0.1;
}

.profile-text {
  font-size: 16px;
  line-height: 1.8;
  margin: 0;
  color: #4b5563;
  position: relative;
  z-index: 1;
}

/* 时间线样式 */
.timeline-container {
  position: relative;
  padding-left: 50px;
}

.creative-timeline {
  position: relative;
}

.creative-timeline::before {
  content: '';
  position: absolute;
  left: 25px;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, #667eea, #764ba2, #ff6b6b);
  border-radius: 2px;
  animation: flow 3s ease-in-out infinite;
}

@keyframes flow {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

.timeline-node {
  position: relative;
  margin-bottom: 40px;
}

.node-marker {
  position: absolute;
  left: -35px;
  top: 20px;
}

.node-dot {
  width: 20px;
  height: 20px;
  background: #667eea;
  border-radius: 50%;
  border: 4px solid white;
  box-shadow: 0 0 0 4px #667eea;
  animation: pulse 2s ease-in-out infinite;
}

.node-line {
  position: absolute;
  left: 8px;
  top: 20px;
  width: 3px;
  height: 50px;
  background: #667eea;
  border-radius: 2px;
}

.timeline-content {
  margin-left: 20px;
}

.work-bubble {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 25px;
  padding: 30px;
  border-left: 8px solid #667eea;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.work-bubble:hover {
  transform: translateY(-5px);
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.company-name {
  font-size: 22px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.time-badge {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.job-title {
  font-size: 18px;
  color: #667eea;
  margin: 10px 0 20px 0;
  font-weight: 600;
}

.work-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-item {
  display: flex;
  gap: 15px;
}

.detail-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.detail-content h4 {
  font-size: 16px;
  color: #374151;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.detail-content p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

/* 技能样式 */
.skills-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
}

.skill-bubble {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 25px;
  padding: 25px;
  border-left: 8px solid #667eea;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.skill-bubble:hover {
  transform: translateY(-5px);
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.skill-name {
  font-weight: 700;
  color: #2c3e50;
  font-size: 16px;
}

.skill-visual {
  display: flex;
  align-items: center;
  gap: 15px;
}

.skill-circles {
  display: flex;
  gap: 5px;
}

.skill-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #e5e7eb;
  transition: background 0.3s ease;
}

.skill-circle.filled {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.skill-level {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 600;
}

/* 项目样式 */
.projects-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 25px;
}

.project-bubble {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 25px;
  padding: 30px;
  border-left: 8px solid #764ba2;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.project-bubble:hover {
  transform: translateY(-5px);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.project-name {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.project-role {
  font-size: 16px;
  color: #764ba2;
  margin: 10px 0;
  font-weight: 600;
}

.project-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 15px 0 0 0;
}

/* 其他信息 */
.additional-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
}

.info-bubble {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 25px;
  padding: 25px;
  border-left: 8px solid #ff6b6b;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.info-bubble:hover {
  transform: translateY(-5px);
}

.info-title {
  font-size: 16px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.info-description {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 8px 0;
}

.info-time {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

@media print {
  .resume-template3 {
    background: white;
  }

  .creative-header {
    background: #667eea;
  }

  .floating-shapes {
    display: none;
  }

  .name-circle {
    background: #667eea;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 30px;
  }

  .name-circle {
    width: 150px;
    height: 150px;
  }

  .creative-name {
    font-size: 24px;
  }

  .creative-content {
    padding: 30px 20px;
  }

  .skills-container,
  .projects-container,
  .additional-container {
    grid-template-columns: 1fr;
  }
}
</style>
