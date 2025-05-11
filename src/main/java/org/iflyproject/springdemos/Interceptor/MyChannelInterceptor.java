package org.iflyproject.springdemos.Interceptor;

import org.iflyproject.springdemos.config.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 自定义{@link ChannelInterceptor}，实现断开连接的处理
 */

@Component

public class MyChannelInterceptor implements ChannelInterceptor {
    @Autowired
    @Lazy
    private SimpMessagingTemplate messagingTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String sessionId = accessor.getSessionId();
        if (StompCommand.CONNECT.equals(accessor.getCommand())){
            List<String> tokens = accessor.getNativeHeader("user");
             if(tokens!=null&&tokens.size()>0) {
                 LoginUser loginUser=new LoginUser();
                 loginUser.setName(tokens.get(0));
                 accessor.setUser(loginUser);
             }
        }
        return message;
    }
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

//用户已经断开连接
        if (StompCommand.DISCONNECT.equals(command)) {
            LoginUser principal = (LoginUser) accessor.getUser();
            logger.debug(MessageFormat.format("用户{0}的WebSocket连接已经断开", principal));
        }

    }


}