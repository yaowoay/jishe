package com.aiinterview.service.impl.writtenTest;

import com.aiinterview.config.DifyApiConfig;
import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationRequest;
import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationResponse;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.model.entity.resume.Resume;
import com.aiinterview.model.entity.writtenTest.WrittenTestResult;
import com.aiinterview.constants.WorkflowType;
import com.aiinterview.mapper.ApplicationMapper;
import com.aiinterview.mapper.ResumeMapper;
import com.aiinterview.repository.writtenTest.WrittenTestResultRepository;
import com.aiinterview.service.application.ApplicationService;
import com.aiinterview.service.resume.ResumeService;
import com.aiinterview.service.writtenTest.WrittenTestGenerationService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 笔试生成服务实现类
 */
@Slf4j
@Service
public class WrittenTestGenerationServiceImpl implements WrittenTestGenerationService {

    @Autowired
    private WrittenTestResultRepository writtenTestResultRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private DifyApiConfig difyApiConfig;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    private final RestTemplate restTemplate;

    public WrittenTestGenerationServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public WrittenTestGenerationResponse generateWrittenTest(WrittenTestGenerationRequest request) {
        log.info("开始生成笔试题目，申请ID: {}, 简历ID: {}", request.getApplicationId(), request.getResumeId());

        try {
            // 1. 获取简历记录（用于AI生成模式）
            Resume resume = null;
            if (request.getAiGenerated() && request.getResumeId() != null) {
                resume = getResumeById(request.getResumeId());
                if (resume == null) {
                    throw new RuntimeException("简历记录不存在");
                }
            }

            // 2. 检查是否已存在笔试结果
            WrittenTestResult existing = writtenTestResultRepository.findByApplicationId(request.getApplicationId());
            if (existing != null) {
                log.info("申请ID {} 已存在笔试结果，将更新现有记录", request.getApplicationId());
            }

            // 3. 创建或更新笔试结果记录
            WrittenTestResult testResult = existing != null ? existing : new WrittenTestResult();
            testResult.setApplicationId(request.getApplicationId());
            testResult.setResumeId(request.getResumeId());
            testResult.setJobPosition(request.getJobPosition());
            testResult.setTotalQuestions(request.getTotalQuestions());
            testResult.setChoiceQuestions(request.getChoiceQuestions());
            testResult.setTrueFalseQuestions(request.getTrueFalseQuestions());
            testResult.setTotalScore(request.getTotalScore());
            testResult.setGenerationStatus("pending");
            testResult.setErrorMessage(null);

            if (existing == null) {
                writtenTestResultRepository.insert(testResult);
            } else {
                writtenTestResultRepository.updateById(testResult);
            }

            // 4. 如果是AI生成模式，调用Dify工作流
            if (request.getAiGenerated()) {
                return generateWithDifyWorkflow(testResult, request, resume);
            } else {
                // 自定义模式，直接返回成功状态
                testResult.setGenerationStatus("success");
                testResult.setCandidateField("自定义");
                testResult.setQuestionsJson("{}"); // 空的题目JSON，由前端处理
                writtenTestResultRepository.updateById(testResult);

                return buildResponse(testResult, new ArrayList<>());
            }

        } catch (Exception e) {
            log.error("生成笔试题目失败", e);
            throw new RuntimeException("生成笔试题目失败: " + e.getMessage());
        }
    }

