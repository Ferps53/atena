package com.atena.auth.controller;

import com.atena.auth.AuthDTO;

public class SessionModel {

  private final AuthDTO authDTO;

  public SessionModel(AuthDTO authDTO) {
    this.authDTO = authDTO;
  }

  public AuthDTO getAuth() {
    return this.authDTO;
  }
}
