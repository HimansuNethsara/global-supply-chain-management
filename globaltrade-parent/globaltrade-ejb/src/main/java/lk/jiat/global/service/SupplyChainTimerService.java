package lk.jiat.global.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;  // ← ADD THIS IMPORT
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import lk.jiat.global.dao.InventoryItemDAO;
import lk.jiat.global.dao.ShipmentDAO;
import lk.jiat.global.dao.CustomsDocumentDAO;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.CustomsDocument;
import lk.jiat.global.entity.Alert;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.dao.VendorPerformanceEvaluationDAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Singleton
@Startup
public class SupplyChainTimerService {

    private static final String TIMER_INVENTORY = "Inventory Reorder Check";
    private static final String TIMER_SHIPMENT_DELAY = "Shipment Delay Check";
    private static final String TIMER_CUSTOMS = "Customs Deadline Check";
    private static final String TIMER_VENDOR_EVAL = "Vendor Performance Evaluation";

    @Resource
    private TimerService timerService;

    @Inject
    private InventoryItemDAO inventoryItemDAO;

    @Inject
    private ShipmentDAO shipmentDAO;

    @Inject
    private CustomsDocumentDAO customsDocumentDAO;

    @Inject
    private VendorPerformanceEvaluationDAO vendorPerformanceEvaluationDAO;

    @Inject
    private VendorPerformanceEvaluationServiceLocal vendorEvaluationService;

    @Inject
    private SystemTimerServiceLocal systemTimerService;

    @Inject
    private AlertServiceLocal alertService;

    @PostConstruct
    public void init() {
        AppLogger.info("SupplyChainTimerService initialized");
    }

    // ============================================================
    // 1. INVENTORY REORDER CHECK - Every 5 minutes
    // ============================================================

    @Schedule(hour = "*", minute = "*/5", second = "0", persistent = false)
    public void checkInventoryReorderLevels() {
        AppLogger.info("Running inventory reorder check...");
        try {
            systemTimerService.registerTimerExecution(TIMER_INVENTORY, "ACTIVE", null);

            List<InventoryItem> lowStockItems = inventoryItemDAO.findLowStockItems();

            for (InventoryItem item : lowStockItems) {
                String message = String.format(
                        "Low stock alert: Item '%s' (SKU: %s) has %d units remaining. Reorder level is %d.",
                        item.getName(), item.getSku(), item.getQuantity(), item.getReorderLevel()
                );
                alertService.createOrUpdateAlert(
                        "LOW_INVENTORY",
                        "InventoryItem",
                        item.getItemId(),
                        item.getQuantity() <= 0 ? "CRITICAL" : "HIGH",
                        message
                );
                AppLogger.info("Low inventory alert for item: {}", item.getSku());
            }

            checkAndResolveInventoryAlerts();

            AppLogger.info("Inventory reorder check completed. Found {} low stock items.", lowStockItems.size());

        } catch (Exception e) {
            AppLogger.error("Inventory reorder check failed", e);
            systemTimerService.registerTimerExecution(TIMER_INVENTORY, "FAILED", e.getMessage());
        }
    }

    // ============================================================
    // 2. SHIPMENT DELAY CHECK - Every 15 minutes
    // ============================================================

