package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
public class InventoryItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @NotBlank(message = "Item name is required")
    @Size(max = 150, message = "Item name must not exceed 150 characters")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotNull(message = "Warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be at least 0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level must be at least 0")
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 10;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must be at least 0")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShipmentItem> shipmentItems = new ArrayList<>();

    public InventoryItem(String sku, String name, Warehouse warehouse, Integer quantity, Integer reorderLevel, BigDecimal unitPrice) {
        this.sku = sku;
        this.name = name;
        this.warehouse = warehouse;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.unitPrice = unitPrice;
    }
}