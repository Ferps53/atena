package com.atena.auth;

import com.atena.exceptions.exception.UnauthorizedException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthRepository implements PanacheRepository<Auth> {

  public Auth findByRefreshToken(String refreshToken) {
    return (Auth)
        Auth.find("refreshToken", refreshToken)
            .firstResultOptional()
            .orElseThrow(UnauthorizedException::new);
  }
}
