package io.github.mfthfzn.controller;

import io.github.mfthfzn.dto.ResetPasswordRequest;
import io.github.mfthfzn.enums.InternalErrorCode;
import io.github.mfthfzn.exception.ResetPasswordTokenExpiredException;
import io.github.mfthfzn.exception.ResetPasswordTokenNotFoundException;
import io.github.mfthfzn.exception.TokenRequiredException;
import io.github.mfthfzn.repository.ResetPasswordTokenRepository;
import io.github.mfthfzn.repository.ResetPasswordTokenRepositoryImpl;
import io.github.mfthfzn.repository.UserRepository;
import io.github.mfthfzn.repository.UserRepositoryImpl;
import io.github.mfthfzn.service.AuthService;
import io.github.mfthfzn.service.AuthServiceImpl;
import io.github.mfthfzn.util.JpaUtil;
import io.github.mfthfzn.util.ValidatorUtil;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@WebServlet(urlPatterns = "/api/auth/reset-password")
public class ResetPasswordController extends BaseController{

  private final ResetPasswordTokenRepository resetPasswordTokenRepository =
          new ResetPasswordTokenRepositoryImpl(JpaUtil.getEntityManagerFactory());

  private final UserRepository userRepository =
          new UserRepositoryImpl(JpaUtil.getEntityManagerFactory());

  private final AuthService authService =
          new AuthServiceImpl(userRepository, resetPasswordTokenRepository);

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {

      String token = req.getParameter("token");

      if (token == null || token.isBlank()) throw new TokenRequiredException("Token required");

      String email = authService.validateResetPasswordToken(token);

      ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(
              email, req.getParameter("new-password"), req.getParameter("retype-new-password")
      );
      Set<ConstraintViolation<Object>> constraintViolations = ValidatorUtil.validate(resetPasswordRequest);

      if (!constraintViolations.isEmpty()) {
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
          sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Data request invalid", Map.of(
                  "message", constraintViolation.getMessage()
          ));
          break;
        }
        return;
      }

      authService.resetPassword(resetPasswordRequest);
      sendSuccess(resp, HttpServletResponse.SC_OK, "Success reset password", Map.of(
              "message", "Your password has been reset"
      ));
    } catch (TokenRequiredException tokenRequiredException) {
      sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Failed to reset password", Map.of(
              "message", tokenRequiredException.getMessage(),
              "internal_error_code", InternalErrorCode.TOKEN_MISSING
      ));
    } catch (ResetPasswordTokenNotFoundException resetPasswordTokenNotFoundException) {
      sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Failed to reset password", Map.of(
              "message", resetPasswordTokenNotFoundException.getMessage(),
              "internal_error_code", InternalErrorCode.TOKEN_INVALID
      ));
    } catch (ResetPasswordTokenExpiredException resetPasswordTokenExpiredException) {

      sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Failed to reset password", Map.of(
              "message", resetPasswordTokenExpiredException.getMessage(),
              "internal_error_code", InternalErrorCode.RESET_PASSWORD_EXPIRED
      ));
    } catch (PersistenceException exception) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reset password failed", Map.of(
              "message", "Server database error"
      ));
    } catch (Exception exception) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reset password failed", Map.of(
              "message", "Server error"
      ));
      log.error(exception.getMessage());
    }
  }
}