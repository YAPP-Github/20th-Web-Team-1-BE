package com.yapp.betree.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yapp.betree.dto.LoginUserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.yapp.betree.domain.UserTest.TEST_SAVE_USER;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUser생성 아규먼트 리졸버 테스트")
public class ArgumentResolverTest {
    private UserHandlerMethodArgumentResolver userHandlerMethodArgumentResolver;


    @BeforeEach
    void setUp() {
        userHandlerMethodArgumentResolver = new testArgumentResolver();
    }

    @Test
    @DisplayName("아규먼트 리졸버가 로그인 유저를 반환한다.")
    void argumentResolverTest() throws JsonProcessingException {
        LoginUserDto user = (LoginUserDto) userHandlerMethodArgumentResolver.resolveArgument(null, null, null, null);

        Assertions.assertThat(user.getId()).isEqualTo(TEST_SAVE_USER.getId());
        Assertions.assertThat(user.getEmail()).isEqualTo(TEST_SAVE_USER.getEmail());
        Assertions.assertThat(user.getNickname()).isEqualTo(TEST_SAVE_USER.getNickName());
    }

    static class testArgumentResolver extends UserHandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return true;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws JsonProcessingException {
            return LoginUserDto.of(TEST_SAVE_USER);
        }
    }
}
