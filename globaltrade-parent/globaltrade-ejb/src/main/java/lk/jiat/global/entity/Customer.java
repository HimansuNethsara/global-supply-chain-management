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
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @NotNull(message = "User is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Size(max = 150, message = "Company name must not exceed 150 characters")
    @Column(name = "company_name", length = 150)
    private String companyName;

    @Size(max = 20, message = "Contact phone must not exceed 20 characters")
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @NotBlank(message = "Country is required")
    @Size(max = 60, message = "Country must not exceed 60 characters")
    @Column(name = "country", nullable = false, length = 60)
    private String country;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Customer(User user, String country) {
        this.user = user;
        this.country = country;
    }
}