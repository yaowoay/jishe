<template>
  <div class="word-cloud-wrapper">
    <div ref="wordCloudChart" class="word-cloud-container"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import 'echarts-wordcloud'
//import axios from 'axios';
//import { getSkillData, getCompanyData, getFinanceData, getSalaryData } from '@/services/api';

export default {
  name: 'WordCloudChart',
  data() {
    return {
      wordData: [],  // 存储技能数据
      chartInstance: null,
      updateInterval: null,
      loading: false,  // 补充加载状态字段（原代码缺失）
      error: ''        // 补充错误状态字段（原代码缺失）
    }
  },
  mounted() {
    this.initChart()
    this.fetchSkillData()  // 组件挂载时拉取数据
    this.startDynamicUpdate()
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    clearInterval(this.updateInterval)
    window.removeEventListener('resize', this.handleResize)
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  methods: {
    /**
     * 从API拉取技能数据
     * 保留原接口逻辑，新增大模型技能兜底数据（避免API返回为空时词云无内容）
     */
    // async fetchSkillData() {
    //   this.loading = true;
    //   this.error = '';

    //   try {
    //     // 保留原接口调用逻辑（getCompanyData等暂未使用，可根据后续需求扩展）
    //     const skillRes = await getSkillData();
    //     // 调用其他接口（若后续需整合数据可在此处理）
    //     await getCompanyData();
    //     await getFinanceData();
    //     await getSalaryData();

    //     // 处理接口返回数据：若接口返回有效数组则用接口数据，否则用模拟数据（含大模型技能）
    //     let sourceData = [];
    //     if (Array.isArray(skillRes) && skillRes.length > 0) {
    //       sourceData = skillRes;
    //     } else {
    //       // 模拟数据：保留原有技能，新增Transformer/LLM等大模型相关技能
    //       sourceData = [
    //         { skill_name: 'Spring', demand_count: 9862 },
    //         { skill_name: 'SQL', demand_count: 9197 },
    //         { skill_name: 'Java', demand_count: 6334 },
    //         { skill_name: 'Spark', demand_count: 5958 },
    //         { skill_name: 'Hadoop', demand_count: 3574 },
    //         // 新增大模型相关技能（按需求热度排序）
    //         { skill_name: 'Transformer', demand_count: 8752 },
    //         { skill_name: 'LLM', demand_count: 7923 },
    //         { skill_name: 'Prompt Engineering', demand_count: 6845 },
    //         { skill_name: 'LangChain', demand_count: 5218 },
    //         { skill_name: 'RAG', demand_count: 4936 },
    //         { skill_name: 'TensorFlow', demand_count: 4721 },
    //         { skill_name: 'PyTorch', demand_count: 4589 }
    //       ];
    //     }

    //     // 格式转换：适配词云所需的{name, value}结构
    //     this.wordData = sourceData.map(item => ({
    //       name: item.skill_name,
    //       value: item.demand_count
    //     }));
    //     this.renderWordCloud();  // 渲染词云

    //   } catch (err) {
    //     this.error = `数据加载失败: ${err.message || '未知错误'}`;
    //     console.error('获取技能数据失败', err);
    //     // 错误时用兜底数据（含大模型技能），避免词云空白
    //     this.wordData = [
    //       { name: 'Spring', value: 9862 },
    //       { name: 'SQL', value: 9197 },
    //       { name: 'Transformer', value: 8752 },
    //       { name: 'LLM', value: 7923 },
    //       { name: 'Java', value: 6334 }
    //     ];
    //     this.renderWordCloud();
    //   } finally {
    //     this.loading = false;
    //   }
    // },
    async fetchSkillData() {

      // 处理接口返回数据：若接口返回有效数组则用接口数据，否则用模拟数据（含大模型技能）
      let sourceData = []
      // 模拟数据：保留原有技能，新增Transformer/LLM等大模型相关技能
      sourceData = [
        { skill_name: '团队协作', demand_count: 2506 },
        { skill_name: 'Java', demand_count: 1233 },
        { skill_name: 'MySQL', demand_count: 890 },
        { skill_name: '学习能力', demand_count: 879 },
        { skill_name: '办公技能', demand_count: 699 },
        { skill_name: 'Python', demand_count: 689 },
        { skill_name: 'Hadoop', demand_count: 660 },
        { skill_name: 'SQL', demand_count: 649 },
        { skill_name: 'Linux', demand_count: 606 },
        { skill_name: 'Oracle', demand_count: 564 }
      ]

      // 格式转换：适配词云所需的{name, value}结构
      this.wordData = sourceData.map(item => ({
        name: item.skill_name,
        value: item.demand_count
      }))
      this.renderWordCloud()  // 渲染词云
    },

    /**
     * 初始化ECharts实例
     */
    initChart() {
      this.chartInstance = echarts.init(this.$refs.wordCloudChart)
    },

    /**
     * 渲染词云图表
     */
    renderWordCloud() {
      if (!this.chartInstance) return

      const option = {
        title: {
          show: false
        },
        tooltip: {
          show: true,
          formatter: params => `${params.name}: 需求热度 ${params.value}`  // 自定义提示框内容
        },
        series: [{
          name: '技能需求热度',
          type: 'wordCloud',
          sizeRange: [14, 36],  // 调整文字大小范围（原14-20偏小，优化后更易区分热度）
          rotationRange: [0, 0],  // 文字不旋转，保持可读性
          shape: 'circle',  // 词云形状（可选：circle/rect/roundRect/triangle/diamond/pentagon/hexagon）
          gridSize: 20,  // 字符间距（原30偏大，优化后更紧凑）
          textPadding: 5,  // 文字内边距
          autoSize: {
            enable: true,
            minSize: 8
          },
          textStyle: {
            normal: {
              // 科技风彩色词云，每个词使用不同颜色
              color: function() {
                const colors = [
                  '#00d4ff', // 科技蓝
                  '#00ff88', // 科技绿
                  '#ff6b6b', // 科技红
                  '#ffd93d', // 科技黄
                  '#6c5ce7', // 科技紫
                  '#fd79a8', // 科技粉
                  '#00cec9', // 科技青
                  '#fdcb6e', // 科技橙
                  '#e17055', // 科技橘
                  '#74b9ff', // 科技浅蓝
                  '#a29bfe', // 科技浅紫
                  '#fd79a8', // 科技玫瑰
                  '#55efc4', // 科技薄荷绿
                  '#ff7675', // 科技珊瑚红
                  '#81ecec'  // 科技水蓝
                ]
                return colors[Math.floor(Math.random() * colors.length)]
              },
              fontWeight: 'bold',
              textShadow: '0 0 10px rgba(0, 212, 255, 0.5)'
            },
            emphasis: {
              shadowBlur: 20,
              shadowColor: 'rgba(0, 212, 255, 0.8)',
              textShadow: '0 0 15px rgba(0, 212, 255, 0.8)'
            }
          },
          data: this.wordData
        }]
      }

      this.chartInstance.setOption(option)
    },

    /**
     * 启动词云动态更新（3秒/次）
     * 优化：新增大模型技能到随机添加列表，确保动态更新时能出现
     */
    startDynamicUpdate() {
      this.updateInterval = setInterval(() => {
        if (this.wordData.length === 0) return

        // 1. 随机修改某个技能的需求热度
        const randomIndex = Math.floor(Math.random() * this.wordData.length)
        this.wordData[randomIndex].value = Math.floor(Math.random() * 5000) + 5000  // 热度范围5000-10000

        // 2. 随机添加新技能（最多15个，避免过度拥挤）
        if (Math.random() > 0.7 && this.wordData.length < 15) {
          // 新增技能列表：包含传统技能和大模型技能
          const newSkillPool = [
            // 传统技能
            { name: 'Oracle', baseValue: 564 },
            { name: '云原生', baseValue: 493 }
          ]

          // 过滤已存在的技能
          const availableSkills = newSkillPool.filter(skill =>
            !this.wordData.some(item => item.name === skill.name)
          )

          if (availableSkills.length > 0) {
            const randomSkill = availableSkills[Math.floor(Math.random() * availableSkills.length)]
            this.wordData.push({
              name: randomSkill.name,
              value: Math.floor(Math.random() * 1500) + randomSkill.baseValue  // 热度在基准值±1500波动
            })
          }
        }

        // 3. 随机删除技能（最少保留5个，确保基础展示）
        if (Math.random() > 0.7 && this.wordData.length > 5) {
          const deleteIndex = Math.floor(Math.random() * this.wordData.length)
          this.wordData.splice(deleteIndex, 1)
        }

        // 重新渲染词云
        this.renderWordCloud()
      }, 3000)
    },

    /**
     * 窗口 resize 时调整图表大小
     */
    handleResize() {
      if (this.chartInstance) {
        this.chartInstance.resize()
      }
    }
  }
}
</script>

<style scoped>
.word-cloud-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  background: transparent; /* 设置为透明 */
}

.word-cloud-container {
  width: 100%;
  height: 100%;
  background: transparent; /* 设置为透明 */
}

</style>