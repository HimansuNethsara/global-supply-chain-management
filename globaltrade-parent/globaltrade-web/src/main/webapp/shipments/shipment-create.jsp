<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/23/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="estimatedDeliveryDate" type=""--%>
<%--@elvariable id="scheduledDate" type=""--%>
<%--@elvariable id="destination" type=""--%>
<%--@elvariable id="distanceKm" type=""--%>
<%--@elvariable id="plannedRoute" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Create Shipment</title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

    <!-- Tailwind CSS Output -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/output.css">
</head>

<!-- ===== SESSION NOTIFICATIONS ===== -->
<c:if test="${not empty sessionScope.errorNotification}">
    <input type="hidden" id="sessionErrorMsg" value="${sessionScope.errorNotification}">
    <c:remove var="errorNotification" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.successNotification}">
    <input type="hidden" id="sessionSuccessMsg" value="${sessionScope.successNotification}">
    <c:remove var="successNotification" scope="session" />
</c:if>

<body class="dashboard-container font-inter">

<!-- Background Glow Orbs -->
<div class="fixed inset-0 z-0 pointer-events-none overflow-hidden">
    <div class="glow-orb w-[600px] h-[600px] -top-[200px] -right-[200px] bg-red-500/5"></div>
    <div class="glow-orb w-[400px] h-[400px] -bottom-[100px] -left-[100px] bg-red-900/5"></div>
    <div class="glow-orb w-[300px] h-[300px] top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-500/3"></div>
</div>

<!-- ===== HEADER ===== -->
<jsp:include page="/WEB-INF/views/header.jsp" />

<!-- ===== SIDEBAR ===== -->
<jsp:include page="/WEB-INF/views/sidebar.jsp" />

