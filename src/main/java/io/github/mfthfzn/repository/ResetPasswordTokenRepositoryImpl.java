package io.github.mfthfzn.repository;

import io.github.mfthfzn.entity.RefreshToken;
import io.github.mfthfzn.entity.ResetPasswordToken;
import io.github.mfthfzn.entity.User;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ResetPasswordTokenRepositoryImpl implements ResetPasswordTokenRepository {

  private final EntityManagerFactory entityManagerFactory;

  public ResetPasswordTokenRepositoryImpl(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public void insert(ResetPasswordToken resetPasswordToken) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    try {
      transaction.begin();

      User userReference = entityManager.getReference(User.class, resetPasswordToken.getUser().getEmail());
      resetPasswordToken.setUser(userReference);

      entityManager.persist(resetPasswordToken);

      transaction.commit();
      log.info("berhasil insert");
    } catch (Exception exception) {
      if (transaction.isActive()) transaction.rollback();
      log.error(exception.getMessage());
      throw new PersistenceException(exception);
    } finally {
      entityManager.close();
    }
  }

  @Override
  public Optional<ResetPasswordToken> findByToken(String token) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    try {

      transaction.begin();
      TypedQuery<ResetPasswordToken> query = entityManager
              .createQuery("SELECT t FROM ResetPasswordToken t WHERE t.token = :token", ResetPasswordToken.class)
              .setParameter("token", token);
      List<ResetPasswordToken> resultList = query.getResultList();
      transaction.commit();

      return resultList.stream().findFirst();
    } catch (Exception exception) {
      if (transaction.isActive()) transaction.rollback();
      log.error(exception.getMessage());
      throw new PersistenceException(exception);
    } finally {
      entityManager.close();
    }
  }

  @Override
  public void removeByEmail(String email) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    try {

      transaction.begin();
      entityManager.createQuery("DELETE FROM ResetPasswordToken t WHERE t.email = :email")
              .setParameter("email", email)
              .executeUpdate();
      transaction.commit();

    } catch (Exception exception) {
      if (transaction.isActive()) transaction.rollback();
      log.error(exception.getMessage());
      throw new PersistenceException(exception);
    } finally {
      entityManager.close();
    }
  }

  @Override
  public void cleanUpTokenExpired() {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    try {

      LocalDateTime cutOffTime = LocalDateTime.now().minusHours(12);

      transaction.begin();
      entityManager.createQuery("DELETE FROM ResetPasswordToken t WHERE t.expiredAt < :cutOffTime")
              .setParameter("cutOffTime", cutOffTime)
              .executeUpdate();
      transaction.commit();

    } catch (Exception exception) {
      if (transaction.isActive()) transaction.rollback();
      log.error(exception.getMessage());
      throw new PersistenceException(exception);
    } finally {
      entityManager.close();
    }
  }

}
