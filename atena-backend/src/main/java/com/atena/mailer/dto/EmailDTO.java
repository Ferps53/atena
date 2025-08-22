package com.atena.mailer.dto;

import com.atena.mailer.enums.EmailImages;
import com.atena.mailer.enums.EmailModels;
import java.util.List;

public record EmailDTO(
    String address, EmailModels model, List<EmailContentsDTO> contents, List<EmailImages> images) {}
