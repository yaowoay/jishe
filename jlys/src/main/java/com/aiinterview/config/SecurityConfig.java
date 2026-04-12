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
                // 认证相关路径 - 最重要，放在最前面
                .antMatchers("/auth/**").permitAll()
                // DISC测试相关路径
                .antMatchers("/disc-test/**").permitAll()
                // 人脸检测相关路径
                .antMatchers("/face/**").permitAll()
                // 视频分析相关路径
                .antMatchers("/video-analysis/**").permitAll()
                // 视频上传相关路径
                .antMatchers("/video/**").permitAll()
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
        configuration.setMaxAge(3600L); // 预检请求缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
