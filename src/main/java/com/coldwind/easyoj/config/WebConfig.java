package com.coldwind.easyoj.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     * 允许所有来源访问API接口
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 只对/api开头的路径启用CORS
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    /**
     * 文件上传配置
     * 限制单个文件最大10MB，请求最大10MB
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 文件大小限制
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        // 总请求大小限制
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        // 设置临时目录（可选）
        factory.setLocation(System.getProperty("java.io.tmpdir"));
        return factory.createMultipartConfig();
    }

    /**
     * 额外的Web配置可以在此添加
     */
    // 例如：添加拦截器、格式化器等
}