    /**
     * 使用Dify工作流生成笔试题目
     */
    private WrittenTestGenerationResponse generateWithDifyWorkflow(WrittenTestResult testResult,
                                                                  WrittenTestGenerationRequest request,
                                                                  Resume resume) {
        try {
            // 1. 上传简历文件到Dify
            String fileId = uploadResumeToExternalApi(resume, request.getUser());
            if (fileId == null) {
                throw new RuntimeException("简历文件上传失败");
            }

            // 2. 调用笔试生成工作流
            String workflowResponse = callWrittenTestWorkflow(fileId, request);

            // 3. 解析工作流响应
            JSONObject responseJson = JSON.parseObject(workflowResponse);
            if (responseJson.containsKey("data")) {
                JSONObject data = responseJson.getJSONObject("data");

                // 获取工作流运行ID
                String workflowRunId = data.getString("workflow_run_id");
                testResult.setWorkflowRunId(workflowRunId);

                // 解析输出结果
                if (data.containsKey("outputs")) {
                    JSONObject outputs = data.getJSONObject("outputs");
                    if (outputs.containsKey("bs_question")) {
                        JSONObject bsQuestion = outputs.getJSONObject("bs_question");

                        // 保存题目JSON
                        testResult.setQuestionsJson(bsQuestion.toJSONString());
                        testResult.setGenerationStatus("success");

                        // 解析候选人领域（从工作流的中间结果获取）
                        // 这里需要根据实际的工作流输出结构来调整
                        testResult.setCandidateField("AI生成");

                        writtenTestResultRepository.updateById(testResult);

                        // 解析题目列表
                        List<WrittenTestGenerationResponse.QuestionDTO> questions = parseQuestions(bsQuestion);

                        return buildResponse(testResult, questions);
                    }
                }
            }

            throw new RuntimeException("工作流响应格式不正确");

        } catch (Exception e) {
            log.error("调用Dify工作流失败", e);
            testResult.setGenerationStatus("failed");
            testResult.setErrorMessage(e.getMessage());
            writtenTestResultRepository.updateById(testResult);
            throw new RuntimeException("AI生成笔试题目失败: " + e.getMessage());
        }
    }

