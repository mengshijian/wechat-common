package com.cootf.exception;

public class AppTimeOutException extends RuntimeException {

  private static final long serialVersionUID = 6625043780113914319L;
  private String msg;

  public AppTimeOutException(String message) {
    super(message);
  }

  public AppTimeOutException(String message, Throwable t) {
    super(message, t);
  }
}
