<template>
  <div class="resume-template-clean">
    <div class="resume-wrapper">
      <!-- 个人信息头部 -->
      <div class="resume-head">
        <div class="head-left">
          <h1 class="user-name">{{ resume.fullName || '姓名' }}</h1>
          <p class="user-job">{{ resume.position || '求职岗位' }}</p>
        </div>
        <div class="head-right">
          <div class="avatar-box">
            <img :src="photoUrl" alt="个人照片" @error="handleImageError" />
          </div>
        </div>
      </div>

      <!-- 基础信息栏 -->
      <div class="base-info">
        <div class="info-item">
          <span class="label">电话</span>
          <span class="text">{{ resume.phone || '13800000000' }}</span>
        </div>
        <div class="info-item">
          <span class="label">邮箱</span>
          <span class="text">{{ resume.email || 'example@email.com' }}</span>
        </div>
        <div class="info-item">
          <span class="label">性别</span>
          <span class="text">{{ gender || '男' }}</span>
        </div>
        <div class="info-item">
          <span class="label">年龄</span>
          <span class="text">{{ age || '25岁' }}</span>
        </div>
        <div class="info-item" v-if="resume.location">
          <span class="label">现居地</span>
          <span class="text">{{ resume.location }}</span>
        </div>
      </div>

      <!-- 教育背景 -->
      <div class="resume-section">
        <h2 class="section-title">教育背景</h2>
        <div class="section-body">
          <div v-for="(edu, idx) in educationList" :key="idx" class="item-block">
            <div class="item-head">
              <span class="school">{{ edu.school }}</span>
              <span class="date">{{ edu.date }}</span>
            </div>
            <div class="item-info">
              {{ edu.degree }} | {{ edu.major }}
            </div>
          </div>
        </div>
      </div>

      <!-- 工作/项目经历 -->
      <div class="resume-section">
        <h2 class="section-title">工作与项目经历</h2>
        <div class="section-body">
          <div v-for="(exp, idx) in projectList" :key="idx" class="item-block">
            <div class="item-head">
              <span class="company">{{ exp.name }}</span>
            </div>
            <ul class="desc-list" v-if="exp.achievements.length">
              <li v-for="(item, i) in exp.achievements" :key="i">{{ item }}</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 专业技能 -->
      <div class="resume-section">
        <h2 class="section-title">专业技能</h2>
        <div class="section-body">
          <div class="skills-wrap">
            <div v-for="(skill, idx) in skillList" :key="idx" class="skill-tag">
              {{ skill.name }} <small>{{ skill.level }}</small>
            </div>
          </div>
        </div>
      </div>

      <!-- 自我评价 & 附加信息 -->
      <div class="resume-section">
        <h2 class="section-title">自我评价</h2>
        <div class="section-body">
          <p class="self-desc" v-if="resume.summary">{{ resume.summary }}</p>
          <p class="self-desc" v-else>
            具备良好的学习能力与责任心，工作认真负责，善于沟通协作，能够快速适应新环境与新任务，对技术有持续学习的热情。
          </p>

          <div class="extra-links" v-if="github || blog">
            <a v-if="github" :href="github" target="_blank" class="link-item">GitHub：{{ github }}</a>
            <a v-if="blog" :href="blog" target="_blank" class="link-item">技术博客：{{ blog }}</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResumeTemplateClean',
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
    // 图片加载失败默认图
    handleImageError(e) {
      e.target.src = 'https://via.placeholder.com/110x150?text=Photo'
    },
    initData() {
      // 头像地址（优先使用生成结果里的头像）
      this.photoUrl = this.resume.avatarUrl || this.resume.avatar || this.resume.photoUrl || this.resume.imgUrl || '/template/resume_with_photo-main/photo.jpg'

      // 年龄
      this.age = this.resume.workYears ? `${this.resume.workYears}岁` : '25岁'

      // 教育经历
      this.educationList = (this.resume.educations || []).map(edu => ({
        school: edu.school,
        major: edu.major,
        degree: edu.degree,
        date: edu.startDate ? `${edu.startDate} - ${edu.endDate || '至今'}` : ''
      }))
      if (this.educationList.length === 0) {
        this.educationList = [{
          school: '某某大学',
          major: '计算机科学与技术',
          degree: '本科',
          date: '2016 - 2020'
        }]
      }

      // 项目 + 工作经历
      this.projectList = []
      const projects = this.resume.projectExperiences || []
      projects.forEach(proj => {
        this.projectList.push({
          name: proj.projectName,
          achievements: proj.description ? proj.description.split('\n').filter(i => i.trim()) : []
        })
      })

      const works = this.resume.workExperiences || []
      works.forEach(work => {
        this.projectList.push({
          name: work.company,
          achievements: work.responsibility ? [work.responsibility] : []
        })
      })

      // 技能
      this.skillList = (this.resume.skills || []).map(skill => ({
        name: skill.skillName,
        level: this.getSkillLevel(skill.level)
      }))

      // GitHub / 博客
      const infos = this.resume.additionalInfos || []
      infos.forEach(info => {
        if (info.name?.toLowerCase().includes('github')) this.github = info.name
        if (info.name?.toLowerCase().includes('blog')) this.blog = info.name
      })
    },
    // 技能等级映射
    getSkillLevel(level) {
      const map = { 1: '入门', 2: '掌握', 3: '熟练', 4: '精通', 5: '专家' }
      return map[level] || '熟练'
    }
  }
}
</script>

