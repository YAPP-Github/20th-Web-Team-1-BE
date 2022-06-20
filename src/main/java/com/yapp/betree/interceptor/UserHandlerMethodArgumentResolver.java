package com.yapp.betree.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yapp.betree.annotation.LoginUser;
import com.yapp.betree.dto.LoginUserDto;
import com.yapp.betree.exception.BetreeException;
import com.yapp.betree.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

import static com.yapp.betree.interceptor.TokenInterceptor.USER_ATTR_KEY;

public class UserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws JsonProcessingException {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        LoginUserDto loginUserDto = (LoginUserDto) request.getAttribute(USER_ATTR_KEY);

        LoginUser loginUser = parameter.getParameterAnnotation(LoginUser.class);

        if (loginUser == null || loginUserDto == null) {
            throw new BetreeException(ErrorCode.USER_NOT_FOUND, "로그인된 사용자 정보가 존재하지 않습니다. user = " + loginUserDto);
        }

        return loginUserDto;
    }
}
