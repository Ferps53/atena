package com.atena.exceptions.exception;

import com.atena.exceptions.MessageTranslator;
import jakarta.ws.rs.core.Response;

public class GenericException extends RuntimeException {

  public final transient Response.StatusType status;

  public GenericException(String message, Response.StatusType status, Object... params) {
    super(MessageTranslator.loadMessage(message, params));
    this.status = status;
  }
}
