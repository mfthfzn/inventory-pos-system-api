package io.github.mfthfzn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "refresh_tokens")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

  @Id
  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String token;

  @OneToOne
  @MapsId
  @JoinColumn(name = "email", referencedColumnName = "email")
  private User user;

}
