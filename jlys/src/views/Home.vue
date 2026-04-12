<template>
  <div class="home-root">
    <!-- 新头部 -->
    <div class="main-header">
      <div class="header-left">
        <div class="logo">
          <i class="fas fa-robot"></i>
          <span>智面星图</span>
        </div>
      </div>
      <div class="header-right">
        <a href="#" class="btn btn-login" @click="$router.push('/login')">登录</a>
      </div>
    </div>
    <!-- 主视觉区 -->
    <div class="main-content">
      <div class="center-content">
        <h1>获得你的面试<span class="highlight">超级Buff</span></h1>
        <!-- AI数字人头像卡片 -->
        <div class="ai-avatar-card">
          <img src="@/assets/images/logo.png" alt="AI面试官" class="ai-avatar" />
          <div class="avatar-glow"></div>
        </div>
        <div class="ai-subtitle">智面星图助力你的求职之路</div>
        <p>AI驱动的多模态智能面试系统，通过数字人面试官、实时表情分析和个性化评估报告，助你轻松应对各种面试挑战！</p>
        <div class="hero-cta">
          <a class="btn btn-register btn-main" @click="goToRegister">立即体验</a>
          <a href="#" class="btn btn-demo btn-main">观看演示</a>
        </div>
      </div>
    </div>
    <!-- 恢复原有内容区块 -->
    <section class="features">
      <div class="container">
        <div class="section-title">
          <h2>核心功能</h2>
          <p>全方位提升面试能力的智能工具集合</p>
        </div>
        <div class="feature-cards">
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-user-tie"></i>
            </div>
            <h3>AI数字人面试官</h3>
            <p>智能数字人面试官，支持多种岗位角色，还原真实面试场景</p>
            <a href="#" class="btn btn-login" @click="$router.push('/layout/simulatExam')">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-file-alt"></i>
            </div>
            <h3>智能简历分析</h3>
            <p>AI优化简历内容，精准匹配职位要求，一键生成专业级简历</p>
            <a href="#" class="btn btn-login" @click="$router.push('/layout/editResume')">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-chart-line"></i>
            </div>
            <h3>多模态分析</h3>
            <p>综合语音、表情、行为等多维度数据，智能分析面试表现和心理状态</p>
            <a href="#" class="btn btn-login">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-clipboard-list"></i>
            </div>
            <h3>三维评估报告</h3>
            <p>面试后生成详细报告，包含技术能力、表达能力和问题解决能力分析</p>
            <a href="#" class="btn btn-login" @click="$router.push('/test-assess-report-v2')">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-brain"></i>
            </div>
            <h3>智能笔试系统</h3>
            <p>基于AI的个性化笔试题目生成，智能推荐专属学习路径</p>
            <a href="#" class="btn btn-login" @click="$router.push('/layout/beforeWritten')">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-laptop-code"></i>
            </div>
            <h3>OJ刷题系统</h3>
            <p>集成企业真题题库，实时评判系统，针对性提升技术能力</p>
            <a href="#" class="btn btn-login" @click="$router.push('/layout/oj')">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-building"></i>
            </div>
            <h3>企业招聘管理</h3>
            <p>职位发布管理、应聘者跟踪、面试安排，全流程招聘数据分析</p>
            <a href="#" class="btn btn-login" @click="$router.push('/company')">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-users"></i>
            </div>
            <h3>候选人管理</h3>
            <p>智能简历筛选、批量面试安排、候选人评分排序和状态跟踪</p>
            <a href="#" class="btn btn-login" @click="$router.push('/company/applicants')">了解更多</a>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-chart-bar"></i>
            </div>
            <h3>招聘数据分析</h3>
            <p>面试通过率统计、招聘效率分析、人才质量评估报告</p>
            <a href="#" class="btn btn-login" @click="$router.push('/company/analytics')">了解更多</a>
          </div>
        </div>
      </div>
    </section>
    <section class="user-types">
      <div class="container">
        <div class="section-title">
          <h2>双重价值体系</h2>
          <p>为企业和求职者提供全方位智能化服务</p>
        </div>
        <div class="value-grid">
          <div class="value-section jobseeker-section">
            <div class="value-header">
              <div class="value-badge jobseeker-badge">
                <i class="fas fa-user-graduate"></i>
                <span>求职者</span>
              </div>
              <h3>全面提升面试竞争力</h3>
              <p>从简历优化到面试技巧，AI助力求职成功</p>
            </div>
            <div class="value-features">
              <div class="feature-item">
                <i class="fas fa-robot"></i>
                <span>AI面试官实战训练</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-file-alt"></i>
                <span>智能简历优化建议</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-route"></i>
                <span>个性化学习路径</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-chart-line"></i>
                <span>多模态智能分析反馈</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-clipboard-check"></i>
                <span>专业技能评估测试</span>
              </div>
            </div>
            <div class="value-action">
              <a href="#" class="btn-value jobseeker-btn" @click="$router.push('/register?type=applicant')">
                <span>开始求职之旅</span>
                <i class="fas fa-arrow-right"></i>
              </a>
            </div>
          </div>

          <div class="value-section enterprise-section">
            <div class="value-header">
              <div class="value-badge enterprise-badge">
                <i class="fas fa-briefcase"></i>
                <span>企业HR</span>
              </div>
              <h3>高效智能的招聘管理</h3>
              <p>从职位发布到人才筛选，AI赋能招聘全流程</p>
            </div>
            <div class="value-features">
              <div class="feature-item">
                <i class="fas fa-building"></i>
                <span>职位发布与管理</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-search"></i>
                <span>AI简历智能筛选</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-calendar-alt"></i>
                <span>批量面试安排</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-sort-amount-up"></i>
                <span>候选人评分排序</span>
              </div>
              <div class="feature-item">
                <i class="fas fa-chart-bar"></i>
                <span>招聘数据深度分析</span>
              </div>
            </div>
            <div class="value-action">
              <a href="#" class="btn-value enterprise-btn" @click="$router.push('/register?type=company')">
                <span>开启智能招聘</span>
                <i class="fas fa-arrow-right"></i>
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>
    <section class="interview-modes">
      <div class="container">
        <div class="section-title">
          <h2>双模面试系统</h2>
          <p>从学习到实战，满足不同阶段需求</p>
        </div>
        <div class="mode-container">
          <div class="mode-card simulate">
            <div class="mode-icon">
              <i class="fas fa-hands-helping"></i>
            </div>
            <h3>模拟面试模式</h3>
            <p>AI辅助学习模式，逐步提升面试能力</p>
            <ul>
              <li>实时AI提示与参考答案</li>
              <li>分步骤答题引导</li>
              <li>面试技巧即时指导</li>
              <li>错误分析与改进建议</li>
              <li>低压力练习环境</li>
            </ul>
            <a href="#" class="btn btn-register" @click="$router.push('/layout/simulatExam')">开启训练</a>
          </div>
          <div class="mode-card formal">
            <div class="mode-icon">
              <i class="fas fa-bullseye"></i>
            </div>
            <h3>正式面试模式</h3>
            <p>真实面试环境，全面检验准备成果</p>
            <ul>
              <li>全真模拟企业面试流程</li>
              <li>无AI辅助的实战环境</li>
              <li>多轮次压力面试</li>
              <li>完整录像与回放功能</li>
              <li>企业级评估标准</li>
            </ul>
            <a href="#" class="btn btn-register" @click="$router.push('/layout/offecialExam')">挑战自我</a>
          </div>
        </div>
      </div>
    </section>
    <section class="process">
      <div class="container">
        <div class="section-title">
          <h2>智能招聘全流程</h2>
          <p>从人才发现到精准匹配，AI赋能的完整招聘解决方案</p>
        </div>
        <div class="steps">
          <div class="step">
            <div class="step-number">1</div>
            <h4>智能简历匹配</h4>
            <p>AI分析简历与职位匹配度，精准筛选优质候选人</p>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <h4>多模态面试评估</h4>
            <p>数字人面试官+多维度分析，全面评估候选人能力</p>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <h4>智能决策支持</h4>
            <p>综合评估报告与数据分析，辅助HR做出最优决策</p>
          </div>
          <div class="step">
            <div class="step-number">4</div>
            <h4>持续优化改进</h4>
            <p>基于招聘数据反馈，不断优化匹配算法和评估标准</p>
          </div>
        </div>
      </div>
    </section>
    <footer>
      <div class="container">
        <div class="footer-content">
          <div class="footer-column">
            <div class="logo">
              <i class="fas fa-robot"></i>
              <span>AI面试官</span>
            </div>
            <p>AI赋能的面试训练平台，帮助求职者获得面试竞争优势，轻松斩获心仪Offer。</p>
            <div class="social-links">
              <a href="#"><i class="fab fa-weixin"></i></a>
              <a href="#"><i class="fab fa-zhihu"></i></a>
              <a href="#"><i class="fab fa-github"></i></a>
              <a href="#"><i class="fab fa-linkedin-in"></i></a>
            </div>
          </div>
          <div class="footer-column">
            <h3>产品功能</h3>
            <ul class="footer-links">
              <li><a href="#" @click="$router.push('/layout/editResume')">智能简历生成</a></li>
              <li><a href="#" @click="$router.push('/layout/simulatExam')">AI模拟面试</a></li>
              <li><a href="#" @click="$router.push('/layout/offecialExam')">正式面试挑战</a></li>
              <li><a href="#" @click="$router.push('/test-assess-report-v2')">能力评估报告</a></li>
              <li><a href="#" @click="$router.push('/layout/beforeWritten')">学习路线规划</a></li>
            </ul>
          </div>
          <div class="footer-column">
            <h3>学习资源</h3>
            <ul class="footer-links">
              <li><a href="#" @click="$router.push('/layout/oj')">面试题库</a></li>
              <li><a href="#">面经分享</a></li>
              <li><a href="#">行业趋势</a></li>
              <li><a href="#">求职指南</a></li>
              <li><a href="#">技能图谱</a></li>
            </ul>
          </div>
          <div class="footer-column">
            <h3>联系我们</h3>
            <ul class="footer-links">
              <li><a href="#">商务合作</a></li>
              <li><a href="#">企业服务</a></li>
              <li><a href="#">问题反馈</a></li>
              <li><a href="#">加入我们</a></li>
            </ul>
          </div>
        </div>
        <div class="copyright">
          <p>&copy; 2023 AI面试官系统 版权所有 | 京ICP备XXXXXX号</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'AIInterviewHome',
  mounted() {
    // 数字人动画效果
    const avatar = document.querySelector('.ai-avatar')
    if (avatar) {
      setInterval(() => {
        avatar.style.transform = 'translateY(-10px)'
        setTimeout(() => {
          avatar.style.transform = 'translateY(0)'
        }, 1000)
      }, 3000)
    }
    // 功能卡片动画
    const featureCards = document.querySelectorAll('.feature-card')
    featureCards.forEach((card, index) => {
      card.style.transitionDelay = `${index * 0.1}s`
    })
    // 滚动显示动画
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.style.opacity = 1
          entry.target.style.transform = 'translateY(0)'
        }
      })
    }, { threshold: 0.1 })
    document.querySelectorAll('.feature-card, .mode-card, .value-section, .step').forEach(el => {
      el.style.opacity = 0
      el.style.transform = 'translateY(20px)'
      el.style.transition = 'opacity 0.5s ease, transform 0.5s ease'
      observer.observe(el)
    })
  },
  methods: {
    goToRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style>
:root {
  --primary: #165DFF;      /* 主色：深蓝色 */
  --secondary: #4080FF;    /* 次要：中蓝色 */
  --accent: #69B1FF;       /* 强调：浅蓝色 */
  --light-blue: #E8F3FF;   /* 浅蓝背景 */
  --ultra-light: #F5F9FF;  /* 超浅蓝背景 */
  --dark: #1D2129;         /* 深色文本 */
  --gray: #86909C;         /* 灰色文本 */
  --white: #FFFFFF;        /* 白色 */
  --shadow: 0 4px 20px rgba(22, 93, 255, 0.1); /* 统一阴影 */
  --shadow-hover: 0 8px 30px rgba(22, 93, 255, 0.15); /* 悬停阴影 */
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
body {
  background: var(--ultra-light);
  color: var(--dark);
  overflow-x: hidden;
  line-height: 1.6;
}
.home-root {
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
  background: var(--ultra-light);
  color: var(--dark);
  overflow-x: hidden;
  line-height: 1.6;
}

/* 新头部和主视觉区样式 */
.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 60px;
  background: transparent;
}
.header-left .logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--primary);
}
.header-left .logo i {
  font-size: 2rem;
  color: var(--primary);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}
