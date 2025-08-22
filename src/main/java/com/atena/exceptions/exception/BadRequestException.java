package com.atena.exceptions.exception;

import jakarta.ws.rs.core.Response;

public class BadRequestException extends GenericException {

  public BadRequestException(String message, Object... params) {
    super(message, Response.Status.BAD_REQUEST, params);
  }

  public BadRequestException() {
    super("msg.badrequest", Response.Status.BAD_REQUEST);
  }

  public BadRequestException(Exception e) {
    super(e.getMessage(), Response.Status.BAD_REQUEST);
  }
}
