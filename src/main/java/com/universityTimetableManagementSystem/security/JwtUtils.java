package com.universityTimetableManagementSystem.security;

import com.universityTimetableManagementSystem.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class    JwtUtils {

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
    Collection<? extends GrantedAuthority> rolesOfUser=userPrincipal.getAuthorities();
    String jwt = generateTokenFromUsername(userPrincipal.getUsername(),rolesOfUser);
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
  public String generateTokenFromUsername(String username, Collection<? extends GrantedAuthority> roles) {
    long nowInMillis = System.currentTimeMillis();
    JwtBuilder jwtBuilder = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(nowInMillis))
            .setExpiration(new Date(nowInMillis + jwtExpirationMs))
            .signWith(verifyKey);

    if (roles != null && !roles.isEmpty()) {
      List<String> roleNames = roles.stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toList());
      jwtBuilder.claim("roles", roleNames); // Include role names in the payload
    }

    String token = jwtBuilder.compact();

    // Ensure token has exactly two periods
    while (token.chars().filter(ch -> ch == '.').count() != 2) {
      token = jwtBuilder.compact();
    }

    return token;
  }



  public ResponseCookie generateUsernameCookie(String username) {
    return ResponseCookie.from(USERNAME_COOKIE_NAME, username)
            .path(BASE_PATH)
            .maxAge(MAX_AGE_DAY_IN_SECONDS)
            .httpOnly(true)
            .build();
  }

  public List<String> getUserRolesFromJwtToken(String token) {
    Jws<Claims> claims = Jwts.parser().setSigningKey(verifyKey).build().parseClaimsJws(token);
    String rolesClaim = (String) claims.getBody().get("roles");
    return List.of(rolesClaim.split(","));
  }


}