package io.github.mfthfzn.service;

public interface RateLimiterService {

  boolean isAllowed(String ipAddress);

  boolean isAllowedRegistered(String email);

  boolean isAllowedForgotPassword(String email);

}
