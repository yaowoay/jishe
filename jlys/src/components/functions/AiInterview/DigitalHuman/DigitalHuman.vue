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
import { ref, reactive, onMounted, onBeforeUnmount, defineExpose, defineEmits, defineProps, watch } from 'vue'
import AvatarPlatform, { PlayerEvents, SDKEvents } from '../vm-sdk/avatar-sdk-web_3.1.1.1011/index.js'
import axios from 'axios'

// 定义props
const props = defineProps({
  avatarConfig: {
    type: Object,
    default: () => ({
      avatarId: '110117005', // 默认数字人ID
      tone: 'professional',
      speechRate: 1.0,
      voiceType: 'female'
    })
  }
})

// 定义向父组件发送的事件
const emit = defineEmits(['nlp-content'])

// 状态
const SetApiInfodialog = ref(false)
const SetGlobalParamsdialog = ref(false)
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
// const recorderbutton = ref(false); // 未使用，先注释
// const action = ref('A_RH_hello_O'); // 未使用，先注释
const nlp = ref(true)
const emotion = ref(13)
// const volume = ref(100); // 未使用，先注释

let avatarPlatform2 = null

function open2(text) {
  // alert(text); // 禁止所有弹窗提示
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
      .on(SDKEvents.connected, function (initResp) { console.log('SDKEvent.connect:initResp:', initResp) })
      .on(SDKEvents.stream_start, function () { console.log('stream_start') })
      .on(SDKEvents.disconnected, function (err) { console.log('SDKEvent.disconnected:', err) })
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
            isComplete: isComplete,
            rawData: nlpData // 保留原始数据以便调试
          })
        } else {
          console.warn('收到的NLP数据格式不正确:', nlpData)
        }
      })
      .on(SDKEvents.frame_start, function (frame_start) { console.log('推流开始 frame_start:', frame_start) })
      .on(SDKEvents.frame_stop, function (frame_stop) { console.log('推流结束 frame_stop:', frame_stop) })
      .on(SDKEvents.error, function (error) { console.log('错误信息error:', error) })
      .on(SDKEvents.connected, function () { console.log('connected') })
      .on(SDKEvents.asr, function (asrData) { console.log('语音识别数据asr:', asrData) })
      .on(SDKEvents.tts_duration, function (ttsData) { console.log('语音合成用时tts：', ttsData) })
      .on(SDKEvents.subtitle_info, function (subtitleData) { console.log('subtitleData：', subtitleData) })
      .on(SDKEvents.action_start, function (action_start) { console.log('动作推流开始 action_start:', action_start) })
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
  if (avatarPlatform2 == null) alert('请先实例化SDK')
  else {
    //参数
    //参数
    //参数
    const params = { appId: '39055720', apiKey: 'dd3c730bfdbfea06a3c5510aebfd3886', apiSecret: 'Nzc2NDAzY2FjMjBlMzI3ODg0NjNmODE5', serverUrl: 'wss://avatar.cn-huadong-1.xf-yun.com/v1/interact', sceneId: '212871453928984576' }
    avatarPlatform2.setApiInfo(params)
    open2('初始化SDK成功')
  }
}
function SetGlobalParams() {
  if (avatarPlatform2 != null) {
    // 调试信息：打印接收到的props
    console.log('DigitalHuman组件接收到的props.avatarConfig:', props.avatarConfig)

    // 使用props中的avatarId，如果没有则使用默认值
    const avatarId = props.avatarConfig?.avatarId || '110117005'
    console.log('使用的avatarId:', avatarId)

    // 根据avatar_id获取对应的vcn音色
    const getVcnByAvatarId = (avatarId) => {
      const avatarVcnMap = {
        '110017006': 'x4_mingge',                        // 明哥音色
        '138801001': 'x4_yuexiaoni_assist',              // 悦小妮音色
        '110021006': 'x4_lingxiaoyu_assist',             // 灵小雨音色
        '110117005': 'x4_lingxiaoqi_oral'
      }
      return avatarVcnMap[avatarId] || 'x4_lingxiaoqi_oral'
    }

    const vcn = getVcnByAvatarId(avatarId)

    // 根据tone获取对应的emotion值
    const getEmotionByTone = (tone) => {
      const emotionMap = {
        'formal': 8,         // 正式严肃 - 严肃/正式的语调
        'friendly': 13,      // 温和友好 - 友好/亲和的语调
        'casual': 1,         // 轻松幽默 - 高兴/愉悦的语调
        'professional': 12   // 专业严谨 - 专业/商务的语调
      }
      return emotionMap[tone] || 13 // 默认友好
    }

    const emotionValue = getEmotionByTone(props.avatarConfig?.tone || 'professional')

    // 更新vc变量和emotion值
    vc.value = vcn
    emotion.value = emotionValue
    console.log('更新vc变量为:', vc.value)
    console.log('更新emotion值为:', emotion.value, '(对应tone:', props.avatarConfig?.tone || 'professional', ')')

    // 同时更新setglobalparamsform中的配置，防止被覆盖
    setglobalparamsform.tts.vcn = vcn
    setglobalparamsform.tts.emotion = emotionValue
    setglobalparamsform.avatar.avatar_id = avatarId
    console.log('更新setglobalparamsform.tts.vcn为:', setglobalparamsform.tts.vcn)
    console.log('更新setglobalparamsform.tts.emotion为:', setglobalparamsform.tts.emotion)

    const params = {
      stream: { protocol: 'webrtc', fps: 25, bitrate: 1000000, alpha: 0 },
      avatar: { avatar_id: avatarId, width: 1080, height: 1920, mask_region: '[0,0,1080,1920]', scale: 1, move_h: 0, move_v: 0, audio_format: 1 },
      tts: { vcn: vcn, speed:60, pitch: 50, volume: 100, emotion: emotionValue },
      avatar_dispatch: { interactive_mode: 0 },
      subtitle: { subtitle: 0, font_color: '#FFFFFF', font_name: 'Sanji.Suxian.Simple', position_x: 100, position_y: 0, font_size: 10, width: 100, height: 100 },
      background:{//背景信息
        type:'url',//（非必传）上传图片的类型，支持url以及res_key。（res_key请到交互平台-素材管理-背景中上传获取)
        data:'https://aicloudfile.xfyousheng.com/api/v1/comuptfm/7Yfq8Hwi8YrQzbaa7TNjz5Zodbfl9cfl7SeayHVvycRY9c0pzI8QebNYeI1c?authorization=c2ltcGxlLWp3dCBhaz1qamZpc2QwdXUwNXVqaGozdTQwaWc7ZXhwPTIwNjg2NTAwMzQ7YWxnbz1obWFjLXNoYTI1NjtzaWc9elpxblQvYjV4REUxN1dsNllSTmpSdk14VU5UaytrVWhJTFVZZVlScjF6Yz0='//（非必传）图片的值，当type='url'时,data='http://xxx/xxx.png'，当type='res_key'时，data='res_key值'（res_key请到交互平台-素材管理-背景中上传获取)
      },
      air:{//动作模式
        air:1,//（非必传）是否开启自动动作，0关闭，1开启（需配合nlp=true时生效)，当开启时，星火大模型会根据语义理解的内容自动插入动作
        add_nonsemantic:1//（非必传）是否开启无指向性动作，0关闭，1开启（需配合nlp=true时生效)，虚拟人会做没有意图指向性的动作
      },
      // 添加文档问答配置，解决 docIds and dbList can not be both null 错误
      docqa: {
        // 可以配置文档ID列表或数据库列表，至少配置一个
        docIds: [], // 文档ID列表，如果不使用可以设为空数组
        dbList: ['default'], // 数据库列表，设置一个默认值避免为null
        // 或者完全禁用文档问答功能
        enabled: false
      }
    }
    avatarPlatform2.setGlobalParams(params)
    console.log('数字人配置已设置，avatar_id:', avatarId, ', vcn音色:', vcn, ', emotion值:', emotionValue, ', tone风格:', props.avatarConfig?.tone)
    open2('设置全局变量成功')
  } else alert('请先实例化SDK')
}
function writeText(textParam, useNlp = true) {
  if (avatarPlatform2 != null) {
    const text = textParam || textarea.value
    if (!text) { alert('内容不许为空'); return }

    console.log('writeText调用，使用的vcn音色:', vc.value)
    console.log('当前avatarConfig:', props.avatarConfig)

    avatarPlatform2.writeText(text, { nlp: true, tts: { vcn: vc.value, volume: 100, emotion: emotion.value } })
  } else alert('请先实例化SDK')
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

// 监听avatarConfig变化，重新设置参数
watch(() => props.avatarConfig, (newConfig, oldConfig) => {
  console.log('avatarConfig发生变化:', { newConfig, oldConfig })
  if (newConfig && avatarPlatform2) {
    // 延迟一点时间确保SDK已经初始化完成
    setTimeout(() => {
      SetGlobalParams()
    }, 1000)
  }
}, { deep: true, immediate: false })

onMounted(() => {
  initSDK()
  createRecoder()
  setSDKEvenet()
  setPlayerEvenet()
  SetApiInfo2()
  SetGlobalParams()
  // 只在页面加载时自动执行start，只执行一次
  const startOnce = () => {
    if (avatarPlatform2) {
      avatarPlatform2.stop()
      avatarPlatform2.start({ wrapper: document.querySelector('#wrapper') }).catch((e) => { console.error(e.code, e.message, e.name, e.stack) })
    } else {
      alert('请先实例化SDK')
    }
    window.removeEventListener('click', startOnce)
  }
  window.addEventListener('click', startOnce)
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