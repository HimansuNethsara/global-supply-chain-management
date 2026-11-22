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
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Action is required")
    @Size(max = 100, message = "Action must not exceed 100 characters")
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @NotBlank(message = "Entity type is required")
    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    @Lob
    @Column(name = "details")
    private String details;

    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt = LocalDateTime.now();

    public AuditLog(User user, String action, String entityType, Integer entityId, String details) {
        this.user = user;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }
}