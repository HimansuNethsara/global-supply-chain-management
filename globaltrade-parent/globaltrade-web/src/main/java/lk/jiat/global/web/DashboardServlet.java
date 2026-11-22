package lk.jiat.global.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.dao.InventoryItemDAO;
import lk.jiat.global.dao.ShipmentDAO;
import lk.jiat.global.dao.VendorPerformanceEvaluationDAO;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.User;
import lk.jiat.global.entity.VendorPerformanceEvaluation;
import lk.jiat.global.util.AppLogger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @EJB
    private ShipmentDAO shipmentDAO;

    @EJB
    private InventoryItemDAO inventoryItemDAO;

    @EJB
    private VendorPerformanceEvaluationDAO vendorPerformanceEvaluationDAO;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            User user = (User) session.getAttribute("user");

            // ===== STATS CARDS =====
            DashboardStats stats = getDashboardStats();

            // ===== RECENT SHIPMENTS =====
            List<DashboardShipment> recentShipments = getRecentShipments();

            // ===== VENDOR PERFORMANCE =====
            List<DashboardVendorPerformance> vendorPerformances = getVendorPerformance();

            // ===== LOW INVENTORY ITEMS =====
            List<DashboardInventoryItem> lowInventoryItems = getLowInventoryItems();

            // ===== ALERTS COUNT =====
            int pendingAlertCount = getPendingAlertCount();

            // Set attributes for JSP
            req.setAttribute("user", user);
            req.setAttribute("stats", stats);
            req.setAttribute("recentShipments", recentShipments);
            req.setAttribute("vendorPerformances", vendorPerformances);
            req.setAttribute("lowInventoryItems", lowInventoryItems);
            req.setAttribute("pendingAlertCount", pendingAlertCount);

            AppLogger.info("Dashboard loaded for user: {}", user.getUsername());

        } catch (Exception e) {
            AppLogger.error("Failed to load dashboard", e);
            req.setAttribute("error", "Failed to load dashboard data");
        }

        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }

    // ============================================================
    // PRIVATE HELPER METHODS
    // ============================================================

    private DashboardStats getDashboardStats() {
        try {
            List<Shipment> allShipments = shipmentDAO.findAll();

            long totalShipments = allShipments.size();
            long inTransit = allShipments.stream()
                    .filter(s -> "IN_TRANSIT".equals(s.getStatus()))
                    .count();
            long delayed = allShipments.stream()
                    .filter(s -> "DELAYED".equals(s.getStatus()))
                    .count();
            long delivered = allShipments.stream()
                    .filter(s -> "DELIVERED".equals(s.getStatus()))
                    .count();

            // Calculate growth (compare last 30 days vs previous 30 days)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime thirtyDaysAgo = now.minusDays(30);
            LocalDateTime sixtyDaysAgo = now.minusDays(60);

            long recentCount = allShipments.stream()
                    .filter(s -> s.getCreatedAt().isAfter(thirtyDaysAgo))
                    .count();
            long previousCount = allShipments.stream()
                    .filter(s -> s.getCreatedAt().isAfter(sixtyDaysAgo) && s.getCreatedAt().isBefore(thirtyDaysAgo))
                    .count();

            double growthPercent = 0;
            if (previousCount > 0) {
                growthPercent = ((double) (recentCount - previousCount) / previousCount) * 100;
            }

            long lowStockItems = inventoryItemDAO.findLowStockItems().size();

            // Average delivery time (for delivered shipments)
            double avgDeliveryDays = 0;
            if (delivered > 0) {
                long totalDays = allShipments.stream()
                        .filter(s -> "DELIVERED".equals(s.getStatus()) &&
                                s.getActualDeliveryDate() != null &&
                                s.getScheduledDate() != null)
                        .mapToLong(s -> {
                            long days = java.time.Duration.between(
                                    s.getScheduledDate(),
                                    s.getActualDeliveryDate()
                            ).toDays();
                            return Math.max(0, days);
                        })
                        .sum();
                avgDeliveryDays = (double) totalDays / delivered;
            }

            return new DashboardStats(
                    totalShipments,
                    inTransit,
                    delayed,
                    delivered,
                    lowStockItems,
                    Math.round(growthPercent * 100) / 100.0,
                    Math.round(avgDeliveryDays * 100) / 100.0
            );

        } catch (Exception e) {
            AppLogger.error("Failed to get dashboard stats", e);
            return new DashboardStats(0, 0, 0, 0, 0, 0, 0);
        }
    }

    private List<DashboardShipment> getRecentShipments() {
        try {
            List<Shipment> shipments = shipmentDAO.findAll();

            // Sort by createdAt descending and limit to 5
            return shipments.stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(5)
                    .map(s -> new DashboardShipment(
                            s.getTrackingNumber(),
                            s.getOriginWarehouse().getLocation(),
                            s.getDestination(),
                            s.getStatus(),
                            s.getScheduledDate(),
                            s.getShipmentId()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            AppLogger.error("Failed to get recent shipments", e);
            return new ArrayList<>();
        }
    }

    private List<DashboardVendorPerformance> getVendorPerformance() {
        try {
            List<DashboardVendorPerformance> performances = new ArrayList<>();

            List<Object[]> results = vendorPerformanceEvaluationDAO.findVendorPerformanceWithLastEval();

            int count = 0;
            for (Object[] row : results) {
                if (count >= 3) break;

                Integer vendorId = (Integer) row[0];

                String vendorName = (String) row[1];

                String country = (String) row[2];

                BigDecimal avgRating = row[3] != null ? BigDecimal.valueOf((Double) row[3]) : BigDecimal.ZERO;

                LocalDate lastEvalDate = (LocalDate) row[4];

                String lastEval = lastEvalDate != null ? lastEvalDate.toString() : "N/A";

                performances.add(new DashboardVendorPerformance(
                        vendorName,
                        country,
                        avgRating,
                        "ACTIVE",
                        lastEval
                ));
                count++;
            }

            return performances;

        } catch (Exception e) {
            AppLogger.error("Failed to get vendor performance", e);
            return new ArrayList<>();
        }
    }

    private List<DashboardInventoryItem> getLowInventoryItems() {
        try {
            List<InventoryItem> lowStockItems = inventoryItemDAO.findLowStockItems();

            return lowStockItems.stream()
                    .limit(3)
                    .map(item -> new DashboardInventoryItem(
                            item.getSku(),
                            item.getName(),
                            item.getQuantity(),
                            item.getReorderLevel(),
                            item.getUnitPrice(),
                            item.getWarehouse().getName()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            AppLogger.error("Failed to get low inventory items", e);
            return new ArrayList<>();
        }
    }

    private int getPendingAlertCount() {
        try {
            // This would be from AlertService, but for now we'll count from inventory
            return inventoryItemDAO.findLowStockItems().size();
        } catch (Exception e) {
            return 0;
        }
    }

    // ============================================================
    // INNER CLASSES FOR DATA TRANSFER
    // ============================================================

    public static class DashboardStats {
        private final long totalShipments;
        private final long inTransit;
        private final long delayed;
        private final long delivered;
        private final long lowInventory;
        private final double growthPercent;
        private final double avgDeliveryDays;

        public DashboardStats(long totalShipments, long inTransit, long delayed,
                              long delivered, long lowInventory, double growthPercent,
                              double avgDeliveryDays) {
            this.totalShipments = totalShipments;
            this.inTransit = inTransit;
            this.delayed = delayed;
            this.delivered = delivered;
            this.lowInventory = lowInventory;
            this.growthPercent = growthPercent;
            this.avgDeliveryDays = avgDeliveryDays;
        }

        public long getTotalShipments() { return totalShipments; }
        public long getInTransit() { return inTransit; }
        public long getDelayed() { return delayed; }
        public long getDelivered() { return delivered; }
        public long getLowInventory() { return lowInventory; }
        public double getGrowthPercent() { return growthPercent; }
        public double getAvgDeliveryDays() { return avgDeliveryDays; }
        public long getPendingShipments() { return totalShipments - delivered - delayed; }
    }

    public static class DashboardShipment {
        private final String trackingNumber;
        private final String origin;
        private final String destination;
        private final String status;
        private final LocalDateTime scheduledDate;
        private final Integer shipmentId;

        public DashboardShipment(String trackingNumber, String origin, String destination,
                                 String status, LocalDateTime scheduledDate, Integer shipmentId) {
            this.trackingNumber = trackingNumber;
            this.origin = origin;
            this.destination = destination;
            this.status = status;
            this.scheduledDate = scheduledDate;
            this.shipmentId = shipmentId;
        }

        public String getTrackingNumber() { return trackingNumber; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
        public String getStatus() { return status; }
        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public Integer getShipmentId() { return shipmentId; }
        public String getStatusDisplay() {
            switch (status) {
                case "DELIVERED": return "Delivered";
                case "IN_TRANSIT": return "In Transit";
                case "DELAYED": return "Delayed";
                case "PENDING": return "Pending";
                case "CANCELLED": return "Cancelled";
                default: return status;
            }
        }
        public String getStatusClass() {
            switch (status) {
                case "DELIVERED": return "completed";
                case "IN_TRANSIT": return "pending";
                case "DELAYED": return "failed";
                case "CANCELLED": return "failed";
                default: return "pending";
            }
        }
    }

    public static class DashboardVendorPerformance {
        private final String vendorName;
        private final String country;
        private final BigDecimal rating;
        private final String status;
        private final String lastEvaluation;

        public DashboardVendorPerformance(String vendorName, String country,
                                          BigDecimal rating, String status, String lastEvaluation) {
            this.vendorName = vendorName;
            this.country = country;
            this.rating = rating;
            this.status = status;
            this.lastEvaluation = lastEvaluation;
        }

        public String getVendorName() { return vendorName; }
        public String getCountry() { return country; }
        public BigDecimal getRating() { return rating; }
        public String getStatus() { return status; }
        public String getLastEvaluation() { return lastEvaluation; }
        public String getRatingColor() {
            if (rating == null) return "text-white/30";
            if (rating.compareTo(BigDecimal.valueOf(4.0)) >= 0) return "text-green-400";
            if (rating.compareTo(BigDecimal.valueOf(3.0)) >= 0) return "text-yellow-400";
            return "text-red-400";
        }
    }

    public static class DashboardInventoryItem {
        private final String sku;
        private final String name;
        private final int quantity;
        private final int reorderLevel;
        private final BigDecimal unitPrice;
        private final String warehouse;

        public DashboardInventoryItem(String sku, String name, int quantity,
                                      int reorderLevel, BigDecimal unitPrice, String warehouse) {
            this.sku = sku;
            this.name = name;
            this.quantity = quantity;
            this.reorderLevel = reorderLevel;
            this.unitPrice = unitPrice;
            this.warehouse = warehouse;
        }

        public String getSku() { return sku; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public int getReorderLevel() { return reorderLevel; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public String getWarehouse() { return warehouse; }
        public boolean isLowStock() { return quantity <= reorderLevel; }
    }
}