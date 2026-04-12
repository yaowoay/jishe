package com.aiinterview.service.impl.interview;

import com.aiinterview.model.dto.interview.InterviewExamDTO;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.model.entity.interview.InterviewAnswer;
import com.aiinterview.model.entity.interview.InterviewResult;
import com.aiinterview.model.entity.resume.Resume;
import com.aiinterview.mapper.*;
import com.aiinterview.repository.interview.InterviewAnswerRepository;
import com.aiinterview.repository.interview.InterviewResultRepository;
import com.aiinterview.service.interview.InterviewExamService;
import com.aiinterview.service.api.DifyService;
import com.aiinterview.config.DifyApiConfig;
import com.aiinterview.constants.WorkflowType;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 面试考试服务实现类
 */
@Slf4j
@Service
public class InterviewExamServiceImpl implements InterviewExamService {

    @Autowired
    private InterviewResultRepository interviewResultRepository;

    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private DifyService difyService;

    @Autowired
    private DifyApiConfig difyApiConfig;

    @Override
    @Transactional
    public InterviewResult generateInterviewQuestions(Integer applicationId) {
        log.info("开始生成面试题目，申请ID: {}", applicationId);

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

        // 3. 检查是否已经生成过面试题目
        InterviewResult existingResult = interviewResultRepository.findByApplicationId(applicationId);
        if (existingResult != null && "success".equals(existingResult.getGenerationStatus())) {
            log.info("面试题目已存在，申请ID: {}", applicationId);
            return existingResult;
        }

        // 4. 创建面试结果记录
        InterviewResult interviewResult = new InterviewResult();
        interviewResult.setApplicationId(applicationId);
        interviewResult.setResumeId(application.getResumeId());
        interviewResult.setJobPosition("面试职位"); // 可以从Job表获取
        interviewResult.setJobDescription("面试职位描述"); // 可以从Job表获取
        interviewResult.setCandidateName("候选人"); // 可以从简历解析数据获取
        interviewResult.setGenerationStatus("pending");
        interviewResult.setTotalQuestions(5); // 默认5道题

        if (existingResult != null) {
            interviewResult.setId(existingResult.getId());
            interviewResultRepository.updateById(interviewResult);
        } else {
            interviewResultRepository.insert(interviewResult);
        }

        try {
            // 5. 上传简历文件到Dify
            String fileId = uploadResumeToExternalApi(resume, "system");
            if (fileId == null) {
                throw new RuntimeException("简历文件上传失败");
            }

            // 6. 调用面试生成工作流
            String response = callInterviewWorkflow(fileId, "面试职位描述");
            log.info("Dify面试工作流响应: {}", response);

            // 6. 解析响应
            JSONObject responseJson = JSON.parseObject(response);
            if (responseJson.containsKey("data") && responseJson.getJSONObject("data").containsKey("outputs")) {
                JSONObject data = responseJson.getJSONObject("data");
                JSONObject outputs = data.getJSONObject("outputs");

                // 7. 解析面试题目 - 根据实际返回的数据结构
                String questionsJson = parseInterviewQuestionsFromResponse(outputs);

                // 8. 更新面试结果
                interviewResult.setQuestionsJson(questionsJson);
                interviewResult.setGenerationStatus("success");
                interviewResult.setWorkflowRunId(data.getString("id"));
                interviewResultRepository.updateById(interviewResult);

                log.info("面试题目生成成功，申请ID: {}", applicationId);
                return interviewResult;
            } else {
                throw new RuntimeException("Dify响应格式错误");
            }

        } catch (Exception e) {
            log.error("生成面试题目失败，申请ID: {}", applicationId, e);
            interviewResult.setGenerationStatus("failed");
            interviewResult.setErrorMessage(e.getMessage());
            interviewResultRepository.updateById(interviewResult);
            throw new RuntimeException("生成面试题目失败: " + e.getMessage());
        }
    }

