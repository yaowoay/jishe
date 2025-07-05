package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.config.Message;
import com.coldwind.easyoj.config.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.Principal;

@RestController
public class RedisMessageController {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageController.class);

    private String topicName="topic-test";
 
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
 
    @Autowired
    private SimpUserRegistry userRegistry;

    @MessageMapping("/message")
    public void subscription(Message message, Principal principal) throws Exception {
        this.sendToUser(message, "/queue");
        System.out.println(principal.getName() + "发送了一条消息给：" + message.getTo());
    }

 
    /**
     * 给指定用户发送消息，并处理接收者不在线的情况
     * @param message 消息
     * @param destination 目的地
     */
    private void sendToUser(Message message, String destination){
        SimpUser simpUser = userRegistry.getUser(message.getTo());
 
        //如果接收者存在，则发送消息
        if(simpUser != null && StringUtils.isNotEmpty(simpUser.getName())){
            messagingTemplate.convertAndSendToUser(message.getTo(), destination, message.getMessage());
        }
    }
 

    // 用法一：
    @MessageMapping("/send")
    public void sendAll(@RequestParam String msg) {

        log.info("[发送消息]>>>> msg: {}", msg);

        // 发送消息给客户端
        // 第一个参数是浏览器中订阅消息的地址，第二个参数是消息本身
        messagingTemplate.convertAndSend("/topic/public", msg);
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message  chatMessage) {
        return chatMessage;
    }
    @MessageMapping("/chatPlayAudio")
    public void playAudio(Message  message, Principal principal) throws Exception {
        try {
            InputStream inputStream= (InputStream) message.getMessage();
            byte[] buff = new byte[inputStream.available()];
            inputStream.read(buff, 0, inputStream.available());
            messagingTemplate.convertAndSendToUser(message.getTo(), "/playAudio", ByteBuffer.wrap(buff));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}