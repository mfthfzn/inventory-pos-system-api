package io.github.mfthfzn.repository;

import io.github.mfthfzn.entity.ResetPasswordToken;

import java.util.Optional;

public interface ResetPasswordTokenRepository {

  void insert(ResetPasswordToken resetPasswordToken);

  Optional<ResetPasswordToken> findByToken(String token);

  void removeByEmail(String email);

  void cleanUpTokenExpired();
}