    @Override
    public InterviewExamDTO getExamInfo(Integer applicationId) {
        log.info("获取面试考试信息，申请ID: {}", applicationId);

        // 1. 获取面试题目
        InterviewResult interviewResult = interviewResultRepository.findByApplicationId(applicationId);
        if (interviewResult == null) {
            throw new RuntimeException("未找到面试题目，请联系HR");
        }

        if (!"success".equals(interviewResult.getGenerationStatus())) {
            throw new RuntimeException("面试题目生成失败，请联系HR");
        }

        // 2. 检查是否已经答过题
        InterviewAnswer existingAnswer = interviewAnswerRepository.findByApplicationIdAndInterviewResultId(
                applicationId, interviewResult.getId());

        // 3. 解析题目
        List<InterviewExamDTO.InterviewQuestionDTO> examQuestions = parseQuestionsForExam(interviewResult.getQuestionsJson());

        return InterviewExamDTO.builder()
                .interviewResultId(interviewResult.getId())
                .applicationId(applicationId)
                .jobPosition(interviewResult.getJobPosition())
                .candidateName(interviewResult.getCandidateName())
                .totalQuestions(interviewResult.getTotalQuestions())
                .status(existingAnswer != null ? existingAnswer.getStatus() : "not_started")
                .startTime(existingAnswer != null ? existingAnswer.getStartTime() : null)
                .timeLimit(60) // 默认60分钟
                .questions(examQuestions)
                .build();
    }

    @Override
    @Transactional
    public InterviewExamDTO startExam(Integer applicationId) {
        log.info("开始面试考试，申请ID: {}", applicationId);

        InterviewExamDTO examInfo = getExamInfo(applicationId);

        // 创建或更新答题记录
        InterviewAnswer answer = interviewAnswerRepository.findByApplicationIdAndInterviewResultId(
                applicationId, examInfo.getInterviewResultId());

        if (answer == null) {
            // 创建新的答题记录
            answer = new InterviewAnswer();
            answer.setApplicationId(applicationId);
            answer.setInterviewResultId(examInfo.getInterviewResultId());
            answer.setTotalQuestions(examInfo.getTotalQuestions());
            answer.setStatus("in_progress");
            answer.setStartTime(LocalDateTime.now());
            interviewAnswerRepository.insert(answer);
        } else if ("not_started".equals(answer.getStatus())) {
            // 开始答题
            answer.setStatus("in_progress");
            answer.setStartTime(LocalDateTime.now());
            interviewAnswerRepository.updateById(answer);
        } else if ("completed".equals(answer.getStatus())) {
            // 如果已完成，允许重新开始（重置答题记录）
            log.info("检测到已完成的面试，允许重新开始，申请ID: {}", applicationId);
            answer.setAnswersJson(null);
            answer.setScore(null);
            answer.setCompletionTime(null);
            answer.setSubmitTime(null);
            answer.setInterviewerFeedback(null);
            answer.setTechnicalScore(null);
            answer.setCommunicationScore(null);
            answer.setProblemSolvingScore(null);
            answer.setStatus("in_progress");
            answer.setStartTime(LocalDateTime.now());
            interviewAnswerRepository.updateById(answer);
        } else if ("in_progress".equals(answer.getStatus())) {
            // 如果正在进行中，继续使用现有记录
            log.info("继续进行中的面试，申请ID: {}", applicationId);
        }

        examInfo.setStatus("in_progress");
        examInfo.setStartTime(answer.getStartTime());

        return examInfo;
    }

