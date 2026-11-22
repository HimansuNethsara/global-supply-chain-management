<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 8/27/2026
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
    <title>GlobalTrade Logistics - Audit Logs</title>

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
                    <h1 class="text-xl sm:text-2xl font-bold text-white">Audit Logs</h1>
                    <p class="text-white/30 text-sm font-light">System activity and audit trail</p>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="refreshLogs()" class="px-4 py-2 bg-dracula-red/20 border border-dracula-red/30 rounded-lg text-dracula-red text-sm font-medium hover:bg-dracula-red/30 transition-all flex items-center gap-2">
                        <i class="fas fa-sync-alt"></i>
                        <span>Refresh</span>
                    </button>
                    <button onclick="window.print()" class="px-4 py-2 bg-white/5 border border-white/5 rounded-lg text-white/50 hover:text-white hover:bg-white/10 transition-all text-xs flex items-center gap-2">
                        <i class="fas fa-file-export"></i>
                        <span>Export</span>
                    </button>
                </div>
            </div>

            <!-- ===== STATS ===== -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                <div class="dashboard-card bg-white/3">
                    <div class="text-center">
                        <label class="text-white/30 text-xs block uppercase tracking-wider">Total Logs</label>
                        <div class="text-3xl font-bold text-white mt-1">${fn:length(auditLogs)}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-blue-500/5 border-blue-500/20">
                    <div class="text-center">
                        <label class="text-blue-400/60 text-xs block uppercase tracking-wider">Today</label>
                        <div class="text-3xl font-bold text-blue-400 mt-1">${todayCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-green-500/5 border-green-500/20">
                    <div class="text-center">
                        <label class="text-green-400/60 text-xs block uppercase tracking-wider">User Actions</label>
                        <div class="text-3xl font-bold text-green-400 mt-1">${userActionCount}</div>
                    </div>
                </div>
                <div class="dashboard-card bg-yellow-500/5 border-yellow-500/20">
                    <div class="text-center">
                        <label class="text-yellow-400/60 text-xs block uppercase tracking-wider">System Actions</label>
                        <div class="text-3xl font-bold text-yellow-400 mt-1">${systemActionCount}</div>
                    </div>
                </div>
            </div>

            <!-- ===== FILTER SECTION ===== -->
            <div class="dashboard-card mb-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <div class="form-group">
                        <label for="searchInput" class="text-white/40 text-xs font-medium block mb-1.5">Search</label>
                        <input type="text" id="searchInput" placeholder="Search actions or details..."
                               class="form-control w-full px-3 py-2.5 bg-white/5 border border-white/10 rounded-lg text-white text-sm placeholder:text-white/20 focus:border-dracula-red focus:outline-none transition-all">
                    </div>
                    <div class="form-group">
                        <label for="actionFilter" class="text-white/40 text-xs font-medium block mb-1.5">Action</label>
                        <div class="select-wrapper">
                            <select id="actionFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Actions</option>
                                <option value="LOGIN" class="bg-black">Login</option>
                                <option value="LOGOUT" class="bg-black">Logout</option>
                                <option value="CREATE" class="bg-black">Create</option>
                                <option value="UPDATE" class="bg-black">Update</option>
                                <option value="DELETE" class="bg-black">Delete</option>
                                <option value="VIEW" class="bg-black">View</option>
                                <option value="TIMER" class="bg-black">Timer</option>
                            </select>
                            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-white/20 pointer-events-none">
                                <i class="fas fa-chevron-down text-xs"></i>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="entityFilter" class="text-white/40 text-xs font-medium block mb-1.5">Entity Type</label>
                        <div class="select-wrapper">
                            <select id="entityFilter" class="form-select w-full px-3 py-2.5 pr-10 bg-white/5 border border-white/10 rounded-lg text-white text-sm focus:border-dracula-red focus:outline-none transition-all appearance-none cursor-pointer">
                                <option value="" class="bg-black text-white/40">All Entities</option>
                                <option value="USER" class="bg-black">User</option>
                                <option value="VENDOR" class="bg-black">Vendor</option>
                                <option value="SHIPMENT" class="bg-black">Shipment</option>
                                <option value="INVENTORY" class="bg-black">Inventory</option>
                                <option value="CUSTOMER" class="bg-black">Customer</option>
                                <option value="ORDER" class="bg-black">Order</option>
                                <option value="CUSTOMS" class="bg-black">Customs</option>
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

            <!-- ===== AUDIT LOGS TABLE ===== -->
            <div class="dashboard-card overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="transaction-table">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>User</th>
                            <th>Action</th>
                            <th>Entity Type</th>
                            <th>Entity ID</th>
                            <th>Details</th>
                            <th>Logged At</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty auditLogs}">
                                <c:forEach var="log" items="${auditLogs}" varStatus="status">
                                    <tr>
                                        <td class="text-white/40 text-xs">${status.index + 1}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.user != null}">
                                                    <span class="text-white/60 text-xs font-medium">${log.user.fullName}</span>
                                                    <div class="text-white/30 text-[10px]">@${log.user.username}</div>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-white/30 text-xs">SYSTEM</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.action == 'LOGIN'}">
                                                        <span class="px-2 py-0.5 bg-green-500/20 text-green-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-sign-in-alt mr-1"></i>Login
                                                        </span>
                                                </c:when>
                                                <c:when test="${log.action == 'LOGOUT'}">
                                                        <span class="px-2 py-0.5 bg-red-500/20 text-red-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-sign-out-alt mr-1"></i>Logout
                                                        </span>
                                                </c:when>
                                                <c:when test="${log.action == 'CREATE'}">
                                                        <span class="px-2 py-0.5 bg-blue-500/20 text-blue-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-plus mr-1"></i>Create
                                                        </span>
                                                </c:when>
                                                <c:when test="${log.action == 'UPDATE'}">
                                                        <span class="px-2 py-0.5 bg-yellow-500/20 text-yellow-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-edit mr-1"></i>Update
                                                        </span>
                                                </c:when>
                                                <c:when test="${log.action == 'DELETE'}">
                                                        <span class="px-2 py-0.5 bg-red-500/20 text-red-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-trash mr-1"></i>Delete
                                                        </span>
                                                </c:when>
                                                <c:when test="${log.action == 'TIMER'}">
                                                        <span class="px-2 py-0.5 bg-purple-500/20 text-purple-400 rounded-full text-[10px] font-medium">
                                                            <i class="fas fa-clock mr-1"></i>Timer
                                                        </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="px-2 py-0.5 bg-white/10 text-white/60 rounded-full text-[10px]">${log.action}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.entityType == 'USER'}">
                                                    <span class="text-blue-400/60 text-xs"><i class="fas fa-user mr-1"></i>User</span>
                                                </c:when>
                                                <c:when test="${log.entityType == 'VENDOR'}">
                                                    <span class="text-purple-400/60 text-xs"><i class="fas fa-building mr-1"></i>Vendor</span>
                                                </c:when>
                                                <c:when test="${log.entityType == 'SHIPMENT'}">
                                                    <span class="text-orange-400/60 text-xs"><i class="fas fa-truck mr-1"></i>Shipment</span>
                                                </c:when>
                                                <c:when test="${log.entityType == 'INVENTORY'}">
                                                    <span class="text-yellow-400/60 text-xs"><i class="fas fa-box mr-1"></i>Inventory</span>
                                                </c:when>
                                                <c:when test="${log.entityType == 'CUSTOMER'}">
                                                    <span class="text-green-400/60 text-xs"><i class="fas fa-user mr-1"></i>Customer</span>
                                                </c:when>
                                                <c:when test="${log.entityType == 'ORDER'}">
                                                    <span class="text-cyan-400/60 text-xs"><i class="fas fa-shopping-cart mr-1"></i>Order</span>
                                                </c:when>
                                                <c:when test="${log.entityType == 'CUSTOMS'}">
                                                    <span class="text-indigo-400/60 text-xs"><i class="fas fa-passport mr-1"></i>Customs</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-white/40 text-xs">${log.entityType}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-white/40 text-xs">#${log.entityId}</td>
                                        <td class="text-white/40 text-xs max-w-[150px] truncate" title="${log.details}">
                                                ${log.details != null ? log.details : '—'}
                                        </td>
                                        <td class="text-white/30 text-xs">
                                                ${log.loggedAt != null ? log.loggedAt.toString().replace('T', ' ').substring(0, 16) : 'N/A'}
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center text-white/20 py-8">
                                        <div class="flex flex-col items-center gap-2">
                                            <i class="fas fa-clipboard-list text-3xl text-white/10"></i>
                                            <span class="text-sm">No audit logs found</span>
                                            <span class="text-xs text-white/10">System activity will appear here</span>
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
                    <div class="text-white/20 text-xs">Showing ${fn:length(auditLogs)} logs</div>
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