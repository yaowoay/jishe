// 简历分析页面滚动修复验证脚本
// 在浏览器控制台中运行此脚本来检查滚动功能

console.log('=== 简历分析页面滚动修复验证 ===')

// 1. 检查当前页面URL
const currentUrl = window.location.href
console.log('1. 当前页面URL:', currentUrl)

if (currentUrl.includes('/applicant/resume/analysis')) {
  console.log('✅ 正在简历分析页面')
} else {
  console.log('⚠️ 不在简历分析页面，请导航到 /applicant/resume/analysis')
}

// 2. 检查页面高度
const bodyHeight = document.body.scrollHeight
const windowHeight = window.innerHeight
const canScroll = bodyHeight > windowHeight

console.log('2. 页面尺寸检查:')
console.log('   - 页面总高度:', bodyHeight + 'px')
console.log('   - 视口高度:', windowHeight + 'px')
console.log('   - 可滚动距离:', (bodyHeight - windowHeight) + 'px')
console.log('   - 是否需要滚动:', canScroll ? '是' : '否')

// 3. 检查关键元素的样式
const analysisContainer = document.querySelector('.resume-analysis-container')
if (analysisContainer) {
  const containerStyles = window.getComputedStyle(analysisContainer)
  console.log('3. 主容器样式检查:')
  console.log('   - overflow-y:', containerStyles.overflowY)
  console.log('   - overflow-x:', containerStyles.overflowX)
  console.log('   - height:', containerStyles.height)
  console.log('   - min-height:', containerStyles.minHeight)
  console.log('   - max-height:', containerStyles.maxHeight)
} else {
  console.log('3. ⚠️ 未找到 .resume-analysis-container 元素')
}

// 4. 检查body和html的样式
const bodyStyles = window.getComputedStyle(document.body)
const htmlStyles = window.getComputedStyle(document.documentElement)

console.log('4. 全局样式检查:')
console.log('   - body overflow-y:', bodyStyles.overflowY)
console.log('   - body overflow-x:', bodyStyles.overflowX)
console.log('   - html overflow-y:', htmlStyles.overflowY)
console.log('   - html overflow-x:', htmlStyles.overflowX)

// 5. 测试滚动功能
console.log('5. 开始滚动功能测试...')

const originalScrollY = window.scrollY
console.log('   - 当前滚动位置:', originalScrollY)

// 尝试滚动
window.scrollTo({ top: 100, behavior: 'smooth' })

setTimeout(() => {
  const newScrollY = window.scrollY
  console.log('   - 滚动后位置:', newScrollY)
    
  if (newScrollY !== originalScrollY) {
    console.log('✅ 滚动功能正常工作')
  } else if (!canScroll) {
    console.log('ℹ️ 页面内容未超出视口，无需滚动')
  } else {
    console.log('❌ 滚动功能可能存在问题')
  }
    
  // 恢复原始位置
  window.scrollTo({ top: originalScrollY, behavior: 'smooth' })
}, 1000)

// 6. 检查是否有阻止滚动的事件监听器
console.log('6. 事件监听器检查:')
const hasWheelListeners = window.getEventListeners ? 
  Object.keys(window.getEventListeners(window)).includes('wheel') : 
  '无法检查（需要开发者工具）'
console.log('   - wheel事件监听器:', hasWheelListeners)

// 7. 提供修复建议
console.log('7. 修复状态总结:')
console.log('   - ResumeAnalysisReport.vue: min-height 已修改为 auto')
console.log('   - ResumeView.vue: overflow 已修改为允许滚动')
console.log('   - 全局样式: 已优化滚动设置')

if (canScroll) {
  console.log('✅ 页面应该可以正常滚动')
  console.log('💡 如果仍然无法滚动，请尝试:')
  console.log('   1. 清除浏览器缓存并刷新页面')
  console.log('   2. 检查是否有浏览器扩展阻止滚动')
  console.log('   3. 尝试使用键盘方向键或Page Down/Up')
  console.log('   4. 检查鼠标滚轮是否正常工作')
} else {
  console.log('ℹ️ 当前页面内容较少，可能不需要滚动')
  console.log('💡 尝试添加更多内容或缩小浏览器窗口来测试滚动')
}

console.log('=== 验证完成 ===')

// 8. 持续监控滚动事件
let scrollEventCount = 0
const scrollListener = () => {
  scrollEventCount++
  if (scrollEventCount === 1) {
    console.log('✅ 检测到滚动事件，滚动功能正常')
    window.removeEventListener('scroll', scrollListener)
  }
}

window.addEventListener('scroll', scrollListener)

// 10秒后移除监听器
setTimeout(() => {
  window.removeEventListener('scroll', scrollListener)
  if (scrollEventCount === 0) {
    console.log('ℹ️ 10秒内未检测到滚动事件')
  }
}, 10000)