package com.aiinterview.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
<<<<<<< HEAD
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 公开的认证接口（不需要认证）
                .antMatchers("/api/auth/**", "/api/email/**", "/auth/**").permitAll()
                // 公开的其他路径
                .antMatchers("/public/**", "/uploads/**", "/error", "/actuator/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v2/api-docs").permitAll()
                .antMatchers("/simple-test/**", "/profile-test/**").permitAll()
                // ❌ 删除或注释掉这行！不要允许所有 /api/**
                // .antMatchers("/api/**").permitAll()
=======
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests() //允许访问的接口
                .antMatchers("/api/**").permitAll()
                .antMatchers("/**/auth/**").permitAll()
                // 认证相关路径 - 最重要，放在最前面
                .antMatchers("/auth/**").permitAll()
                // 简历相关路径 - 允许上传
                .antMatchers("/resume/**").permitAll()
                .antMatchers("/resumes/**").permitAll()
                // DISC测试相关路径
                .antMatchers("/disc-test/**").permitAll()
                // 人脸检测相关路径
                .antMatchers("/face/**").permitAll()
                // 视频分析相关路径
                .antMatchers("/video-analysis/**").permitAll()
                // 视频上传相关路径
                .antMatchers("/videos/**").permitAll()
                // 面试记录相关路径
                .antMatchers("/ai-interviews/**").permitAll()
                // 其他公开路径
                .antMatchers("/public/**").permitAll()
                .antMatchers("/uploads/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/jobs/active").permitAll()
                .antMatchers("/jobs/search").permitAll()
                .antMatchers("/jobs/type/**").permitAll()
                .antMatchers("/simple-test/**").permitAll()
                .antMatchers("/profile-test/**").permitAll()
                .antMatchers("/applicant/profile/test").permitAll()
                .antMatchers("/applicant-simple/**").permitAll()
                .antMatchers("/company/profile/test").permitAll()
                .antMatchers("/external-resume/**").permitAll()
                .antMatchers("/job-description/**").permitAll()
                .antMatchers("/resume-scoring/**").permitAll()
                .antMatchers("/test-results/**").permitAll()
                .antMatchers("/candidate-answer-stats").permitAll()
                .antMatchers("/applicant-management").permitAll()
                .antMatchers("/interview-evaluation").permitAll()
                .antMatchers("/interview-evaluation/**").permitAll()
                .antMatchers("/recommend/**").permitAll()
                .antMatchers("/student/**").permitAll()
//                .antMatchers("/student/profile").permitAll()
>>>>>>> 787dee59a5a3d259a5095d69e8f77fe180672a0d

                // ❌ 删除这行！面试记录需要认证
                // .antMatchers("/ai-interviews/**").permitAll()

                // 其他需要认证的接口
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}