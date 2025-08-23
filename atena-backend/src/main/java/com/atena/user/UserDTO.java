package com.atena.user;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserDTO(
    Integer idUser, String name, String email, String password, boolean emailConfirmed) {}
