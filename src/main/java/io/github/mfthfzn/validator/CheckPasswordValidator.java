package io.github.mfthfzn.validator;

import io.github.mfthfzn.annotation.CheckPassword;
import io.github.mfthfzn.dto.ResetPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckPasswordValidator implements ConstraintValidator<CheckPassword, ResetPasswordRequest> {

  @Override
  public boolean isValid(ResetPasswordRequest resetPasswordRequest, ConstraintValidatorContext constraintValidatorContext) {
    return resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getRetypeNewPassword());
  }
}
