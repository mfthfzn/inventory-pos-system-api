package io.github.mfthfzn.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.github.mfthfzn.dto.UserResponse;
import io.github.mfthfzn.exception.TooManyRequestedException;
import io.github.mfthfzn.repository.RefreshTokenRepositoryImpl;
import io.github.mfthfzn.service.RateLimiterService;
import io.github.mfthfzn.service.RateLimiterServiceImpl;
import io.github.mfthfzn.service.TokenService;
import io.github.mfthfzn.service.TokenServiceImpl;
import io.github.mfthfzn.util.IpUtil;
import io.github.mfthfzn.util.JpaUtil;
import io.github.mfthfzn.util.RedisUtil;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
@WebServlet(urlPatterns = "/api/auth/logout")
public class LogoutController extends BaseController {

  private final TokenService tokenService =
          new TokenServiceImpl(
                  new RefreshTokenRepositoryImpl(JpaUtil.getEntityManagerFactory())
          );

  private final RateLimiterService rateLimiterService =
          new RateLimiterServiceImpl(
                  RedisUtil.getConnection().sync()
          );

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {

      if (!rateLimiterService.isAllowed(IpUtil.getClientIpAddress(req))) {
        throw new TooManyRequestedException("Too many requested!");
      }

      String refreshToken = getCookieValue(req, "refresh_token");
      if (refreshToken != null && !refreshToken.isBlank()) {
        UserResponse userFromToken = tokenService.getUserFromToken(refreshToken);
        tokenService.removeRefreshToken(userFromToken.getEmail());
      }

      removeCookie(resp, "access_token");
      removeCookie(resp, "refresh_token");

      sendSuccess(resp, HttpServletResponse.SC_OK, "Success to logout", Map.of(
              "message", "Success to delete jwt token"
      ));
    } catch (TooManyRequestedException tooManyRequestedException) {
      sendError(resp, 429, "Slow down!", Map.of(
              "message", tooManyRequestedException.getMessage()
      ));
    } catch (JWTVerificationException exception) {
      removeCookie(resp, "access_token");
      removeCookie(resp, "refresh_token");
      sendSuccess(resp, HttpServletResponse.SC_OK, "Session already cleared", null);

    } catch (PersistenceException exception) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Logout failed", Map.of(
              "message", "Server database error"
      ));
    }

  }

}
