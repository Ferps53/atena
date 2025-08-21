package com.atena.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TokenDTO(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") int expiresIn,
    @JsonProperty("refresh_expires_in") int refreshExpiresIn,
    @JsonProperty("type") String tokenType) {

  static final int HOUR_IN_SECONDS = 60 * 60;
  static final int FOUR_HOURS_IN_SECONDS = HOUR_IN_SECONDS * 4;

  public TokenDTO(String accessToken, String refreshToken) {
    this(accessToken, refreshToken, HOUR_IN_SECONDS, FOUR_HOURS_IN_SECONDS, "bearer");
  }
}
