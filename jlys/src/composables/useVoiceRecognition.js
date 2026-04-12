import { ref } from 'vue';

// ==================== 常量配置 ====================
const CONFIG = {
    get WEBSOCKET_URL() {
        // 检测是否在IDE开发环境中
        let wsHost = window.location.host;
        if (window.location.port === '63342') {

            wsHost = 'localhost:8089';
        }
        return `ws://${wsHost}/api/webSocket/content`;
    },
    MESSAGE_TYPES: {
        CONNECT: 'CONNECT',
        INFO: 'INFO'
    },
    // 长语音录入配置
    LONG_RECORDING: {
        MAX_DURATION: 55000,        // 55秒后自动重连（留5秒缓冲）
        RECONNECT_DELAY: 1000,      // 重连延迟1秒
        MAX_SEGMENTS: 100,          // 最大分段数量
        SILENCE_THRESHOLD: 0.01,    // 静音检测阈值
        SILENCE_DURATION: 2000      // 静音持续时间（毫秒）
    }
};

// ==================== 长语音录入管理类 ====================
class LongRecordingManager {
    constructor() {
        this.isRecording = false;
        this.segments = [];                    // 存储所有分段结果
        this.currentSegment = '';              // 当前分段内容
        this.segmentIndex = 0;                 // 当前分段索引
        this.recordingStartTime = null;       // 录音开始时间
        this.segmentStartTime = null;         // 当前分段开始时间
        this.reconnectTimer = null;           // 重连定时器
        this.silenceTimer = null;             // 静音检测定时器
        this.lastSpeechTime = null;           // 最后检测到语音的时间
        this.wsManager = null;                // WebSocket管理器
        this.onTextUpdate = null;             // 文本更新回调
        this.onStatusUpdate = null;           // 状态更新回调
    }

    /**
     * 开始长语音录入
     */
    startLongRecording(wsManager, onTextUpdate, onStatusUpdate) {
        this.wsManager = wsManager;
        this.onTextUpdate = onTextUpdate;
        this.onStatusUpdate = onStatusUpdate;

        this.isRecording = true;
        this.segments = [];
        this.currentSegment = '';
        this.segmentIndex = 0;
        this.recordingStartTime = Date.now();

        console.log('🎙️ 开始长语音录入模式');
        this.startNewSegment();
    }

    /**
     * 开始新的录音分段
     */
    startNewSegment() {
        if (!this.isRecording) return;

        this.segmentIndex++;
        this.segmentStartTime = Date.now();
        this.currentSegment = '';

        console.log(`📝 开始第 ${this.segmentIndex} 段录音`);
        this.onStatusUpdate && this.onStatusUpdate(`录音中 - 第${this.segmentIndex}段`);

        // 建立新的WebSocket连接
        this.connectSegment();

        // 设置自动重连定时器
        this.setAutoReconnectTimer();
    }

    /**
     * 连接当前分段
     */
    connectSegment() {
        if (!this.wsManager) return;

        this.wsManager.connect({
            onConnect: async () => {
                console.log(`✅ 第 ${this.segmentIndex} 段连接成功`);
                // 连接成功后开始录音
                await this.startSegmentRecording();
            },
            onMessage: (data) => {
                this.handleSegmentMessage(data);
            },
            onError: () => {
                console.error(`❌ 第 ${this.segmentIndex} 段连接错误`);
                this.handleSegmentError();
            },
            onClose: () => {
                console.log(`🔌 第 ${this.segmentIndex} 段连接关闭`);
                this.handleSegmentClose();
            }
        });
    }

