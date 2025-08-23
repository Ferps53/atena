package com.atena.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.type.TrueFalseConverter;

@Entity
@Table(name = "users")
@SequenceGenerator(
    sequenceName = "user_seq",
    initialValue = 1,
    allocationSize = 1,
    name = "user_seq")
public class User extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @Column(name = "id_user", nullable = false, updatable = false)
  private Integer idUser;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Convert(converter = TrueFalseConverter.class)
  @Column(name = "email_confirmed", length = 1, nullable = false)
  private boolean emailConfirmed;

  public User() {}

  public static User createNewUser(final String name, final String email, final String password) {

    final User newUser = new User();
    newUser.name = name;
    newUser.email = email;
    newUser.password = password;
    newUser.emailConfirmed = false;
    newUser.createdAt = LocalDateTime.now();
    return newUser;
  }

  @Override
  public String toString() {
    return String.format(
        "User {idUser=%s, name=%s, email=%s, password=%s, emailConfirmed=%s, createdAt=%s}",
        idUser, name, email, password, emailConfirmed, createdAt);
  }

  public Integer getIdUser() {
    return idUser;
  }

  public void setIdUser(Integer idUser) {
    this.idUser = idUser;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public boolean isEmailConfirmed() {
    return emailConfirmed;
  }

  public void markEmailAsConfirmed() {
    this.emailConfirmed = true;
  }
}
