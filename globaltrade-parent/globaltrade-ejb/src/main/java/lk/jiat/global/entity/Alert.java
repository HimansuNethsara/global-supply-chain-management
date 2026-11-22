package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Integer alertId;

    @NotNull(message = "Alert type is required")
    @Column(name = "alert_type", nullable = false)
    private String alertType;

    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Integer entityId;

    @NotBlank(message = "Severity is required")
    @Column(name = "severity", nullable = false)
    private String severity = "MEDIUM";

    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message must not exceed 500 characters")
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "resolved", nullable = false)
    private Boolean resolved = false;

    public Alert(String alertType, String severity, String message) {
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
    }

    public Alert(String alertType, String entityType, Integer entityId, String severity, String message) {
        this.alertType = alertType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.severity = severity;
        this.message = message;
    }
}