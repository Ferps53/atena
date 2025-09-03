package com.atena.auth.controller;

public class SessionHolder {

  private static final InheritableThreadLocal<String> CURRENT_SESSION =
      new InheritableThreadLocal<>();

  private SessionHolder() {}

  public static void setSession(final String authToken) {
    CURRENT_SESSION.set(authToken);
  }

  public static String getSession() {
    return CURRENT_SESSION.get();
  }
}
