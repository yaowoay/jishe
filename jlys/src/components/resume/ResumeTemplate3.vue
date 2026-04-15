<template>
  <div class="resume-template-photo">
    <div class="resume-card">
      <!-- 头部：姓名和照片 -->
      <div class="resume-header">
        <div class="header-left">
          <h1 class="name">{{ resume.fullName || '张三' }}</h1>
          <p class="position">{{ resume.position || '求职意向' }}</p>
        </div>
        <div class="header-right">
          <div class="photo">
            <img :src="photoUrl" alt="证件照" @error="handleImageError" />
          </div>
        </div>
      </div>

      <!-- 联系方式表格 -->
      <div class="contact-section">
        <table class="contact-table">
          <tbody>
          <tr>
            <td class="label">电话</td>
            <td class="value">{{ resume.phone || '138-1234-5678' }}</td>
            <td class="label">邮箱</td>
            <td class="value">{{ resume.email || 'zhangsan@email.com' }}</td>
          </tr>
          <tr>
            <td class="label">性别</td>
            <td class="value">{{ gender || '男' }}</td>
            <td class="label">年龄</td>
            <td class="value">{{ age || '28' }}</td>
          </tr>
          <tr v-if="resume.location">
            <td class="label">地址</td>
            <td class="value" colspan="3">{{ resume.location }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 教育背景 -->
      <div class="section">
        <h2 class="section-title">教育背景</h2>
        <div class="section-content">
          <div v-for="(edu, idx) in educationList" :key="idx" class="edu-item">
            <div class="edu-header">
              <span class="edu-degree">{{ edu.degree }}</span>
              <span class="edu-major">{{ edu.major }}</span>
              <span class="edu-date">{{ edu.date }}</span>
            </div>
            <div class="edu-school">{{ edu.school }}</div>
          </div>
        </div>
      </div>

      <!-- 实习/项目经历 -->
      <div class="section">
        <h2 class="section-title">实习/项目经历</h2>
        <div class="section-content">
          <div v-for="(exp, idx) in projectList" :key="idx" class="exp-item">
            <div class="exp-title">{{ exp.name }}</div>
            <ul class="exp-list">
              <li v-for="(item, i) in exp.achievements" :key="i">{{ item }}</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- IT技能 -->
      <div class="section">
        <h2 class="section-title">IT技能</h2>
        <div class="section-content">
          <div class="skills-grid">
            <div v-for="(skill, idx) in skillList" :key="idx" class="skill-item">
              <span class="skill-name">{{ skill.name }}</span>
              <span class="skill-level">{{ skill.level }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 其他信息 -->
      <div class="section">
        <h2 class="section-title">其他信息</h2>
        <div class="section-content">
          <div class="other-item" v-if="github">
            <span class="other-label">GitHub：</span>
            <a :href="github" target="_blank">{{ github }}</a>
          </div>
          <div class="other-item" v-if="blog">
            <span class="other-label">技术博客：</span>
            <a :href="blog" target="_blank">{{ blog }}</a>
          </div>
          <div class="other-item" v-if="resume.summary">
            <span class="other-label">自我介绍：</span>
            <p>{{ resume.summary }}</p>
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
  data() {
    return {
      photoUrl: '',
      gender: '男',
      age: '',
      educationList: [],
      projectList: [],
      skillList: [],
      github: '',
      blog: ''
    }
  },
  mounted() {
    this.initData()
  },
  watch: {
    resume: {
      deep: true,
      handler() {
        this.initData()
      }
    }
  },
  methods: {
    handleImageError(e) {
      e.target.src = 'https://via.placeholder.com/120x150?text=Photo'
    },
    initData() {
      // 照片
      this.photoUrl = '/template/resume_with_photo-main/photo.jpg'

      // 年龄
      this.age = this.resume.workYears ? this.resume.workYears + '岁' : ''

      // 教育背景
      this.educationList = (this.resume.educations || []).map(edu => ({
        school: edu.school,
        major: edu.major,
        degree: edu.degree,
        date: edu.startDate ? `${edu.startDate} - ${edu.endDate || '至今'}` : ''
      }))

      // 如果没有教育经历，显示默认
      if (this.educationList.length === 0) {
        this.educationList = [{
          school: '示例大学',
          major: '计算机科学与技术',
          degree: '本科',
          date: '2016 - 2020'
        }]
      }

      // 项目经验
      const projects = this.resume.projectExperiences || []
      this.projectList = projects.map(proj => ({
        name: proj.projectName,
        achievements: proj.description ? proj.description.split('\n').filter(s => s.trim()) : []
      }))

      // 工作经历也加入项目列表
      const works = this.resume.workExperiences || []
      works.forEach(work => {
        this.projectList.push({
          name: work.company,
          achievements: [work.responsibility].filter(Boolean)
        })
      })

      // 技能
      this.skillList = (this.resume.skills || []).map(skill => ({
        name: skill.skillName,
        level: this.getSkillLevel(skill.level)
      }))

      // GitHub 和博客（从 additionalInfos 中获取）
      const infos = this.resume.additionalInfos || []
      infos.forEach(info => {
        if (info.name && info.name.toLowerCase().includes('github')) {
          this.github = info.name
        }
        if (info.name && info.name.toLowerCase().includes('blog')) {
          this.blog = info.name
        }
      })
    },
    getSkillLevel(level) {
      const levelMap = { 1: '了解', 2: '基础', 3: '熟练', 4: '精通', 5: '专家' }
      return levelMap[level] || '熟练'
    }
  }
}
</script>

<style scoped>
.resume-template-photo {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
  background: #f0f0f0;
  padding: 30px;
}

.resume-card {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  padding: 40px;
  border-radius: 4px;
}

/* 头部 */
.resume-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #3498db;
  padding-bottom: 20px;
  margin-bottom: 20px;
}

