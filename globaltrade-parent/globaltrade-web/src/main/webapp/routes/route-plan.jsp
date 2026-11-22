<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/24/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%--@elvariable id="routePlan" type="lk.jiat.global.entity.RoutePlan"--%>
<%--@elvariable id="shipments" type="java.util.List"--%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Route Plan</title>

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
        <div class="p-4 sm:p-6 lg:p-8 max-w-4xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/shipments" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Route Plan</h1>
                    <p class="text-white/30 text-sm font-light">View route planning information</p>
                </div>
            </div>

            <!-- ===== SEARCH / FILTER ===== -->
            <div class="dashboard-card mb-6">
                <form id="routePlanForm" method="GET" action="${pageContext.request.contextPath}/route-plan">
                    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        <div class="form-group">
                            <label for="shipmentSelect" class="text-white/40 text-xs font-medium block mb-1.5">Select Shipment</label>
                            <div class="select-wrapper">
                                <select id="shipmentSelect" name="shipmentId" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                    <option value="" class="bg-black text-white/40">Select Shipment</option>
                                    <c:forEach var="s" items="${shipments}">
                                        <option value="${s.shipmentId}" class="bg-black" ${param.shipmentId == s.shipmentId ? 'selected' : ''}>
                                                ${s.trackingNumber} - ${s.destination}
                                        </option>
                                    </c:forEach>
                                </select>
                                <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                    <i class="fas fa-chevron-down text-xs"></i>
                                </span>
                            </div>
                        </div>
                        <div class="form-group flex items-end">
                            <button type="submit" id="viewRouteBtn" class="w-full px-4 py-2.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all">
                                <i class="fas fa-route mr-1.5"></i>View Route
                            </button>
                        </div>
                        <div class="form-group flex items-end">
                            <c:set var="carrierServiceType" value="${shipment != null && shipment.carrier != null ? shipment.carrier.serviceType : 'Standard'}" />
                            <button type="button" id="optimizeRouteBtn"
                                    onclick="openOptimizeRouteModal('${param.shipmentId}', '${routePlan != null ? routePlan.routeId : ''}', '${routePlan != null ? routePlan.distanceKm : ''}', '${carrierServiceType}')"
                                    class="w-full px-4 py-2.5 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-sm font-medium hover:bg-green-500/30 transition-all">
                                <i class="fas fa-magic mr-1.5"></i>Optimize Route
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- ===== ROUTE PLAN DETAILS ===== -->
            <div class="dashboard-card">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                        <i class="fas fa-route text-dracula-red"></i>
                        Route Details
                    </h2>
                    <span class="text-white/30 text-xs">
                        Calculated: ${routePlan != null ? routePlan.calculatedAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                    </span>
                </div>

                <c:choose>
                    <c:when test="${not empty routePlan}">
                        <!-- Shipment Info -->
                        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                            <div>
                                <label class="text-white/30 text-xs block">Tracking Number</label>
                                <div class="text-white/80 text-base font-medium">${routePlan.shipment.trackingNumber}</div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Distance</label>
                                <div class="text-white/80 text-base font-medium">
                                        ${routePlan.distanceKm != null ? routePlan.distanceKm : 'N/A'} km
                                    <c:if test="${not empty routePlan.optimizedRoute}">
                                        <span class="text-green-400 text-xs ml-2">(Optimized)</span>
                                    </c:if>
                                </div>
                            </div>
                            <div>
                                <label class="text-white/30 text-xs block">Estimated Time</label>
                                <div class="text-white/80 text-base font-medium">
                                        ${routePlan.estimatedTimeHours != null ? routePlan.estimatedTimeHours : 'N/A'} hours
                                    <c:if test="${not empty routePlan.optimizedRoute}">
                                        <span class="text-green-400 text-xs ml-2">(Optimized)</span>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <!-- Status Badge -->
                        <div class="mb-4 flex items-center gap-2">
                            <c:if test="${not empty routePlan.optimizedRoute}">
                                <span class="text-green-400 text-xs font-semibold px-3 py-1 bg-green-500/10 rounded-full border border-green-500/20">
                                    <i class="fas fa-check-circle mr-1"></i>Optimized
                                </span>
                            </c:if>
                            <c:if test="${empty routePlan.optimizedRoute}">
                                <span class="text-yellow-400 text-xs font-semibold px-3 py-1 bg-yellow-500/10 rounded-full border border-yellow-500/20">
                                    <i class="fas fa-clock mr-1"></i>Planned
                                </span>
                            </c:if>
                        </div>

                        <!-- Planned Route -->
                        <div class="mb-4">
                            <h3 class="text-white/50 text-xs font-semibold uppercase tracking-wider mb-2">Planned Route</h3>
                            <div class="bg-white/5 border border-white/10 rounded-lg p-4">
                                <p class="text-white/70 text-sm whitespace-pre-wrap">${routePlan.plannedRoute}</p>
                            </div>
                        </div>

                        <!-- Optimized Route (if exists) -->
                        <c:if test="${not empty routePlan.optimizedRoute}">
                            <div>
                                <h3 class="text-white/50 text-xs font-semibold uppercase tracking-wider mb-2 flex items-center gap-2">
                                    <i class="fas fa-check-circle text-green-400"></i>
                                    Optimized Route
                                </h3>
                                <div class="bg-green-500/5 border border-green-500/20 rounded-lg p-4">
                                    <p class="text-white/70 text-sm whitespace-pre-wrap">${routePlan.optimizedRoute}</p>
                                    <div class="mt-3 pt-3 border-t border-green-500/10 flex items-center gap-4">
                                        <span class="text-green-400 text-xs font-medium">
                                            <i class="fas fa-arrow-down mr-1"></i>
                                            ${routePlan.distanceKm != null ? routePlan.distanceKm : 'N/A'} km
                                        </span>
                                        <span class="text-green-400 text-xs font-medium">
                                            <i class="fas fa-clock mr-1"></i>
                                            ${routePlan.estimatedTimeHours != null ? routePlan.estimatedTimeHours : 'N/A'} hours
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- History Link -->
                        <div class="mt-4 pt-4 border-t border-white/5">
                            <a href="${pageContext.request.contextPath}/route-plan/history?shipmentId=${routePlan.shipment.shipmentId}"
                               class="text-white/30 text-xs hover:text-white/60 transition-all">
                                <i class="fas fa-history mr-1"></i>View Route History
                            </a>
                        </div>

                    </c:when>
                    <c:otherwise>
                        <div class="text-center text-white/20 py-8">
                            <div class="flex flex-col items-center gap-2">
                                <i class="fas fa-route text-3xl text-white/10"></i>
                                <span class="text-sm">No route plan found</span>
                                <c:if test="${not empty param.shipmentId}">
                                    <span class="text-xs text-white/10">Select a different shipment or create a route plan for this shipment</span>
                                </c:if>
                                <c:if test="${empty param.shipmentId}">
                                    <span class="text-xs text-white/10">Select a shipment and click "View Route" to see route details</span>
                                </c:if>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </main>

    <!-- ===== OPTIMIZE ROUTE MODAL ===== -->
    <div id="optimizeRouteModal" class="status-modal-overlay hidden">
        <div class="status-modal-content">
            <div class="status-modal-header">
                <h3>Optimize Route</h3>
                <button class="close-btn" onclick="closeOptimizeRouteModal()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <p class="status-modal-subtitle">Enter the optimized route details for this shipment</p>

            <form id="optimizeRouteForm" action="${pageContext.request.contextPath}/route-plan" method="POST">
                <input type="hidden" name="action" value="optimize">
                <input type="hidden" name="shipmentId" id="optimizeShipmentId" value="${param.shipmentId}">
                <input type="hidden" name="routePlanId" id="optimizeRoutePlanId" value="">
                <input type="hidden" name="serviceType" id="optimizeServiceType" value="${carrierServiceType}">

                <div class="status-modal-body">
                    <!-- Current Route Info -->
                    <div class="form-group">
                        <label class="text-white/40 text-xs font-medium block mb-1.5">Current Planned Route</label>
                        <div class="current-status-display text-white/60 text-sm p-2 bg-white/5 rounded-lg border border-white/10">
                            ${routePlan != null ? routePlan.plannedRoute : 'N/A'}
                        </div>
                    </div>

                    <!-- Optimized Route -->
                    <div class="form-group">
                        <label for="optimizedRoute" class="text-white/40 text-xs font-medium block mb-1.5">Optimized Route <span class="text-dracula-red">*</span></label>
                        <textarea id="optimizedRoute" name="optimizedRoute" rows="3"
                                  placeholder="Enter the optimized route description (e.g., Colombo to Singapore via optimized sea route with fewer stops)"
                                  class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required></textarea>
                    </div>

                    <!-- Distance -->
                    <div class="form-group">
                        <label for="optimizeDistanceKm" class="text-white/40 text-xs font-medium block mb-1.5">Distance (km) <span class="text-dracula-red">*</span></label>
                        <input type="number" id="optimizeDistanceKm" name="distanceKm"
                               placeholder="e.g., 1234.56" step="0.01" min="0"
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all" required>
                    </div>

                    <!-- Estimated Time (auto-calculated display) -->
                    <div class="form-group">
                        <label class="text-white/40 text-xs font-medium block mb-1.5">Estimated Time (hours)</label>
                        <div class="text-white/60 text-sm p-2 bg-white/5 rounded-lg border border-white/10">
                            <span id="optimizeEstimatedTime">--</span> hours
                        </div>
                    </div>
                </div>

                <div class="status-modal-actions">
                    <button type="button" class="btn-cancel" onclick="closeOptimizeRouteModal()">Cancel</button>
                    <button type="submit" class="btn-update" id="optimizeRouteSubmitBtn">
                        <i class="fas fa-magic mr-1.5"></i> Optimize Route
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/routes.js"></script>
</body>
</html>