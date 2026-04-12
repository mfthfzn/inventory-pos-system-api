package io.github.mfthfzn.controller;

import io.github.mfthfzn.dto.*;
import io.github.mfthfzn.exception.AuthenticateException;
import io.github.mfthfzn.exception.TooManyRequestedException;
import io.github.mfthfzn.repository.RefreshTokenRepositoryImpl;
import io.github.mfthfzn.repository.UserRepository;
import io.github.mfthfzn.repository.UserRepositoryImpl;
import io.github.mfthfzn.service.*;
import io.github.mfthfzn.util.IpUtil;
import io.github.mfthfzn.util.JpaUtil;
import io.github.mfthfzn.util.RedisUtil;
import io.github.mfthfzn.util.ValidatorUtil;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@WebServlet(urlPatterns = "/api/auth/login")
public class LoginController extends BaseController {

  private final UserRepository userRepository =
          new UserRepositoryImpl(JpaUtil.getEntityManagerFactory());

  private final TokenService tokenService =
          new TokenServiceImpl(
                  new RefreshTokenRepositoryImpl(JpaUtil.getEntityManagerFactory())
          );

  private final AuthService authService =
          new AuthServiceImpl(
                  userRepository
          );

  private final RateLimiterService rateLimiterService =
          new RateLimiterServiceImpl(
                  RedisUtil.getConnection().sync()
          );

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {

      if (!rateLimiterService.isAllowed(IpUtil.getClientIpAddress(req))) {
        throw new TooManyRequestedException("Too many request!");
      }

      LoginRequest loginRequest = new LoginRequest(req.getParameter("email"), req.getParameter("password"));
      Set<ConstraintViolation<Object>> constraintViolations = ValidatorUtil.validate(loginRequest);

      if (!constraintViolations.isEmpty()) {
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
          sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Data request invalid", Map.of(
                  "message", constraintViolation.getMessage()
          ));
          break;
        }
        return;
      }

      LoginResponse loginResponse = authService.authenticate(loginRequest);

      if (loginResponse.getUser().getRefreshToken() != null) {
        tokenService.removeRefreshToken(loginResponse.getUser().getEmail());
      }

      loginResponse.setAccessToken(tokenService.generateAccessToken(loginResponse));
      loginResponse.setRefreshToken(tokenService.generateRefreshToken(loginResponse));
      tokenService.saveRefreshToken(loginResponse);

      addCookie(resp, "access_token", loginResponse.getAccessToken(), 60 * 60);
      addCookie(resp, "refresh_token", loginResponse.getRefreshToken(), 60 * 60 * 24 * 7);
      addCookie(resp, "email", loginRequest.getEmail(), 60 * 60 * 24 * 7);

      UserResponse userResponse = new UserResponse();
      userResponse.setRole(loginResponse.getUser().getRole().toString());
      sendSuccess(resp, HttpServletResponse.SC_OK, "Login success", userResponse);

    } catch (TooManyRequestedException tooManyRequestedException) {
      sendError(resp, 429, "Slow down!", Map.of(
              "message", tooManyRequestedException.getMessage()
      ));
    } catch (AuthenticateException authenticateException) {
      sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Login failed", Map.of(
              "message", authenticateException.getMessage()
      ));
    } catch (PersistenceException persistenceException) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login failed", Map.of(
              "message", "An error occurred on the database server."
      ));
    }
  }

}
