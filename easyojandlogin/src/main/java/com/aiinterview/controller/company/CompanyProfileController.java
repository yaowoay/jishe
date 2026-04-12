package com.aiinterview.controller.company;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.company.CompanyProfileDTO;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.service.company.CompanyService;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 公司信息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/company/profile")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class CompanyProfileController {

    private final CompanyService companyService;
    private final JwtUtils jwtUtils;
    private final CompanyMapper companyMapper;
    
    /**
     * 获取当前用户的公司信息
     */
    @GetMapping
    public ApiResponse<CompanyProfileDTO> getCompanyProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            CompanyProfileDTO profile = companyService.getCompanyByUserId(userId);
            
            if (profile == null) {
                return ApiResponse.success("暂无公司信息", null);
            }
            
            return ApiResponse.success("获取成功", profile);
        } catch (Exception e) {
            log.error("获取公司信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 保存或更新公司信息
     */
    @PostMapping
    public ApiResponse<CompanyProfileDTO> saveOrUpdateCompanyProfile(
            @Valid @RequestBody CompanyProfileDTO companyProfileDTO,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            CompanyProfileDTO result = companyService.saveOrUpdateCompany(userId, companyProfileDTO);
            return ApiResponse.success("保存成功", result);
        } catch (Exception e) {
            log.error("保存公司信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否已有公司信息
     */
    @GetMapping("/exists")
    public ApiResponse<Boolean> hasCompanyProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            boolean exists = companyService.hasCompanyProfile(userId);
            return ApiResponse.success("检查成功", exists);
        } catch (Exception e) {
            log.error("检查公司信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 从Token中获取公司ID
     */
    private Long getCompanyIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("未找到有效的认证信息");
            }

            // 提取token
            token = token.substring(7);

            // 从token中获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);

            // 验证用户角色是否为公司
            String role = jwtUtils.getRoleFromToken(token);
            if (!"company".equals(role)) {
                throw new RuntimeException("用户角色不正确，需要企业用户权限");
            }

            // 根据用户ID查询公司ID
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Company company = companyMapper.selectOne(queryWrapper);

            if (company == null) {
                // 如果没有公司信息，返回用户ID（用于创建新的公司信息）
                log.info("未找到公司信息，将使用用户ID: {}", userId);
                return userId;
            }

            return company.getCompanyId();
        } catch (Exception e) {
            log.error("获取公司ID失败: {}", e.getMessage());
            throw new RuntimeException("获取公司信息失败: " + e.getMessage());
        }
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("未找到有效的认证信息");
            }

            // 提取token
            token = token.substring(7);

            // 从token中获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);

            // 验证用户角色是否为公司
            String role = jwtUtils.getRoleFromToken(token);
            if (!"company".equals(role)) {
                throw new RuntimeException("用户角色不正确，需要企业用户权限");
            }

            return userId;
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
            throw new RuntimeException("认证失败: " + e.getMessage());
        }
    }
}
