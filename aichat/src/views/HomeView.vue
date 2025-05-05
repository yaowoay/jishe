<template>
  <div class="home">
    <!-- 主展示区 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1>产品名字</h1>
        <p>文字，产品主要亮点介绍</p>
        <div class="hero-buttons">
          <el-button type="primary" class="primary-btn" @click="goToLogin">立即体验</el-button>
          <el-button class="outlined-btn">视频演示</el-button>
        </div>
      </div>
    </div>

    <!-- 核心功能区 -->
    <div class="features-section">
      <h2>核心功能</h2>
      <div class="features-container">
        <FeatureCard
          v-for="(feature, index) in features"
          :key="index"
          :title="feature.title"
          :description="feature.description"
          :image="feature.image"
        />
      </div>
    </div>

    <!-- 职业进阶之路 -->
    <div class="career-path-section">
      <div class="section-header">
        <el-image :src="careerPathImage" class="path-image" />
      </div>
      <div class="path-content">
        <h2>你的职业进阶之路</h2>
        <p>清晰的职业成长路径，助你从职场小白到精英，实现职业跃迁。</p>
        
        <div class="steps-container">
          <StepItem
            v-for="(step, index) in careerSteps"
            :key="index"
            :number="step.number"
            :title="step.title"
            :description="step.description"
            :points="step.points"
          />
        </div>
      </div>
    </div>

    <!-- 数据安全区 -->
    <div class="safety-section">
      <div class="safety-content">
        <div class="safety-text">
          <h2>您的数据安全，我们来守护</h2>
          <p>我们采用先进的加密技术和严格的安全措施，确保您的个人信息安全无忧。</p>
          <div class="safety-points">
            <SafetyPoint
              v-for="(point, index) in safetyPoints"
              :key="index"
              :icon="point.icon"
              :title="point.title"
              :description="point.description"
            />
          </div>
        </div>
        <div class="safety-image">
          <el-image :src="safetyImage" class="safety-img" />
        </div>
      </div>
    </div>

    <!-- 底部呼吁区 -->
    <div class="cta-section">
      <div class="cta-content">
        <h3>加图</h3>
        <p>Web 1920 1</p>
        <el-button type="primary" class="cta-button">马上体验！</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import FeatureCard from '@/components/feature/FeatureCard.vue'
import StepItem from '@/components/feature/StepItem.vue'
import SafetyPoint from '@/components/feature/SafetyPoint.vue'
import { useRouter } from 'vue-router';
export default {
  name: 'HomeView',
 
  components: {
    FeatureCard,
    StepItem,
    SafetyPoint
  },
  setup() {
    const router = useRouter();

    const goToLogin = () => {
      router.push('/login');
    };

    return {
      goToLogin
    };
  },
  data() {
    return {
      features: [
        {
          title: '智能简历诊断',
          description: '基于深度学习的简历检核技术，精准识别求职者能力项，给予专业的改进建议',
          image: require('@/assets/images/feature1.png')
        },
        {
          title: '多模态面试评测',
          description: '实时分析仪态表情，语音语调，答题内容，提供全方位的面试表现评估',
          image: require('@/assets/images/feature2.png')
        },
        {
          title: '个性化学习路径',
          description: '智能分析结果，智能推荐针对性的学习资料和练习内容，助力快速提升',
          image: require('@/assets/images/feature3.png')
        }
      ],
      careerSteps: [
        {
          number: '1',
          title: '评估与诊断',
          points: ['通过AI评估，全面了解你的水平与潜力', '精准诊断你的职业发展与提升空间']
        },
        {
          number: '2',
          title: '个性化方案',
          points: ['提供专业提升建议', '智能推荐学习资源']
        },
        {
          number: '3',
          title: '模拟与提升',
          points: ['真实场景模拟', '专业反馈分析']
        },
        {
          number: '4',
          title: '成长与进阶',
          points: ['实时进步跟踪', '助力职业提升']
        }
      ],
      safetyPoints: [
        {
          icon: 'lock',
          title: '数据加密存储',
          description: '数据加密存储，防止未授权的访问'
        },
        {
          icon: 'shield',
          title: '严格遵守隐私政策',
          description: '严格遵守隐私政策，保障您的个人数据'
        }
      ],
      careerPathImage: require('@/assets/images/path.png'),
      safetyImage: require('@/assets/images/safety.png')
    }
  }
}
</script>