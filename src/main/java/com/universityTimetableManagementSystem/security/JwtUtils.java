package com.universityTimetableManagementSystem.security;

import com.universityTimetableManagementSystem.service.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

  public static final int MAX_AGE_DAY_IN_SECONDS = 24 * 60 * 60;
  public static final String BASE_PATH = "/tms";
  private final SecretKey verifyKey;


  private final int jwtExpirationMs;


//  private final String jwtCookie;


  public static final String JWT_COOKIE_NAME = "UniversityTimetableManagementSystem";

  public static final String USERNAME_COOKIE_NAME = "TMSUserName";

  public JwtUtils( @Value("${tms.jwt.secret}") String jwtSecret,
                   @Value("${tms.jwt.expirationMs}") int jwtExpirationMs) {
    this.verifyKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    this.jwtExpirationMs = jwtExpirationMs;
  }

//  public JwtUtils( @Value("${tms.jwt.secret}") String jwtSecret,
//                   @Value("${tms.jwt.expirationMs}") int jwtExpirationMs,
//                   @Value("${tms.jwt.cookieName}") String jwtCookie) {
//    this.verifyKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
//    this.jwtExpirationMs = jwtExpirationMs;
//    this.jwtCookie = jwtCookie;
//  }

  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, JWT_COOKIE_NAME);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
    String jwt = generateTokenFromUsername(userPrincipal.getUsername());
    return ResponseCookie.from(JWT_COOKIE_NAME, jwt)
        .path(BASE_PATH)
        .maxAge(MAX_AGE_DAY_IN_SECONDS)
        .httpOnly(true)
        .build();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser()
        .verifyWith(verifyKey).build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    Jwts.parser()
        .verifyWith(verifyKey)
        .build()
        .parse(authToken);
    return true;
  }

  public String generateTokenFromUsername(String username) {
    long nowInMillis = System.currentTimeMillis();
    return Jwts.builder()
        .subject(username)
        .issuedAt(new Date(nowInMillis))
        .expiration(new Date(nowInMillis + jwtExpirationMs))
        .signWith(verifyKey)
        .compact();
  }

  public ResponseCookie generateUsernameCookie(String username) {
    return ResponseCookie.from(USERNAME_COOKIE_NAME, username)
            .path(BASE_PATH)
            .maxAge(MAX_AGE_DAY_IN_SECONDS)
            .httpOnly(true)
            .build();
  }



}