.name {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.position {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.photo {
  width: 100px;
  height: 120px;
  overflow: hidden;
  border: 2px solid #ddd;
  border-radius: 4px;
}

.photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 联系方式表格 */
.contact-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 25px;
  font-size: 13px;
}

.contact-table td {
  padding: 8px;
}

.contact-table .label {
  font-weight: bold;
  color: #666;
  width: 60px;
  background: #f5f5f5;
}

.contact-table .value {
  color: #333;
  width: 150px;
}

/* 章节样式 */
.section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #3498db;
  border-left: 4px solid #3498db;
  padding-left: 12px;
  margin: 0 0 15px 0;
}

.section-content {
  padding-left: 16px;
}

/* 教育经历 */
.edu-item {
  margin-bottom: 15px;
}

.edu-header {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 5px;
}

.edu-degree {
  font-weight: bold;
  color: #2c3e50;
}

.edu-major {
  color: #555;
}

.edu-date {
  color: #999;
  font-size: 12px;
}

.edu-school {
  color: #666;
  font-size: 13px;
}

/* 项目经历 */
.exp-item {
  margin-bottom: 20px;
}

.exp-title {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 15px;
}

.exp-list {
  margin: 0;
  padding-left: 20px;
}

.exp-list li {
  margin: 5px 0;
  line-height: 1.5;
  color: #555;
  font-size: 13px;
}

/* 技能 */
.skills-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.skill-item {
  display: flex;
  justify-content: space-between;
  width: calc(50% - 20px);
  padding: 5px 0;
  border-bottom: 1px dashed #eee;
}

.skill-name {
  font-weight: bold;
  color: #2c3e50;
}

.skill-level {
  color: #999;
  font-size: 12px;
}

/* 其他信息 */
.other-item {
  margin-bottom: 10px;
}

.other-label {
  font-weight: bold;
  color: #555;
}

.other-item a {
  color: #3498db;
  text-decoration: none;
}

.other-item a:hover {
  text-decoration: underline;
}

.other-item p {
  margin: 5px 0 0 0;
  color: #666;
  line-height: 1.6;
}

@media print {
  .resume-template-photo {
    padding: 0;
    background: white;
  }
  .resume-card {
    box-shadow: none;
    padding: 20px;
  }
}

@media (max-width: 600px) {
  .resume-header {
    flex-direction: column-reverse;
    text-align: center;
    gap: 15px;
  }
  .contact-table td {
    display: block;
    width: 100%;
  }
  .skills-grid {
    flex-direction: column;
  }
  .skill-item {
    width: 100%;
  }
}
</style>