<!-- ===== MAIN CONTENT WRAPPER ===== -->
<div class="main-content-wrapper">
    <main class="main-content relative z-10">
        <div class="p-4 sm:p-6 lg:p-8 max-w-3xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/shipments" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Create Shipment</h1>
                    <p class="text-white/30 text-sm font-light">Create a new shipment</p>
                </div>
            </div>

            <!-- ===== CREATE FORM ===== -->
            <div class="dashboard-card">
                <form id="shipmentCreateForm" action="${pageContext.request.contextPath}/shipments/create" method="POST">
                    <input type="hidden" name="csrfToken" value="placeholder_token">

                    <!-- Row 1: Tracking Number (Auto-generated) + Origin Warehouse -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="trackingNumber" class="text-white/40 text-xs font-medium block mb-1.5">
                                Tracking Number <span class="text-dracula-red">*</span>
                                <span class="text-white/20 text-[10px] font-light ml-1">(Auto-generated)</span>
                            </label>
                            <div class="relative">
                                <input type="text" id="trackingNumber" name="trackingNumber"
                                       placeholder="Will be auto-generated"
                                       class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/60 text-sm cursor-not-allowed"
                                       readonly>
                                <button type="button" id="regenerateTrackingBtn"
                                        class="absolute right-2 top-1/2 -translate-y-1/2 text-white/30 hover:text-dracula-red transition-all text-xs px-2 py-1 rounded hover:bg-white/5"
                                        title="Regenerate tracking number">
                                    <i class="fas fa-rotate"></i>
                                </button>
                            </div>
                            <div class="text-[8px] sm:text-[9px] text-white/20 font-light mt-0.5">
                                Format: WAREHOUSE-VENDOR-CARRIER-TIMESTAMP
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="originWarehouseId" class="text-white/40 text-xs font-medium block mb-1.5">
                                Origin Warehouse <span class="text-dracula-red">*</span>
                            </label>
                            <div class="select-wrapper">
                                <select id="originWarehouseId" name="originWarehouseId"
                                        class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer"
                                        required onchange="updateTrackingNumber()">
                                    <option value="" class="bg-black text-white/40" disabled selected>Select Warehouse</option>
                                    <c:forEach var="w" items="${warehouses}">
                                        <option value="${w.warehouseId}" class="bg-black" ${w.warehouseId == preSelectedWarehouse ? 'selected' : ''}>
                                                ${w.name} - ${w.location}
                                        </option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                <i class="fas fa-chevron-down text-xs"></i>
            </span>
                            </div>
                        </div>
                    </div>

                    <!-- Row 2: Destination + Vendor -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="destination" class="text-white/40 text-xs font-medium block mb-1.5">Destination <span class="text-dracula-red">*</span></label>
                            <input type="text" id="destination" name="destination" placeholder="Enter destination"
                                   value="${param.destination != null ? param.destination : destination != null ? destination : ''}"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required>
                        </div>
                        <div class="form-group">
                            <label for="vendorId" class="text-white/40 text-xs font-medium block mb-1.5">Vendor <span class="text-dracula-red">*</span></label>
                            <div class="select-wrapper">
                                <select id="vendorId" name="vendorId"
                                        class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer"
                                        required onchange="updateTrackingNumber()">
                                    <option value="" class="bg-black text-white/40" disabled selected>Select Vendor</option>
                                    <c:forEach var="v" items="${vendors}">
                                        <option value="${v.vendorId}" class="bg-black" ${v.vendorId == preSelectedVendor ? 'selected' : ''}>
                                                ${v.vendorName}
                                        </option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Row 3: Carrier + Status -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="carrierId" class="text-white/40 text-xs font-medium block mb-1.5">Carrier <span class="text-dracula-red">*</span></label>
                            <div class="select-wrapper">
                                <select id="carrierId" name="carrierId" required
                                        class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer"
                                        onchange="updateTrackingNumber()">
                                    <option value="" class="bg-black text-white/40" disabled selected>Select Carrier</option>
                                    <c:forEach var="c" items="${carriers}">
                                        <option value="${c.carrierId}" class="bg-black"
                                                data-service-type="${c.serviceType != null ? c.serviceType : 'Standard'}"
                                            ${c.carrierId == preSelectedCarrier ? 'selected' : ''}>
                                                ${c.carrierName} ${c.serviceType != null ? '(' += c.serviceType += ')' : ''}
                                        </option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="status" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                            <div class="select-wrapper">
                                <select id="status" name="status"
                                        class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                    <option value="PENDING" class="bg-black" selected>Pending</option>
                                    <option value="IN_TRANSIT" class="bg-black">In Transit</option>
                                    <option value="DELIVERED" class="bg-black">Delivered</option>
                                    <option value="DELAYED" class="bg-black">Delayed</option>
                                    <option value="CANCELLED" class="bg-black">Cancelled</option>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Row 3.5: Attach Customer Orders -->
                    <div class="form-group border-t border-white/5 pt-4 mt-2">
                        <label class="text-white/40 text-xs font-medium block mb-2">
                            Attach Customer Orders
                            <span class="text-white/20 text-[10px] font-light">(Optional - Pending orders only)</span>
                        </label>

                        <div class="bg-white/5 border border-white/10 rounded-lg p-3">
                            <div class="text-white/30 text-xs mb-2" id="orderHelpText">
                                <i class="fas fa-info-circle mr-1"></i>
                                Select warehouse first to see eligible orders
                            </div>

                            <div id="orderSelectContainer">
                                <c:if test="${not empty pendingOrders}">
                                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-2 max-h-40 overflow-y-auto bg-white/5 rounded-lg p-2 border border-white/5">
                                        <c:forEach var="order" items="${pendingOrders}">
                                            <label class="flex items-start gap-2 p-2 rounded hover:bg-white/5 cursor-pointer transition-all">
                                                <input type="checkbox" name="orderIds" value="${order.orderId}"
                                                       class="mt-1 accent-dracula-red order-checkbox"
                                                       data-order-id="${order.orderId}"
                                                       data-items="${order.itemCount}">
                                                <div>
                                                    <div class="text-white/70 text-xs font-medium">Order #${order.orderId}</div>
                                                    <div class="text-white/30 text-[10px]">${order.destination}</div>
                                                    <div class="text-white/20 text-[9px]">${order.itemCount} item(s)</div>
                                                </div>
                                            </label>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${empty pendingOrders}">
                                    <div class="text-white/20 text-xs py-2 text-center">
                                        No pending orders available
                                    </div>
                                </c:if>
                            </div>

                            <div class="mt-2 flex items-center gap-3">
                                <button type="button" id="selectAllOrders" class="text-white/20 text-[10px] hover:text-white/40 transition-all">
                                    <i class="fas fa-check-double mr-1"></i>Select All
                                </button>
                                <button type="button" id="deselectAllOrders" class="text-white/20 text-[10px] hover:text-white/40 transition-all">
                                    <i class="fas fa-times mr-1"></i>Deselect All
                                </button>
                                <span class="text-white/20 text-[9px]" id="selectedOrdersCount">Selected: 0 orders</span>
                            </div>
                        </div>
                    </div>

                    <!-- Row 4: Scheduled Date + Estimated Delivery -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="scheduledDate" class="text-white/40 text-xs font-medium block mb-1.5">Scheduled Date <span class="text-dracula-red">*</span></label>
                            <input type="datetime-local" id="scheduledDate" name="scheduledDate"
                                   value="${param.scheduledDate != null ? param.scheduledDate : scheduledDate != null ? scheduledDate : ''}"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all" required>
                        </div>
                        <div class="form-group">
                            <label for="estimatedDeliveryDate" class="text-white/40 text-xs font-medium block mb-1.5">
                                Estimated Delivery <span class="text-white/20 text-[10px]">(Auto-calculated)</span>
                            </label>
                            <input type="datetime-local" id="estimatedDeliveryDate" name="estimatedDeliveryDate"
                                   readonly
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/60 text-sm cursor-not-allowed">
                        </div>
                    </div>

                    <!-- Row 5: Planned Route + Distance -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="plannedRoute" class="text-white/40 text-xs font-medium block mb-1.5">Planned Route</label>
                            <input type="text" id="plannedRoute" name="plannedRoute" placeholder="Enter planned route description"
                                   value="${param.plannedRoute != null ? param.plannedRoute : plannedRoute != null ? plannedRoute : ''}"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                            <div class="text-[8px] sm:text-[9px] text-white/20 font-light mt-0.5">
                                Route description for the shipment (e.g., Colombo to Singapore via sea route)
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="distanceKm" class="text-white/40 text-xs font-medium block mb-1.5">Distance (km)</label>
                            <input type="number" id="distanceKm" name="distanceKm" placeholder="e.g., 1234.56" step="0.01" min="0"
                                   value="${param.distanceKm != null ? param.distanceKm : distanceKm != null ? distanceKm : ''}"
                                   class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                            <div class="text-[8px] sm:text-[9px] text-white/20 font-light mt-0.5">
                                Distance in kilometers between origin and destination
                            </div>
                        </div>
                    </div>

                    <!-- Buttons -->
                    <div class="flex items-center gap-3 pt-3 border-t border-white/5 mt-4">
                        <button type="submit" id="submitBtn" class="px-6 py-2.5 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-sm font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all">
                            <i class="fas fa-plus mr-1.5"></i>Create Shipment
                        </button>
                        <button type="reset" id="resetBtn" class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/40 text-sm hover:bg-white/10 hover:text-white/60 transition-all">
                            <i class="fas fa-undo mr-1.5"></i>Reset
                        </button>
                        <a href="${pageContext.request.contextPath}/shipments" class="px-6 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white/30 text-sm hover:bg-white/10 hover:text-white/50 transition-all">
                            Cancel
                        </a>
                    </div>

                </form>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/shipments.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // ===== SELECT ALL ORDERS =====
        document.getElementById('selectAllOrders')?.addEventListener('click', function() {
            document.querySelectorAll('.order-checkbox').forEach(function(cb) {
                cb.checked = true;
            });
            updateSelectedCount();
        });

        // ===== DESELECT ALL ORDERS =====
        document.getElementById('deselectAllOrders')?.addEventListener('click', function() {
            document.querySelectorAll('.order-checkbox').forEach(function(cb) {
                cb.checked = false;
            });
            updateSelectedCount();
        });

        // ===== UPDATE SELECTED COUNT =====
        function updateSelectedCount() {
            var checked = document.querySelectorAll('.order-checkbox:checked').length;
            var countDisplay = document.getElementById('selectedOrdersCount');
            if (countDisplay) {
                countDisplay.textContent = 'Selected: ' + checked + ' order' + (checked !== 1 ? 's' : '');
            }
        }

        // Update count on checkbox change
        document.querySelectorAll('.order-checkbox').forEach(function(cb) {
            cb.addEventListener('change', updateSelectedCount);
        });

        // Initial count update
        updateSelectedCount();

        // ===== FILTER ORDERS BY WAREHOUSE =====
        var warehouseSelect = document.getElementById('originWarehouseId');
        if (warehouseSelect) {
            warehouseSelect.addEventListener('change', function() {
                var warehouseId = this.value;
                if (warehouseId) {
                    // Redirect to reload with warehouse filter
                    var currentUrl = new URL(window.location.href);
                    currentUrl.searchParams.set('warehouseId', warehouseId);
                    window.location.href = currentUrl.toString();
                }
            });
        }
    });
</script>
</body>
</html>