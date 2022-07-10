package com.yapp.betree.config;

import com.yapp.betree.interceptor.TokenInterceptor;
import com.yapp.betree.interceptor.UserHandlerMethodArgumentResolver;
import com.yapp.betree.service.oauth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization")
                .maxAge(3000);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor(jwtTokenProvider))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/signin", "/api/refresh-token");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserHandlerMethodArgumentResolver());
    }
}
