/**
 * 语音识别优化配置
 * 解决语音识别不流利的问题
 */

export const VOICE_RECOGNITION_CONFIG = {
    // 基础配置
    WEBSOCKET_URL: 'ws://localhost:8085/api/webSocket/content',
    
    // 音频录制配置
    AUDIO: {
        // 采样率配置
        SAMPLE_RATE: 16000,
        CHANNEL_COUNT: 1,
        
        // 音频增强配置
        ECHO_CANCELLATION: true,    // 回声消除
        NOISE_SUPPRESSION: true,    // 噪音抑制
        AUTO_GAIN_CONTROL: true,    // 自动增益控制
        
        // 音频块配置
        CHUNK_INTERVAL: 200,        // 音频块发送间隔（毫秒）- 增加到200ms提高流利度
        CHUNK_SIZE: 1024,           // 音频块大小
        
        // 支持的音频格式（按优先级排序）
        SUPPORTED_TYPES: [
            'audio/webm;codecs=opus',
            'audio/webm',
            'audio/mp4',
            'audio/ogg;codecs=opus'
        ]
    },
    
    // 静音检测配置
    SILENCE_DETECTION: {
        THRESHOLD: 0.005,           // 静音检测阈值（降低以提高敏感度）
        DURATION: 6000,             // 静音持续时间（6秒）- 增加避免误判
        CHECK_INTERVAL: 500,        // 静音检测间隔
        VOICE_ACTIVITY_RESET: true  // 检测到语音活动时重置计时器
    },
    
    // 长语音录制配置
    LONG_RECORDING: {
        MAX_DURATION: 180000,       // 最大录制时长（3分钟）
        SEGMENT_DURATION: 120000,   // 分段时长（2分钟）
        RECONNECT_DELAY: 300,       // 重连延迟（300ms）
        MAX_SEGMENTS: 50,           // 最大分段数
        AUTO_RECONNECT: true        // 自动重连
    },
    
    // 语音识别配置
    RECOGNITION: {
        LANGUAGE: 'zh-CN',          // 识别语言
        CONTINUOUS: true,           // 连续识别
        INTERIM_RESULTS: true,      // 中间结果
        MAX_ALTERNATIVES: 1,        // 最大候选数
        
        // 结果处理配置
        RESULT_TYPES: {
            PARTIAL: 'PARTIAL',     // 部分结果
            FINAL: 'FINAL',         // 最终结果
            REPLACE: 'REPLACE'      // 替换结果
        }
    },
    
    // 网络配置
    NETWORK: {
        RECONNECT_ATTEMPTS: 3,      // 重连尝试次数
        RECONNECT_DELAY: 1000,      // 重连延迟
        PING_INTERVAL: 30000,       // 心跳间隔
        CONNECTION_TIMEOUT: 10000   // 连接超时
    },
    
    // 性能优化配置
    PERFORMANCE: {
        BUFFER_SIZE: 4096,          // 缓冲区大小
        PROCESSING_DELAY: 50,       // 处理延迟
        DEBOUNCE_DELAY: 100,        // 防抖延迟
        THROTTLE_DELAY: 200         // 节流延迟
    }
};

/**
 * 获取优化的音频约束
 */
export function getOptimizedAudioConstraints() {
    return {
        audio: {
            sampleRate: VOICE_RECOGNITION_CONFIG.AUDIO.SAMPLE_RATE,
            channelCount: VOICE_RECOGNITION_CONFIG.AUDIO.CHANNEL_COUNT,
            echoCancellation: VOICE_RECOGNITION_CONFIG.AUDIO.ECHO_CANCELLATION,
            noiseSuppression: VOICE_RECOGNITION_CONFIG.AUDIO.NOISE_SUPPRESSION,
            autoGainControl: VOICE_RECOGNITION_CONFIG.AUDIO.AUTO_GAIN_CONTROL,
            // 高级配置
            latency: 0.01,              // 低延迟
            volume: 1.0,                // 音量
            googEchoCancellation: true, // Google回声消除
            googNoiseSuppression: true, // Google噪音抑制
            googAutoGainControl: true,  // Google自动增益
            googHighpassFilter: true,   // 高通滤波器
            googTypingNoiseDetection: true // 打字噪音检测
        }
    };
}

