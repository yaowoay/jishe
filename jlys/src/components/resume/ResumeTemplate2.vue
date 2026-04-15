<template>
  <div class="resume-template-it">
    <div class="container">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="title">
          <h1>{{ resume.fullName || '姓名' }}的简历</h1>
          <h2>{{ resume.position || '期望职位' }}</h2>
        </div>

        <ul class="side-info">
          <!-- 基本信息 -->
          <li class="someRight">
            <dt><i class="fa fa-bookmark"></i>Basic info. 基本信息</dt>
            <dd><span>个人信息:</span> {{ resume.fullName || '' }} / {{ gender }} / {{ age }} / {{ grade }}</dd>
            <dd><span>学校：</span>{{ school }}</dd>
            <dd><span>专业：</span>{{ major }}</dd>
            <dd v-if="englishLevel"><span>英语水平:</span> {{ englishLevel }}</dd>
            <dd v-if="github"><span>GitHub:</span> <a :href="github" target="_blank">{{ github }}</a></dd>
          </li>

          <!-- 联系方式 -->
          <li class="someRight">
            <dt><i class="fa fa-bookmark"></i>Contact. 联系方式</dt>
            <dd v-for="(item, idx) in contactList" :key="idx">
              <i :class="'fa ' + item.icon"></i>{{ item.name }}:
              <a :href="item.link" target="_blank">{{ item.value }}</a>
            </dd>
          </li>

          <!-- 应聘岗位 -->
          <li class="someRight">
            <dt><i class="fa fa-bookmark"></i>Application. 应聘岗位</dt>
            <dd>{{ resume.position || '未填写' }}</dd>
          </li>

          <!-- 个人简介 -->
          <li v-if="resume.profile" class="someRight">
            <dt><i class="fa fa-bookmark"></i>Profile. 个人简介</dt>
            <dd>{{ resume.profile }}</dd>
          </li>

          <!-- 技能点 -->
          <li class="skill">
            <dt><i class="fa fa-bookmark"></i>Tech. 主要技能点</dt>
            <dd v-for="(skill, idx) in techList" :key="idx">
              <div class="skill-name">{{ skill.name }}</div>
              <div class="skill-bar-wrap">
                <div class="skill-bar" :style="{ width: skill.percentage }"></div>
              </div>
            </dd>
          </li>
        </ul>
      </div>

      <!-- 主内容 -->
      <div class="main">
        <ul class="main-info">
          <!-- 技能清单 -->
          <li class="skill">
            <dt><i class="fa fa-bookmark"></i>Skill. 技能清单</dt>
            <ul v-for="(item, idx) in skillList" :key="idx" class="exp">
              <li v-html="item.des"></li>
            </ul>
          </li>

          <!-- 工作经历 -->
          <li class="experience">
            <dt><i class="fa fa-bookmark"></i>Experience. 工作经历</dt>
            <ul class="exp">
              <li v-for="(work, idx) in workList" :key="idx">
                <h4>{{ work.company }} <span>{{ work.date }}</span></h4>
                <p v-html="work.desc"></p>
              </li>
            </ul>
          </li>

          <!-- 项目经验 -->
          <li class="experience">
            <dt><i class="fa fa-bookmark"></i>Projects. 项目经验</dt>
            <ul class="exp">
              <li v-for="(project, idx) in projectList" :key="idx">
                <div class="circle"></div>
                <h4>{{ project.name }} <span>{{ project.date }}</span></h4>
                <div class="clearfix">
                  <div v-html="project.description"></div>
                </div>
              </li>
            </ul>
          </li>
        </ul>
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
  data() {
    return {
      gender: '男',
      age: '',
      grade: '',
      school: '',
      major: '',
      englishLevel: '',
      github: '',
      contactList: [],
      techList: [],
      skillList: [],
      workList: [],
      projectList: []
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
    initData() {
      // 基本信息
      this.age = this.resume.workYears ? this.resume.workYears + '年经验' : ''
      this.school = this.resume.educations?.[0]?.school || ''
      this.major = this.resume.educations?.[0]?.major || ''
      this.github = ''

      // 联系方式
      this.contactList = [
        { icon: 'fa-phone', name: '电话', value: this.resume.phone || '', link: `tel:${this.resume.phone}` },
        { icon: 'fa-envelope', name: 'Email', value: this.resume.email || '', link: `mailto:${this.resume.email}` }
      ]
      if (this.resume.location) {
        this.contactList.push({ icon: 'fa-map-marker', name: '地址', value: this.resume.location, link: '#' })
      }

      // 技能点
      this.techList = (this.resume.skills || []).map(skill => ({
        name: skill.skillName,
        percentage: (skill.level / 5) * 100 + '%'
      }))

      // 技能清单
      this.skillList = (this.resume.skills || []).map(skill => ({
        des: skill.skillName + (skill.description ? '：' + skill.description : '')
      }))

      // 工作经历
      this.workList = (this.resume.workExperiences || []).map(work => ({
        company: work.company,
        date: work.startDate ? `${work.startDate} - ${work.endDate || '至今'}` : '',
        desc: work.responsibility || work.achievement || ''
      }))

      // 项目经验
      this.projectList = (this.resume.projectExperiences || []).map(project => ({
        name: project.projectName,
        date: project.startDate ? `${project.startDate} - ${project.endDate || '至今'}` : '',
        description: project.description || ''
      }))
    }
  }
}
</script>

