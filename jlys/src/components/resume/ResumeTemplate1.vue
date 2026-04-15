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
            <dd><span>专业：</span>{{ profession }}</dd>
            <dd v-if="englishLevel"><span>英语水平:</span> {{ englishLevel }}</dd>
            <dd v-if="nickname"><span>常用ID:</span> {{ nickname }}</dd>
            <dd v-if="blog"><span>Blog:</span> <a :href="blog" target="_blank">{{ blog }}</a></dd>
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

          <!-- 奖项荣誉 -->
          <li v-if="honorList.length" class="someRight">
            <dt><i class="fa fa-bookmark"></i>Honor. 奖项荣誉</dt>
            <dd v-for="(item, idx) in honorList" :key="idx">
              <ul>
                <li>{{ item.date }} {{ item.name }}</li>
              </ul>
            </dd>
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

        <!-- 个人简介 -->
        <div v-if="resume.profile" class="note">
          {{ resume.profile }}
        </div>
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

          <!-- 项目经验 -->
          <li class="experience">
            <dt><i class="fa fa-bookmark"></i>Experience. 项目经验</dt>

            <template v-for="(exp, idx) in experienceList" :key="idx">
              <h2 class="exp-title">{{ exp.section }}</h2>

              <!-- 个人项目 -->
              <template v-if="exp.section === '个人项目'">
                <ul class="exp">
                  <li v-for="(project, pIdx) in exp.projects" :key="pIdx">
                    <div class="circle"></div>
                    <h4>
                      {{ project.name }} {{ project.date }}
                      <a v-if="project.website" :href="project.website" target="_blank">
                        <i class="fa fa-link"></i>Demo
                      </a>
                      <a v-if="project.sourceCode" :href="project.sourceCode" target="_blank">
                        <i class="fa fa-link"></i>SourceCode
                      </a>
                    </h4>
                    <div class="clearfix">
                      <img v-if="project.img" :src="project.img" />
                      <div v-html="project.des"></div>
                    </div>
                  </li>
                </ul>
              </template>

              <!-- 工作经验 -->
              <template v-else>
                <ul class="exp">
                  <li v-for="(work, wIdx) in exp.experiences" :key="wIdx">
                    <h4>{{ work.company }} <span>{{ work.date }}</span></h4>
                    <p v-html="work.desc"></p>
                    <ul>
                      <li v-for="(project, pIdx) in work.projects" :key="pIdx">
                        <div class="circle"></div>
                        <h4>
                          {{ project.name }} {{ project.date }}
                          <a v-if="project.website" :href="project.website" target="_blank">
                            <i class="fa fa-link"></i>Demo
                          </a>
                          <a v-if="project.sourceCode" :href="project.sourceCode" target="_blank">
                            <i class="fa fa-link"></i>SourceCode
                          </a>
                        </h4>
                        <div class="clearfix">
                          <img v-if="project.img" :src="project.img" />
                          <div v-html="project.des"></div>
                        </div>
                      </li>
                    </ul>
                  </li>
                </ul>
              </template>
            </template>
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
      profession: '',
      englishLevel: '',
      nickname: '',
      blog: '',
      github: '',
      contactList: [],
      honorList: [],
      techList: [],
      skillList: [],
      experienceList: []
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
      // 从 resume 中提取数据
      const basic = this.resume.basicInfo || {}

      // 基本信息
      this.age = basic.workYears ? basic.workYears + '岁' : ''
      this.school = (this.resume.educations?.[0]?.school) || ''
      this.profession = (this.resume.educations?.[0]?.major) || ''

      // 联系方式
      this.contactList = [
        { icon: 'fa-phone', name: '电话', value: basic.phone || '', link: `tel:${basic.phone}` },
        { icon: 'fa-envelope', name: 'Email', value: basic.email || '', link: `mailto:${basic.email}` }
      ]

      // 技能点
      this.techList = (this.resume.skills || []).map(skill => ({
        name: skill.skillName,
        percentage: (skill.level / 5) * 100 + '%'
      }))

      // 技能清单
      this.skillList = (this.resume.skills || []).map(skill => ({
        des: skill.skillName + '：' + (skill.description || '')
      }))

      // 项目经验
      const projects = (this.resume.projectExperiences || []).map(proj => ({
        name: proj.projectName,
        date: proj.startDate ? `${proj.startDate} - ${proj.endDate || '至今'}` : '',
        des: proj.description,
        website: '',
        sourceCode: ''
      }))

      this.experienceList = [
        {
          section: '项目经验',
          projects: projects
        }
      ]
    }
  }
}
</script>

<style scoped>
/* 将原来 index.html 的 CSS 复制到这里 */
.resume-template-it {
  font-family: 'Microsoft YaHei', sans-serif;
  background: #f5f5f5;
  padding: 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.sidebar {
  width: 35%;
  background: #2c3e50;
  color: white;
  padding: 20px;
}

.main {
  width: 65%;
  padding: 20px;
}

.skill-bar-wrap {
  background: rgba(255,255,255,0.2);
  border-radius: 10px;
  overflow: hidden;
  margin-top: 5px;
}

.skill-bar {
  height: 8px;
  background: #3498db;
  border-radius: 10px;
}

.fa {
  margin-right: 8px;
}

@media print {
  .resume-template-it {
    padding: 0;
  }
  .container {
    box-shadow: none;
  }
}
</style>