package com.aiinterview.config;

import com.aiinterview.constants.WorkflowType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DifyApiConfig 方法测试类
 * 验证新添加的基于枚举的方法是否正常工作
 */
@SpringBootTest
public class DifyApiConfigMethodTest {

    private final DifyApiConfig difyApiConfig = new DifyApiConfig();

    @Test
    public void testGetAuthorizationHeaderWithWorkflowType() {
        // 测试职位描述工作流
        String jobDescAuth = difyApiConfig.getAuthorizationHeader(WorkflowType.JOB_DESCRIPTION);
        assertNotNull(jobDescAuth);
        assertTrue(jobDescAuth.startsWith("Bearer "));
        
        // 测试简历工作流
        String resumeAuth = difyApiConfig.getAuthorizationHeader(WorkflowType.RESUME);
        assertNotNull(resumeAuth);
        assertTrue(resumeAuth.startsWith("Bearer "));
        
        // 验证两个工作流使用不同的API Key
        assertNotEquals(jobDescAuth, resumeAuth);
        
        System.out.println("职位描述工作流 Auth: " + jobDescAuth);
        System.out.println("简历分析工作流 Auth: " + resumeAuth);
    }

    @Test
    public void testGetApiKeyWithWorkflowType() {
        // 测试职位描述工作流
        String jobDescApiKey = difyApiConfig.getApiKey(WorkflowType.JOB_DESCRIPTION);
        assertNotNull(jobDescApiKey);
        assertTrue(jobDescApiKey.startsWith("app-"));
        
        // 测试简历工作流
        String resumeApiKey = difyApiConfig.getApiKey(WorkflowType.RESUME);
        assertNotNull(resumeApiKey);
        assertTrue(resumeApiKey.startsWith("app-"));
        
        // 验证两个工作流使用不同的API Key
        assertNotEquals(jobDescApiKey, resumeApiKey);
        
        System.out.println("职位描述工作流 API Key: " + jobDescApiKey);
        System.out.println("简历分析工作流 API Key: " + resumeApiKey);
    }

    @Test
    public void testGetWorkflowIdWithWorkflowType() {
        // 测试职位描述工作流
        String jobDescWorkflowId = difyApiConfig.getWorkflowId(WorkflowType.JOB_DESCRIPTION);
        assertNotNull(jobDescWorkflowId);
        assertFalse(jobDescWorkflowId.isEmpty());
        
        // 测试简历工作流
        String resumeWorkflowId = difyApiConfig.getWorkflowId(WorkflowType.RESUME);
        assertNotNull(resumeWorkflowId);
        assertFalse(resumeWorkflowId.isEmpty());
        
        // 验证两个工作流使用不同的工作流ID
        assertNotEquals(jobDescWorkflowId, resumeWorkflowId);
        
        System.out.println("职位描述工作流 ID: " + jobDescWorkflowId);
        System.out.println("简历分析工作流 ID: " + resumeWorkflowId);
    }

    @Test
    public void testGetAuthorizationHeaderByCode() {
        // 测试通过代码获取Authorization头部
        String jobDescAuth = difyApiConfig.getAuthorizationHeaderByCode("job_description");
        assertNotNull(jobDescAuth);
        assertTrue(jobDescAuth.startsWith("Bearer "));
        
        String resumeAuth = difyApiConfig.getAuthorizationHeaderByCode("resume");
        assertNotNull(resumeAuth);
        assertTrue(resumeAuth.startsWith("Bearer "));
        
        // 验证结果与直接使用枚举的结果一致
        assertEquals(jobDescAuth, difyApiConfig.getAuthorizationHeader(WorkflowType.JOB_DESCRIPTION));
        assertEquals(resumeAuth, difyApiConfig.getAuthorizationHeader(WorkflowType.RESUME));
    }

    @Test
    public void testGetApiKeyByCode() {
        // 测试通过代码获取API Key
        String jobDescApiKey = difyApiConfig.getApiKeyByCode("job_description");
        assertNotNull(jobDescApiKey);
        assertTrue(jobDescApiKey.startsWith("app-"));
        
        String resumeApiKey = difyApiConfig.getApiKeyByCode("resume");
        assertNotNull(resumeApiKey);
        assertTrue(resumeApiKey.startsWith("app-"));
        
        // 验证结果与直接使用枚举的结果一致
        assertEquals(jobDescApiKey, difyApiConfig.getApiKey(WorkflowType.JOB_DESCRIPTION));
        assertEquals(resumeApiKey, difyApiConfig.getApiKey(WorkflowType.RESUME));
    }

    @Test
    public void testIsValidWorkflowType() {
        // 测试有效的工作流类型
        assertTrue(difyApiConfig.isValidWorkflowType("job_description"));
        assertTrue(difyApiConfig.isValidWorkflowType("resume"));
        
        // 测试无效的工作流类型
        assertFalse(difyApiConfig.isValidWorkflowType("invalid_type"));
        assertFalse(difyApiConfig.isValidWorkflowType(""));
        assertFalse(difyApiConfig.isValidWorkflowType(null));
    }

    @Test
    public void testInvalidWorkflowCodeThrowsException() {
        // 测试无效的工作流代码会抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            difyApiConfig.getAuthorizationHeaderByCode("invalid_code");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            difyApiConfig.getApiKeyByCode("invalid_code");
        });
    }

    @Test
    public void testMethodConsistency() {
        // 验证不同方法返回的结果一致性
        
        // 职位描述工作流
        String jobDescAuth1 = difyApiConfig.getAuthorizationHeader(WorkflowType.JOB_DESCRIPTION);
        String jobDescAuth2 = difyApiConfig.getAuthorizationHeaderByCode("job_description");
        assertEquals(jobDescAuth1, jobDescAuth2);
        
        // 简历分析工作流
        String resumeAuth1 = difyApiConfig.getAuthorizationHeader(WorkflowType.RESUME);
        String resumeAuth2 = difyApiConfig.getAuthorizationHeaderByCode("resume");
        assertEquals(resumeAuth1, resumeAuth2);
    }
}
