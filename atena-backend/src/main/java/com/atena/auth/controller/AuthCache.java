package com.atena.auth.controller;

import com.atena.auth.AuthDTO;
import com.atena.redis.RedisCache;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthCache extends RedisCache<AuthDTO> {

  private static final long HOUR_IN_SECONDS = 60 * 60;

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