    @Override
    @Transactional
    public InterviewAnswer submitAnswers(Integer applicationId, List<Map<String, Object>> answers) {
        log.info("提交面试答案，申请ID: {}", applicationId);

        // 1. 获取面试结果
        InterviewResult interviewResult = interviewResultRepository.findByApplicationId(applicationId);
        if (interviewResult == null) {
            throw new RuntimeException("面试题目不存在");
        }

        // 2. 获取答题记录
        InterviewAnswer answer = interviewAnswerRepository.findByApplicationIdAndInterviewResultId(
                applicationId, interviewResult.getId());
        if (answer == null) {
            throw new RuntimeException("面试答题记录不存在");
        }

        // 3. 保存答案
        answer.setAnswersJson(JSON.toJSONString(answers));
        answer.setSubmitTime(LocalDateTime.now());
        answer.setStatus("completed");

        // 4. 计算完成时间
        if (answer.getStartTime() != null) {
            long minutes = ChronoUnit.MINUTES.between(answer.getStartTime(), answer.getSubmitTime());
            answer.setCompletionTime((int) minutes);
        }

        // 5. 初始化评分（后续可以由面试官手动评分）
        answer.setScore(BigDecimal.ZERO);
        answer.setTechnicalScore(BigDecimal.ZERO);
        answer.setCommunicationScore(BigDecimal.ZERO);
        answer.setProblemSolvingScore(BigDecimal.ZERO);

        interviewAnswerRepository.updateById(answer);

        log.info("面试答案提交成功，申请ID: {}", applicationId);
        return answer;
    }

    @Override
    public InterviewAnswer getInterviewResult(Integer applicationId) {
        return interviewAnswerRepository.findByApplicationId(applicationId);
    }

    @Override
    public String getInterviewStatus(Integer applicationId) {
        InterviewAnswer answer = interviewAnswerRepository.findByApplicationId(applicationId);
        return answer != null ? answer.getStatus() : "not_started";
    }

    @Override
    @Transactional
    public InterviewExamDTO restartInterview(Integer applicationId) {
        log.info("重新开始面试，申请ID: {}", applicationId);
        return startExam(applicationId);
    }

    @Override
    @Transactional
    public boolean deleteInterviewRecord(Integer applicationId) {
        log.info("删除面试记录，申请ID: {}", applicationId);

        try {
            interviewAnswerRepository.deleteByApplicationId(applicationId);
            interviewResultRepository.deleteByApplicationId(applicationId);
            return true;
        } catch (Exception e) {
            log.error("删除面试记录失败，申请ID: {}", applicationId, e);
            return false;
        }
    }

