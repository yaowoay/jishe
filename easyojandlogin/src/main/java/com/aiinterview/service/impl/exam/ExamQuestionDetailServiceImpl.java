package com.aiinterview.service.impl.exam;

import com.aiinterview.model.entity.exam.ExamQuestionDetail ;
import com.aiinterview.repository.exam.ExamQuestionDetailRepository;
import com.aiinterview.service.exam.ExamQuestionDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamQuestionDetailServiceImpl implements ExamQuestionDetailService {
    
    private final ExamQuestionDetailRepository repository;

    @Override
    public void saveQuestions(String taskId, List<Map<String, Object>> questions) {
        for (Map<String, Object> q : questions) {
            ExamQuestionDetail detail = new ExamQuestionDetail();
            detail.setTaskId(taskId);
            detail.setQuestionId((String) q.get("questionId"));
            detail.setType((String) q.get("type"));
            detail.setContent((String) (q.get("questionContent") != null ? q.get("questionContent") : q.get("statement")));
            detail.setOptionA((String) q.get("A"));
            detail.setOptionB((String) q.get("B"));
            detail.setOptionC((String) q.get("C"));
            detail.setOptionD((String) q.get("D"));
            detail.setAnswer(q.get("answer") != null ? q.get("answer").toString() : null);
            detail.setExplanation(q.get("trapDetection") != null ? q.get("trapDetection").toString() : 
                    (q.get("explanation") != null ? q.get("explanation").toString() : null));
            detail.setKnowledgePoint(q.get("knowledgePoint") != null ? q.get("knowledgePoint").toString() : null);
            detail.setScenario(q.get("scenario") != null ? q.get("scenario").toString() : null);
            detail.setDifficulty(q.get("difficulty") != null ? q.get("difficulty").toString() : "中等");
            detail.setTrapDetection(q.get("trapDetection") != null ? q.get("trapDetection").toString() : null);

            repository.insert(detail);
            log.info("保存题目: taskId={}, questionId={}", taskId, detail.getQuestionId());
        }
    }

    @Override
    public List<ExamQuestionDetail> getQuestionsByTaskId(String taskId) {
        return repository.findByTaskId(taskId);
    }

    @Override
    public ExamQuestionDetail getQuestionById(String questionId) {
        return repository.findByQuestionId(questionId);
    }
}