.btn {
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  font-size: 1rem;
}
.btn-login {
  background: transparent;
  border: 1px solid var(--primary);
  color: var(--primary);
}
.btn-login:hover {
  background: var(--primary);
  color: var(--white);
}
.btn-register {
  background: var(--primary);
  color: var(--white);
  border: none;
}
.btn-register:hover {
  background: #0E42D2;
  transform: translateY(-2px);
  box-shadow: var(--shadow);
}
.main-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, var(--ultra-light) 0%, var(--light-blue) 100%);
  position: relative;
  overflow: hidden;
}
/* 装饰元素 */
.main-content::before {
  content: '';
  position: absolute;
  top: -10%;
  right: -10%;
  width: 40%;
  height: 80%;
  background: rgba(105, 177, 255, 0.1);
  border-radius: 50%;
  z-index: 0;
}
.main-content::after {
  content: '';
  position: absolute;
  bottom: -15%;
  left: -10%;
  width: 30%;
  height: 60%;
  background: rgba(64, 128, 255, 0.08);
  border-radius: 50%;
  z-index: 0;
}
.center-content {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
  border-radius: 16px;
  padding: 60px 40px;
  position: relative;
  z-index: 1;
  background: var(--white);
  box-shadow: var(--shadow);
}
.center-content h1 {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 20px;
  color: var(--dark);
  line-height: 1.3;
}
.center-content .highlight {
  color: var(--primary);
  position: relative;
}
.center-content .highlight::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 0;
  width: 100%;
  height: 8px;
  background: rgba(22, 93, 255, 0.1);
  z-index: -1;
  border-radius: 4px;
}
.center-content p {
  font-size: 1.1rem;
  color: var(--gray);
  margin-bottom: 30px;
  line-height: 1.7;
}
.hero-cta {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 20px;
}
.btn-main {
  padding: 14px 32px;
  font-size: 1.05rem;
  font-weight: 600;
  border-radius: 8px;
}
.btn-demo {
  background: transparent;
  border: 1px solid var(--primary);
  color: var(--primary);
}
.btn-demo:hover {
  background: rgba(22, 93, 255, 0.05);
}
.ai-avatar-card {
  position: relative;
  width: 140px;
  height: 140px;
  margin: 0 auto 24px auto;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--light-blue) 0%, #D6E8FF 100%);
  box-shadow: 0 8px 24px rgba(22, 93, 255, 0.15);
  overflow: visible;
  z-index: 2;
  animation: avatar-float 3s ease-in-out infinite;
}
.ai-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  z-index: 2;
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
  background: var(--white);
  object-fit: cover;
  position: relative;
  border: 4px solid var(--white);
}
.avatar-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 160px;
  height: 160px;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  background: radial-gradient(circle, rgba(22, 93, 255, 0.2) 0%, rgba(64, 128, 255, 0.1) 60%, transparent 100%);
  filter: blur(12px);
  z-index: 1;
  pointer-events: none;
}
@keyframes avatar-float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}
.ai-subtitle {
  text-align: center;
  font-size: 1.1rem;
  color: var(--primary);
  margin-bottom: 16px;
  font-weight: 600;
}

