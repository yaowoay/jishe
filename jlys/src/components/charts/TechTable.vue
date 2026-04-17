<template>
  <div class="tech-table-wrapper">
    <div class="component-title">最新职位列表</div>

    <div class="table-header">
      <div class="header-item" v-for="header in headers" :key="header">{{ header }}</div>
    </div>
    <div class="table-body">
      <div class="table-row" v-for="item in processedData" :key="item.id">
        <div class="cell" :title="item.position">{{ item.position }}</div>
        <div class="cell">{{ item.companySize }}</div>
        <div class="cell">{{ item.salary }}</div>
        <div class="cell">{{ item.location }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 表头定义
const headers = ref(['职位名称', '公司规模', '薪资范围', '发布城市'])

// 原始数据直接定义在组件内部
const rawJobData = ref([
  '1662	AI专家	某知名公司	1-49人	重庆	250-500k·20薪',
  '2551	高性能整车前沿技术研究高级主任(AIGC设计工业软件)	某国内大型整车制造公司	5000-10000人	深圳	300-500k·15薪',
  '1061	aigc技术总监	某北京大型在线社交/媒体公司	500-999人	北京	400-500k',
  '3160	大模型算法负责人	某世界500强整车制造公司	10000人以上	深圳	200-350k·18薪',
  '1544	AIGC技术总监	某上海专业技术服务公司	100-499人	北京	300-500k'
])

// 对内置数据进行处理和格式化
const processedData = computed(() => {
  return rawJobData.value.map(item => {
    const parts = item.trim().split(/\s+/)
    return {
      id: parts[0],
      position: parts[1],
      companySize: parts[3],
      location: parts[4],
      salary: parts[5]
    }
  })
})

</script>

<style scoped>
.tech-table-wrapper {
  width: 100%;
  /*background-color: #0A1B3A; /* 深蓝色背景 */
  border: 1px solid #1A3A6A;
  border-radius: 8px;
  padding: 20px;
  font-family: 'Helvetica Neue', 'Microsoft YaHei', sans-serif;
  box-shadow: 0 0 15px rgba(0, 123, 255, 0.3);
  overflow: hidden;
}

/* 2. 为标题添加样式 */
.component-title {
  color: #7DF9FF; /* 亮青色，科技感 */
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 20px; /* 标题和表头之间的间距 */
  text-shadow: 0 0 5px rgba(125, 249, 255, 0.5); /* 添加微妙的发光效果 */
}

.table-header {
  display: grid;
  grid-template-columns: 2.5fr 1fr 1.5fr 1fr; /* 列宽比例，职位名称更宽 */
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(0, 191, 255, 0.5); /* 亮蓝色边框 */
  color: #00BFFF; /* 亮蓝色文字 */
  font-weight: bold;
  font-size: 16px;
  text-align: center;
}

.table-body {
  max-height: 300px; /* 您可以根据需要调整高度 */
  overflow-y: auto;
}

/* 自定义滚动条样式 */
.table-body::-webkit-scrollbar {
  width: 8px;
}

.table-body::-webkit-scrollbar-track {
  background: #0A1B3A;
  border-radius: 4px;
}

.table-body::-webkit-scrollbar-thumb {
  background: rgba(0, 191, 255, 0.6);
  border-radius: 4px;
}

.table-body::-webkit-scrollbar-thumb:hover {
  background: #00BFFF;
}

.table-row {
  display: grid;
  grid-template-columns: 2.5fr 1fr 1.5fr 1fr;
  align-items: center;
  color: #E0E0E0; /* 浅灰色文字 */
  font-size: 14px;
  padding: 12px 0;
  border-bottom: 1px solid #1A3A6A; /* 行分隔线 */
  transition: background-color 0.3s ease, color 0.3s ease;
  text-align: center;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background-color: rgba(0, 123, 255, 0.2); /* 悬浮时高亮 */
  color: #FFFFFF;
  cursor: pointer;
}

.cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* 超出部分显示省略号 */
  padding: 0 10px;
}

/* 第一个单元格（职位名称）左对齐，以获得更好的可读性 */
.table-row .cell:first-child {
  text-align: left;
  color: #7DF9FF; /* 职位名称使用更亮的颜色 */
  font-weight: 500;
}
</style>