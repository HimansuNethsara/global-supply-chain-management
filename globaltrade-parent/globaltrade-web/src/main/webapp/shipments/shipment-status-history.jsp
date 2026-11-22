<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/23/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Status History</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Status History</h1>
                    <p class="text-white/30 text-sm font-light">History for shipment <span class="text-white/60">${shipment.trackingNumber}</span></p>
                </div>
            </div>

            <!-- ===== SHIPMENT INFO ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                        <label class="text-white/30 text-xs block">Tracking Number</label>
                        <div class="text-white/80 text-sm font-medium">${shipment.trackingNumber}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Origin</label>
                        <div class="text-white/80 text-sm font-medium">${shipment.originWarehouse.name}</div>
                    </div>
                    <div>
                        <label class="text-white/30 text-xs block">Destination</label>
                        <div class="text-white/80 text-sm font-medium">${shipment.destination}</div>
                    </div>
                </div>
            </div>

            <!-- ===== STATUS HISTORY TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <h3 class="text-white/60 text-sm font-semibold mb-4 flex items-center gap-2">
                    <i class="fas fa-clock-rotate-left text-dracula-red"></i>
                    Status History
                </h3>

                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Old Status</th>
                            <th>New Status</th>
                            <th>Changed By</th>
                            <th>Changed At</th>
                            <th>Remarks</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty statusHistory}">
                                <c:forEach var="h" items="${statusHistory}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${h.oldStatus != null}">
                                                    <span class="px-2 py-0.5 bg-white/5 rounded-full text-[10px] text-white/40">${h.oldStatus}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-white/20 text-[10px]">—</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${h.newStatus == 'DELIVERED'}">
                                                    <span class="transaction-status completed">Delivered</span>
                                                </c:when>
                                                <c:when test="${h.newStatus == 'IN_TRANSIT'}">
                                                    <span class="transaction-status pending">In Transit</span>
                                                </c:when>
                                                <c:when test="${h.newStatus == 'DELAYED'}">
                                                    <span class="transaction-status failed">Delayed</span>
                                                </c:when>
                                                <c:when test="${h.newStatus == 'CANCELLED'}">
                                                    <span class="transaction-status failed">Cancelled</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">${h.newStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/50 text-xs">${h.changedBy != null ? h.changedBy.fullName : 'N/A'}</td>
                                        <td class="text-white/30 text-xs">
                                                ${h.changedAt != null ? h.changedAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td class="text-white/40 text-xs max-w-[150px] truncate" title="${h.remarks}">
                                                ${h.remarks != null ? h.remarks : '—'}
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-clock text-3xl text-white/10"></i>
                                            <span class="text-sm">No status history found</span>
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
<script src="${pageContext.request.contextPath}/js/shipments.js"></script>
</body>
</html>