/* 通用容器样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  width: 100%;
  box-sizing: border-box;
}

/* 功能区块样式 */
.features {
  padding: 100px 0;
  background: var(--white);
}
.section-title {
  text-align: center;
  margin-bottom: 60px;
  position: relative;
  padding-bottom: 20px;
}
.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: var(--primary);
  border-radius: 2px;
}
.section-title h2 {
  font-size: 2.2rem;
  margin-bottom: 15px;
  color: var(--dark);
  font-weight: 700;
}
.section-title p {
  color: var(--gray);
  font-size: 1.05rem;
  max-width: 600px;
  margin: 0 auto;
}
.feature-cards {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 30px;
}
.feature-card {
  background: var(--white);
  border-radius: 12px;
  padding: 30px;
  border: 1px solid #EEF2F7;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  width: 340px;
  min-height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: var(--shadow);
}
.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-hover);
  border-color: rgba(22, 93, 255, 0.1);
}
.feature-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, var(--primary), var(--accent));
}
.feature-icon {
  width: 64px;
  height: 64px;
  background: var(--light-blue);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}
.feature-icon i {
  font-size: 1.8rem;
  color: var(--primary);
}
.feature-card h3 {
  font-size: 1.3rem;
  margin-bottom: 16px;
  font-weight: 600;
  color: var(--dark);
  text-align: center;
}
.feature-card p {
  color: var(--gray);
  margin-bottom: 24px;
  font-size: 1rem;
  text-align: center;
  line-height: 1.6;
  flex-grow: 1;
}

