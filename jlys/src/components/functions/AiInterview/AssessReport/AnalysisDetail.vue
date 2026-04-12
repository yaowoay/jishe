<!--<template>-->
<!--  <div class="analysis-detail">-->
<!--    <div class="detail-container">-->
<!--      <div class="content-section">-->
<!--        <h3>详细分析</h3>-->
<!--        <p class="detail-text">{{ analysisContent }}</p>-->
<!--      </div>-->

<!--      <div class="recommendations">-->
<!--        <h3>提升建议</h3>-->
<!--        <ul>-->
<!--          <li v-for="(item, index) in recommendations" :key="index">{{ item }}</li>-->
<!--        </ul>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import { ref, watch } from 'vue'-->

<!--export default {-->
<!--  name: 'AnalysisDetail',-->
<!--  props: {-->
<!--    analysisType: {  // 通过props接收分析类型-->
<!--      type: String,-->
<!--      required: true-->
<!--    },-->
<!--    analysisId: {  // 通过props接收分析ID-->
<!--      type: String,-->
<!--      required: true-->
<!--    }-->
<!--  },-->
<!--  setup(props) {-->
<!--    const analysisTitle = ref('能力详细分析')-->
<!--    const analysisContent = ref('')-->
<!--    const recommendations = ref([])-->

<!--    // 监听props变化，更新数据-->
<!--    watch([() => props.analysisType, () => props.analysisId],-->
<!--      () => loadAnalysisData(),-->
<!--      { immediate: true }-->
<!--    )-->

<!--    // 加载分析数据的方法（包含8个项目的完整数据）-->
<!--    const loadAnalysisData = () => {-->
<!--      try {-->
<!--        if (props.analysisType === 'professional') {-->
<!--          analysisTitle.value = '专业能力解析'-->

<!--          // ID 0-3（原有数据）-->
<!--          if (props.analysisId === '0') {-->
<!--            analysisTitle.value = '专业技术水平与系统架构能力解析'-->
<!--            analysisContent.value = '在专业技术方面，候选人展现了扎实的前端开发基础，熟练掌握Vue.js等框架的使用。对于组件化和模块化开发有很好的理解，能够熟练应用相关设计模式。系统架构能力方面，能够理解基本的前端架构设计理念，但在复杂系统设计上还有提升空间。'-->
<!--            recommendations.value = [-->
<!--              '建议深入学习前端架构设计模式，提升系统设计能力',-->
<!--              '多参与大型项目的架构讨论，积累实践经验',-->
<!--              '加强对性能优化和安全性方面的学习'-->
<!--            ]-->
<!--          } else if (props.analysisId === '1') {-->
<!--            analysisTitle.value = '应用能力与沟通协作能力解析'-->
<!--            analysisContent.value = '候选人在实际应用能力方面表现良好，能够将所学知识应用到实际开发中。解决问题的能力较强，能够面对常见的技术挑战并找到解决方案。沟通协作能力方面，表达清晰，能够理解需求并与团队成员有效沟通，但在跨部门协作方面可以进一步提升。'-->
<!--            recommendations.value = [-->
<!--              '提升跨部门沟通效率，学习相关沟通技巧',-->
<!--              '增强技术方案表达能力，练习向非技术人员解释复杂概念',-->
<!--              '参与更多团队协作项目，锻炼协调与妥协能力'-->
<!--            ]-->
<!--          } else if (props.analysisId === '2') {-->
<!--            analysisTitle.value = '动手能力与学习能力解析'-->
<!--            analysisContent.value = '候选人的动手能力表现出色，能够迅速将想法转化为代码实现。在编码过程中表现出较强的逻辑思维和问题解决能力。学习能力方面，候选人能够快速掌握新技术和工具，适应技术变化，持续学习的意愿和能力都很强。但在某些复杂技术的深度理解上还需要加强。'-->
<!--            recommendations.value = [-->
<!--              '参与更多开源项目，提升实际编码经验',-->
<!--              '系统学习计算机科学基础理论，增强解决复杂问题的能力',-->
<!--              '建立个人知识体系，形成技术学习方法论'-->
<!--            ]-->
<!--          } else if (props.analysisId === '3') {-->
<!--            analysisTitle.value = '执行力解析'-->
<!--            analysisContent.value = '候选人在任务执行方面表现出色，能够按时高质量地完成分配的工作。工作态度积极主动，具有一定的自我管理能力和时间规划能力。在压力环境下能够保持稳定的工作状态，但在工作优先级的把握和资源协调方面还有提升空间。'-->
<!--            recommendations.value = [-->
<!--              '学习项目管理方法论，提升任务规划与执行效率',-->
<!--              '培养问题分解能力，将复杂任务拆解为可管理的小任务',-->
<!--              '练习在高压环境下的情绪管理和高效工作能力'-->
<!--            ]-->
<!--          }-->
<!--          // ID 4-7（新增数据）-->
<!--          else if (props.analysisId === '4') {-->
<!--            analysisTitle.value = '创新能力解析'-->
<!--            analysisContent.value = '候选人在创新思维方面表现出一定潜力，能够提出一些新的想法和解决方案。在面对常规问题时，有时能够跳出传统思维模式，寻找更优的解决途径。然而，在将创新想法转化为实际可行方案方面的经验还比较欠缺，需要进一步培养系统创新能力。'-->
<!--            recommendations.value = [-->
<!--              '学习设计思维方法论，提升创新问题解决能力',-->
<!--              '参与创新项目或黑客马拉松，积累实战经验',-->
<!--              '培养跨界思维，关注其他领域的创新案例和方法'-->
<!--            ]-->
<!--          } else if (props.analysisId === '5') {-->
<!--            analysisTitle.value = '团队领导力解析'-->
<!--            analysisContent.value = '候选人在团队协作中表现出一定的组织协调能力，能够较好地配合团队完成任务。在小型团队中能够发挥积极作用，有时会主动承担协调工作。但在正式领导角色中，缺乏明确的领导策略和团队激励方法，需要进一步提升领导力相关技能。'-->
<!--            recommendations.value = [-->
<!--              '参加领导力培训课程，学习团队管理理论',-->
<!--              '尝试在项目中担任小组负责人角色，积累实践经验',-->
<!--              '阅读领导力相关书籍，学习成功领导者的经验'-->
<!--            ]-->
<!--          } else if (props.analysisId === '6') {-->
<!--            analysisTitle.value = '技术文档能力解析'-->
<!--            analysisContent.value = '候选人能够编写基本的技术文档，包括API说明和使用指南。文档结构较为清晰，但在详细程度和规范性方面还有提升空间。对于复杂系统的文档组织能力不足，缺乏统一的文档标准和模板意识。图表使用和复杂概念可视化表达能力有待加强。'-->
<!--            recommendations.value = [-->
<!--              '学习技术文档写作规范和最佳实践',-->
<!--              '建立个人文档模板库，统一文档风格',-->
<!--              '练习使用图表工具可视化复杂概念和流程'-->
<!--            ]-->
<!--          } else if (props.analysisId === '7') {-->
<!--            analysisTitle.value = '质量保障意识解析'-->
<!--            analysisContent.value = '候选人具备基本的代码质量意识，能够进行简单的自测。了解单元测试的概念，但实际应用经验有限。对于代码审查流程和质量标准的理解不够深入，缺乏系统性的质量保障思维。在性能优化和安全防护方面的意识需要加强。'-->
<!--            recommendations.value = [-->
<!--              '系统学习软件测试理论和实践方法',-->
<!--              '掌握至少一种单元测试框架并应用于实际项目',-->
<!--              '学习代码审查流程和质量评估标准'-->
<!--            ]-->
<!--          }-->
<!--        } else if (props.analysisType === 'state') {-->
<!--          analysisTitle.value = '状态解析'-->

<!--          // ID 0-3（原有数据）-->
<!--          if (props.analysisId === '0') {-->
<!--            analysisTitle.value = '应答能力解析'-->
<!--            analysisContent.value = '候选人在面试过程中应答流畅，能够理解问题并给出相关回答。思维较为清晰，表达有条理，但在一些技术深度问题上回答深度有待提升。'-->
<!--            recommendations.value = [-->
<!--              '准备更多技术深度问题的回答',-->
<!--              '练习总结和提炼核心观点的能力',-->
<!--              '注意回答的逻辑性和完整性'-->
<!--            ]-->
<!--          } else if (props.analysisId === '1') {-->
<!--            analysisTitle.value = '谈吐措辞与表达性解析'-->
<!--            analysisContent.value = '候选人在面试交流中表达清晰，语言组织较为流畅，能够使用适当的专业术语描述技术问题。在回答问题时偶有紧张情况，有时会出现语速过快或停顿的情况。总体上能够保持良好的表达风格，但在技术描述的精确性上还可以进一步提升。'-->
<!--            recommendations.value = [-->
<!--              '多进行模拟面试训练，提高表达的流畅度和自信心',-->
<!--              '丰富技术词汇，提升专业表达的准确性',-->
<!--              '学习结构化表达方法，提高表达的条理性'-->
<!--            ]-->
<!--          } else if (props.analysisId === '2') {-->
<!--            analysisTitle.value = '表达能力解析'-->
<!--            analysisContent.value = '候选人能够清晰地表达自己的想法和观点，技术描述较为准确。在面试过程中展示出良好的语言组织能力，能够将复杂的技术概念用简洁的语言表达出来。然而，在某些抽象概念的解释上还不够形象化，有时缺乏足够的案例支持观点。'-->
<!--            recommendations.value = [-->
<!--              '练习使用类比和案例来解释抽象概念',-->
<!--              '多参与技术分享和讨论，锻炼口头表达能力',-->
<!--              '提升沟通的针对性，根据不同听众调整表达方式'-->
<!--            ]-->
<!--          } else if (props.analysisId === '3') {-->
<!--            analysisTitle.value = '稳重性向解析'-->
<!--            analysisContent.value = '候选人在面试过程中总体表现沉稳，能够保持积极的态度和专注力。遇到困难问题时能够保持冷静，逻辑思考能力较强。但在面对压力型问题或反馈时，偶尔会表现出一定的紧张和不安，情绪波动略微明显。'-->
<!--            recommendations.value = [-->
<!--              '通过心理训练提升压力应对能力',-->
<!--              '增加高压环境下的面试经历，积累经验',-->
<!--              '学习积极的情绪管理技巧，保持稳定的心态'-->
<!--            ]-->
<!--          }-->
<!--          // ID 4-7（新增数据）-->
<!--          else if (props.analysisId === '4') {-->
<!--            analysisTitle.value = '问题解决思路解析'-->
<!--            analysisContent.value = '候选人在解决问题时表现出一定的逻辑思维能力，能够按照常规思路分析问题。对于熟悉领域的问题能够快速找到解决方案，但在面对陌生问题时，缺乏系统性的问题分析方法，有时会陷入试错式的解决方式，效率不高。'-->
<!--            recommendations.value = [-->
<!--              '学习系统性问题解决方法论，如PDCA、5Why等',-->
<!--              '培养结构化思维，建立问题分析框架',-->
<!--              '多练习解决跨领域问题，拓展思维广度'-->
<!--            ]-->
<!--          } else if (props.analysisId === '5') {-->
<!--            analysisTitle.value = '技术热情与自我驱动力解析'-->
<!--            analysisContent.value = '候选人对技术有基本的兴趣，能够关注行业动态。在工作中能够完成分配的任务，但主动学习新技术的积极性有待提高。缺乏明确的技术发展规划和自我提升计划，对技术深度探索的动力不足。'-->
<!--            recommendations.value = [-->
<!--              '参与技术社区，拓展行业视野',-->
<!--              '制定个人技术发展计划和学习目标',-->
<!--              '尝试参与开源项目或个人技术项目'-->
<!--            ]-->
<!--          } else if (props.analysisId === '6') {-->
<!--            analysisTitle.value = '抗压能力解析'-->
<!--            analysisContent.value = '候选人在一般工作压力下能够保持工作效率，但在高强度压力下表现出明显的紧张情绪。面对多任务并行和紧急情况时，时间管理能力和优先级判断能力有待提升。压力下的决策质量和问题解决能力有较大提升空间。'-->
<!--            recommendations.value = [-->
<!--              '学习压力管理技巧和时间管理方法',-->
<!--              '练习在压力环境下的任务分解和优先级排序',-->
<!--              '培养正念思维，提升情绪调节能力'-->
<!--            ]-->
<!--          } else if (props.analysisId === '7') {-->
<!--            analysisTitle.value = '职业规划清晰度解析'-->
<!--            analysisContent.value = '候选人对自身职业发展有初步想法，但缺乏清晰的规划和目标。对行业发展趋势的了解不够深入，对自身优势和发展方向的认知不够明确。在职业决策时，更多考虑短期因素而非长期发展。'-->
<!--            recommendations.value = [-->
<!--              '进行职业测评，深入了解自身优势和兴趣',-->
<!--              '研究行业发展趋势，明确职业发展方向',-->
<!--              '制定短期和长期职业目标及实现路径'-->
<!--            ]-->
<!--          }-->
<!--        }-->
<!--      } catch (error) {-->
<!--        console.error('加载详情数据出错:', error)-->
<!--        analysisTitle.value = '能力分析详情'-->
<!--        analysisContent.value = '加载详细数据时出现问题，请稍后再试。'-->
<!--        recommendations.value = ['刷新页面重试']-->
<!--      }-->
<!--    }-->

<!--    return {-->
<!--      analysisTitle,-->
<!--      analysisContent,-->
<!--      recommendations-->
<!--    }-->
<!--  }-->
<!--}-->
<!--</script>-->