<style scoped>
/* 整体容器：简约正式 */
.resume-template-clean {
  width: 100%;
  background: #f7f7f7;
  padding: 25px 0;
  font-family: "Microsoft YaHei", "PingFang SC", sans-serif;
  color: #333;
}

.resume-wrapper {
  width: 100%;
  max-width: 860px;
  margin: 0 auto;
  background: #fff;
  padding: 50px 55px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  box-sizing: border-box;
}

/* 头部：姓名 + 照片 */
.resume-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 25px;
}

.user-name {
  font-size: 26px;
  font-weight: 500;
  color: #222;
  margin: 0 0 6px 0;
}

.user-job {
  font-size: 15px;
  color: #666;
  margin: 0;
}

.avatar-box {
  width: 110px;
  height: 150px;
  border: 1px solid #eee;
  overflow: hidden;
}

.avatar-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 基础信息行 */
.base-info {
  display: flex;
  flex-wrap: wrap;
  gap: 18px 30px;
  margin-bottom: 35px;
  font-size: 14px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  color: #666;
  margin-right: 8px;
  font-weight: 500;
}

.info-item .text {
  color: #333;
}

/* 模块通用 */
.resume-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 17px;
  font-weight: 500;
  color: #222;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin: 0 0 16px 0;
}

.section-body {
  padding-left: 2px;
}

/* 列表项 */
.item-block {
  margin-bottom: 20px;
}

.item-head {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 500;
  color: #222;
  margin-bottom: 6px;
}

.item-info {
  font-size: 14px;
  color: #555;
}

.desc-list {
  margin: 8px 0 0 0;
  padding-left: 20px;
}

.desc-list li {
  font-size: 14px;
  color: #444;
  line-height: 1.6;
  margin-bottom: 4px;
}

/* 技能标签 */
.skills-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 3px;
  font-size: 14px;
  color: #333;
}

.skill-tag small {
  color: #888;
  margin-left: 4px;
  font-size: 12px;
}

/* 自我评价 */
.self-desc {
  font-size: 14px;
  color: #444;
  line-height: 1.7;
  margin: 0 0 15px 0;
}

.extra-links {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 10px;
}

.link-item {
  font-size: 14px;
  color: #333;
  text-decoration: none;
}

.link-item:hover {
  color: #000;
}

/* 打印适配 */
@media print {
  .resume-template-clean {
    background: #fff;
    padding: 0;
  }
  .resume-wrapper {
    box-shadow: none;
    padding: 20px;
  }
}

/* 移动端适配 */
@media (max-width: 600px) {
  .resume-wrapper {
    padding: 30px 20px;
  }
  .resume-head {
    flex-direction: column-reverse;
    gap: 15px;
    text-align: center;
  }
  .base-info {
    justify-content: center;
  }
}
</style>