/* 面试模式区块 */
.interview-modes {
  padding: 100px 0;
  background: var(--ultra-light);
  position: relative;
}
.mode-container {
  display: flex;
  gap: 30px;
  margin-top: 40px;
  justify-content: center;
  flex-wrap: wrap;
}
.mode-card {
  flex: 1;
  max-width: 420px;
  background: var(--white);
  border-radius: 16px;
  padding: 36px;
  text-align: center;
  border: 1px solid #EEF2F7;
  transition: all 0.3s ease;
  min-width: 300px;
  box-shadow: var(--shadow);
  position: relative;
}
.mode-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-hover);
  background: var(--white);
  color: var(--dark);
}
.mode-card.simulate {
  border-top: 4px solid var(--primary);
}
.mode-card.formal {
  border-top: 4px solid var(--secondary);
}
.mode-icon {
  font-size: 3rem;
  margin-bottom: 20px;
  color: var(--primary);
}
.formal .mode-icon {
  color: var(--secondary);
}
.mode-card h3 {
  font-size: 1.4rem;
  margin-bottom: 16px;
  font-weight: 600;
  color: var(--dark);
}
.mode-card p {
  color: var(--gray);
  font-size: 1rem;
  line-height: 1.6;
}
.mode-card ul {
  list-style: none;
  text-align: left;
  margin: 24px 0;
  padding: 0;
  color: var(--dark);
}
.mode-card ul li {
  margin-bottom: 12px;
  padding-left: 24px;
  position: relative;
  font-size: 1rem;
  color: var(--gray);
}
.mode-card ul li:before {
  content: '\f00c';
  font-family: 'Font Awesome 5 Free';
  font-weight: 900;
  position: absolute;
  left: 0;
  color: var(--primary);
}
.formal ul li:before {
  color: var(--secondary);
}

