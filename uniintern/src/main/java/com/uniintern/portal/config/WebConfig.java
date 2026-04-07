package com.uniintern.portal.config;

import com.uniintern.portal.admin.AdminInterceptor;
import com.uniintern.portal.company.interceptor.CompanyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;
    private final CompanyInterceptor companyInterceptor;

    public WebConfig(AdminInterceptor adminInterceptor, CompanyInterceptor companyInterceptor) {
        this.adminInterceptor = adminInterceptor;
        this.companyInterceptor = companyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ... Admin Interceptor
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/logout",
                        "/admin/css/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**");
                        
        // ... Company Interceptor
        registry.addInterceptor(companyInterceptor)
                .addPathPatterns("/company/**");
    }
}