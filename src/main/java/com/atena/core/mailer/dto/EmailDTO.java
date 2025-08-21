package com.atena.core.mailer.dto;

import com.atena.core.mailer.enums.EmailImages;
import com.atena.core.mailer.enums.EmailModels;
import java.util.List;

public record EmailDTO(
    String address, EmailModels model, List<EmailContentsDTO> contents, List<EmailImages> images) {}
