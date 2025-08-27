package com.atena.auth;

import com.atena.auth.dto.TokenDTO;
import com.atena.user.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "auth")
public class Auth extends PanacheEntityBase {

  @Id
  @Column(name = "access_token", nullable = false, unique = true, updatable = false)
  private String accessToken;

  @Column(name = "refresh_token", nullable = false, unique = true, updatable = false)
  private String refreshToken;

  @Column(name = "is_valid", nullable = false)
  private boolean isValid;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "expire_time", nullable = false, updatable = false)
  private LocalDateTime expireTime;

  @Column(name = "refresh_expire_time", nullable = false, updatable = false)
  private LocalDateTime refreshExpireTime;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(referencedColumnName = "id_user", nullable = false)
  private User user;

  protected Auth() {}

  public Auth(TokenDTO tokenDTO, User user) {

    final LocalDateTime now = LocalDateTime.now();

    this.accessToken = tokenDTO.accessToken();
    this.refreshToken = tokenDTO.refreshToken();
    this.createdAt = now;
    this.expireTime = now.plusSeconds(tokenDTO.expiresIn());
    this.refreshExpireTime = now.plusSeconds(tokenDTO.refreshExpiresIn());
    this.isValid = true;
    this.user = user;
  }

  public void invalidate() {
    this.isValid = false;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public boolean isValid() {
    return isValid;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getExpireTime() {
    return expireTime;
  }

  public LocalDateTime getRefreshExpireTime() {
    return refreshExpireTime;
  }

  public User getUser() {
    return user;
  }
}
