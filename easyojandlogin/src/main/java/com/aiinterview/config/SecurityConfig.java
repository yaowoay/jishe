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
                .antMatchers("/resume/**", "/resumes/**").permitAll()
                .antMatchers("/disc-test/**").permitAll()
                .antMatchers("/face/**").permitAll()
                .antMatchers("/video-analysis/**").permitAll()
                .antMatchers("/videos/**").permitAll()
                // ❌ 删除这行！面试记录需要认证
                // .antMatchers("/ai-interviews/**").permitAll()
                // 其他公开路径
                .antMatchers("/jobs/active", "/jobs/search", "/jobs/type/**").permitAll()
                .antMatchers("/external-resume/**", "/job-description/**").permitAll()
                .antMatchers("/resume-scoring/**", "/test-results/**").permitAll()
                .antMatchers("/candidate-answer-stats", "/applicant-management").permitAll()
                .antMatchers("/interview-evaluation/**", "/recommend/**").permitAll()
                .antMatchers("/student/**").permitAll()
                // 其他所有请求都需要认证
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
        // ✅ 改为 true，允许携带认证信息
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}