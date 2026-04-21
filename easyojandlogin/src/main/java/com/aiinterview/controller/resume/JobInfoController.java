package com.aiinterview.controller.resume;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.entity.resumer;
import com.aiinterview.mapper.ResumerMapper;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 求职信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobInfoController {

    private final ResumerMapper resumerMapper;
    private final JwtUtils jwtUtils;

    /**
     * 获取求职信息
     */
    @GetMapping("/job-info")
    public ApiResponse<Map<String, Object>> getJobInfo(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ApiResponse.error("未登录");
            }

            // 查询用户的默认简历
            resumer resume = resumerMapper.selectDefaultByUserId(userId);
            
            if (resume == null) {
                // 如果没有默认简历，返回空数据
                return ApiResponse.success("暂无求职信息", new HashMap<>());
            }

            // 构建返回数据
            Map<String, Object> jobInfo = new HashMap<>();
            jobInfo.put("position", resume.getPosition());
            jobInfo.put("workYears", resume.getWorkYears());
            jobInfo.put("expectedCity", resume.getExpectedCity());
            jobInfo.put("expectedSalaryMin", resume.getExpectedSalaryMin());
            jobInfo.put("expectedSalaryMax", resume.getExpectedSalaryMax());
            jobInfo.put("expectedIndustry", resume.getExpectedIndustry());
            jobInfo.put("skill", resume.getSkill());
            jobInfo.put("profile", resume.getProfile());

            return ApiResponse.success("获取求职信息成功", jobInfo);
        } catch (Exception e) {
            log.error("获取求职信息失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取求职信息失败: " + e.getMessage());
        }
    }

    /**
     * 保存求职信息
     */
    @PostMapping("/job-info")
    public ApiResponse<Map<String, Object>> saveJobInfo(
            HttpServletRequest request,
            @RequestBody Map<String, Object> jobInfoData) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ApiResponse.error("未登录");
            }

            // 查询用户的默认简历
            resumer resume = resumerMapper.selectDefaultByUserId(userId);
            
            if (resume == null) {
                // 如果没有默认简历，创建一个新的
                resume = new resumer();
                resume.setUserId(userId);
                resume.setIsDefault(true);
                resume.setIsDeleted(false);
                resume.setStatus("active");
                resume.setName("默认简历");
            }

            // 更新求职信息
            if (jobInfoData.containsKey("position")) {
                resume.setPosition((String) jobInfoData.get("position"));
            }
            if (jobInfoData.containsKey("workYears")) {
                resume.setWorkYears((Integer) jobInfoData.get("workYears"));
            }
            if (jobInfoData.containsKey("expectedCity")) {
                resume.setExpectedCity((String) jobInfoData.get("expectedCity"));
            }
            if (jobInfoData.containsKey("expectedSalaryMin")) {
                resume.setExpectedSalaryMin((Integer) jobInfoData.get("expectedSalaryMin"));
            }
            if (jobInfoData.containsKey("expectedSalaryMax")) {
                resume.setExpectedSalaryMax((Integer) jobInfoData.get("expectedSalaryMax"));
            }
            if (jobInfoData.containsKey("expectedIndustry")) {
                resume.setExpectedIndustry((String) jobInfoData.get("expectedIndustry"));
            }
            if (jobInfoData.containsKey("skill")) {
                resume.setSkill((String) jobInfoData.get("skill"));
            }
            if (jobInfoData.containsKey("profile")) {
                resume.setProfile((String) jobInfoData.get("profile"));
            }

            // 保存或更新
            if (resume.getId() == null) {
                resumerMapper.insert(resume);
            } else {
                resumerMapper.updateById(resume);
            }

            // 返回更新后的数据
            Map<String, Object> result = new HashMap<>();
            result.put("position", resume.getPosition());
            result.put("workYears", resume.getWorkYears());
            result.put("expectedCity", resume.getExpectedCity());
            result.put("expectedSalaryMin", resume.getExpectedSalaryMin());
            result.put("expectedSalaryMax", resume.getExpectedSalaryMax());
            result.put("expectedIndustry", resume.getExpectedIndustry());
            result.put("skill", resume.getSkill());
            result.put("profile", resume.getProfile());

            return ApiResponse.success("保存求职信息成功", result);
        } catch (Exception e) {
            log.error("保存求职信息失败: {}", e.getMessage(), e);
            return ApiResponse.error("保存求职信息失败: " + e.getMessage());
        }
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtils.getUserIdFromToken(token);
        }
        return null;
    }
}
