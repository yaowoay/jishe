<template>
  <div class="recommendation-section">
    <div
      v-for="(item, idx) in recommendations"
      :key="idx"
      class="recommendation-item"
      style="margin-bottom: 24px; border-bottom: 1px dashed #eee; padding-bottom: 12px;"
    >
      <div style="font-size: 17px; font-weight: bold; margin-bottom: 6px;">
        {{ item.position }}
        <span style="font-size: 14px; color: #1E90FF; font-weight: normal; margin-left: 10px;">{{ item.match }}，{{ item.score }}分</span>
      </div>
      <div style="margin-bottom: 6px;">
        <span class="recommendation-type">能力标签：</span>
        <span class="recommendation-content">
          <el-tag v-for="(tag, i) in item.tags" :key="i" size="small" style="margin-right:6px;">{{ tag }}</el-tag>
        </span>
      </div>
      <div>
        <span class="recommendation-type">匹配理由：</span>
        <span class="recommendation-content">{{ item.reason }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  recommendations: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.recommendation-section {
  padding: 20px;
  min-height: 100px;
}
.recommendation-item {
  margin-bottom: 12px;
}
.recommendation-type {
  font-weight: bold;
  color: #333;
}
.recommendation-content {
  color: #1E90FF;
}
</style>