<style scoped>
.resume-template-it {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
  background: #f5f5f5;
  padding: 20px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  border-radius: 8px;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 35%;
  background: #2c3e50;
  color: white;
  padding: 30px 20px;
}

.sidebar .title h1 {
  font-size: 24px;
  margin: 0 0 10px 0;
  color: white;
}

.sidebar .title h2 {
  font-size: 16px;
  margin: 0 0 20px 0;
  color: #3498db;
  font-weight: normal;
}

.side-info {
  list-style: none;
  padding: 0;
  margin: 0;
}

.side-info li {
  margin-bottom: 20px;
}

.side-info dt {
  font-weight: bold;
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.2);
  padding-bottom: 5px;
  font-size: 14px;
}

.side-info dt i {
  margin-right: 8px;
}

.side-info dd {
  margin: 0 0 8px 0;
  font-size: 13px;
  line-height: 1.6;
}

.side-info dd span {
  font-weight: bold;
}

.side-info a {
  color: #3498db;
  text-decoration: none;
}

.side-info a:hover {
  text-decoration: underline;
}

/* 技能条 */
.skill-name {
  margin-bottom: 5px;
  font-size: 13px;
}

.skill-bar-wrap {
  background: rgba(255,255,255,0.2);
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 10px;
}

.skill-bar {
  height: 6px;
  background: #3498db;
  border-radius: 10px;
}

/* 主内容 */
.main {
  width: 65%;
  padding: 30px 25px;
  background: white;
}

.main-info {
  list-style: none;
  padding: 0;
  margin: 0;
}

.main-info dt {
  font-weight: bold;
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 15px;
  padding-bottom: 5px;
  border-bottom: 2px solid #3498db;
}

.main-info dt i {
  margin-right: 8px;
  color: #3498db;
}

.exp {
  list-style: none;
  padding: 0;
  margin: 0 0 20px 0;
}

.exp li {
  margin-bottom: 20px;
}

.exp h4 {
  font-size: 15px;
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.exp h4 span {
  font-size: 12px;
  color: #999;
  font-weight: normal;
  margin-left: 10px;
}

.exp p {
  font-size: 13px;
  line-height: 1.6;
  margin: 0;
  color: #555;
}

.circle {
  width: 6px;
  height: 6px;
  background: #3498db;
  border-radius: 50%;
  display: inline-block;
  margin-right: 8px;
}

.clearfix {
  overflow: hidden;
}

.clearfix img {
  max-width: 100%;
  margin-top: 10px;
}

/* Font Awesome 图标 */
.fa {
  font-family: 'FontAwesome';
  font-style: normal;
}

.fa-bookmark:before {
  content: "★";
}

.fa-phone:before {
  content: "📞";
}

.fa-envelope:before {
  content: "✉";
}

.fa-map-marker:before {
  content: "📍";
}

@media print {
  .resume-template-it {
    padding: 0;
    background: white;
  }
  .container {
    box-shadow: none;
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }
  .sidebar, .main {
    width: 100%;
  }
}
</style>