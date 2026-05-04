package com.aiinterview.aspect;

import com.aiinterview.model.entity.application.Application;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * 用户行为记录切面
 * 自动记录用户的浏览、投递、申请等行为到日志文件
 */
@Slf4j
@Aspect
@Component
public class UserActionLogAspect {

    // 专门用于用户行为日志的 Logger
    private static final Logger USER_ACTION_LOGGER = LoggerFactory.getLogger("USER_ACTION_LOGGER");
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 切点：职位详情查看
     * 匹配 JobController 的 getJobById 方法
     */
    @Pointcut("execution(* com.aiinterview.controller.job.JobController.getJobById(..))")
    public void jobViewPointcut() {}

    /**
     * 切点：简历投递
     * 匹配 ApplicationController 的 submitApplication 方法
     */
    @Pointcut("execution(* com.aiinterview.controller.application.ApplicationController.submitApplication(..))")
    public void applicationSubmitPointcut() {}

    /**
     * 切点：职位收藏
     * 匹配 JobCollectionController 的 collectJob 方法
     */
    @Pointcut("execution(* com.aiinterview.controller.JobCollectionController.collectJob(..))")
    public void jobCollectPointcut() {}

    /**
     * 记录职位浏览行为
     */
    @AfterReturning(pointcut = "jobViewPointcut()", returning = "result")
    public void logJobView(JoinPoint joinPoint, Object result) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            
            // 获取用户ID
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return;
            }

            // 获取职位ID（从方法参数中获取）
            Object[] args = joinPoint.getArgs();
            Long jobId = null;
            if (args.length > 0 && args[0] instanceof Long) {
                jobId = (Long) args[0];
            }

            if (jobId == null) {
                return;
            }

            // 记录浏览行为
            logUserAction(userId, jobId, "VIEW", 1);
            
        } catch (Exception e) {
            log.error("记录职位浏览行为失败", e);
        }
    }

    /**
     * 记录简历投递行为
     */
    @AfterReturning(pointcut = "applicationSubmitPointcut()", returning = "result")
    public void logApplicationSubmit(JoinPoint joinPoint, Object result) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();

            // 获取用户ID
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return;
            }

            // 从返回结果中获取职位ID
            Long jobId = null;
            if (result != null) {
                try {
                    // 假设返回的是 ApiResponse<Application>
                    Object data = result.getClass().getMethod("getData").invoke(result);
                    if (data instanceof Application) {
                        Application application = (Application) data;
                        jobId = application.getJobId();
                    }
                } catch (Exception e) {
                    log.debug("无法从返回结果中提取职位ID", e);
                }
            }

            if (jobId == null) {
                return;
            }

            // 记录投递行为
            logUserAction(userId, jobId, "APPLY", 2);

        } catch (Exception e) {
            log.error("记录简历投递行为失败", e);
        }
    }

    /**
     * 记录职位收藏行为
     */
    @AfterReturning(pointcut = "jobCollectPointcut()", returning = "result")
    public void logJobCollect(JoinPoint joinPoint, Object result) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();

            // 获取用户ID
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return;
            }

            // 从方法参数中获取 payload
            Object[] args = joinPoint.getArgs();
            Long jobId = null;

            if (args.length > 0 && args[0] instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Integer> payload = (java.util.Map<String, Integer>) args[0];
                Integer jobIdInt = payload.get("jobId");
                if (jobIdInt != null) {
                    jobId = jobIdInt.longValue();
                }
            }

            if (jobId == null) {
                return;
            }

            // 检查返回结果是否成功
            boolean success = false;
            if (result != null) {
                try {
                    Object code = result.getClass().getMethod("getCode").invoke(result);
                    success = Integer.valueOf(0).equals(code);
                } catch (Exception e) {
                    log.debug("无法从返回结果中提取状态码", e);
                }
            }

            // 只有收藏成功才记录日志
            if (success) {
                logUserAction(userId, jobId, "COLLECT", 3);
            }

        } catch (Exception e) {
            log.error("记录职位收藏行为失败", e);
        }
    }

    /**
     * 记录用户行为到日志文件
     * 
     * @param userId 用户ID
     * @param jobId 职位ID
     * @param actionType 行为类型（VIEW/APPLY/COLLECT）
     * @param score 行为分数
     */
    private void logUserAction(Long userId, Long jobId, String actionType, int score) {
        LocalDateTime now = LocalDateTime.now();
        String dt = now.format(DATE_FORMATTER);
        String eventTime = now.format(DATETIME_FORMATTER);

        // 使用 Logstash 的结构化参数记录日志
        USER_ACTION_LOGGER.info("USER_ACTION",
                kv("userId", String.valueOf(userId)),
                kv("jobId", String.valueOf(jobId)),
                kv("actionType", actionType),
                kv("score", score),
                kv("dt", dt),
                kv("eventTime", eventTime),
                kv("duration", 0)
        );
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            // 尝试从 Header 中获取 userId
            String userIdHeader = request.getHeader("userId");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                return Long.valueOf(userIdHeader);
            }

            // 尝试从 JWT Token 中获取
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                return jwtUtils.getUserIdFromToken(token);
            }

            return null;
        } catch (Exception e) {
            log.debug("获取用户ID失败", e);
            return null;
        }
    }
}
