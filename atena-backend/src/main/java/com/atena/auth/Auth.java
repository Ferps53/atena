package com.atena.auth;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth")
public class Auth extends PanacheEntityBase {

  @Id private Integer idAuth;
}