    /**
     * 调用面试生成工作流
     */
    private String callInterviewWorkflow(String fileId, String jobDescription) {
        try {
            String workflowUrl = difyApiConfig.getWorkflowRunUrl();
            WorkflowType workflowType = WorkflowType.INTERVIEW;

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

            // jd参数 - 职位描述
            inputs.put("jd", jobDescription);

            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", "system");

            String requestBodyJson = JSON.toJSONString(requestBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);

            log.info("调用面试生成工作流: {}", workflowUrl);
            log.info("请求体: {}", requestBodyJson);

            // 发送请求
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(workflowUrl, requestEntity, String.class);

            log.info("面试工作流API响应状态: {}", response.getStatusCode());
            log.info("面试工作流API响应内容: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("调用面试工作流API失败: " + response.getStatusCode() + ": " + response.getBody());
            }

        } catch (Exception e) {
            log.error("调用面试工作流失败", e);
            throw new RuntimeException("调用面试工作流失败: " + e.getMessage());
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
            WorkflowType workflowType = WorkflowType.INTERVIEW; // 使用面试工作流类型

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
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.info("文件上传成功，响应: {}", responseBody);

                // 解析响应获取文件ID
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                if (jsonResponse.containsKey("id")) {
                    return jsonResponse.getString("id");
                } else {
                    log.error("响应中未找到文件ID: {}", responseBody);
                    return null;
                }
            } else {
                log.error("文件上传失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }

        } catch (Exception e) {
            log.error("上传简历文件失败", e);
            return null;
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

    /**
     * 从Dify响应中解析面试题目
     */
    private String parseInterviewQuestionsFromResponse(JSONObject outputs) {
        try {
            log.info("开始解析面试题目响应: {}", outputs.toJSONString());

            // 检查是否包含resume_question字段
            if (!outputs.containsKey("resume_question")) {
                throw new RuntimeException("响应中未找到resume_question字段");
            }

            JSONArray resumeQuestionArray = outputs.getJSONArray("resume_question");
            if (resumeQuestionArray == null || resumeQuestionArray.isEmpty()) {
                throw new RuntimeException("resume_question数组为空");
            }

            // 获取第一个候选人的题目
            JSONObject candidateData = resumeQuestionArray.getJSONObject(0);
            if (candidateData == null) {
                throw new RuntimeException("候选人数据为空");
            }

            String candidateName = candidateData.getString("candidate_name");
            JSONArray questionsArray = candidateData.getJSONArray("questions");

            if (questionsArray == null || questionsArray.isEmpty()) {
                throw new RuntimeException("题目数组为空");
            }

            // 转换为标准格式
            JSONArray standardQuestions = new JSONArray();
            for (int i = 0; i < questionsArray.size(); i++) {
                JSONObject originalQuestion = questionsArray.getJSONObject(i);

                JSONObject standardQuestion = new JSONObject();
                standardQuestion.put("questionNumber", i + 1);
                standardQuestion.put("question", originalQuestion.getString("question"));
                standardQuestion.put("category", "综合面试");
                standardQuestion.put("difficulty", "medium");
                standardQuestion.put("timeLimit", 10);
                standardQuestion.put("tips", "请结合您的实际经验详细回答");

                standardQuestions.add(standardQuestion);
            }

            log.info("面试题目解析成功，候选人: {}, 题目数量: {}", candidateName, standardQuestions.size());
            return standardQuestions.toJSONString();

        } catch (Exception e) {
            log.error("解析面试题目失败", e);
            throw new RuntimeException("解析面试题目失败: " + e.getMessage());
        }
    }

    /**
     * 解析面试题目文本为JSON格式（备用方法）
     */
    private String parseInterviewQuestions(String questionsText) {
        try {
            JSONArray questions = new JSONArray();

            // 简单的文本解析逻辑，可以根据实际格式调整
            String[] lines = questionsText.split("\n");
            int questionNumber = 1;

            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && (line.matches("^\\d+[.、].*") || line.startsWith("问题"))) {
                    JSONObject question = new JSONObject();
                    question.put("questionNumber", questionNumber++);
                    question.put("question", line.replaceFirst("^\\d+[.、]\\s*", "").replaceFirst("^问题\\d*[：:]\\s*", ""));
                    question.put("category", "综合");
                    question.put("difficulty", "medium");
                    question.put("timeLimit", 10);
                    question.put("tips", "请结合您的实际经验回答");
                    questions.add(question);
                }
            }

            return questions.toJSONString();
        } catch (Exception e) {
            log.error("解析面试题目失败", e);
            throw new RuntimeException("解析面试题目失败");
        }
    }

    /**
     * 解析题目用于考试显示
     */
    private List<InterviewExamDTO.InterviewQuestionDTO> parseQuestionsForExam(String questionsJson) {
        try {
            JSONArray questionsArray = JSON.parseArray(questionsJson);
            List<InterviewExamDTO.InterviewQuestionDTO> result = new ArrayList<>();

            for (int i = 0; i < questionsArray.size(); i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);

                InterviewExamDTO.InterviewQuestionDTO questionDTO = InterviewExamDTO.InterviewQuestionDTO.builder()
                        .questionNumber(questionObj.getInteger("questionNumber"))
                        .question(questionObj.getString("question"))
                        .category(questionObj.getString("category"))
                        .difficulty(questionObj.getString("difficulty"))
                        .timeLimit(questionObj.getInteger("timeLimit"))
                        .tips(questionObj.getString("tips"))
                        .build();

                result.add(questionDTO);
            }

            return result;
        } catch (Exception e) {
            log.error("解析面试题目用于考试显示失败", e);
            throw new RuntimeException("解析面试题目失败");
        }
    }

