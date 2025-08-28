package com.atena.auth.controller;

import com.atena.auth.AuthDTO;
import com.atena.redis.RedisCacheTemplate;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthCache extends RedisCacheTemplate<AuthDTO> {

  private static final long HOUR_IN_SECONDS = 60L * 60L;

  @Override
  public String generateKey(String param) {
    return param;
  }

  @Override
  public long expirationTime() {
    return HOUR_IN_SECONDS;
  }

  @Override
  public Class<AuthDTO> classDef() {
    return AuthDTO.class;
  }
}
