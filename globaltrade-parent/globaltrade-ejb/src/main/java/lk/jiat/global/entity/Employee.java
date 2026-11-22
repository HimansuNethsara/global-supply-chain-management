package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @NotBlank(message = "NIC number is required")
    @Size(max = 20, message = "NIC must not exceed 20 characters")
    @Column(name = "nic_number", nullable = false, unique = true, length = 20)
    private String nicNumber;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Job position is required")
    @Size(max = 50, message = "Job position must not exceed 50 characters")
    @Column(name = "job_position", nullable = false, length = 50)
    private String jobPosition;

    @Size(max = 50, message = "Department must not exceed 50 characters")
    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "is_registered", nullable = false)
    private Boolean isRegistered = false;

    public Employee(String nicNumber, String fullName, String jobPosition) {
        this.nicNumber = nicNumber;
        this.fullName = fullName;
        this.jobPosition = jobPosition;
    }
}