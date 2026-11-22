package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vendor_performance_evaluations")
@Getter
@Setter
@NoArgsConstructor
public class VendorPerformanceEvaluation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eval_id")
    private Integer evalId;

    @NotNull(message = "Vendor is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @NotNull(message = "Evaluator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score must be at least 0")
    @DecimalMax(value = "5.0", message = "Score must not exceed 5.0")
    @Column(name = "score", nullable = false, precision = 3, scale = 2)
    private BigDecimal score;

    @NotNull(message = "Evaluation date is required")
    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate;

    @Lob
    @Column(name = "comments")
    private String comments;

    public VendorPerformanceEvaluation(Vendor vendor, User evaluator, BigDecimal score, LocalDate evaluationDate) {
        this.vendor = vendor;
        this.evaluator = evaluator;
        this.score = score;
        this.evaluationDate = evaluationDate;
    }
}