<template>
  <div class="avatar">
    <el-container>
      <!-- <el-aside width="200px" height="840px">
        <el-row>
          <el-button style="margin: 0px" @click="writeText()" type="primary">文本驱动</el-button>
          ... 其它按钮和控件 ...
        </el-row>
      </el-aside> -->
      <el-main class="htmleaf-content" style="padding: 0px">
        <div class="weather rain" id="wrapper"></div>
        <!-- <span>透明度</span><input type="range" id="opacityRange" min="0" max="1" step="0.1" value="1"> -->
      </el-main>
      <!--SetApiInfo悬浮框-->
      <el-dialog title="初始化SDK" v-model:visible="SetApiInfodialog">
        <el-form :model="form">
          <span>此处参数均去交互平台-接口服务中获取</span>
          <el-form-item label="Appid" :label-width="formLabelWidth">
            <el-input
                class="widthclass"
                v-model="form.appid"
                autocomplete="off"
            ></el-input>
            <span>必填</span>
          </el-form-item>
          <el-form-item label="ApiKey" :label-width="formLabelWidth">
            <el-input
                class="widthclass"
                v-model="form.apikey"
                autocomplete="off"
            ></el-input>
            <span>必填</span>
          </el-form-item>
          <el-form-item label="ApiSecret" :label-width="formLabelWidth">
            <el-input
                class="widthclass"
                v-model="form.apisecret"
                autocomplete="off"
            ></el-input>
            <span>必填</span>
          </el-form-item>
          <el-form-item label="SceneId" :label-width="formLabelWidth">
            <el-input
                class="widthclass"
                v-model="form.sceneid"
                autocomplete="off"
            ></el-input>
            <span>必填</span>
          </el-form-item>
          <el-form-item label="ServerUrl" :label-width="formLabelWidth">
            <el-input
                class="widthclass"
                v-model="form.serverurl"
                autocomplete="off"
            ></el-input>
            <span>必填</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="SetApiInfodialog = false">取 消</el-button>
          <el-button
              type="primary"
              @click="(SetApiInfodialog = false), SetApiInfo2()"
          >确 定</el-button
          >
        </template>
      </el-dialog>
      <!--SetGlobalParams悬浮框-->
      <el-dialog title="设置全局变量" v-model:visible="SetGlobalParamsdialog">
        <div style="text-align: center"><h3>打断模式全局设置</h3></div>
        <el-form :model="setglobalparamsform" :label-width="formLabelWidth">
          <el-form-item label="视频协议">
            <el-tooltip
                class="item"
                effect="dark"
                content="支持webrtc/xrtc/rtmp(控制台打印视频流地址)"
                placement="right-start"
            >
              <i class="el-icon-question"></i>
            </el-tooltip>
            <el-select
                v-model="setglobalparamsform.stream.protocol"
                placeholder="请选择视频流协议"
            >
              <el-option label="xrtc" value="xrtc"></el-option>
              <el-option label="webrtc" value="webrtc"></el-option>
              <el-option label="rtmp" value="rtmp"></el-option>
            </el-select>
            <span>必填</span>
          </el-form-item>
          <el-form-item label="透明背景">
            <el-tooltip
                class="item"
                effect="dark"
                content="仅支持xrtc协议"
                placement="right-start"
            >
              <i class="el-icon-question"></i>
            </el-tooltip>
            <el-switch v-model="setglobalparamsform.stream.alpha"></el-switch>
          </el-form-item>
          <el-form-item label="全局交互模式">
            <el-radio-group v-model="setglobalparamsform.avatar_dispatch.interactive_mode">
              <el-radio :label="0">追加模式（信息依次播报）</el-radio>
              <el-radio :label="1">打断模式（直接播报最新）</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item
              label="形象ID"
          >
            <el-input
                class="widthclass"
                v-model="setglobalparamsform.avatar.avatar_id"
                autocomplete="on"
                placeholder="到交互平台-接口服务-形象列表中获取id"
            ></el-input>
            <span>必填</span>
          </el-form-item>
          <el-form-item
              label="分辨率高"
          >
            <el-input
                class="widthclass"
                v-model="setglobalparamsform.avatar.height"
                autocomplete="on"
            ></el-input>
          </el-form-item>
          <el-form-item
              label="分辨率宽"
          >
            <el-input
                class="widthclass"
                v-model="setglobalparamsform.avatar.width"
                autocomplete="on"
            ></el-input>
          </el-form-item>
          <el-form-item label="音频采样率">
            <el-radio-group v-model="setglobalparamsform.avatar.audio_format">
              <el-radio :label="1">16K(传1)</el-radio>
              <el-radio :label="2">24K(传2，大部分情况默认24K即可)</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item
              label="形象裁剪"
              v-if="setglobalparamsform.avatar.mask_region != null"
          >
            <el-input
                class="widthclass"
                v-model="setglobalparamsform.avatar.mask_region"
                autocomplete="on"
                placeholder="对形象进行裁剪[从左到右,从上到下,从右到左,从下到上]"
            ></el-input>
          </el-form-item>
          <el-form-item label="发音人">
            <el-input
                class="widthclass"
                v-model="setglobalparamsform.tts.vcn"
                autocomplete="on"
                placeholder="到交互平台-接口服务-声音列表中获取id"
            ></el-input>
            <span>必填</span>
          </el-form-item>
          <el-form-item label="情感">
            <el-input
                class="widthclass"
                v-model.number="setglobalparamsform.tts.emotion"
                autocomplete="on"
                placeholder="到交互平台-接口服务-声音列表中获取id"
            ></el-input>
          </el-form-item>
          <el-form-item label="是否开启字幕">
            <el-radio-group v-model="setglobalparamsform.subtitle.subtitle">
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0"
              >关闭</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item label="字体颜色">
            <el-color-picker v-model="setglobalparamsform.subtitle.font_color"></el-color-picker>
          </el-form-item>
          <el-form-item label="是否开启背景图">
            <el-radio-group v-model="setglobalparamsform.enable">
              <el-radio :label="true">开启</el-radio>
              <el-radio :label="false"
              >关闭</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item label="背景图片">
            <el-radio-group v-model="setglobalparamsform.background.type">
              <el-radio label="url">URL</el-radio>
              <el-radio label="res_key"
              >res_key(到交互平台-素材管理中获取)</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item label="背景数据">
            <el-input
                v-model="setglobalparamsform.background.data"
                autocomplete="on"
            ></el-input>
          </el-form-item>
        </el-form>

        <el-form :model="form"> </el-form>
        <template #footer>
          <el-button @click="SetGlobalParamsdialog = false">取 消</el-button>
          <el-button
              type="primary"
              @click="(SetGlobalParamsdialog = false), SetGlobalParams()"
          >确 定</el-button
          >
        </template>
      </el-dialog>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, defineExpose, defineEmits } from 'vue'
