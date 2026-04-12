package io.github.mfthfzn.exception;

public class TooManyRequestedException extends RuntimeException {
  public TooManyRequestedException(String message) {
    super(message);
  }
}
