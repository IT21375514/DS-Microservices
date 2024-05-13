package com.example.coursemanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

  public JwtUtils(@Value("${tms.jwt.secret}") String jwtSecret,
                  @Value("${tms.jwt.expirationMs}") int jwtExpirationMs ) {
    this.verifyKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    this.jwtExpirationMs = jwtExpirationMs;
  }



  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser()
            .verifyWith(verifyKey).build()
            .parseSignedClaims(token.replace("Bearer ",""))
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

//  public List<String> getUserRolesFromJwtToken(String token) {
//    Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(verifyKey).build().parseClaimsJws(token);
//    List<String> roles = new ArrayList<>();
//    List<?> rawRoles = (List<?>) claims.getBody().get("roles");
//    if (rawRoles != null) {
//      for (Object rawRole : rawRoles) {
//        roles.add((String) rawRole);
//      }
//    }
//    return roles;
//}

}