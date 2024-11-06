package com.ttak.backend.global.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FcmConfig {

	private final ClassPathResource firebaseResource = new ClassPathResource(
		"ttak-440401-firebase-adminsdk-a7dv0-b4b7563f3b.json");

	@Bean
	FirebaseApp firebaseApp() throws IOException {
		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(
				firebaseResource.getInputStream()))
			.build();

		return FirebaseApp.initializeApp(options);
	}

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		return FirebaseMessaging.getInstance(firebaseApp());
	}
}
