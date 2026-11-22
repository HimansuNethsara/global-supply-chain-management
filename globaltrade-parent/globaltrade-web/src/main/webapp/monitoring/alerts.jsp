<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/26/2026
  Time: 12:00 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en" data-theme="dracula">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GlobalTrade Logistics - Alerts</title>

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
        <div class="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto">

            <!-- ===== PAGE HEADER ===== -->
            <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-6">
                <div>
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Alerts</h1>
                    <p class="text-white/30 text-sm font-light">Real-time system alerts and notifications</p>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="resolveAllAlerts()" class="px-4 py-2 bg-green-500/20 border border-green-500/30 rounded-lg text-green-400 text-sm font-medium hover:bg-green-500/30 transition-all flex items-center gap-2">
                        <i class="fas fa-check-double"></i>
                        <span>Resolve All</span>
                    </button>
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== ALERT STATS ===== -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 mb-6">
                <div class="dashboard-card bg-white/3">
                    <div class="text-center">
                        <label class="text-white/30 text-xs block uppercase tracking-wider">Total Alerts</label>
                        <div class="text-3xl font-bold text-white mt-1">${fn:length(alerts)}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-red-500/5 border-red-500/20">
                    <div class="text-center">
                        <label class="text-red-400/60 text-xs block uppercase tracking-wider">Critical</label>
                        <div class="text-3xl font-bold text-red-400 mt-1">${criticalCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-orange-500/5 border-orange-500/20">
                    <div class="text-center">
                        <label class="text-orange-400/60 text-xs block uppercase tracking-wider">High</label>
                        <div class="text-3xl font-bold text-orange-400 mt-1">${highCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-yellow-500/5 border-yellow-500/20">
                    <div class="text-center">
                        <label class="text-yellow-400/60 text-xs block uppercase tracking-wider">Medium</label>
                        <div class="text-3xl font-bold text-yellow-400 mt-1">${mediumCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-blue-500/5 border-blue-500/20">
                    <div class="text-center">
                        <label class="text-blue-400/60 text-xs block uppercase tracking-wider">Resolved</label>
                        <div class="text-3xl font-bold text-blue-400 mt-1">${resolvedCount}</div>
                    </div>
                </div>
            </div>

            <!-- ===== FILTER SECTION ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <div class="form-group">
                        <label for="alertTypeFilter" class="text-white/40 text-xs font-medium block mb-1.5">Alert Type</label>
                        <div class="select-wrapper">
                            <select id="alertTypeFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Types</option>
                                <option value="SHIPMENT_DELAY" class="bg-black">Shipment Delay</option>
                                <option value="LOW_INVENTORY" class="bg-black">Low Inventory</option>
                                <option value="VENDOR_ISSUE" class="bg-black">Vendor Issue</option>
                                <option value="CUSTOMS_DEADLINE" class="bg-black">Customs Deadline</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="severityFilter" class="text-white/40 text-xs font-medium block mb-1.5">Severity</label>
                        <div class="select-wrapper">
                            <select id="severityFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Severities</option>
                                <option value="CRITICAL" class="bg-black">Critical</option>
                                <option value="HIGH" class="bg-black">High</option>
                                <option value="MEDIUM" class="bg-black">Medium</option>
                                <option value="LOW" class="bg-black">Low</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="statusFilter" class="text-white/40 text-xs font-medium block mb-1.5">Status</label>
                        <div class="select-wrapper">
                            <select id="statusFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Status</option>
                                <option value="false" class="bg-black">Active</option>
                                <option value="true" class="bg-black">Resolved</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group flex items-end">
                        <button id="applyFiltersBtn" class="w-full px-4 py-2.5 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all">
                            <i class="fas fa-filter mr-1.5"></i>Apply Filters
                        </button>
                    </div>
                </div>
            </div>

            <!-- ===== ALERTS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Type</th>
                            <th>Severity</th>
                            <th>Message</th>
                            <th>Entity</th>
                            <th>Created</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty alerts}">
                                <c:forEach var="a" items="${alerts}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.alertType == 'SHIPMENT_DELAY'}">
                                                        <span class="px-2 py-0.5 bg-red-500/20 text-red-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-truck mr-1"></i>Delay
                                                        </span>
                                                </c:when>
                                                <c:when test="${a.alertType == 'LOW_INVENTORY'}">
                                                        <span class="px-2 py-0.5 bg-yellow-500/20 text-yellow-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-box mr-1"></i>Low Stock
                                                        </span>
                                                </c:when>
                                                <c:when test="${a.alertType == 'VENDOR_ISSUE'}">
                                                        <span class="px-2 py-0.5 bg-orange-500/20 text-orange-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-building mr-1"></i>Vendor
                                                        </span>
                                                </c:when>
                                                <c:when test="${a.alertType == 'CUSTOMS_DEADLINE'}">
                                                        <span class="px-2 py-0.5 bg-blue-500/20 text-blue-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-passport mr-1"></i>Customs
                                                        </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="px-2 py-0.5 bg-white/10 text-white/60 rounded-full text-[10px]">${a.alertType}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.severity == 'CRITICAL'}">
                                                    <span class="px-2 py-0.5 bg-red-500/30 text-red-400 rounded-full text-[10px] font-bold">Critical</span>
                                                </c:when>
                                                <c:when test="${a.severity == 'HIGH'}">
                                                    <span class="px-2 py-0.5 bg-orange-500/30 text-orange-400 rounded-full text-[10px] font-medium">High</span>
                                                </c:when>
                                                <c:when test="${a.severity == 'MEDIUM'}">
                                                    <span class="px-2 py-0.5 bg-yellow-500/30 text-yellow-400 rounded-full text-[10px]">Medium</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="px-2 py-0.5 bg-blue-500/30 text-blue-400 rounded-full text-[10px]">Low</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/50 text-xs max-w-[200px] truncate" title="${a.message}">
                                                ${a.message}
                                        </td>
                                        <td class="text-white/40 text-xs">
                                            <c:if test="${a.entityType != null}">
                                                ${a.entityType} #${a.entityId}
                                            </c:if>
                                            <c:if test="${a.entityType == null}">
                                                <span class="text-white/20">—</span>
                                            </c:if>
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${a.createdAt != null ? a.createdAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.resolved}">
                                                    <span class="transaction-status completed">Resolved</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="transaction-status pending">Active</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="flex items-center gap-2">
                                                <c:if test="${!a.resolved}">
                                                    <button class="text-green-400/60 hover:text-green-400 text-xs transition-all" title="Resolve" onclick="resolveAlert(${a.alertId})">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                </c:if>
                                                <button class="text-white/20 hover:text-red-400 text-xs transition-all" title="Delete" onclick="deleteAlert(${a.alertId})">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-bell-slash text-3xl text-white/10"></i>
                                            <span class="text-sm">No alerts found</span>
                                            <span class="text-xs text-white/10">All systems are operating normally</span>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- ===== TABLE FOOTER ===== -->
                <div class="flex flex-wrap items-center justify-between gap-3 mt-4 pt-4 border-t border-white/5">
                    <div class="text-white/20 text-xs">Showing ${fn:length(alerts)} alerts</div>
                    <div class="flex items-center gap-1">
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Previous</button>
                        <button class="px-3 py-1 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-xs hover:bg-dracula-red/30 transition-all">1</button>
                        <button class="px-3 py-1 bg-white/5 border border-white/10 rounded-lg text-white/30 text-xs hover:bg-white/10 transition-all" disabled>Next</button>
                    </div>
                </div>
            </div>

        </div>
    </main>

    <!-- ===== FOOTER ===== -->
    <jsp:include page="/WEB-INF/views/footer.jsp" />
</div>

<!-- Custom JavaScript -->
<script src="${pageContext.request.contextPath}/js/monitoring.js"></script>
</body>
</html>