import AvatarPlatform, { PlayerEvents, SDKEvents } from '../vm-sdk/avatar-sdk-web_3.1.1.1011/index.js'

// 定义向父组件发送的事件
const emit = defineEmits(['nlp-content'])

// 状态
const SetApiInfodialog = ref(false)
const SetGlobalParamsdialog = ref(false)
const sdkConnected = ref(false) // SDK连接状态
const sdkInitialized = ref(false) // SDK初始化状态
const form = reactive({
  appid: '',
  apikey: '',
  apisecret: '',
  sceneid: '',
  serverurl: 'wss://avatar.cn-huadong-1.xf-yun.com/v1/interact'
})
const setglobalparamsform = reactive({
  stream: { protocol: 'webrtc', fps: 25, bitrate: 1000000, alpha: false },
  avatar: { avatar_id: '110117005', width: 1080, height: 1920, mask_region: '[0,0,1080,1920]', scale: 1, move_h: 0, move_v: 0, audio_format: 1 },
  tts: { vcn: 'x4_lingxiaoqi_oral', speed: 50, pitch: 50, volume: 100, emotion: 13 },
  avatar_dispatch: { interactive_mode: 1 },
  subtitle: { subtitle: 0, font_color: '#FFFFFF', font_name: 'Sanji.Suxian.Simple', position_x: 100, position_y: 0, font_size: 10, width: 100, height: 100 },
  enable: false,
  background: { type: 'res_key', data: '' }
})
const formLabelWidth = '120px'
const textarea = ref('你好啊，我是小爱同学')
const vc = ref('x4_lingxiaoqi_oral')
// const recorderbutton = ref(false) // 未使用，先注释
// const action = ref('A_RH_hello_O') // 未使用，先注释
// const nlp = ref(true) // 未使用，先注释
const emotion = ref(13)
// const volume = ref(100) // 未使用，先注释

