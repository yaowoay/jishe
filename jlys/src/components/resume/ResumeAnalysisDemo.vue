<template>
  <div class="resume-analysis-demo">
    <div class="demo-header">
      <h1>简历分析结果演示</h1>
      <button @click="loadDemoData" class="demo-btn">加载演示数据</button>
    </div>

    <div v-if="analysisData" class="analysis-result">
      <!-- 匹配度雷达 -->
      <div class="section radar-section">
        <h2>🎯 匹配度雷达</h2>
        <div class="score-grid">
          <div class="score-card primary">
            <div class="score-number">{{ radarData.jobMatch }}</div>
            <div class="score-label">岗位契合度</div>
          </div>
          <div class="score-card secondary">
            <div class="score-number">{{ radarData.atsReadability }}</div>
            <div class="score-label">ATS可读性</div>
          </div>
        </div>
        
        <div class="keywords-area">
          <h3>关键词命中</h3>
          <div class="keywords-container">
            <span 
              v-for="keyword in radarData.keywords" 
              :key="keyword.name"
              class="keyword-badge"
              :class="getKeywordLevel(keyword.count)"
            >
              {{ keyword.name }}({{ keyword.count }})
            </span>
          </div>
        </div>
      </div>

      <!-- 亮点提炼 -->
      <div class="section highlights-section">
        <h2>⭐ 亮点提炼</h2>
        <div class="highlights-grid">
          <div 
            v-for="(highlight, index) in highlights" 
            :key="index"
            class="highlight-card"
          >
            <span class="highlight-emoji">{{ highlight.icon }}</span>
            <p class="highlight-content">{{ highlight.text }}</p>
          </div>
        </div>
      </div>

      <!-- 风险预警 -->
      <div class="section risks-section">
        <h2>⚠️ 风险预警</h2>
        <div class="risks-grid">
          <div class="risk-item">
            <h4>缺失信息</h4>
            <p>{{ risks.missing }}</p>
          </div>
          <div class="risk-item">
            <h4>易扣分项</h4>
            <p>{{ risks.deductions }}</p>
          </div>
          <div class="risk-item">
            <h4>隐性雷区</h4>
            <p>{{ risks.hidden }}</p>
          </div>
        </div>
      </div>

      <!-- 即刻优化 -->
      <div class="section optimization-section">
        <h2>✏️ 即刻优化</h2>
        <div class="optimization-container">
          <div class="before-example">
            <h4>原句：</h4>
            <p>{{ optimization.before }}</p>
          </div>
          <div class="arrow">→</div>
          <div class="after-example">
            <h4>✅ 改为：</h4>
            <p>{{ optimization.after }}</p>
          </div>
        </div>
      </div>

      <!-- 推荐动词替换 -->
      <div class="section vocabulary-section">
        <h2>📚 推荐动词替换</h2>
        <div class="vocabulary-grid">
          <div class="vocab-header">
            <div>原文词汇</div>
            <div>升级方案</div>
            <div>场景示例</div>
          </div>
          <div 
            v-for="(item, index) in vocabularyReplacements" 
            :key="index"
            class="vocab-row"
          >
            <div class="original">{{ item.original }}</div>
            <div class="upgraded">{{ item.upgraded }}</div>
            <div class="example">{{ item.example }}</div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="no-data">
      <p>点击"加载演示数据"查看分析结果</p>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'ResumeAnalysisDemo',
  setup() {
    const analysisData = ref(null)

    // 演示数据
    const demoData = {
      end_resume: `## 匹配度雷达
- **岗位契合度**：89/100  
- **关键词命中**：用户增长(7)、A/B测试(5)、漏斗优化(6)、SQL建模(4)、跨部门协作(8)、数据驱动决策(9)、ROI提升(3)、裂变活动策划(5)、留存率↑15%(2)、LTV↑22%(1)  
- **ATS可读性**：76/100（因部分描述过长且缺乏标准术语）

---

## 亮点提炼
- 💡 搭建用户分层标签体系，使精准推送点击率↑38%，季度GMV贡献超1200万  
- 🚀 主导暑期拉新战役，通过裂变机制设计实现DAU周环比↑65%，获CEO专项表彰  
- 📊 重构数据看板逻辑，缩短市场部决策周期从5天→2天，复盘效率提升70%

---

## 风险预警
- **缺失信息**：未提及具体工具（如Mixpanel/神策）、预算管控经验、英语能力等级  
- **易扣分项**：①连续4次使用"参与"（弱化主动性）；②"很多""较好"等模糊表述；③项目起止月份缺失导致经历断层疑云  
- **隐性雷区**："熟悉Python"却无脚本落地案例，可能被质疑技能真实性

---

## 即刻优化
模板：  
> 原句：「参与用户画像项目，效果比较好」  
> ✅ 改为：「构建RFM分层模型驱动定向触达，使次月复购率↑22%，相关经验复用于3个业务线」

---

## 推荐动词替换
| 原文词汇 | 升级方案                | 场景示例                          |
|----------|------------------------|-----------------------------------|
| 负责      | orchestrated            | Orchestrated A/B test matrix across 5 product lines |
| 完成      | engineered              | Engineered scalable tag management system handling 1M+ daily events |
| 帮助      | accelerated             | Accelerated customer onboarding funnel by optimizing CTAs based on heatmap analysis |`
    }

    // 解析数据的计算属性
    const radarData = computed(() => {
      if (!analysisData.value) return {}
      
      const content = analysisData.value.end_resume || ''
      
      // 解析岗位契合度
      const jobMatchMatch = content.match(/岗位契合度.*?(\d+)\/100/)
      const jobMatch = jobMatchMatch ? parseInt(jobMatchMatch[1]) : 0
      
      // 解析ATS可读性
      const atsMatch = content.match(/ATS可读性.*?(\d+)\/100/)
      const atsReadability = atsMatch ? parseInt(atsMatch[1]) : 0
      
      // 解析关键词
      const keywordsMatch = content.match(/关键词命中.*?：(.*?)(?=\n|$)/)
      const keywords = []
      if (keywordsMatch) {
        const keywordStr = keywordsMatch[1]
        const keywordMatches = keywordStr.match(/([^(]+)\((\d+)\)/g)
        if (keywordMatches) {
          keywordMatches.forEach(match => {
            const parts = match.match(/([^(]+)\((\d+)\)/)
            if (parts) {
              keywords.push({
                name: parts[1].trim(),
                count: parseInt(parts[2])
              })
            }
          })
        }
      }
      
      return { jobMatch, atsReadability, keywords }
    })

    const highlights = computed(() => {
      if (!analysisData.value) return []
      
      const content = analysisData.value.end_resume || ''
      const highlightSection = content.match(/## 亮点提炼\n(.*?)(?=\n---|\n##|$)/s)
      
      if (!highlightSection) return []
      
      const highlightLines = highlightSection[1].split('\n').filter(line => line.trim().startsWith('-'))
      
      return highlightLines.map(line => {
        const match = line.match(/- ([💡🚀📊]) (.+)/)
        return match ? {
          icon: match[1],
          text: match[2]
        } : null
      }).filter(Boolean)
    })

    const risks = computed(() => {
      if (!analysisData.value) return {}
      
      const content = analysisData.value.end_resume || ''
      const riskSection = content.match(/## 风险预警\n(.*?)(?=\n---|\n##|$)/s)
      
      if (!riskSection) return {}
      
      const riskContent = riskSection[1]
      
      const missingMatch = riskContent.match(/缺失信息.*?：(.*?)(?=\n|$)/)
      const deductionsMatch = riskContent.match(/易扣分项.*?：(.*?)(?=\n|$)/)
      const hiddenMatch = riskContent.match(/隐性雷区.*?：(.*?)(?=\n|$)/)
      
      return {
        missing: missingMatch ? missingMatch[1].trim() : '',
        deductions: deductionsMatch ? deductionsMatch[1].trim() : '',
        hidden: hiddenMatch ? hiddenMatch[1].trim() : ''
      }
    })

    const optimization = computed(() => {
      if (!analysisData.value) return {}
      
      const content = analysisData.value.end_resume || ''
      const optimizationSection = content.match(/## 即刻优化\n(.*?)(?=\n---|\n##|$)/s)
      
      if (!optimizationSection) return {}
      
      const beforeMatch = optimizationSection[1].match(/原句：「(.*?)」/)
      const afterMatch = optimizationSection[1].match(/✅ 改为：「(.*?)」/)
      
      return {
        before: beforeMatch ? beforeMatch[1] : '',
        after: afterMatch ? afterMatch[1] : ''
      }
    })

    const vocabularyReplacements = computed(() => {
      if (!analysisData.value) return []
      
      const content = analysisData.value.end_resume || ''
      const vocabSection = content.match(/## 推荐动词替换\n(.*?)(?=\n##|$)/s)
      
      if (!vocabSection) return []
      
      const tableRows = vocabSection[1].split('\n').filter(line => line.includes('|') && !line.includes('---'))
      
      return tableRows.slice(1).map(row => {
        const cells = row.split('|').map(cell => cell.trim()).filter(cell => cell)
        return cells.length >= 3 ? {
          original: cells[0],
          upgraded: cells[1],
          example: cells[2]
        } : null
      }).filter(Boolean)
    })

    // 方法
    const loadDemoData = () => {
      analysisData.value = demoData
    }

    const getKeywordLevel = (count) => {
      if (count >= 7) return 'high'
      if (count >= 4) return 'medium'
      return 'low'
    }

    return {
      analysisData,
      radarData,
      highlights,
      risks,
      optimization,
      vocabularyReplacements,
      loadDemoData,
      getKeywordLevel
    }
  }
}
</script>

<style scoped>
.resume-analysis-demo {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.demo-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.demo-header h1 {
  margin: 0 0 15px 0;
  font-size: 2rem;
}

.demo-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.demo-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.analysis-result {
  display: grid;
  gap: 25px;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

.section h2 {
  margin: 0 0 20px 0;
  color: #2d3748;
  font-size: 1.5rem;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 10px;
}

/* 匹配度雷达 */
.score-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 25px;
}

.score-card {
  text-align: center;
  padding: 25px;
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.score-card.primary {
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
}

.score-card.secondary {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
}

.score-number {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 8px;
}

.score-label {
  font-size: 1rem;
  opacity: 0.9;
}

.keywords-area h3 {
  color: #4a5568;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.keywords-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.keyword-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.keyword-badge.high {
  background: #c6f6d5;
  color: #22543d;
  border: 1px solid #9ae6b4;
}

.keyword-badge.medium {
  background: #fef5e7;
  color: #c05621;
  border: 1px solid #f6e05e;
}

.keyword-badge.low {
  background: #fed7d7;
  color: #c53030;
  border: 1px solid #fc8181;
}

/* 亮点提炼 */
.highlights-grid {
  display: grid;
  gap: 15px;
}

.highlight-card {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 20px;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-radius: 10px;
  border-left: 4px solid #4299e1;
  transition: transform 0.2s ease;
}

.highlight-card:hover {
  transform: translateX(5px);
}

.highlight-emoji {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.highlight-content {
  margin: 0;
  color: #2d3748;
  line-height: 1.6;
}

/* 风险预警 */
.risks-grid {
  display: grid;
  gap: 20px;
}

.risk-item {
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%);
  border-radius: 10px;
  border-left: 4px solid #f56565;
}

.risk-item h4 {
  color: #c53030;
  margin: 0 0 10px 0;
  font-size: 1.1rem;
}

.risk-item p {
  color: #2d3748;
  margin: 0;
  line-height: 1.6;
}

/* 即刻优化 */
.optimization-container {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 20px;
  align-items: center;
}

.before-example,
.after-example {
  padding: 20px;
  border-radius: 10px;
}

.before-example {
  background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%);
  border-left: 4px solid #f56565;
}

.after-example {
  background: linear-gradient(135deg, #f0fff4 0%, #c6f6d5 100%);
  border-left: 4px solid #38a169;
}

.before-example h4,
.after-example h4 {
  margin: 0 0 10px 0;
  font-size: 1rem;
}

.before-example h4 {
  color: #c53030;
}

.after-example h4 {
  color: #38a169;
}

.before-example p,
.after-example p {
  margin: 0;
  line-height: 1.6;
  color: #2d3748;
}

.arrow {
  font-size: 2rem;
  color: #4299e1;
  font-weight: bold;
  text-align: center;
}

/* 推荐动词替换 */
.vocabulary-grid {
  display: grid;
  gap: 2px;
  background: #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.vocab-header {
  display: grid;
  grid-template-columns: 1fr 1fr 2fr;
  background: #4a5568;
  color: white;
  font-weight: 600;
}

.vocab-header > div {
  padding: 15px;
}

.vocab-row {
  display: grid;
  grid-template-columns: 1fr 1fr 2fr;
  background: white;
}

.vocab-row:nth-child(even) {
  background: #f7fafc;
}

.vocab-row > div {
  padding: 15px;
  border-right: 1px solid #e2e8f0;
}

.vocab-row > div:last-child {
  border-right: none;
}

.original {
  color: #e53e3e;
  font-weight: 500;
}

.upgraded {
  color: #38a169;
  font-weight: 500;
}

.example {
  color: #4a5568;
  font-style: italic;
  line-height: 1.5;
}

.no-data {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
  font-size: 1.1rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-analysis-demo {
    padding: 15px;
  }

  .section {
    padding: 20px;
  }

  .score-grid {
    grid-template-columns: 1fr;
  }

  .optimization-container {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .arrow {
    transform: rotate(90deg);
  }

  .vocab-header,
  .vocab-row {
    grid-template-columns: 1fr;
  }

  .vocab-header > div,
  .vocab-row > div {
    border-right: none;
    border-bottom: 1px solid #e2e8f0;
  }

  .vocab-header > div:last-child,
  .vocab-row > div:last-child {
    border-bottom: none;
  }
}
</style>