<!--<style scoped>-->
<!--.analysis-detail {-->
<!--  max-width: 1000px;-->
<!--  margin: 0 auto;-->
<!--  padding: 20px;-->
<!--  background-color: #f8f9fa;-->
<!--  min-height: 100vh;-->
<!--}-->

<!--.detail-header {-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--  margin-bottom: 20px;-->
<!--  background-color: #fff;-->
<!--  padding: 15px 20px;-->
<!--  border-radius: 8px;-->
<!--  box-shadow: 0 2px 8px rgba(0,0,0,0.1);-->
<!--  position: relative;-->
<!--}-->

<!--.header-left {-->
<!--  position: absolute;-->
<!--  left: 20px;-->
<!--}-->

<!--.back-button {-->
<!--  background: none;-->
<!--  border: none;-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  cursor: pointer;-->
<!--  color: #409EFF;-->
<!--  font-size: 14px;-->
<!--  padding: 5px 10px;-->
<!--  border-radius: 4px;-->
<!--  transition: all 0.3s;-->
<!--}-->

<!--.back-button:hover {-->
<!--  background-color: #f0f9ff;-->
<!--}-->

<!--.back-icon {-->
<!--  margin-right: 4px;-->
<!--  font-size: 16px;-->
<!--}-->

<!--.detail-header h2 {-->
<!--  margin: 0;-->
<!--  color: #333;-->
<!--  font-size: 22px;-->
<!--}-->

