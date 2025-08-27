package com.atena.auth;

import java.time.LocalDateTime;

public record AuthDTO(
    String accessToken,
    String refreshToken,
    LocalDateTime createdAt,
    LocalDateTime expireTime,
    LocalDateTime refreshExpireTime,
    Integer idUser) {

  public static AuthDTO fromAuth(Auth auth) {

    return new AuthDTO(
        auth.getAccessToken(),
        auth.getRefreshToken(),
        auth.getCreatedAt(),
        auth.getExpireTime(),
        auth.getRefreshExpireTime(),
        auth.getUser().getIdUser());
  }
}
