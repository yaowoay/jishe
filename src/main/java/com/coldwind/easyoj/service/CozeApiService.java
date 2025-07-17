package com.coldwind.easyoj.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CozeApiService {

    @Value("${coze.api.base-url}")
    private String baseUrl;

    @Value("${coze.api.bot-id}")
    private String botId;

    @Value("${coze.api.auth-token}")
    private String authToken;

    private final WebClient webClient;

    public CozeApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getBotOnlineInfo() {
        String url = baseUrl + "/bot/get_online_info?bot_id=" + botId;
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
} 