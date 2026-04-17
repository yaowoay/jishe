<template>
  <div class="table-lens-container" ref="rootRef">
    <!-- 滚动容器：适配小窗口横向滚动 -->
    <div class="table-scroll-container" ref="scrollRef">
      <table class="data-table">
        <thead>
          <tr>
            <th>公司名称</th>
            <th>职位名称</th>
            <th>经验要求</th>
            <th>学历要求</th>
            <th>薪资(元)</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in sortedData" :key="`${item.company_name}-${item.position_name}`" class="table-row">
            <td>{{ item.company_name }}</td>
            <td>{{ item.position_name }}</td>
            <td>{{ item.experience_req }}</td>
            <td>{{ item.education_req }}</td>
            <td>
              <!-- 薪资条形图展示容器 -->
              <div class="salary-bar-wrapper">
                <span class="salary-value">{{ item.salary.toLocaleString() }}</span>
                <div class="salary-bar-bg">
                  <div 
                    class="salary-bar" 
                    :style="{ width: `${(item.salary / maxSalary) * 100}%` }"
                  ></div>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'

// 原始招聘数据
const tableData = ref([
  {
    company_name: '北京伯凯科技',
    position_name: '大数据方案架构师',
    experience_req: '无需经验',
    education_req: '本科',
    company_type: '外资（欧美）',
    salary: 45000
  },
  {
    company_name: '东莞新能安科技',
    position_name: '大数据开发工程师',
    experience_req: '3-4年经验',
    education_req: '本科',
    company_type: '合资',
    salary: 41667
  },
  {
    company_name: '合肥国轩高科动力能源',
    position_name: '大数据开发工程师',
    experience_req: '5-7年经验',
    education_req: '本科',
    company_type: '已上市',
    salary: 20000
  },
  {
    company_name: '亚信科技（中国）',
    position_name: '大数据开发工程师',
    experience_req: '5-7年经验',
    education_req: '本科',
    company_type: '已上市',
    salary: 17500
  },
  {
    company_name: '京北方信息技术',
    position_name: '大数据开发工程师',
    experience_req: '3-4年经验',
    education_req: '本科',
    company_type: '民营',
    salary: 15000
  },
  {
    company_name: '中国电信',
    position_name: 'C++开发工程师',
    experience_req: '10年以上经验',
    education_req: '硕士',
    company_type: '国企',
    salary: 12500
  },
  {
    company_name: '中国电信',
    position_name: 'Java开发工程师',
    experience_req: '3-4年经验',
    education_req: '本科',
    company_type: '国企',
    salary: 12458
  }
])

// 排序控制
const sortField = ref('salary')
const sortAsc = ref(false)

// 计算最大薪资（用于条形图宽度比例计算）
const maxSalary = computed(() => {
  return Math.max(...tableData.value.map(item => item.salary))
})

// refs for responsive resize handling
const rootRef = ref(null)
const scrollRef = ref(null)

let ro = null

onMounted(() => {
  // ResizeObserver 自适应调整滚动容器高度
  if (typeof ResizeObserver !== 'undefined') {
    ro = new ResizeObserver(async () => {
      await nextTick()
      const root = rootRef.value
      const scroll = scrollRef.value
      if (!root || !scroll) return
      const controls = root.querySelector('.table-controls')
      const rootStyle = getComputedStyle(root)
      const paddingTop = parseFloat(rootStyle.paddingTop) || 0
      const paddingBottom = parseFloat(rootStyle.paddingBottom) || 0
      const controlsHeight = controls ? controls.getBoundingClientRect().height : 0
      
      // 计算目标高度：显示7行数据 + 表头
      const thead = root.querySelector('thead')
      const firstRow = root.querySelector('tbody tr')
      const headerH = thead ? thead.getBoundingClientRect().height : 0
      const rowH = firstRow ? firstRow.getBoundingClientRect().height : 44
      const visibleRows = 7
      const gap = 8
      const targetHeight = headerH + rowH * visibleRows + gap

      // 可用高度限制
      const available = root.clientHeight - paddingTop - paddingBottom - controlsHeight - gap
      const finalHeight = Math.min(targetHeight, Math.max(40, available))
      scroll.style.maxHeight = finalHeight + 'px'
    })
    if (rootRef.value) ro.observe(rootRef.value)
  }
})

onBeforeUnmount(() => {
  // 清理ResizeObserver
  if (ro) {
    ro.disconnect()
    ro = null
  }
})

// 排序后的数据计算属性
const sortedData = computed(() => {
  return [...tableData.value].sort((a, b) => {
    let valA, valB
    switch (sortField.value) {
    case 'salary':
      valA = a.salary
      valB = b.salary
      break
    case 'company_name':
      // 字符串按拼音排序（中文）
      valA = a.company_name.localeCompare(b.company_name, 'zh-CN')
      valB = 0
      break
    case 'experience_req': {
      // 经验要求排序（简单处理：数字优先，文字最后）
      const getExpNum = (exp) => {
        const num = exp.match(/\d+/g)?.[0] || 0
        return exp.includes('无需') ? -1 : Number(num)
      }
      valA = getExpNum(a.experience_req)
      valB = getExpNum(b.experience_req)
      break
    }
    case 'education_req': {
      // 学历排序优先级：硕士 > 本科
      const eduRank = { '硕士': 2, '本科': 1 }
      valA = eduRank[a.education_req] || 0
      valB = eduRank[b.education_req] || 0
      break
    }
    default:
      return 0
    }
    // 处理字符串排序的特殊情况
    if (sortField.value === 'company_name') {
      return sortAsc.value ? valA : -valA
    }
    return sortAsc.value ? valA - valB : valB - valA
  })
})