    @Override
    public boolean saveAnswerRealTime(Integer applicationId, Map<String, Object> answerData) {
        try {
            log.info("实时保存面试回答，申请ID: {}", applicationId);

            // 获取或创建面试答案记录
            InterviewAnswer interviewAnswer = interviewAnswerRepository.findByApplicationId(applicationId);
            if (interviewAnswer == null) {
                // 创建新的面试答案记录
                interviewAnswer = new InterviewAnswer();
                interviewAnswer.setApplicationId(applicationId);

                // 获取面试结果ID
                InterviewResult interviewResult = interviewResultRepository.findByApplicationId(applicationId);
                if (interviewResult != null) {
                    interviewAnswer.setInterviewResultId(interviewResult.getId());
                } else {
                    log.warn("未找到申请ID {}对应的面试结果记录，无法保存面试答案", applicationId);
                    return false;
                }

                interviewAnswer.setStatus("in_progress");
                interviewAnswer.setStartTime(LocalDateTime.now());
                interviewAnswer.setAnswersJson("[]"); // 初始化为空数组
                interviewAnswer.setCreatedAt(LocalDateTime.now());
                interviewAnswer.setUpdatedAt(LocalDateTime.now());
            }

            // 解析现有答案
            String existingAnswersJson = interviewAnswer.getAnswersJson();
            List<Map> existingAnswers = new ArrayList<>();

            if (existingAnswersJson != null && !existingAnswersJson.isEmpty() && !existingAnswersJson.equals("[]")) {
                try {
                    existingAnswers = JSON.parseArray(existingAnswersJson, Map.class);
                } catch (Exception e) {
                    log.warn("解析现有答案失败，重新初始化: {}", e.getMessage());
                    existingAnswers = new ArrayList<>();
                }
            }

            // 添加新答案
            Map<String, Object> newAnswerRecord = new HashMap<>();
            newAnswerRecord.put("timestamp", System.currentTimeMillis());
            newAnswerRecord.put("data", answerData);
            newAnswerRecord.put("saved_at", LocalDateTime.now().toString());

            existingAnswers.add(newAnswerRecord);

            // 更新答案JSON
            interviewAnswer.setAnswersJson(JSON.toJSONString(existingAnswers));
            interviewAnswer.setUpdatedAt(LocalDateTime.now());

            // 保存到数据库
            if (interviewAnswer.getId() == null) {
                // 新记录，使用insert
                interviewAnswerRepository.insert(interviewAnswer);
            } else {
                // 更新记录，使用updateById
                interviewAnswerRepository.updateById(interviewAnswer);
            }

            log.info("实时保存成功，申请ID: {}, 当前答案数量: {}", applicationId, existingAnswers.size());
            return true;

        } catch (Exception e) {
            log.error("实时保存面试回答失败，申请ID: {}", applicationId, e);
            return false;
        }
    }

    @Override
    public boolean updateInterviewStatus(Integer applicationId, String status) {
        try {
            log.info("更新面试状态，申请ID: {}, 状态: {}", applicationId, status);

            // 更新面试答案记录的状态
            InterviewAnswer interviewAnswer = interviewAnswerRepository.findByApplicationId(applicationId);
            if (interviewAnswer != null) {
                interviewAnswer.setStatus(status);

                if ("in_progress".equals(status) && interviewAnswer.getStartTime() == null) {
                    interviewAnswer.setStartTime(LocalDateTime.now());
                } else if ("completed".equals(status)) {
                    interviewAnswer.setSubmitTime(LocalDateTime.now());

                    // 计算完成时间（分钟）
                    if (interviewAnswer.getStartTime() != null) {
                        long minutes = ChronoUnit.MINUTES.between(interviewAnswer.getStartTime(), LocalDateTime.now());
                        interviewAnswer.setCompletionTime((int) minutes);
                    }
                }

                interviewAnswer.setUpdatedAt(LocalDateTime.now());
                interviewAnswerRepository.updateById(interviewAnswer);
            }

            // 更新面试结果记录的状态
            InterviewResult interviewResult = interviewResultRepository.findByApplicationId(applicationId);
            if (interviewResult != null) {
                interviewResult.setGenerationStatus(status);
                interviewResult.setUpdatedAt(LocalDateTime.now());
                interviewResultRepository.updateById(interviewResult);
            }

            log.info("面试状态更新成功，申请ID: {}", applicationId);
            return true;

        } catch (Exception e) {
            log.error("更新面试状态失败，申请ID: {}", applicationId, e);
            return false;
        }
    }
}
