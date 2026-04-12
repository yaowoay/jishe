<template>
  <div class="coze-assistant">
    <!-- Coze 聊天框容器 -->
    <div id="coze-chat-container" class="coze-chat-container">
      <!-- Coze 聊天框将在这里渲染 -->
      <div class="loading-placeholder" v-if="!cozeLoaded">
        <div class="loading-icon">🤖</div>
        <div class="loading-text">AI助手正在初始化...</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CozeAssistant',

  data() {
    return {
      cozeWebSDK: null,
      cozeLoaded: false
    }
  },

  mounted() {
    this.initializeCozeChat()
  },

  beforeUnmount() {
    if (this.cozeWebSDK) {
      try {
        this.cozeWebSDK.destroy && this.cozeWebSDK.destroy()
      } catch (error) {
        console.warn('清理 Coze 资源时出错:', error)
      }
    }
  },
  methods: {
    initializeCozeChat() {
      this.$nextTick(() => {
        const container = document.getElementById('coze-chat-container')
        if (!container) {
          console.error('Coze 容器元素未找到')
          return
        }

        if (window.CozeWebSDK) {
          try {
            this.cozeWebSDK = new window.CozeWebSDK.WebChatClient({
              config: {
                type: 'bot',
                botId: '7526417335507746879',
                isIframe: false,//必须设置为true才能显示会话列表
                uploadable: true, // 必须设置为true才能上传文件
                isNeedAudio: true,// 必须设置为true才能语音输入
                botInfo: {
                  parameters: {
                    user_name: '用户',
                    page_url: window.location.href,
                    source: '独立组件'
                  }
                }
              },
              auth: {
                type: 'token',
                token: 'pat_OmUlK9PRVhrymzKJ6cIHcaR6mgdfeQ25SLYpzR4cCMLTTTJhYpeFaC9ewzajanxA',
                onRefreshToken: async () => 'pat_OmUlK9PRVhrymzKJ6cIHcaR6mgdfeQ25SLYpzR4cCMLTTTJhYpeFaC9ewzajanxA'
              },
              userInfo: {
                id: 'coze_component_user_' + Date.now(),
                nickname: 'user',
                url: 'https://img.icons8.com/?size=100&id=VmOJRyfYyS9e&format=png&color=000000'
              },
              ui: {
                base: {
                  icon: 'https://lf-coze-web-cdn.coze.cn/obj/coze-web-cn/obric/coze/favicon.1970.png',
                  layout: 'pc',
                  lang: 'zh-CN',
                  zIndex: 1000
                },
                asstBtn: {
                  isNeed:true
                },
                header: {
                  isShow: false,
                  isNeedClose: true//右上角的❌
                },
                footer: {
                  isShow: true,
                  expressionText: 'Powered by{{desc}}',
                  linkvars: {
                    name: {
                      text: '',
                      link: 'https://www.zhimianai.com'
                    },
                    desc: {
                      text: '专业面试辅导平台',
                      link: 'https://www.zhimianai.com'
                    }
                  }
                },
                conversations: {
                  isNeed: true
                },
                chatBot: {
                  title: 'AI助手',
                  uploadable: true,
                  width: 900,
                  el: container,
                  isNeedAudio: true,
                  isNeedFunctionCallMessage: true,
                  isNeedAddNewConversation: true,
                  isNeedQuote:true,

                  feedback: {
                    isNeedFeedback: true,
                    feedbackPanel: {
                      title: '您对这个回答有什么看法？请告诉我们',
                      placeholder: '请详细描述您的问题或建议...',
                      tags: [
                        {
                          label: '信息不正确'
                        },
                        {
                          label: '不够详细',
                          isNeedDetail: true
                        },
                        {
                          label: '很有帮助'
                        },
                        {
                          label: '其他问题',
                          isNeedDetail: true
                        }
                      ]
                    }
                  },
                  onShow: () => {
                    this.cozeLoaded = true
                  }
                }
              }
            })

            setTimeout(() => {
              if (this.cozeWebSDK) {
                this.cozeWebSDK.showChatBot()
              }
            }, 2000)

          } catch (error) {
            console.error('你的智能助手初始化失败:', error)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.coze-assistant {
  width: 100%;
  height: 100%;
}

.coze-chat-container {
  width: 900px;
  height: 700px;
  min-height: 100px;
  background: #f8f9fa;
}

.loading-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 40px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.loading-icon {
  font-size: 48px;
  margin-bottom: 16px;
  animation: bounce 2s infinite;
}

.loading-text {
  font-size: 18px;
  font-weight: 600;
  color: #495057;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

</style>