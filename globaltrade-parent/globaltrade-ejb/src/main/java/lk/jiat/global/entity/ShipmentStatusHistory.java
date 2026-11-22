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
@Table(name = "shipment_status_history")
@Getter
@Setter
@NoArgsConstructor
public class ShipmentStatusHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @NotNull(message = "Shipment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "old_status")
    private String oldStatus;

    @NotBlank(message = "New status is required")
    @Column(name = "new_status", nullable = false)
    private String newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    @Column(name = "remarks", length = 255)
    private String remarks;

    public ShipmentStatusHistory(Shipment shipment, String newStatus, User changedBy, String remarks) {
        this.shipment = shipment;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.remarks = remarks;
    }
}