package com.atena.confirmation_code;

import com.atena.exceptions.exception.BadRequestException;
import com.atena.exceptions.exception.NotFoundException;
import com.atena.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.Random;

@ApplicationScoped
public class ConfirmationCodeController {

  private final Random random = new Random();
  @Inject ConfirmationCodeRepository repository;

  public String createCode(int size, User user) {

    String code = generateCode(size);
    final var confirmationCodes = repository.listAll();

    for (ConfirmationCode confirmationCode : confirmationCodes) {
      if (confirmationCode.code.equals(code)) {
        code = generateCode(size);
      }
    }

    final var confirmationCode = new ConfirmationCode(code, user);

    repository.persist(confirmationCode);
    return code;
  }

  public void validateCode(String code, String email) {

    final ConfirmationCode confirmationCode =
        repository.findConfirmationCodeByCodeAndUserEmail(code, email);

    if (confirmationCode == null) throw new NotFoundException("Invalid confirmation code");

    if (confirmationCode.expiryDate.isBefore(LocalDateTime.now())) {
      throw new BadRequestException("Expired confirmation code");
    }

    // Deletes the code so it isn't used twice
    repository.delete(confirmationCode);
  }

  private String generateCode(int size) {

    final StringBuilder randomCode = new StringBuilder();
    for (int i = 0; i < size; i++) {
      randomCode.append(random.nextInt(10));
    }

    return randomCode.toString();
  }
}
