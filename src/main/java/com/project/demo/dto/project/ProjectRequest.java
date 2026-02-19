package com.project.demo.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
       @NotBlank String name
) {
}
