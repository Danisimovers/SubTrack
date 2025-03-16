package ru.project.subtrack.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String status;          // например, "error"
    private String message;         // краткое описание ошибки
    private Map<String, String> errors; // детальные ошибки по полям (если есть)
}
