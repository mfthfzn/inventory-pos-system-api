package io.github.mfthfzn.service;

import io.github.mfthfzn.util.RedisUtil;
import io.lettuce.core.api.sync.RedisCommands;

public class RateLimiterServiceImpl implements RateLimiterService {

  private final RedisCommands<String, String> commands;

  private final int LIMIT_DURATION = 60;
  public RateLimiterServiceImpl(RedisCommands<String, String> commands) {
    this.commands = commands;
  }

  @Override
  public boolean isAllowed(String ipAddress) {

    String key = "rl:" + ipAddress;
    long LIMIT_REQUEST = 10L;

    long increment = commands.incr(key);

    if (increment == 1L) {
      commands.expire(key, LIMIT_DURATION);
    }

    return increment <= LIMIT_REQUEST;
  }

  @Override
  public boolean isAllowedRegistered(String email) {

    String key = "rl:" + email;
    long LIMIT_REQUEST = 60L;

    long increment = commands.incr(key);

    if (increment == 1L) {
      commands.expire(key, LIMIT_DURATION);
    }

    return increment <= LIMIT_REQUEST;
  }

  @Override
  public boolean isAllowedForgotPassword(String email) {
    String key = "rl:" + email + "forgot_password";
    long LIMIT_REQUEST = 3L;

    long increment = commands.incr(key);

    if (increment == 1L) {
      commands.expire(key, LIMIT_DURATION);
    }

    return increment <= LIMIT_REQUEST;
  }
}
