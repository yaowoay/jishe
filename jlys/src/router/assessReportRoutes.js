// 面试评估报告相关路由配置

export const assessReportRoutes = [
  {
    path: '/assess-report',
    name: 'AssessReport',
    component: () => import('@/components/functions/AiInterview/AssessReport/AssessReportView.vue'),
    meta: {
      title: '面试评估报告 V1',
      requiresAuth: true
    }
  },
  {
    path: '/assess-report-v2',
    name: 'AssessReportV2',
    component: () => import('@/components/functions/AiInterview/AssessReport/AssessReportView2.vue'),
    meta: {
      title: '面试评估报告 V2',
      requiresAuth: true
    }
  },
  {
    path: '/test-assess-report',
    name: 'TestAssessReport',
    component: () => import('@/views/TestAssessReport.vue'),
    meta: {
      title: '测试评估报告 V1',
      requiresAuth: false
    }
  },
  {
    path: '/test-assess-report-v2',
    name: 'TestAssessReportV2',
    component: () => import('@/views/TestAssessReport2.vue'),
    meta: {
      title: '测试评估报告 V2',
      requiresAuth: false
    }
  },
  // 模拟的课程详情页面路由
  {
    path: '/courses/:courseName',
    name: 'CourseDetail',
    component: () => import('@/views/CourseDetail.vue'),
    meta: {
      title: '课程详情',
      requiresAuth: false
    }
  },
  // 模拟的岗位详情页面路由
  {
    path: '/positions/:positionName',
    name: 'PositionDetail',
    component: () => import('@/views/PositionDetail.vue'),
    meta: {
      title: '岗位详情',
      requiresAuth: false
    }
  },
  // 模拟的资源页面路由
  {
    path: '/resources/:resourceTitle',
    name: 'ResourceDetail',
    component: () => import('@/views/ResourceDetail.vue'),
    meta: {
      title: '学习资源',
      requiresAuth: false
    }
  },
  // 模拟的项目页面路由
  {
    path: '/projects/:projectName',
    name: 'ProjectDetail',
    component: () => import('@/views/ProjectDetail.vue'),
    meta: {
      title: '项目详情',
      requiresAuth: false
    }
  }
]

export default assessReportRoutes
