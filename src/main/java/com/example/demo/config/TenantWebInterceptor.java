package com.example.demo.config;

import com.example.demo.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class TenantWebInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if (UserContext.getTenantID() != null) {
            return true;
        }

        String tenantId = request.getHeader("tenant-id");
        System.out.println("📬 拦截器抓取到的 Header tenant-id 为: " + tenantId);
        if (tenantId == null || tenantId.isEmpty()) {
            UserContext.setTenantID(tenantId);
        }

        UserContext.setTenantID(tenantId);
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        UserContext.clear();
    }
}