    /**
     * 开始分段录音
     */
    async startSegmentRecording() {
        try {
            // 检查浏览器支持
            if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
                throw new Error('浏览器不支持音频录制功能');
            }

            if (!window.MediaRecorder) {
                throw new Error('浏览器不支持MediaRecorder API');
            }

            // 获取麦克风权限
            const audioStream = await navigator.mediaDevices.getUserMedia({
                audio: {
                    sampleRate: 16000,
                    channelCount: 1,
                    echoCancellation: true,
                    noiseSuppression: true
                }
            });

            // 检查支持的MIME类型
            const supportedTypes = [
                'audio/webm;codecs=opus',
                'audio/webm',
                'audio/mp4',
                'audio/ogg;codecs=opus'
            ];
            
            let selectedType = null;
            for (const type of supportedTypes) {
                if (MediaRecorder.isTypeSupported(type)) {
                    selectedType = type;
                    break;
                }
            }

            if (!selectedType) {
                throw new Error('浏览器不支持任何音频格式');
            }

            // 创建MediaRecorder
            const mediaRecorder = new MediaRecorder(audioStream, {
                mimeType: selectedType
            });

            // 处理音频数据
            mediaRecorder.ondataavailable = (event) => {
                if (event.data.size > 0 && this.wsManager && this.wsManager.isConnected) {
                    // 将音频Blob转为base64并封装为JSON发送
                    const reader = new FileReader();
                    reader.onload = function() {
                        const base64Audio = reader.result.split(',')[1]; // 去掉data:前缀
                        const jsonMsg = JSON.stringify({ audio: base64Audio });
                        this.wsManager.send(jsonMsg);
                    }.bind(this);
                    reader.readAsDataURL(event.data);
                }
            };

            // 开始录制
            mediaRecorder.start(100); // 每100ms发送一次音频数据
            console.log(`🎙️ 第 ${this.segmentIndex} 段开始录音`);

        } catch (error) {
            console.error('获取麦克风权限失败:', error);
        }
    }

    /**
     * 处理分段消息
     */
    handleSegmentMessage(data) {
        if (!this.isRecording) return;

        if (data.text) {
            this.currentSegment = data.text;
            this.lastSpeechTime = Date.now();

            // 更新完整文本（所有分段 + 当前分段）
            const fullText = this.getFullText();
            this.onTextUpdate && this.onTextUpdate(fullText);

            // 重置静音检测
            this.resetSilenceTimer();
        }
    }

    /**
     * 获取完整文本
     */
    getFullText() {
        const allSegments = [...this.segments];
        if (this.currentSegment) {
            allSegments.push(this.currentSegment);
        }
        return allSegments.join(' ');
    }

    /**
     * 设置自动重连定时器
     */
    setAutoReconnectTimer() {
        // 清除之前的定时器
        if (this.reconnectTimer) {
            clearTimeout(this.reconnectTimer);
        }

        // 设置新的定时器
        this.reconnectTimer = setTimeout(() => {
            if (this.isRecording) {
                console.log('⏰ 达到最大录音时长，自动重连...');
                this.finishCurrentSegment();
            }
        }, CONFIG.LONG_RECORDING.MAX_DURATION);
    }

    /**
     * 完成当前分段
     */
    finishCurrentSegment() {
        if (this.currentSegment.trim()) {
            this.segments.push(this.currentSegment.trim());
            console.log(`✅ 第 ${this.segmentIndex} 段完成: "${this.currentSegment}"`);
        }

        // 关闭当前连接
        if (this.wsManager) {
            this.wsManager.close();
        }

        // 检查是否继续录音
        if (this.isRecording && this.segmentIndex < CONFIG.LONG_RECORDING.MAX_SEGMENTS) {
            // 短暂延迟后开始下一段
            setTimeout(() => {
                if (this.isRecording) {
                    this.startNewSegment();
                }
            }, CONFIG.LONG_RECORDING.RECONNECT_DELAY);
        } else {
            this.stopLongRecording();
        }
    }

    /**
     * 处理分段错误
     */
    handleSegmentError() {
        console.warn(`⚠️ 第 ${this.segmentIndex} 段出现错误，尝试重连...`);
        if (this.isRecording) {
            setTimeout(() => {
                this.connectSegment();
            }, CONFIG.LONG_RECORDING.RECONNECT_DELAY);
        }
    }

    /**
     * 处理分段关闭
     */
    handleSegmentClose() {
        // 如果是正常录音中且不是主动关闭，则尝试重连
        if (this.isRecording && this.wsManager && !this.wsManager.isManualClose) {
            this.finishCurrentSegment();
        }
    }

    /**
     * 设置静音检测定时器
     */
    resetSilenceTimer() {
        if (this.silenceTimer) {
            clearTimeout(this.silenceTimer);
        }

        this.silenceTimer = setTimeout(() => {
            if (this.isRecording) {
                console.log('🔇 检测到长时间静音，完成当前分段');
                this.finishCurrentSegment();
            }
        }, CONFIG.LONG_RECORDING.SILENCE_DURATION);
    }

    /**
     * 停止长语音录入
     */
    stopLongRecording() {
        console.log('🛑 停止长语音录入');
        this.isRecording = false;

        // 清除所有定时器
        if (this.reconnectTimer) {
            clearTimeout(this.reconnectTimer);
            this.reconnectTimer = null;
        }
        if (this.silenceTimer) {
            clearTimeout(this.silenceTimer);
            this.silenceTimer = null;
        }

        // 保存最后一段
        if (this.currentSegment.trim()) {
            this.segments.push(this.currentSegment.trim());
        }

        // 关闭WebSocket连接
        if (this.wsManager) {
            this.wsManager.close();
        }

        // 计算总时长
        const totalDuration = Date.now() - this.recordingStartTime;
        const finalText = this.segments.join(' ');

        console.log(`📊 录音统计: 总时长 ${Math.round(totalDuration/1000)}秒, 分段数 ${this.segments.length}, 总字数 ${finalText.length}`);

        this.onStatusUpdate && this.onStatusUpdate(`录音完成 - ${this.segments.length}段 ${Math.round(totalDuration/1000)}秒`);
        this.onTextUpdate && this.onTextUpdate(finalText);

        return finalText;
    }

    /**
     * 获取录音统计信息
     */
    getStats() {
        const currentTime = Date.now();
        const totalDuration = this.recordingStartTime ? currentTime - this.recordingStartTime : 0;
        const segmentDuration = this.segmentStartTime ? currentTime - this.segmentStartTime : 0;

        return {
            totalDuration: Math.round(totalDuration / 1000),
            segmentDuration: Math.round(segmentDuration / 1000),
            segmentIndex: this.segmentIndex,
            totalSegments: this.segments.length,
            totalChars: this.getFullText().length,
            isRecording: this.isRecording
        };
    }
}

