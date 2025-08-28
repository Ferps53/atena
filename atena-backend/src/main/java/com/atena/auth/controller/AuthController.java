package com.atena.auth.controller;

import com.atena.auth.Auth;
import com.atena.auth.AuthDTO;
import com.atena.auth.AuthRepository;
import com.atena.auth.dto.TokenDTO;
import com.atena.confirmation_code.ConfirmationCode;
import com.atena.confirmation_code.ConfirmationCodeController;
import com.atena.exceptions.exception.BadRequestException;
import com.atena.exceptions.exception.UnauthorizedException;
import com.atena.mailer.EmailController;
import com.atena.mailer.dto.EmailContentsDTO;
import com.atena.mailer.dto.EmailDTO;
import com.atena.mailer.enums.EmailImages;
import com.atena.mailer.enums.EmailModels;
import com.atena.user.NewUserCreatedDTO;
import com.atena.user.User;
import com.atena.user.UserMapper;
import com.atena.user.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Transactional
@ApplicationScoped
public class AuthController {

  private static final int HOUR_IN_SECONDS = 60 * 60;
  private static final int FOUR_HOURS_IN_SECONDS = HOUR_IN_SECONDS * 4;

  private final String basicUsername;
  private final String basicPassword;
  private final AuthRepository authRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final EmailController emailController;
  private final ConfirmationCodeController confirmationCodeController;
  private final AuthCache authCache;

  @Inject
  public AuthController(
      UserRepository userRepository,
      AuthRepository authRepository,
      UserMapper userMapper,
      EmailController emailController,
      ConfirmationCodeController confirmationCodeController,
      AuthCache authCache,
      @ConfigProperty(name = "BASIC_USERNAME", defaultValue = "Test") String basicUsername,
      @ConfigProperty(name = "BASIC_PASSWORD", defaultValue = "Test") String basicPassword) {

    this.userRepository = userRepository;
    this.authRepository = authRepository;
    this.userMapper = userMapper;
    this.emailController = emailController;
    this.confirmationCodeController = confirmationCodeController;
    this.authCache = authCache;
    this.basicUsername = basicUsername;
    this.basicPassword = basicPassword;
  }

  public NewUserCreatedDTO createNewUser(
      String basic, String username, String email, String password) {

    validateBasic(basic);

    if (username == null
        || username.isEmpty()
        || email == null
        || email.isEmpty()
        || password == null
        || password.isEmpty()) {
      throw new BadRequestException("user.sign.in.notNull");
    }

    username = decode(username);
    password = BcryptUtil.bcryptHash(decode(password));
    email = decode(email);

    if (userRepository.doesUserExists(username, email)) {
      throw new BadRequestException("user.exists");
    }

    final User user = User.createNewUser(username, email, password);
    userRepository.persist(user);

    sendConfirmationEmail(user);
    return userMapper.toUserCreatedDTO(user);
  }

  public TokenDTO login(String basic, String usernameOrEmail, String password) {

    validateBasic(basic);

    if (usernameOrEmail == null
        || usernameOrEmail.isEmpty()
        || password == null
        || password.isEmpty()) {
      throw new BadRequestException("user.login.notNull");
    }

    usernameOrEmail = decode(usernameOrEmail);
    password = decode(password);

    final User user = userRepository.findUserLogin(usernameOrEmail);

    if (!BcryptUtil.matches(password, user.getPassword())) {
      throw new UnauthorizedException("user.login.password.incorrect");
    }

    final var tokenDTO =
        new TokenDTO(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            HOUR_IN_SECONDS,
            FOUR_HOURS_IN_SECONDS);

    final var auth = new Auth(tokenDTO, user);
    authRepository.persist(auth);

    final var authDTO = AuthDTO.fromAuth(auth);
    authCache.saveWithExpiration(authDTO, auth.getAccessToken());

    return tokenDTO;
  }

  public TokenDTO refreshToken(String basic, String refreshToken) {

    validateBasic(basic);

    final Auth oldAuth = authRepository.findByRefreshToken(refreshToken);

    if (!oldAuth.isValid() || LocalDateTime.now().isAfter(oldAuth.getRefreshExpireTime())) {
      throw new UnauthorizedException();
    }

    oldAuth.invalidate();
    oldAuth.persist();

    final var tokenDTO =
        new TokenDTO(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            HOUR_IN_SECONDS,
            FOUR_HOURS_IN_SECONDS);

    final var auth = new Auth(tokenDTO, oldAuth.getUser());
    final var authDTO = AuthDTO.fromAuth(auth);
    authCache.saveWithExpiration(authDTO, authDTO.accessToken());

    return tokenDTO;
  }

  @Transactional(dontRollbackOn = BadRequestException.class)
  public void confirmEmail(String basic, String confirmationCode, String email) {

    validateBasic(basic);

    try {

      confirmationCodeController.validateCode(confirmationCode, email);
      userRepository.confirmEmail(email);

    } catch (BadRequestException e) {

      final ConfirmationCode oldCode =
          ConfirmationCode.find("user.email = ?1", email).firstResult();
      sendConfirmationEmail(oldCode.user);
      oldCode.delete();

      throw new BadRequestException("user.new.code.sent");
    }
  }

  public void resendConfirmationEmail(String basic, String email) {

    validateBasic(basic);
    Optional<User> userOptional = User.find("email = ?1", email).firstResultOptional();

    // Not throwing an error hides the email registered in the db
    userOptional.ifPresent((this::sendConfirmationEmail));
  }

  private void validateBasic(String basic) {

    if (basic == null || basic.isBlank() || !basic.toUpperCase().startsWith("BASIC "))
      throw new UnauthorizedException();

    final String expectedBase64 =
        Base64.getEncoder().encodeToString((basicUsername + ":" + basicPassword).getBytes());

    if (!basic.substring(6).equals(expectedBase64)) throw new UnauthorizedException();
  }

  private String decode(String param) {
    return URLDecoder.decode(param, StandardCharsets.UTF_8);
  }

  private void sendConfirmationEmail(User user) {

    final String code = confirmationCodeController.createCode(6, user);

    final var contents =
        List.of(
            new EmailContentsDTO("-username-", user.getName(), false),
            new EmailContentsDTO("-code-", code, false));

    final var images = List.of(EmailImages.LOGO);
    emailController.sendEmail(
        new EmailDTO(user.getEmail(), EmailModels.EMAIL_CONFIRMATION, contents, images));
  }
}
