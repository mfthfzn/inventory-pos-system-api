package io.github.mfthfzn.annotation;

import io.github.mfthfzn.validator.CheckPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Documented
@Constraint(
        validatedBy = {CheckPasswordValidator.class}
)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPassword {

  String message() default "{io.github.mfthfzn.annotation.CheckPassword}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
