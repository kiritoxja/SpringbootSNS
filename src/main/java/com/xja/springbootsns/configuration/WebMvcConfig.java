package com.xja.springbootsns.configuration;

import com.xja.springbootsns.interceptor.LoginInterceptor;
import com.xja.springbootsns.interceptor.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

/**
 *
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    UserInfoInterceptor userInfoInterceptor;

    @Autowired
    LoginInterceptor loginInterceptor;

    //添加视图映射
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
    }


    //添加自己的区域解析器
    @Bean
    public LocaleResolver localeResolver(){
        return new LocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                Optional<String> localeParam = Optional.ofNullable(request.getParameter("locale"));
                Locale locale = localeParam.map(l->{
                    String[] strings = l.split("_");
                    return new Locale(strings[0],strings[1]);
                }).orElse(Locale.getDefault());
                return locale;
            }

            @Override
            public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

            }
        };
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInfoInterceptor);
        registry.addInterceptor(loginInterceptor).addPathPatterns("/user/*");
    }
}
