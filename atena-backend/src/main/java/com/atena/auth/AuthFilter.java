package com.atena.auth;

import com.atena.auth.controller.AuthController;
import com.atena.exceptions.exception.UnauthorizedException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Provider
@Singleton
@Priority(1)
public class AuthFilter implements ContainerRequestFilter {

  private final ResourceInfo info;
  private final AuthController authController;
  private final String basicUsername;
  private final String basicPassword;

  @Inject
  public AuthFilter(
      ResourceInfo info,
      AuthController authController,
      @ConfigProperty(name = "BASIC_USERNAME", defaultValue = "Test") String basicUsername,
      @ConfigProperty(name = "BASIC_PASSWORD", defaultValue = "Test") String basicPassword) {
    this.info = info;
    this.authController = authController;
    this.basicUsername = basicUsername;
    this.basicPassword = basicPassword;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {

    final String headerAuthorization = getAuthorizationHeader(requestContext);

    if (info.getResourceMethod().isAnnotationPresent(PublicSession.class)) {
      validateBasic(headerAuthorization);
      return;
    }

    authController.validateAccessToken(headerAuthorization);
  }

  private String getAuthorizationHeader(ContainerRequestContext requestContext) {
    final String headerAuthorization = requestContext.getHeaderString("Authorization");

    if (headerAuthorization == null || headerAuthorization.isBlank()) {
      throw new UnauthorizedException();
    }

    if (headerAuthorization.toUpperCase().startsWith("BASIC ")
        || headerAuthorization.toUpperCase().startsWith("BEARER ")) {
      return headerAuthorization.substring(6).trim();
    }
    throw new UnauthorizedException();
  }

  private void validateBasic(String basic) {

    final byte[] bytes = (basicUsername + ":" + basicPassword).getBytes(StandardCharsets.UTF_8);
    final String encoded = Base64.getEncoder().encodeToString(bytes);
    if (!basic.equals(encoded)) {
      throw new UnauthorizedException();
    }
  }
}
