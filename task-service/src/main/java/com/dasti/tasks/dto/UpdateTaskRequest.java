package com.dasti.tasks.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskRequest(@NotBlank String title, String details) {
}