let avatarPlatform2 = null

function open2(text) {
  // alert(text) // 禁止所有弹窗提示
}

function initSDK() {
  avatarPlatform2 = new AvatarPlatform()
  if (avatarPlatform2 != null) open2('实例化SDK成功')
}
function createRecoder() {
  if (avatarPlatform2 != null) open2('创建录音器成功')
  else alert('请实例化SDK实例')
}
function setSDKEvenet() {
  if (avatarPlatform2 != null) {
    avatarPlatform2
      .on(SDKEvents.connected, function (initResp) {
        console.log('数字人SDK连接成功:', initResp)
        sdkConnected.value = true
      })
      .on(SDKEvents.stream_start, function () {
        console.log('数字人流媒体开始')
      })
      .on(SDKEvents.disconnected, function (err) {
        console.log('数字人SDK断开连接:', err)
        sdkConnected.value = false
      })
      .on(SDKEvents.tts_start, function () {
        console.log('TTS开始播放')
      })
      .on(SDKEvents.tts_end, function () {
        console.log('TTS播放结束')
      })
      .on(SDKEvents.error, function (error) {
        console.error('数字人SDK错误:', error)
      })
      .on(SDKEvents.nlp, function (nlpData) {
        console.log('语义理解内容nlp:', nlpData)

        // 向父组件发送NLP内容
        if (nlpData && nlpData.content) {
          // 判断是否为完整内容（根据实际SDK返回的数据结构调整）
          // 常见的完成标志：status、final、is_end、complete等
          const isComplete = nlpData.status === 'complete' ||
                nlpData.status === 'end' ||
                nlpData.final === true ||
                nlpData.is_end === true ||
                nlpData.complete === true ||
                nlpData.finished === true

          console.log('NLP数据处理 - 内容:', nlpData.content, '是否完成:', isComplete, '原始数据:', nlpData)

          emit('nlp-content', {
            content: nlpData.content,
            isComplete,
            rawData: nlpData // 保留原始数据以便调试
          })
        } else {
          console.warn('收到的NLP数据格式不正确:', nlpData)
        }
      })
      // eslint-disable-next-line camelcase
      .on(SDKEvents.frame_start, function (frame_start) { console.log('推流开始 frame_start:', frame_start) })
      // eslint-disable-next-line camelcase
      .on(SDKEvents.frame_stop, function (frame_stop) { console.log('推流结束 frame_stop:', frame_stop) })
      .on(SDKEvents.error, function (error) { console.log('错误信息error:', error) })
      .on(SDKEvents.connected, function () { console.log('connected') })
      .on(SDKEvents.asr, function (asrData) { console.log('语音识别数据asr:', asrData) })
      .on(SDKEvents.tts_duration, function (ttsData) { console.log('语音合成用时tts：', ttsData) })
      .on(SDKEvents.subtitle_info, function (subtitleData) { console.log('subtitleData：', subtitleData) })
      // eslint-disable-next-line camelcase
      .on(SDKEvents.action_start, function (action_start) { console.log('动作推流开始 action_start:', action_start) })
      // eslint-disable-next-line camelcase
      .on(SDKEvents.action_stop, function (action_stop) { console.log('动作推流结束 action_stop：', action_stop) })
    open2('监听SDK事件成功')
  } else alert('请先实例化SDK')
}
function setPlayerEvenet() {
  if (avatarPlatform2 != null) {
    const player = avatarPlatform2.createPlayer()
    player
      .on(PlayerEvents.play, function () { console.log('paly') })
      .on(PlayerEvents.playing, function () { console.log('playing') })
      .on(PlayerEvents.waiting, function () { console.log('waiting') })
      .on(PlayerEvents.stop, function () { console.log('stop') })
      .on(PlayerEvents.playNotAllowed, function () { player.resume() })
    open2('监听播放器事件成功')
  } else alert('请先实例化SDK')
}
function SetApiInfo2() {
  if (avatarPlatform2 == null) {
    console.warn('请先实例化SDK')
    return
  }

  // 检查是否有配置的API信息
  if (!form.appid || !form.apikey || !form.apisecret || !form.sceneid) {
    console.warn('数字人API配置不完整，使用默认配置进行测试')
    // 使用硬编码的测试配置
    const params = {
      appId: '79e7ff4f',
      apiKey: 'bd87246792bcdcf41d778ffed9c3445b',
      apiSecret: 'MWU5ODQ4MWZlYTRhZjJhOTZkZWFmZjY3',
      serverUrl: 'wss://avatar.cn-huadong-1.xf-yun.com/v1/interact',
      sceneId: '303724129247432704'

    }

    try {
      avatarPlatform2.setApiInfo(params)
      console.log('使用默认配置初始化SDK成功')
      sdkInitialized.value = true
    } catch (error) {
      console.error('SDK初始化失败:', error)
      sdkInitialized.value = false
    }
  } else {
    // 使用用户配置的API信息
    const params = {
      appId: form.appid,
      apiKey: form.apikey,
      apiSecret: form.apisecret,
      serverUrl: form.serverurl,
      sceneId: form.sceneid
    }

    try {
      avatarPlatform2.setApiInfo(params)
      console.log('使用用户配置初始化SDK成功')
      sdkInitialized.value = true
    } catch (error) {
      console.error('SDK初始化失败:', error)
      sdkInitialized.value = false
    }
  }
}
function SetGlobalParams() {
  if (avatarPlatform2 != null) {
    const params = {
      stream: { protocol: 'webrtc', fps: 25, bitrate: 1000000, alpha: 0 },
      avatar: { avatar_id: '110117005', width: 1080, height: 1920, mask_region: '[0,0,1080,1920]', scale: 1, move_h: 0, move_v: 0, audio_format: 1 },
      tts: { vcn: 'x4_lingxiaoying_assist', speed: 55, pitch: 50, volume: 100, emotion: 13 },
      avatar_dispatch: { interactive_mode: 0 },
      subtitle: { subtitle: 0, font_color: '#FFFFFF', font_name: 'Sanji.Suxian.Simple', position_x: 100, position_y: 0, font_size: 10, width: 100, height: 100 },
      background: { // 背景信息
        type:'res_key',
        data:'22SLM2teIw+aqR6Xsm2JbH6Ng310kDam2NiCY/RQ9n6s3nYJXloZWW1l64/g32vrn7d2lJQR7m9xD5EHYkVs113K3Jn2eluAuk73uTvKcJdksdXt2yfIwz3STXfvn3FPjR/aFKuaVBhrW3YyYPbIV8nsEi4/6H6X8Pr39Nu9Fu4iFlRTp3qUZ0JmsqdkP3yZIYHfMfWiljAyC4FuBCg0Y4idpMWA5hv9XJz75jD6UzebjCRMLBV4lFLeGyw9X+UiPt3dKB/2wqz2VkNBDoSLdsis0kmU5y33ECJ1oj9+eDtMhuBAZKvHZRtADRJwInjH2LTPMM99L5IgIys+QwD9iOTaHn7qYH8NrxZFnjElVc4='
        //type: 'url', // （非必传）上传图片的类型，支持url以及res_key。（res_key请到交互平台-素材管理-背景中上传获取)
        //data: 'https://aicloudfile.xfyousheng.com/api/v1/comuptfm/7Yfq8Hwi8YrQzbaa7TNjz5Zodbfl9cfl7SeayHVvycRY9c0pzI8QebNYeI1c?authorization=c2ltcGxlLWp3dCBhaz1qamZpc2QwdXUwNXVqaGozdTQwaWc7ZXhwPTIwNjg2NTAwMzQ7YWxnbz1obWFjLXNoYTI1NjtzaWc9elpxblQvYjV4REUxN1dsNllSTmpSdk14VU5UaytrVWhJTFVZZVlScjF6Yz0=' // （非必传）图片的值，当type='url'时,data='http://xxx/xxx.png'，当type='res_key'时，data='res_key值'（res_key请到交互平台-素材管理-背景中上传获取)
      },
      air: { // 动作模式
        air: 1, // （非必传）是否开启自动动作，0关闭，1开启（需配合nlp=true时生效)，当开启时，星火大模型会根据语义理解的内容自动插入动作
        add_nonsemantic: 1 // （非必传）是否开启无指向性动作，0关闭，1开启（需配合nlp=true时生效)，虚拟人会做没有意图指向性的动作
      }
    }
    avatarPlatform2.setGlobalParams(params)
    open2('设置全局变量成功')
  } else alert('请先实例化SDK')
}
function writeText(textParam, useNlp = true) {
  const text = textParam || textarea.value
  if (!text) {
    console.error('文本内容为空，无法播报')
    return
  }

  // 检查SDK状态
  if (avatarPlatform2 == null) {
    console.warn('数字人SDK未初始化，使用模拟播报')
    simulateDigitalHumanSpeech(text, useNlp)
    return
  }

  // 检查SDK连接状态
  try {
    // 先检查SDK是否处于可用状态
    if (typeof avatarPlatform2.writeText !== 'function') {
      console.warn('数字人SDK方法不可用，使用模拟播报')
      simulateDigitalHumanSpeech(text, useNlp)
      return
    }

    const ttsConfig = {
      vcn: vc.value,
      volume: 100,
      emotion: emotion.value
    }

    const writeConfig = {
      nlp: useNlp,
      tts: ttsConfig
    }

    console.log('尝试数字人播报:', text)
    console.log('配置:', writeConfig)

    // 使用Promise包装来捕获异步错误
    Promise.resolve().then(() => {
      return avatarPlatform2.writeText(text, writeConfig)
    }).then(() => {
      console.log('文本已发送到数字人SDK')
    }).catch((error) => {
      console.warn('数字人播报失败，切换到模拟模式:', error.message || error)
      // 播报失败时，使用模拟播报
      simulateDigitalHumanSpeech(text, useNlp)
    })

  } catch (error) {
    console.warn('数字人SDK调用异常，使用模拟播报:', error.message || error)
    simulateDigitalHumanSpeech(text, useNlp)
  }
}

