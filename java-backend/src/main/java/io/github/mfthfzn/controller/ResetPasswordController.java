package io.github.mfthfzn.controller;

import io.github.mfthfzn.dto.ResetPasswordResponse;
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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/api/auth/reset-password")
public class ResetPasswordController extends BaseController{

  private final ResetPasswordTokenRepository resetPasswordTokenRepository =
          new ResetPasswordTokenRepositoryImpl(JpaUtil.getEntityManagerFactory());

  private final UserRepository userRepository =
          new UserRepositoryImpl(JpaUtil.getEntityManagerFactory());

  private final AuthService authService =
          new AuthServiceImpl(userRepository, resetPasswordTokenRepository);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
      String token = req.getParameter("token");

      if (token == null || token.isBlank()) throw new TokenRequiredException("Reset password token required");

      ResetPasswordResponse resetPasswordResponse = authService.validateResetPasswordToken(token);

      sendSuccess(resp, HttpServletResponse.SC_OK, "Success to send email",
              Map.of(
                      "message", "Success send email if your email registered"
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
              "internal_error_code", InternalErrorCode.TOKEN_INVALID
      ));
    }

  }
}