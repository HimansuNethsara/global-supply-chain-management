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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@DisplayName("Vendor Service Integration Tests")
class VendorServiceArquillianTest {

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
    private VendorServiceLocal vendorService;

    @EJB
    private VendorPerformanceEvaluationServiceLocal vendorEvalService;

    @EJB
    private UserServiceLocal userService;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    private User evaluator;
    private static final String EVALUATOR_USERNAME = "evaluser_" + System.currentTimeMillis();

    @BeforeEach
    void setUp() throws Exception {
        if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        userTransaction.begin();
        try {
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            em.flush();

            Role role = new Role("LOGISTICS_COORDINATOR", "LC Role");
            em.persist(role);
            em.flush();

            evaluator = new User(EVALUATOR_USERNAME, "hash", "eval@test.com", "Evaluator", role);
            evaluator.setStatus("ACTIVE");
            em.persist(evaluator);
            em.flush();

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
            em.createQuery("DELETE FROM VendorPerformanceEvaluation").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.createQuery("DELETE FROM Vendor").executeUpdate();
            userTransaction.commit();
        } catch (Exception e) {
            userTransaction.rollback();
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    // ============================================================
    // 1. CREATE VENDOR TESTS
    // ============================================================

    @Test
    @DisplayName("Should create vendor successfully")
    void createVendor_ShouldSucceed() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Test Vendor",
                "vendor@test.com",
                "1234567890",
                "Sri Lanka",
                "ACTIVE"
        );

        assertNotNull(vendor);
        assertNotNull(vendor.getVendorId());
        assertEquals("Test Vendor", vendor.getVendorName());
        assertEquals("vendor@test.com", vendor.getContactEmail());
        assertEquals("Sri Lanka", vendor.getCountry());
        assertEquals("ACTIVE", vendor.getStatus());
        assertNotNull(vendor.getCreatedAt());
    }

    @Test
    @DisplayName("Should throw ValidationException when creating vendor with missing fields")
    void createVendor_ShouldThrowValidationException_MissingFields() {
        assertThrows(ValidationException.class, () ->
                vendorService.createVendor(null, null, null, null, null)
        );
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException for duplicate email")
    void createVendor_ShouldThrowDuplicateResourceException() throws Exception {
        vendorService.createVendor(
                "Vendor 1",
                "duplicate@email.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        assertThrows(DuplicateResourceException.class, () ->
                vendorService.createVendor(
                        "Vendor 2",
                        "duplicate@email.com",
                        "456",
                        "Sri Lanka",
                        "ACTIVE"
                )
        );
    }

    // ============================================================
    // 2. UPDATE VENDOR TESTS
    // ============================================================

    @Test
    @DisplayName("Should update vendor successfully")
    void updateVendor_ShouldSucceed() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Original Name",
                "original@email.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        Vendor updated = vendorService.updateVendor(
                vendor.getVendorId(),
                "Updated Name",
                "updated@email.com",
                "456",
                "Singapore",
                "SUSPENDED"
        );

