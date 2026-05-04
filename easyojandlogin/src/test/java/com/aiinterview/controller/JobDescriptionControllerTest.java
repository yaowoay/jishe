//package com.aiinterview.controller;
//
//import com.aiinterview.dto.JobDescriptionRequest;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * 职位描述生成控制器测试类
// */
//@SpringBootTest
//@AutoConfigureTestMvc
//public class JobDescriptionControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void testGenerateJobDescriptionWithFormData() throws Exception {
//        mockMvc.perform(post("/api/job-description/generate")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("job", "Java后端开发工程师")
//                .param("jobtype", "全职")
//                .param("salary", "15K-25K")
//                .param("education", "本科")
//                .param("experience", "3-5年")
//                .param("place", "北京市朝阳区")
//                .param("description", "负责后端系统开发和维护")
//                .param("requirement", "熟悉Java开发，有分布式系统经验")
//                .param("user", "test-user"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.success").exists());
//    }
//
//    @Test
//    public void testGenerateJobDescriptionWithJson() throws Exception {
//        JobDescriptionRequest request = new JobDescriptionRequest();
//        request.setJob("Java后端开发工程师");
//        request.setJobtype("全职");
//        request.setSalary("15K-25K");
//        request.setEducation("本科");
//        request.setExperience("3-5年");
//        request.setPlace("北京市朝阳区");
//        request.setDescription("负责后端系统开发和维护");
//        request.setRequirement("熟悉Java开发，有分布式系统经验");
//        request.setUser("test-user");
//
//        mockMvc.perform(post("/api/job-description/generate-json")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.success").exists());
//    }
//
//    @Test
//    public void testGenerateJobDescriptionWithMissingParameters() throws Exception {
//        mockMvc.perform(post("/api/job-description/generate")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("job", "Java后端开发工程师")
//                // 缺少其他必填参数
//                .param("user", "test-user"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testHealthCheck() throws Exception {
//        mockMvc.perform(get("/api/job-description/health"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.status").value("OK"))
//                .andExpect(jsonPath("$.message").exists())
//                .andExpect(jsonPath("$.external_api").exists());
//    }
//
//    @Test
//    public void testGetWorkflowStatus() throws Exception {
//        String workflowRunId = "test-workflow-run-id";
//
//        mockMvc.perform(get("/api/job-description/status/{workflowRunId}", workflowRunId)
//                .param("user", "test-user"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.success").exists());
//    }
//
//    @Test
//    public void testGenerateJobDescriptionWithInvalidJson() throws Exception {
//        JobDescriptionRequest request = new JobDescriptionRequest();
//        // 只设置部分必填字段，应该触发验证错误
//        request.setJob("Java后端开发工程师");
//        request.setUser("test-user");
//
//        mockMvc.perform(post("/api/job-description/generate-json")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//}
