package com.atena.auth.controller;

import com.atena.auth.dto.TokenDTO;
import com.atena.exceptions.exception.BadRequestException;
import com.atena.exceptions.exception.UnauthorizedException;
import com.atena.user.UserDTO;
import com.atena.user.UserRepository;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class JwtController {

  @Inject JWTParser jwtParser;

  @Inject UserRepository userRepository;

  @ConfigProperty(name = "mp.jwt.verify.issuer")
  String issuer;

  public TokenDTO generateToken(UserDTO user) {

    return new TokenDTO(generateToken(user, false), generateToken(user, true));
  }

  public TokenDTO refreshToken(String refreshToken) {

    try {
      JsonWebToken jwt = jwtParser.parse(refreshToken);

      if (!jwt.getIssuer().equals(issuer)
          || jwt.getExpirationTime() < Instant.now().getEpochSecond())
        throw new UnauthorizedException();

      final String isRefresh = jwt.getClaim("is_refresh");
      if (isRefresh == null || isRefresh.equals("false")) throw new UnauthorizedException();

      final var user = userRepository.findUserDTOById(Long.valueOf(jwt.getSubject()));

      if (user == null) throw new UnauthorizedException();

      return generateToken(user);

    } catch (ParseException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  private String generateToken(UserDTO user, boolean isRefreshToken) {
    final Instant issuedAt = Instant.now();
    return Jwt.issuer(issuer)
        .issuedAt(issuedAt)
        .subject(user.id().toString())
        .expiresAt(
            isRefreshToken
                ? issuedAt.plus(4, ChronoUnit.HOURS)
                : issuedAt.plus(1, ChronoUnit.HOURS))
        .claim("is_refresh", Boolean.toString(isRefreshToken))
        .sign();
  }
}
