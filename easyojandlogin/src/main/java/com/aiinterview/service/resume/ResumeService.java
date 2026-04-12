package com.aiinterview.service.resume;

import com.aiinterview.model.dto.request.ResumeCreateRequest;
import com.aiinterview.model.dto.request.ResumeUpdateRequest;
import com.aiinterview.model.dto.response.ResumeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 简历服务接口
 */
public interface ResumeService {
    
    /**
     * 创建简历
     */
    ResumeResponse createResume(ResumeCreateRequest request, String userKey);
    
    /**
     * 更新简历
     */
    ResumeResponse updateResume(Long resumeId, ResumeUpdateRequest request, String userKey);
    
    /**
     * 删除简历
     */
    void deleteResume(Long resumeId, String userKey);
    
    /**
     * 获取简历详情
     */
    ResumeResponse getResumeById(Long resumeId, String userKey);
    
    /**
     * 获取用户的所有简历
     */
    List<ResumeResponse> getUserResumes(String userKey);
    
    /**
     * 分页获取用户简历
     */
    org.springframework.data.domain.Page<ResumeResponse> getUserResumesPage(String userKey, Pageable pageable);
    
    /**
     * 设置默认简历
     */
    void setDefaultResume(Long resumeId, String userKey);
    
    /**
     * 复制简历
     */
    ResumeResponse copyResume(Long resumeId, String userKey);
    
    /**
     * 生成分享链接
     */
    String generateShareUrl(Long resumeId, String userKey);
    
    /**
     * 通过分享链接获取简历
     */
    ResumeResponse getResumeByShareUrl(String shareUrl);
    
    /**
     * 搜索简历
     */
    org.springframework.data.domain.Page<ResumeResponse> searchResumes(String keyword, Pageable pageable);
    
    /**
     * 获取热门简历
     */
    List<ResumeResponse> getPopularResumes(int limit);
    
    /**
     * 获取最近更新的简历
     */
    List<ResumeResponse> getRecentResumes(String userKey, int limit);

    /**
     * 根据用户ID获取简历列表
     */
    List<com.aiinterview.model.entity.resumer> getResumesByUserId(Long userId); //非软件杯

    /**
     * 获取简历文件路径
     */
    String getResumeFilePath(Long resumeId, Long userId);//非软件杯
}