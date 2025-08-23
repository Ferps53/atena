package com.atena.user;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record NewUserCreatedDTO(Integer idUser, String username, String email) {}
