package com.aiinterview.service.impl.exam;

import com.aiinterview.service.exam.AnswerStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerStorageServiceImpl implements AnswerStorageService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private static final String ANSWER_KEY_PREFIX = "exam:answer:";

    @Override
    public void storeAnswer(String taskId, String questionId, String userAnswer) {
        String key = ANSWER_KEY_PREFIX + taskId + ":" + questionId;
        redisTemplate.opsForValue().set(key, userAnswer);
        log.info("存储答案到Redis: key={}, value={}", key, userAnswer);
    }

    @Override
    public String retrieveAnswer(String taskId, String questionId) {
        String key = ANSWER_KEY_PREFIX + taskId + ":" + questionId;
        String answer = redisTemplate.opsForValue().get(key);
        log.info("从Redis获取答案: key={}, value={}", key, answer);
        return answer;
    }
}
