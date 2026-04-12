<template>
  <div class="resume-preview" :class="{ 'mobile-view': isMobile }">
    <!-- 使用动态模板渲染器 -->
    <ResumeTemplateRenderer 
      v-if="!isEmpty"
      :resume="convertedResumeData" 
      :template="template || 'template1'"
    />
    
    <!-- 空状态提示 -->
    <div v-else class="empty-state">
      <el-empty description="请在左侧填写简历信息">
        <el-button type="primary" @click="$emit('start-editing')">开始编辑</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import ResumeTemplateRenderer from './ResumeTemplateRenderer.vue'
import { Phone, Message, Location, Star } from '@element-plus/icons-vue'

export default {
  name: 'ResumePreview',
  components: {
    ResumeTemplateRenderer,
    Phone,
    Message,
    Location,
    Star
  },
  props: {
    resumeData: {
      type: Object,
      default: () => ({})
    },
    template: {
      type: String,
      default: 'template1'
    },
    isMobile: {
      type: Boolean,
      default: false
    }
  },
  emits: ['start-editing'],
  setup(props) {
    // 转换数据结构，将前端的resumeForm结构转换为后端ResumeResponse结构
    const convertedResumeData = computed(() => {
      const data = props.resumeData
      
      // 如果数据为空，返回默认结构
      if (!data || Object.keys(data).length === 0) {
        return {
          fullName: '姓名',
          position: '期望职位',
          phone: '',
          email: '',
          location: '',
          profile: '',
          workExperiences: [],
          educations: [],
          projectExperiences: [],
          skills: [],
          additionalInfos: []
        }
      }
      
      // 如果是后端返回的数据结构（已经有fullName等字段），直接返回
      if (data.fullName || (data.id && !data.basicInfo)) {
        return {
          id: data.id,
          name: data.name,
          fullName: data.fullName || '',
          phone: data.phone || '',
          email: data.email || '',
          position: data.position || '',
          workYears: data.workYears || 0,
          location: data.location || '',
          profile: data.profile || '',
          template: data.template || 'template1',
          workExperiences: data.workExperiences || [],
          educations: data.educations || [],
          projectExperiences: data.projectExperiences || [],
          skills: data.skills || [],
          additionalInfos: data.additionalInfos || []
        }
      }
      
      // 如果是前端resumeForm结构，需要转换
      return {
        id: data.id,
        name: data.title || data.name,
        fullName: data.basicInfo?.name || '',
        phone: data.basicInfo?.phone || '',
        email: data.basicInfo?.email || '',
        position: data.basicInfo?.position || '',
        workYears: data.basicInfo?.workYears || 0,
        location: data.basicInfo?.address || '',
        profile: data.basicInfo?.summary || '',
        template: data.template || 'template1',
        // 工作经历
        workExperiences: (data.workExperience || []).map(work => ({
          id: work.id,
          company: work.company || '',
          position: work.position || '',
          startDate: work.startDate || '',
          endDate: work.endDate || '',
          responsibility: work.responsibilities || '',
          achievement: work.achievements || ''
        })),
        // 教育经历
        educations: (data.education || []).map(edu => ({
          id: edu.id,
          school: edu.school || '',
          major: edu.major || '',
          degree: edu.degree || '',
          startDate: edu.startDate || '',
          endDate: edu.endDate || '',
          description: edu.description || ''
        })),
        // 项目经历
        projectExperiences: (data.projects || []).map(project => ({
          id: project.id,
          projectName: project.name || '',
          role: project.role || '',
          startDate: project.startDate || '',
          endDate: project.endDate || '',
          description: project.description || ''
        })),
        // 技能
        skills: (data.skills || []).map(skill => ({
          id: skill.id,
          skillName: skill.name || '',
          level: skill.level || skill.proficiency || 1,
          description: skill.description || ''
        })),
        // 其他信息
        additionalInfos: [
          ...(data.others?.certificates || []).map(cert => ({
            id: cert.id,
            name: cert.name || '',
            time: cert.date || '',
            description: cert.issuer || ''
          })),
          ...(data.others?.awards || []).map(award => ({
            id: award.id,
            name: award.name || '',
            time: award.date || '',
            description: award.level || ''
          })),
          ...(data.others?.hobbies || []).map(hobby => ({
            id: hobby.id,
            name: hobby || '',
            time: '',
            description: ''
          }))
        ]
      }
    })

    // 检查是否为空状态
    const isEmpty = computed(() => {
      const data = props.resumeData
      if (!data || Object.keys(data).length === 0) return true
      
      // 如果是后端数据结构
      if (data.fullName || (data.id && !data.basicInfo)) {
        return !data.fullName && 
               !data.profile &&
               (!data.workExperiences || data.workExperiences.length === 0) &&
               (!data.educations || data.educations.length === 0) &&
               (!data.skills || data.skills.length === 0) &&
               (!data.projectExperiences || data.projectExperiences.length === 0)
      }
      
      // 如果是前端数据结构
      return !data.basicInfo?.name && 
             !data.basicInfo?.summary &&
             (!data.workExperience || data.workExperience.length === 0) &&
             (!data.education || data.education.length === 0) &&
             (!data.skills || data.skills.length === 0) &&
             (!data.projects || data.projects.length === 0) &&
             !hasOtherInfo.value
    })

    // 检查是否有其他信息
    const hasOtherInfo = computed(() => {
      const others = props.resumeData.others
      return others && (
        (others.certificates && others.certificates.length > 0) ||
        (others.awards && others.awards.length > 0) ||
        (others.hobbies && others.hobbies.length > 0)
      )
    })

    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      return dateStr.replace('-', '/')
    }

    return {
      convertedResumeData,
      isEmpty,
      hasOtherInfo,
      formatDate
    }
  }
}
</script>

