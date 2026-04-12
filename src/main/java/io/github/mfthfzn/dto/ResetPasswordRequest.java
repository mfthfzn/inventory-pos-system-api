package io.github.mfthfzn.dto;

import io.github.mfthfzn.annotation.CheckPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@CheckPassword(message = "New password and retype new password must be same")
public class ResetPasswordRequest {

  @NotBlank(message = "Email cannot blank")
  @Email(message = "Email must be valid valid")
  private String email;

  @NotBlank(message = "New password cannot blank")
  private String newPassword;

  @NotBlank(message = "Retype New password cannot blank")
  private String retypeNewPassword;

}
