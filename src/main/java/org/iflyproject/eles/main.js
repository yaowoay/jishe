const socket = new WebSocket('wss://spark-api.xf-yun.com/v1/x1');

socket.onopen = () => {
    console.log('WebSocket connection established');
};

socket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    // 根据返回数据的类型处理不同的逻辑
    if (data.type === 'text') {
        displayText(data.content);
    } else if (data.type === 'image') {
        displayImage(data.content);
    }
};

socket.onclose = () => {
    console.log('WebSocket connection closed');
};

function displayText(text) {
    const textContainer = document.getElementById('text-container');
    textContainer.innerText = text;
}

function displayImage(imageUrl) {
    const imageContainer = document.getElementById('image-container');
    const img = document.createElement('img');
    img.src = imageUrl;
    imageContainer.appendChild(img);
}const socket = new WebSocket('')