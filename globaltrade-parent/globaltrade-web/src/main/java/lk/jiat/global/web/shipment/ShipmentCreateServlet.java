package lk.jiat.global.web.shipment;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.global.entity.Carrier;
import lk.jiat.global.entity.CustomerOrder;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.OrderItem;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.InsufficientInventoryException;
import lk.jiat.global.exception.VendorSuspendedException;
import lk.jiat.global.service.CarrierServiceLocal;
import lk.jiat.global.service.CustomerOrderServiceLocal;
import lk.jiat.global.service.RoutePlanServiceLocal;
import lk.jiat.global.service.ShipmentServiceLocal;
import lk.jiat.global.service.VendorServiceLocal;
import lk.jiat.global.service.WarehouseServiceLocal;
import lk.jiat.global.util.AppLogger;
import lk.jiat.global.util.TrackingNumberGenerator;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/shipments/create")
public class ShipmentCreateServlet extends HttpServlet {

    @EJB
    private ShipmentServiceLocal shipmentService;

    @EJB
    private WarehouseServiceLocal warehouseService;

    @EJB
    private VendorServiceLocal vendorService;

    @EJB
    private CarrierServiceLocal carrierService;

    @EJB
    private RoutePlanServiceLocal routePlanService;

    @EJB
    private CustomerOrderServiceLocal customerOrderService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final String[] VALID_STATUSES = {"PENDING", "IN_TRANSIT", "DELIVERED", "DELAYED", "CANCELLED"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"LOGISTICS_COORDINATOR".equals(role) && !"WAREHOUSE_MANAGER".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp?requiredRole=LOGISTICS_COORDINATOR");
            return;
        }

        try {
            List<Warehouse> warehouses = warehouseService.findAllWarehouses();
            List<Vendor> vendors = vendorService.findAllVendors();
            List<Carrier> carriers = carrierService.findAllCarriers();

            List<CustomerOrder> pendingOrders = new ArrayList<>();
            try {
                pendingOrders = customerOrderService.getPendingOrders();

                for (CustomerOrder order : pendingOrders) {
                    try {
                        int count = customerOrderService.getOrderItemCount(order.getOrderId());
                        order.setItemCount(count);
                    } catch (Exception e) {
                        AppLogger.warning("Failed to get item count for order {}: {}", order.getOrderId(), e.getMessage());
                        order.setItemCount(0);
                    }
                }

                AppLogger.info("Loaded {} pending orders", pendingOrders.size());
            } catch (Exception e) {
                AppLogger.warning("Failed to load pending orders: {}", e.getMessage());
            }

            // Pre-filter orders by warehouse if specified
            String warehouseIdParam = req.getParameter("warehouseId");
            List<CustomerOrder> filteredOrders = pendingOrders;
            if (warehouseIdParam != null && !warehouseIdParam.isEmpty()) {
                try {
                    Integer warehouseId = Integer.parseInt(warehouseIdParam);
                    filteredOrders = filterOrdersByWarehouse(pendingOrders, warehouseId);
                    AppLogger.info("Filtered to {} orders for warehouse {}", filteredOrders.size(), warehouseId);
                } catch (NumberFormatException e) {
                    AppLogger.warning("Invalid warehouse ID: {}", warehouseIdParam);
                }
            }

            req.setAttribute("warehouses", warehouses);
            req.setAttribute("vendors", vendors);
            req.setAttribute("carriers", carriers);
            req.setAttribute("pendingOrders", filteredOrders);
            req.setAttribute("allPendingOrders", pendingOrders);

            String warehouseId = req.getParameter("warehouseId");
            String vendorId = req.getParameter("vendorId");
            String carrierId = req.getParameter("carrierId");

            req.setAttribute("preSelectedWarehouse", warehouseId);
            req.setAttribute("preSelectedVendor", vendorId);
            req.setAttribute("preSelectedCarrier", carrierId);

        } catch (Exception e) {
            AppLogger.error("Failed to load data for shipment creation", e);
            req.setAttribute("error", "Failed to load data: " + e.getMessage());
        }

        req.getRequestDispatcher("/shipments/shipment-create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");