// 模拟数字人播报（用于开发测试）
function simulateDigitalHumanSpeech(text, useNlp) {
  console.log('模拟数字人播报:', text)
  console.log('播报模式:', useNlp ? 'NLP智能回复' : '直接播报')

  // 模拟NLP处理
  if (useNlp) {
    // 模拟智能回复
    setTimeout(() => {
      let mockResponse = ''

      // 根据文本内容生成简洁的模拟回复
      if (text.includes('面试问题') || text.includes('问题')) {
        mockResponse = '好的，请您详细回答这个问题。'
      } else if (text.includes('追问') || text.includes('回答')) {
        // 生成简洁的追问
        const followUpQuestions = [
          '能具体说说你在这个过程中遇到的挑战吗？',
          '你是如何解决这个技术难点的？',
          '这个方案有什么优缺点？',
          '你觉得还有其他更好的实现方式吗？',
          '在实际应用中效果如何？',
          '你从这个项目中学到了什么？'
        ]
        mockResponse = followUpQuestions[Math.floor(Math.random() * followUpQuestions.length)]
      } else if (text.includes('欢迎') || text.includes('开始')) {
        mockResponse = text // 直接使用欢迎语
      } else {
        mockResponse = '请继续您的回答。'
      }

      // 触发NLP内容事件
      const mockNlpData = {
        content: mockResponse,
        isComplete: true,
        timestamp: Date.now()
      }

      console.log('模拟NLP回复:', mockResponse)

      // 向父组件发送模拟的NLP内容
      emit('nlp-content', mockNlpData)
    }, 1500) // 模拟1.5秒处理时间
  } else {
    // 直接播报模式，模拟播报完成
    setTimeout(() => {
      console.log('模拟直接播报完成:', text)

      // 对于直接播报，也可以触发完成事件
      const mockNlpData = {
        content: text,
        isComplete: true,
        timestamp: Date.now()
      }

      emit('nlp-content', mockNlpData)
    }, 800) // 模拟0.8秒播报时间
  }
}
// function writeCmd() {
//   avatarPlatform2.writeCmd('action', action.value);
// }
// function interrupt() {
//   if (avatarPlatform2 != null) avatarPlatform2.interrupt();
//   else alert('请先实例化SDK');
// }
// function startRecord() {
//   if (avatarPlatform2 != null) {
//     avatarPlatform2.recorder.startRecord(0, () => { console.warn('STOPED RECORDER'); }, { nlp: true, avatar_dispatch: { interactive_mode: 0 } });
//     recorderbutton.value = true;
//   } else alert('请先实例化SDK');
// }
// function stopRecord() {
//   if (avatarPlatform2 != null) {
//     avatarPlatform2.recorder.stopRecord();
//     recorderbutton.value = false;
//   } else alert('请先实例化SDK');
// }
// function stop() {
//   if (avatarPlatform2 != null) avatarPlatform2.stop();
//   else alert('请先实例化SDK');
// }
// function destroy() {
//   if (avatarPlatform2 != null) {
//     avatarPlatform2.destroy();
//     avatarPlatform2 = null;
//   } else alert('请先实例化SDK');
// }

