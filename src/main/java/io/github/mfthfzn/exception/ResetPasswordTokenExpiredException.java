package io.github.mfthfzn.exception;

public class ResetPasswordTokenExpiredException extends RuntimeException {
  public ResetPasswordTokenExpiredException(String message) {
    super(message);
  }
}
