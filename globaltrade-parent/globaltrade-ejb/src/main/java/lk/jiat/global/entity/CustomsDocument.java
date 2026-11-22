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
@Table(name = "customs_documents")
@Getter
@Setter
@NoArgsConstructor
public class CustomsDocument implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Integer docId;

    @NotNull(message = "Shipment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @NotBlank(message = "Document type is required")
    @Size(max = 80, message = "Document type must not exceed 80 characters")
    @Column(name = "document_type", nullable = false, length = 80)
    private String documentType;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customs_agent_id")
    private User customsAgent;

    public CustomsDocument(Shipment shipment, String documentType) {
        this.shipment = shipment;
        this.documentType = documentType;
    }
}