const sortTable = () => {
  // 依赖计算属性自动更新，无需额外逻辑
}
</script>

<style scoped>
/* 容器基础样式 - 优化整体布局（适配白色背景） */
.table-lens-container {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  color: #333333; /* 改为深色文字适配白色背景 */
  display: flex;
  flex-direction: column;
  padding:20px 0px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); /* 弱化阴影适配白色背景 */
  background-color: #ffffff; /* 白色背景 */
}

/* 新增标题区域样式（已适配白色背景） */
.table-header {
  margin-bottom: 0px;
  padding-bottom: 13px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.table-title {
  font-size: 20px;
  font-weight: 700;
  color: #333333; /* 改为深色标题 */
  margin: 0 0 6px 0;
  display: flex;
  /* 核心：水平居中 */
  justify-content: center;
  /* 原有垂直居中 */
  align-items: center;
  gap: 8px;
  /* 移除霓虹效果，适配白色背景 */
  text-shadow: none;
}

.table-subtitle {
  font-size: 12px;
  color: #666666; /* 改为灰色副标题 */
  margin: 0;
}

/* 滚动容器样式 - 优化视觉（适配白色背景） */
.table-scroll-container {
  flex: 1 1 auto;
  overflow-x: auto;
  overflow-y: auto;
  border-radius: 8px;
  background-color: #ffffff; /* 白色背景 */
  min-height: 80px;
  padding: 4px;
}

/* 表格基础样式 - 重新设计（适配白色背景） */
.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
  font-size: 14px;
}

.data-table th {
  padding: 12px 16px;
  white-space: nowrap;
  background: linear-gradient(90deg, #4096ff, #69b1ff); /* 改为浅蓝渐变适配白色背景 */
  color: #ffffff; /* 表头文字保持白色 */
  font-weight: 600;
  position: sticky;
  top: 0;
  z-index: 10;
  border: none;
  border-bottom: 2px solid #e5e6eb; /* 改为浅灰色边框 */
}
.data-table th {
  background-color: transparent; /* 表头透明 */
  color: #ffffff; /* 表头文字白色 */
  font-weight: 600;
  cursor: pointer;
  position: sticky; /* 表头粘性定位，滚动时不消失 */
  top: 0;
  z-index: 10;
}

/* 行交替颜色：提升可读性（适配白色背景） */
.table-row:nth-child(even) {
  background-color: #f8f9fa; /* 浅灰色交替行 */
}

.data-table td {
  padding: 12px 16px;
  white-space: nowrap;
  border: none;
  border-bottom: 1px solid #e5e6eb; /* 改为浅灰色分隔线 */
  color: #333333; /* 深色文字适配白色背景 */
}

/* 行样式 - 优化交互（适配白色背景） */
.table-row {
  transition: all 0.2s ease;
}

.table-row:hover {
  background-color: rgba(64, 150, 255, 0.1); /* 浅蓝色hover效果 */
  transform: translateX(2px);
}

.table-row:last-child td {
  border-bottom: none;
}

/* 薪资条形图样式 - 优化视觉（适配白色背景） */
.salary-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  width: 220px;
  margin: 0 auto;
}

.salary-value {
  font-size: 14px;
  color: #333333; /* 深色薪资数值 */
  font-weight: 600;
  width: 100%;
  text-align: center;
}

.salary-bar-bg {
  width: 100%;
  height: 14px;
  background-color: #e5e6eb; /* 浅灰色背景条 */
  border-radius: 7px;
  overflow: hidden;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05); /* 弱化阴影 */
}

.salary-bar {
  height: 100%;
  background: linear-gradient(90deg, #4096ff, #69b1ff, #91c2ff); /* 浅蓝渐变适配白色背景 */
  border-radius: 7px;
  transition: width 0.4s ease-in-out;
  box-shadow: 0 0 8px rgba(64, 150, 255, 0.3); /* 浅蓝色发光效果 */
}

/* 滚动条美化（适配白色背景） */
.table-scroll-container::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.table-scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1; /* 浅灰色滚动条轨道 */
  border-radius: 4px;
}

.table-scroll-container::-webkit-scrollbar-thumb {
  background: #cccccc; /* 灰色滚动条 */
  border-radius: 4px;
}

.table-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #999999; /* 深灰色hover */
}

/* 移动端适配（适配白色背景） */
@media (max-width: 768px) {
  .table-lens-container {
    padding: px;
    border-radius: 8px;
  }

  .table-title {
    font-size: 18px;
  }

  .data-table th,
  .data-table td {
    padding: 0px 0px;
    font-size: 13px;
  }

  .salary-bar-wrapper {
    width: 180px;
  }
}
</style>