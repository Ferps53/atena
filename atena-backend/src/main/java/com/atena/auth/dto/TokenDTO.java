package com.atena.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TokenDTO(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") int expiresIn,
    @JsonProperty("refresh_expires_in") int refreshExpiresIn) {}