// ==================== WebSocket 管理类 ====================
class WebSocketManager {
    constructor() {
        this.ws = null;
        this.isConnected = false;
        this.isManualClose = false;
    }

    /**
     * 建立WebSocket连接
     * @param {Object} callbacks - 回调函数对象
     * @param {Function} callbacks.onConnect - 连接建立回调
     * @param {Function} callbacks.onMessage - 消息接收回调
     * @param {Function} callbacks.onError - 错误回调
     * @param {Function} callbacks.onClose - 连接关闭回调
     */
    connect(callbacks = {}) {
        try {
            this.ws = new WebSocket(CONFIG.WEBSOCKET_URL);

            this.ws.onopen = () => {
                this.isConnected = true;
                console.log('WebSocket 连接已建立');
                callbacks.onConnect && callbacks.onConnect();
            };

            this.ws.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    console.log('收到服务器消息：', event.data);
                    callbacks.onMessage && callbacks.onMessage(data);
                } catch (error) {
                    console.error('解析消息失败：', error);
                }
            };

            this.ws.onerror = (event) => {
                console.error('WebSocket 连接出现错误：', event);
                callbacks.onError && callbacks.onError(event);
            };

            this.ws.onclose = () => {
                this.isConnected = false;
                console.log('WebSocket 连接已关闭');
                callbacks.onClose && callbacks.onClose();
            };
        } catch (error) {
            console.error('WebSocket 连接失败：', error);
        }
    }

    /**
     * 发送消息
     * @param {string|Object|Blob} message - 要发送的消息
     */
    send(message) {
        if (this.isConnected && this.ws) {
            if (message instanceof Blob) {
                // 发送音频数据
                console.log('发送音频数据到后端:', message.size, 'bytes');
                this.ws.send(message);
            } else {
                // 发送文本消息
                const msg = typeof message === 'string' ? message : JSON.stringify(message);
                // console.log('发送消息到后端:', msg);
                this.ws.send(msg);
            }
        } else {
            console.warn('WebSocket 未连接，无法发送消息');
        }
    }

    /**
     * 关闭连接
     */
    close() {
        this.isManualClose = true;
        if (this.ws) {
            this.ws.close();
            this.ws = null;
            this.isConnected = false;
        }
    }
}

