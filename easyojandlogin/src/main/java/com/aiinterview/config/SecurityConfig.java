package com.aiinterview.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置 (SpringBoot 2.7.2) - 使用新的配置方式
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 允许所有 OPTIONS 请求（CORS 预检）
                .antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                // 认证相关路径
                .antMatchers("/auth/**", "/**/auth/**").permitAll()
                // 简历相关路径 - 允许上传
                .antMatchers("/resume/**", "/resumes/**").permitAll()
                // DISC测试相关路径
                .antMatchers("/disc-test/**").permitAll()
                // 人脸检测相关路径
                .antMatchers("/face/**").permitAll()
                // 视频分析相关路径
                .antMatchers("/video-analysis/**", "/video/**").permitAll()
                // 面试记录相关路径
                .antMatchers("/ai-interviews/**").permitAll()
                // 职位相关路径
                .antMatchers("/jobs/**", "/api/jobs/**").permitAll()
                // 其他公开路径
                .antMatchers("/public/**", "/uploads/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v2/api-docs").permitAll()
                .antMatchers("/error", "/actuator/**").permitAll()
                .antMatchers("/simple-test/**", "/profile-test/**").permitAll()
                .antMatchers("/applicant/profile/test", "/applicant-simple/**").permitAll()
                .antMatchers("/company/profile/test").permitAll()
                .antMatchers("/external-resume/**", "/job-description/**").permitAll()
                .antMatchers("/resume-scoring/**", "/test-results/**").permitAll()
                .antMatchers("/candidate-answer-stats", "/applicant-management").permitAll()
                .antMatchers("/interview-evaluation", "/interview-evaluation/**").permitAll()
                .antMatchers("/recommend/**").permitAll()
                .antMatchers("/api/**").permitAll()

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
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
