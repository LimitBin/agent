package com.example.demo.config;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private TenantWebInterceptor tenantWebInterceptor;
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")             // 拦截所有路径
                .excludePathPatterns("/auth/login")// 排除登录接口，否则没法领证
                .excludePathPatterns("/test/user/list")
                .order(1);
        registry.addInterceptor(tenantWebInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login")
                .order(2);
    }
}