<style scoped>
/* 迁移自frontend，将scss语法改为css - 简历预览组件样式 */
.resume-preview {
  width: 100%;
  background: #f5f5f5;
  padding: 20px;
  overflow: hidden;
}

/* 隐藏滚动条 */
::-webkit-scrollbar {
  display: none;
}

.resume-preview.mobile-view {
  padding: 10px;
}

.resume-preview.mobile-view .resume-paper {
  transform: scale(0.8);
  transform-origin: top center;
}

.resume-paper {
  width: 210mm;
  min-height: 297mm;
  background: white;
  margin: 0 auto;
  padding: 20mm;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  font-family: 'Microsoft YaHei', sans-serif;
  line-height: 1.6;
}

.resume-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #409eff;
}

.resume-header .header-left {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  flex: 1;
}

.resume-header .avatar-section .avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.resume-header .basic-info {
  flex: 1;
}

.resume-header .basic-info .name {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
}

.resume-header .basic-info .position {
  font-size: 16px;
  color: #409eff;
  margin-bottom: 12px;
  font-weight: 500;
}

.resume-header .basic-info .contact-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.resume-header .basic-info .contact-info .contact-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
}

.resume-header .basic-info .contact-info .contact-item .el-icon {
  color: #409eff;
}

.resume-section {
  margin-bottom: 25px;
}

.resume-section .section-title {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  padding-bottom: 8px;
  border-bottom: 1px solid #e0e0e0;
}

.resume-section .section-content .summary {
  margin: 0;
  color: #555;
  line-height: 1.8;
}

.work-item,
.education-item,
.project-item {
  margin-bottom: 20px;
}

.work-item:last-child,
.education-item:last-child,
.project-item:last-child {
  margin-bottom: 0;
}

.work-header,
.education-header,
.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.work-title,
.education-title,
.project-title {
  flex: 1;
}

.work-title h3,
.education-title h3,
.project-title h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.work-title .company,
.education-title .major,
.project-title .role {
  font-size: 14px;
  color: #666;
}

.work-period,
.education-period,
.project-period {
  font-size: 14px;
  color: #999;
  white-space: nowrap;
}

.work-description,
.education-description,
.project-description,
.project-tech {
  color: #555;
  margin-top: 8px;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.skills-grid .skill-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.skills-grid .skill-item .skill-name {
  font-weight: 500;
  color: #2c3e50;
}

.skills-grid .skill-item .skill-level .skill-stars {
  display: flex;
  gap: 2px;
}

.skills-grid .skill-item .skill-level .skill-stars .el-icon {
  font-size: 14px;
  color: #ddd;
}

.skills-grid .skill-item .skill-level .skill-stars .el-icon.filled {
  color: #ffd700;
}

.other-subsection {
  margin-bottom: 16px;
}

.other-subsection h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.certificates-list .cert-item,
.awards-list .award-item {
  margin-bottom: 4px;
  color: #555;
}

.certificates-list .cert-item .date,
.awards-list .award-item .date {
  color: #999;
  font-size: 12px;
  margin-left: 8px;
}

.hobbies-list {
  color: #555;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

@media print {
  .resume-preview {
    background: white;
    padding: 0;
  }

  .resume-paper {
    box-shadow: none;
    margin: 0;
    padding: 15mm;
  }
}

.resume-section, .section-title, .resume-header, .resume-paper {
  margin-top: 0 !important;
  padding-top: 0 !important;
  margin-bottom: 0 !important;
}
</style>