        // Get all parameters
        String originWarehouseIdStr = req.getParameter("originWarehouseId");
        String destination = req.getParameter("destination");
        String vendorIdStr = req.getParameter("vendorId");
        String carrierIdStr = req.getParameter("carrierId");
        String status = req.getParameter("status");
        String scheduledDateStr = req.getParameter("scheduledDate");
        String estimatedDeliveryDateStr = req.getParameter("estimatedDeliveryDate");

        // Route fields
        String plannedRoute = req.getParameter("plannedRoute");
        String distanceKmStr = req.getParameter("distanceKm");

        // ===== ORDER IDS =====
        String[] orderIdStrings = req.getParameterValues("orderIds");
        List<Integer> orderIds = new ArrayList<>();
        if (orderIdStrings != null) {
            for (String idStr : orderIdStrings) {
                try {
                    orderIds.add(Integer.parseInt(idStr));
                } catch (NumberFormatException e) {
                    AppLogger.warning("Invalid order ID: {}", idStr);
                }
            }
        }

        // Create form data
        ShipmentFormData formData = new ShipmentFormData(
                originWarehouseIdStr, destination, vendorIdStr,
                carrierIdStr, status, scheduledDateStr, estimatedDeliveryDateStr,
                plannedRoute, distanceKmStr, orderIds
        );

