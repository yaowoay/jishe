package com.aiinterview.controller.doris;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.model.dto.doris.EmploymentScreenBigDataDTO;
import com.aiinterview.service.doris.EmploymentScreenBigDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doris/employment-screen-bigdata")
@RequiredArgsConstructor
public class EmploymentScreenBigDataController {

    private final EmploymentScreenBigDataService employmentScreenBigDataService;

    @GetMapping
    public BaseResponse<EmploymentScreenBigDataDTO> getSingle() {
        EmploymentScreenBigDataDTO data = employmentScreenBigDataService.getSingle();
        if (data == null) {
            return BaseResponse.error(404, "数据不存在");
        }
        return BaseResponse.success(data, "获取 Doris 单行数据成功");
    }
}