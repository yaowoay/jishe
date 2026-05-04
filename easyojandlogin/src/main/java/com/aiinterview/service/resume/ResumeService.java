package com.aiinterview.service.resume;

import com.aiinterview.model.dto.resume.ResumeCreateRequest;
import com.aiinterview.model.dto.resume.ResumeUpdateRequest;
import com.aiinterview.model.dto.response.ResumeResponse;
import com.aiinterview.model.entity.resumer;
import com.aiinterview.model.entity.resume.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 简历服务接口
 * 说明：
 * - resumer 实体对应 resume 表（在线编辑简历）
 * - Resume 实体对应 resumes 表（文件上传简历）
 */
public interface ResumeService {

    // ==================== 在线简历编辑（resumer 实体） ====================

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
     * 根据用户ID获取简历实体列表（resumer 实体）
     */
    List<resumer> getResumersByUserId(Long userId);

    // ==================== 文件上传简历（Resume 实体） ====================

    /**
     * 上传简历文件
     */
    Resume uploadResume(MultipartFile file, Long userId, String filename);

    /**
     * 获取用户的上传简历列表
     */
    List<Resume> getUploadedResumesByUserId(Long userId);

    /**
     * 根据ID获取上传简历详情
     */
    Resume getUploadedResumeById(Long resumeId, Long userId);

    /**
     * 删除上传的简历
     */
    boolean deleteUploadedResume(Long resumeId, Long userId);

    /**
     * 分析简历
     */
    String analyzeResume(Long resumeId, Long userId);

    /**
     * 获取简历文件路径
     */
    String getResumeFilePath(Long resumeId, Long userId);

    /**
     * 更新简历分析状态
     */
    boolean updateAnalysisStatus(Long resumeId, boolean analyzed, String analysisResult);
}