    @Schedule(hour = "*", minute = "*/15", second = "0", persistent = false)
    @PermitAll  // ✅ ADD THIS
    public void checkShipmentDelays() {
        AppLogger.info("Running shipment delay check...");
        try {
            systemTimerService.registerTimerExecution(TIMER_SHIPMENT_DELAY, "ACTIVE", null);

            List<Shipment> delayedShipments = shipmentDAO.findDelayedShipments();

            for (Shipment shipment : delayedShipments) {
                String message = String.format(
                        "Shipment %s (Tracking: %s) is delayed. Destination: %s",
                        shipment.getShipmentId(), shipment.getTrackingNumber(),
                        shipment.getDestination()
                );
                alertService.createOrUpdateAlert(
                        "SHIPMENT_DELAY",
                        "Shipment",
                        shipment.getShipmentId(),
                        "HIGH",
                        message
                );
                AppLogger.info("Shipment delay alert for: {}", shipment.getTrackingNumber());
            }

            // Check PENDING shipments past scheduled date
            List<Shipment> pendingShipments = shipmentDAO.findByStatus("PENDING");
            LocalDateTime now = LocalDateTime.now();
            for (Shipment shipment : pendingShipments) {
                if (shipment.getScheduledDate().isBefore(now.minusHours(2))) {
                    String message = String.format(
                            "Pending shipment %s (Tracking: %s) was scheduled for %s but has not been dispatched.",
                            shipment.getShipmentId(), shipment.getTrackingNumber(),
                            shipment.getScheduledDate()
                    );
                    alertService.createOrUpdateAlert(
                            "SHIPMENT_DELAY",
                            "Shipment",
                            shipment.getShipmentId(),
                            "MEDIUM",
                            message
                    );
                }
            }

            AppLogger.info("Shipment delay check completed. Found {} delayed shipments.", delayedShipments.size());

        } catch (Exception e) {
            AppLogger.error("Shipment delay check failed", e);
            systemTimerService.registerTimerExecution(TIMER_SHIPMENT_DELAY, "FAILED", e.getMessage());
        }
    }

    // ============================================================
    // 3. CUSTOMS DEADLINE CHECK - Every 10 minutes
    // ============================================================

    @Schedule(hour = "*", minute = "*/10", second = "0", persistent = false)
    @PermitAll  // ✅ ADD THIS
    public void checkCustomsDeadlines() {
        AppLogger.info("Running customs deadline check...");
        try {
            systemTimerService.registerTimerExecution(TIMER_CUSTOMS, "ACTIVE", null);

            List<CustomsDocument> pendingDocs = customsDocumentDAO.findByStatus("PENDING");

            for (CustomsDocument doc : pendingDocs) {
                LocalDateTime createdTime = doc.getShipment().getCreatedAt();
                if (createdTime != null && createdTime.isBefore(LocalDateTime.now().minusHours(24))) {
                    String message = String.format(
                            "Customs document %d (Type: %s) for shipment %s is pending for more than 24 hours.",
                            doc.getDocId(), doc.getDocumentType(),
                            doc.getShipment().getTrackingNumber()
                    );
                    alertService.createOrUpdateAlert(
                            "CUSTOMS_DEADLINE",
                            "CustomsDocument",
                            doc.getDocId(),
                            "MEDIUM",
                            message
                    );
                    AppLogger.info("Customs deadline alert for document: {}", doc.getDocId());
                }
            }

            // Check SUBMITTED documents awaiting approval (more than 12 hours)
            List<CustomsDocument> submittedDocs = customsDocumentDAO.findByStatus("SUBMITTED");
            for (CustomsDocument doc : submittedDocs) {
                LocalDateTime submittedDate = doc.getSubmittedDate();
                if (submittedDate != null && submittedDate.isBefore(LocalDateTime.now().minusHours(12))) {
                    String message = String.format(
                            "Customs document %d (Type: %s) for shipment %s has been submitted for approval more than 12 hours ago.",
                            doc.getDocId(), doc.getDocumentType(),
                            doc.getShipment().getTrackingNumber()
                    );
                    alertService.createOrUpdateAlert(
                            "CUSTOMS_DEADLINE",
                            "CustomsDocument",
                            doc.getDocId(),
                            "HIGH",
                            message
                    );
                }
            }

            AppLogger.info("Customs deadline check completed. Found {} pending documents.", pendingDocs.size());

        } catch (Exception e) {
            AppLogger.error("Customs deadline check failed", e);
            systemTimerService.registerTimerExecution(TIMER_CUSTOMS, "FAILED", e.getMessage());
        }
    }

    // ============================================================
    // 4. VENDOR PERFORMANCE EVALUATION - Every 24 hours at 23:59
    // ============================================================

