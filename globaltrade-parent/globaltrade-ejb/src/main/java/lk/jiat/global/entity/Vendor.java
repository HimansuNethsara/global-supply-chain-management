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
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
public class Vendor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Integer vendorId;

    @NotBlank(message = "Vendor name is required")
    @Size(max = 150, message = "Vendor name must not exceed 150 characters")
    @Column(name = "vendor_name", nullable = false, length = 150)
    private String vendorName;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Contact email must not exceed 100 characters")
    @Column(name = "contact_email", nullable = false, unique = true, length = 100)
    private String contactEmail;

    @Size(max = 20, message = "Contact phone must not exceed 20 characters")
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @NotBlank(message = "Country is required")
    @Size(max = 60, message = "Country must not exceed 60 characters")
    @Column(name = "country", nullable = false, length = 60)
    private String country;

    @DecimalMin(value = "0.0", message = "Performance rating must be at least 0")
    @DecimalMax(value = "5.0", message = "Performance rating must not exceed 5.0")
    @Column(name = "performance_rating", precision = 3, scale = 2)
    private BigDecimal performanceRating;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private List<Shipment> shipments = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VendorPerformanceEvaluation> evaluations = new ArrayList<>();

    public Vendor(String vendorName, String contactEmail, String country) {
        this.vendorName = vendorName;
        this.contactEmail = contactEmail;
        this.country = country;
    }
}