package com.aiinterview.service.resume;

import com.aiinterview.model.dto.request.ResumeCreateRequest;
import com.aiinterview.model.dto.request.ResumeUpdateRequest;
import com.aiinterview.model.dto.response.ResumeResponse;
import com.aiinterview.model.entity.resumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 简历服务接口（已与 ResumeServiceImpl 完全对齐）
 */
public interface ResumeService {

    /**
     * 创建简历
     */
    ResumeResponse createResume(ResumeCreateRequest request, Long userId);

    /**
     * 更新简历
     */
    ResumeResponse updateResume(Long resumeId, ResumeUpdateRequest request, Long userId);

    /**
     * 删除简历
     */
    void deleteResume(Long resumeId, Long userId);

    /**
     * 获取简历详情
     */
    ResumeResponse getResumeById(Long resumeId, Long userId);

    /**
     * 获取用户的所有简历
     */
    List<ResumeResponse> getUserResumes(Long userId);

    /**
     * 分页获取用户简历
     */
    Page<ResumeResponse> getUserResumesPage(Long userId, Pageable pageable);

    /**
     * 设置默认简历
     */
    void setDefaultResume(Long resumeId, Long userId);

    /**
     * 复制简历
     */
    ResumeResponse copyResume(Long resumeId, Long userId);

    /**
     * 生成分享码
     */
    String generateShareCode(Long resumeId, Long userId);

    /**
     * 通过分享码获取简历
     */
    ResumeResponse getResumeByShareCode(String shareCode);

    /**
     * 搜索简历
     */
    Page<ResumeResponse> searchResumes(String keyword, Pageable pageable);

    /**
     * 获取热门简历
     */
    List<ResumeResponse> getPopularResumes(int limit);

    /**
     * 获取最近更新的简历
     */
    List<ResumeResponse> getRecentResumes(Long userId, int limit);

    /**
     * 根据用户ID获取简历实体列表（非软件杯）
     */
    List<resumer> getResumesByUserId(Long userId);

    /**
     * 获取简历文件路径（非软件杯）
     */
    String getResumeFilePath(Long resumeId, Long userId);
}