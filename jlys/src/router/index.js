import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/student/profile-complete',
    name: 'StudentProfileComplete',
    component: () => import('@/views/student/ProfileComplete.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/company/profile-complete',
    name: 'CompanyProfileComplete',
    component: () => import('@/views/company/ProfileComplete.vue'),
    meta: { requiresAuth: true, role: 'company' }
  },
  {
    path: '/teacher/profile-complete',
    name: 'TeacherProfileComplete',
    component: () => import('@/views/teacher/ProfileComplete.vue'),
    meta: { requiresAuth: true, role: 'teacher' }
  },
  /*{
    path: '/interview/:id?',
    name: 'Interview',
    component: () => import('@/views/Interview.vue'),
    meta: { requiresAuth: true }
  },*/
  // 笔试考试路由
  {
    path: '/exam/written-test/:applicationId',
    name: 'WrittenTest',
    component: () => import('@/views/exam/WrittenTest.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/exam/result/:applicationId',
    name: 'WrittenTestResult',
    component: () => import('@/views/exam/WrittenTestResult.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/applicant',
    name: 'ApplicantLayout',
    component: () => import('@/views/layouts/ApplicantLayout.vue'),
    meta: { requiresAuth: true, role: ['applicant', 'student'] },
    children: [
      {
        path: 'ApplicationVisual',
        name: 'ApplicationVisual',
        component: () => import('@/views/visualScreen/ApplicationVisual.vue')
      },
      {
        path: 'dashboard',
        name: 'ApplicantDashboard',
        component: () => import('@/views/applicant/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'ApplicantProfile',
        component: () => import('@/views/applicant/Profile.vue')
      },
      {
        path: 'profile/edit',
        name: 'ApplicantProfileEdit',
        component: () => import('@/components/forms/ApplicantProfileForm.vue')
      },
      {
        path: 'professional-test',
        name: 'ProfessionalTest',
        component: () => import('@/views/applicant/ProfessionTest/ProfessionalTest.vue')
      },
      {
        path: 'professional-test/exam/:category/:categoryName',
        name: 'ProfessionalTestExam',
        component: () => import('@/views/applicant/ProfessionTest/ProfessionalTestExam.vue')
      },
      {
        path: 'professional-test/result/:testResultId?',
        name: 'TestResult',
        component: () => import('@/views/applicant/ProfessionTest/TestResult.vue')
      },
      {
        path: 'personality-test',
        name: 'PersonalityTest',
        component: () => import('@/views/applicant/disctest/PersonalityTest.vue')
      },
      {
        path: 'disc-test',
        name: 'DiscTest',
        component: () => import('@/components/test/DiscTest.vue')
      },
      {
        path: 'personality-test-execution/:testId',
        name: 'PersonalityTestExecution',
        component: () => import('@/views/applicant/disctest/PersonalityTestExecution.vue')
      },
      {
        path: 'personality-report/:recordId',
        name: 'PersonalityReport',
        component: () => import('@/views/applicant/disctest/PersonalityReport.vue')
      },
      // {
      //   path: 'multimodal-interview',
      //   name: 'MultimodalInterview',
      //   component: () => import('@/views/applicant/MultimodalInterview.vue')
      // },
      // 简历管理模块
      {
        path: 'resume',
        name: 'ResumeModule',
        redirect: 'resume/list',
        meta: { title: '简历管理', icon: 'el-icon-document' },
        children: [
          {
            path: 'list',
            name: 'ResumeList',
            component: () => import('@/views/resume/ResumeList.vue'),
            meta: { title: '我的简历', icon: 'el-icon-document' }
          },
          {
            path: 'editor/:id?',
            name: 'ResumeEditor',
            component: () => import('@/views/resume/ResumeEditorEnhanced.vue'),
            meta: { title: '简历编辑器', icon: 'el-icon-edit' }
          },
          {
            path: 'generation',
            name: 'ResumeGeneration',
            component: () => import('@/views/applicant/resume/ResumeGenerator.vue'),
            meta: { title: 'AI生成简历', icon: 'el-icon-magic-stick' }
          },
          {
            path: 'analysis',
            name: 'ResumeAnalysis',
            component: () => import('@/views/applicant/resume/ResumeAnalysis.vue'),
            meta: { title: '简历分析', icon: 'el-icon-data-analysis' }
          },
          {
            path: 'match-analysis',
            name: 'ResumeMatchAnalysis',
            component: () => import('@/views/applicant/resume/ResumeMatchAnalysis.vue'),
            meta: { title: '简历匹配分析', icon: 'el-icon-data-analysis' }
          },
          {
            path: 'submit',
            name: 'ResumeSubmit',
            component: () => import('@/views/applicant/resume/ResumeSubmit.vue'),
            meta: { title: '投递简历', icon: 'el-icon-s-promotion' }
          },
          {
            path: 'share/:shareUrl',
            name: 'ResumeShare',
            component: () => import('@/views/resume/ResumeShare.vue'),
            meta: { title: '简历分享', icon: 'el-icon-share' }
          }
        ]
      },
      // 面试模块
      {
        path: 'interview',
        name: 'ApplicantInterview',
        component: () => import('@/views/applicant/simulatInterview/Interview.vue'),
        meta: { title: '模拟面试', icon: 'el-icon-chat-dot-round' }
      },
      // {
      //   path: 'simulate-exam',
      //   name: 'SimulateExam',
      //   component: () => import('@/views/applicant/simulatInterview/SimulatExam.vue')
      // },
      // {
      //   path: 'interview-invitations',
      //   name: 'InterviewInvitations',
      //   component: () => import('@/views/applicant/InterviewInvitations.vue')
      // },
      {
        path: 'interview-center',
        name: 'InterviewCenter',
        component: () => import('@/views/applicant/InterviewCenter.vue')
      },
      {
        path: 'notes',
        name: 'ApplicantNotes',
        component: () => import('@/components/Notes/NoteBooks.vue')
      },
      // 数据可视化模块
      {
        path: 'salary-analysis',
        name: 'SalaryAnalysis',
        component: () => import('@/views/dataAnalysis/SalaryDistribution.vue'),
        meta: { title: '薪资维度分析', icon: 'el-icon-trend-charts' }
      },
      {
        path: 'position-analysis',
        name: 'PositionAnalysis',
        component: () => import('@/views/dataAnalysis/EduRequirement.vue'),
        meta: { title: '岗位维度分析', icon: 'el-icon-position' }
      },
      {
        path: 'company-analysis',
        name: 'CompanyAnalysis',
        component: () => import('@/views/dataAnalysis/CompanyAnalysis.vue'),
        meta: { title: '招聘企业分析', icon: 'el-icon-office-building' }
      },
      {
        path: 'skill-rules',
        name: 'SkillRules',
        component: () => import('@/views/dataAnalysis/SkillAssociationRules.vue'),
        meta: { title: '关联规则分析', icon: 'el-icon-link' }
      },
      // 职位管理模块
      {
        path: 'intelligent-recommend',
        name: 'IntelligentRecommend',
        component: () => import('@/views/jobManagement/IntelligentRecommendationSimple.vue'),
        meta: { title: '智能推荐系统', icon: 'el-icon-magic-stick' }
      },
      {
        path: 'job-list',
        name: 'JobListPage',
        redirect: '/applicant/resume/submit',
        meta: { title: '职位列表', icon: 'el-icon-search' }
      },
      {
        path: 'salary-predict',
        name: 'SalaryPredict',
        component: () => import('@/views/jobManagement/SalaryPrediction.vue'),
        meta: { title: '薪资预测', icon: 'el-icon-trend-charts' }
      },
      // 个人中心模块
      {
        path: 'my-collections',
        name: 'MyCollectionsPage',
        component: () => import('@/views/personalCenter/MyCollections.vue'),
        meta: { title: '我的收藏', icon: 'el-icon-star-on' }
      },
      {
        path: 'behavior-analysis',
        name: 'BehaviorAnalysisPage',
        component: () => import('@/views/personalCenter/UserBehaviorDashboard.vue'),
        meta: { title: '行为分析', icon: 'el-icon-data-analysis' }
      },
      // 可视化大屏
      {
        path: 'visual-screen',
        name: 'VisualScreen',
        component: () => import('@/views/visualScreen/VisualScreen.vue'),
        meta: { title: '可视化大屏', icon: 'el-icon-monitor' }
      }
      // {
      //   path: 'exam-center/:applicationId/:jobId',
      //   name: 'ExamCenter',
      //   component: () => import('@/views/applicant/ExamCenter.vue')
      // },
      // {
      //   path: 'reports',
      //   name: 'ApplicantReports',
      //   component: () => import('@/views/applicant/Reports.vue')
      // }
    ]
  },
  {
    path: '/teacher',
    name: 'TeacherLayout',
    component: () => import('@/views/layouts/TeacherLayout.vue'),
    meta: { requiresAuth: true, role: 'teacher' },
    children: [
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/Dashboard.vue')
      },
      {
        path: 'students',
        name: 'TeacherStudents',
        component: () => import('@/views/teacher/Students.vue')
      },
      {
        path: 'students/warning',
        name: 'TeacherWarningStudents',
        component: () => import('@/views/teacher/WarningStudents.vue')
      },
      {
        path: 'employment',
        name: 'TeacherEmployment',
        component: () => import('@/views/teacher/Employment.vue')
      },
      {
        path: 'employment/stats',
        name: 'TeacherEmploymentStats',
        component: () => import('@/views/teacher/EmploymentStats.vue')
      },
      {
        path: 'companies',
        name: 'TeacherCompanies',
        component: () => import('@/views/teacher/Companies.vue')
      },
      {
        path: 'jobs',
        name: 'TeacherJobs',
        component: () => import('@/views/teacher/Jobs.vue')
      },
      {
        path: 'activities',
        name: 'TeacherActivities',
        component: () => import('@/views/teacher/Activities.vue')
      },
      {
        path: 'activities/create',
        name: 'TeacherActivityCreate',
        component: () => import('@/views/teacher/ActivityCreate.vue')
      },
      {
        path: 'assistance',
        name: 'TeacherAssistance',
        component: () => import('@/views/teacher/Assistance.vue')
      },
      {
        path: 'assistance/create',
        name: 'TeacherAssistanceCreate',
        component: () => import('@/views/teacher/AssistanceCreate.vue')
      },
      {
        path: 'resume-guidance',
        name: 'TeacherResumeGuidance',
        component: () => import('@/views/teacher/ResumeGuidance.vue')
      },
      {
        path: 'mock-interview',
        name: 'TeacherMockInterview',
        component: () => import('@/views/teacher/MockInterview.vue')
      },
      {
        path: 'cooperation',
        name: 'TeacherCooperation',
        component: () => import('@/views/teacher/Cooperation.vue')
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/Profile.vue')
      }
      ,
      {
        path: 'employmentscreen',
        name: 'EmploymentScreen',
        component: () => import('@/views/visualScreen/EmploymentScreen.vue'),
        meta: { title: '可视化大屏', icon: 'el-icon-monitor' }
      }
    ]
  },
  {
    path: '/company',
    name: 'CompanyLayout',
    component: () => import('@/views/layouts/CompanyLayout.vue'),
    meta: { requiresAuth: true, role: 'company' },
    children: [
      {
        path: 'CompanyVisual',
        name: 'CompanyVisual',
        component: () => import('@/views/visualScreen/CompanyVisual.vue')
      },
      {
        path: 'dashboard',
        name: 'CompanyDashboard',
        component: () => import('@/views/company/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'CompanyProfile',
        component: () => import('@/views/company/Profile.vue')
      },
      {
        path: 'jobs',
        name: 'CompanyJobs',
        component: () => import('@/views/company/Jobs.vue')
      },
      {
        path: 'applicants',
        name: 'CompanyApplicants',
        component: () => import('@/views/company/Applicants.vue')
      },
      {
        path: 'applicant-detail/:applicationId',
        name: 'CompanyApplicantDetail',
        component: () => import('@/views/company/ApplicantDetail.vue')
      },
      {
        path: 'interviews',
        name: 'CompanyInterviews',
        component: () => import('@/views/company/Interviews.vue')
      },
      {
        path: 'simulatExam',
        name: 'SimulatExam',
        component: () => import('@/components/interview/SimulatExam.vue')
      },
      {
        path: 'cooperation',
        name: 'CompanyCooperation',
        component: () => import('@/views/company/CooperationEnterprise.vue')
      }
    ]
  },
  // 教师院校端路由 - 移除重复定义
  {
    path: '/debug-auth',
    name: 'DebugAuth',
    component: () => import('@/views/DebugAuth.vue')
  },
  {
    path: '/layout/simulatExam',
    name: 'SimulatExam',
    component: () => import('@/components/interview/SimulatExam.vue')
  },
  {
    path: '/simulatExam',
    name: 'SimulatExamOld',
    component: () => import('@/components/interview/SimulatExam.vue')
  },
  {
    path: '/layout/offecialExam',
    name: 'OfficialExam',
    component: () => import('@/components/interview/OfficialExam.vue')
  },
  {
    path: '/layout/offecialExam/:applicationId?',
    name: 'OfficialExamWithId',
    component: () => import('@/components/interview/OfficialExam.vue')
  },
  {
    path: '/officialExam/:applicationId?',
    name: 'OfficialExamOld',
    component: () => import('@/components/interview/OfficialExam.vue')
  },
  {
    path: '/officialExam',
    name: 'OfficialExamOldNoId',
    component: () => import('@/components/interview/OfficialExam.vue')
  },
  {
    path: '/beforeExam',
    name: 'BeforeExam',
    component: () => import('@/components/interview/BeforeExam.vue')
  },
  {
    path: '/layout/writtenExam',
    name: 'WrittenExam',
    component: () => import('@/components/interview/WrittenExam.vue')
  },
  {
    path: '/interview/report',
    name: 'InterviewReport',
    component: () => import('@/components/interview/InterviewReport.vue')
  },
  {
    path: '/interview-report',
    name: 'InterviewReportNew',
    component: () => import('@/components/interview/InterviewReport.vue')
  }


]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = store.getters.token
  const userRole = store.getters.userRole

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next('/login')
    } else if (to.meta.role) {
      // 支持数组形式的角色检查
      const allowedRoles = Array.isArray(to.meta.role) ? to.meta.role : [to.meta.role]
      if (!allowedRoles.includes(userRole)) {
        // 角色不匹配，重定向到对应角色的首页
        if (userRole === 'student') {
          next('/applicant/dashboard')
        } else if (userRole === 'company') {
          next('/company/dashboard')
        } else if (userRole === 'teacher') {
          next('/teacher/dashboard')
        } else {
          next('/login')
        }
      } else {
        next()
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
