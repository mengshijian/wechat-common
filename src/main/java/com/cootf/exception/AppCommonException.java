package com.cootf.exception;

public class AppCommonException extends RuntimeException {

  private static final long serialVersionUID = 4832657323166387941L;
  private String msg;

  public AppCommonException(String message) {
    super(message);
  }

  public AppCommonException(String message, Throwable t) {
    super(message, t);
  }
}
