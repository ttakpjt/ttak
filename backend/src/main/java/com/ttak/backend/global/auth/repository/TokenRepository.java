package com.ttak.backend.global.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ttak.backend.global.auth.Entity.Token;

public interface TokenRepository extends CrudRepository<Token, String> {

	Optional<Token> findByAccessToken(String accessToken);

}
