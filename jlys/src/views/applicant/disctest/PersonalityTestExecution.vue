<template>
  <div class="personality-test-execution">
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <h2>{{ testInfo.name }}</h2>
          <el-tag type="info" size="large">{{ testInfo.status }}</el-tag>
        </div>
      </template>
      
      <div class="test-content">
        <div class="test-info">
          <h3>测试说明</h3>
          <p>{{ testInfo.description }}</p>
          
          <div class="test-details">
            <div class="detail-item">
              <i class="el-icon-time"></i>
              <span>预计用时：{{ testInfo.duration }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-document"></i>
              <span>题目数量：{{ testInfo.questionCount }}</span>
            </div>
          </div>
        </div>
        
        <div class="coming-soon">
          <el-result
            icon="info"
            title="功能开发中"
            sub-title="该测试功能正在开发中，敬请期待！"
          >
            <template #extra>
              <el-button type="primary" @click="goBack">返回测试选择</el-button>
              <el-button @click="tryDiscTest">体验DISC测试</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'PersonalityTestExecution',
  data() {
    return {
      testInfo: {
        name: '测试加载中...',
        description: '',
        duration: '',
        questionCount: '',
        status: '开发中'
      }
    }
  },
  
  created() {
    this.loadTestInfo()
  },
  
  methods: {
    loadTestInfo() {
      const testId = this.$route.params.testId
      
      // 根据testId获取测试信息
      const testTypes = {
        1: {
          name: 'MBTI 职业性格测试',
          description: '基于荣格心理类型理论，通过16种人格类型分析您的性格特征和职业倾向。',
          duration: '15-20分钟',
          questionCount: '60题',
          status: '开发中'
        },
        2: {
          name: '大五人格测试',
          description: '通过开放性、尽责性、外向性、宜人性、神经质五个维度全面评估您的人格特征。',
          duration: '10-15分钟',
          questionCount: '50题',
          status: '开发中'
        },
        3: {
          name: '霍兰德职业兴趣测试',
          description: '通过六大职业类型（现实型、研究型、艺术型、社会型、企业型、常规型）分析您的职业兴趣。',
          duration: '15-20分钟',
          questionCount: '60题',
          status: '开发中'
        },
        4: {
          name: '职场能力评估',
          description: '综合评估您在沟通协调、团队合作、领导力、创新思维、抗压能力等方面的表现。',
          duration: '20-25分钟',
          questionCount: '80题',
          status: '开发中'
        }
      }
      
      this.testInfo = testTypes[testId] || {
        name: '未知测试',
        description: '测试信息加载失败',
        duration: '未知',
        questionCount: '未知',
        status: '错误'
      }
    },
    
    goBack() {
      this.$router.push({ name: 'PersonalityTest' })
    },
    
    tryDiscTest() {
      this.$router.push({ name: 'DiscTest' })
    }
  }
}
</script>

<style scoped>
.personality-test-execution {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.test-content {
  padding: 20px 0;
}

.test-info {
  margin-bottom: 40px;
}

.test-info h3 {
  color: #409eff;
  margin-bottom: 15px;
}

.test-info p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}

.test-details {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  color: #909399;
}

.detail-item i {
  margin-right: 8px;
  font-size: 16px;
}

.coming-soon {
  text-align: center;
  padding: 40px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .personality-test-execution {
    padding: 10px;
  }
  
  .test-details {
    flex-direction: column;
    gap: 15px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>
