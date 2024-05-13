package com.example.coursemanagement.config;

import com.example.coursemanagement.security.JwtTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add the JwtTokenInterceptor to intercept all requests to the /notifications endpoint
//        registry.addInterceptor(jwtTokenInterceptor)
//                .addPathPatterns("/notifications/**");
    }
}