    /**
     * 上传简历文件到外部API
     */
    private String uploadResumeToExternalApi(Resume resume, String user) {
        try {
            // 获取简历文件路径
            String filePath = resume.getFileUrl();
            if (filePath == null || filePath.isEmpty()) {
                throw new RuntimeException("简历文件路径为空");
            }

            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("简历文件不存在: " + filePath);
            }

            String uploadUrl = difyApiConfig.getFileUploadUrl();
            WorkflowType workflowType = WorkflowType.WRITTEN_TEST;

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", difyApiConfig.getAuthorizationHeader(workflowType));

            // 创建文件资源
            FileSystemResource fileResource = new FileSystemResource(file);

            // 构建multipart请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("user", user);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("上传简历文件到外部API: {}", uploadUrl);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                if (responseJson.containsKey("id")) {
                    String fileId = responseJson.getString("id");
                    log.info("简历文件上传成功，文件ID: {}", fileId);
                    return fileId;
                }
            }

            log.error("简历文件上传失败，响应状态: {}, 响应内容: {}",
                     response.getStatusCode(), response.getBody());
            return null;

        } catch (Exception e) {
            log.error("上传简历文件失败", e);
            return null;
        }
    }

    /**
     * 调用笔试生成工作流
     */
    private String callWrittenTestWorkflow(String fileId, WrittenTestGenerationRequest request) {
        try {
            String workflowUrl = difyApiConfig.getWorkflowRunUrl();
            WorkflowType workflowType = WorkflowType.WRITTEN_TEST;

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", difyApiConfig.getAuthorizationHeader(workflowType));

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();

            // 构建inputs
            Map<String, Object> inputs = new HashMap<>();

            // cv_two参数 - 简历文件列表
            List<Map<String, Object>> fileList = new ArrayList<>();
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("transfer_method", "local_file");
            fileInfo.put("upload_file_id", fileId);
            fileInfo.put("type", "document");
            fileList.add(fileInfo);
            inputs.put("cv_two", fileList);

            // bs参数 - 职位描述
            inputs.put("bs", request.getJobDescription() != null ? request.getJobDescription() : request.getJobPosition());

            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", request.getResponseMode());
            requestBody.put("user", request.getUser());

            String requestBodyJson = JSON.toJSONString(requestBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);

            log.info("调用笔试生成工作流: {}", workflowUrl);
            log.info("请求体: {}", requestBodyJson);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(workflowUrl, requestEntity, String.class);

            log.info("工作流API响应状态: {}", response.getStatusCode());
            log.info("工作流API响应内容: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("工作流调用失败，状态码: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("调用笔试生成工作流失败", e);
            throw new RuntimeException("调用工作流失败: " + e.getMessage());
        }
    }

    /**
     * 解析题目JSON
     */
    private List<WrittenTestGenerationResponse.QuestionDTO> parseQuestions(JSONObject questionsJson) {
        List<WrittenTestGenerationResponse.QuestionDTO> questions = new ArrayList<>();

        try {
            if (questionsJson.containsKey("questions")) {
                JSONArray questionsArray = questionsJson.getJSONArray("questions");

                for (int i = 0; i < questionsArray.size(); i++) {
                    JSONObject questionObj = questionsArray.getJSONObject(i);

                    WrittenTestGenerationResponse.QuestionDTO question = WrittenTestGenerationResponse.QuestionDTO.builder()
                            .questionId(questionObj.getString("questionId"))
                            .type(questionObj.getString("type"))
                            .answer(questionObj.getString("answer"))
                            .explanation(questionObj.getString("explanation"))
                            .build();

                    if ("choice".equals(question.getType())) {
                        question.setQuestionContent(questionObj.getString("questionContent"));
                        question.setOptionA(questionObj.getString("A"));
                        question.setOptionB(questionObj.getString("B"));
                        question.setOptionC(questionObj.getString("C"));
                        question.setOptionD(questionObj.getString("D"));
                    } else if ("true_false".equals(question.getType())) {
                        question.setStatement(questionObj.getString("statement"));
                    }

                    questions.add(question);
                }
            }
        } catch (Exception e) {
            log.error("解析题目JSON失败", e);
        }

        return questions;
    }

    /**
     * 构建响应对象
     */
    private WrittenTestGenerationResponse buildResponse(WrittenTestResult testResult,
                                                       List<WrittenTestGenerationResponse.QuestionDTO> questions) {
        return WrittenTestGenerationResponse.builder()
                .id(testResult.getId())
                .applicationId(testResult.getApplicationId())
                .resumeId(testResult.getResumeId())
                .jobPosition(testResult.getJobPosition())
                .candidateField(testResult.getCandidateField())
                .totalQuestions(testResult.getTotalQuestions())
                .choiceQuestions(testResult.getChoiceQuestions())
                .trueFalseQuestions(testResult.getTrueFalseQuestions())
                .totalScore(testResult.getTotalScore())
                .generationStatus(testResult.getGenerationStatus())
                .errorMessage(testResult.getErrorMessage())
                .createdAt(testResult.getCreatedAt())
                .updatedAt(testResult.getUpdatedAt())
                .questions(questions)
                .build();
    }

    @Override
    public WrittenTestResult getByApplicationId(Integer applicationId) {
        return writtenTestResultRepository.findByApplicationId(applicationId);
    }

    @Override
    public List<WrittenTestResult> getByResumeId(Long resumeId) {
        return writtenTestResultRepository.findByResumeId(resumeId);
    }

    @Override
    public WrittenTestGenerationResponse getDetailById(Long id) {
        WrittenTestResult testResult = writtenTestResultRepository.selectById(id);
        if (testResult == null) {
            return null;
        }

        List<WrittenTestGenerationResponse.QuestionDTO> questions = new ArrayList<>();
        if (testResult.getQuestionsJson() != null && !testResult.getQuestionsJson().isEmpty()) {
            try {
                JSONObject questionsJson = JSON.parseObject(testResult.getQuestionsJson());
                questions = parseQuestions(questionsJson);
            } catch (Exception e) {
                log.error("解析题目JSON失败", e);
            }
        }

        return buildResponse(testResult, questions);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, String status, String errorMessage) {
        try {
            WrittenTestResult testResult = writtenTestResultRepository.selectById(id);
            if (testResult == null) {
                return false;
            }

            testResult.setGenerationStatus(status);
            testResult.setErrorMessage(errorMessage);

            return writtenTestResultRepository.updateById(testResult) > 0;
        } catch (Exception e) {
            log.error("更新笔试结果状态失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteByApplicationId(Integer applicationId) {
        try {
            return writtenTestResultRepository.deleteByApplicationId(applicationId) > 0;
        } catch (Exception e) {
            log.error("删除笔试结果失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public WrittenTestGenerationResponse generateWrittenTestByApplicationId(Integer applicationId) {
        System.out.println("=== 开始生成笔试题目 ===");
        System.out.println("申请ID: " + applicationId);
        log.info("根据申请ID生成笔试题目，申请ID: {}", applicationId);

        try {
            // 1. 根据applicationId获取申请记录
            Application application = getApplicationById(applicationId);
            if (application == null) {
                throw new RuntimeException("申请记录不存在");
            }

            // 2. 获取简历记录
            Resume resume = getResumeById(application.getResumeId());
            if (resume == null) {
                throw new RuntimeException("简历记录不存在");
            }

            // 3. 获取职位信息（可选，用于设置职位描述）
            String jobPosition = "软件开发工程师"; // 默认值
            String jobDescription = "负责软件开发相关工作"; // 默认值

            // 4. 构建笔试生成请求
            WrittenTestGenerationRequest request = new WrittenTestGenerationRequest();
            request.setApplicationId(applicationId);
            request.setResumeId(application.getResumeId());
            request.setJobPosition(jobPosition);
            request.setJobDescription(jobDescription);
            request.setTotalQuestions(20); // 默认20题
            request.setChoiceQuestions(10); // 默认10道选择题
            request.setTrueFalseQuestions(10); // 默认10道判断题
            request.setTotalScore(100); // 默认100分
            request.setAiGenerated(true);

            // 5. 调用原有的生成方法
            return generateWrittenTest(request);

        } catch (Exception e) {
            log.error("根据申请ID生成笔试题目失败", e);
            throw new RuntimeException("生成笔试题目失败: " + e.getMessage());
        }
    }

    /**
     * 根据申请ID获取申请记录
     */
    private Application getApplicationById(Integer applicationId) {
        try {
            // 使用Mapper直接查询
            return applicationMapper.selectById(applicationId.longValue());
        } catch (Exception e) {
            log.error("获取申请记录失败，申请ID: {}", applicationId, e);
            return null;
        }
    }

    /**
     * 根据简历ID获取简历记录
     */
    private Resume getResumeById(Long resumeId) {
        try {
            // 使用Mapper直接查询
            return resumeMapper.selectById(resumeId);
        } catch (Exception e) {
            log.error("获取简历记录失败，简历ID: {}", resumeId, e);
            return null;
        }
    }

    @Override
    @Transactional
    public WrittenTestGenerationResponse regenerateWrittenTest(Integer applicationId) {
        // 获取现有的笔试结果
        WrittenTestResult existing = writtenTestResultRepository.findByApplicationId(applicationId);
        if (existing == null) {
            throw new RuntimeException("未找到现有的笔试结果");
        }

        // 构建重新生成请求
        WrittenTestGenerationRequest request = new WrittenTestGenerationRequest();
        request.setApplicationId(applicationId);
        request.setResumeId(existing.getResumeId());
        request.setJobPosition(existing.getJobPosition());
        request.setTotalQuestions(existing.getTotalQuestions());
        request.setChoiceQuestions(existing.getChoiceQuestions());
        request.setTrueFalseQuestions(existing.getTrueFalseQuestions());
        request.setTotalScore(existing.getTotalScore());
        request.setAiGenerated(true);

        return generateWrittenTest(request);
    }
}
