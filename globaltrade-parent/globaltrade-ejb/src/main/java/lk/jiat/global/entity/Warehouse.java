package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class Warehouse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 100, message = "Warehouse name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 150, message = "Location must not exceed 150 characters")
    @Column(name = "location", nullable = false, length = 150)
    private String location;

    @NotBlank(message = "Country is required")
    @Size(max = 60, message = "Country must not exceed 60 characters")
    @Column(name = "country", nullable = false, length = 60)
    private String country;

    @NotNull(message = "Capacity is required")
    @Min(value = 0, message = "Capacity must be at least 0")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    @OneToMany(mappedBy = "originWarehouse", fetch = FetchType.LAZY)
    private List<Shipment> shipments = new ArrayList<>();

    public Warehouse(String name, String location, String country, Integer capacity) {
        this.name = name;
        this.location = location;
        this.country = country;
        this.capacity = capacity;
    }
}