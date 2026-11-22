package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_plans")
@Getter
@Setter
@NoArgsConstructor
public class RoutePlan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Integer routeId;

    @NotNull(message = "Shipment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @NotBlank(message = "Planned route is required")
    @Lob
    @Column(name = "planned_route", nullable = false)
    private String plannedRoute;

    @Lob
    @Column(name = "optimized_route")
    private String optimizedRoute;

    @PositiveOrZero(message = "Distance must be zero or positive")
    @Column(name = "distance_km", precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @PositiveOrZero(message = "Estimated time must be zero or positive")
    @Column(name = "estimated_time_hours", precision = 6, scale = 2)
    private BigDecimal estimatedTimeHours;

    @Column(name = "calculated_at", nullable = false, updatable = false)
    private LocalDateTime calculatedAt = LocalDateTime.now();

    public RoutePlan(Shipment shipment, String plannedRoute) {
        this.shipment = shipment;
        this.plannedRoute = plannedRoute;
    }
}