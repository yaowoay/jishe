//调用讯飞sparkx1模型
//暂未设置键盘事件
//逻辑设计有错误还未更改

const APPID = '96c077b7';
const API_SECRET = 'NTY1NDg5MzZmYTE3MGU5YzBmMTZkYjMw';
const API_KEY = '8cf73945dd152d12c504928d26cf529e';
const httpUrl = new URL("wss://spark-api.xf-yun.com/v1/x1");
let modelDomain;
modelDomain = "x1";

function getWebsocketUrl() {
    return new Promise((resolve, reject) => {
        const apiKey = API_KEY;
        const apiSecret = API_SECRET;
        let url = 'wss://' + httpUrl.host + httpUrl.pathname;
        const host = location.host;
        const date = new Date().toGMTString();
        const algorithm = 'hmac-sha256';
        const headers = 'host date request-line';
        const signatureOrigin = `host: ${host}\ndate: ${date}\nGET ${httpUrl.pathname} HTTP/1.1`;
        const CryptoJS = window.CryptoJS;
        const signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
        const signature = CryptoJS.enc.Base64.stringify(signatureSha);
        const authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
        const authorization = btoa(authorizationOrigin);
        url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
        resolve(url);
    });
}

function connectWebSocket(inputText) {
   
    getWebsocketUrl().then(url => {
        let ttsWS; //ttsWS是讯飞语音语音接口
        if ('WebSocket' in window) {
            ttsWS = new WebSocket(url);
        } else if ('MozWebSocket' in window) {
            ttsWS = new MozWebSocket(url);
        } else {
            alert('浏览器不支持 WebSocket');
            return;
        }

        ttsWS.onopen = () => {
            const params = {
                "header": {
                    "app_id": APPID,
                    "uid": "fd3f47e4-d"
                },
                "parameter": {
                    "chat": {
                        "domain": modelDomain,
                        "temperature": 0.5,
                        "max_tokens": 1024
                    }
                },
                "payload": {
                    "message": {
                        "text":inputText
                    }
                }
            };
            ttsWS.send(JSON.stringify(params));
        };
        
        
        ttsWS.onmessage = (e) => {//事件信息
            marked.setOptions(marked.getDefaults()); 
            const messageContainer = document.getElementById('chatMessages');
            console.log(messageContainer.scrollTop)
            messageContainer.scrollTop = messageContainer.scrollHeight;
            const jsonData = JSON.parse(e.data);
            const reasoningContent = jsonData.payload.choices.text[0].reasoning_content;
            const normalContent = jsonData.payload.choices.text[0].content;
            // console.log(jsonData.payload.choices.text[0])
            if (reasoningContent) {
                var html = document.getElementById(message_data.length +'s').innerHTML + jsonData.payload.choices.text[0].reasoning_content;
                    html = marked.parse(html);
                    
                document.getElementById(message_data.length +'s').innerHTML = html;
                
            }
            if (normalContent) {
                var htmls = document.getElementById(message_data.length).innerHTML + jsonData.payload.choices.text[0].content;;
                    htmls = marked.parse(htmls);
                    console.log(htmls)
                document.getElementById(message_data.length).innerHTML = htmls;
                const event = new CustomEvent('receiveAnswer', { detail: normalContent });
                document.dispatchEvent(event);
            }
        };

        ttsWS.onerror = (e) => {
            alert('WebSocket 报错，请 F12 查看详情');
            console.error(`详情查看：${encodeURI(url.replace('wss:', 'https:'))}`);
        };

        ttsWS.onclose = (e) => {
            console.log('WebSocket 连接已关闭');
        };
    });
}
    