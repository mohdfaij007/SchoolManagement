package com.schoolmanagement.schoolbackend.config;

import com.schoolmanagement.schoolbackend.security.UserDetailsImpl;
import com.schoolmanagement.schoolbackend.security.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Autowired
    private EntityManager entityManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        // 1. JWT Filter ne request ko pehle hi authenticate kar diya hai
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Check karte hain ki user logged in hai ya nahi
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            
            Long tenantId = null;
            String userRole = userPrincipal.getRole(); // Tumhara getRole() method use ho raha hai

            // 3. SUPER ADMIN LOGIC (Impersonation)
            if (userRole != null && userRole.equals("SUPER_ADMIN")) {
                // Super Admin UI se dropdown select karke 'X-Tenant-ID' bhejega
                String headerTenantId = request.getHeader("X-Tenant-ID");
                if (headerTenantId != null && !headerTenantId.isEmpty()) {
                    tenantId = Long.valueOf(headerTenantId);
                }
            } 
            // 4. NORMAL USER LOGIC (Hacking-Proof Security)
            else {
                // Yaha header ignore hoga aur direct JWT/Database se verified schoolId niklegi
                tenantId = userPrincipal.getSchoolId(); // Tumhara getSchoolId() method use ho raha hai
            }

            // 5. Agar school ki ID mil gayi, toh poore database session ke liye lock (filter) laga do
            if (tenantId != null) {
                TenantContext.setCurrentTenant(tenantId);
                Session session = entityManager.unwrap(Session.class);
                session.enableFilter("tenantFilter").setParameter("schoolId", tenantId);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Memory leak se bachne ke liye request end hone par context zarur clear karein
        TenantContext.clear();
    }
}