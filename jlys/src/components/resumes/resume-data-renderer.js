// 简历分析数据渲染脚本
class ResumeAnalysisRenderer {
  constructor() {
    this.data = null
    this.chart = null
  }

  // 获取数据
  getData() {
    // 使用内联数据，如果没有则返回默认数据
    if (window.RESUME_DATA) {
      return window.RESUME_DATA
    }
        
    // 默认数据
    return {
      basicInfo: { name: '示例姓名', position: '前端工程师', experience: '3年', education: '本科' },
      overallScore: { score: 75, level: '良好', description: '简历与岗位需求匹配度较高' },
      abilityRadar: { 
        labels: ['技术能力', '项目经验', '学习能力', '沟通协作', '问题解决', '创新思维'],
        currentLevel: [7, 6, 8, 6, 7, 6],
        requiredLevel: [8, 7, 8, 7, 8, 6]
      },
      strengths: ['具备前端开发经验', '技术学习能力强'],
      weaknesses: ['项目经验需要提升', '团队协作待加强'],
      atsAnalysis: [
        { item: '关键词覆盖率', status: '良好', statusClass: 'status-pass', suggestion: '技术关键词覆盖较好' }
      ],
      hardSkills: [
        { skill: 'JavaScript', status: '熟练', statusClass: 'status-pass', suggestion: '基础扎实' }
      ],
      softSkills: [
        { skill: '沟通能力', status: '良好', statusClass: 'status-pass', suggestion: '表达清晰' }
      ],
      suggestions: [
        { icon: 'fas fa-code', title: '提升技能', content: '继续学习新技术' }
      ],
      insights: { title: '分析总结', content: ['整体表现良好，有提升空间'] }
    }
  }

  // 初始化数据
  init(data = null) {
    this.data = data || this.getData()
    this.renderAll()
  }

  // 渲染所有内容
  renderAll() {
    this.renderOverallScore()
    this.renderStrengthsWeaknesses()
    this.renderATSAnalysis()
    this.renderHardSkills()
    this.renderSoftSkills()
    this.renderSuggestions()
    this.renderInsights()
    this.renderRadarChart()
  }

  // 渲染整体评分
  renderOverallScore() {
    const scoreValue = document.querySelector('.score-value')
    const progressBar = document.getElementById('match-progress')
    const targetScore = this.data.overallScore.score
        
    // 动画更新分数
    setTimeout(() => {
      let currentScore = 0
      const increment = targetScore / 60
      const scoreInterval = setInterval(() => {
        currentScore += increment
        if (currentScore >= targetScore) {
          currentScore = targetScore
          clearInterval(scoreInterval)
        }
        scoreValue.textContent = Math.round(currentScore) + '%'
        progressBar.style.width = currentScore + '%'
      }, 16)
    }, 500)
  }

  // 渲染优势和劣势
  renderStrengthsWeaknesses() {
    const strengthsList = document.querySelector('.strengths ul')
    const weaknessesList = document.querySelector('.weaknesses ul')
        
    // 清空现有内容
    strengthsList.innerHTML = ''
    weaknessesList.innerHTML = ''
        
    // 渲染优势
    this.data.strengths.forEach(strength => {
      const li = document.createElement('li')
      li.textContent = strength
      strengthsList.appendChild(li)
    })
        
    // 渲染劣势
    this.data.weaknesses.forEach(weakness => {
      const li = document.createElement('li')
      li.textContent = weakness
      weaknessesList.appendChild(li)
    })
  }

  // 渲染ATS分析表格
  renderATSAnalysis() {
    const tbody = document.querySelector('.analysis-table tbody')
    const tables = document.querySelectorAll('.analysis-table')
        
    if (tables.length > 0) {
      const atsTable = tables[0]
      const tbody = atsTable.querySelector('tbody')
      this.renderTable(tbody, this.data.atsAnalysis)
    }
  }

  // 渲染硬技能表格
  renderHardSkills() {
    const tables = document.querySelectorAll('.analysis-table')
    if (tables.length > 1) {
      const hardSkillsTable = tables[1]
      const tbody = hardSkillsTable.querySelector('tbody')
      this.renderTable(tbody, this.data.hardSkills, 'skill')
    }
  }

  // 渲染软技能表格
  renderSoftSkills() {
    const tables = document.querySelectorAll('.analysis-table')
    if (tables.length > 2) {
      const softSkillsTable = tables[2]
      const tbody = softSkillsTable.querySelector('tbody')
      this.renderTable(tbody, this.data.softSkills, 'skill')
    }
  }

  // 通用表格渲染方法
  renderTable(tbody, data, nameKey = 'item') {
    tbody.innerHTML = ''
        
    data.forEach(item => {
      const row = document.createElement('tr')
      row.innerHTML = `
                <td>${item[nameKey]}</td>
                <td class="${item.statusClass}">${item.status}</td>
                <td>${item.suggestion}</td>
            `
      tbody.appendChild(row)
    })
  }