        try {
            // Validate required fields
            validateRequiredFields(formData);

            // Parse and validate IDs
            Integer originWarehouseId = parseInteger(originWarehouseIdStr);
            Integer vendorId = parseInteger(vendorIdStr);
            Integer carrierId = parseInteger(carrierIdStr);

            // Parse and validate dates
            LocalDateTime scheduledDate = parseDateTime(scheduledDateStr);
            LocalDateTime estimatedDeliveryDate = parseNullableDateTime(estimatedDeliveryDateStr);

            // Validate business rules
            validateBusinessRules(scheduledDate, estimatedDeliveryDate);

            // Verify entities exist and are valid
            Warehouse warehouse = verifyWarehouse(originWarehouseId);
            Vendor vendor = verifyVendor(vendorId);
            Carrier carrier = verifyCarrier(carrierId);

            // Get service type from carrier
            String serviceType = null;
            if (carrier != null) {
                serviceType = carrier.getServiceType();
                if (serviceType == null || serviceType.isEmpty()) {
                    serviceType = "Standard";
                }
            }

            // Validate and set default status
            String finalStatus = validateAndGetStatus(status);

            // Generate tracking number
            String trackingNumber = generateUniqueTrackingNumber(warehouse, vendor, carrier);

            // ===== VALIDATE ORDER ITEMS ARE IN WAREHOUSE =====
            if (!orderIds.isEmpty()) {
                validateOrdersInWarehouse(orderIds, originWarehouseId);
            }

            // Create shipment with orders
            Shipment shipment = shipmentService.createShipment(
                    trackingNumber,
                    originWarehouseId,
                    destination.trim(),
                    vendorId,
                    carrierId,
                    finalStatus,
                    scheduledDate,
                    estimatedDeliveryDate,
                    userId,
                    orderIds
            );

            // Create route plan if planned route is provided
            if (plannedRoute != null && !plannedRoute.trim().isEmpty()) {
                BigDecimal distanceKm = null;
                if (distanceKmStr != null && !distanceKmStr.isEmpty()) {
                    distanceKm = new BigDecimal(distanceKmStr);
                }
                routePlanService.createInitialRoutePlan(
                        shipment.getShipmentId(),
                        plannedRoute.trim(),
                        distanceKm,
                        serviceType
                );
                AppLogger.info("Route plan created for shipment: {}", shipment.getShipmentId());
            }

            AppLogger.info("Shipment created with tracking number: {} and {} attached orders",
                    trackingNumber, orderIds.size());
            session.setAttribute("successNotification", "Shipment created successfully! Tracking: " + trackingNumber);
            resp.sendRedirect(req.getContextPath() + "/shipments");

        } catch (VendorSuspendedException e) {
            handleError(req, resp, session, formData, e);

        } catch (InsufficientInventoryException e) {
            handleError(req, resp, session, formData, e);

        } catch (BusinessException e) {
            handleError(req, resp, session, formData, e);

        } catch (Exception e) {
            AppLogger.error("Failed to create shipment", e);
            session.setAttribute("errorNotification", "An unexpected error occurred: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/shipments/create");
        }
    }

    // ===== PRIVATE HELPER METHODS =====

    private void validateRequiredFields(ShipmentFormData formData) throws BusinessException {
        if (formData.originWarehouseId() == null || formData.originWarehouseId().trim().isEmpty()) {
            throw new BusinessException("Origin warehouse is required");
        }
        if (formData.destination() == null || formData.destination().trim().isEmpty()) {
            throw new BusinessException("Destination is required");
        }
        if (formData.vendorId() == null || formData.vendorId().trim().isEmpty()) {
            throw new BusinessException("Vendor is required");
        }
        if (formData.carrierId() == null || formData.carrierId().trim().isEmpty()) {
            throw new BusinessException("Carrier is required");
        }
        if (formData.scheduledDate() == null || formData.scheduledDate().trim().isEmpty()) {
            throw new BusinessException("Scheduled date is required");
        }
    }

    private Integer parseInteger(String value) throws BusinessException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("Invalid number format: " + value);
        }
    }

    private LocalDateTime parseDateTime(String value) throws BusinessException {
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (Exception e) {
            throw new BusinessException("Invalid scheduled date format. Please use the date picker.");
        }
    }

    private LocalDateTime parseNullableDateTime(String value) throws BusinessException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (Exception e) {
            throw new BusinessException("Invalid estimated delivery date format. Please use the date picker.");
        }
    }

    private void validateBusinessRules(LocalDateTime scheduledDate, LocalDateTime estimatedDeliveryDate) throws BusinessException {
        if (scheduledDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Scheduled date must be in the future");
        }
        if (estimatedDeliveryDate != null && estimatedDeliveryDate.isBefore(scheduledDate)) {
            throw new BusinessException("Estimated delivery date must be after scheduled date");
        }
    }

    private Warehouse verifyWarehouse(Integer warehouseId) throws BusinessException {
        return warehouseService.findWarehouseById(warehouseId)
                .orElseThrow(() -> new BusinessException("Warehouse not found with ID: " + warehouseId));
    }

    private Vendor verifyVendor(Integer vendorId) throws BusinessException {
        Vendor vendor = vendorService.findVendorById(vendorId)
                .orElseThrow(() -> new BusinessException("Vendor not found with ID: " + vendorId));
        if (!"ACTIVE".equals(vendor.getStatus())) {
            throw new BusinessException("Vendor '" + vendor.getVendorName() + "' is not active. Please select an active vendor.");
        }
        return vendor;
    }

    private Carrier verifyCarrier(Integer carrierId) throws BusinessException {
        if (carrierId == null) {
            throw new BusinessException("Carrier is required. Please select a carrier.");
        }
        return carrierService.findCarrierById(carrierId)
                .orElseThrow(() -> new BusinessException("Carrier not found with ID: " + carrierId));
    }

    private String validateAndGetStatus(String status) throws BusinessException {
        if (status == null || status.trim().isEmpty()) {
            return "PENDING";
        }
        String trimmedStatus = status.trim();
        boolean valid = false;
        for (String s : VALID_STATUSES) {
            if (s.equals(trimmedStatus)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new BusinessException("Invalid status: " + trimmedStatus);
        }
        if ("DELIVERED".equals(trimmedStatus) || "CANCELLED".equals(trimmedStatus)) {
            throw new BusinessException("Cannot create a shipment with status: " + trimmedStatus + ". Use PENDING or IN_TRANSIT instead.");
        }
        return trimmedStatus;
    }

    private String generateUniqueTrackingNumber(Warehouse warehouse, Vendor vendor, Carrier carrier) throws BusinessException {
        String trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, carrier);
        int retryCount = 0;
        while (!shipmentService.isTrackingNumberUnique(trackingNumber, null) && retryCount < 3) {
            trackingNumber = TrackingNumberGenerator.generateTrackingNumber(warehouse, vendor, carrier);
            retryCount++;
        }
        return trackingNumber;
    }

    // ===== ORDER VALIDATION =====

    private void validateOrdersInWarehouse(List<Integer> orderIds, Integer warehouseId)
            throws BusinessException {

        for (Integer orderId : orderIds) {
            List<OrderItem> items = customerOrderService.getOrderItems(orderId);
            for (OrderItem item : items) {
                InventoryItem inventoryItem = item.getItem();

                // Check warehouse
                if (!inventoryItem.getWarehouse().getWarehouseId().equals(warehouseId)) {
                    throw new BusinessException(
                            "Order #" + orderId + " contains item '" + inventoryItem.getName() +
                                    "' which is not in the selected warehouse."
                    );
                }

                // Check inventory
                if (inventoryItem.getQuantity() < item.getQuantity()) {
                    throw new BusinessException(
                            "Order #" + orderId + " requires " + item.getQuantity() +
                                    " units of '" + inventoryItem.getName() +
                                    "' but only " + inventoryItem.getQuantity() + " available."
                    );
                }
            }
        }
    }

    // ===== FILTER ORDERS BY WAREHOUSE =====

    private List<CustomerOrder> filterOrdersByWarehouse(List<CustomerOrder> orders, Integer warehouseId) {
        List<CustomerOrder> result = new ArrayList<>();

        for (CustomerOrder order : orders) {
            try {
                List<OrderItem> orderItems = customerOrderService.getOrderItems(order.getOrderId());

                if (orderItems.isEmpty()) {
                    continue;
                }

                boolean allItemsInWarehouse = true;

                for (OrderItem orderItem : orderItems) {
                    InventoryItem item = orderItem.getItem();
                    Integer itemWarehouseId = item.getWarehouse().getWarehouseId();

                    if (!itemWarehouseId.equals(warehouseId)) {
                        allItemsInWarehouse = false;
                        break;
                    }
                }

                if (allItemsInWarehouse) {
                    result.add(order);
                }

            } catch (Exception e) {
                AppLogger.warning("Failed to check order {}: {}", order.getOrderId(), e.getMessage());
            }
        }

        return result;
    }

    // ===== ERROR HANDLING =====

    private void handleError(HttpServletRequest req,
                             HttpServletResponse resp,
                             HttpSession session,
                             ShipmentFormData formData,
                             BusinessException e)
            throws ServletException, IOException {

        AppLogger.error("Shipment creation failed", e);

        session.setAttribute("errorNotification", e.getMessage());

        req.setAttribute("originWarehouseId", formData.originWarehouseId());
        req.setAttribute("destination", formData.destination());
        req.setAttribute("vendorId", formData.vendorId());
        req.setAttribute("carrierId", formData.carrierId());
        req.setAttribute("status", formData.status());
        req.setAttribute("scheduledDate", formData.scheduledDate());
        req.setAttribute("estimatedDeliveryDate", formData.estimatedDeliveryDate());
        req.setAttribute("plannedRoute", formData.plannedRoute());
        req.setAttribute("distanceKm", formData.distanceKm());
        req.setAttribute("selectedOrderIds", formData.orderIds());

        try {
            req.setAttribute("warehouses", warehouseService.findAllWarehouses());
            req.setAttribute("vendors", vendorService.findAllVendors());
            req.setAttribute("carriers", carrierService.findAllCarriers());

            // Reload pending orders
            List<CustomerOrder> pendingOrders = customerOrderService.getPendingOrders();
            if (formData.originWarehouseId() != null && !formData.originWarehouseId().isEmpty()) {
                try {
                    Integer warehouseId = Integer.parseInt(formData.originWarehouseId());
                    req.setAttribute("pendingOrders", filterOrdersByWarehouse(pendingOrders, warehouseId));
                } catch (NumberFormatException ex) {
                    req.setAttribute("pendingOrders", pendingOrders);
                }
            } else {
                req.setAttribute("pendingOrders", pendingOrders);
            }

        } catch (Exception ex) {
            AppLogger.error("Failed to reload data", ex);
        }

        req.getRequestDispatcher("/shipments/shipment-create.jsp")
                .forward(req, resp);
    }

    // ===== RECORD CLASS FOR FORM DATA =====
    private record ShipmentFormData(
            String originWarehouseId,
            String destination,
            String vendorId,
            String carrierId,
            String status,
            String scheduledDate,
            String estimatedDeliveryDate,
            String plannedRoute,
            String distanceKm,
            List<Integer> orderIds
    ) {}
}