<!--.detail-container {-->
<!--  background-color: #fff;-->
<!--  border-radius: 8px;-->
<!--  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);-->
<!--  padding: 20px;-->
<!--  margin-bottom: 20px;-->
<!--}-->

<!--.content-section {-->
<!--  margin-bottom: 30px;-->
<!--  border-bottom: 1px solid #eee;-->
<!--  padding-bottom: 20px;-->
<!--}-->

<!--h3 {-->
<!--  color: #333;-->
<!--  margin-bottom: 15px;-->
<!--  font-size: 18px;-->
<!--}-->

<!--.detail-text {-->
<!--  line-height: 1.6;-->
<!--  color: #555;-->
<!--  margin-bottom: 15px;-->
<!--}-->

<!--.recommendations ul {-->
<!--  padding-left: 20px;-->
<!--}-->

<!--.recommendations li {-->
<!--  margin-bottom: 8px;-->
<!--  color: #555;-->
<!--  line-height: 1.5;-->
<!--}-->

<!--@media (prefers-color-scheme: dark) {-->
<!--  .analysis-detail {-->
<!--    background-color: #2c2c2c;-->
<!--  }-->

<!--  .detail-header, .detail-container {-->
<!--    background-color: #333;-->
<!--    color: #fff;-->
<!--  }-->

<!--  .detail-header h2, h3 {-->
<!--    color: #fff;-->
<!--  }-->

<!--  .detail-text, .recommendations li {-->
<!--    color: #ddd;-->
<!--  }-->

<!--  .content-section, .recommendations {-->
<!--    border-color: #444;-->
<!--  }-->

<!--  .back-button {-->
<!--    color: #88c0ff;-->
<!--  }-->

<!--  .back-button:hover {-->
<!--    background-color: #3a3a3a;-->
<!--  }-->
<!--}-->
<!--</style>-->


