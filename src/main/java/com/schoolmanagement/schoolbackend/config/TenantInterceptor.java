package com.schoolmanagement.schoolbackend.config;

import com.schoolmanagement.schoolbackend.security.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Autowired
    private EntityManager entityManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Frontend se bheja gaya 'X-Tenant-ID' header read karo
        String tenantIdStr = request.getHeader("X-Tenant-ID");
        
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            Long tenantId = Long.valueOf(tenantIdStr);
            TenantContext.setCurrentTenant(tenantId);
            
            // Hibernate session me filter enable kar do
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("schoolId", tenantId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Memory leak se bachne ke liye request khatam hone par context clear karna zaroori hai
        TenantContext.clear();
    }
}