/* 用户类型区块 */
.user-types {
  padding: 100px 0;
  background: var(--white);
  position: relative;
}
.value-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 40px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.value-section {
  background: var(--ultra-light);
  border-radius: 16px;
  padding: 36px;
  box-shadow: var(--shadow);
  border: 1px solid #EEF2F7;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.value-section:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-hover);
}
.value-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--primary);
}
.enterprise-section::before {
  background: var(--secondary);
}
.value-header {
  margin-bottom: 28px;
}
.value-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 16px;
  background: rgba(22, 93, 255, 0.1);
  color: var(--primary);
  border: 1px solid rgba(22, 93, 255, 0.2);
}
.enterprise-badge {
  background: rgba(64, 128, 255, 0.1);
  color: var(--secondary);
  border: 1px solid rgba(64, 128, 255, 0.2);
}
.value-header h3 {
  font-size: 1.4rem;
  font-weight: 600;
  color: var(--dark);
  margin-bottom: 12px;
  line-height: 1.3;
}
.value-header p {
  color: var(--gray);
  font-size: 1rem;
  line-height: 1.6;
}
.value-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 28px;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--white);
  border-radius: 8px;
  transition: all 0.2s ease;
  border: 1px solid #EEF2F7;
}
.feature-item:hover {
  background: var(--light-blue);
  transform: translateX(4px);
  border-color: rgba(22, 93, 255, 0.1);
}
.feature-item i {
  width: 20px;
  color: var(--primary);
  font-size: 1rem;
}
.enterprise-section .feature-item i {
  color: var(--secondary);
}
.feature-item span {
  color: var(--dark);
  font-weight: 500;
  font-size: 0.95rem;
}
.value-action {
  text-align: center;
}
.btn-value {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 1rem;
  border: none;
  cursor: pointer;
  background: var(--primary);
  color: var(--white);
}
.btn-value:hover {
  background: #0E42D2;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(22, 93, 255, 0.2);
}
.enterprise-btn {
  background: var(--secondary);
}
.enterprise-btn:hover {
  background: #2E6BDE;
  box-shadow: 0 6px 16px rgba(64, 128, 255, 0.2);
}

