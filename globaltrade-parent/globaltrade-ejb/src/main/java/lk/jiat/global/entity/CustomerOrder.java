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
@Table(name = "customer_orders")
@Getter
@Setter
@NoArgsConstructor
public class CustomerOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank(message = "Destination is required")
    @Size(max = 150, message = "Destination must not exceed 150 characters")
    @Column(name = "destination", nullable = false, length = 150)
    private String destination;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "requested_date", nullable = false)
    private LocalDateTime requestedDate = LocalDateTime.now();

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShipmentOrder> shipmentOrders = new ArrayList<>();

    @Transient
    private int itemCount;

    @Transient
    private String customerName;

    public CustomerOrder(Customer customer, String destination) {
        this.customer = customer;
        this.destination = destination;
    }
}