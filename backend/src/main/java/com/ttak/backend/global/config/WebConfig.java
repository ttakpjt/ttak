package com.ttak.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import com.ttak.backend.global.auth.annotation.resolver.UserPkArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final UserPkArgumentResolver userPkArgumentResolver;

	public WebConfig(UserPkArgumentResolver userPkArgumentResolver) {
		this.userPkArgumentResolver = userPkArgumentResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(userPkArgumentResolver);
	}
}
