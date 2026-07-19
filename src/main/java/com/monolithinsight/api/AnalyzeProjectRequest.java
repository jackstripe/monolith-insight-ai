package com.monolithinsight.api;

import jakarta.validation.constraints.NotBlank;

public record AnalyzeProjectRequest(@NotBlank String projectPath) {
}
