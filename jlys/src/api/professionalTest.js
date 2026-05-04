// import request from '@/utils/request'
//
// // 获取所有题目分类
// export function getCategories() {
//   return request({
//     url: '/professional-test/categories',
//     method: 'get'
//   })
// }
//
// // 获取分类统计信息
// export function getCategoryStatistics(categoryId) {
//   return request({
//     url: `/professional-test/categories/${categoryId}/statistics`,
//     method: 'get'
//   })
// }
//
// // 开始测试
// export function startTest(categoryId, questionCount, difficultyLevel) {
//   return request({
//     url: '/professional-test/start',
//     method: 'post',
//     params: {
//       categoryId,
//       questionCount,
//       difficultyLevel
//     }
//   })
// }
//
// // 获取测试题目
// export function getTestQuestions(recordId) {
//   return request({
//     url: `/professional-test/records/${recordId}/questions`,
//     method: 'get'
//   })
// }
//
// // 提交单个答案
// export function submitAnswer(recordId, questionId, userAnswer, timeSpent) {
//   return request({
//     url: `/professional-test/records/${recordId}/answers`,
//     method: 'post',
//     params: {
//       questionId,
//       userAnswer,
//       timeSpent
//     }
//   })
// }
//
// // 提交整个测试
// export function submitTest(recordId, submission) {
//   return request({
//     url: `/professional-test/records/${recordId}/submit`,
//     method: 'post',
//     data: submission
//   })
// }
//
// // 获取测试结果
// export function getTestResult(recordId) {
//   return request({
//     url: `/professional-test/records/${recordId}/result`,
//     method: 'get'
//   })
// }
//
// // 获取最近的测试记录
// export function getRecentTestRecords(limit = 10) {
//   return request({
//     url: '/professional-test/records/recent',
//     method: 'get',
//     params: { limit }
//   })
// }
//
// // 获取指定分类的测试记录
// export function getTestRecordsByCategory(categoryId, limit = 10) {
//   return request({
//     url: `/professional-test/categories/${categoryId}/records`,
//     method: 'get',
//     params: { limit }
//   })
// }
//
// // 获取最佳成绩记录
// export function getBestScoreRecord(categoryId) {
//   return request({
//     url: `/professional-test/categories/${categoryId}/best-score`,
//     method: 'get'
//   })
// }
//
// // 删除测试记录
// export function deleteTestRecord(recordId) {
//   return request({
//     url: `/professional-test/records/${recordId}`,
//     method: 'delete'
//   })
// }
//
// // 保存测试记录
// export function saveTestRecord(testRecord) {
//   return request({
//     url: '/professional-test/save-record',
//     method: 'post',
//     data: testRecord
//   })
// }
//
// // 获取测试记录详情
// export function getTestRecordDetail(recordId) {
//   return request({
//     url: `/professional-test/records/${recordId}`,
//     method: 'get'
//   })
// }
