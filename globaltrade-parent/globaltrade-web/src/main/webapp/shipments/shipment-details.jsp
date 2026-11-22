<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/23/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="shipment" type=""--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Shipment Details</title>

    <!-- Favicon -->
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/images/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">

    <!-- Tailwind CSS Output -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/output.css">
</head>
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
        <div class="p-4 sm:p-6 lg:p-8 max-w-4xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/shipments" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Shipment Details</h1>
                    <p class="text-white/30 text-sm font-light">View shipment information</p>
                </div>
                <div class="ml-auto flex items-center gap-2">
                    <button onclick="showStatusModal(${shipment.shipmentId}, '${shipment.trackingNumber}', '${shipment.status}')"
                            class="px-4 py-2 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all flex items-center gap-2">
                        <i class="fas fa-pen"></i>
                        <span>Update Status</span>
                    </button>
                </div>
            </div>

            <!-- ===== SHIPMENT INFO ===== -->
            <div class="dashboard-card mb-6">
                <div class="flex items-start justify-between mb-4">
                    <h2 class="text-white/60 text-sm font-semibold">Shipment Information</h2>
                    <c:choose>
                        <c:when test="${shipment.status == 'DELIVERED'}">
                            <span class="transaction-status completed">Delivered</span>
                        </c:when>
                        <c:when test="${shipment.status == 'IN_TRANSIT'}">
                            <span class="transaction-status pending">In Transit</span>
                        </c:when>
                        <c:when test="${shipment.status == 'DELAYED'}">
                            <span class="transaction-status failed">Delayed</span>
                        </c:when>
                        <c:otherwise>
                            <span class="transaction-status pending">${shipment.status}</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="text-white/30 text-xs block">Tracking Number</label>
                        <div class="text-white/80 text-base font-medium">${shipment.trackingNumber}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Origin Warehouse</label>
                        <div class="text-white/80 text-base font-medium">${shipment.originWarehouse.name}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Destination</label>
                        <div class="text-white/80 text-base font-medium">${shipment.destination}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Vendor</label>
                        <div class="text-white/80 text-base font-medium">${shipment.vendor.vendorName}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Carrier</label>
                        <div class="text-white/80 text-base font-medium">${shipment.carrier != null ? shipment.carrier.carrierName : 'N/A'}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Scheduled Date</label>
                        <div class="text-white/80 text-base font-medium">
                            ${shipment.scheduledDate != null ? shipment.scheduledDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Estimated Delivery</label>
                        <div class="text-white/80 text-base font-medium">
                            ${shipment.estimatedDeliveryDate != null ? shipment.estimatedDeliveryDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Actual Delivery</label>
                        <div class="text-white/80 text-base font-medium">
                            ${shipment.actualDeliveryDate != null ? shipment.actualDeliveryDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Created By</label>
                        <div class="text-white/80 text-base font-medium">${shipment.createdBy.fullName}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Created At</label>
                        <div class="text-white/80 text-base font-medium">
                            ${shipment.createdAt != null ? shipment.createdAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                        </div>
                    </div>
                </div>
            </div>

            <!-- ===== ROUTE INFORMATION ===== -->
            <div class="dashboard-card mb-6">
                <h2 class="text-white/60 text-sm font-semibold mb-4">Route Information</h2>

                <c:choose>
                    <c:when test="${not empty routePlan}">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label class="text-white/30 text-xs block">Planned Route</label>
                                <div class="text-white/70 text-sm">${routePlan.plannedRoute}</div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Optimized Route</label>
                                <div class="text-white/70 text-sm">
                                    <c:choose>
                                        <c:when test="${not empty routePlan.optimizedRoute}">
                                            ${routePlan.optimizedRoute}
                                            <span class="text-green-400 text-xs ml-2">✓ Optimized</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-white/30 text-sm">Not optimized yet</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Distance</label>
                                <div class="text-white/80 text-base font-medium">
                                        ${routePlan.distanceKm != null ? routePlan.distanceKm : 'N/A'} km
                                </div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Estimated Time</label>
                                <div class="text-white/80 text-base font-medium">
                                        ${routePlan.estimatedTimeHours != null ? routePlan.estimatedTimeHours : 'N/A'} hours
                                </div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Status</label>
                                <div>
                                    <c:choose>
                                        <c:when test="${not empty routePlan.optimizedRoute}">
                                <span class="text-green-400 text-xs font-semibold px-3 py-1 bg-green-500/10 rounded-full border border-green-500/20">
                                    <i class="fas fa-check-circle mr-1"></i>Optimized
                                </span>
                                        </c:when>
                                        <c:otherwise>
                                <span class="text-yellow-400 text-xs font-semibold px-3 py-1 bg-yellow-500/10 rounded-full border border-yellow-500/20">
                                    <i class="fas fa-clock mr-1"></i>Planned
                                </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Calculated</label>
                                <div class="text-white/80 text-sm">
                                        ${routePlan.calculatedAt != null ? routePlan.calculatedAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                </div>
                            </div>
                        </div>

                        <!-- ===== ROUTE INFORMATION - Optimize Button ===== -->
                        <div class="mt-4 pt-4 border-t border-white/5 flex gap-2 flex-wrap">
                            <c:choose>
                                <c:when test="${shipment.status == 'DELIVERED' || shipment.status == 'CANCELLED'}">
                                    <button disabled
                                            class="px-4 py-2 bg-white/5 border border-white/10 rounded-lg text-white/20 text-sm font-medium cursor-not-allowed flex items-center gap-2">
                                        <i class="fas fa-magic"></i>
                                        <span>Optimize Route</span>
                                    </button>
                                    <span class="text-white/20 text-xs self-center">
                                        <i class="fas fa-info-circle mr-1"></i>
                                        Cannot optimize ${shipment.status.toLowerCase()} shipments
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <!-- CHANGE THIS: Make it a link to route-plan with shipmentId -->
                                    <a href="${pageContext.request.contextPath}/route-plan?shipmentId=${shipment.shipmentId}"
                                       class="px-4 py-2 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-sm font-medium hover:bg-green-500/30 transition-all flex items-center gap-2">
                                        <i class="fas fa-magic"></i>
                                        <span>Optimize Route</span>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                            <a href="${pageContext.request.contextPath}/route-plan/history?shipmentId=${shipment.shipmentId}"
                               class="px-4 py-2 bg-white/5 border border-white/10 rounded-lg text-white/40 text-sm hover:bg-white/10 transition-all flex items-center gap-2">
                                <i class="fas fa-history"></i>
                                <span>View History</span>
                            </a>
                        </div>

                    </c:when>
                    <c:otherwise>
                        <div class="text-white/20 text-center py-4">
                            <span class="text-sm">No route information available for this shipment</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- ===== SHIPMENT ITEMS ===== -->
            <div class="dashboard-card">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                        <i class="fas fa-boxes text-dracula-red"></i>
                        Shipment Items
                    </h3>
                </div>

                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>SKU</th>
                            <th>Item Name</th>
                            <th>Quantity</th>
                            <th>Unit Price</th>
                            <th>Total</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty shipmentItems}">
                                <c:forEach var="item" items="${shipmentItems}">
                                    <tr>
                                        <td class="text-white/60 text-xs font-medium">${item.item.sku}</td>
                                        <td class="text-white/50 text-xs">${item.item.name}</td>
                                        <td class="text-white/60 text-xs">${item.quantity}</td>
                                        <td class="text-white/60 text-xs">LKR ${item.item.unitPrice}</td>
                                        <td class="text-white/60 text-xs font-medium">LKR ${item.quantity * item.item.unitPrice}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center text-white/20 py-4">
                                        <span class="text-xs">No items in this shipment</span>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>

    <jsp:include page="/shipments/shipment-status-update.jsp" />
    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/shipments.js"></script>
</body>
</html>