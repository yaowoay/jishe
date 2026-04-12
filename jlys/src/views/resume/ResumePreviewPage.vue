<template>
  <div class="resume-preview-page">
    <div ref="pdfContent">
      <ResumePreview v-if="resume" :resumeData="resume" />
    </div>
    <div class="actions" style="margin-top: 32px; text-align: center;">
      <el-button type="primary" @click="editResume">编辑</el-button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getResume } from '@/api/resume'
import ResumePreview from '@/components/resume/ResumePreview.vue'
// 需要先安装 html2pdf.js: npm install html2pdf.js
import html2pdf from 'html2pdf.js'

export default {
  name: 'ResumePreviewPage',
  components: { ResumePreview },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const resume = ref(null)
    const pdfContent = ref(null)

    onMounted(async () => {
      const id = route.params.id
      const res = await getResume(id)
      if (res.code === 0) {
        resume.value = mapResumeData(res.data)
        await nextTick()
        // 检查是否需要自动下载PDF
        if (route.query.download === '1') {
          exportPDF()
        }
      }
    })

    const editResume = () => {
      router.push(`/layout/editResume/editor/${route.params.id}`)
    }

    // 导出PDF方法
    const exportPDF = () => {
      const element = pdfContent.value
      if (!element) return
      html2pdf()
        .set({
          margin: 0,
          filename: 'resume.pdf',
          html2canvas: { scale: 2 },
          jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
        })
        .from(element)
        .save()
    }

    // 字段映射函数
    function mapResumeData(data) {
      // 1. basicInfo
      const basicInfo = {
        name: data.name,
        phone: data.phone,
        email: data.email,
        position: data.position,
        summary: data.profile,
        address: data.location,
        avatar: data.avatar || ''
      }

      // 2. workExperience
      const workExperience = (data.workExperiences || []).map(item => ({
        company: item.company,
        position: item.position,
        startDate: item.startDate,
        endDate: item.endDate,
        isCurrent: item.isCurrent || false,
        responsibilities: item.responsibility || '',
        achievements: item.achievement || ''
      }))

      // 3. education
      const education = (data.educations || []).map(item => ({
        school: item.school,
        major: item.major,
        degree: item.degree,
        startDate: item.startDate,
        endDate: item.endDate,
        description: item.description || ''
      }))

      // 4. projects
      const projects = (data.projectExperiences || []).map(item => ({
        name: item.projectName || '',
        role: item.role || '',
        startDate: item.startDate,
        endDate: item.endDate,
        description: item.description || '',
        technologies: item.technologies || ''
      }))

      // 5. skills
      const skills = (data.skills || []).map(item => ({
        name: item.skillName,
        level: item.proficiency || 0,
        description: item.description || ''
      }))

      // 6. others
      const others = {
        certificates: [],
        awards: [],
        hobbies: []
      }
      ;(data.additionalInfos || []).forEach(item => {
        if (item.type === 'certificate') {
          others.certificates.push({
            name: item.name,
            issuer: item.issuer || '',
            date: item.startDate || '',
            description: item.description || ''
          })
        } else if (item.type === 'award') {
          others.awards.push({
            name: item.name,
            level: item.level || '',
            date: item.startDate || '',
            description: item.description || ''
          })
        } else if (item.type === 'hobby') {
          others.hobbies.push(item.name)
        }
      })

      return {
        basicInfo,
        workExperience,
        education,
        projects,
        skills,
        others
      }
    }

    return { resume, editResume, pdfContent }
  }
}
</script>

<style scoped>
.resume-preview-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
</style> 