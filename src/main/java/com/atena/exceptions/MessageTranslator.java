package com.atena.exceptions;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
public final class MessageTranslator {

  private MessageTranslator() {}

  public static String translate(String messageCode) {

    try {
      return ResourceBundle.getBundle(
              "messages", Locale.of("pt", "BR"), Thread.currentThread().getContextClassLoader())
          .getString(messageCode);
    } catch (Exception e) {
      return messageCode;
    }
  }
}