/* 流程区块 */
.process {
  padding: 100px 0;
  background: var(--light-blue);
}
.steps {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 40px;
  position: relative;
  flex-wrap: wrap;
}
.steps:before {
  content: '';
  position: absolute;
  top: 40px;
  left: 10%;
  width: 80%;
  height: 2px;
  background: var(--primary);
  opacity: 0.3;
  z-index: 0;
}
.step {
  text-align: center;
  z-index: 1;
  position: relative;
  width: 220px;
  min-width: 180px;
  background: var(--white);
  border-radius: 12px;
  padding: 30px 18px 20px 18px;
  box-shadow: var(--shadow);
  border: 1px solid #EEF2F7;
  margin-bottom: 12px;
  transition: all 0.3s;
}
.step:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-hover);
}
.step-number {
  width: 60px;
  height: 60px;
  background: var(--primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0 auto 16px;
  color: var(--white);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
}
.step h4 {
  font-size: 1.1rem;
  margin-bottom: 12px;
  font-weight: 600;
  color: var(--dark);
}
.step p {
  color: var(--gray);
  font-size: 0.95rem;
  line-height: 1.6;
}

/* 页脚样式 */
footer {
  background: var(--dark);
  padding: 80px 0 40px;
  color: #C9CDD4;
}
.footer-content {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  align-items: start;
  gap: 40px;
  margin-bottom: 50px;
}
.footer-column {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  min-height: 220px;
}
.footer-column h3 {
  font-size: 1.1rem;
  margin-bottom: 20px;
  color: var(--white);
  font-weight: 600;
  position: relative;
  padding-bottom: 10px;
}
.footer-column h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 30px;
  height: 2px;
  background: var(--primary);
}
.footer-column .logo {
  font-weight: 700;
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: 16px;
}
.footer-column .logo i {
  color: var(--primary);
}
.footer-column p {
  color: #86909C;
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 20px;
}
.footer-links {
  list-style: none;
  padding: 0;
}
.footer-links li {
  margin-bottom: 12px;
}
.footer-links a {
  color: #86909C;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 0.95rem;
}
.footer-links a:hover {
  color: var(--primary);
  padding-left: 4px;
}
.social-links {
  display: flex;
  gap: 16px;
  margin-top: 20px;
}
.social-links a {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #86909C;
  transition: all 0.3s ease;
  font-size: 1.1rem;
}
.social-links a:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-3px);
}
.copyright {
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  color: #86909C;
  font-size: 0.9rem;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .value-grid {
    gap: 24px;
  }
  .steps {
    gap: 20px;
  }
}
@media (max-width: 992px) {
  .value-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  .steps {
    flex-wrap: wrap;
    gap: 24px;
  }
  .steps:before {
    display: none;
  }
  .step {
    width: 45%;
  }
  .footer-content {
    grid-template-columns: 1fr 1fr;
    gap: 30px;
  }
}
@media (max-width: 768px) {
  .main-header {
    padding: 20px 20px;
  }
  .center-content {
    padding: 40px 20px;
    margin: 0 10px;
  }
  .center-content h1 {
    font-size: 2rem;
  }
  .hero-cta {
    flex-direction: column;
    gap: 12px;
  }
  .btn-main {
    width: 100%;
  }
  .feature-card {
    width: 100%;
    max-width: 400px;
  }
  .step {
    width: 100%;
    max-width: 300px;
  }
  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  .section-title h2 {
    font-size: 1.8rem;
  }
}
@media (max-width: 576px) {
  .main-header {
    flex-direction: column;
    gap: 16px;
  }
  .mode-card {
    min-width: 100%;
  }
  .ai-avatar-card {
    width: 120px;
    height: 120px;
  }
  .ai-avatar {
    width: 80px;
    height: 80px;
  }
}
</style>