/**
 * 获取支持的音频格式
 */
export function getSupportedAudioType() {
    const types = VOICE_RECOGNITION_CONFIG.AUDIO.SUPPORTED_TYPES;
    
    for (const type of types) {
        if (MediaRecorder.isTypeSupported(type)) {
            return type;
        }
    }
    
    throw new Error('浏览器不支持任何音频格式');
}

/**
 * 创建优化的MediaRecorder配置
 */
export function getOptimizedMediaRecorderOptions(mimeType) {
    return {
        mimeType: mimeType,
        audioBitsPerSecond: 128000,     // 音频比特率
        videoBitsPerSecond: undefined,  // 不使用视频
        bitsPerSecond: 128000          // 总比特率
    };
}

/**
 * 静音检测工具类
 */
export class SilenceDetector {
    constructor(config = {}) {
        this.threshold = config.threshold || VOICE_RECOGNITION_CONFIG.SILENCE_DETECTION.THRESHOLD;
        this.duration = config.duration || VOICE_RECOGNITION_CONFIG.SILENCE_DETECTION.DURATION;
        this.checkInterval = config.checkInterval || VOICE_RECOGNITION_CONFIG.SILENCE_DETECTION.CHECK_INTERVAL;
        
        this.silenceTimer = null;
        this.lastVoiceActivity = Date.now();
        this.isActive = false;
    }
    
    /**
     * 开始静音检测
     */
    start(onSilenceDetected) {
        this.isActive = true;
        this.lastVoiceActivity = Date.now();
        this.onSilenceDetected = onSilenceDetected;
        
        this.checkSilence();
    }
    
    /**
     * 停止静音检测
     */
    stop() {
        this.isActive = false;
        if (this.silenceTimer) {
            clearTimeout(this.silenceTimer);
            this.silenceTimer = null;
        }
    }
    
    /**
     * 重置语音活动时间
     */
    resetVoiceActivity() {
        this.lastVoiceActivity = Date.now();
        console.log('🎤 语音活动检测，重置静音计时器');
    }
    
    /**
     * 检查静音状态
     */
    checkSilence() {
        if (!this.isActive) return;
        
        const now = Date.now();
        const silenceDuration = now - this.lastVoiceActivity;
        
        if (silenceDuration >= this.duration) {
            console.log(`🔇 检测到长时间静音: ${silenceDuration}ms`);
            this.onSilenceDetected && this.onSilenceDetected();
            return;
        }
        
        // 继续检测
        this.silenceTimer = setTimeout(() => {
            this.checkSilence();
        }, this.checkInterval);
    }
}

/**
 * 音频质量检测工具
 */
export class AudioQualityDetector {
    constructor() {
        this.audioContext = null;
        this.analyser = null;
        this.dataArray = null;
    }
    
    /**
     * 初始化音频分析
     */
    init(audioStream) {
        try {
            this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
            this.analyser = this.audioContext.createAnalyser();
            
            const source = this.audioContext.createMediaStreamSource(audioStream);
            source.connect(this.analyser);
            
            this.analyser.fftSize = 256;
            this.dataArray = new Uint8Array(this.analyser.frequencyBinCount);
            
            return true;
        } catch (error) {
            console.error('音频质量检测初始化失败:', error);
            return false;
        }
    }
    
    /**
     * 获取音频音量
     */
    getVolume() {
        if (!this.analyser || !this.dataArray) return 0;
        
        this.analyser.getByteFrequencyData(this.dataArray);
        
        let sum = 0;
        for (let i = 0; i < this.dataArray.length; i++) {
            sum += this.dataArray[i];
        }
        
        return sum / this.dataArray.length / 255; // 归一化到0-1
    }
    
    /**
     * 检测是否有语音活动
     */
    hasVoiceActivity() {
        const volume = this.getVolume();
        return volume > VOICE_RECOGNITION_CONFIG.SILENCE_DETECTION.THRESHOLD;
    }
    
    /**
     * 清理资源
     */
    cleanup() {
        if (this.audioContext) {
            this.audioContext.close();
            this.audioContext = null;
        }
        this.analyser = null;
        this.dataArray = null;
    }
}
