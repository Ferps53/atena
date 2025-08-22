package com.atena.exceptions.exception;

import jakarta.ws.rs.core.Response;

public class NotFoundException extends GenericException {

  public NotFoundException(String message, Object... params) {
    super(message, Response.Status.NOT_FOUND, params);
  }

  public NotFoundException() {
    super("msg.notfound", Response.Status.NOT_FOUND);
  }
}
