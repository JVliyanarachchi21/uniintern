package com.uniintern.portal.config;

import com.uniintern.portal.admin.AdminInterceptor;
import com.uniintern.portal.company.interceptor.CompanyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CompanyInterceptor companyInterceptor;

    public WebConfig(CompanyInterceptor companyInterceptor) {
        this.companyInterceptor = companyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ... Company Interceptor
        registry.addInterceptor(companyInterceptor)
                .addPathPatterns("/company/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}