<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 7/31/2026
  Time: 11:59 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="stats" type=""--%>
<%--@elvariable id="vendorPerformances" type=""--%>
<%--@elvariable id="recentShipments" type=""--%>
<%
    String fullName = (String) session.getAttribute("fullName");
    String role = (String) session.getAttribute("role");
    if (fullName == null) fullName = "User";
    if (role == null) role = "Guest";
%>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Dashboard</title>

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

<!-- ===== SESSION NOTIFICATION HIDDEN INPUTS ===== -->
<c:if test="${not empty sessionScope.errorNotification}">
    <input type="hidden" id="sessionErrorMsg" value="${sessionScope.errorNotification}">
    <c:remove var="errorNotification" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.successNotification}">
    <input type="hidden" id="sessionSuccessMsg" value="${sessionScope.successNotification}">
    <c:remove var="successNotification" scope="session" />
</c:if>

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
        <div class="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto">

            <!-- Welcome Section -->
            <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-6">
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Welcome back, <%= fullName %> 🚢</h1>
                    <p class="text-white/30 text-sm font-light">Role: <%= role.replace("_", " ") %></p>
                    <c:if test="${not empty stats}">
                        <p class="text-white/20 text-xs mt-1">
                            <i class="fas fa-clock mr-1"></i>
                            Avg. Delivery: ${stats.avgDeliveryDays} days
                            <span class="mx-2">|</span>
                            <i class="fas fa-truck mr-1"></i>
                                ${stats.totalShipments} total shipments
                        </p>
                    </c:if>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export Report</span>
                    </button>
                    <a href="${pageContext.request.contextPath}/shipments/create" class="px-4 py-2 bg-gradient-to-r from-dracula-red to-red-900 rounded-lg text-white text-xs font-semibold hover:shadow-lg hover:shadow-red-500/25 transition-all flex items-center gap-2">
                        <i class="fas fa-plus"></i>
                        <span>New Shipment</span>
                    </a>
                </div>
            </div>

            <!-- ===== STATS CARDS ===== -->
            <section class="grid grid-cols-2 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                <div class="dashboard-card">
                    <div class="flex items-center justify-between">
                        <div>
                            <div class="balance-label text-[10px]">Total Shipments</div>
                            <div class="balance-amount text-2xl">${stats.totalShipments}</div>
                            <span class="text-[10px] ${stats.growthPercent >= 0 ? 'text-green-400' : 'text-red-400'} bg-${stats.growthPercent >= 0 ? 'green' : 'red'}-500/10 px-2 py-0.5 rounded-full inline-block mt-1">
                                ${stats.growthPercent >= 0 ? '+' : ''}${stats.growthPercent}%
                            </span>
                        </div>
                        <div class="w-10 h-10 rounded-lg bg-red-500/10 flex items-center justify-center text-dracula-red">
                            <i class="fas fa-truck"></i>
                        </div>
                    </div>
                </div>

                <div class="dashboard-card">
                    <div class="flex items-center justify-between">
                        <div>
                            <div class="balance-label text-[10px]">In Transit</div>
                            <div class="balance-amount text-2xl">${stats.inTransit}</div>
                            <span class="text-[10px] text-yellow-400 bg-yellow-500/10 px-2 py-0.5 rounded-full inline-block mt-1">On Track</span>
                        </div>
                        <div class="w-10 h-10 rounded-lg bg-red-500/10 flex items-center justify-center text-dracula-red">
                            <i class="fas fa-ship"></i>
                        </div>
                    </div>
                </div>

                <div class="dashboard-card">
                    <div class="flex items-center justify-between">
                        <div>
                            <div class="balance-label text-[10px]">Delayed Shipments</div>
                            <div class="balance-amount text-2xl text-red-400">${stats.delayed}</div>
                            <span class="text-[10px] text-red-400 bg-red-500/10 px-2 py-0.5 rounded-full inline-block mt-1">Action Required</span>
                        </div>
                        <div class="w-10 h-10 rounded-lg bg-red-500/10 flex items-center justify-center text-red-400">
                            <i class="fas fa-clock"></i>
                        </div>
                    </div>
                </div>

                <div class="dashboard-card">
                    <div class="flex items-center justify-between">
                        <div>
                            <div class="balance-label text-[10px]">Low Inventory Items</div>
                            <div class="balance-amount text-2xl">${stats.lowInventory}</div>
                            <span class="text-[10px] text-yellow-400 bg-yellow-500/10 px-2 py-0.5 rounded-full inline-block mt-1">Needs Reorder</span>
                        </div>
                        <div class="w-10 h-10 rounded-lg bg-red-500/10 flex items-center justify-center text-dracula-red">
                            <i class="fas fa-warehouse"></i>
                        </div>
                    </div>
                </div>
            </section>

            <!-- ===== RECENT SHIPMENTS ===== -->
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
                <div class="lg:col-span-2 dashboard-card">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                            <i class="fas fa-clock-rotate-left text-dracula-red"></i>
                            Recent Shipments
                        </h3>
                        <a href="${pageContext.request.contextPath}/shipments" class="text-dracula-red text-xs hover:text-red-400 transition-all">View All →</a>
                    </div>

                    <div class="overflow-x-auto">
                        <table class="transaction-table">
                            <thead>
                            <tr>
                                <th>Tracking #</th>
                                <th>Origin</th>
                                <th>Destination</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty recentShipments}">
                                    <c:forEach var="s" items="${recentShipments}">
                                        <tr>
                                            <td class="font-medium text-white/70 text-xs">
                                                <a href="${pageContext.request.contextPath}/shipments/details?shipmentId=${s.shipmentId}" class="hover:text-dracula-red transition-all">
                                                        ${s.trackingNumber}
                                                </a>
                                            </td>
                                            <td class="text-white/40 text-xs">${s.origin}</td>
                                            <td class="text-white/40 text-xs">${s.destination}</td>
                                            <td>
                                                <span class="transaction-status ${s.statusClass}">${s.statusDisplay}</span>
                                            </td>
                                            <td class="text-white/30 text-xs">
                                                    ${s.scheduledDate != null ? s.scheduledDate.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center text-white/20 py-4">
                                            <span class="text-xs">No shipments found</span>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Quick Actions -->
                <div class="dashboard-card">
                    <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2 mb-4">
                        <i class="fas fa-bolt text-dracula-red"></i>
                        Quick Actions
                    </h3>

                    <div class="space-y-2">
                        <a href="${pageContext.request.contextPath}/shipments/create" class="flex items-center gap-3 p-3 bg-white/5 rounded-lg border border-white/5 hover:border-white/10 hover:bg-white/10 transition-all">
                            <div class="w-8 h-8 rounded-lg bg-red-500/10 flex items-center justify-center text-dracula-red">
                                <i class="fas fa-plus"></i>
                            </div>
                            <div>
                                <div class="text-white/80 text-sm font-medium">Create Shipment</div>
                                <div class="text-white/30 text-[10px]">New shipment booking</div>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/inventory" class="flex items-center gap-3 p-3 bg-white/5 rounded-lg border border-white/5 hover:border-white/10 hover:bg-white/10 transition-all">
                            <div class="w-8 h-8 rounded-lg bg-red-500/10 flex items-center justify-center text-dracula-red">
                                <i class="fas fa-boxes"></i>
                            </div>
                            <div>
                                <div class="text-white/80 text-sm font-medium">Check Inventory</div>
                                <div class="text-white/30 text-[10px]">${stats.lowInventory} low stock items</div>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/vendors" class="flex items-center gap-3 p-3 bg-white/5 rounded-lg border border-white/5 hover:border-white/10 hover:bg-white/10 transition-all">
                            <div class="w-8 h-8 rounded-lg bg-red-500/10 flex items-center justify-center text-dracula-red">
                                <i class="fas fa-building"></i>
                            </div>
                            <div>
                                <div class="text-white/80 text-sm font-medium">Manage Vendors</div>
                                <div class="text-white/30 text-[10px]">View vendor performance</div>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/monitoring/alerts" class="flex items-center gap-3 p-3 bg-white/5 rounded-lg border border-white/5 hover:border-white/10 hover:bg-white/10 transition-all">
                            <div class="w-8 h-8 rounded-lg bg-red-500/10 flex items-center justify-center text-dracula-red">
                                <i class="fas fa-bell"></i>
                            </div>
                            <div>
                                <div class="text-white/80 text-sm font-medium">View Alerts</div>
                                <div class="text-white/30 text-[10px]">${pendingAlertCount} pending alerts</div>
                            </div>
                        </a>
                    </div>
                </div>
            </div>

            <!-- ===== VENDOR PERFORMANCE + NEWS ===== -->
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <!-- Vendor Performance -->
                <div class="lg:col-span-2 dashboard-card">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2">
                            <i class="fas fa-chart-line text-dracula-red"></i>
                            Top Performing Vendors
                        </h3>
                        <a href="${pageContext.request.contextPath}/vendors/performance" class="text-dracula-red text-xs hover:text-red-400 transition-all">View All →</a>
                    </div>

                    <div class="overflow-x-auto">
                        <table class="transaction-table">
                            <thead>
                            <tr>
                                <th>Vendor</th>
                                <th>Country</th>
                                <th>Rating</th>
                                <th>Status</th>
                                <th>Last Evaluation</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty vendorPerformances}">
                                    <c:forEach var="v" items="${vendorPerformances}">
                                        <tr>
                                            <td class="font-medium text-white/70 text-xs">${v.vendorName}</td>
                                            <td class="text-white/40 text-xs">${v.country}</td>
                                            <td class="${v.ratingColor} font-medium text-xs">
                                                    ${v.rating} ★
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${v.status == 'ACTIVE'}">
                                                        <span class="transaction-status completed">Active</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="transaction-status pending">${v.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-white/30 text-xs">${v.lastEvaluation}</td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center text-white/20 py-4">
                                            <span class="text-xs">No vendor performance data available</span>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- News & Updates -->
                <div class="dashboard-card">
                    <h3 class="text-white/60 text-sm font-semibold flex items-center gap-2 mb-4">
                        <i class="fas fa-newspaper text-dracula-red"></i>
                        Quick Stats
                    </h3>

                    <div class="space-y-3">
                        <div class="news-item">
                            <div class="icon"><i class="fas fa-check-circle text-green-400"></i></div>
                            <div>
                                <div class="title text-white/70 text-sm">Delivered</div>
                                <div class="desc text-white/30 text-xs">${stats.delivered} shipments delivered</div>
                            </div>
                        </div>

                        <div class="news-item">
                            <div class="icon"><i class="fas fa-clock text-yellow-400"></i></div>
                            <div>
                                <div class="title text-white/70 text-sm">Pending</div>
                                <div class="desc text-white/30 text-xs">${stats.totalShipments - stats.delivered - stats.delayed} shipments pending</div>
                            </div>
                        </div>

                        <div class="news-item">
                            <div class="icon"><i class="fas fa-truck-fast text-blue-400"></i></div>
                            <div>
                                <div class="title text-white/70 text-sm">In Transit</div>
                                <div class="desc text-white/30 text-xs">${stats.inTransit} shipments on the move</div>
                            </div>
                        </div>

                        <div class="news-item">
                            <div class="icon"><i class="fas fa-warehouse text-orange-400"></i></div>
                            <div>
                                <div class="title text-white/70 text-sm">Low Stock Items</div>
                                <div class="desc text-white/30 text-xs">${stats.lowInventory} items need reorder</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
</body>
</html>