        assertEquals("Updated Name", updated.getVendorName());
        assertEquals("updated@email.com", updated.getContactEmail());
        assertEquals("Singapore", updated.getCountry());
        assertEquals("SUSPENDED", updated.getStatus());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vendor not found")
    void updateVendor_ShouldThrowResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () ->
                vendorService.updateVendor(999, "Name", "email@test.com", "123", "Sri Lanka", "ACTIVE")
        );
    }

    // ============================================================
    // 3. FIND VENDOR TESTS
    // ============================================================

    @Test
    @DisplayName("Should find vendor by ID")
    void findVendorById_ShouldReturnVendor() throws Exception {
        Vendor created = vendorService.createVendor(
                "Find Test",
                "find@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        Optional<Vendor> found = vendorService.findVendorById(created.getVendorId());

        assertTrue(found.isPresent());
        assertEquals(created.getVendorId(), found.get().getVendorId());
        assertEquals("Find Test", found.get().getVendorName());
    }

    @Test
    @DisplayName("Should find vendor by email")
    void findVendorByEmail_ShouldReturnVendor() throws Exception {
        String email = "emailfind@test.com";
        vendorService.createVendor(
                "Email Find Test",
                email,
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        Optional<Vendor> found = vendorService.findVendorByEmail(email);

        assertTrue(found.isPresent());
        assertEquals(email, found.get().getContactEmail());
    }

    @Test
    @DisplayName("Should find all vendors")
    void findAllVendors_ShouldReturnList() throws Exception {
        vendorService.createVendor("Vendor A", "a@test.com", "111", "Sri Lanka", "ACTIVE");
        vendorService.createVendor("Vendor B", "b@test.com", "222", "Sri Lanka", "ACTIVE");

        List<Vendor> vendors = vendorService.findAllVendors();

        assertTrue(vendors.size() >= 2);
    }

    // ============================================================
    // 4. DELETE VENDOR TESTS
    // ============================================================

    @Test
    @DisplayName("Should delete vendor successfully")
    void deleteVendor_ShouldSucceed() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Delete Test",
                "delete@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        vendorService.deleteVendor(vendor.getVendorId());

        Optional<Vendor> found = vendorService.findVendorById(vendor.getVendorId());
        assertFalse(found.isPresent());
    }

    // ============================================================
    // 5. PERFORMANCE EVALUATION TESTS - Using VendorPerformanceEvaluationService
    // ============================================================

    @Test
    @DisplayName("Should create evaluation and update vendor rating")
    void evaluateVendor_ShouldUpdateRating() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Eval Vendor",
                "eval@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        VendorPerformanceEvaluation eval1 = vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(4.5),
                LocalDate.now(),
                "Excellent performance"
        );

        assertNotNull(eval1);
        assertEquals(4.5, eval1.getScore().doubleValue());

        userTransaction.begin();
        Vendor updatedVendor = em.find(Vendor.class, vendor.getVendorId());
        userTransaction.commit();

        assertNotNull(updatedVendor);
        assertNotNull(updatedVendor.getPerformanceRating());
        assertEquals(4.5, updatedVendor.getPerformanceRating().doubleValue());

        // Second evaluation
        VendorPerformanceEvaluation eval2 = vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(3.8),
                LocalDate.now(),
                "Good but improvement needed"
        );

        assertNotNull(eval2);

        userTransaction.begin();
        Vendor finalVendor = em.find(Vendor.class, vendor.getVendorId());
        userTransaction.commit();

        assertNotNull(finalVendor);
        double avg = (4.5 + 3.8) / 2;
        assertEquals(avg, finalVendor.getPerformanceRating().doubleValue(), 0.01);
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid score")
    void evaluateVendor_ShouldThrowValidationException_InvalidScore() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Invalid Score Vendor",
                "invalid@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        assertThrows(ValidationException.class, () ->
                vendorEvalService.createEvaluation(
                        vendor.getVendorId(),
                        evaluator.getUserId(),
                        BigDecimal.valueOf(6.0),
                        LocalDate.now(),
                        "Invalid score"
                )
        );

        assertThrows(ValidationException.class, () ->
                vendorEvalService.createEvaluation(
                        vendor.getVendorId(),
                        evaluator.getUserId(),
                        BigDecimal.valueOf(-1.0),
                        LocalDate.now(),
                        "Invalid score"
                )
        );
    }

    @Test
    @DisplayName("Should get vendor evaluations")
    void getVendorEvaluations_ShouldReturnList() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Eval List Vendor",
                "evalist@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(4.0),
                LocalDate.now(),
                "First"
        );
        vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(3.5),
                LocalDate.now(),
                "Second"
        );

        List<VendorPerformanceEvaluation> evaluations = vendorEvalService.getEvaluationsForVendor(vendor.getVendorId());

        assertEquals(2, evaluations.size());
        assertTrue(evaluations.stream().anyMatch(e -> "First".equals(e.getComments())));
        assertTrue(evaluations.stream().anyMatch(e -> "Second".equals(e.getComments())));
    }

    @Test
    @DisplayName("Should get average rating")
    void getAverageRating_ShouldReturnAverage() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Avg Vendor",
                "avg@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(4.0),
                LocalDate.now(),
                null
        );
        vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(3.0),
                LocalDate.now(),
                null
        );

        BigDecimal avg = vendorEvalService.getAverageRating(vendor.getVendorId());

        assertNotNull(avg);
        assertEquals(3.5, avg.doubleValue(), 0.01);
    }

    @Test
    @DisplayName("Should recalculate vendor rating")
    void recalculateVendorRating_ShouldUpdateRating() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Recalc Vendor",
                "recalc@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(4.0),
                LocalDate.now(),
                "First"
        );
        vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(3.0),
                LocalDate.now(),
                "Second"
        );

        vendorEvalService.recalculateVendorRating(vendor.getVendorId());

        userTransaction.begin();
        Vendor updatedVendor = em.find(Vendor.class, vendor.getVendorId());
        userTransaction.commit();

        assertNotNull(updatedVendor);
        assertEquals(3.5, updatedVendor.getPerformanceRating().doubleValue(), 0.01);
    }

    // ============================================================
    // 6. VENDOR STATUS MANAGEMENT TESTS
    // ============================================================

    @Test
    @DisplayName("Should activate vendor")
    void activateVendor_ShouldSucceed() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Activate Test",
                "activate@test.com",
                "123",
                "Sri Lanka",
                "SUSPENDED"
        );

        vendorService.activateVendor(vendor.getVendorId());

        Optional<Vendor> updated = vendorService.findVendorById(vendor.getVendorId());
        assertTrue(updated.isPresent());
        assertEquals("ACTIVE", updated.get().getStatus());
    }

    @Test
    @DisplayName("Should suspend vendor")
    void suspendVendor_ShouldSucceed() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Suspend Test",
                "suspend@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        vendorService.suspendVendor(vendor.getVendorId());

        Optional<Vendor> updated = vendorService.findVendorById(vendor.getVendorId());
        assertTrue(updated.isPresent());
        assertEquals("SUSPENDED", updated.get().getStatus());
    }

    // ============================================================
    // 7. EMAIL UNIQUENESS TESTS
    // ============================================================

    @Test
    @DisplayName("Should check email uniqueness")
    void isEmailUnique_ShouldReturnCorrectValue() throws Exception {
        String email = "unique@test.com";
        vendorService.createVendor(
                "Unique Test",
                email,
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        assertFalse(vendorService.isEmailUnique(email, null));

        Optional<Vendor> vendor = vendorService.findVendorByEmail(email);
        assertTrue(vendor.isPresent());
        assertTrue(vendorService.isEmailUnique(email, vendor.get().getVendorId()));

        assertTrue(vendorService.isEmailUnique("new@test.com", null));
    }

    // ============================================================
    // 8. DELETE EVALUATION TEST
    // ============================================================

    @Test
    @DisplayName("Should delete evaluation and recalculate rating")
    void deleteEvaluation_ShouldRecalculateRating() throws Exception {
        Vendor vendor = vendorService.createVendor(
                "Delete Eval Vendor",
                "deleteeval@test.com",
                "123",
                "Sri Lanka",
                "ACTIVE"
        );

        VendorPerformanceEvaluation eval1 = vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(5.0),
                LocalDate.now(),
                "Perfect"
        );

        vendorEvalService.createEvaluation(
                vendor.getVendorId(),
                evaluator.getUserId(),
                BigDecimal.valueOf(3.0),
                LocalDate.now(),
                "Average"
        );

        vendorEvalService.deleteEvaluation(eval1.getEvalId());

        userTransaction.begin();
        Vendor updatedVendor = em.find(Vendor.class, vendor.getVendorId());
        userTransaction.commit();

        assertNotNull(updatedVendor);
        assertEquals(3.0, updatedVendor.getPerformanceRating().doubleValue(), 0.01);
    }
}