  // 渲染建议列表
  renderSuggestions() {
    const suggestionsList = document.querySelector('.suggestion-list')
    suggestionsList.innerHTML = ''
        
    this.data.suggestions.forEach(suggestion => {
      const li = document.createElement('li')
      li.className = 'suggestion-item'
      li.innerHTML = `
                <div class="suggestion-icon">
                    <i class="${suggestion.icon}"></i>
                </div>
                <div class="suggestion-content">
                    <div class="suggestion-title">${suggestion.title}</div>
                    <p>${suggestion.content}</p>
                </div>
            `
      suggestionsList.appendChild(li)
    })
  }

  // 渲染洞察内容
  renderInsights() {
    const insightContent = document.querySelector('.insight-content')
    insightContent.innerHTML = ''
        
    this.data.insights.content.forEach(paragraph => {
      const p = document.createElement('p')
      p.innerHTML = paragraph
      insightContent.appendChild(p)
    })
  }

  // 渲染雷达图
  renderRadarChart() {
    const ctx = document.getElementById('radar-chart').getContext('2d')
        
    // 如果已存在图表，先销毁
    if (this.chart) {
      this.chart.destroy()
    }
        
    this.chart = new Chart(ctx, {
      type: 'radar',
      data: {
        labels: this.data.abilityRadar.labels,
        datasets: [{
          label: '当前水平',
          data: this.data.abilityRadar.currentLevel,
          backgroundColor: 'rgba(44, 111, 187, 0.2)',
          borderColor: 'rgba(44, 111, 187, 1)',
          pointBackgroundColor: 'rgba(44, 111, 187, 1)',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: 'rgba(44, 111, 187, 1)'
        }, {
          label: '岗位要求',
          data: this.data.abilityRadar.requiredLevel,
          backgroundColor: 'rgba(52, 152, 219, 0.2)',
          borderColor: 'rgba(52, 152, 219, 1)',
          pointBackgroundColor: 'rgba(52, 152, 219, 1)',
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: 'rgba(52, 152, 219, 1)'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          r: {
            angleLines: {
              display: true
            },
            suggestedMin: 0,
            suggestedMax: 10,
            pointLabels: {
              font: {
                size: 12
              }
            },
            ticks: {
              display: false
            }
          }
        },
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              padding: 20
            }
          }
        }
      }
    })
  }

  // 更新数据并重新渲染
  updateData(newData) {
    this.data = { ...this.data, ...newData }
    this.renderAll()
  }

  // 导出数据
  exportData() {
    return JSON.stringify(this.data, null, 2)
  }

  // 从JSON字符串加载数据
  loadFromJSON(jsonString) {
    try {
      const data = JSON.parse(jsonString)
      this.init(data)
      return true
    } catch (error) {
      console.error('JSON数据解析失败:', error)
      return false
    }
  }
}

// 全局实例
window.resumeRenderer = new ResumeAnalysisRenderer()

// 页面加载完成后自动初始化
document.addEventListener('DOMContentLoaded', function() {
  // 初始化渲染器，使用内联数据
  window.resumeRenderer.init()
    
  // 保留原有的交互效果
  addTableHoverEffects()
  addSuggestionClickEffects()
})

// 添加表格悬停效果
function addTableHoverEffects() {
  const tableRows = document.querySelectorAll('.analysis-table tbody tr')
  tableRows.forEach(row => {
    row.addEventListener('mouseenter', function() {
      this.style.backgroundColor = '#e3f2fd'
      this.style.transform = 'translateX(5px)'
      this.style.transition = 'all 0.3s ease'
    })
        
    row.addEventListener('mouseleave', function() {
      this.style.backgroundColor = ''
      this.style.transform = 'translateX(0)'
    })
  })
}

// 添加建议项点击展开效果
function addSuggestionClickEffects() {
  const suggestionItems = document.querySelectorAll('.suggestion-item')
  suggestionItems.forEach(item => {
    item.addEventListener('click', function() {
      const content = this.querySelector('.suggestion-content p')
      if (content.style.maxHeight) {
        content.style.maxHeight = null
        content.style.overflow = 'hidden'
      } else {
        content.style.maxHeight = content.scrollHeight + 'px'
        content.style.overflow = 'visible'
      }
    })
  })
}

// 提供一些便捷的API
window.ResumeAPI = {
  // 更新整体评分
  updateScore: (score) => {
    window.resumeRenderer.updateData({
      overallScore: { ...window.resumeRenderer.data.overallScore, score }
    })
  },
    
  // 更新能力数据
  updateAbilities: (currentLevel, requiredLevel) => {
    window.resumeRenderer.updateData({
      abilityRadar: {
        ...window.resumeRenderer.data.abilityRadar,
        currentLevel,
        requiredLevel
      }
    })
  },
    
  // 添加优势
  addStrength: (strength) => {
    const strengths = [...window.resumeRenderer.data.strengths, strength]
    window.resumeRenderer.updateData({ strengths })
  },
    
  // 添加改进建议
  addSuggestion: (suggestion) => {
    const suggestions = [...window.resumeRenderer.data.suggestions, suggestion]
    window.resumeRenderer.updateData({ suggestions })
  },
    
  // 获取当前数据
  getData: () => window.resumeRenderer.data,
    
  // 导出数据
  export: () => window.resumeRenderer.exportData(),
    
  // 从JSON加载
  loadJSON: (jsonString) => window.resumeRenderer.loadFromJSON(jsonString)
}