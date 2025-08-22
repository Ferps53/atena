package com.atena.exceptions;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@ApplicationScoped
public final class MessageTranslator {

  private MessageTranslator() {}

  public static String loadMessage(String msgCode, Object... params) {

    if (msgCode == null) {
      return "No error message content";
    }

    try {
      final String message = loadMessageFromResources(msgCode);
      return fillParameters(message, params);
    } catch (MissingResourceException e) {
      return fillParameters(msgCode, params);
    }
  }

  private static String loadMessageFromResources(String msgCode) {

    return ResourceBundle.getBundle(
            "messages", Locale.of("pt", "BR"), Thread.currentThread().getContextClassLoader())
        .getString(msgCode);
  }

  private static String fillParameters(String msg, Object... params) {

    if (params.length == 0 && !msg.contains("?1")) {
      return msg;
    }

    if (!msg.contains("?1")) {
      throw new IllegalStateException(
          "Não é possível passar parametros para uma mensagem de erro sem \"?1\"");
    }

    validateParamLength(msg, params);

    for (int j = 0; j < params.length; j++) {
      final Object o = params[j];
      final String param = "?".concat(String.valueOf(j + 1));
      msg = msg.replace(param, o.toString());
    }

    return msg;
  }

  private static void validateParamLength(String msg, Object[] params) {
    int i = 0;
    while (i < params.length) {

      final String param = "?".concat(String.valueOf(i + 1));
      if (!msg.contains(param)) {
        throw new IllegalStateException("Não foi possível encontrar o parametro " + param);
      }
      i++;
    }

    final String param = "?".concat(String.valueOf(i + 1));
    if (msg.contains(param)) {
      throw new IllegalStateException(
          "Não foi possível encontrar o objeto para mapear o parametro " + param);
    }
  }
}
