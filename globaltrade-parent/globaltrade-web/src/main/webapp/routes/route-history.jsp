<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/25/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Route History - GlobalTrade Logistics</title>

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
        <div class="p-4 sm:p-6 lg:p-8 max-w-5xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex items-center gap-3 mb-6">
                <a href="${pageContext.request.contextPath}/route-plan?shipmentId=${shipment.shipmentId}" class="text-white/30 hover:text-white/60 transition-all">
                    <i class="fas fa-arrow-left"></i>
                </a>
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Route History</h1>
                    <p class="text-white/30 text-sm font-light">
                        Route history for shipment <span class="text-white/60">${shipment.trackingNumber}</span>
                    </p>
                </div>
            </div>

            <!-- ===== HISTORY TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Type</th>
                            <th>Distance</th>
                            <th>Est. Time</th>
                            <th>Status</th>
                            <th>Calculated At</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty history}">
                                <c:forEach var="r" items="${history}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty r.optimizedRoute}">
                                                    <span class="text-green-400 text-xs font-medium">
                                                        <i class="fas fa-magic mr-1"></i>Optimized
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-yellow-400 text-xs font-medium">
                                                        <i class="fas fa-road mr-1"></i>Planned
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/60 text-xs">${r.distanceKm != null ? r.distanceKm : 'N/A'} km</td>
                                        <td class="text-white/60 text-xs">${r.estimatedTimeHours != null ? r.estimatedTimeHours : 'N/A'} hrs</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty r.optimizedRoute}">
                                                    <span class="transaction-status completed">Optimized</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">Planned</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${r.calculatedAt != null ? r.calculatedAt.toString().replace('T', ' ') : 'N/A'}
                                        </td>
                                        <td>
                                            <button onclick="viewRoutePlan(${r.routeId})"
                                                    class="text-blue-400/60 hover:text-blue-400 text-xs transition-all">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-route text-3xl text-white/10"></i>
                                            <span class="text-sm">No route history found</span>
                                        </div>
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

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/routes.js"></script>
</body>
</html>