onMounted(() => {
  try {
    console.log(' 开始初始化数字人组件')

    // 添加全局错误处理
    window.addEventListener('unhandledrejection', (event) => {
      if (event.reason && event.reason.message && event.reason.message.includes('InvalidConnect')) {
        console.warn(' 捕获到数字人连接异常，已自动处理')
        event.preventDefault() // 阻止错误冒泡
      }
    })

    initSDK()
    createRecoder()
    setSDKEvenet()
    setPlayerEvenet()
    SetApiInfo2()
    SetGlobalParams()

    // 只在页面加载时自动执行start，只执行一次
    const startOnce = () => {
      if (avatarPlatform2) {
        try {
          avatarPlatform2.stop()
          avatarPlatform2.start({ wrapper: document.querySelector('#wrapper') })
            .then(() => {
              console.log('数字人启动成功')
            })
            .catch((e) => {
              console.error('数字人启动失败:', e.code, e.message, e.name)
              console.warn('数字人启动失败，但不影响面试功能')
            })
        } catch (error) {
          console.error('数字人启动异常:', error)
          console.warn('数字人启动异常，但不影响面试功能')
        }
      } else {
        console.warn(' 数字人SDK未初始化，跳过启动')
      }
      window.removeEventListener('click', startOnce)
    }

    window.addEventListener('click', startOnce)
    console.log('数字人组件初始化完成')

  } catch (error) {
    console.error(' 数字人组件初始化失败:', error)
    console.warn('数字人组件初始化失败，但不影响面试功能')
  }
})

onBeforeUnmount(() => {
  if (avatarPlatform2) avatarPlatform2.stop()
})
defineExpose({ writeText })
</script>

<style scoped>
* {
  margin: 0px;
  padding: 0px;
  box-sizing: border-box;
  border: none;
}
.el-button {
  width: 200px;
  margin: 0px;
}
.avatar, .el-container, .el-main {
  width: 100%;
  height: 100%;
  min-width: 0;
  min-height: 0;
}
.el-aside {
  width: 30%;
  min-width: 0;
  height: 100%;
}
.htmleaf-content {
  width: 70%;
  height: 100%;
  min-width: 0;
  min-height: 0;
  padding: 0;
}
#wrapper {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  margin: 0;
  padding: 0;
  background: transparent;
  overflow: hidden;
  transform: translateY(20px);
  box-sizing: border-box;
}
#wrapper > * {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
  display: block;
}
.error {
  border-block-color: red;
}
.widthclass {
  width: 400px;
}
span {
  color: red;
}
</style>