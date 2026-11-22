package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "exception_logs")
@Getter
@Setter
@NoArgsConstructor
public class ExceptionLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exception_id")
    private Long exceptionId;

    @NotBlank(message = "Exception type is required")
    @Size(max = 100, message = "Exception type must not exceed 100 characters")
    @Column(name = "exception_type", nullable = false, length = 100)
    private String exceptionType;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Lob
    @Column(name = "stack_trace", columnDefinition = "LONGTEXT")
    private String stackTrace;

    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Integer entityId;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt = LocalDateTime.now();

    @Column(name = "resolved", nullable = false)
    private Boolean resolved = false;

    public ExceptionLog(String exceptionType, String message, String stackTrace) {
        this.exceptionType = exceptionType;
        this.message = message;
        this.stackTrace = stackTrace;
    }
}