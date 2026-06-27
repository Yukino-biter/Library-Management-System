package com.hhu.bookshop.config;

import com.hhu.bookshop.filter.AuthFilter;
import com.hhu.bookshop.filter.EncodingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web配置类 - 注册过滤器的执行顺序
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private EncodingFilter encodingFilter;

    @Autowired
    private AuthFilter authFilter;

    @Value("${upload.path:src/main/resources/static/uploads/}")
    private String uploadPath;

    /**
     * 将 /uploads/** 映射到文件系统实际目录，
     * 解决运行时上传的文件无法立即访问、必须重启的问题
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = new File(uploadPath).getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }

    /**
     * 编码过滤器（最高优先级）
     */
    @Bean
    public FilterRegistrationBean<EncodingFilter> encodingFilterRegistration() {
        FilterRegistrationBean<EncodingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(encodingFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("encodingFilter");
        return registration;
    }

    /**
     * 认证过滤器（编码之后）
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(authFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        registration.setName("authFilter");
        return registration;
    }
}
