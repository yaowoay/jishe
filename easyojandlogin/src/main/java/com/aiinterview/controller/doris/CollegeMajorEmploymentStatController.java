package com.aiinterview.controller.doris;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.model.dto.doris.CollegeMajorEmploymentStatusCountDTO;
import com.aiinterview.model.dto.doris.EmploymentStatusCountDTO;
import com.aiinterview.service.doris.CollegeMajorEmploymentStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doris/employment-stat")
@RequiredArgsConstructor
public class CollegeMajorEmploymentStatController {

    private final CollegeMajorEmploymentStatService collegeMajorEmploymentStatService;

    @GetMapping("/employment-status-count")
    public BaseResponse<List<EmploymentStatusCountDTO>> employmentStatusCount(
            @RequestParam(required = false) Integer graduationYear) {
        return BaseResponse.success(
                collegeMajorEmploymentStatService.getEmploymentStatusCount(graduationYear),
                "获取就业去向总体分布成功"
        );
    }

    @GetMapping("/college-major-employment-status-count")
    public BaseResponse<List<CollegeMajorEmploymentStatusCountDTO>> collegeMajorEmploymentStatusCount(
            @RequestParam(required = false) Integer graduationYear) {
        return BaseResponse.success(
                collegeMajorEmploymentStatService.getCollegeMajorEmploymentStatusCount(graduationYear),
                "获取学院专业就业去向分布成功"
        );
    }
}