package com.atena.core.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.atena.auth.controller.AuthCache;
import com.atena.auth.controller.AuthController;
import com.atena.confirmation_code.ConfirmationCodeController;
import com.atena.exceptions.exception.BadRequestException;
import com.atena.exceptions.exception.UnauthorizedException;
import com.atena.mailer.EmailController;
import com.atena.mailer.dto.EmailDTO;
import com.atena.user.NewUserCreatedDTO;
import com.atena.user.User;
import com.atena.user.UserMapper;
import com.atena.user.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import java.util.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthControllerTest {

  private static final User TEST_USER = new User();
  private static final User TEST_USER_INCORRECT_PASSWORD = new User();
  private static final NewUserCreatedDTO expectedUser =
      new NewUserCreatedDTO(null, "test", "test@gmail.com");
  private AuthController authController;

  private UserRepository userRepository;

  private UserMapper userMapper;

  private EmailController emailController;

  private ConfirmationCodeController confirmationCodeController;

  private AuthCache authCache;

  private String basicUsername;

  private String basicPassword;

  @BeforeAll
  static void setUserValues() {
    TEST_USER.setIdUser(1);
    TEST_USER.setName("test");
    TEST_USER.setEmail("test@gmail.com");
    TEST_USER.setPassword(BcryptUtil.bcryptHash("12345678"));

    TEST_USER_INCORRECT_PASSWORD.setIdUser(1);
    TEST_USER_INCORRECT_PASSWORD.setName("test");
    TEST_USER_INCORRECT_PASSWORD.setEmail("test@gmail.com");
    TEST_USER_INCORRECT_PASSWORD.setPassword(BcryptUtil.bcryptHash("wrongpass"));
  }

  @BeforeEach
  void beforeEach() {

    userRepository = mock(UserRepository.class);
    userMapper = mock(UserMapper.class);
    emailController = mock(EmailController.class);
    confirmationCodeController = mock(ConfirmationCodeController.class);
    authCache = mock(AuthCache.class);
    basicUsername = "Test";
    basicPassword = "Test";

    authController =
        new AuthController(
            userRepository,
            userMapper,
            emailController,
            confirmationCodeController,
            authCache,
            basicUsername,
            basicPassword);
  }

  @Test
  void createUserOk() {

    when(userRepository.doesUserExists(anyString(), anyString())).thenReturn(false);
    doNothing().when(userRepository).persist(any(User.class));
    doNothing().when(emailController).sendEmail(any(EmailDTO.class));
    final NewUserCreatedDTO newUser =
        authController.createNewUser(
            generateBasic(), TEST_USER.getName(), TEST_USER.getEmail(), "12345678");

    assertEquals(expectedUser, newUser);
  }

  @Test
  void createUserFailBasic() {

    when(userRepository.doesUserExists(anyString(), anyString())).thenReturn(false);
    doNothing().when(userRepository).persist(any(User.class));
    doNothing().when(emailController).sendEmail(any(EmailDTO.class));

    assertThrows(
        UnauthorizedException.class,
        () ->
            authController.createNewUser(
                "wrongBasic", TEST_USER.getName(), TEST_USER.getEmail(), "12345678"));
  }

  @Test
  void createUserFailNullParameters() {

    assertThrows(
        BadRequestException.class,
        () -> authController.createNewUser(generateBasic(), null, null, null));
  }

  @Test
  void createUserFailEmptyParameters() {

    assertThrows(
        BadRequestException.class, () -> authController.createNewUser(generateBasic(), "", "", ""));
  }

  @Test
  void createUserAlreadyExists() {

    when(userRepository.doesUserExists(anyString(), anyString())).thenReturn(true);
    assertThrows(
        BadRequestException.class,
        () ->
            authController.createNewUser(
                generateBasic(), TEST_USER.getName(), TEST_USER.getEmail(), "12345678"));
  }

  @Test
  void loginOk() {

    when(userRepository.findUserLogin(anyString())).thenReturn(TEST_USER);
    assertNotNull(authController.login(generateBasic(), TEST_USER.getEmail(), "12345678"));
  }

  @Test
  void loginInvalidPassword() {
    when(userRepository.findUserLogin(anyString())).thenReturn(TEST_USER_INCORRECT_PASSWORD);
    assertThrows(
        UnauthorizedException.class,
        () -> authController.login(generateBasic(), TEST_USER.getEmail(), "12345678"));
  }

  String generateBasic() {
    return "BASIC "
        + Base64.getEncoder().encodeToString((basicUsername + ":" + basicPassword).getBytes());
  }
}