    @Schedule(hour = "23", minute = "59", second = "0", persistent = false)
    @PermitAll  // ✅ ADD THIS
    public void evaluateVendorPerformance() {
        AppLogger.info("Running vendor performance evaluation...");
        try {
            systemTimerService.registerTimerExecution(TIMER_VENDOR_EVAL, "ACTIVE", null);

            List<Object[]> lowPerformingVendors = vendorPerformanceEvaluationDAO
                    .findVendorsWithLowRating(BigDecimal.valueOf(3.0));

            for (Object[] result : lowPerformingVendors) {
                Integer vendorId = (Integer) result[0];
                String vendorName = (String) result[1];
                BigDecimal avgRating = (BigDecimal) result[2];

                String message = String.format(
                        "Vendor '%s' has a low performance rating of %.2f. Review required.",
                        vendorName, avgRating
                );
                alertService.createOrUpdateAlert(
                        "VENDOR_ISSUE",
                        "Vendor",
                        vendorId,
                        avgRating.compareTo(BigDecimal.valueOf(2.0)) < 0 ? "CRITICAL" : "HIGH",
                        message
                );
                AppLogger.info("Vendor performance alert for: {} (rating: {})", vendorName, avgRating);
            }

            AppLogger.info("Vendor performance evaluation completed. Found {} low performing vendors.",
                    lowPerformingVendors.size());

        } catch (Exception e) {
            AppLogger.error("Vendor performance evaluation failed", e);
            systemTimerService.registerTimerExecution(TIMER_VENDOR_EVAL, "FAILED", e.getMessage());
        }
    }

    // ============================================================
    // 5. PROGRAMMATIC TIMER
    // ============================================================

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createProgrammaticTimer() {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("Programmatic timer for supply chain monitoring");
        timerConfig.setPersistent(true);

        ScheduleExpression schedule = new ScheduleExpression()
                .hour("*")
                .minute("0")
                .second("0");

        Timer timer = timerService.createCalendarTimer(schedule, timerConfig);
        AppLogger.info("Created programmatic timer with ID: {}", timer.getInfo());

        try {
            systemTimerService.createTimer(
                    "Programmatic Hourly Timer",
                    "PROGRAMMATIC",
                    "0 0 * * * ?"
            );
        } catch (Exception e) {
            AppLogger.warning("Failed to register programmatic timer: {}", e.getMessage());
        }
    }

    /**
     * Timeout callback for programmatic timer
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll  // ✅ ADD THIS - Timer callback
    public void programmaticTimerTimeout(Timer timer) {
        AppLogger.info("Programmatic timer fired: {}", timer.getInfo());
        systemTimerService.registerTimerExecution("Programmatic Hourly Timer", "ACTIVE", null);
    }

    // ============================================================
    // PRIVATE HELPER METHODS
    // ============================================================

    private void checkAndResolveInventoryAlerts() {
        try {
            List<InventoryItem> lowStockItems = inventoryItemDAO.findLowStockItems();
            List<Alert> activeAlerts = alertService.getActiveAlerts();

            for (Alert alert : activeAlerts) {
                if (!"LOW_INVENTORY".equals(alert.getAlertType()) || alert.getResolved()) {
                    continue;
                }

                if (alert.getEntityType() != null && "InventoryItem".equals(alert.getEntityType())) {
                    Integer itemId = alert.getEntityId();
                    if (itemId == null) {
                        continue;
                    }

                    boolean isStillLow = false;
                    for (InventoryItem item : lowStockItems) {
                        if (item.getItemId().equals(itemId)) {
                            isStillLow = true;
                            break;
                        }
                    }

                    if (!isStillLow) {
                        try {
                            InventoryItem item = inventoryItemDAO.findById(itemId).orElse(null);
                            String itemName = item != null ? item.getName() : "Item " + itemId;

                            alert.setResolved(true);
                            alert.setMessage(alert.getMessage() + " (Resolved - Inventory restocked to " +
                                    (item != null ? item.getQuantity() : "normal") + " units)");
                            alertService.resolveAlert(alert.getAlertId());
                            AppLogger.info("Auto-resolved inventory alert for item: {} (ID: {})", itemName, itemId);
                        } catch (Exception e) {
                            AppLogger.warning("Failed to auto-resolve inventory alert for item {}: {}",
                                    itemId, e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            AppLogger.error("Failed to check and resolve inventory alerts: {}", e.getMessage());
        }
    }
}