package com.atena.auth.controller;

import com.atena.auth.Auth;
import com.atena.auth.AuthDTO;
import com.atena.auth.AuthRepository;
import com.atena.exceptions.exception.UnauthorizedException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Named;
import java.time.LocalDateTime;

@Named
@RequestScoped
public class SessionProducer {

  private final AuthCache authCache;
  private final AuthRepository authRepository;

  public SessionProducer(AuthCache authCache, AuthRepository authRepository) {
    this.authCache = authCache;
    this.authRepository = authRepository;
  }

  @Default
  @Session
  @RequestScoped
  public SessionModel loadUserSession() {
    final String authToken = SessionHolder.getSession();
    AuthDTO authDTO = authCache.load(authToken);
    if (authDTO != null) {
      return new SessionModel(authDTO);
    }

    final Auth auth = authRepository.findLatestActiveTokenByAccess(authToken);
    if (auth.getExpireTime().isAfter(LocalDateTime.now())) {
      return new SessionModel(AuthDTO.fromAuth(auth));
    }

    throw new UnauthorizedException();
  }
}
