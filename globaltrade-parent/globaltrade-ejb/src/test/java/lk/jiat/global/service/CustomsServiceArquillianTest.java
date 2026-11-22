package lk.jiat.global.service;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import lk.jiat.global.entity.*;
import lk.jiat.global.exception.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@DisplayName("Customs Service Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomsServiceArquillianTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "lk.jiat.global")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("org.hibernate.orm:hibernate-core")
                                .withTransitivity().asFile()
                )
                .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml")
                                .resolve("com.mysql:mysql-connector-j")
                                .withTransitivity().asFile()
                );
    }

    @EJB
    private CustomsServiceLocal customsService;

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private VendorServiceLocal vendorService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @EJB
    private CarrierServiceLocal carrierService;

    @EJB
    private UserServiceLocal userService;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    private Warehouse warehouse;
    private Vendor vendor;
    private Carrier carrier;
    private User logisticsCoordinator;
    private User customsAgent;
    private Shipment shipment;

    // Use unique usernames to avoid conflicts
    private static final String LC_USERNAME = "lcuser_" + System.currentTimeMillis();
    private static final String CA_USERNAME = "causer_" + System.currentTimeMillis();

    @BeforeEach
    void setUp() throws Exception {
        // Check if there's an active transaction and rollback if needed
        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        userTransaction.begin();
        try {
            // ===== CLEAN UP IN CORRECT ORDER (child tables first) =====
            em.createQuery("DELETE FROM CustomsDocument").executeUpdate();
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM InventoryItem").executeUpdate();
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            em.createQuery("DELETE FROM Carrier").executeUpdate();
            em.createQuery("DELETE FROM Warehouse").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.flush();

            // ===== CREATE ROLES =====
            Role lcRole = new Role("LOGISTICS_COORDINATOR", "LC Role");
            em.persist(lcRole);

            Role caRole = new Role("CUSTOMS_AGENT", "CA Role");
            em.persist(caRole);
            em.flush();

            // ===== CREATE USERS WITH UNIQUE USERNAMES =====
            logisticsCoordinator = new User(LC_USERNAME, "hash", "lc@test.com", "LC User", lcRole);
            logisticsCoordinator.setStatus("ACTIVE");
            em.persist(logisticsCoordinator);

            customsAgent = new User(CA_USERNAME, "hash", "ca@test.com", "CA User", caRole);
            customsAgent.setStatus("ACTIVE");
            em.persist(customsAgent);
            em.flush();

            // ===== CREATE TEST DATA =====
            warehouse = warehouseService.createWarehouse("Customs WH", "Location", "Sri Lanka", 10000);
            vendor = vendorService.createVendor("Customs Vendor", "customs@vendor.com", "123", "Sri Lanka", "ACTIVE");
            carrier = carrierService.createCarrier("Customs Carrier", "Standard", "info@carrier.com");

            // Create a shipment
            shipment = shipmentService.createShipment(
                    "CUSTOMS-SHIP-001",
                    warehouse.getWarehouseId(),
                    "Customs Destination",
                    vendor.getVendorId(),
                    carrier.getCarrierId(),
                    "PENDING",
                    LocalDateTime.now().plusDays(1),
                    null,
                    logisticsCoordinator.getUserId()
            );

            userTransaction.commit();

        } catch (Exception e) {
            userTransaction.rollback();
            throw e;
        }
    }

    @AfterEach
    void tearDown() throws Exception {

        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        userTransaction.begin();
        try {
            em.createQuery("DELETE FROM CustomsDocument").executeUpdate();
            em.createQuery("DELETE FROM ShipmentStatusHistory").executeUpdate();
            em.createQuery("DELETE FROM ShipmentOrder").executeUpdate();
            em.createQuery("DELETE FROM ShipmentItem").executeUpdate();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createQuery("DELETE FROM InventoryItem").executeUpdate();
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            em.createQuery("DELETE FROM Carrier").executeUpdate();
            em.createQuery("DELETE FROM Warehouse").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            userTransaction.commit();
        } catch (Exception e) {
            userTransaction.rollback();
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    // ============================================================
    // 1. CREATE DOCUMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should create customs document successfully (LC role)")
    void createDocument_ShouldSucceed() throws Exception {
        // Act
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Import Declaration",
                logisticsCoordinator.getUserId()
        );

        // Assert
        assertNotNull(document);
        assertNotNull(document.getDocId());
        assertEquals("Import Declaration", document.getDocumentType());
        assertEquals("PENDING", document.getStatus());
        assertNull(document.getSubmittedDate());
        assertNull(document.getCustomsAgent());
        assertEquals(shipment.getShipmentId(), document.getShipment().getShipmentId());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException for duplicate document type")
    void createDocument_ShouldThrowDuplicateResourceException() throws Exception {
        // Arrange
        customsService.createDocument(
                shipment.getShipmentId(),
                "Bill of Lading",
                logisticsCoordinator.getUserId()
        );

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () ->
                customsService.createDocument(
                        shipment.getShipmentId(),
                        "Bill of Lading",
                        logisticsCoordinator.getUserId()
                )
        );
    }

    @Test
    @DisplayName("Should throw ValidationException when creating with null fields")
    void createDocument_ShouldThrowValidationException_NullFields() {
        assertThrows(ValidationException.class, () ->
                customsService.createDocument(null, null, null)
        );
    }

    // ============================================================
    // 2. SUBMIT DOCUMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should submit document successfully (LC role)")
    void submitDocument_ShouldSucceed() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Export Declaration",
                logisticsCoordinator.getUserId()
        );

        // Act
        CustomsDocument submitted = customsService.submitDocument(
                document.getDocId(),
                logisticsCoordinator.getUserId()
        );

        // Assert
        assertEquals("SUBMITTED", submitted.getStatus());
        assertNotNull(submitted.getSubmittedDate());
    }

    @Test
    @DisplayName("Should throw ValidationException when submitting non-PENDING document")
    void submitDocument_ShouldThrowValidationException_NonPending() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Commercial Invoice",
                logisticsCoordinator.getUserId()
        );

        customsService.submitDocument(document.getDocId(), logisticsCoordinator.getUserId());

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                customsService.submitDocument(document.getDocId(), logisticsCoordinator.getUserId())
        );
    }

    // ============================================================
    // 3. REVIEW DOCUMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should approve document successfully (CA role)")
    void reviewDocument_ShouldApprove() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Certificate of Origin",
                logisticsCoordinator.getUserId()
        );
        customsService.submitDocument(document.getDocId(), logisticsCoordinator.getUserId());

        // Act
        CustomsDocument approved = customsService.reviewDocument(
                document.getDocId(),
                "APPROVED",
                "All documents are in order",
                customsAgent.getUserId()
        );

        // Assert
        assertEquals("APPROVED", approved.getStatus());
        assertNotNull(approved.getApprovedDate());
        assertEquals(customsAgent.getUserId(), approved.getCustomsAgent().getUserId());
    }

    @Test
    @DisplayName("Should reject document successfully (CA role)")
    void reviewDocument_ShouldReject() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Packing List",
                logisticsCoordinator.getUserId()
        );
        customsService.submitDocument(document.getDocId(), logisticsCoordinator.getUserId());

        // Act
        CustomsDocument rejected = customsService.reviewDocument(
                document.getDocId(),
                "REJECTED",
                "Incomplete documentation",
                customsAgent.getUserId()
        );

        // Assert
        assertEquals("REJECTED", rejected.getStatus());
        assertNotNull(rejected.getApprovedDate());
        assertEquals(customsAgent.getUserId(), rejected.getCustomsAgent().getUserId());
    }

    @Test
    @DisplayName("Should throw ValidationException when reviewing non-SUBMITTED document")
    void reviewDocument_ShouldThrowValidationException_NonSubmitted() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Import License",
                logisticsCoordinator.getUserId()
        );

        // Act & Assert - Try to review without submitting
        assertThrows(ValidationException.class, () ->
                customsService.reviewDocument(
                        document.getDocId(),
                        "APPROVED",
                        "Invalid review",
                        customsAgent.getUserId()
                )
        );
    }

    // ============================================================
    // 4. FIND DOCUMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should find document by ID")
    void findDocumentById_ShouldReturnDocument() throws Exception {
        // Arrange
        CustomsDocument created = customsService.createDocument(
                shipment.getShipmentId(),
                "Find Test Document",
                logisticsCoordinator.getUserId()
        );

        // Act
        Optional<CustomsDocument> found = customsService.findDocumentById(created.getDocId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(created.getDocId(), found.get().getDocId());
        assertEquals("Find Test Document", found.get().getDocumentType());
    }

    @Test
    @DisplayName("Should find all documents")
    void findAllDocuments_ShouldReturnList() throws Exception {
        // Arrange
        customsService.createDocument(shipment.getShipmentId(), "Document 1", logisticsCoordinator.getUserId());
        customsService.createDocument(shipment.getShipmentId(), "Document 2", logisticsCoordinator.getUserId());

        // Act
        List<CustomsDocument> documents = customsService.findAllDocuments();

        // Assert
        assertTrue(documents.size() >= 2);
    }

    @Test
    @DisplayName("Should find documents by shipment")
    void findDocumentsByShipment_ShouldReturnShipmentDocuments() throws Exception {
        // Arrange
        customsService.createDocument(shipment.getShipmentId(), "Shipment Doc 1", logisticsCoordinator.getUserId());
        customsService.createDocument(shipment.getShipmentId(), "Shipment Doc 2", logisticsCoordinator.getUserId());

        // Act
        List<CustomsDocument> documents = customsService.findDocumentsByShipment(shipment.getShipmentId());

        // Assert
        assertEquals(2, documents.size());
    }

    // ============================================================
    // 5. DELETE DOCUMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should delete PENDING document")
    void deleteDocument_ShouldDeletePending() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Delete Test",
                logisticsCoordinator.getUserId()
        );

        // Act
        customsService.deleteDocument(document.getDocId());

        // Assert - Document should not exist
        Optional<CustomsDocument> found = customsService.findDocumentById(document.getDocId());
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting APPROVED document")
    void deleteDocument_ShouldThrowException_WhenApproved() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "Approved Delete Test",
                logisticsCoordinator.getUserId()
        );
        customsService.submitDocument(document.getDocId(), logisticsCoordinator.getUserId());
        customsService.reviewDocument(
                document.getDocId(),
                "APPROVED",
                "Approved",
                customsAgent.getUserId()
        );

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                customsService.deleteDocument(document.getDocId())
        );
    }

    // ============================================================
    // 6. STATUS TRANSITION VALIDATION TESTS
    // ============================================================

    @Test
    @DisplayName("Should validate valid status transitions")
    void isValidStatusTransition_ShouldReturnTrueForValidTransitions() {
        assertTrue(customsService.isValidStatusTransition("PENDING", "SUBMITTED"));
        assertTrue(customsService.isValidStatusTransition("SUBMITTED", "APPROVED"));
        assertTrue(customsService.isValidStatusTransition("SUBMITTED", "REJECTED"));
    }

    @Test
    @DisplayName("Should reject invalid status transitions")
    void isValidStatusTransition_ShouldReturnFalseForInvalidTransitions() {
        assertFalse(customsService.isValidStatusTransition("PENDING", "APPROVED"));
        assertFalse(customsService.isValidStatusTransition("PENDING", "REJECTED"));
        assertFalse(customsService.isValidStatusTransition("APPROVED", "SUBMITTED"));
        assertFalse(customsService.isValidStatusTransition("REJECTED", "SUBMITTED"));
        assertFalse(customsService.isValidStatusTransition(null, "PENDING"));
        assertFalse(customsService.isValidStatusTransition("PENDING", null));
    }

    // ============================================================
    // 7. ROLE-BASED ACCESS TESTS
    // ============================================================

    @Test
    @DisplayName("Logistics Coordinator should create and submit documents")
    void lc_ShouldCreateAndSubmitDocuments() throws Exception {
        // Act - Create
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "LC Test Doc",
                logisticsCoordinator.getUserId()
        );

        // Assert - Created
        assertEquals("PENDING", document.getStatus());

        // Act - Submit
        CustomsDocument submitted = customsService.submitDocument(
                document.getDocId(),
                logisticsCoordinator.getUserId()
        );

        // Assert - Submitted
        assertEquals("SUBMITTED", submitted.getStatus());
        assertNotNull(submitted.getSubmittedDate());
    }

    @Test
    @DisplayName("Customs Agent should review and approve documents")
    void ca_ShouldReviewAndApproveDocuments() throws Exception {
        // Arrange - LC creates and submits
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "CA Review Test",
                logisticsCoordinator.getUserId()
        );
        customsService.submitDocument(document.getDocId(), logisticsCoordinator.getUserId());

        // Act - CA approves
        CustomsDocument approved = customsService.reviewDocument(
                document.getDocId(),
                "APPROVED",
                "Approved by CA",
                customsAgent.getUserId()
        );

        // Assert
        assertEquals("APPROVED", approved.getStatus());
        assertEquals(customsAgent.getUserId(), approved.getCustomsAgent().getUserId());
    }

    @Test
    @DisplayName("Customs Agent should not create documents")
    void ca_ShouldNotCreateDocuments() {
        assertThrows(ValidationException.class, () ->
                customsService.createDocument(
                        shipment.getShipmentId(),
                        "CA Create Test",
                        customsAgent.getUserId()
                )
        );
    }

    @Test
    @DisplayName("Logistics Coordinator should not review documents")
    void lc_ShouldNotReviewDocuments() throws Exception {
        // Arrange
        CustomsDocument document = customsService.createDocument(
                shipment.getShipmentId(),
                "LC Review Test",
                logisticsCoordinator.getUserId()
        );
        customsService.submitDocument(document.getDocId(), logisticsCoordinator.getUserId());

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                customsService.reviewDocument(
                        document.getDocId(),
                        "APPROVED",
                        "LC trying to approve",
                        logisticsCoordinator.getUserId()
                )
        );
    }
}