package com.atena.user;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record NewUserCreatedDTO(Long id, String username, String email) {}