<template>
  <div class="analysis-detail">
    <div class="detail-header">
      <button @click="$router.back()" class="back-button">
        <span class="back-icon">&lt;</span> 返回
      </button>
      <h2>{{ currentAnalysis.title }}</h2>
    </div>

    <div class="detail-container">
      <div class="content-section">
        <h3>综合概述</h3>
        <p class="detail-text">{{ currentAnalysis.summary }}</p>
      </div>

      <div v-if="currentAnalysis.strengths && currentAnalysis.strengths.length > 0" class="content-section">
        <h3>✔️ 优点分析</h3>
        <ul>
          <li v-for="(item, index) in currentAnalysis.strengths" :key="`strength-${index}`">{{ item }}</li>
        </ul>
      </div>

      <div v-if="currentAnalysis.weaknesses && currentAnalysis.weaknesses.length > 0" class="content-section">
        <h3>⚠️ 待改进点</h3>
        <ul>
          <li v-for="(item, index) in currentAnalysis.weaknesses" :key="`weakness-${index}`">{{ item }}</li>
        </ul>
      </div>

      <div v-if="currentAnalysis.recommendations && currentAnalysis.recommendations.length > 0" class="recommendations">
        <h3>🚀 提升建议</h3>
        <ul>
          <li v-for="(item, index) in currentAnalysis.recommendations" :key="`rec-${index}`">{{ item }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
// 导入你的数据中心
import { analysisData } from '@/services/analysisData.js'

export default {
  name: 'AnalysisDetail',
  props: {
    analysisType: {
      type: String,
      required: true
    },
    analysisId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    // 使用 computed 属性来动态查找数据，替代所有 if/else
    const currentAnalysis = computed(() => {
      // 安全地从数据中心查找数据，如果找不到则返回一个默认的空对象
      return analysisData[props.analysisType]?.[props.analysisId] || {
        title: '未知分析项',
        summary: '没有找到对应的分析数据。',
        strengths: [],
        weaknesses: [],
        recommendations: []
      }
    })

    // 直接返回这个 computed 属性
    return {
      currentAnalysis
    }
  }
}
</script>

<style scoped>
.analysis-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.detail-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: relative;
}

