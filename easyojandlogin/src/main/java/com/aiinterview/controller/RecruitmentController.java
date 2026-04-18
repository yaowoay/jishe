package com.aiinterview.controller;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.entity.job.Recruitment;
import com.aiinterview.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 招聘职位Controller
 */
@RestController
@RequestMapping("/api/recruitment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecruitmentController {

    @Autowired
    private RecruitmentService recruitmentService;

    /**
     * 获取所有职位
     */
    @GetMapping("/list")
    public BaseResponse<List<Recruitment>> getAllRecruitments() {
        List<Recruitment> recruitments = recruitmentService.getAllRecruitments();
        return ResultUtils.success(recruitments);
    }

    /**
     * 根据ID获取职位
     */
    @GetMapping("/{jobId}")
    public BaseResponse<Recruitment> getRecruitmentById(@PathVariable Long jobId) {
        Recruitment recruitment = recruitmentService.getRecruitmentById(jobId);
        return ResultUtils.success(recruitment);
    }

    /**
     * 根据城市获取职位
     */
    @GetMapping("/city/{city}")
    public BaseResponse<List<Recruitment>> getByCity(@PathVariable String city) {
        List<Recruitment> recruitments = recruitmentService.getRecruitmentsByCity(city);
        return ResultUtils.success(recruitments);
    }

    /**
     * 根据公司获取职位
     */
    @GetMapping("/company/{companyName}")
    public BaseResponse<List<Recruitment>> getByCompany(@PathVariable String companyName) {
        List<Recruitment> recruitments = recruitmentService.getRecruitmentsByCompany(companyName);
        return ResultUtils.success(recruitments);
    }

    /**
     * 搜索职位
     */
    @GetMapping("/search")
    public BaseResponse<List<Recruitment>> search(@RequestParam String keyword) {
        List<Recruitment> recruitments = recruitmentService.searchRecruitments(keyword);
        return ResultUtils.success(recruitments);
    }

    /**
     * 根据薪资范围获取职位
     */
    @GetMapping("/salary-range")
    public BaseResponse<List<Recruitment>> getBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary) {
        List<Recruitment> recruitments = recruitmentService.getRecruitmentsBySalaryRange(minSalary, maxSalary);
        return ResultUtils.success(recruitments);
    }

    /**
     * 分页获取职位
     */
    @GetMapping("/page")
    public BaseResponse<List<Recruitment>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Recruitment> recruitments = recruitmentService.getRecruitmentsByPage(pageNum, pageSize);
        long total = recruitmentService.getTotalCount();
        return ResultUtils.success(recruitments);
    }

    /**
     * 添加职位
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addRecruitment(@RequestBody Recruitment recruitment) {
        boolean result = recruitmentService.addRecruitment(recruitment);
        return ResultUtils.success(result);
    }

    /**
     * 更新职位
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updateRecruitment(@RequestBody Recruitment recruitment) {
        boolean result = recruitmentService.updateRecruitment(recruitment);
        return ResultUtils.success(result);
    }

    /**
     * 删除职位
     */
    @DeleteMapping("/{jobId}")
    public BaseResponse<Boolean> deleteRecruitment(@PathVariable Long jobId) {
        boolean result = recruitmentService.deleteRecruitment(jobId);
        return ResultUtils.success(result);
    }
}
