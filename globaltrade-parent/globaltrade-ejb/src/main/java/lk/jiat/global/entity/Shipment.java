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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Integer shipmentId;

    @NotBlank(message = "Tracking number is required")
    @Size(max = 50, message = "Tracking number must not exceed 50 characters")
    @Column(name = "tracking_number", nullable = false, unique = true, length = 50)
    private String trackingNumber;

    @NotNull(message = "Origin warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_warehouse_id", nullable = false)
    private Warehouse originWarehouse;

    @NotBlank(message = "Destination is required")
    @Size(max = 150, message = "Destination must not exceed 150 characters")
    @Column(name = "destination", nullable = false, length = 150)
    private String destination;

    @NotNull(message = "Vendor is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @NotNull(message = "Carrier is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @NotNull(message = "Scheduled date is required")
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @NotNull(message = "Created by is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShipmentItem> shipmentItems = new ArrayList<>();

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CustomsDocument> customsDocuments = new ArrayList<>();

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoutePlan> routePlans = new ArrayList<>();

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShipmentStatusHistory> statusHistories = new ArrayList<>();

    public Shipment(String trackingNumber, Warehouse originWarehouse, String destination,
                    Vendor vendor, User createdBy, LocalDateTime scheduledDate) {
        this.trackingNumber = trackingNumber;
        this.originWarehouse = originWarehouse;
        this.destination = destination;
        this.vendor = vendor;
        this.createdBy = createdBy;
        this.scheduledDate = scheduledDate;
    }
}