<template>
  <div class="written-exam-records-container">
    <div class="timeline-panel">
      <div v-for="(group, idx) in groupedRecords" :key="group.date" class="timeline-date-block">
        <div class="timeline-dot"></div>
        <div class="timeline-date">{{ group.date }}</div>
        <div v-if="idx !== groupedRecords.length - 1" class="timeline-line"></div>
      </div>
    </div>
    <div class="record-list-panel">
      <div v-for="group in groupedRecords" :key="group.date" class="record-group">
        <div class="group-date-title">{{ group.date }}</div>
        <div class="record-cards-row">
          <el-card
            v-for="record in group.records"
            :key="record.id"
            class="mini-record-card"
            shadow="hover"
            @click="showDetail(record)"
          >
            <div class="mini-title">{{ record.title }}</div>
            <div class="mini-score">得分: <span style="color:#67c23a;font-weight:bold;">{{ record.score }}</span></div>
            <div class="mini-summary">{{ record.summary }}</div>
            <div class="mini-time"><el-icon style="font-size:14px;color:#409eff;margin-right:2px;"><i class="el-icon-time"></i></el-icon>{{ record.time.split(' ')[1] }}</div>
          </el-card>
        </div>
      </div>
      <el-dialog v-model="detailVisible" title="笔试详情" width="600px" :close-on-click-modal="true">
        <div v-if="currentDetail">
          <div class="record-title">{{ currentDetail.title }}</div>
          <div class="record-time">考试时间：{{ currentDetail.time }}</div>
          <div class="record-score">得分：<span style="color:#67c23a;font-weight:bold;">{{ currentDetail.score }}</span></div>
          <div class="record-summary">主要内容：{{ currentDetail.summary }}</div>
          <el-divider />
          <div class="record-detail">
            <div v-for="(item, idx) in currentDetail.details" :key="idx" class="record-detail-item">
              <div><b>题目{{ idx + 1 }}：</b>{{ item.question }}</div>
              <div><b>你的答案：</b>{{ item.answer }}</div>
              <div><b>标准答案：</b>{{ item.standard }}</div>
              <div><b>得分：</b>{{ item.score }}</div>
              <el-divider v-if="idx !== currentDetail.details.length - 1" />
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 丰富模拟数据，覆盖多天、每组多条
const records = ref([
  // 2024-06-01
  {
    id: 1, time: '2024-06-01 10:00', title: 'Java开发岗笔试', score: 88, summary: '涵盖Java基础、算法、数据库等内容。',
    details: [
      { question: 'Java中final关键字的作用？', answer: '修饰类、方法、变量，表示不可变', standard: '修饰类、方法、变量，表示不可变', score: 5 },
      { question: '请写出冒泡排序的实现。', answer: 'for循环嵌套...', standard: '标准冒泡排序代码', score: 8 },
      { question: 'MySQL索引的类型有哪些？', answer: '主键、唯一、普通、全文', standard: '主键、唯一、普通、全文', score: 5 }
    ]
  },
  {
    id: 2, time: '2024-06-01 14:30', title: 'Python数据分析岗笔试', score: 91, summary: '数据分析与Pandas库应用。',
    details: [
      { question: 'Pandas中DataFrame的作用？', answer: '二维表格数据结构', standard: '二维表格数据结构', score: 5 },
      { question: '如何读取CSV文件？', answer: 'pd.read_csv()', standard: 'pd.read_csv()', score: 5 },
      { question: 'Numpy和Pandas的区别？', answer: 'Numpy处理数值，Pandas处理表格', standard: 'Numpy处理数值，Pandas处理表格', score: 5 }
    ]
  },
  // 2024-05-31
  {
    id: 3, time: '2024-05-31 09:00', title: '算法岗笔试', score: 80, summary: '主要考查算法与数据结构基础。',
    details: [
      { question: '二分查找的时间复杂度？', answer: 'O(logn)', standard: 'O(logn)', score: 5 },
      { question: '请实现链表反转。', answer: 'while循环反转指针', standard: '标准链表反转代码', score: 8 },
      { question: '堆排序的基本思想？', answer: '构建大顶堆/小顶堆', standard: '构建大顶堆/小顶堆', score: 5 }
    ]
  },
  {
    id: 4, time: '2024-05-31 15:20', title: 'C++工程师笔试', score: 85, summary: 'C++基础与STL应用。',
    details: [
      { question: 'C++中引用和指针的区别？', answer: '引用不能为null', standard: '引用不能为null', score: 5 },
      { question: 'vector和list的区别？', answer: 'vector连续内存，list链表', standard: 'vector连续内存，list链表', score: 5 },
      { question: '什么是RAII？', answer: '资源获取即初始化', standard: '资源获取即初始化', score: 5 }
    ]
  },
  // 2024-05-30
  {
    id: 5, time: '2024-05-30 11:00', title: '前端工程师笔试', score: 92, summary: '考查HTML/CSS/JS及Vue框架。',
    details: [
      { question: 'flex布局的主轴和交叉轴分别是什么？', answer: '主轴是row，交叉轴是column', standard: '主轴（main axis）、交叉轴（cross axis）', score: 5 },
      { question: 'Vue的响应式原理？', answer: '依赖收集+数据劫持', standard: '依赖收集+数据劫持', score: 8 },
      { question: '实现一个防抖函数。', answer: 'function debounce(fn, delay) {...}', standard: '标准防抖实现', score: 5 }
    ]
  },
  {
    id: 6, time: '2024-05-30 16:40', title: '产品经理岗笔试', score: 78, summary: '产品设计与需求分析。',
    details: [
      { question: '什么是PRD？', answer: '产品需求文档', standard: '产品需求文档', score: 5 },
      { question: '竞品分析的步骤？', answer: '调研-对比-总结', standard: '调研-对比-总结', score: 5 },
      { question: '用户画像的作用？', answer: '定位目标用户', standard: '定位目标用户', score: 5 }
    ]
  },
  // 2024-05-29
  {
    id: 7, time: '2024-05-29 09:30', title: '测试工程师笔试', score: 86, summary: '软件测试基础与自动化。',
    details: [
      { question: '单元测试和集成测试区别？', answer: '单元测试针对模块，集成测试针对系统', standard: '单元测试针对模块，集成测试针对系统', score: 5 },
      { question: '什么是Mock？', answer: '模拟依赖', standard: '模拟依赖', score: 5 },
      { question: '自动化测试工具有哪些？', answer: 'Selenium、Jest', standard: 'Selenium、Jest', score: 5 }
    ]
  },
  {
    id: 8, time: '2024-05-29 14:00', title: '运维工程师笔试', score: 83, summary: 'Linux运维与自动化脚本。',
    details: [
      { question: '常用的Linux命令有哪些？', answer: 'ls, cd, grep', standard: 'ls, cd, grep', score: 5 },
      { question: '什么是Shell脚本？', answer: '自动化脚本', standard: '自动化脚本', score: 5 },
      { question: '如何查看端口占用？', answer: 'netstat -anp', standard: 'netstat -anp', score: 5 }
    ]
  }
])