.header-left {
  position: absolute;
  left: 20px;
}

.back-button {
  background: none;
  border: none;
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #409EFF;
  font-size: 14px;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
  position: absolute; /* 定位返回按钮 */
  left: 20px;
}

.back-button:hover {
  background-color: #f0f9ff;
}

.back-icon {
  margin-right: 4px;
  font-weight: bold;
}

.detail-header h2 {
  margin: 0;
  color: #333;
  font-size: 22px;
}

.detail-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px 30px;
  margin-bottom: 20px;
}

.content-section, .recommendations {
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}
.content-section:last-child, .recommendations:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 10px;
}

h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 18px;
  display: flex;
  align-items: center;
}

.detail-text {
  line-height: 1.7;
  color: #555;
}

ul {
  padding-left: 20px;
  list-style-type: disc;
}

li {
  margin-bottom: 10px;
  color: #555;
  line-height: 1.6;
}

@media (prefers-color-scheme: dark) {
  .analysis-detail {
    background-color: #1e1e1e;
  }

  .detail-header, .detail-container {
    background-color: #2c2c2c;
  }

  .detail-header h2, h3 {
    color: #f0f0f0;
  }

  .detail-text, li {
    color: #ccc;
  }

  .content-section, .recommendations {
    border-color: #444;
  }

  .back-button {
    color: #88c0ff;
  }

  .back-button:hover {
    background-color: #3a3a3a;
  }
}
</style>