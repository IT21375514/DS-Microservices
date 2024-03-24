package com.universityTimetableManagementSystem.controller;

import com.universityTimetableManagementSystem.model.ERole;
import com.universityTimetableManagementSystem.model.ErrorDetails;
import com.universityTimetableManagementSystem.model.data.Role;
import com.universityTimetableManagementSystem.model.data.User;
import com.universityTimetableManagementSystem.model.security.LoginRequest;
import com.universityTimetableManagementSystem.model.security.SignupRequest;
import com.universityTimetableManagementSystem.repository.RoleRepository;
import com.universityTimetableManagementSystem.repository.UserRepository;
import com.universityTimetableManagementSystem.security.JwtUtils;
import com.universityTimetableManagementSystem.service.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tms/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final PasswordEncoder encoder;

  private final JwtUtils jwtUtils;

  public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                        RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.encoder = encoder;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    ResponseCookie userNameCookie = jwtUtils.generateUsernameCookie(userDetails.getUsername());

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .header(HttpHeaders.SET_COOKIE, userNameCookie.toString())
            .body("Sign in successful");


//    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return signupValidationFailResponse("Username already exists");
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return signupValidationFailResponse("Email already exists");
    }

    var user = User.builder()
        .username(signUpRequest.getUsername())
        .email(signUpRequest.getEmail())
        .password(encoder.encode(signUpRequest.getPassword()))
        .build();

    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles;

    roles = (strRoles == null || strRoles.isEmpty() ? List.of(ERole.ROLE_STUDENT.name()) : strRoles)
        .stream()
        .map(strRole -> roleRepository.findByName(ERole.valueOf(strRole.toUpperCase()))
            .orElseThrow(() -> new RuntimeException("Invalid role name")))
        .collect(Collectors.toSet());

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity
        .ok()
        .build();
  }

  private static ResponseEntity<ErrorDetails> signupValidationFailResponse(
      String usernameAlreadyExists) {
    return ResponseEntity
        .badRequest()
        .body(ErrorDetails.builder()
            .message(usernameAlreadyExists)
            .status(HttpStatus.BAD_REQUEST)
            .build());
  }
}