// 按日期分组
const groupedRecords = computed(() => {
  const groups = {}
  records.value.forEach(r => {
    const date = r.time.split(' ')[0]
    if (!groups[date]) groups[date] = []
    groups[date].push(r)
  })
  // 转为数组并按日期降序排列
  return Object.entries(groups)
    .map(([date, records]) => ({ date, records }))
    .sort((a, b) => b.date.localeCompare(a.date))
})

const detailVisible = ref(false)
const currentDetail = ref(null)
function showDetail(record) {
  currentDetail.value = record
  detailVisible.value = true
}
</script>

<style scoped>
.written-exam-records-container {
  display: flex;
  flex-direction: row;
  width: 100%;
  min-height: 600px;
  background: #f8faff;
  padding: 32px 0;
  box-sizing: border-box;
  align-items: flex-start;
}
.timeline-panel {
  width: 120px;
  min-width: 80px;
  background: transparent;
  margin-left: 32px;
  margin-right: 0;
  padding: 0;
  height: fit-content;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 让时间轴和右侧分组对齐 */
  justify-content: flex-start;
}
.timeline-date-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 64px; /* 增大间隔，与右侧分组对齐 */
  position: relative;
}
.timeline-dot {
  width: 16px;
  height: 16px;
  background: #409eff;
  border-radius: 50%;
  margin-bottom: 6px;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px #409eff33;
  z-index: 2;
}
.timeline-date {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 6px;
  text-align: center;
  letter-spacing: 1px;
}
.timeline-line {
  width: 4px;
  height: 64px; /* 与分组间距一致 */
  background: #e0e3e8;
  border-radius: 2px;
  margin: 0 auto;
  z-index: 1;
}
.record-list-panel {
  flex: 1;
  margin-left: 0;
  margin-right: 32px;
  min-width: 320px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
}
.record-group {
  margin-bottom: 64px; /* 与时间轴节点间隔一致 */
  width: 100%;
}
.group-date-title {
  font-size: 20px;
  font-weight: 800;
  color: #409eff;
  margin-bottom: 12px;
  margin-left: 2px;
  letter-spacing: 1px;
  background: #eaf4ff;
  border-radius: 8px;
  padding: 4px 18px 4px 8px;
  display: inline-block;
  box-shadow: 0 2px 8px #b3d8ff11;
}
.record-cards-row {
  display: flex;
  flex-wrap: wrap;
  gap: 28px 28px;
}
.mini-record-card {
  width: 340px;
  min-height: 160px;
  border-radius: 16px;
  box-shadow: 0 4px 18px #b3d8ff22;
  padding: 28px 24px 18px 24px;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  margin-bottom: 8px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: #fff;
}
.mini-record-card:hover {
  box-shadow: 0 8px 28px #409eff33;
  transform: translateY(-2px) scale(1.04);
}
.mini-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.mini-score {
  font-size: 16px;
  color: #67c23a;
  margin-bottom: 6px;
  font-weight: 600;
}
.mini-summary {
  font-size: 15px;
  color: #6b7280;
  margin-bottom: 8px;
  line-height: 1.6;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.mini-time {
  font-size: 13px;
  color: #aaa;
  text-align: right;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 2px;
}
.record-title {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 8px;
}
.record-time {
  font-size: 15px;
  color: #888;
  margin-bottom: 8px;
}
.record-score {
  font-size: 18px;
  color: #222;
  margin-bottom: 8px;
}
.record-summary {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}
.record-detail {
  width: 100%;
}
.record-detail-item {
  margin-bottom: 8px;
  font-size: 15px;
  color: #444;
}
.empty-tip {
  color: #bbb;
  font-size: 18px;
  margin-top: 80px;
  text-align: center;
}
</style>
  