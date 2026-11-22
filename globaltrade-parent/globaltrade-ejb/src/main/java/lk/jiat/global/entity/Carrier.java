package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carriers")
@Getter
@Setter
@NoArgsConstructor
public class Carrier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrier_id")
    private Integer carrierId;

    @NotBlank(message = "Carrier name is required")
    @Size(max = 100, message = "Carrier name must not exceed 100 characters")
    @Column(name = "carrier_name", nullable = false, length = 100)
    private String carrierName;

    @Size(max = 50, message = "Service type must not exceed 50 characters")
    @Column(name = "service_type", length = 50)
    private String serviceType;

    @Size(max = 150, message = "Contact info must not exceed 150 characters")
    @Column(name = "contact_info", length = 150)
    private String contactInfo;

    @OneToMany(mappedBy = "carrier", fetch = FetchType.LAZY)
    private List<Shipment> shipments = new ArrayList<>();

    public Carrier(String carrierName, String serviceType, String contactInfo) {
        this.carrierName = carrierName;
        this.serviceType = serviceType;
        this.contactInfo = contactInfo;
    }
}