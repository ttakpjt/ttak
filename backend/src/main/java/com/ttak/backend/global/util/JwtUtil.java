// package com.ttak.backend.global.util;
//
// import static com.ttak.backend.global.common.ErrorCode.*;
//
// import java.util.Collections;
// import java.util.Date;
// import java.util.List;
// import java.util.stream.Collectors;
//
// import javax.crypto.SecretKey;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;
//
// import com.ttak.backend.domain.user.entity.User;
// import com.ttak.backend.domain.user.repository.UserRepository;
// import com.ttak.backend.global.auth.Entity.Token;
// import com.ttak.backend.global.auth.dto.UserPrincipal;
// import com.ttak.backend.global.auth.service.TokenService;
// import com.ttak.backend.global.exception.NotFoundException;
// import com.ttak.backend.global.exception.UnAuthorizedException;
//
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.MalformedJwtException;
// import io.jsonwebtoken.security.Keys;
// import io.jsonwebtoken.security.SecurityException;
// import jakarta.annotation.PostConstruct;
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class JwtUtil {
//
// 	private final UserRepository userRepository;
// 	@Value("${spring.jwt.secret}")
// 	private String key;
// 	@Value("${spring.jwt.expiration.access-token}")
// 	private int ACCESS_TOKEN_EXPIRE_TIME;
// 	@Value("${spring.jwt.expiration.refresh-token}")
// 	private int REFRESH_TOKEN_EXPIRE_TIME;;
//
// 	private SecretKey secretKey;
// 	private static final String KEY_ROLE = "role";
// 	private final TokenService tokenService;
//
// 	@PostConstruct
// 	private void setSecretKey() {
// 		secretKey = Keys.hmacShaKeyFor(key.getBytes());
// 	}
//
// 	public String generateAccessToken(Authentication authentication) {
// 		return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
// 	}
//
// 	public void generateRefreshToken(Authentication authentication, String accessToken, HttpServletResponse response) {
// 		String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
// 		UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
// 		tokenService.saveOrUpdate(userPrincipal.getUserId(), refreshToken, accessToken);
//
// 		response.addCookie(createCookies("refresh_token", refreshToken));
// 		System.out.println("refresh token: " + refreshToken);
// 	}
//
// 	private String generateToken(Authentication authentication, long expireTime) {
// 		Date now = new Date();
// 		Date expireDate = new Date(now.getTime() + expireTime);
// 		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//
// 		String authorities = authentication.getAuthorities().stream()
// 			.map(GrantedAuthority::getAuthority)
// 			.collect(Collectors.joining());
//
// 		return Jwts.builder()
// 			.subject(authentication.getName())
// 			.claim("userId", userPrincipal.getUserId())
// 			.claim(KEY_ROLE, authorities)
// 			.issuedAt(now)
// 			.expiration(expireDate)
// 			.signWith(secretKey, Jwts.SIG.HS512)
// 			.compact();
// 	}
//
// 	public Authentication getAuthentication(String token) {
// 		Claims claims = parseClaims(token);
// 		List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
// 		User user = userRepository.findByUserId(Long.valueOf(claims.get("userId").toString()))
// 			.orElseThrow(() -> new NotFoundException(A007));
//
//
// 		UserPrincipal userPrincipal = new UserPrincipal(user, claims);
// 		return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
// 	}
//
// 	private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
// 		return Collections.singletonList(new SimpleGrantedAuthority(
// 			claims.get(KEY_ROLE).toString()));
// 	}
//
// 	public String reissueAccessToken(String accessToken, HttpServletResponse response) {
// 		if (StringUtils.hasText(accessToken)) {
// 			Token token = tokenService.findByAccessTokenOrThrow(accessToken);
// 			String refreshToken = token.getRefreshToken();
//
// 			if (validateToken(refreshToken)) {
// 				String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
// 				generateRefreshToken(getAuthentication(accessToken), reissueAccessToken, response);
// 				tokenService.updateToken(reissueAccessToken, token);
// 				return reissueAccessToken;
// 			}
// 		}
// 		return null;
// 	}
//
// 	public boolean validateToken(String token) {
// 		if (!StringUtils.hasText(token)) {
// 			return false;
// 		}
//
// 		Claims claims = parseClaims(token);
// 		return claims.getExpiration().after(new Date());
// 	}
//
// 	private Claims parseClaims(String token) {
// 		try {
// 			return Jwts.parser().verifyWith(secretKey).build()
// 				.parseSignedClaims(token).getPayload();
// 		} catch (ExpiredJwtException e) {
// 			return e.getClaims();
// 		} catch (MalformedJwtException e) {
// 			throw new UnAuthorizedException(A004);
// 		} catch (SecurityException e) {
// 			throw new UnAuthorizedException(A005);
// 		}
// 	}
//
//
// 	public Long getUserId(String token) {
// 		return Jwts.parser()
// 			.verifyWith(secretKey)
// 			.build()
// 			.parseSignedClaims(token)
// 			.getPayload()
// 			.get("userId", Long.class);
// 	}
//
//
// 	// public Boolean isExpired(String token) {
// 	// 	return Jwts.parser()
// 	// 		.verifyWith(secretKey)
// 	// 		.build()
// 	// 		.parseSignedClaims(token)
// 	// 		.getPayload()
// 	// 		.getExpiration()
// 	// 		.before(new Date());
// 	// }
// 	//
// 	//
// 	// public String createAccessToken(Long userId, Long expiredTime, Date now) {
// 	// 	return Jwts.builder()
// 	// 		.claim("category", "access")
// 	// 		.claim("userId", userId)
// 	// 		.issuedAt(now)
// 	// 		.expiration(new Date(now.getTime() + expiredTime))
// 	// 		.signWith(secretKey)
// 	// 		.compact();
// 	// }
// 	//
// 	// public String createRefreshToken(Long expiredTime, Date now) {
// 	// 	return Jwts.builder()
// 	// 		.claim("category", "refresh")
// 	// 		.issuedAt(now)
// 	// 		.expiration(new Date(now.getTime() + expiredTime))
// 	// 		.signWith(secretKey)
// 	// 		.compact();
// 	// }
// 	//
// 	public Cookie createCookies(String key, String value) {
// 		Cookie cookie = new Cookie(key, value);
// 		cookie.setMaxAge(REFRESH_TOKEN_EXPIRE_TIME); //10년
// 		cookie.setSecure(true);
// 		cookie.setHttpOnly(true);
// 		cookie.setPath("/");
//
// 		return cookie;
// 	}
//
// }
