import request from '@/utils/request'

const API_PREFIX = '/teacher'

// 教师信息
export const getTeacherProfile = () => request.get(`${API_PREFIX}/profile`)
export const saveTeacherProfile = (data) => request.post(`${API_PREFIX}/profile`, data)

// 工作台
export const getTeacherDashboard = () => request.get(`${API_PREFIX}/dashboard`)
export const getEmploymentStats = () => request.get(`${API_PREFIX}/employment-stats`)

// 学生管理
export const queryStudents = (data) => request.post(`${API_PREFIX}/students/query`, data)

// 就业管理
export const getEmploymentList = () => request.get(`${API_PREFIX}/employment`)
export const auditEmployment = (data) => request.post(`${API_PREFIX}/employment/audit`, data)

// 企业管理
export const getCompanies = (verifyStatus) => 
  request.get(`${API_PREFIX}/companies`, { params: { verifyStatus } })
export const auditCompany = (companyId, verifyStatus, remark) =>
  request.post(`${API_PREFIX}/companies/${companyId}/audit`, null, {
    params: { verifyStatus, remark }
  })

// 职位管理
export const getJobs = (verifyStatus) =>
  request.get(`${API_PREFIX}/jobs`, { params: { verifyStatus } })
export const auditJob = (jobId, verifyStatus, remark) =>
  request.post(`${API_PREFIX}/jobs/${jobId}/audit`, null, {
    params: { verifyStatus, remark }
  })

// 帮扶管理
export const getAssistanceRecords = () => request.get(`${API_PREFIX}/assistance`)
export const saveAssistanceRecord = (data) => request.post(`${API_PREFIX}/assistance`, data)

// 活动管理
export const createActivity = (data) => request.post(`${API_PREFIX}/activities`, data)
export const getActivities = () => request.get(`${API_PREFIX}/activities`)
export const getActivityById = (activityId) => request.get(`${API_PREFIX}/activities/${activityId}`)

// 校企合作
export const getCooperationApplications = () => request.get(`${API_PREFIX}/cooperation-applications`)
export const auditCooperationApplication = (applicationId, status, comment) =>
  request.post(`${API_PREFIX}/cooperation-applications/${applicationId}/audit`, null, {
    params: { status, comment }
  })
