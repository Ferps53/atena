package com.atena.auth;

import com.atena.exceptions.exception.UnauthorizedException;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthRepository implements PanacheRepositoryBase<Auth, String> {

  public Auth findByRefreshToken(String refreshToken) {
    return find("refreshToken", refreshToken)
        .firstResultOptional()
        .orElseThrow(UnauthorizedException::new);
  }

  public Auth findLatestActiveTokenByAccess(String accessToken) {
    return find("accessToken = ?1 and isValid = true", Sort.descending("createdAt"), accessToken)
        .firstResultOptional()
        .orElseThrow(UnauthorizedException::new);
  }
}
