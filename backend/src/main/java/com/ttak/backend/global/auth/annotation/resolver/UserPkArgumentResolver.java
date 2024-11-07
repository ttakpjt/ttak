package com.ttak.backend.global.auth.annotation.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.ttak.backend.global.auth.annotation.UserPk;

@Component
public class UserPkArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(UserPk.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {
		String userPk = webRequest.getHeader("user");
		if (userPk == null) {
			throw new IllegalArgumentException("Header 'user' is missing");
		}
		return Long.parseLong(userPk);  // userPk 타입에 맞게 변환 (Long 또는 Integer)
	}
}

