// 用户角色
export const USER_ROLES = {
  APPLICANT: 'applicant',
  COMPANY: 'company'
}

// 用户角色显示名称
export const USER_ROLE_NAMES = {
  [USER_ROLES.APPLICANT]: '求职者',
  [USER_ROLES.COMPANY]: '企业用户'
}

// 申请状态 (使用英文状态值与后端保持一致)
export const APPLICATION_STATUS = {
  PENDING: 'pending',
  REJECTED: 'rejected',
  HIRED: 'hired',
  REJECT_HIRE: 'reject_hire',
  INTERVIEW: 'interview',
  COMPLETED: 'completed',
  ACCEPTED: 'accepted',
  INT_REJECTED: 'int_rejected'
}

// 申请状态显示名称
export const APPLICATION_STATUS_NAMES = {
  [APPLICATION_STATUS.PENDING]: '已投递',
  [APPLICATION_STATUS.REJECTED]: '拒绝申请',
  [APPLICATION_STATUS.HIRED]: '已录用',
  [APPLICATION_STATUS.REJECT_HIRE]: '拒绝录用',
  [APPLICATION_STATUS.INTERVIEW]: '待面试',
  [APPLICATION_STATUS.COMPLETED]: '待定',
  [APPLICATION_STATUS.ACCEPTED]: '接受面试',
  [APPLICATION_STATUS.INT_REJECTED]: '拒绝面试'
}

// 申请状态标签类型
export const APPLICATION_STATUS_TYPES = {
  [APPLICATION_STATUS.PENDING]: 'info',
  [APPLICATION_STATUS.REJECTED]: 'danger',
  [APPLICATION_STATUS.HIRED]: 'success',
  [APPLICATION_STATUS.REJECT_HIRE]: 'warning',
  [APPLICATION_STATUS.INTERVIEW]: 'primary',
  [APPLICATION_STATUS.COMPLETED]: 'warning',
  [APPLICATION_STATUS.ACCEPTED]: 'success',
  [APPLICATION_STATUS.INT_REJECTED]: 'danger'
}

// 教育水平
export const EDUCATION_LEVELS = [
  '高中',
  '大专',
  '本科',
  '硕士',
  '博士'
]

// 性别选项
export const GENDER_OPTIONS = [
  { label: '男', value: 'male' },
  { label: '女', value: 'female' },
  { label: '其他', value: 'other' }
]

// 公司规模
export const COMPANY_SCALES = [
  '1-50人',
  '51-100人',
  '101-500人',
  '500人以上'
]

// 职位类型
export const JOB_TYPES = [
  '全职',
  '兼职',
  '实习'
]

// 文件类型
export const FILE_TYPES = {
  IMAGE: ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'],
  DOCUMENT: ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt'],
  RESUME: ['pdf', 'doc', 'docx']
}

// 文件大小限制（字节）
export const FILE_SIZE_LIMITS = {
  IMAGE: 5 * 1024 * 1024, // 5MB
  DOCUMENT: 10 * 1024 * 1024, // 10MB
  RESUME: 10 * 1024 * 1024 // 10MB
}

// API响应状态码
export const API_CODES = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500
}

// 本地存储键名
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_ROLE: 'userRole',
  USER_ID: 'userId',
  USER_INFO: 'userInfo'
}

// 路由路径
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  APPLICANT: {
    DASHBOARD: '/applicant/dashboard',
    PROFILE: '/applicant/profile',
    RESUME: '/applicant/resume',
    INTERVIEW: '/applicant/interview',
    REPORTS: '/applicant/reports'
  },
  COMPANY: {
    DASHBOARD: '/company/dashboard',
    PROFILE: '/company/profile',
    JOBS: '/company/jobs',
    APPLICANTS: '/company/applicants',
    INTERVIEWS: '/company/interviews'
  }
}

// 面试评分维度
export const INTERVIEW_DIMENSIONS = {
  TECHNICAL_ABILITY: 'technical_ability',
  LEARNING_ABILITY: 'learning_ability',
  TEAM_COLLABORATION: 'team_collaboration',
  PROBLEM_SOLVING: 'problem_solving',
  COMMUNICATION_EXPRESSION: 'communication_expression'
}

// 面试评分维度显示名称
export const INTERVIEW_DIMENSION_NAMES = {
  [INTERVIEW_DIMENSIONS.TECHNICAL_ABILITY]: '技术能力',
  [INTERVIEW_DIMENSIONS.LEARNING_ABILITY]: '学习能力',
  [INTERVIEW_DIMENSIONS.TEAM_COLLABORATION]: '团队协作',
  [INTERVIEW_DIMENSIONS.PROBLEM_SOLVING]: '问题解决',
  [INTERVIEW_DIMENSIONS.COMMUNICATION_EXPRESSION]: '沟通表达'
}

// 分页配置
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZES: [10, 20, 50, 100]
}

// 时间格式
export const DATE_FORMATS = {
  DATE: 'YYYY-MM-DD',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  TIME: 'HH:mm:ss'
}

// 正则表达式
export const REGEX = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PHONE: /^1[3-9]\d{9}$/,
  PASSWORD: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/
}