/**
 * 语音识别组合式函数
 * 提供语音录制和WebSocket通信功能
 */
export function useVoiceRecognition() {
  // WebSocket语音识别相关
  let wsManager = null;
  let longRecordingManager = null;
  let mediaRecorder = null;
  let audioStream = null;
  
  // 响应式状态
  const isRecording = ref(false);
  const recordingStatus = ref('准备就绪');
  const recordingMode = ref('standard'); // 'standard' 或 'long'
  const recognizedText = ref('');
  const isConnected = ref(false); // 新增：连接状态

  /**
   * 开始语音识别
   */
  async function startVoice() {
    try {
      isRecording.value = true;
      recognizedText.value = '';
      recordingStatus.value = '';

      // 初始化WebSocket管理器
      if (!wsManager) {
        wsManager = new WebSocketManager();
      }

      if (recordingMode.value === 'long') {
        await startLongRecording();
      } else {
        await startStandardRecording();
      }

    } catch (error) {
      console.error('开始录音失败:', error);
      recordingStatus.value = '启动失败: ' + error.message;
      isRecording.value = false;
    }
  }

  /**
   * 开始音频录制
   */
  async function startAudioRecording() {
    try {
      // 检查浏览器支持
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('浏览器不支持音频录制功能');
      }

      if (!window.MediaRecorder) {
        throw new Error('浏览器不支持MediaRecorder API');
      }

      // 获取麦克风权限
      audioStream = await navigator.mediaDevices.getUserMedia({
        audio: {
          sampleRate: 16000,
          channelCount: 1,
          echoCancellation: true,
          noiseSuppression: true
        }
      });

      // 检查支持的MIME类型
      const supportedTypes = [
        'audio/webm;codecs=opus',
        'audio/webm',
        'audio/mp4',
        'audio/ogg;codecs=opus'
      ];
      
      let selectedType = null;
      for (const type of supportedTypes) {
        if (MediaRecorder.isTypeSupported(type)) {
          selectedType = type;
          break;
        }
      }

      if (!selectedType) {
        throw new Error('浏览器不支持任何音频格式');
      }

      // 创建MediaRecorder
      mediaRecorder = new MediaRecorder(audioStream, {
        mimeType: selectedType
      });

      // 处理音频数据
      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0 && wsManager && wsManager.isConnected) {
          // 将音频Blob转为base64并封装为JSON发送
          const reader = new FileReader();
          reader.onload = function() {
            const base64Audio = reader.result.split(',')[1]; // 去掉data:前缀
            const jsonMsg = JSON.stringify({ audio: base64Audio });
            wsManager.send(jsonMsg);
          }.bind(this);
          reader.readAsDataURL(event.data);
        }
      };

      // 开始录制
      mediaRecorder.start(100); // 每100ms发送一次音频数据
      recordingStatus.value = '正在录音...';

    } catch (error) {
      console.error('获取麦克风权限失败:', error);
      throw new Error('无法访问麦克风，请检查权限设置');
    }
  }

  /**
   * 标准录音模式
   */
  async function startStandardRecording() {
    wsManager.connect({
      onConnect: async () => {
        isConnected.value = true;
        recordingStatus.value = '';
        
        // WebSocket连接成功后开始录音
        await startAudioRecording();
        recordingStatus.value = '';
      },
      onMessage: (data) => {
        handleWebSocketMessage(data);
      },
      onError: (event) => {
        isConnected.value = false;
        handleWebSocketError(event);
      },
      onClose: () => {
        isConnected.value = false;
        handleWebSocketClose();
      }
    });

    // 60秒自动停止
    setTimeout(() => {
      if (isRecording.value) {
        stopVoice();
        recordingStatus.value = '';
      }
    }, 60000);
  }

  /**
   * 长语音录音模式
   */
  async function startLongRecording() {
    if (!longRecordingManager) {
      longRecordingManager = new LongRecordingManager();
    }

    longRecordingManager.startLongRecording(
      wsManager,
      // 文本更新回调
      (text) => {
        recognizedText.value = text;
        recordingStatus.value = '';
      },
      // 状态更新回调
      () => {
        recordingStatus.value = '';
      }
    );
  }

  /**
   * 停止语音识别
   */
  async function stopVoice() {
    try {
      isRecording.value = false;

      // 停止音频录制
      if (mediaRecorder && mediaRecorder.state !== 'inactive') {
        mediaRecorder.stop();
      }

      // 关闭音频流
      if (audioStream) {
        audioStream.getTracks().forEach(track => track.stop());
        audioStream = null;
      }

      if (longRecordingManager && longRecordingManager.isRecording) {
        // 长语音模式
        const finalText = longRecordingManager.stopLongRecording();
        recognizedText.value = finalText;
      }

      // 关闭WebSocket连接
      if (wsManager) {
        wsManager.close();
      }

      recordingStatus.value = '';

    } catch (error) {
      console.error('停止录音失败:', error);
      recordingStatus.value = '停止失败: ' + error.message;
    }
  }

  /**
   * 切换录音状态
   */
  function toggleVoice() {
    if (!isRecording.value) {
      startVoice();
    } else {
      stopVoice();
    }
  }

  /**
   * 处理WebSocket消息
   */
  function handleWebSocketMessage(data) {
    if (data.text) {
      handleSpeechResult(data);
    } else if (data.error) {
      console.error('语音识别错误：', data.error);
      recordingStatus.value = `错误: ${data.error}`;
    } else if (data.status) {
      recordingStatus.value = data.status;
    }
  }

  /**
   * 处理语音识别结果
   */
  function handleSpeechResult(data) {
    const text = data.text || '';
    console.log("识别", text)
    const messageType = data.type || 'PARTIAL';

    switch (messageType) {
      case 'REPLACE':
        recognizedText.value = text;
        break;
      case 'FINAL':
        // recognizedText.value = text;
        // console.log('识别完成:', text);
        // break;
      case 'PARTIAL':
      default:
        handlePartialResult(text);
        break;
    }
  }

  /**
   * 处理部分识别结果
   */
  function handlePartialResult(newText) {
    const currentText = recognizedText.value || '';

    if (newText.length > currentText.length && newText.startsWith(currentText)) {
      recognizedText.value = newText;
    } else if (newText.length < currentText.length) {
      const punctuation = /^[，。！？；：""''（）【】《》、\s]*$/;
      if (punctuation.test(newText)) {
        recognizedText.value = currentText + newText;
      } else {
        recognizedText.value = newText;
      }
    } else {
      recognizedText.value = newText;
    }
  }

  /**
   * WebSocket错误处理
   */
  function handleWebSocketError(event) {
    console.error('WebSocket错误:', event);
    recordingStatus.value = '连接错误';
  }

  /**
   * WebSocket关闭处理
   */
  function handleWebSocketClose() {
    console.log('WebSocket连接已关闭');
    if (isRecording.value) {
      recordingStatus.value = '';
    }
  }

  /**
   * 清理资源
   */
  function cleanup() {
    // 停止音频录制
    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      mediaRecorder.stop();
    }
    
    // 关闭音频流
    if (audioStream) {
      audioStream.getTracks().forEach(track => track.stop());
      audioStream = null;
    }

    if (wsManager) {
      wsManager.close();
      wsManager = null;
    }
    if (longRecordingManager) {
      longRecordingManager = null;
    }
    isRecording.value = false;
    recordingStatus.value = '';
    recognizedText.value = '';
  }

  return {
    // 状态
    isRecording,
    recordingStatus,
    recordingMode,
    recognizedText,
    isConnected,
    
    // 方法
    startVoice,
    stopVoice,
    toggleVoice,
    cleanup
  };
} 